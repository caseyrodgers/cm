package hotmath.assessment;

import hotmath.ProblemID;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;

import java.sql.Connection;
import java.util.List;

import sb.logger.SbLogger;


public class AssessmentPrescriptionCustom extends AssessmentPrescription {
    int _customProgramSubjectLevel;
    static final int PID_COUNT = 3;
    public AssessmentPrescriptionCustom(final Connection conn,HaTestRun testRun) throws Exception {
        super();
        
        this.testRun = testRun;
        int custProgId = testRun.getHaTest().getProgramInfo().getCustomProgramId();
        
        CmList<CustomLessonModel> progLessons = new CmCustomProgramDao().getCustomProgramDefinition(conn, custProgId);
        int sessNum = 0;
        for(CustomLessonModel clm: progLessons) {
            
            INeedMoreHelpItem item = new INeedMoreHelpItem("review",clm.getFile(),clm.getLesson());
            InmhItemData itemData = new InmhItemData(item);
            
            // now choose pids from the pool for this item
            List<RppWidget> workBookPids = itemData.getWookBookSolutionPool(conn);
            if (workBookPids.size() == 0) {
                logger.warn("No pool solutions found for + '" + itemData.getInmhItem().toString() + "'");
                continue; // nothing to see here.
            }

            // cmdao.getSubjectLevel(clm.getSubject());
            
            AssessmentPrescriptionSession session = new AssessmentPrescriptionSession(this,"Session: " + (sessNum + 1));
            for(RppWidget widget: workBookPids) {
                ProblemID pid = new ProblemID(widget.getFile());
                // subject filter solutions
                int gradeLevel = pid.getGradeLevel();
                if (gradeLevel > getGradeLevel()) {
                    SbLogger.postMessage("AssessmentPrescriptionSession: " + testRun.getRunId() + ", level: " + getGradeLevel() + ", inmh item not included due to higher grade level:  " + pid + ", level: " + gradeLevel);
                    continue;
                }
                    
                    
                List<SessionData> si = session.getSessionItems();

                si.add(new SessionData(itemData.getInmhItem(), pid.getGUID(), (int) PID_COUNT, itemData.getWeight()));
 
                if (si.size() > TOTAL_SESSION_SOLUTIONS-1)
                    break;
            }
            
            // assert that there is at least one
            if(session.getSessionItems().size() == 0) {
                // this session has no items, so it is invalid and will be
                // skipped
                SbLogger.postMessage("AssessmentPrescriptionSession: session has no items: " + session);
            }
            else {
                // add this session, and move to next
               _sessions.add(session);
               sessNum++;
            }
        }
        new HaTestRunDao().addLessonsToTestRun(conn,testRun, _sessions);
    }
 
}
