package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/** HaTest Class 
 *  
 *  Describes a single Hotmath Advance test.
 *  
 *  
 * @author Casey
 *
 */
public class HaTest {
	
	final static int QUESTIONS_PER_TEST  = 10;
	final static int SEGMENTS_PER_PROGRAM = 4;
	
	HaUser user;
	Integer testId;
	List<String> pids = new ArrayList<String>();
	HaTestDef testDef;
	int segment;
	int totalSegments;
	int numTestQuestions;
	
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

	public void setTestDef(HaTestDef def) {
		this.testDef = def;
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
	public List<HaTestRunResult> getTestCurrentResponses() throws Exception {
		
		List<HaTestRunResult> results = new ArrayList<HaTestRunResult>();
		
		Connection conn=null;
		PreparedStatement pstat=null;
		try {
			String sql = "select * from v_HA_TEST_CURRENT_STATUS where test_id = ?";
			conn = HMConnectionPool.getConnection();
			pstat = conn.prepareStatement(sql);
			
			pstat.setInt(1,getTestId());
			
			ResultSet rs = pstat.executeQuery();
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
			SqlUtilities.releaseResources(null,pstat,conn);
		}	
		
		return results;
	}
	
	
	/** Remove from DB all current answer selections
	 * 
	 * @throws Exception
	 */
	public void clearCurrentResults() throws Exception {
		
		Connection conn=null;
		PreparedStatement pstat=null;
		try {
			String sql = "delete from HA_TEST_CURRENT where test_id = ?";
			conn = HMConnectionPool.getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setInt(1,getTestId());
		    pstat.executeUpdate();
		}
		finally {
			SqlUtilities.releaseResources(null,pstat,conn);
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
	static public HaTest createTest(Integer uid,HaTestDef testDef,int segment) throws HotMathException {
		Connection conn=null;
		PreparedStatement pstat=null;
		try {
			
			HaUser user = HaUser.lookUser(uid,null);
			
			HaTestConfig config = user.getTestConfig(); 
			
			List<String> testIds = testDef.getTestIdsForSegment(segment,config);
			
			String sql = "insert into HA_TEST(user_id,test_def_id,create_time,test_segment,total_segments,test_question_count)values(?,?,?,?,?,?)";
			conn = HMConnectionPool.getConnection();
			pstat = conn.prepareStatement(sql);
			
			pstat.setInt(1,uid);
			pstat.setInt(2,testDef.getTestDefId());
			pstat.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
			pstat.setInt(4,segment);
			pstat.setInt(5,4);
			pstat.setInt(6,testIds.size());
			
			// make sure there are not currently defines items for this test
			//Statement stmt = conn.createStatement();
			//stmt.executeUpdate("delete from HA_TEST_IDS where test_id);
			//stmt.close();
			
			int cnt = pstat.executeUpdate();
			if(cnt != 1)
				throw new HotMathException("Create not create new test for: " + uid);  // but why?
			
			// extract the auto created pk
		    int testId = -1;
		    ResultSet rs=null;
		    try {
		    	rs = pstat.getGeneratedKeys();
		    	if (rs.next()) {
		    		testId = rs.getInt(1);
		    	} else {
		    		throw new HotMathException("Error creating PK for test");
		    	}
		    }
		    finally {
		    	if (rs != null) rs.close();
		    }
		    
		    // insert IDS to use for this test
		    pstat.close();
		    pstat = conn.prepareStatement("insert into HA_TEST_IDS(test_id, pid) values(?,?)");
		    for(String pid: testIds) {
		    	pstat.setInt(1,testId);
		    	pstat.setString(2,pid);
		    	
		    	if(pstat.executeUpdate() != 1)
		    		throw new Exception("Could not add a problem id to a test");
		    }
		    
		    pstat.close();
		    
		    // mark this user's record indicating this test as the active
		    user.setActiveTest(testId);
		    user.setActiveTestRunId(null);
		    user.setActiveTestSegment(segment);
		    user.update();
		    
		    return loadTest(testId);
		}
		catch(HotMathException hme) {
			throw hme;
		}
		catch(Exception e) {
			throw new HotMathException(e,"Error looking up Catchup Math user: " + e.getMessage());
		}
		finally {
			SqlUtilities.releaseResources(null,pstat,conn);
		}
	}
	
	static Map<Integer, HaTest> __testCache = new HashMap<Integer, HaTest>(); 
	static public HaTest loadTest(int testId) throws HotMathException {
	    
	    
	    if(__testCache.containsKey(testId))
	        return __testCache.get(testId);
	        
	    
		Connection conn=null;
		PreparedStatement pstat=null;
		try {
			String sql = "select * from HA_TEST where test_id = ?";
			conn = HMConnectionPool.getConnection();
			pstat = conn.prepareStatement(sql);
			pstat.setInt(1,testId);
			ResultSet rs = pstat.executeQuery();
			if(!rs.first())
				throw new HotMathException("Could not load test");

			HaTest test = new HaTest();
			test.setTestId(rs.getInt("test_id"));
			test.setUser(HaUser.lookUser(rs.getInt("user_id"),null));
			test.setTestDef(HaTestDefFactory.createTestDef(rs.getInt("test_def_id")));
			test.setSegment(rs.getInt("test_segment"));
			test.setNumTestQuestions(rs.getInt("test_question_count"));
			test.setTotalSegments(rs.getInt("total_segments"));
			pstat.close();
			rs.close();

			List<String> testIds = getTestIdsForTest(test.getTestId());
			
			for(String pid: testIds) {
				test.addPid(pid);
			}
			
			__testCache.put(testId, test);
			
			return test;
		}
		catch(HotMathException hme) {
			throw hme;
		}
		catch(Exception e) {
			throw new HotMathException(e,"Error looking up Hotmath Advance user: " + e.getMessage());
		}
		finally {
			SqlUtilities.releaseResources(null,pstat,conn);
		}
	}
	
	
	int TEST_SIZE=10;
	/** Create a new Prescription based on test answers
	 * 
	 * @param wrongGids
	 * @param answeredCorrect
	 * @param answeredIncorrect
	 * @return
	 * @throws HotMathException
	 */
	public HaTestRun createTestRun(String wrongGids[], int answeredCorrect, int answeredIncorrect, int notAnswered) throws HotMathException {
		Connection conn=null;
		PreparedStatement pstat=null;
		try {
			
			HaTest test = HaTest.loadTest(testId);
			String sql = "insert into HA_TEST_RUN(test_id, run_time, answered_correct, answered_incorrect, not_answered, run_session)values(?,?,?,?,?,1)";
			conn = HMConnectionPool.getConnection();
			pstat = conn.prepareStatement(sql);
			HaTestRun testRun = new HaTestRun();
			
			pstat.setInt(1,testId);
			Timestamp ts = new Timestamp(System.currentTimeMillis());
			pstat.setTimestamp(2,ts);
			pstat.setInt(3,answeredCorrect);
			pstat.setInt(4,answeredIncorrect);
			pstat.setInt(5,notAnswered);
			
			int cnt = pstat.executeUpdate();
			if(cnt != 1)
				throw new HotMathException("Create not create new test for: " + testId);
			
		    int autoIncKeyFromApi = -1;
		    ResultSet rs=null;
		    try {
		    	rs = pstat.getGeneratedKeys();
		    	if (rs.next()) {
		    		autoIncKeyFromApi = rs.getInt(1);
		    	} else {
		    		throw new HotMathException("Error creating PK for test");
		    	}
		    }
		    finally {
		    	if (rs != null) rs.close();
		    }
		    testRun.setRunId(autoIncKeyFromApi);
			testRun.setRunTime(ts.getTime());
			testRun.setHaTest(test);
			testRun.setAnsweredCorrect(answeredCorrect);
			testRun.setAnsweredIncorrect(answeredIncorrect);
			
			testRun.transferCurrentToTestRun();
			
			// update this User's row to indicate new run
			test.getUser().setActiveTestRunId(testRun.getRunId());
			test.getUser().update();
			
			return testRun;
		}
		catch(HotMathException hme) {
			throw hme;
		}
		catch(Exception e) {
			throw new HotMathException(e,"Error looking up Hotmath Advance test: " + e.getMessage());
		}
		finally {
			SqlUtilities.releaseResources(null,pstat,conn);
		}
	}

	/** Return list of pids that represent all ids currently in test
	 *  for all segments.  
	 *  
	 *  Print warning if zero found.
	 * 
	 * @param testId
	 * @return
	 * @throws Exception
	 */
	static public List<String> getTestIdsForTest(int testId) throws Exception {
		
		List<String> pids = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pstat=null;
 
		try {
			conn = HMConnectionPool.getConnection();
			// now read test's pids
			String sql = "select * from HA_TEST_IDS where test_id = ?";
			pstat = conn.prepareStatement(sql);
			pstat.setInt(1,testId);
			ResultSet rs = pstat.executeQuery();
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
			SqlUtilities.releaseResources(null,pstat,conn);
		}
	}	
	
	public String toString() {
		return user + "," + testId;
	}
}
