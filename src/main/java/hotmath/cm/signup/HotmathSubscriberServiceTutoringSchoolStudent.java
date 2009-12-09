package hotmath.cm.signup;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.subscriber.HotMathSubscriber;
import hotmath.subscriber.PurchasePlan;
import hotmath.subscriber.service.HotMathSubscriberService;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class HotmathSubscriberServiceTutoringSchoolStudent extends HotMathSubscriberService {
    
    static Logger __logger = Logger.getLogger(HotmathSubscriberServiceTutoringSchoolStudent.class);
    
    /**
     * When installing tutoring, we must make sure there is a record in
     * LWL_TUTORING that provides the mapping between LWL and HM.
     * 
     * If this record already exists, then simply add minutes to the existing
     * account
     * 
     */
    public void installService(HotMathSubscriber sub, PurchasePlan plan) {
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            conn = HMConnectionPool.getConnection();


            Integer schoolId=0;
            
            // get the schoolId associated with this student
            PreparedStatement stmt = null;
            try {
                stmt = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("LWL_STUDENT_SCHOOL_ID"));
                stmt.setInt(1, Integer.parseInt(sub.getId()));
                ResultSet rs = stmt.executeQuery();
                if(!rs.first())
                    __logger.error("No school_id for user: " + sub.getId());
                
                schoolId = rs.getInt("school_id");
            }
            finally {
                stmt.close();
            }
            

            
            __logger.info("LWL: Registering CM student " + sub.getId() + " of school " + schoolId + " into LWL_TUTORING" );
            

            String sql = "insert into LWL_TUTORING(subscriber_id, available_minutes,status,create_date,school_id,account_type)values(?,?,?,?,?,1)";
            ps = conn.prepareStatement(sql);

            ps.setString(1, sub.getId());
            ps.setInt(2, 0);
            ps.setString(3, "A");
            ps.setDate(4, new Date(System.currentTimeMillis()));
            ps.setInt(5, schoolId);

            try {
                ps.executeUpdate();
            } catch (SQLException sqe) {
                __logger.info("Attempt to add LWL registration failed", sqe);
            }
        } catch (Exception e) {
            __logger.info("Error installing Tutoring service", e);
        } finally {
            SqlUtilities.releaseResources(null, ps, conn);
        }
    }

    /**  do not show the minutes */
    public String getStatusMessage() {
        return "Enabled";
    }    
}
