package hotmath.assessment;

import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;

import java.sql.Connection;
import java.util.List;

public class AssessmentPrescriptionCustomMobile extends AssessmentPrescription {

    public AssessmentPrescriptionCustomMobile(final Connection conn,HaTestRun testRun,List<CustomLessonModel> lessonModels) throws Exception {
        super();
        
        this.testRun = testRun;
        int custProgId = testRun.getHaTest().getProgramInfo().getCustomProgramId();
        
        int sessNum = 0;
        for(CustomLessonModel clm: lessonModels) {
            
            INeedMoreHelpItem item = new INeedMoreHelpItem("review",clm.getFile(),clm.getLesson());
            InmhItemData itemData = new InmhItemData(item);
            
            // now choose pids from the pool for this item
            List<RppWidget> workBookPids = itemData.getWookBookSolutionPool(conn,testRun.getHaTest().getUser().getUid() + "/" + testRun.getRunId(),false);
            if (workBookPids.size() == 0) {
                logger.warn("No pool solutions found for + '" + itemData.getInmhItem().toString() + "'");
            }

            AssessmentPrescriptionSession session = createSession(sessNum,workBookPids,itemData,false);
            // add this session, and move to next
            _sessions.add(session);
            sessNum++;
        }
        new HaTestRunDao().addLessonsToTestRun(conn,testRun, _sessions);
    }

}
