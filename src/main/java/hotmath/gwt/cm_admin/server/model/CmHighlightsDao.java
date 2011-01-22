package hotmath.gwt.cm_admin.server.model;

import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;



public class CmHighlightsDao {

    
    /** 
     * 
     * Greatest Effort from <start date> to <end date>
     *  Ordered listing showing number of lesson topics completed   
     *  Smith, Suzie    107
     *  
     *  
     * @param conn
     * @param uids
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public CmList<HighlightReportData> getReportGreatestEffort(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = 
            " select u.uid, u.user_name, count(*) as lessons_completed " +
            "from HA_USER u " +
            "JOIN HA_TEST t on t.user_id = u.uid " +
            "JOIN HA_TEST_RUN r on r.test_id = t.test_id " +
            "JOIN HA_TEST_RUN_LESSON l on l.run_id = r.run_id " +
            "where u.uid in ( " + createInList(uids) + " ) " +
            "AND l.date_completed is not null " +
            "group by u.uid " +
            "order by lessons_completed desc, u.user_name";
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("lessons_completed")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        
        
        return list;
    }

    /** NOTE: same SQL as Greatest, just differnt order
     * 
     * TODO: share data set with greatest effort report
     * 
     * @param conn
     * @param uids
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public CmList<HighlightReportData> getReportLeastEffort(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = 
            " select u.uid, u.user_name, count(*) as lessons_completed " +
            "from HA_USER u " +
            "JOIN HA_TEST t on t.user_id = u.uid " +
            "JOIN HA_TEST_RUN r on r.test_id = t.test_id " +
            "JOIN HA_TEST_RUN_LESSON l on l.run_id = r.run_id " +
            "where u.uid in ( " + createInList(uids) + " ) " +
            "AND l.date_completed is not null " +
            "group by u.uid " +
            "order by lessons_completed, u.user_name";
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("lessons_completed")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    public CmList<HighlightReportData> getReportMostGames(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = 
            "select u.uid, " +
            "u.user_name, " +
            "count(*) as games_viewed " +
            " from   HA_USER u " +
            " join HA_TEST t " +
            " on t.user_id = u.uid " +
            " join HA_TEST_RUN r " + 
            " on r.test_id = t.test_id " +
            " join HA_TEST_RUN_INMH_USE i " +
            " on i.run_id = r.run_id " +
            " where  u.uid in (" + createInList(uids) + ") " +
            " and i.item_type = 'activity_standard' " +
            " group  by u.uid " +
            " order  by games_viewed desc, u.user_name"; 
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("games_viewed")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    public CmList<HighlightReportData> getReportQuizzesPassed(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = 
            "select u.uid, " +
            " u.user_name, " +
            "count(*) as quizzes_passed " +
            " from   HA_USER u " +
            " join HA_TEST t " +
            " on t.user_id = u.uid " +
            " join HA_TEST_RUN r " +
            " on r.test_id = t.test_id " +
            " where  u.uid in (" + createInList(uids) + ") " +
            " and r.is_passing = 1 " +
            " group  by u.uid " +
            " order  by quizzes_passed desc, u.user_name"; 
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("quizzes_passed")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    public CmList<HighlightReportData> getReportAvgQuizScores(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = 
            "select u.uid, u.user_name, " +
            "floor(avg((answered_correct / (answered_correct + answered_incorrect + not_answered)) * 100)) as avg_quiz_score " +
            " from   HA_USER u " +
            " join HA_TEST t " +
            " on t.user_id = u.uid "+
            " join HA_TEST_RUN r " +
            " on r.test_id = t.test_id " +
            " where  u.uid in ( " + createInList(uids) + " ) " +
            " group by u.uid " +
            " order by avg_quiz_score desc, u.user_name";

        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("avg_quiz_score")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    } 
    
    public CmList<HighlightReportData> getReportFailedQuizzes(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = 
            "select  u.uid,  u.user_name, count(*) as failed_quizzes " +
            "FROM HA_USER u " +
            " join HA_TEST t " +
            " on t.user_id = u.uid " +
            " join HA_TEST_RUN r " +
            " on r.test_id = t.test_id " +
            " where  u.uid in ( " + createInList(uids) + " ) " +
            " and r.is_passing = 0 " +
            " group by u.uid " +
            " order by failed_quizzes desc";
     
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("failed_quizzes")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    } 
    
    
    /** 
     * 
     * 
     * 
     * Looking for repeated quiz failures of most recent quiz for each student, 
     * (E.g., Johnny in Pre-alg, failed section one quiz 2 times; Suzie failed Alg 1 Section3 quiz 9 times ...)
     * 
     * 
     * I guess if Mathilda failed 22 times but then finally passed it, 
     * she would not be listed, so it only applies to students whose last quiz activity was a failure ....
     * 
     * @param conn
     * @param uids
     * @param from
     * @param to
     * @return
     * @throws Exception
     */

    public CmList<HighlightReportData> getReportFailedCurrentQuizzes(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = "select  u.uid, u.user_name, count(r.run_id) as failed_quizzes " +
        "from HA_USER u " +
        "   JOIN HA_TEST t on t.user_id = u.uid " +
        "  JOIN  CM_USER_PROGRAM c1  " +
        "       on c1.id = t.user_prog_id " +
        "   JOIN (select user_id, max(id) from CM_USER_PROGRAM group by user_id) c2  " +
        "       on c2.user_id = u.uid      " +
        "   JOIN HA_TEST_RUN r  " +
        "       on r.test_id = t.test_id " +
        "where t.user_id in(" + createInList(uids) + " ) " +
        "and t.test_segment = u.active_segment " +
        "and r.is_passing = 0 " +
        "group by u.uid " +
        "order by failed_quizzes desc, user_name";
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("failed_quizzes")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }

    
    public CmList<HighlightReportData> getReportComparePerformance(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        String sql = 
            " SELECT * FROM v_HA_USER_HIGHLIGHTS where uid in (" + createInList(uids) + ")";

        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String values = rs.getInt("group_val") + "|" + rs.getInt("school_val") + "|" + rs.getInt("db_val");
                list.add(new HighlightReportData(0, rs.getString("stat_label"), values));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        
        
        return list;
    }    
        
    
    
    private String createInList(List<String> uids) {
        String inList = "";
        for(String uid: uids) {
            if(inList.length() > 0 )
                inList += ", ";
            inList += uid;
        }
        return inList;
    }
}

