package hotmath.testset.ha;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.testset.ha.HaTestRunDao.TestRunLesson;

import java.util.List;

public class HaTestRunDao_Test extends CmDbTestCase {
    
    public HaTestRunDao_Test(String name) {
        super(name);
    }

    public void testLoadExistingPrescription() throws Exception {
        HaTestRun testRun = (_testRun != null?_testRun:setupDemoAccountTestRun());
        
        /** this will get original from cache 
         * 
         */
        AssessmentPrescription apOrignal = AssessmentPrescriptionManager.getInstance().getPrescription(conn,testRun.getRunId());
        
        /** create from persistent state
         * 
         */
        List<TestRunLesson> lessons = new HaTestRunDao().loadTestRunLessonsAndPids(conn, testRun.getRunId());

        AssessmentPrescription apLoaded = new AssessmentPrescription(conn,lessons,testRun);
        
        assertTrue(apOrignal.equals(apLoaded));
    }
    
    
    public void testMarkLessonAsViewed() throws Exception  {
        HaTestRun testRun = (_testRun != null?_testRun:setupDemoAccountTestRun());
        
        HaTestRunDao dao = new HaTestRunDao();
        dao.setLessonViewed(conn, _testRun.getRunId(), 1);
        List<TestRunLessonModel> lessons = dao.getTestRunLessons(conn, testRun.getRunId());
        
        assertTrue(lessons.get(1).getViewed());
    } 

    public void testAddLessons() throws Exception {
        AssessmentPrescription p = setupDemoAccountPrescription();
        
        new HaTestRunDao().addLessonsToTestRun(conn,_testRun, p.getSessions());
    }


    public void testGetTestRunLessons() throws Exception  {
        HaTestRun testRun = (_testRun != null?_testRun:setupDemoAccountTestRun());
        
        List<TestRunLessonModel> lessons = new HaTestRunDao().getTestRunLessons(conn, testRun.getRunId());
        assertTrue(lessons != null);
        assertTrue(lessons.size() > 0);
        assertTrue(lessons.get(0).getFile() != null);
    }
    
    
    public void testMarkLessonAsCompleted() throws Exception  {
        HaTestRun testRun = (_testRun != null?_testRun:setupDemoAccountTestRun());
        
        HaTestRunDao dao = new HaTestRunDao();
        List<TestRunLessonModel> lessons = dao.getTestRunLessons(conn, testRun.getRunId());
        
        dao.markLessonAsCompleted(conn, _testRun.getRunId(), lessons.get(0).getLesson());
    }
}
