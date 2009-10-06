package hotmath.testset.ha;

import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

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
            
            String sql = "update HA_TEST_RUN_LESSON set lesson_viewed = now() where run_id = ? and lesson_number = ?";
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
    
    
    /** Return all lessons assignged to this run
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
    
    
    /** Mark this lesson as being viewed.
     * 
     *  set the date completion, but only set
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
            String sql = "update HA_TEST_RUN_LESSON SET date_completed = now() where run_id = ? and lesson_name = ? and date_completed is null";
            pstat = conn.prepareStatement(sql);
            
            pstat.setInt(1, runId);
            pstat.setString(2,lesson);
            
            pstat.executeUpdate();
        }
        finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }
    
}
