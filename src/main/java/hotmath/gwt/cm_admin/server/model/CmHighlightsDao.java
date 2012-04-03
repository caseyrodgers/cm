package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.StudentActivityDao.ActivityTime;
import hotmath.gwt.cm_admin.server.model.StudentActivityDao.ActivityTypeEnum;
import hotmath.gwt.cm_admin.server.model.highlight.CmHighLightManager;
import hotmath.gwt.cm_admin.server.model.highlight.CmHighLightManager.HighLightStat;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;


public class CmHighlightsDao extends SimpleJdbcDaoSupport{


    final static Logger __logger = Logger.getLogger(CmHighlightsDao.class);
    
    static private CmHighlightsDao __instance;
    static public CmHighlightsDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (CmHighlightsDao)SpringManager.getInstance().getBeanFactory().getBean("cmHighlightsDao");
        }
        return __instance;
    }
    
    
    private CmHighlightsDao() {/** empty */}
    
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
    public List<HighlightReportData> getReportGreatestEffort(List<String> uids, Date from, Date to) throws Exception {
        String[] vals = QueryHelper.getDateTimeRange(from, to);
        List<HighlightReportData> list = getJdbcTemplate().query(
                CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_GREATEST_EFFORT",createInListMap(createInList(uids)) ),
                new Object[]{vals[0], vals[1]},
                new RowMapper<HighlightReportData>() {
                    @Override
                    public HighlightReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("lessons_viewed"));
                    }
                });
        
        return list;
    }

    /** NOTE: same SQL as Greatest, just different order
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
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_LEAST_EFFORT",createInListMap(createInList(uids)) );

        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            String[] vals = QueryHelper.getDateTimeRange(from, to);
            ps.setString(1, vals[0]);
            ps.setString(2, vals[1]);
            
            __logger.info("report sql: " + ps);
            
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
    
    public CmList<HighlightReportData> getReportMostGames(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_MOST_GAMES_VIEWED",createInListMap(createInList(uids)) );
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            String[] vals = QueryHelper.getDateTimeRange(from, to);
            ps.setString(1, vals[0]);
            ps.setString(2, vals[1]);
            ps.setString(3, vals[0]);
            ps.setString(4, vals[1]);

            __logger.info("report sql: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("games_viewed"), rs.getInt("quizzes_taken")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    public CmList<HighlightReportData> getReportQuizzesPassed(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_QUIZZES_PASSED",createInListMap(createInList(uids)) );
        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            String[] vals = QueryHelper.getDateTimeRange(from, to);
            ps.setString(1, vals[0]);
            ps.setString(2, vals[1]);
            ps.setString(3, vals[0]);
            ps.setString(4, vals[1]);
            
            __logger.info("report sql: " + ps);
            
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("quizzes_passed"),rs.getInt("quizzes_taken")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    
    
    
    public CmList<HighlightReportData> getReportAvgQuizScores(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        /** Make sure to not include Auto Enroll (15)
         *  or custom program (36)
         * 
         */
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_AVG_QUIZ_SCORES",createInListMap(createInList(uids)) );

        
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            String[] vals = QueryHelper.getDateTimeRange(from, to);
            ps.setString(1, vals[0]);
            ps.setString(2, vals[1]);
            ps.setString(3, vals[0]);
            ps.setString(4, vals[1]);
            
            __logger.info("report sql: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("avg_quiz_score"),rs.getInt("quizzes_taken")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    } 

    
    
    public CmList<HighlightReportData> getReportFailedQuizzes(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        /** not auto-test or custom-program */
        String sql = 
            "select  u.uid,  u.user_name, count(*) as failed_quizzes " +
            "FROM v_HA_USER_ACTIVE u " +
            " join HA_TEST t " +
            " on t.user_id = u.uid " +
            " join CM_USER_PROGRAM c on c.id = t.user_prog_id " +
            " join HA_TEST_RUN r " +
            " on r.test_id = t.test_id " +
            " where  u.uid in ( " + createInList(uids) + " ) " +
            " and r.is_passing = 0 " +
            " and c.test_def_id not in(15,36) " +
            " and date(r.run_time) between ? and ? " +
            " group by u.uid " +
            " order by failed_quizzes desc";
     
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            String[] vals = QueryHelper.getDateTimeRange(from, to);
            ps.setString(1, vals[0]);
            ps.setString(2, vals[1]);

            __logger.info("report sql: " + ps);
            
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

    public List<HighlightReportData> getReportFailedCurrentQuizzes(List<String> uids, Date from, Date to) throws Exception {
        
        String[] vals = QueryHelper.getDateTimeRange(from, to);
        return getJdbcTemplate().query(
                CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_FAILED_CURRENT_QUIZ",createInListMap(createInList(uids))),
                new Object[]{vals[0],vals[1]},
                new RowMapper<HighlightReportData>() {
                    @Override
                    public HighlightReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), rs.getString("failed_quizzes"));                       
                    }
                });
    }
    
    
    public CmList<HighlightReportData> getReportLoginsPerWeek(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        String sql =
            
            "select u.uid, u.user_name,  floor( (count(*)  /  7) ) as avg_login_week " +
            " from   HA_USER_LOGIN l " +
            "   JOIN v_HA_USER_ACTIVE u ON u.uid = l.user_id " +
            " where  u.uid in(" + createInList(uids) + " ) " +
            " and date(l.login_time) between ? and ? " +
            " group by u.uid " +
            " order by avg_login_week desc, u.user_name";
            
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            String[] vals = QueryHelper.getDateTimeRange(from, to);
            ps.setString(1, vals[0]);
            ps.setString(2, vals[1]);
            
            __logger.info("report sql: " + ps);
            
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
    
    /*return students who have not login
     * 
     */
    public CmList<HighlightReportData>  getReportZeroLogins(final Connection conn, List<String> uids, Date from, Date to) throws Exception {
        
        
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_ZERO_LOGIN",createInListMap(createInList(uids)) );
            
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            String[] vals = QueryHelper.getDateTimeRange(from, to);
            ps.setString(1, vals[0]);
            ps.setString(2, vals[1]);
            
            __logger.info("report sql: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), null));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    /*return students' time-on-task
     * 
     */
    public CmList<HighlightReportData> getReportTimeOnTask(final Connection conn, List<String> uids, Date from, Date to) throws Exception {

        String sql =
        	CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_TIME_ON_TASK", createInListMap(createInList(uids)) );

        CmList<HighlightReportData> list = new CmArrayList<HighlightReportData>();

        PreparedStatement ps=null;
        try {
            ps = conn.prepareStatement(sql);
            String[] vals = QueryHelper.getDateTimeRange(from, to);
            ps.setString(1, vals[0]);
            ps.setString(2, vals[1]);
            ps.setString(3, vals[0]);
            ps.setString(4, vals[1]);            
            ps.setString(5, vals[0]);
            ps.setString(6, vals[1]);            

            __logger.debug("report sql: " + ps);

            Map<ActivityTypeEnum, ActivityTime> map = StudentActivityDao.getInstance().getActivityTimeMap();
            ResultSet rs = ps.executeQuery();
            int userId = -1;
            int totalTime = 0;
            String userName = null;
            while(rs.next()) {
            	int count = rs.getInt("activity_count");
            	String type = rs.getString("activity_type");
            	int time = map.get(ActivityTypeEnum.valueOf(type.toUpperCase())).timeOnTask;
                            	
            	if (userId != rs.getInt("user_id") && userId > 0) {
            		// new student, add previous and reset total time
                    list.add(new HighlightReportData(userId, userName, String.valueOf(totalTime)));
                    totalTime = 0;
            	}
            	userId = rs.getInt("user_id");
            	userName = rs.getString("user_name");
            	totalTime = totalTime + count * time;
            }
            // add last student
            if (userId != -1)
                list.add(new HighlightReportData(userId, userName, String.valueOf(totalTime)));

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
                put("DATE_FROM",QueryHelper.getDateTime(from, true));
                put("DATE_TO",QueryHelper.getDateTime(to, false));
                put("AID", Integer.toString(adminId));
            }
        };
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_HIGHLIGHT_REPORT_GROUP_PROGRESS", tokenMap);
            
            
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        Statement ps=null;
        try {
            ps = conn.createStatement();
            
            __logger.info("report sql: " + sql);
            
            ResultSet rs = ps.executeQuery(sql);
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getString("group_name"),rs.getInt("active_count"),rs.getInt("login_count"),rs.getInt("lessons_viewed"),rs.getInt("quizzes_passed")));
            }
            list.add(createTotalRow(list));            
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }

    
    
    /**     •  Name: Group Usage
    •  Tooltip: Shows the usage of optional learning resources for groups with at least one active student.
    •  Sample report

        Group name  Active Videos Games Activities Flash Cards
        7th-Graders <n> <n> <n> <n> <n>
        8th-Graders <n> <n> <n> <n> <n>
        SCHOOLWIDE <n> <n> <n> <n> <n>
    */
    public CmList<HighlightReportData> getReportGroupUsage(final Connection conn, final int adminId,final List<String> uids, final Date from, final Date to) throws Exception {
        Map<String,String> tokenMap = new HashMap<String,String>()
        {
            {
                put("UIDLIST",createInList(uids));
                put("DATE_FROM",QueryHelper.getDateTime(from, true));
                put("DATE_TO",QueryHelper.getDateTime(to, false));
                put("AID", Integer.toString(adminId));
            }
        };
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_HIGHLIGHT_REPORT_GROUP_USAGE", tokenMap);
            
            
        CmList<HighlightReportData> list=new CmArrayList<HighlightReportData>();
        
        Statement ps=null;
        try {
            ps = conn.createStatement();
            
            __logger.info("report sql: " + sql);
            
            ResultSet rs = ps.executeQuery(sql);
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getString("group_name"),rs.getInt("active_count"),rs.getInt("videos_viewed"),rs.getInt("games_viewed"),rs.getInt("activities_viewed"),rs.getInt("flash_cards_viewed")));
            }
            list.add(createTotalRow(list));
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    
    private HighlightReportData createTotalRow(CmList<HighlightReportData> list) {
        HighlightReportData row = new HighlightReportData();
        row.setName("SCHOOLWIDE");

        for(HighlightReportData d: list) {
            row.setActiveCount(row.getActiveCount() + d.getActiveCount());
            row.setVideosViewed(row.getVideosViewed() + d.getVideosViewed());
            row.setGamesViewed(row.getGamesViewed() + d.getGamesViewed());
            row.setActivitiesViewed(row.getActivitiesViewed() + d.getActivitiesViewed());
            row.setLoginCount(row.getLoginCount() + d.getLoginCount());
            row.setLessonsViewed(row.getLessonsViewed() + d.getLessonsViewed());
            row.setQuizzesPassed(row.getQuizzesPassed() + d.getQuizzesPassed());            
        }
        return row;
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
   
    public Map<Integer, Integer> getTimeOnTaskMap(final Connection conn, List<StudentModelI> smList, Date from, Date to) throws Exception {
        List<String> uidList = new ArrayList<String>();
    	for (StudentModelI sm : smList) {
    		uidList.add(sm.getUid().toString());
    	}
    	
    	CmList<HighlightReportData> totList = getReportTimeOnTask(conn, uidList, from, to);

    	Map<Integer, Integer> totMap = new HashMap<Integer, Integer>();
    	for (HighlightReportData tot : totList) {
    		totMap.put(tot.getUid(), Integer.valueOf(tot.getData()));
    		if (logger.isDebugEnabled())
    			logger.debug("+++ getTimeOnTaskMap(): uid: " + tot.getUid() + ", timeOnTask: " + tot.getData());
    	}

    	return totMap;
    }

    public Map<Integer, Integer> getTimeOnTaskMapForUids(final Connection conn, List<String> uidList, Date from, Date to) throws Exception {
    	
    	CmList<HighlightReportData> totList = getReportTimeOnTask(conn, uidList, from, to);

    	Map<Integer, Integer> totMap = new HashMap<Integer, Integer>();
    	for (HighlightReportData tot : totList) {
    		totMap.put(tot.getUid(), Integer.valueOf(tot.getData()));
    		if (logger.isDebugEnabled())
    			logger.debug("+++ getTimeOnTaskMap(): uid: " + tot.getUid() + ", timeOnTask: " + tot.getData());
    	}

    	return totMap;
    }

    private Map<String,String> createInListMap(String list) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("UID_LIST", list);
        return map;
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



/** 

" join ( " +
"        select user_id, count(*) as quizzes_taken " +
"        from   HA_TEST t " +
"           JOIN HA_TEST_RUN r on r.test_id = t.test_id " +
"        where r.run_time between ? and ? " + 
"        GROUP BY user_id " +
"      ) qv on qv.user_id = u.uid " +

*/