package hotmath.gwt.shared.server.service.command;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionCustomMobile;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.testset.ha.info.CmLessonDao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;



/**
 * Read an existing prescription based on a test run
 * 
 * and return data that represents a single program prescription lesson.
 * 
 * @author casey
 * 
 */
public class GetTopicPrescriptionCommand implements ActionHandler<GetTopicPrescriptionAction, PrescriptionSessionResponse> {

    static Logger __logger = Logger.getLogger(GetPrescriptionCommand.class);
    
    @Override
    public PrescriptionSessionResponse execute(final Connection conn, GetTopicPrescriptionAction action) throws Exception {

        int userIdToUse=0;
        PrescriptionSessionResponse res=null;
        if(action.getUid() == 0) {
            res = (PrescriptionSessionResponse)CmCacheManager.getInstance().retrieveFromCache(CacheName.TOPIC_SEARCH_PRESCRIPTION, action.getTopicFile());
            if(res != null) {
                return res;
            }
            
            userIdToUse = CatchupMathProperties.getInstance().getProblemAsInt("topic_search_shared_user",737521 ); // 24412);
        }
        else {
            userIdToUse = action.getUid();
        }
        
        String lesson = action.getTopicFile();
        if(!lesson.endsWith(".html")) {
            lesson = CmLessonDao.getInstance().lookupLessonFileFromName(conn, lesson);
        }

        HaTest custTest = HaTestDao.getInstance().createTest(userIdToUse,HaTestDefDao.getInstance().getTestDef(CmProgram.CUSTOM_PROGRAM.getDefId()), HaTestDao.EMPTY_TEST);
        StudentUserProgramModel userProgram = new StudentUserProgramModel();
        custTest.setProgramInfo(userProgram);
        
        int numQuestions =  custTest.getNumTestQuestions();
        HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn, userIdToUse, custTest.getTestId(), numQuestions, 0, 0);
        testRun.setHaTest(custTest);
        
        List<CustomLessonModel> lessonModels = new ArrayList<CustomLessonModel>();
        lessonModels.add(new CustomLessonModel(CmLessonDao.getInstance().getTopicLessonTitle(lesson), lesson, "General"));
        
        AssessmentPrescription prescription = new AssessmentPrescriptionCustomMobile(conn, testRun, lessonModels);
        res = GetPrescriptionCommand.createPrescriptionResponse(conn, prescription, 0);
        
        CmCacheManager.getInstance().addToCache(CacheName.TOPIC_SEARCH_PRESCRIPTION, action.getTopicFile(), res);      
        return res;
    }




    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        // TODO Auto-generated method stub
        return GetTopicPrescriptionAction.class;
    }
    
    static public void main(String as[]) {
        
        try {
            System.out.println(ActionDispatcher.getInstance().execute(new GetTopicPrescriptionAction("topics/number-line.html")));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
    }
    
}