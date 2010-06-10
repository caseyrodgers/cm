package hotmath.testset.ha.report;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DataMining1 {
    
    Connection conn;
    List<MinedData> data = new ArrayList<MinedData>();
    public DataMining1(final Connection conn) throws Exception {
        this.conn = conn;
        
        String listOfUserSql2 = 
            "select  u.admin_id,sub.password, u.user_name,u.uid,ud.first_login,r.run_time as pass_date," +
            "datediff(r.run_time, first_login) as days_from_login, 0 as num_segments_to_pass,0 as num_segments_to_pass2," +
            "t.test_id,t.test_segment, r.run_id,p.id as user_prog_id,d.test_def_id,d.test_name,pd.title" +
            " from   HA_USER u " +
            "  JOIN HA_TEST t on t.user_id = u.uid" +
            "  JOIN HA_TEST_RUN r on r.test_id = t.test_id" +
            "  JOIN CM_USER_PROGRAM p on p.id = t.user_prog_id" +
            "  JOIN HA_TEST_DEF d on d.test_def_id = p.test_def_id" +
            "  JOIN HA_PROG_DEF pd on pd.id = d.prog_id" +
            "  JOIN HA_ADMIN a on a.aid = u.admin_id" +
            "  JOIN SUBSCRIBERS sub on sub.id = a.subscriber_id" +
            "  LEFT JOIN (" +
            "      SELECT user_id,  min(date(login_time)) first_login" +
            "      from   HA_USER_LOGIN" +
            "      GROUP by user_id" +
            "  ) ud on ud.user_id = u.uid" +
            " where r.is_passing = 1  " +
            " and    ( pd.id = 'Prof'  or pd.id like '%Prep%') " +
            " and u.admin_id = 2";
        
        String listOfUserSql = "select * from JUNK_DM";
        
        /** for every user */
        ResultSet rs = conn.createStatement().executeQuery(listOfUserSql);
        while(rs.next()) {
            /** get all tests created by this user */
            analyzePassedQuiz(rs.getInt("uid"),rs.getDate("first_login"),rs.getInt("days_to_pass"), rs.getDate("pass_date"),rs.getInt("test_id"),rs.getInt("test_segment"),rs.getInt("run_id"));
        }
    }

    
    /** 
     * 1)      For every CM student that has passed section 2 (any segment)
          -- for every student   
      of any proficiency program or grad prep program, 

       what is the average number of days and average number of sessions counting from first login to the date of passing?
       In each case, how many such students were counted?

     2)      Same for sections 3, 4, 5, and 6.

     * @param uid
     * @param firstLogin
     * @param passDate
     * @param testId
     * @param runId
     * @throws Exception
     */
    private void analyzePassedQuiz(int uid, Date firstLogin, int daysFromLogin, Date passDate, int testId, int testSegment, int runId) throws Exception {
        try {
            MinedData mdata = new MinedData(uid, testId, runId);
            data.add(mdata);
            
            /** days from first login */
            mdata.daysFromFirstLogin = daysFromLogin;
            
                /** num sessions from first login */
            String sql = 
                "select count(distinct lesson_name) " +
                "from HA_TEST_RUN_LESSON l " +
                " JOIN HA_TEST_RUN r on r.run_id = l.run_id " +
                " JOIN HA_TEST t on t.test_id = r.test_id " + 
                " where t.user_id = " + uid + 
                " and t.test_segment <= " + testSegment; 
                
                ResultSet rs = conn.createStatement().executeQuery(sql);
                rs.first();
                mdata.numSessionsFromPreviousSegment = rs.getInt(1);
                
                /** num logins from pass */
                sql = 
                    "select count(*) " +
                    "from HA_USER_LOGIN l " +
                    " where user_id = " + uid + 
                    " and login_time < ? ";
                    
                PreparedStatement ps = null;
                try {
                    ps = conn.prepareStatement(sql);
                    ps.setDate(1, passDate);
                    rs = ps.executeQuery();
                    rs.first();
                    mdata.loginsToPass = rs.getInt(1);                
                }
                finally {
                    SqlUtilities.releaseResources(null,ps,null);
                }
                
           conn.createStatement().executeUpdate("update JUNK_DM set lessons_to_pass = " + mdata.numSessionsFromPreviousSegment + 
                   ",logins_to_pass = " + mdata.loginsToPass + 
                   "  where run_id = " + runId);
        }
        catch(Exception e) {
            throw e;
        }
    }

    

    static public void main(String as[]) {
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();
          new DataMining1(conn);  
          
          System.out.println("Data Mining Complete!");
          System.exit(0);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
          SqlUtilities.releaseResources(null,null,conn);   
        }
    }    
}


class MinedData {
    int uid;
    int testId;
    int runId;
    int daysFromFirstLogin;
    int daysFromPreviousSegment;
    int numSegmentsFromPreviousSegment;
    int numSessionsFromPreviousSegment;
    int numSessionsFromFirstLogin;
    int loginsToPass;
    
    public MinedData(int uid, int testId, int runId) {
        this.uid = uid;
        this.testId = testId;
        this.runId = runId;
    }
}
