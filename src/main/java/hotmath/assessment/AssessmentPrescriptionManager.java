package hotmath.assessment;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaTestRunDao;

import java.sql.Connection;

import org.apache.log4j.Logger;


public class AssessmentPrescriptionManager {
	
	
	static private AssessmentPrescriptionManager __instance;
	static public AssessmentPrescriptionManager getInstance() {
		if(__instance == null)
			__instance = new AssessmentPrescriptionManager();
		return __instance;
	}

	
	static Logger __logger = Logger.getLogger(AssessmentPrescriptionManager.class);
	
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
	public AssessmentPrescription createPrescription(final Connection conn, int testId,String pids,int answeredCorrect, int answeredIncorrect, int notAnswered) throws Exception {
		
		HaTest test = HaTestDao.loadTest(conn,testId);
		AssessmentPrescription pres =
			AssessmentPrescriptionFactory.create(conn, HaTestDao.createTestRun(conn, test.getUser().getUid(), testId,answeredCorrect, answeredIncorrect, notAnswered));

		CmCacheManager.getInstance().addToCache(CacheName.PRESCRIPTION, pres.getTestRun().getRunId().toString(), pres);
		return pres;
	}

	
	
	public AssessmentPrescription getPrescription(final Connection conn, Integer runId) throws Exception {
	    
	    /** First check memory cache
	     * 
	     */
		__logger.info("Getting prescription for: " + runId);
	    AssessmentPrescription pres = (AssessmentPrescription)CmCacheManager.getInstance().retrieveFromCache(CacheName.PRESCRIPTION, runId.toString());
		if(pres == null) {
			__logger.info("Creating new prescription for: " + runId);
			/**
			 * first need to lookup the test for this run
			 */
			HaTestRun testRun = new HaTestRunDao().lookupTestRun(conn, runId);

			if(testRun.getHaTest().getTestDef().getName().indexOf("Auto-Enrollment") > -1)
			    pres = AssessmentPrescriptionFactory.create(conn, testRun);
			else 
			    pres = AssessmentPrescriptionFactory.createOrLoadExisting(conn, testRun);
			
			CmCacheManager.getInstance().addToCache(CacheName.PRESCRIPTION, pres.getTestRun().getRunId().toString(), pres);
		}
		else {
			__logger.info("Return prescription from cache: " + runId);
		}
		return pres;
	}
}
