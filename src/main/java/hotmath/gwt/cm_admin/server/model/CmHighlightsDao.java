package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.dao.CCSSReportDao;
import hotmath.cm.server.model.StudentAssignmentStatus;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.StudentActivityDao.ActivityTime;
import hotmath.gwt.cm_admin.server.model.StudentActivityDao.ActivityTypeEnum;
import hotmath.gwt.cm_admin.server.model.highlight.CmHighLightManager;
import hotmath.gwt.cm_admin.server.model.highlight.CmHighLightManager.HighLightStat;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction.ReportType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
                        return new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), ReportType.GREATEST_EFFORT, rs.getInt("lessons_viewed"));
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
            
            __logger.debug("report sql: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), ReportType.LEAST_EFFORT, rs.getInt("lessons_viewed")));
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

            __logger.debug("report sql: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), ReportType.MOST_GAMES, rs.getInt("games_viewed"), rs.getInt("quizzes_taken")));
            }
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }
    
    public List<HighlightReportData> getReportAssignments(List<String> uids, Date fromDate, Date toDate) throws Exception {
    	
    	AssignmentDao asgDao = AssignmentDao.getInstance();        
    	List<StudentAssignmentStatus> list = asgDao.getCompletedAssignmentsForUsersDateRange(uids, fromDate, toDate);

    	List<HighlightReportData> rList = new ArrayList<HighlightReportData> ();

    	int asgCount = 0;
    	int scoreTotal = 0;
    	int userId = 0;
    	String userName = "";

    	for (StudentAssignmentStatus status : list) {
    		if (status.getUserId() != userId && userId > 0) {
    			int avgScore = Math.round((float)scoreTotal / (float)asgCount);
    			HighlightReportData data = new HighlightReportData(userId, userName, ReportType.ASSIGNMENTS, asgCount, avgScore);
    			rList.add(data);
    			asgCount = 0;
    			scoreTotal = 0;
    		}
    		userId = status.getUserId();
    		userName = status.getStudentName();
    		asgCount++;
    		scoreTotal += status.getScore();
    	}
    	if (asgCount > 0) {
		    int avgScore = Math.round((float)scoreTotal / (float)asgCount);
		    HighlightReportData data = new HighlightReportData(userId, userName, ReportType.ASSIGNMENTS, asgCount, avgScore);
		    rList.add(data);
    	}
    	
    	// TODO: sort in order of descending average score
        Collections.sort(rList, new Comparator<HighlightReportData>() {
            @Override
            public int compare(HighlightReportData o1, HighlightReportData o2) {
            	int a1 = o1.getAssignmentAverage();
            	int a2 = o2.getFirstTimeCorrectPercent();
            	if (a1 != a2) return (a1 > a2) ? -1 : 1;
            	if (a1 == a2) return 0;

                String n1 = o1.getName();
            	String n2 = o2.getName();
            	if (n1.compareTo(n2) != 0) return n1.compareTo(n2);
            	
            	int c1 = o1.getAssignmentCount();
            	int c2 = o2.getAssignmentCount();
            	
            	if (c1 != c2) return (c1 > c2) ? -1 : 1;
              	if (a1 == a2) return 0;
              	return 0;

            }
        });
        return rList;

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
            
            if (__logger.isDebugEnabled()) __logger.debug("report sql: " + ps);            
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), ReportType.MOST_QUIZZES_PASSED, rs.getInt("quizzes_passed"), rs.getInt("quizzes_taken")));
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
            
            if (__logger.isDebugEnabled()) __logger.debug("report sql: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), ReportType.AVERAGE_QUIZ_SCORES, rs.getInt("avg_quiz_score"), rs.getInt("quizzes_taken")));
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

            __logger.debug("report sql: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), ReportType.FAILED_QUIZZES, rs.getInt("failed_quizzes")));
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
                        return new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), ReportType.FAILED_CURRENT_QUIZZES, rs.getInt("failed_quizzes"));                       
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
            
            __logger.debug("report sql: " + ps);
            
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
            
            __logger.debug("report sql: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("uid"), rs.getString("user_name"), ReportType.ZERO_LOGINS));
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
            ps.setString(7, vals[0]);
            ps.setString(8, vals[1]);

            if (__logger.isDebugEnabled()) __logger.debug("report sql: " + ps);

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
                    list.add(new HighlightReportData(userId, userName, ReportType.TIME_ON_TASK, totalTime));
                    totalTime = 0;
            	}
            	userId = rs.getInt("user_id");
            	userName = rs.getString("user_name");
            	totalTime = totalTime + count * time;
            }
            // add last student
            if (userId != -1)
                list.add(new HighlightReportData(userId, userName, ReportType.TIME_ON_TASK, totalTime));

        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
        return list;
    }

    final int TOTAL_WIDGETS=0, COUNT_CORRECT=1;

    public CmList<HighlightReportData> getReportWidgetAnswersPercent(List<String> uids, Date from, Date to) throws Exception {

        String[] vals = QueryHelper.getDateTimeRange(from, to);

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_WIDGET_ANSWERS", createInListMap(createInList(uids)));

        final CmList<HighlightReportData> list = new CmArrayList<HighlightReportData>();

        final double totals[] = new double[2];
        
        List<WidgetAnswer> waList = getJdbcTemplate().query(sql, new Object[] {vals[0], vals[1]}, new RowMapper<WidgetAnswer>() {
            @Override
            public WidgetAnswer mapRow(ResultSet rs, int rowNum) throws SQLException {
            	WidgetAnswer wa = new WidgetAnswer();
            	wa.uid = rs.getInt("uid");
            	wa.name = rs.getString("user_name");
            	wa.pid = rs.getString("pid");
            	wa.correct = rs.getInt("correct");
            	return wa;
            }
        });

        int lastUid = 0;
        String name = null;
        for (WidgetAnswer wa : waList) {
        	if (lastUid != 0 && lastUid != wa.uid) {
        		//add to list and reset
                HighlightReportData report = createReportItem(lastUid, name, totals);
                list.add(report);
                totals[TOTAL_WIDGETS] = 0;
                totals[COUNT_CORRECT] = 0;
        	}

        	lastUid = wa.uid;
        	name = wa.name;
        	
            totals[TOTAL_WIDGETS]++;  // total count
            totals[COUNT_CORRECT] += wa.correct;  // correct count
        }

        if (lastUid != 0) {
            HighlightReportData report = createReportItem(lastUid, name, totals);
            list.add(report);
        }

        // sort in order of ascending name and descending percent correct
        Collections.sort(list, new Comparator<HighlightReportData>() {
            @Override
            public int compare(HighlightReportData o1, HighlightReportData o2) {
            	String n1 = o1.getName();
            	String n2 = o2.getName();
            	if (n1.compareTo(n2) != 0) return n1.compareTo(n2);

            	int p1 = o1.getFirstTimeCorrectPercent();
            	int p2 = o2.getFirstTimeCorrectPercent();
            	if (p1 == p2) return 0;
                return (p1 > p2) ? -1 : 1;
            }
        });

        return list;
    }

    private HighlightReportData createReportItem(int uid, String name, double[] totals) {
        double percent = 0.0;
        if (totals[TOTAL_WIDGETS] != 0) {
            percent  = (totals[COUNT_CORRECT] / totals[TOTAL_WIDGETS]) * 100;
        }
        int percentCorrect = (int) Math.round(percent);
        HighlightReportData report = new HighlightReportData(uid, name, ReportType.FIRST_TIME_CORRECT, percentCorrect);
    	return report;
    }

    public CmList<HighlightReportData> getReportMostCCSSCoverage(final List<String> uids, final Date from, final Date to) throws Exception {
        String[] vals = QueryHelper.getDateTimeRange(from, to);

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_COUNTS_FOR_STUDENT", createInListMap(createInList(uids)));

        List<StudentCCSSCount> items = getJdbcTemplate().query(sql, new Object[] {vals[0], vals[1], vals[0], vals[1], vals[0], vals[1]}, new RowMapper<StudentCCSSCount>() {
            @Override
            public StudentCCSSCount mapRow(ResultSet rs, int rowNum) throws SQLException {
            	return new StudentCCSSCount(rs.getString("student_name"), rs.getInt("user_id"), rs.getString("usage_type"), rs.getInt("usage_count"));
            }
        });

        HighlightReportData data = null;
        int userId = -1;
        final CmList<HighlightReportData> list = new CmArrayList<HighlightReportData>();

        for (StudentCCSSCount item : items) {
        	if (userId == -1 || userId != item.userId) {
        		// a new student
        		userId = item.userId;
        		data = new HighlightReportData();
        		data.setName(item.studentName);
        		data.setUid(userId);
        		list.add(data);
        	}
        	if ("ASSIGNMENT".equalsIgnoreCase(item.activityName))
        		data.setAssignmentCount(item.standardCount);
        	else if ("LESSON".equalsIgnoreCase(item.activityName))
        		data.setLessonCount(item.standardCount);
        	else if ("QUIZ".equalsIgnoreCase(item.activityName))
        		data.setQuizCount(item.standardCount);
        	data.setDbCount(data.getDbCount() + item.standardCount);
        }
        Collections.sort(list, new Comparator<HighlightReportData>() {
            @Override
            public int compare(HighlightReportData o1, HighlightReportData o2) {
            	int c1 = o1.getDbCount();
            	int c2 = o2.getDbCount();
            	if (c1 != c2)
            		return (c1 > c2) ? -1 : 1;

                String n1 = o1.getName();
            	String n2 = o2.getName();
            	return n1.compareTo(n2);
            }
        });

    	return list;
    }

    public CmList<HighlightReportData> getReportCCSSCoverage(final List<String> uids, final Date from, final Date to) throws Exception {
        String[] vals = QueryHelper.getDateTimeRange(from, to);

        String sql = CmMultiLinePropertyReader.getInstance().getProperty("HIGHLIGHT_REPORT_CCSS_COVERAGE", createInListMap(createInList(uids)));

        List<StudentCCSSItem> items = getJdbcTemplate().query(sql, new Object[] {vals[0], vals[1], vals[0], vals[1], vals[0], vals[1]}, new RowMapper<StudentCCSSItem>() {
            @Override
            public StudentCCSSItem mapRow(ResultSet rs, int rowNum) throws SQLException {
            	return new StudentCCSSItem(rs.getString("standard_name_new"), rs.getInt("user_id"));
            }
        });

        HighlightReportData data = null;
        String standardName = null;
        CmList<Integer> uidList = null;
        final CmList<HighlightReportData> list = new CmArrayList<HighlightReportData>();
        int uid = 0;

        for (StudentCCSSItem item : items) {
        	if (standardName == null || standardName.equals(item.standardName) == false) {
        		// a new standard
        		standardName = item.standardName;
        		uidList = new CmArrayList<Integer>();
        		data = new HighlightReportData();
        		data.setName(standardName);
        		data.setUidList(uidList);
        		data.setUid(uid++);  // needed for grid row selection in Highlights
        		list.add(data);
        	}
        	uidList.add(item.userId);
        	data.setDbCount(data.getDbCount() + 1);
        }
        Collections.sort(list, new Comparator<HighlightReportData>() {
            @Override
            public int compare(HighlightReportData o1, HighlightReportData o2) {
            	int c1 = o1.getDbCount();
            	int c2 = o2.getDbCount();
            	if (c1 != c2)
            		return (c1 > c2) ? -1 : 1;

                String n1 = o1.getName();
            	String n2 = o2.getName();
            	return n1.compareTo(n2);
            }
        });

    	return list;
    }

    /**
     * get CCSS standards and students (UIDs) that have not covered them for specified CCSS level/strand and date range
     * 
     * @param uids
     * @param levelName
     * @param from
     * @param to
     * @return
     * @throws Exception
     */
    public CmList<HighlightReportData> getReportCCSSLevelNotCovered(final List<String> uids, String levelName, final Date from, final Date to) throws Exception {

    	CmList<HighlightReportData> ccssNotCoveredList = new CmArrayList<HighlightReportData>();

    	if (uids == null || uids.size() == 0) return ccssNotCoveredList;

    	CmList<HighlightReportData> ccssCoverageList = getReportCCSSCoverage(uids, from, to);

    	Map<String, HighlightReportData> coverageMap = new HashMap<String, HighlightReportData>();
    	for (HighlightReportData data : ccssCoverageList) {
    		coverageMap.put(data.getName(), data);
    	}

    	List<String> stdList = CCSSReportDao.getInstance().getStandardNamesForLevel(levelName, true);

    	HighlightReportData data;
    	CmList<Integer> uidList = new CmArrayList<Integer>();
    	for (String uid : uids) {
    		uidList.add(Integer.parseInt(uid));
    	}

        int uid = 0;

        for (String stdName : stdList) {
        	if (coverageMap.containsKey(stdName)) {
        		data = removeStudentsThatCoveredStandard(coverageMap.get(stdName), uidList);
        	}
        	else {
        		data = new HighlightReportData();
        		data.setDbCount(uidList.size());
        		data.setUidList(uidList);
        	}
    		data.setName(stdName);
    		data.setUid(uid++);      // needed for grid row selection in Highlights
    		ccssNotCoveredList.add(data);
        }

    	return ccssNotCoveredList;
    }

    private HighlightReportData removeStudentsThatCoveredStandard(
			HighlightReportData highlightReportData, List<Integer> uidList) {
    	HighlightReportData data = new HighlightReportData();
    	CmList<Integer> uids = highlightReportData.getUidList();
        CmList<Integer> theUids = new CmArrayList<Integer>();
        for (Integer uid : uidList) {
        	if (uids.contains(uid)) continue;
        	theUids.add(uid);
        }
        data.setUidList(theUids);
        data.setDbCount(theUids.size());
        return data;
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
            
            __logger.debug("report sql: " + sql);
            
            ResultSet rs = ps.executeQuery(sql);
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("group_id"), rs.getString("group_name"),ReportType.GROUP_PERFORMANCE, rs.getInt("active_count"),rs.getInt("login_count"),rs.getInt("lessons_viewed"),rs.getInt("quizzes_passed")));
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
            
            __logger.debug("report sql: " + sql);
            
            ResultSet rs = ps.executeQuery(sql);
            while(rs.next()) {
                list.add(new HighlightReportData(rs.getInt("group_id"), rs.getString("group_name"), ReportType.GROUP_USAGE, rs.getInt("active_count"), rs.getInt("videos_viewed"),rs.getInt("games_viewed"),rs.getInt("activities_viewed"),rs.getInt("flash_cards_viewed")));
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
        row.setUid(0);
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
        this.logger.debug("createTotalRow(): list.size(): " + list.size() + ", active: " + row.getActiveCount());
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
    		totMap.put(tot.getUid(), tot.getTimeOnTask());
    		if (logger.isDebugEnabled())
    			logger.debug("+++ getTimeOnTaskMap(): uid: " + tot.getUid() + ", timeOnTask: " + tot.getTimeOnTask());
    	}

    	return totMap;
    }

    public Map<Integer, Integer> getTimeOnTaskMapForUids(final Connection conn, List<String> uidList, Date from, Date to) throws Exception {

    	CmList<HighlightReportData> totList = getReportTimeOnTask(conn, uidList, from, to);
    	Map<Integer, Integer> totMap = new HashMap<Integer, Integer>();
    	for (HighlightReportData tot : totList) {
    		totMap.put(tot.getUid(), tot.getTimeOnTask());
    		if (logger.isDebugEnabled())
    			logger.debug("+++ getTimeOnTaskMap(): uid: " + tot.getUid() + ", timeOnTask: " + tot.getTimeOnTask());
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

    private class WidgetAnswer {
    	int    uid;
    	String name;
    	String pid;
    	int    correct;
    }

    private class StudentCCSSItem {
    	String standardName;
    	int    userId;

    	StudentCCSSItem(String standardName, int userId) {
    		this.standardName = standardName;
    		this.userId = userId;
    	}
    }

    private class StudentCCSSCount {
    	String studentName;
    	int    userId;
    	String activityName;
    	int    standardCount;

    	StudentCCSSCount(String studentName, int userId, String activity, int count) {
    		this.studentName = studentName;
    		this.userId = userId;
    		this.activityName = activity;
    		this.standardCount        = count;
    	}
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