package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;


/** HaTest Class 
 *  
 *  Describes a single Hotmath Advance test.
 *  
 *  
 * @author Casey
 *
 */
public class HaTest {

    static Logger __logger = Logger.getLogger(HaTest.class);
    
	final static int QUESTIONS_PER_TEST  = 10;
	final static int SEGMENTS_PER_PROGRAM = 4;
	
	HaUser user;
	Integer testId;
	List<String> pids = new ArrayList<String>();
	HaTestDef testDef;
	int segment;
	int totalSegments;
	int numTestQuestions;
	
	StudentUserProgramModel programInfo;
	
	public int getTotalSegments() {
		return totalSegments;
	}

	public void setTotalSegments(int totalSegments) {
		this.totalSegments = totalSegments;
	}

	public int getNumTestQuestions() {
		return numTestQuestions;  
	}

	public void setNumTestQuestions(int numTestQuestions) {
		this.numTestQuestions = numTestQuestions;
	}

	public Integer getTestId() {
		return testId;
	}

	public void setTestId(Integer testId) {
		this.testId = testId;
	}
	
	public HaUser getUser() {
		return user;
	}
	public void setUser(HaUser user) {
		this.user = user;
	}
	
	public void addPid(String pid) {
		this.pids.add(pid);
	}
	
	public List<String> getPids() {
		return this.pids;
	}

	public void setTestDef(HaTestDef testDef) {
	    this.testDef = testDef;
	}
	
	public HaTestDef getTestDef() {
		return this.testDef;
	}
	
	public String getTitle() {
		return testDef.getTitle();
	}
	
	public String getSubTitle(int segment) {
		return testDef.getSubTitle(segment);		
	}
	
	/** Return the current segment the user is working in
	 * 
	 * @param segment
	 */
	public void setSegment(int segment) {
		this.segment = segment;
	}
	
	public int getSegment() {
		return this.segment;
	}

    /** 
     * return the number of questions in this test
     * @return
     */
	public int getTestQuestionCount() {
		return QUESTIONS_PER_TEST;
	}
	
	public int getSegmentCount() {
		return getTestDef().getTotalSegmentCount();
	}
	
	
	public StudentUserProgramModel getProgramInfo() {
        return programInfo;
    }

    public void setProgramInfo(StudentUserProgramModel programInfo) {
        this.programInfo = programInfo;
    }

    /** Save an answer for this test question.
	 *  
	 *  Only one answer per question.
	 *   
	 * @param pid
	 * @param answer
	 * @throws Exception
	 */
	public void saveTestQuestionChange(String pid, int answer, boolean isCorrect) throws Exception {
		Connection conn=null;
		PreparedStatement pstat=null;
		try {
			String sql = "insert into HA_TEST_CURRENT(test_id, pid, response_number, is_correct)value(?,?,?,?)";
			conn = HMConnectionPool.getConnection();
			
			// remove the current selected value;
			Statement smtt = conn.createStatement();
			String sql2 = "delete from HA_TEST_CURRENT where test_id = '" + getTestId() + "' and pid = '" + pid + "'";
			smtt.executeUpdate(sql2);
			pstat = conn.prepareStatement(sql);
			
			pstat.setInt(1,getTestId());
			pstat.setString(2, pid);
			pstat.setInt(3,answer);
			pstat.setBoolean(4, isCorrect);
			
			int cnt = pstat.executeUpdate();
			if(cnt != 1)
				throw new HotMathException("Create not update current test answer: " + pid);  // but why?
		}
		finally {
			SqlUtilities.releaseResources(null,pstat,conn);
		}		
	}
	
