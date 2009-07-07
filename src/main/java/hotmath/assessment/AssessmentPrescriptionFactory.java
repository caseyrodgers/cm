package hotmath.assessment;

import hotmath.testset.ha.HaTestRun;

import java.sql.Connection;

public class AssessmentPrescriptionFactory {
	
	private AssessmentPrescriptionFactory() {}
	
	
	/** Create the appropiate type of assessment prescription object
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
}
