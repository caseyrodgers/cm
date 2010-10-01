package hotmath.gwt.cm_mobile_shared.server.rpc;

import hotmath.HotMathException;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionCustomMobile;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileLessonInfoAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.MobileLessonInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.shared.client.rpc.action.GetViewedInmhItemsAction;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.gwt.shared.server.service.command.GetViewedInmhItemsCommand;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpItemFactory;
import hotmath.inmh.INeedMoreHelpResourceType;
import hotmath.inmh.INeedMoreHelpResourceTypeDef;
import hotmath.testset.ha.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.StudentUserProgramModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class GetMobileLessonInfoCommand implements ActionHandler<GetMobileLessonInfoAction, MobileLessonInfo> {

    @Override
    public MobileLessonInfo execute(Connection conn, GetMobileLessonInfoAction action) throws Exception {

        String lessonTitle = getLessonTitle(conn, action.getFile());

        int userId = 23487;

        HaTest custTest = HaTestDao.createTest(conn, userId,
                new HaTestDefDao().getTestDef(conn, CmProgram.CUSTOM_PROGRAM.getDefId()), -1);
        StudentUserProgramModel userProgram = new StudentUserProgramModel();
        custTest.setProgramInfo(userProgram);
        HaTestRun testRun = HaTestDao.createTestRun(conn, userId, custTest.getTestId(), 10, 0, 0);
        testRun.setHaTest(custTest);

        List<CustomLessonModel> lessonModels = new ArrayList<CustomLessonModel>();
        lessonModels.add(new CustomLessonModel(lessonTitle, action.getFile(), "General"));
        AssessmentPrescription pres1 = new AssessmentPrescriptionCustomMobile(conn, testRun, lessonModels);
        AssessmentPrescriptionSession sess = pres1.getSessions().get(0);

        PrescriptionData presData = new PrescriptionData();
        List<AssessmentPrescription.SessionData> practiceProblems = sess.getSessionDataFor(sess.getTopic());

        List<INeedMoreHelpResourceType> items = getPrescriptionInmhTypes(conn, action.getFile(), lessonTitle, null);
        PrescriptionSessionDataResource problemsResource = new PrescriptionSessionDataResource();
        problemsResource.setType("practice");

        /** label either as Problems or Activities */
        boolean isActivity = practiceProblems.get(0).getWidgetArgs() != null;
        String title = "Required Practice " + (isActivity ? "Activities" : "Problems");
        problemsResource.setLabel(title);
        int cnt = 1;
        for (AssessmentPrescription.SessionData sdata : practiceProblems) {
            InmhItemData id = new InmhItemData();
            String type = isActivity ? "Activity " : "Problem ";
            id.setTitle(type + cnt++);
            id.setFile(sdata.getPid());
            id.setType("practice");
            id.setWidgetJsonArgs(sdata.getWidgetArgs());

            problemsResource.getItems().add(id);
        }

        PrescriptionSessionDataResource lessonResource = new PrescriptionSessionDataResource();
        lessonResource.setType("review");
        lessonResource.setLabel("Review Lesson");
        InmhItemData lessonId = new InmhItemData();

        // Get the lesson this session is based on
        // each session is a single topic
        INeedMoreHelpItem item = sess.getSessionCategories().get(0);
        lessonId.setTitle(item.getTitle());
        lessonId.setFile(item.getFile());
        lessonId.setType(item.getType());
        lessonResource.getItems().add(lessonId);

        // always send complete list of all topics
        // @TODO: should we have an initialize phase and return this info
        // This would impose two request/response
        PrescriptionSessionData sessionData = new PrescriptionSessionData();
        sessionData.setSessionRpa(isActivity);
        for (AssessmentPrescriptionSession s : pres1.getSessions()) {
            presData.getSessionTopics().add(s.getTopic());
        }
        presData.setCurrSession(sessionData);

        sessionData.setTopic(sess.getTopic());
        sessionData.setSessionNumber(0);
        for (INeedMoreHelpResourceType t : sess.getPrescriptionInmhTypesDistinct(conn)) {

            // skip the workbooks for now.
            if (t.getTypeDef().getType().equals("workbook"))
                continue;

            PrescriptionSessionDataResource resource = new PrescriptionSessionDataResource();
            String type = t.getTypeDef().getType();
            resource.setType(type);
            resource.setLabel(t.getTypeDef().getLabel());
            for (INeedMoreHelpItem i : t.getResources()) {
                InmhItemData id = new InmhItemData();
                id.setFile(i.getFile());
                id.setTitle(i.getTitle());
                id.setType(i.getType());

                resource.getItems().add(id);
            }
            sessionData.getInmhResources().add(resource);
        }

        sessionData.getInmhResources().add(lessonResource);
        sessionData.getInmhResources().add(problemsResource);

        List<PrescriptionSessionDataResource> resources = GetPrescriptionCommand.fixupInmhResources(sessionData.getInmhResources());
        sessionData.setInmhResources(resources);

        MobileLessonInfo lessonInfo = new MobileLessonInfo();
        lessonInfo.setPresData(presData);

        return lessonInfo;
    }

    // public MobileLessonInfo execute2(Connection conn,
    // GetMobileLessonInfoAction action) throws Exception {
    // try {
    //
    // AssessmentPrescriptionSession sess =
    // pres.getSessions().get(sessionNumber);
    //
    // PrescriptionData presData = new PrescriptionData();
    //
    // List<AssessmentPrescription.SessionData> practiceProblems =
    // sess.getSessionDataFor(sess.getTopic());
    // PrescriptionSessionDataResource problemsResource = new
    // PrescriptionSessionDataResource();
    // problemsResource.setType("practice");
    //
    // __logger.debug("assigning problems to prescription: " + action);
    //
    // /** label either as Problems or Activities */
    // boolean isActivity=practiceProblems.get(0).getWidgetArgs()!=null;
    // String title = "Required Practice " +
    // (isActivity?"Activities":"Problems");
    // problemsResource.setLabel(title);
    // int cnt = 1;
    // for (AssessmentPrescription.SessionData sdata : practiceProblems) {
    // InmhItemData id = new InmhItemData();
    // String type = isActivity?"Activity ":"Problem ";
    // id.setTitle(type + cnt++);
    // id.setFile(sdata.getPid());
    // id.setType("practice");
    // id.setWidgetJsonArgs(sdata.getWidgetArgs());
    //
    // problemsResource.getItems().add(id);
    // }
    //
    // PrescriptionSessionDataResource lessonResource = new
    // PrescriptionSessionDataResource();
    // lessonResource.setType("review");
    // lessonResource.setLabel("Review Lesson");
    // InmhItemData lessonId = new InmhItemData();
    //
    // // Get the lesson this session is based on
    // // each session is a single topic
    // INeedMoreHelpItem item = sess.getSessionCategories().get(0);
    // lessonId.setTitle(item.getTitle());
    // lessonId.setFile(item.getFile());
    // lessonId.setType(item.getType());
    // lessonResource.getItems().add(lessonId);
    //
    // // always send complete list of all topics
    // // @TODO: should we have an initialize phase and return this info
    // // This would impose two request/response
    // PrescriptionSessionData sessionData = new PrescriptionSessionData();
    // sessionData.setSessionRpa(isActivity);
    // for (AssessmentPrescriptionSession s : pres.getSessions()) {
    // presData.getSessionTopics().add(s.getTopic());
    // }
    // presData.setCurrSession(sessionData);
    //
    //
    // __logger.debug("Getting prescription resource items: " + action);
    // sessionData.setTopic(sess.getTopic());
    // sessionData.setSessionNumber(sessionNumber);
    // for (INeedMoreHelpResourceType t :
    // sess.getPrescriptionInmhTypesDistinct(conn)) {
    //
    // // skip the workbooks for now.
    // if (t.getTypeDef().getType().equals("workbook"))
    // continue;
    //
    // PrescriptionSessionDataResource resource = new
    // PrescriptionSessionDataResource();
    // resource.setType(t.getTypeDef().getType());
    // resource.setLabel(t.getTypeDef().getLabel());
    // for (INeedMoreHelpItem i : t.getResources()) {
    // InmhItemData id = new InmhItemData();
    // id.setFile(i.getFile());
    // id.setTitle(i.getTitle());
    // id.setType(i.getType());
    //
    // resource.getItems().add(id);
    // }
    // sessionData.getInmhResources().add(resource);
    // }
    //
    // // add a results resource type to allow user to view current results
    // PrescriptionSessionDataResource resultsResource = new
    // PrescriptionSessionDataResource();
    // resultsResource.setType("results");
    // resultsResource.setLabel("Quiz Results");
    // InmhItemData id = new InmhItemData();
    // id.setTitle("Your quiz results");
    // id.setFile("");
    // id.setType("results");
    // resultsResource.getItems().add(id);
    //
    //
    //
    // __logger.debug("creating prescription sessions: " + action);
    //
    // sessionData.getInmhResources().add(lessonResource);
    // sessionData.getInmhResources().add(problemsResource);
    // sessionData.getInmhResources().add(resultsResource);
    //
    // /** Call action and request list of INMH items */
    // GetViewedInmhItemsAction getViewedAction = new
    // GetViewedInmhItemsAction(runId);
    // List<RpcData> rdata = new GetViewedInmhItemsCommand().execute(conn,
    // getViewedAction).getRpcData();
    //
    // List<PrescriptionSessionDataResource> resources =
    // fixupInmhResources(sessionData.getInmhResources());
    // for (PrescriptionSessionDataResource r : resources) {
    // for (InmhItemData itemData : r.getItems()) {
    // // is this item viewed?
    // for (RpcData rd : rdata) {
    // if (rd.getDataAsString("file").equals(itemData.getFile())) {
    // itemData.setViewed(true);
    // break;
    // }
    // }
    // }
    // }
    // sessionData.setInmhResources(resources);
    //
    // // update this user's active run session
    // if (action.getUpdateActionInfo()) {
    // pres.getTest().getUser().setActiveTestRunSession(sessionNumber);
    // pres.getTest().getUser().update(conn);
    // }
    //
    //
    //
    // /** Mark this lesson as being viewed
    // *
    // */
    // new HaTestRunDao().setLessonViewed(conn,runId,sessionNumber);
    //
    // HaTestRun testRun = pres.getTestRun();
    //
    // PrescriptionSessionResponse response = new PrescriptionSessionResponse();
    // response.setRunId(testRun.getRunId());
    // response.setPrescriptionData(presData);
    // response.setCorrectPercent(getTestPassPercent(testRun.getAnsweredCorrect()
    // + testRun.getAnsweredIncorrect() + testRun.getNotAnswered(),
    // pres.getTestRun().getAnsweredCorrect()));
    // response.setProgramTitle(pres.getTest().getTestDef().getTitle());
    //
    // return response;
    //
    // } catch (Exception e) {
    // throw new CmRpcException(e);
    // }

    private String getLessonTitle(final Connection conn, String file) throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = "select lesson from HA_PROGRAM_LESSONS_static where file = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, file);
            ResultSet rs = ps.executeQuery();
            if (!rs.first())
                throw new Exception("No title found for lesson '" + file + "'");
            return rs.getString("lesson");
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    public List<INeedMoreHelpResourceType> getPrescriptionInmhTypes(final Connection conn, String file, String topic,
            String linkTypeIn) throws HotMathException {

        if (linkTypeIn == null)
            linkTypeIn = "";

        List<INeedMoreHelpResourceType> resourceTypes = new ArrayList<INeedMoreHelpResourceType>();

        String topicListSb = "'" + file + "'";
        PreparedStatement pstat = null;
        try {
            String sql = "select distinct m.file, l.link_title, l.link_key, l.link_type "
                    + " from   inmh_assessment m, inmh_link l " + " where  m.file = l.file " + " and  m.file in ("
                    + topicListSb.toString() + ") " + " and l.link_type like '%" + linkTypeIn + "'"
                    + " order by l.link_type, link_title ";

            pstat = conn.prepareStatement(sql);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {

                int type = 0;
                String linkType = rs.getString("link_type");
                if (linkType.equals("video"))
                    type = INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_videos;
                else if (linkType.equals("activity"))
                    type = INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_activity;
                else if (linkType.equals("workbook"))
                    type = INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_workbook;
                else if (linkType.equals("cmextra"))
                    type = INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_cmextra;
                else if (linkType.equals("flashcard"))
                    type = INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_flashcard;

                INeedMoreHelpResourceType inmhType = new INeedMoreHelpResourceType(
                        INeedMoreHelpResourceTypeDef.RESOURCE_TYPES.get(type));
                inmhType.setTopic(topic);

                INeedMoreHelpItem inmhItem = INeedMoreHelpItemFactory.create(rs.getString("link_type"),
                        rs.getString("link_key"), rs.getString("link_title"));

                inmhType.addResource(inmhItem);
                resourceTypes.add(inmhType);
            }
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error reading prescription inmh items: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }

        return resourceTypes;
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetMobileLessonInfoAction.class;
    }
}