	/** Return list of objects representing each pid and the current answer
	 * selected.  
	 * 
	 * @return
	 * @throws Exception
	 */
	public List<HaTestRunResult> getTestCurrentResponses(Connection conn) throws Exception {
		
		List<HaTestRunResult> results = new ArrayList<HaTestRunResult>();
		
		PreparedStatement pstat=null;
		ResultSet rs = null;
		try {
			String sql = "select * from v_HA_TEST_CURRENT_STATUS where test_id = ?";
			pstat = conn.prepareStatement(sql);
			
			pstat.setInt(1,getTestId());
			
			rs = pstat.executeQuery();
			while(rs.next()) {
				HaTestRunResult res = new HaTestRunResult();
				res.setPid(rs.getString("pid"));
				String isCorrect = "";
				String re = rs.getString("is_correct");
				String responseNum = rs.getString("response_number");
				if(re != null) {
				    res.setResponseIndex(rs.getInt("response_number"));
				    if(re.equals("1"))
				        res.setResult("Correct");
				    else 
				        res.setResult("Incorrect");
				}
				else {
				    res.setResult("Unanswered");
				}
				
                results.add(res);
			    
			}
		}
		finally {
			SqlUtilities.releaseResources(rs,pstat,null);
		}	
		
		return results;
	}
	
	
	/** Remove from DB all current answer selections
	 * 
	 * @throws Exception
	 */
	private void clearCurrentResults(final Connection conn) throws Exception {
		
		PreparedStatement pstat=null;
		try {
			String sql = "delete from HA_TEST_CURRENT where test_id = ?";
			pstat = conn.prepareStatement(sql);
			pstat.setInt(1,getTestId());
		    pstat.executeUpdate();
		}
		finally {
			SqlUtilities.releaseResources(null,pstat,null);
		}			
	}	
	
	/** Return the test run (prescription) that is currently active
	 *  for this user.
	 *  
	 * @return
	 */
	public int getCurrentTestRunId() {
		return getUser().getActiveTestRunId();
	}

	
	/** Convenience method used to create a new HaTest from raw data
	 * 
	 * @param uid
	 * @param textCode
	 * @param chapter
	 * @param startPn
	 * @param endPn
	 * @return
	 * @throws HotMathException
	 */
	static public HaTest createTest(final Connection conn, Integer uid,HaTestDef testDef,int segment) throws HotMathException {
		PreparedStatement pstat=null;
		ResultSet rs = null;
		try {
			
		    __logger.debug("creating test: " + uid + ", " + testDef + ", " + segment);
			
			String sql = "insert into HA_TEST(user_id,user_prog_id,test_def_id,create_time,test_segment,test_segment_slot, total_segments,test_question_count)values(?,?,?,?,?,?,?,?)";
			
			pstat = conn.prepareStatement(sql);
			
			
			// get the program info used to create this test
			StudentUserProgramModel userProgram = new CmStudentDao().loadProgramInfoCurrent(conn, uid);
			
			
			/** Determine the proper quiz zone to use
			 * 
			 */
			int segmentSlot = new CmStudentDao().loadActiveInfo(conn, uid).getActiveSegmentSlot();
			
            HaUser user = HaUser.lookUser(conn,uid,null);
            HaTestConfig config = user.getTestConfig();
            
            List<String> testIds = testDef.getTestIdsForSegment(conn,segment,config, segmentSlot);

			pstat.setInt(1,uid);
			pstat.setInt(2, userProgram.id);
			pstat.setInt(3,testDef.getTestDefId());
			pstat.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
			pstat.setInt(5,segment);
			pstat.setInt(6,segmentSlot);
			pstat.setInt(7,config.getSegmentCount());
			pstat.setInt(8,testIds.size());
			
			// make sure there are not currently defines items for this test
			//Statement stmt = conn.createStatement();
			//stmt.executeUpdate("delete from HA_TEST_IDS where test_id);
			//stmt.close();
			
			int cnt = pstat.executeUpdate();
			if(cnt != 1)
				throw new HotMathException("Create not create new test for: " + uid);  // but why?
			
			// extract the auto created pk
		    int testId = -1;
		    try {
		    	rs = pstat.getGeneratedKeys();
		    	if (rs.next()) {
		    		testId = rs.getInt(1);
		    	} else {
		    		throw new HotMathException("Error creating PK for test");
		    	}
		    }
		    finally {
				SqlUtilities.releaseResources(rs, pstat, null);
		    }
		    
		    // insert IDS to use for this test
		    pstat = conn.prepareStatement("insert into HA_TEST_IDS(test_id, pid) values(?,?)");
		    for(String pid: testIds) {
		    	pstat.setInt(1,testId);
		    	pstat.setString(2,pid);
		    	
		    	if(pstat.executeUpdate() != 1)
		    		throw new Exception("Could not add a problem id to a test");
		    }
		    
		    // mark this user's record indicating this test as the active
		    user.setActiveTest(testId);
		    user.setActiveTestRunId(null);
		    user.setActiveTestSegment(segment);
		    user.update(conn);
		    
		    return loadTest(conn,testId);
		}
		catch(HotMathException hme) {
			throw hme;
		}
		catch(Exception e) {
			throw new HotMathException(e,"Error looking up Catchup Math user: " + e.getMessage());
		}
		finally {
			SqlUtilities.releaseResources(null,pstat,null);
		}
	}
	
