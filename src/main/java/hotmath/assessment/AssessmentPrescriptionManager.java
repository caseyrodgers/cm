package hotmath.assessment;

import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestRun;

import java.util.HashMap;
import java.util.Map;


public class AssessmentPrescriptionManager {
	
	
	static private AssessmentPrescriptionManager __instance;
	static public AssessmentPrescriptionManager getInstance() {
		if(__instance == null)
			__instance = new AssessmentPrescriptionManager();
		return __instance;
	}

	
	Map<Integer, AssessmentPrescription> _prescriptions = new HashMap<Integer, AssessmentPrescription>();
	private AssessmentPrescriptionManager() {
		//
	}
	

	/** 
	 * create the appropriate type of AssessmentPrescription
	 * 
	 * @param testId
	 * @param pids
	 * @param answeredCorrect
	 * @param answeredIncorrect
	 * @return
	 * @throws Exception
	 */
	public AssessmentPrescription createPrescription(int testId,String pids,int answeredCorrect, int answeredIncorrect, int notAnswered) throws Exception {
		
		HaTest test = HaTest.loadTest(testId);
		AssessmentPrescription pres = AssessmentPrescriptionFactory.create(test.createTestRun(pids.split(","),answeredCorrect, answeredIncorrect, notAnswered));
		
		_prescriptions.put(pres.getTestRun().getRunId(), pres);
		return pres;
	}

	
	
	public AssessmentPrescription getPrescription(int runId) throws Exception {
		AssessmentPrescription pres = _prescriptions.get(runId);
		if(pres == null) {
			// create new one and store in map
			
			// first need to lookup the test for this run
			HaTestRun testRun = HaTestRun.lookupTestRun(runId);
			
			String pidList = testRun.getPidList();
			pres = AssessmentPrescriptionFactory.create(testRun);
			pres.setTestRun(testRun);
			
			_prescriptions.put(runId, pres);
		}
		return pres;
	}
}
