package hotmath.assessment;

import hotmath.HotMathProperties;
import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.program.CmProgramFlow;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetUserInfoAction;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel.Type;
import hotmath.gwt.shared.server.service.command.GetUserInfoCommand;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class AssessmentPrescriptionCustom extends AssessmentPrescription {

    final static Logger __logger = Logger.getLogger(AssessmentPrescriptionCustom.class);

    int _numSegments = 0;
    List<AssessmentPrescriptionSession> _sessionsAll = new ArrayList<AssessmentPrescriptionSession>();
    int gradeLevel;

    public AssessmentPrescriptionCustom(final Connection conn, HaTestRun testRun) throws Exception {
        super(conn);

        __logger.info("Creating custom program: " + testRun);

        this.testRun = testRun;

        int custProgId = CmUserProgramDao.getInstance().loadProgramInfoForTest(testRun.getHaTest().getTestId())
                .getCustomProgramId();
        if (custProgId == 0) {
            throw new Exception("custom program ID must be specified");
        }

        readAssessment();

        ClientEnvironment clientEnvironment = HaUserDao.getInstance().getLatestClientEnvironment(
                testRun.getHaTest().getUser().getUid());

        // now choose pids from the pool for this item
        int uid = testRun.getHaTest().getUser().getUid();

        int segment = testRun.getHaTest().getSegment();
        CmList<CustomLessonModel> progLessons = CmCustomProgramDao.getInstance().getCustomProgramLessons(conn,
                custProgId, segment);
        int sessNum = 0;

        String cacheKey = uid + "/" + testRun.getRunId();

        gradeLevel = testRun.getHaTest().getGradeLevel();
        if (gradeLevel == 0) {
            gradeLevel = determineGradeLevel(progLessons, cacheKey);
            __logger.info("determineGradeLevel == " + gradeLevel);
            if (gradeLevel < MAX_GRADE_LEVEL) {
                HaTestDao.getInstance().updateGradeLevel(testRun.getHaTest().getTestId(), gradeLevel);
            }
        }

        __logger.debug("grade level: " + gradeLevel);
        for (CustomLessonModel cpProgItem : progLessons) {
            AssessmentPrescriptionSession session = null;
            if (cpProgItem.getCustomProgramType() == Type.LESSON) {
                /**
                 * is a lesson custom program item
                 * 
                 */
                INeedMoreHelpItem item = new INeedMoreHelpItem("review", cpProgItem.getFile(), cpProgItem.getLesson());
                InmhItemData itemData = new InmhItemData(item);

                List<RppWidget> workBookPids = itemData.getWidgetPool(conn, cacheKey);
                if (workBookPids.size() == 0) {
                    logger.warn("No pool solutions found for + '" + itemData.getInmhItem().toString() + "'");
                    continue; // nothing to see here.
                }

                try {
                    session = createSession(sessNum, workBookPids, itemData, true, clientEnvironment);
                } catch (SbExceptionNoLessonRppsFound noRpps) {
                    __logger.error("No RPPS could be found", noRpps);
                }

                // assert that there is at least one
                if (session == null || session.getSessionItems().size() == 0) {
                    // this session has no items, so it is invalid and will be
                    // skipped
                    __logger.error("AssessmentPrescriptionSession: session has no items: " + itemData);
                } else {
                    // add this session, and move to next
                    _sessions.add(session);
                    sessNum++;
                }
            } else {
                /**
                 * is a quiz custom program item
                 * 
                 */
                _numSegments++;
            }

        }
        HaTestRunDao.getInstance().addLessonsToTestRun(conn, testRun, _sessions);
    }

    @Override
    public int getGradeLevel() {
        return gradeLevel;
    }

    private int determineGradeLevel(CmList<CustomLessonModel> progLessons, String cacheKey) throws Exception {
        int heighestLowestGradeLevel = 0;
        for (CustomLessonModel cpProgItem : progLessons) {
            if (cpProgItem.getCustomProgramType() == Type.LESSON) {
                INeedMoreHelpItem item = new INeedMoreHelpItem("review", cpProgItem.getFile(), cpProgItem.getLesson());
                InmhItemData itemData = new InmhItemData(item);
                List<RppWidget> workBookPids = itemData.getWidgetPool(conn, cacheKey);
                if(workBookPids.size() == 0) {
                    __logger.info("No widgets from pool: " + cpProgItem);
                }
                else {
                    int lowest = getLowestGradeLevel(workBookPids);
                    if (lowest > heighestLowestGradeLevel && lowest < MAX_GRADE_LEVEL) {
                        heighestLowestGradeLevel = lowest;
                    }
		}
            }
        }
        return heighestLowestGradeLevel > 0 ? heighestLowestGradeLevel : DEFAULT_GRADE_LEVEL;
    }

    int MAX_GRADE_LEVEL = 9999;
    int DEFAULT_GRADE_LEVEL = 8;

    @Override
    public CmProgramFlowAction getNextAction() throws Exception {
        return new CmProgramFlowAction(CmPlace.PRESCRIPTION);
    }

    static public void main(String as[]) {
        try {
            int uid=Integer.parseInt(as[0]);
            Connection conn = HotMathProperties.getInstance().getDataSourceObject().getSbDBConnection().getConnection();
            CmProgramFlow flow = new CmProgramFlow(conn, uid);
            flow.reset(conn);
            
            GetUserInfoAction action = new GetUserInfoAction(uid, null);
            UserLoginResponse response = new GetUserInfoCommand().execute(conn, action);
            
            AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, response.getUserInfo().getRunId());
            System.out.println("Prescription grade level: " + pres.getGradeLevel());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.exit(0);
    }
}
