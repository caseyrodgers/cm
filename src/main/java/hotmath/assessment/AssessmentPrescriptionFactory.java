package hotmath.assessment;

import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;
import hotmath.testset.ha.HaTestRunDao.TestRunLesson;

import java.sql.Connection;
import java.util.List;

public class AssessmentPrescriptionFactory {
	
	private AssessmentPrescriptionFactory() {}
	
	
	/** Create the appropriate type of assessment prescription object
	 * 
	 * @param testRun
	 * @return
	 * @throws Exception
	 */
	static public AssessmentPrescription create(final Connection conn, HaTestRun testRun) throws Exception {
		if(testRun.getHaTest().getTestDef().getName().indexOf("Auto-Enrollment") > -1)
			return new AssessmentPrescriptionPlacement(conn, testRun);
		else
			return new AssessmentPrescription(conn, testRun);
	}
	
	static public AssessmentPrescription createOrLoadExisting(final Connection conn, HaTestRun testRun) throws Exception {
	    List<TestRunLesson> lessons = new HaTestRunDao().loadTestRunLessonsAndPids(conn, testRun.getRunId());
	    if(lessons.size() == 0) {
	        
	        if(testRun.getHaTest().getTestDef().getTestDefId() == 36) {
	            return new AssessmentPrescriptionCustom(conn, testRun);
	        }
	        else {
	            return new AssessmentPrescription(conn, testRun);
	        }
	    }
	    else {
	        return new AssessmentPrescription(conn,lessons, testRun);
	    }
	}
}
