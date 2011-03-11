package hotmath.assessment;

import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel.Type;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;

import java.sql.Connection;
import java.util.List;

import org.apache.log4j.Logger;


public class AssessmentPrescriptionCustom extends AssessmentPrescription {
    
    final static Logger __logger = Logger.getLogger(AssessmentPrescriptionCustom.class);
    
    int _customProgramSubjectLevel;
    public AssessmentPrescriptionCustom(final Connection conn,HaTestRun testRun) throws Exception {
        super(conn);
        
        __logger.info("Creating custom program: " + testRun);
        
        this.testRun = testRun;
        
        int custProgId = testRun.getHaTest().getProgramInfo().getCustomProgramId();
        
        readAssessment();        
        
        HaTestDef def = testRun.getHaTest().getTestDef();
        
        CmList<CustomLessonModel> progLessons = new CmCustomProgramDao().getCustomProgramDefinition(conn, custProgId);
        int sessNum = 0;
        for(CustomLessonModel cpProgItem: progLessons) {
            
            AssessmentPrescriptionSession session=null;
            if(cpProgItem.getCustomProgramType() == Type.LESSON) {
                /** is a lesson custom program item
                 * 
                 */
                INeedMoreHelpItem item = new INeedMoreHelpItem("review",cpProgItem.getFile(),cpProgItem.getLesson());
                InmhItemData itemData = new InmhItemData(item);
            
                // now choose pids from the pool for this item
                List<RppWidget> workBookPids = itemData.getWookBookSolutionPool(conn,testRun.getHaTest().getUser().getUid() + "/" + testRun.getRunId());
                if (workBookPids.size() == 0) {
                    logger.warn("No pool solutions found for + '" + itemData.getInmhItem().toString() + "'");
                    continue; // nothing to see here.
                }
                session = createSession(sessNum,workBookPids,itemData,true);
            }
            else {
                /** is a quiz custom program item
                 * 
                 */
                break;
            }
            
            // assert that there is at least one
            if(session.getSessionItems().size() == 0) {
                // this session has no items, so it is invalid and will be
                // skipped
                __logger.error("AssessmentPrescriptionSession: session has no items: " + session);
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
