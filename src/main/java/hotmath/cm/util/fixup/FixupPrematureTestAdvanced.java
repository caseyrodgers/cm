package hotmath.cm.util.fixup;

import hotmath.gwt.shared.server.service.command.GetPrescriptionCommand;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class FixupPrematureTestAdvanced {
    
    
    private Connection conn;

    public FixupPrematureTestAdvanced(Connection conn) {
        this.conn = conn;
    }
    

    private void findTestErrorsFor(int adminId, boolean doUpdate) throws Exception {
        
        System.out.println("Checking admin: " + adminId);
        ResultSet rs = conn.prepareStatement("select * from HA_USER where admin_id = " + adminId + " and is_active = 1").executeQuery();
        while(rs.next()) {
            int uid = rs.getInt("uid");
            int progId = rs.getInt("user_prog_id");
            lookForErrorsInUser(uid, progId, doUpdate);
        }
        
        System.out.println("Admin check complete\n");
    }

    private void lookForErrorsInUser(int uid, int progId, boolean doUpdate) {
        try {
            // System.out.println("UID: " + uid + ", " + progId);
            
            ResultSet rs = conn.createStatement().executeQuery("select tr.*, t.test_segment, t.test_question_count from HA_TEST_RUN tr join HA_TEST t on t.test_id = tr.test_id where user_prog_id = " + progId);
            
            int lastSegment=-1;
            int testId=-1;
            int lastRunId = -1;
            int answered_correct = 0;
            int answered_incorrect = 0;
            int test_question_count = 0;
            int unanswered = 0;
            
            List<Integer[]> list = new ArrayList<Integer[]>();
            
            
            SimpleDateFormat format = new SimpleDateFormat("yyyMMdd");
            
            while(rs.next()) {
                testId = rs.getInt("test_id");
                lastRunId = rs.getInt("run_id");
                int segment = rs.getInt("test_segment");
                
                answered_correct = rs.getInt("answered_correct");
                answered_incorrect = rs.getInt("answered_incorrect");
                unanswered = rs.getInt("not_answered");
                test_question_count = rs.getInt("test_question_count");
                
                Integer vals[] = {answered_correct, answered_correct, unanswered, testId, lastRunId, segment, test_question_count, Integer.parseInt(format.format(rs.getDate("run_time")))};
                list.add(vals);
                
                
                lastSegment = segment;
            }
            
            /** If the previous to last test run has
             *  an 'lower-than-expected' percent then 
             *  indentify problem test and reset previous
             */
            if(lastRunId > -1 && lastSegment > 1 ) {
                // && percent > 0 && percent < 60
                
                if(list.size() > 1) {
                    Integer valsLast[] = list.get(list.size()-1);
                    Integer vals[] = list.get(list.size()-2); // next to last
                    int corr = vals[0];
                    int incor = vals[1];
                    int una = vals[2];
                    int cntProbs = vals[6];
                    int date = vals[7];
                    
                    /** if moved forward
                     * 
                     */
                    int lastSegNum = valsLast[5];
                    int thisSegNum = vals[5];
                    if(lastSegNum > thisSegNum) {
                        int percent = GetPrescriptionCommand.getTestPassPercent(cntProbs, corr);
                        
                        if(percent > 0 && percent < 60)  {  /* if not 'admin moved/debug' and less than can be set */
                            int thisTestId = vals[3];
                            int thisRunId = vals[4];
                            int segToReturnTo = vals[5];
                            
                            System.out.println("Error Found -> " + " uid: " + uid + ", date: " + date + ", test_id: " + thisTestId + ", test_run: " + thisRunId + ", percent: " + percent + ", Segment To Return: " + segToReturnTo);
                            
                            if(doUpdate) {
                                fixIt(uid, segToReturnTo);
                            }
                        }
                    }
                }
            }
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void fixIt(int uid, int segToReturnTo) throws Exception {
        try {
            String sql = "update HA_USER set active_run_id = 0, active_test_id = 0, active_segment = " + segToReturnTo + " where uid = " + uid;
            int res = conn.createStatement().executeUpdate(sql);
            if(res != 1) {
                System.out.println("Update was not successful");
            }
            
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void lookForErrorsInTest(int testId) {
        System.out.println("Test: " + testId);
    }
    


    static public void main(String as[]) {
        Connection conn = null;
        
        int adminIdToCheck=0;
        if(as.length > 0) {
            adminIdToCheck = Integer.parseInt(as[0]);
        }
        try {
            conn = HMConnectionPool.getConnection();
            
            FixupPrematureTestAdvanced fte = new FixupPrematureTestAdvanced(conn);
            
            if(adminIdToCheck > 0) {
                fte.findTestErrorsFor(adminIdToCheck, false);
            }
            else {
                ResultSet rs = conn.createStatement().executeQuery("select aid from HA_ADMIN ORDER BY aid desc");
                while(rs.next()) {
                    int adminId = rs.getInt("aid");
                    fte.findTestErrorsFor(adminId, false);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            SqlUtilities.releaseResources(null,  null,  conn);
        }
        
        System.out.println("Test complete");
        System.exit(0);
    }


}
