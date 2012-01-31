package hotmath.assessment;

import hotmath.cm.login.ClientEnvironment;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;
import java.util.List;

public class AssessmentPrescriptionCustomMobile extends AssessmentPrescription {

    public AssessmentPrescriptionCustomMobile(final Connection conn,HaTestRun testRun,List<CustomLessonModel> lessonModels) throws Exception {
        super(conn);
        
        this.testRun = testRun;
        
        readAssessment();
        
        ClientEnvironment ce = HaUserDao.getInstance().getLatestClientEnvironment(testRun.getHaTest().getUser().getUid());
        
        int sessNum = 0;
        for(CustomLessonModel clm: lessonModels) {
            
            INeedMoreHelpItem item = new INeedMoreHelpItem("review",clm.getFile(),clm.getLesson());
            InmhItemData itemData = new InmhItemData(item);
            
            // now choose pids from the pool for this item
            List<RppWidget> workBookPids = itemData.getWidgetPool(conn,testRun.getHaTest().getUser().getUid() + "/" + testRun.getRunId());
            if (workBookPids.size() == 0) {
                logger.warn("No pool solutions found for + '" + itemData.getInmhItem().toString() + "'");
            }

            AssessmentPrescriptionSession session = createSession(sessNum,workBookPids,itemData,false,ce);
            // add this session, and move to next
            _sessions.add(session);
            sessNum++;
        }
        HaTestRunDao.getInstance().addLessonsToTestRun(conn,testRun, _sessions);
    }

}
