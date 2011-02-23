package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;

/** Identify skipped quizzes by looking a meta data
 * 
 * @author casey
 *
 */
public class FindBug_SkippedQuizzes {
    
    

    final static Logger __logger = Logger.getLogger(FindBug_SkippedQuizzes.class); 
    
    public FindBug_SkippedQuizzes() throws Exception {
    }
    
    public void findSkippedQuizzes(final Connection conn, int adminId) throws Exception {
        
        String sqlAids = "select aid from HA_ADMIN";
        if(adminId > 0) {
            sqlAids += " WHERE aid = " + adminId;
        }
        sqlAids += " order by aid desc";
        ResultSet rsAids = conn.createStatement().executeQuery(sqlAids);
        while(rsAids.next()) {
            int aid = rsAids.getInt("aid");

            testAdminId(conn, aid);
        }
    }

    private void testAdminId(final Connection conn, int adminId) throws Exception {
        __logger.info("Testing admin_id: " + adminId);
        
        PreparedStatement ps=null;
        try {
            String sql = " select u.uid, p.id as program_id,date(t.create_time) as create_time,t.test_id,t.test_segment,r.run_id " +
                      " from  HA_USER u " +
                      " join HA_TEST t on t.user_id = u.uid " +
                      " join CM_USER_PROGRAM p on p.id = t.user_prog_id " +
                      " left join HA_TEST_RUN r on r.test_id = t.test_id " +
                      " where u.admin_id = ? " +
                      " order by u.uid, p.id,t.test_id,t.test_segment ";
            
            ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);

            List<QuizInfo> quizInfos = new ArrayList<QuizInfo>();
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                int userId = rs.getInt("uid");
                int progId = rs.getInt("program_id");
                int testId = rs.getInt("test_id");
                int testSegment = rs.getInt("test_segment");
                int runId = rs.getInt("run_id");
            
                quizInfos.add(new QuizInfo(adminId,userId, progId, testId, testSegment, runId, rs.getDate("create_time")));
            }
            performChecks(quizInfos);
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }
    
    private void performChecks(List<QuizInfo> qs) throws Exception {
        try {
            for(int qi=0;qi<qs.size()-1;qi++) {
                QuizInfo q1 = qs.get(qi);
                
                /** only check quizzes that have been taken */
                if(q1.getRunId() > 0) {
                    
                    /** skip resets
                     * 
                     */
                    if(q1.getTestSegment() < 2) {
                        continue;
                    }
                    
                    /** if we find any of the following this record 
                     *  make sense:
                     *  
                     * 0. a unrun test
                     * 1. a different user, program
                     * 2. an equal or greater test segment
                     */
                    
                    for(int qi2=qi+1;qi2<qs.size();qi2++) {
                        QuizInfo q2 = qs.get(qi2);
                        if(q2.getRunId() == 0) {
                            /** unrun test ...
                             * 
                             */
                        }
                        else if(q2.getUserId() != q1.getUserId()) {
                            /** different user
                             * 
                             */
                            break;
                        }
                        else if(q2.getProgId() != q1.getProgId()) {
                            /** different program
                             * 
                             */
                            break;
                        }
                        else {
                            /** segment must be either equal or one more than q1
                             * 
                             */
                            int distance = (q2.getTestSegment() - q1.getTestSegment());
                            if(distance > 1) {
                                __logger.warn("Found Skipped Quiz: " + q1);
                            }
                            else {
                                
                                if(q2.getTestId() == q1.getTestId()) {
                                    __logger.warn("Found Duplicated Quiz: " + q1);
                                }
                                break;  // no error
                            }
                        }
                    }
                }
            }
        }
        catch(Exception e) {
            __logger.error("Error performing tests", e);
            throw e;
        }
    }
    
    class QuizInfo {
        int adminId;
        int userId;
        int progId;
        int testId;
        int testSegment;
        int runId;
        Date date;
        
        public QuizInfo(int adminId, int userId, int progId, int testId, int testSegment, int runId, Date date) {
            this.adminId = adminId;
            this.userId = userId;
            this.progId = progId;
            this.testId = testId;
            this.testSegment = testSegment;
            this.runId = runId;
            this.date = date;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getProgId() {
            return progId;
        }

        public void setProgId(int progId) {
            this.progId = progId;
        }

        public int getTestId() {
            return testId;
        }

        public void setTestId(int testId) {
            this.testId = testId;
        }

        public int getTestSegment() {
            return testSegment;
        }

        public void setTestSegment(int testSegment) {
            this.testSegment = testSegment;
        }

        public int getRunId() {
            return runId;
        }

        public void setRunId(int runId) {
            this.runId = runId;
        }

        public int getAdminId() {
            return adminId;
        }

        public void setAdminId(int adminId) {
            this.adminId = adminId;
        }

        @Override
        public String toString() {
            return "QuizInfo [adminId=" + adminId + ", userId=" + userId + ", progId=" + progId + ", testId=" + testId
                    + ", testSegment=" + testSegment + ", runId=" + runId + ", date=" + date + "]";
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }
        
    }
    
    static public void main(String as[]) {
        
        SbUtilities.addOptions(as);
        int adminId = SbUtilities.getInt(SbUtilities.getOption(null, "aid"));
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            new FindBug_SkippedQuizzes().findSkippedQuizzes(conn, adminId);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null,null,conn);
            
            System.exit(0);
        }
    }

}
