package hotmath.gwt.shared.server.service.command.cm2;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.dao.HaLoginInfoDao;
import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_core.client.model.Cm2PrescriptionTopic;
import hotmath.gwt.cm_core.client.model.PrescriptionResource;
import hotmath.gwt.cm_core.client.model.ResourceItem;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetReviewHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.cm2.Cm2MobileUser;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2MobileLoginAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.GetCm2QuizHtmlAction;
import hotmath.gwt.cm_rpc.client.rpc.cm2.QuizCm2HtmlResult;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.data.HaBasicUser;
import hotmath.gwt.cm_tools.client.data.HaBasicUser.UserType;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.GetReviewHtmlCommand;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand.CustomProgramInfo;
import hotmath.testset.ha.HaUserFactory;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class GetCm2MobileLoginCommand implements ActionHandler<GetCm2MobileLoginAction, Cm2MobileUser> {

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetCm2MobileLoginAction.class;
    }

    @Override
    public Cm2MobileUser execute(Connection conn, GetCm2MobileLoginAction action) throws Exception {

       int uid = action.getUid();
       
       HaBasicUser basicUser=null;
       if(uid > 0) {
           basicUser = HaUserFactory.getLoginUserInfo(conn, uid, "STUDENT");
       }
       else if(action.getName().equals("catchup_demo") && action.getPassword().equals("demo")) {
           basicUser = HaUserFactory.createDemoUser(conn, "ess");
        }
       else {
           basicUser = HaUserFactory.loginToCatchup(conn, action.getName(), action.getPassword());
       }
       
       if (basicUser.getUserType() != UserType.STUDENT)
            throw new CmException("Invalid user type: " + basicUser.getUserType());
        
        

        CmProgramFlow programFlow = new CmProgramFlow(conn, basicUser.getUserKey());
        
        StudentModelI sm = CmStudentDao.getInstance().getStudentModelBase(conn, basicUser.getUserKey());
        StudentActiveInfo active = programFlow.getActiveInfo();
        
        boolean didPassTest = false;
        
        AssignmentUserInfo assignmentInfo = AssignmentDao.getInstance().getStudentAssignmentMetaInfo(sm.getUid());
        Cm2MobileUser mobileUser = new Cm2MobileUser(sm.getUid(), active.getActiveTestId(), active.getActiveSegment(), active.getActiveSegmentSlot(), didPassTest, active.getActiveRunId(), assignmentInfo);

        
        /** create new security key for this login session */
        String securityKey="";
        if(uid == 0) {
            securityKey = HaLoginInfoDao.getInstance().addLoginInfo(conn, basicUser, new ClientEnvironment(false),true);
        }
        
        int programSegmentCount = 0;
        String testTitle = programFlow.getUserProgram().getTestName();
        boolean isCustomProgram = programFlow.getUserProgram().getTestDefId() == CmProgram.CUSTOM_PROGRAM.getDefId();
        if(isCustomProgram) {
            CustomProgramInfo cpi = GetUserInfoCommand.processCustomProgram(conn, sm.getUid(), active, programFlow.getUserProgram());
            programSegmentCount = cpi.getProgramSegmentCount();
            testTitle = cpi.getTitle();
            
            //mobileUser.getBaseLoginResponse().getUserInfo().setProgramName(testTitle);
            
            
            // TODO:
            // here the CP is now ready for use, but what about the 
            // programSegmentCount and testTitle ...?
        }
        
        
        /** look up the Custom Quiz name */
        boolean isCustomQuiz = programFlow.getUserProgram().getCustomQuizId() > 0;
        if(isCustomQuiz) {
            testTitle = programFlow.getUserProgram().getCustomQuizName();
        }

        /**
         * get list of previous prescribed lessons
         * 
         */
        PreparedStatement ps = null;
        try {
            GetUserInfoAction loginAction = new GetUserInfoAction(mobileUser.getUserId(), null);
            UserLoginResponse userLoginResponse = new GetUserInfoCommand().execute(conn, loginAction);
            mobileUser.setBaseLoginResponse(userLoginResponse);
            mobileUser.setSecurityKey(securityKey);
            
            mobileUser.getBaseLoginResponse().getUserInfo().setProgramName(testTitle);
            
            CmProgramFlowAction nextAction = programFlow.getActiveFlowAction(conn);
            
            mobileUser.setPrescriptionTopics(extractPrescriptionTopics(conn, nextAction));
            
            //mobileUser.setFlowAction(nextAction);
            
            if(mobileUser.getTestId() > 0) {
                // is quiz
                GetCm2QuizHtmlAction actionQuiz = new GetCm2QuizHtmlAction(active.getActiveTestId());
                QuizCm2HtmlResult quizResponse = new GetCm2QuizHtmlCommand().execute(conn, actionQuiz);
                mobileUser.setCm2QuizResponse(quizResponse);
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
        return mobileUser;
    }

    private List<Cm2PrescriptionTopic> extractPrescriptionTopics(Connection conn, CmProgramFlowAction nextAction) throws Exception {
        try {
            List<Cm2PrescriptionTopic> topics = new ArrayList<Cm2PrescriptionTopic>();
            if(nextAction.getPlace() == CmPlace.QUIZ) {
            }
            else if(nextAction.getPlace() == CmPlace.PRESCRIPTION) {
                
                PrescriptionSessionResponse pr = nextAction.getPrescriptionResponse();
                List<SessionTopic> st = pr.getPrescriptionData().getSessionTopics();
                
                for(int i=0;i<st.size();i++) {
                    
                    // for each topic
                    //
                    GetPrescriptionAction pa = new GetPrescriptionAction(pr.getRunId(), i, false);
                    PrescriptionSessionResponse data = new GetPrescriptionCommand().execute(conn, pa);
                    
                    PrescriptionSessionData currSess = data.getPrescriptionData().getCurrSession();
                    String topicHtml = new GetReviewHtmlCommand().execute(conn,new GetReviewHtmlAction(currSess.getFile())).getLesson();
                    
                    
                    Cm2PrescriptionTopic topic = new Cm2PrescriptionTopic(currSess.getTopic(), topicHtml, getResources(data.getPrescriptionData()));
                    topics.add(topic);
                }
            }
            return topics;
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private List<PrescriptionResource> getResources(PrescriptionData prescriptionData) {
        List<PrescriptionResource> resources = new ArrayList<PrescriptionResource>();
        
        for(PrescriptionSessionDataResource r: prescriptionData.getCurrSession().getInmhResources()) {
            PrescriptionResource pr = new PrescriptionResource(r.getType().name()); 
            // only types included in Cm2Mobile
            String t = pr.getType();
            
            if(t.equals(CmResourceType.REVIEW.name()) || t.equals(CmResourceType.PRACTICE.name()) || t.equals(CmResourceType.VIDEO.name())) {
                for(InmhItemData item: r.getItems()) {
                    pr.getItems().add(new ResourceItem(item.getType().label(), item.getFile(), item.getTitle()));
                }
                resources.add(pr);
            }
        }
        return resources;
    }


}
