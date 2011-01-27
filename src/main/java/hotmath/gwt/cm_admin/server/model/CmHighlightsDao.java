package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_admin.server.model.highlight.CmHighLightManager;
import hotmath.gwt.cm_admin.server.model.highlight.CmHighLightManager.HighLightStat;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
            " select u.uid, u.user_name, count(*) as lessons_viewed " +
            "from HA_USER u " +
            "JOIN HA_TEST t on t.user_id = u.uid " +
            "JOIN HA_TEST_RUN r on r.test_id = t.test_id " +
            "JOIN HA_TEST_RUN_LESSON l on l.run_id = r.run_id " +
            "where u.uid in ( " + createInList(uids) + " ) " +
            " AND date(r.run_time) between ? and ? " +
            "AND l.lesson_viewed is not null " +
            "group by u.uid " +
            "order by lessons_viewed desc, u.user_name";
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("lessons_viewed")));
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
            " AND date(r.run_time) between ? and ? " +
            "AND l.lesson_viewed is not null " +            
            "group by u.uid " +
            "order by lessons_completed, u.user_name";
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
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
            " and date(i.view_time) between ? and ? " +
            " group  by u.uid " +
            " order  by games_viewed desc, u.user_name"; 
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));

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
            " join CM_USER_PROGRAM c on c.id = t.user_prog_id " +
            " join HA_TEST_RUN r " +
            " on r.test_id = t.test_id " +
            " where  u.uid in (" + createInList(uids) + ") " +
            " and r.is_passing = 1 " +
            " and c.test_def_id <> 15 " +
            " and date(r.run_time) between ? and ? " +
            " group  by u.uid " +
            " order  by quizzes_passed desc, u.user_name"; 
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
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
        
        /** Make sure to not include Auto Enroll (15)
         * 
         */
        String sql = 
            "select u.uid, u.user_name, " +
            "floor(avg((answered_correct / (answered_correct + answered_incorrect + not_answered)) * 100)) as avg_quiz_score " +
            " from   HA_USER u " +
            " join HA_TEST t " +
            " on t.user_id = u.uid "+
            " join CM_USER_PROGRAM c " +
            " on c.id = t.user_prog_id " +
            " join HA_TEST_RUN r " +
            " on r.test_id = t.test_id " +
            " where  u.uid in ( " + createInList(uids) + " ) " +
            " and c.test_def_id <> 15 " +
            " and date(r.run_time) between ? and ? " +
            " group by u.uid " +
            " order by avg_quiz_score desc, u.user_name";

        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            
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
            " join CM_USER_PROGRAM c on c.id = t.user_prog_id " +
            " join HA_TEST_RUN r " +
            " on r.test_id = t.test_id " +
            " where  u.uid in ( " + createInList(uids) + " ) " +
            " and r.is_passing = 0 " +
            " and c.test_def_id <> 15 " +
            " and date(r.run_time) between ? and ? " +
            " group by u.uid " +
            " order by failed_quizzes desc";
     
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));

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
        " and date(r.run_time) between ? and ? " +
        "group by u.uid " +
        "order by failed_quizzes desc, user_name";
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            
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
    
    
    public CmList<HighlightReportData> getReportLoginsPerWeek(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql =
            
            "select u.uid, u.user_name,  floor( (count(*)  /  7) ) as avg_login_week " +
            " from   HA_USER_LOGIN l " +
            "   JOIN HA_USER u ON u.uid = l.user_id " +
            " where  u.uid in(" + createInList(uids) + " ) " +
            " and date(l.login_time) between ? and ? " +
            " group by u.uid " +
            " order by avg_login_week desc, u.user_name";
            
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setDate(1, new java.sql.Date(from.getTime()));
            ps.setDate(2, new java.sql.Date(to.getTime()));
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("avg_login_week")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    
    public CmList<HighlightReportData> getReportGroupProgress(final Connection conn, final int adminId,final List<String> uids, final Date from, final Date to) throws Exception {

        Map<String,String> tokenMap = new HashMap<String,String>()
        {
            {
                put("UIDLIST",createInList(uids));
                put("DATE_FROM",formatDate(from));
                put("DATE_TO",formatDate(to));
                put("AID", Integer.toString(adminId));
            }
        };
        
        
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_HIGHLIGHT_REPORT_GROUP_PROGRESS", tokenMap);
            
            
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        Statement ps=null;
        try {
            ps = conn.createStatement();
            ResultSet rs = ps.executeQuery(sql);
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getString("group_name"),rs.getInt("active_count"),rs.getInt("login_count"),rs.getInt("lessons_viewed"),rs.getInt("quizzes_passed"),rs.getInt("school_quizzes_passed")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }

    
    SimpleDateFormat _format = new SimpleDateFormat("yyyy-MM-dd");
    private String formatDate(Date date) {
        return _format.format(date);
    }
    
    
    /** Kludged ...
     * 
     * Here we are returning three data values delimited by a |.  The GXT model 
     * looks for this a parses into data1, data2, etc..
     * 
     * The view v_HA_USER_HIGHLIGHTS tries to encapsulate the query 
     * and allow for its maintenance without a build.
     * 
     * 
     * 
     * @param conn
     * @param uids
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public CmList<HighlightReportData> getReportComparePerformance(final Connection conn, int adminId, List<String> uids, Date from, Date to) throws Exception {
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        
        for(HighLightStat stat: new CmHighLightManager(conn).getStats()) {
            list.addAll(stat.getHighLightData(conn, from, to, adminId, uids));
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

