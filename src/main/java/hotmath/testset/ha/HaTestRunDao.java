package hotmath.testset.ha;

import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class HaTestRunDao {
    
    public void addLessonsToTestRun(final Connection conn, HaTestRun testRun, List<AssessmentPrescriptionSession> sessions) throws Exception  {
        
        PreparedStatement pstat=null;
        try {
            
            conn.createStatement().executeUpdate("delete from HA_TEST_RUN_LESSON where run_id = " + testRun.getRunId());
            
            String sql = "insert into HA_TEST_RUN_LESSON(run_id, lesson_name, lesson_file) values(?, ?, ?)";
            pstat = conn.prepareStatement(sql);
            for(AssessmentPrescriptionSession s: sessions) {
                pstat.setInt(1, testRun.getRunId());
                pstat.setString(2, s.getTopic());
                pstat.setString(3, s.getSessionCategories().get(0).getFile());
                
                // write out each pid lesson references
                PreparedStatement pstat2=null;
                try {
                    pstat2 = conn.prepareStatement("insert into HA_TEST_RUN_LESSON_PID(pid, run_id)values(?,?)");
                    
                    for(SessionData sd: s.getSessionItems()) {
                        pstat2.setString(1, sd.getPid());
                        pstat2.setInt(2, testRun.getRunId());
                        
                        if( pstat2.executeUpdate() != 1)
                            throw new Exception("Could not add to HA_TEST_RUN_LESSON_PID for unknown reasons");
                    }
                }
                finally {
                    SqlUtilities.releaseResources(null, pstat2,null);
                }
                
                if(pstat.executeUpdate() != 1)
                    throw new Exception("Could not save record for unknown reason: " + testRun);
            }
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
        }
    }
    
    
    public List<TestRunLessonModel>  getTestRunLessons(final Connection conn, Integer runId) throws Exception {
        
        List<TestRunLessonModel> lessons = new ArrayList<TestRunLessonModel>();
        PreparedStatement pstat=null;
        try {
            String sql = "select * from HA_TEST_RUN_LESSON where run_id = ? order by id";
            pstat = conn.prepareStatement(sql);
            pstat.setInt(1, runId);
            
            ResultSet rs = pstat.executeQuery();
            while(rs.next()) {
                
                TestRunLessonModel im = new TestRunLessonModel(rs.getString("lesson_name"),rs.getString("lesson_file"));
                lessons.add(im);
            }
            
            return lessons;
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
        }        
    }
    
    
}
