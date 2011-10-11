package hotmath.assessment;

import hotmath.cm.login.ClientEnvironment;
import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm_admin.server.model.CmCustomProgramDao;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel.Type;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaUserDao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


public class AssessmentPrescriptionCustom extends AssessmentPrescription {
    
    final static Logger __logger = Logger.getLogger(AssessmentPrescriptionCustom.class);
    
    int _customProgramSubjectLevel;
    int _numSegments=0;
    List<AssessmentPrescriptionSession> _sessionsAll = new ArrayList<AssessmentPrescriptionSession>();
    
    public AssessmentPrescriptionCustom(final Connection conn,HaTestRun testRun) throws Exception {
        super(conn);
        
        __logger.info("Creating custom program: " + testRun);
        
        this.testRun = testRun;
        
        int custProgId = CmUserProgramDao.getInstance().loadProgramInfoForTest(testRun.getHaTest().getTestId()).getCustomProgramId();
        if(custProgId == 0) {
            throw new Exception("custom program ID must be specified");
        }
        
        readAssessment();        

        
        // now choose pids from the pool for this item
        int uid = testRun.getHaTest().getUser().getUid();
        ClientEnvironment clientEnvironment = HaUserDao.getInstance().getLatestClientEnvironment(uid);
        
        int segment=testRun.getHaTest().getSegment();
        CmList<CustomLessonModel> progLessons = CmCustomProgramDao.getInstance().getCustomProgramLessons(conn, custProgId, segment);
        int sessNum = 0;
        for(CustomLessonModel cpProgItem: progLessons) {
            AssessmentPrescriptionSession session=null;
            if(cpProgItem.getCustomProgramType() == Type.LESSON) {
                /** is a lesson custom program item
                 * 
                 */
                INeedMoreHelpItem item = new INeedMoreHelpItem("review",cpProgItem.getFile(),cpProgItem.getLesson());
                InmhItemData itemData = new InmhItemData(item);

                List<RppWidget> workBookPids = itemData.getWidgetPool(
                        conn,
                        uid + "/" + testRun.getRunId(),
                        clientEnvironment);
                if (workBookPids.size() == 0) {
                    logger.warn("No pool solutions found for + '" + itemData.getInmhItem().toString() + "'");
                    continue; // nothing to see here.
                }
                
                gradeLevel = getHighestGradeLevel(workBookPids);
                
                session = createSession(sessNum,workBookPids,itemData,true,clientEnvironment);
                
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
            else {
                /** is a quiz custom program item
                 * 
                 */
                _numSegments++;
            }
            
        }
        HaTestRunDao.getInstance().addLessonsToTestRun(conn,testRun, _sessions);
    }

    
    @Override
    public CmProgramFlowAction getNextAction() throws Exception {
        return new CmProgramFlowAction(CmPlace.PRESCRIPTION);
    }
    
    
    /** Return the grade level for this custom program 
     * 
     */
    int gradeLevel;
    @Override
    public int getGradeLevel() {
    	return gradeLevel;
    }
}
