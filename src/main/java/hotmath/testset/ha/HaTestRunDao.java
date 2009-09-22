package hotmath.testset.ha;

import hotmath.assessment.AssessmentPrescriptionSession;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
                
                if(pstat.executeUpdate() != 1)
                    throw new Exception("Could not save record for unknown reason: " + testRun);
            }
        }
        finally {
            SqlUtilities.releaseResources(null,pstat,null);
        }
    }
}
