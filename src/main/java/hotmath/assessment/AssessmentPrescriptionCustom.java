package hotmath.assessment;

import hotmath.ProblemID;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import sb.logger.SbLogger;


public class AssessmentPrescriptionCustom extends AssessmentPrescription {
    int _customProgramSubjectLevel;
    
    public AssessmentPrescriptionCustom(final Connection conn,HaTestRun testRun) throws Exception {
        super();
        
        this.testRun = testRun;
        int custProgId = testRun.getHaTest().getProgramInfo().getCustomProgramId();
        
        CmList<CustomLessonModel> progLessons = new CmCustomProgramDao().getCustomProgramDefinition(conn, custProgId);
        CmCustomProgramDao cmdao = new CmCustomProgramDao();
        for(CustomLessonModel lesson: progLessons) {
            int lev = cmdao.getSubjectLevel(lesson.getSubject());
            if(lev > _customProgramSubjectLevel) {
                _customProgramSubjectLevel = lev;
            }
        }
        
        
        List<InmhItemData> itemsData = new ArrayList<InmhItemData>();
        for(CustomLessonModel clm: progLessons) {
            INeedMoreHelpItem item = new INeedMoreHelpItem("review",clm.getFile(),clm.getLesson());
            itemsData.add(new InmhItemData(item));
        }
        
        int sessNum = 0;
        for (InmhItemData id : itemsData) {
            int numPids2get = 3;
            
            // now choose pids from the pool for this item
            List<ProblemID> workBookPids = id.getWookBookSolutionPool(conn);
            if (workBookPids.size() == 0) {
                logger.warn("No pool solutions found for + '" + id.getInmhItem().toString() + "'");
                continue; // nothing to see here.
            }

            AssessmentPrescriptionSession session = new AssessmentPrescriptionSession(this,"Session: " + (sessNum + 1));
            for(ProblemID pid: workBookPids) {

                // subject filter solutions
                int gradeLevel = pid.getGradeLevel();
                if (gradeLevel > getGradeLevel()) {
                    SbLogger.postMessage("AssessmentPrescriptionSession: " + testRun.getRunId() + ", level: " + getGradeLevel() + ", inmh item not included due to higher grade level:  " + pid + ", level: " + gradeLevel);
                    continue;
                }
                    
                    
                List<SessionData> si = session.getSessionItems();

                si.add(new SessionData(id.getInmhItem(), pid.getGUID(), (int) numPids2get, id.getWeight()));
 
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
    
    @Override
    /** get grade level for the current custom program 
     *  by using the highest level from lessons in program.
     *  
     *  _customProgramSubjectLevel set in constructor.
     *  
     */
    public int getGradeLevel() {
        return _customProgramSubjectLevel;
    }    
}