	static public HaTest loadTest(final Connection conn, int testId) throws HotMathException {
	    
	    
	    __logger.debug("Loading test: " + testId);
	    
	    HaTest testCached = (HaTest)CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST, testId);
	    if(testCached != null)
	        return testCached;
	        
		PreparedStatement pstat=null;
		ResultSet rs = null;
		try {
			String sql = CmMultiLinePropertyReader.getInstance().getProperty("HA_TEST_LOAD");
			pstat = conn.prepareStatement(sql);
			pstat.setInt(1,testId);
			rs = pstat.executeQuery();
			if(!rs.first())
				throw new HotMathException("Could not load test");

			HaTest test = new HaTest();
			test.setTestId(rs.getInt("test_id"));
			test.setUser(HaUser.lookUser(conn,rs.getInt("user_id"),null));
			test.setSegment(rs.getInt("test_segment"));
			test.setNumTestQuestions(rs.getInt("test_question_count"));
			test.setTotalSegments(rs.getInt("total_segments"));
			
			/** Create program model and add to HaTest object
			 * 
			 */
			StudentUserProgramModel programInfo = null;
			Integer prgId = rs.getInt("prog_id");
			if(prgId != null && prgId > 0) {
    			/** (new style) use data from CM_USER_PROGRAM attached to this test record
    			 * 
    			 */
                __logger.debug("Loading test program info from CM_USER_PROGRAM: " + testId + ", " + prgId);
			    
			    programInfo = new StudentUserProgramModel();
  	            programInfo.setId(prgId);
			    programInfo.setAdminId(rs.getInt("admin_id"));
    			programInfo.setCreateDate(rs.getDate("prog_create_date"));
    			programInfo.setTestDefId(rs.getInt("prog_test_def_id"));
    			programInfo.setTestName(rs.getString("prog_test_name"));
    			programInfo.setConfig(new HaTestConfig(rs.getString("test_config_json")));
			}
			else {
			    /** fall back to extracting program info attached to user
			     * 
			     */
                __logger.debug("Loading test program info from current assigned program: " + testId);
                
			    programInfo = new CmStudentDao().loadProgramInfoCurrent(conn, test.getUser().getUid());
			}
			
			test.setProgramInfo(programInfo);
            test.setTestDef(HaTestDefFactory.createTestDef(conn, programInfo.getTestDefId()));

            
			/** Get all ids defined for test and add to HaTest object
			 * 
			 */
			List<String> testIds = getTestIdsForTest(conn,test.getTestId());
			for(String pid: testIds) {
				test.addPid(pid);
			}

			
			//HaTestConfig config = new HaTestConfig(configJson);
			//test.setConfig(config);
			
			CmCacheManager.getInstance().addToCache(CacheName.TEST, testId, test);
			
			return test;
		}
		catch(HotMathException hme) {
			throw hme;
		}
		catch(Exception e) {
			throw new HotMathException(e,"Error looking up Hotmath Advance user: " + e.getMessage());
		}
		finally {
			SqlUtilities.releaseResources(rs,pstat,null);
		}
	}
	
	
	int TEST_SIZE=10;
	/** Create a new Prescription based on test answers
	 * 
	 * @param wrongGids
	 * @param answeredCorrect
	 * @param answeredIncorrect
	 * @param totalSessions
	 * @return
	 * @throws HotMathException
	 */
	public HaTestRun createTestRun(final Connection conn, String wrongGids[], int answeredCorrect, int answeredIncorrect, int notAnswered) throws HotMathException {
		PreparedStatement pstat=null;
		ResultSet rs = null;
		try {
		    
	        /** Determine if user passed this quiz/test
	         * 
	         * This information is used to move to the next
	         * quiz slot.
	         * 
	         */
	        CmStudentDao dao = new CmStudentDao();
	        StudentUserProgramModel pinfo = dao.loadProgramInfoCurrent(conn, getUser().getUid());
	        int passPercentRequired = pinfo.getConfig().getPassPercent();
	        int testCorrectPercent = GetPrescriptionCommand.getTestPassPercent(answeredCorrect + answeredIncorrect + notAnswered, answeredCorrect);
	        boolean passedQuiz = (testCorrectPercent >= passPercentRequired);
	                
			
			HaTest test = HaTest.loadTest(conn, testId);
			String sql = "insert into HA_TEST_RUN(test_id, run_time, answered_correct, answered_incorrect, not_answered,run_session,is_passing)values(?,?,?,?,?,1,?)";
			pstat = conn.prepareStatement(sql);
			HaTestRun testRun = new HaTestRun();
			
			pstat.setInt(1,testId);
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			pstat.setTimestamp(2,ts);
			pstat.setInt(3,answeredCorrect);
			pstat.setInt(4,answeredIncorrect);
			pstat.setInt(5,notAnswered);
			pstat.setInt(6, passedQuiz?1:0);
			
			int cnt = pstat.executeUpdate();
			if(cnt != 1)
				throw new HotMathException("Create not create new test for: " + testId);
			
		    int runId = -1;
		    rs = pstat.getGeneratedKeys();
		    if (rs.next()) {
		        runId = rs.getInt(1);
		    } else {
		    	throw new HotMathException("Error creating PK for test");
		    }

		    
		    testRun.setRunId(runId);
			testRun.setRunTime(ts.getTime());
			testRun.setHaTest(test);
			testRun.setAnsweredCorrect(answeredCorrect);
			testRun.setAnsweredIncorrect(answeredIncorrect);
			testRun.setPassing(passedQuiz);
			
			/** transfer current selections to this test run
			 * 
			 */
			testRun.transferCurrentToTestRun(conn);
			
			
			/** Clear all existing selections for this test
			 * 
			 */
			test.clearCurrentResults(conn);
			
			/** 
			 * update this User's row to indicate new action test run
			 */
			test.getUser().setActiveTestRunId(testRun.getRunId());
			test.getUser().update(conn);
			
			
			updateTestRunSessions(conn, runId);
            
			return testRun;
		}
		catch(HotMathException hme) {
			throw hme;
		}
		catch(Exception e) {
			throw new HotMathException(e,"Error looking up Hotmath Advance test: " + e.getMessage());
		}
		finally {
			SqlUtilities.releaseResources(rs,pstat,null);
		}
	}
	
	
	/** Update the total sessions for a given test_run.
	 * 
	 * 
	 *   This will create the prescription and extract the 
	 *   session count and update appropriate HA_TEST_RUN.
	 *  
	 * @param conn
	 * @param runId
	 * @throws Exception
	 */
	static public void updateTestRunSessions(Connection conn, Integer runId) throws Exception {
        /** 
         *  now pre-create the prescription, and extract the total number of sessions
         *  
         *  NOTE: this is needed because, we need the test_run to create the prescription.
         */
        AssessmentPrescription pres = AssessmentPrescriptionManager.getInstance().getPrescription(conn, runId);
        int totalSessions = pres.getSessions().size();
        
        // update the HA_TEST_RUN record
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            int c = stmt.executeUpdate("update HA_TEST_RUN set total_sessions = " + totalSessions + " where run_id = " + runId);
            if(c != 1) {
                throw new HotMathException("Could not update test_run with total_sessions: " + runId);
            }
        }
        finally {
            SqlUtilities.releaseResources(null,stmt,null);
        }           
	}

	/** Return list of pids that represent all ids currently in test
	 *  for all segments.  
	 *  
	 *  Print warning if zero found.
	 * @param testId
	 * 
	 * @return
	 * @throws Exception
	 */
	static public List<String> getTestIdsForTest(final Connection conn,int testId) throws Exception {
		
		List<String> pids = new ArrayList<String>();
		PreparedStatement pstat=null;
		ResultSet rs = null;
 
		try {
			// now read test's pids
			String sql = "select * from HA_TEST_IDS where test_id = ?";
			pstat = conn.prepareStatement(sql);
			pstat.setInt(1,testId);
			rs = pstat.executeQuery();
			if(!rs.first()) {
				// HotMathLogger.logMessage("hatest", "Could not read test pids for: " + testId,"");
			}
			else {
				do {
					pids.add(rs.getString("pid"));
				}while(rs.next());
			}
			return pids;
		}
		finally {
			SqlUtilities.releaseResources(rs,pstat,null);
		}
	}	
	
	public String toString() {
		return user + "," + testId;
	}
}
