package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

public class HaTestRunDao {

    

    final static Logger __logger = Logger.getLogger(AssessmentPrescriptionSession.class);
    
    /** Add all lesson names assigned to this prescription 
     *  to the HaTestRunLesson object.  This table is used 
     *  as a quick method of accessing the lessons assigned
     *  to a testRun without having to recreate the prescription.
     */
    public void addLessonsToTestRun(final Connection conn, HaTestRun testRun, List<AssessmentPrescriptionSession> sessions) throws Exception  {
        
        if(testRun.getRunId() == null)
            return;
        
        PreparedStatement pstat=null;
        try {
        	// check sessions match (do we need to compare PIDs too?)
        	if (sessionsMatch(conn, testRun, sessions)) return;
        	
            conn.createStatement().executeUpdate("delete from HA_TEST_RUN_LESSON where run_id = " + testRun.getRunId());
            
            String sql = "insert into HA_TEST_RUN_LESSON(run_id, lesson_name, lesson_number, lesson_file) values(?, ?, ?, ?)";
            pstat = conn.prepareStatement(sql);
            for(int sn=0,t=sessions.size();sn < t;sn++) {
                AssessmentPrescriptionSession s = sessions.get(sn);
                
                pstat.setInt(1, testRun.getRunId());
                pstat.setString(2, s.getTopic());
                pstat.setInt(3, sn);
                pstat.setString(4, s.getSessionCategories().get(0).getFile());
                
                
                if(pstat.executeUpdate() != 1)
                    throw new Exception("Could not save lesson record for unknown reason: " + testRun);

                
                int lid = SqlUtilities.getLastInsertId(conn);
                
                // write out each pid lesson references
                PreparedStatement pstat2=null;
                try {
                    pstat2 = conn.prepareStatement("insert into HA_TEST_RUN_LESSON_PID(pid, lid, run_id)values(?,?,?)");
                    
                    for(SessionData sd: s.getSessionItems()) {
                        pstat2.setString(1, sd.getPid());
                        pstat2.setInt(2, lid);
                        pstat2.setInt(3, testRun.getRunId());
                        
                        if( pstat2.executeUpdate() != 1)
                            throw new Exception("Could not add to HA_TEST_RUN_LESSON_PID for unknown reasons");
                    }
                }
                finally {
                    SqlUtilities.releaseResources(null, pstat2,null);
                }
                
            }
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
        }
    }
    
    
    public void setLessonViewed(final Connection conn, Integer runId, Integer lessonViewed) throws Exception  {
        
        PreparedStatement pstat=null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LESSON_SET_LESSON_VIEWED");
            
            pstat = conn.prepareStatement(sql);
            
            pstat.setInt(1,runId);
            pstat.setInt(2, lessonViewed);
            
            int updated = pstat.executeUpdate();
            if(updated != 1) {
                __logger.info("Could not update lesson viewed: " + pstat);
            }
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
        }
    }
    
    
    /** Return all lessons assigned to this run
     * 
     * @param conn
     * @param runId
     * @return
     * @throws Exception
     */
    public List<TestRunLessonModel>  getTestRunLessons(final Connection conn, Integer runId) throws Exception {
        
        List<TestRunLessonModel> lessons = new ArrayList<TestRunLessonModel>();
        PreparedStatement pstat=null;
        PreparedStatement pstat2 = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LESSONS");
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, runId);
            
            
            String currLesson=null;
            ResultSet rs = pstat.executeQuery();
            TestRunLessonModel im = null;
            while(rs.next()) {
                String lesson = rs.getString("lesson_name");
                if(currLesson == null || !currLesson.equals(lesson)) {
                    im = new TestRunLessonModel(lesson,rs.getString("lesson_file"),rs.getDate("lesson_viewed"),rs.getDate("date_completed"));
                    lessons.add(im);
                    currLesson = lesson;
                }
                
                im.getPids().add(rs.getString("pid"));
            }
            return lessons;
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
            SqlUtilities.releaseResources(null,pstat2,null);
        }        
    }
    

    /** return the count of viewed lessons for the specified test run
     * 
     * @param conn
     * @param runId
     * @return
     * @throws Exception
     */
    public int getLessonsViewedCount(final Connection conn, Integer runId) throws Exception {
        PreparedStatement pstat=null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_ASSIGNED_LESSONS_VIEWED_COUNT");
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, runId);
            ResultSet rs = pstat.executeQuery();
            rs.first();
            return rs.getInt(1);
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
        }        
    }
    
    
    /** Mark this lesson as being viewed.
     * 
     *  set the date completed, but only set
     *  if currently unset.
     *  
     * @param conn
     * @param runId
     * @param lesson
     * @throws Exception
     */
    public void markLessonAsCompleted(final Connection conn, Integer runId, String lesson) throws Exception {
        
        PreparedStatement pstat = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LESSON_SET_DATE_COMPLETED");
            pstat = conn.prepareStatement(sql);
            
            pstat.setInt(1, runId);
            pstat.setString(2, lesson);
            
            pstat.executeUpdate();
        }
        finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }


	/** Look up and load existing test run named by runId
	 * 
	 * @param conn
	 * @param runId
	 * @return
	 * @throws HotMathException
	 */
	public HaTestRun lookupTestRun(final Connection conn, int runId) throws HotMathException {
	    PreparedStatement pstat = null;
	    ResultSet rs = null;
	    try {
	        String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LOOKUP");
	
	        pstat = conn.prepareStatement(sql);
	
	        pstat.setInt(1, runId);
	
	        rs = pstat.executeQuery();
	        if (!rs.first())
	            throw new Exception("No such test run: " + runId);
	
	        HaTestRun testRun = new HaTestRun();
	        testRun.setRunId(runId);
	        testRun.setRunTime(rs.getTimestamp("run_time").getTime());
	        testRun.setSessionNumber(conn, rs.getInt("run_session"));
	        testRun.setAnsweredCorrect((rs.getInt("answered_correct")));
	        testRun.setPassing(rs.getInt("is_passing")==0?false:true);
	
	        testRun.setHaTest(HaTestDao.loadTest(conn,rs.getInt("test_id")));
	        do {
	            String pid = rs.getString("pid");
	            if (pid == null)
	                continue;
	
	            HaTestRunResult result = new HaTestRunResult();
	            result.setPid(pid);
	            result.setResult(rs.getString("answer_status"));
	            result.setResultId(rs.getInt("rid"));
	            result.setResponseIndex(rs.getInt("answer_index"));
	
	            testRun.results.add(result);
	        } while (rs.next());
	        return testRun;
	    } catch (HotMathException hme) {
	        throw hme;
	    } catch (Exception e) {
	        throw new HotMathException(e, "Error adding run result: " + e.getMessage());
	    } finally {
	        SqlUtilities.releaseResources(rs, pstat, null);
	    }
	}


	/** Return all test runs created against test_id
	 * 
	 * @param conn
	 * @param testId
	 * @return
	 * @throws Exception
	 */
	public List<HaTestRun> lookupTestRunsForTest(final Connection conn, int testId) throws Exception {
	    
	    PreparedStatement pstat = null;
	    ResultSet rs = null;
	    
	    List<HaTestRun> runs = new ArrayList<HaTestRun>();
	    try {
	        String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_LOOK_FOR_TESTS");
	
	        pstat = conn.prepareStatement(sql);
	        pstat.setInt(1, testId);
	        rs = pstat.executeQuery();
	        while(rs.next()) {
	            runs.add(lookupTestRun(conn, rs.getInt("run_id")));
	        }
	        
	        return runs;
	    } finally {
	        SqlUtilities.releaseResources(rs, pstat, null);
	    }        
	}

    /** Remove any current results for the specified test run
     * 
     * @param conn
     * @param runId
     * @throws Exception
     */
    public void removeAllQuizResponses(final Connection conn, int runId) throws Exception {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute("delete from HA_TEST_RUN_RESULTS where run_id = " + runId);
        } finally {
            SqlUtilities.releaseResources(null, stmt,null);
        }
    }

    public HaTestRunResult addRunResult(final Connection conn, int runId, String pid, String answerStatus, int answerIndex) throws HotMathException {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_RESULT_INSERT");
            pstat = conn.prepareStatement(sql);
            HaTestRunResult testRunResult = new HaTestRunResult();

            pstat.setInt(1, runId);
            pstat.setString(2, pid);
            pstat.setString(3, answerStatus);
            pstat.setInt(4, answerIndex);

            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new HotMathException("Could not create new test run result for: " + runId);

            int autoIncKeyFromApi = -1;

            rs = pstat.getGeneratedKeys();
            if (rs.next()) {
                autoIncKeyFromApi = rs.getInt(1);
            } else {
                throw new HotMathException("Error creating PK for test");
            }

            testRunResult.setResultId(autoIncKeyFromApi);
            testRunResult.setResult(answerStatus);
            testRunResult.setPid(pid);

            return testRunResult;
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error adding run result: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }
    
    public void setSessionNumber(final Connection conn, int runId, int sn) throws Exception {
        PreparedStatement pstat = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("TEST_RUN_SET_SESSION_NUMBER");
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, sn);
            pstat.setInt(2, runId);
            int cnt = pstat.executeUpdate();
            if (cnt != 1)
                throw new HotMathException("Could not update session_number for run_id = " + runId);
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }

    private boolean sessionsMatch(final Connection conn, HaTestRun testRun, List<AssessmentPrescriptionSession> sessions) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean match = true;
       
        try {

    		// load sessions into Map
    		Map<String, Boolean> foundMap = new HashMap<String, Boolean>();
    		for (AssessmentPrescriptionSession s : sessions) {
    			foundMap.put(s.getTopic().trim() + s.getSessionCategories().get(0).getFile().trim(), false);
    		}
        	
        	ps = conn.prepareStatement("select run_id, lesson_name, lesson_number, lesson_file from HA_TEST_RUN_LESSON where run_id = ?");
        	ps.setInt(1, testRun.getRunId());
        	rs = ps.executeQuery();

        	if (rs.first()) {
        		rs.beforeFirst();

        		// compare sessions with HA_TEST_RUN_LESSON
        		while(rs.next()) {
        			String lessonName = (rs.getString(2) != null) ? rs.getString(2).trim() : "";
                    String file = (rs.getString(4) != null) ? rs.getString(4).trim() : "";

                    if (foundMap.containsKey(lessonName+file))
                    	foundMap.put(lessonName+file, true);
        			else {
        				match = false;
        				break;
        			}
        		}
        	}

    		for (Boolean val : foundMap.values()) {
    			if (val == false || match == false) {
    				match = false;
    				break;
    			}
    		}

        	return match;
        }
        finally {
        	SqlUtilities.releaseResources(rs, ps, null);
        }
    }
}
