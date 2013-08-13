package hotmath.cm.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CCSSCoverageBar;
import hotmath.gwt.shared.client.model.CCSSCoverageData;
import hotmath.gwt.shared.client.model.CCSSData;
import hotmath.gwt.shared.client.model.CCSSDetail;
import hotmath.gwt.shared.client.model.CCSSDomain;
import hotmath.gwt.shared.client.model.CCSSLesson;
import hotmath.gwt.shared.client.model.CCSSGradeLevel;
import hotmath.gwt.shared.client.model.CCSSStandard;
import hotmath.spring.SpringManager;

/**
 * <code>CCSSReportDao</code> provides access to Common Core State Standards (CCSS) data
 * and student coverage of those standards.
 *  
 * @author bob
 *
 */

public class CCSSReportDao extends SimpleJdbcDaoSupport {

    private static final Logger LOGGER = Logger.getLogger(CCSSReportDao.class);

	private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd");

    private static CCSSReportDao __instance;

    static public CCSSReportDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (CCSSReportDao) SpringManager.getInstance().getBeanFactory().getBean("CCSSReportDao");
        }
        return __instance;
    }

    private CCSSReportDao() {
        /** empty */
    }

    /**
     * Get CCSS standard names for student's quizzes
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public List<CCSSCoverageData> getStudentQuizStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CORRECT_QUIZ_CCSS_NAMES_FOR_STUDENT") +
        		CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CCSS_ORDER_BY");
        return getStudentStandardNames(userId, fromDate, toDate, sql);
    }

    /**
     * Get CCSS standard names for student's review work
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public List<CCSSCoverageData> getStudentReviewStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_REVIEW_CCSS_NAMES_FOR_STUDENT") +
        		CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CCSS_ORDER_BY");
        return getStudentStandardNames(userId, fromDate, toDate, sql);
    }

    /**
     * Get CCSS standard names for student's assignments
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public List<CCSSCoverageData> getStudentAssignmentStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_CCSS_NAMES_FOR_STUDENT") +
        		CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CCSS_ORDER_BY");
        return getStudentStandardNames(userId, fromDate, toDate, sql);
    }

    /**
     * Get CCSS standard names for student's quizzes, review work, and assignments
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    public List<CCSSCoverageData> getStudentCombinedStandardNames(int userId, Date fromDate, Date toDate) throws Exception {
    	StringBuilder sb = new StringBuilder();
    	sb.append(CmMultiLinePropertyReader.getInstance().getProperty("GET_CORRECT_QUIZ_CCSS_NAMES_FOR_STUDENT")).append(" UNION ");
    	sb.append(CmMultiLinePropertyReader.getInstance().getProperty("GET_REVIEW_CCSS_NAMES_FOR_STUDENT")).append(" UNION ");
    	sb.append(CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_CCSS_NAMES_FOR_STUDENT"));
    	sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_CCSS_ORDER_BY"));
        return getStudentCombinedStandardNames(userId, fromDate, toDate, sb.toString());
    }

    /**
     * Get CCSS data "tree"
     * 
     * @return
     * @throws Exception
     */
    public CCSSData getCCSSData() throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_DATA");

    	final CCSSData ccssData = new CCSSData("Common Core State Standards");
    	try {
    		getJdbcTemplate().query(sql, new RowMapper<CCSSLesson>() {
    	    	CmList<CCSSGradeLevel> levels = new CmArrayList<CCSSGradeLevel>();
    	    	CmList<CCSSDomain> domains = null;
    	    	CmList<CCSSStandard> standards = null;
    	    	CmList<CCSSLesson> lessons = null;
    	    	CCSSGradeLevel level = null;
    	    	CCSSDomain domain = null;
    	    	CCSSStandard standard = null;
    	    	CCSSLesson lesson = null;

    	    	@Override
    			public CCSSLesson mapRow(ResultSet rs, int rowNum) throws SQLException {
    				String levelName = rs.getString("level_name");
    				if (level == null || level.getName().equals(levelName) == false) {
    				    level = new CCSSGradeLevel();
    				    level.setName(levelName);
    				    level.setParent(ccssData);
    				    if (ccssData.getLevels() == null) {
    					    ccssData.setLevels(levels);
    				    }
    				    levels.add(level);
    				    domains = level.getDomains();
    				    if (domains == null) {
    				    	domains = new CmArrayList<CCSSDomain>();
    				    	level.setDomains(domains);
    				    }
    				    domain = null;
    				}
    				String domainName = rs.getString("domain_name");
    				if (domain == null || domain.getName().equals(domainName) == false) {
    					domain = new CCSSDomain();
    					domain.setName(domainName);
    					domain.setParent(level);
    					domains.add(domain);
    					standards = domain.getStandards();
    					if (standards == null) {
    						standards = new CmArrayList<CCSSStandard>();
    						domain.setStandards(standards);
    					}
    				}
    				String standardName = rs.getString("name");
    				if (standard == null || standard.getName().equals(standardName) == false) {
        				standard = new CCSSStandard(rs.getString("name"), rs.getString("original_name"),
        						rs.getString("summary"), rs.getString("description"));
        				standard.setParent(domain);
        				standards.add(standard);
        				lessons = standard.getLessons();
        				if (lessons == null) {
        					lessons = new CmArrayList<CCSSLesson>();
        					standard.setLessons(lessons);
        				}
    				}
    				lesson = new CCSSLesson(rs.getString("lesson"), rs.getString("file"));
    				lesson.setParent(standard);
    				lessons.add(lesson);
    				return lesson;
    			}

    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getCCSSData(): sal: %s", sql), e);
    		throw e;
    	}

    	// sort standards within each category/domain
    	if (ccssData != null) {
    		for (CCSSGradeLevel level : ccssData.getLevels()) {
    			for (CCSSDomain domain : level.getDomains()) {
    				sortStandards(domain.getStandards());    				
    			}
    		}
    	}

    	return ccssData;
    }

    /**
     * 
     * @return list of CCSS level names
     * @throws Exception
     */
	public List<String> getStandardLevelNames() throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_LEVEL_NAMES");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("+++ getStandardLevelNames(): sql: " + sql);
    	List<String> list = null;
    	try {
    		list = getJdbcTemplate().query(sql, new RowMapper<String>() {
    			@Override
    			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
    				String levelName = rs.getString("level_name");
    				return levelName;
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getStandardLevelNames(): sql: %s", sql), e);
    		throw e;
    	}
    	
    	return list;
	}

    /**
     * 
     * @param levelName
     * @return list of standard names for specified levelName
     * @throws Exception
     */
	public List<String> getStandardNamesForLevel(String levelName, boolean hasCoverage) throws Exception {
        String sql = (hasCoverage != true) ?
        		CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_NAMES_FOR_LEVEL") :
        		CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_NAMES_FOR_LEVEL_WITH_COVERAGE");

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("+++ getStandardNamesForLevel(): sql: " + sql);

        List<String> list = null;
    	try {
    		list = getJdbcTemplate().query(sql, new Object[] { levelName }, new RowMapper<String>() {
    			@Override
    			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
    				String name = rs.getString("standard_name_new");
    				return name;
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getStandardNamesForLevel(): levelName: %s, hasCoverage: %s, sql: %s", levelName, hasCoverage, sql), e);
    		throw e;
    	}
    	
    	return list;
	}

    /**
     * 
     * @param standardList
     */
	private void sortStandards(CmList<CCSSStandard> standardList) {
        Collections.sort(standardList, new Comparator<CCSSStandard>() {
            @Override
            public int compare(CCSSStandard o1, CCSSStandard o2) {
                String name1 = o1.getName().replace(".", "-");
                String name2 = o2.getName().replace(".", "-");

                String[] t1 = name1.split("-");
                String[] t2 = name2.split("-");

                int count;
                if (t1.length == t2.length){
                	count = t1.length;
                }
                else {
                	count = (t2.length > t1.length) ? t1.length : t2.length;
                }
            	for (int idx=0; idx<count; idx++) {
            		if (t1[idx].equalsIgnoreCase(t2[idx]) == false) {
            			boolean isNum1 = NumberUtils.isDigits(t1[idx]);
            			boolean isNum2 = NumberUtils.isDigits(t2[idx]);
            			if (isNum1 == false || isNum2 == false) {
            			    return t1[idx].compareToIgnoreCase(t2[idx]);
            			}
            			else {
            				int n1 = Integer.parseInt(t1[idx]);
            				int n2 = Integer.parseInt(t2[idx]);
            				return n1 - n2;
            			}
            		}
            	}
            	return (t1.length > count) ? 1 : -1;
            }
        });

	}

	/**
     * Get CCSS standard names for student
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    private List<CCSSCoverageData> getStudentStandardNames(int userId, Date fromDate, Date toDate, String sql) {
    	List<CCSSCoverageData> standardNames = null;
    	try {
    		standardNames = getJdbcTemplate().query(sql, new Object[] { userId, fromDate, toDate }, new RowMapper<CCSSCoverageData>() {
    			@Override
    			public CCSSCoverageData mapRow(ResultSet rs, int rowNum) throws SQLException {
    				return new CCSSCoverageData(rs.getString("lesson_name"), rs.getString("standard_name_new"));
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getStudentStandardNames(): userId: %d, fromDate: %s, toDate: %s, sql: %s",
    				userId, DATE_FMT.format(fromDate), DATE_FMT.format(toDate), sql), e);
    		throw e;
    	}
    	return standardNames;
    }

    /**
     * Get CCSS standard names for student
     * 
     * @param userId
     * @param fromDate
     * @param toDate
     * @return
     * @throws Exception
     */
    private List<CCSSCoverageData> getStudentCombinedStandardNames(int userId, Date fromDate, Date toDate, String sql) {
    	List<CCSSCoverageData> standardNames = null;
    	try {
    		standardNames = getJdbcTemplate().query(sql, new Object[] { userId, fromDate, toDate, userId, fromDate, toDate, userId, fromDate, toDate }, new RowMapper<CCSSCoverageData>() {
    			@Override
    			public CCSSCoverageData mapRow(ResultSet rs, int rowNum) throws SQLException {
    				return new CCSSCoverageData(rs.getString("lesson_name"), rs.getString("standard_name_new"));
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getStudentStandardNames(): userId: %d, fromDate: %s, toDate: %s, sql: %s",
    				userId, DATE_FMT.format(fromDate), DATE_FMT.format(toDate), sql), e);
    		throw e;
    	}
    	return standardNames;
    }

    public List<CCSSCoverageData> getCCSSGroupCoverageData(int adminId, int groupId, Date fromDate, Date toDate, int percentMin,
    		int percentMax) throws Exception {

    	int studentCount = getNumberOfStudentsInGroup(adminId, groupId);

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_COUNTS_FOR_GROUP");
    	List<CCSSCoverageData> list = null;
    	try {
    		list = getJdbcTemplate().query(sql, new Object[] { adminId, groupId, fromDate, toDate, adminId, groupId, fromDate, toDate, adminId, groupId, fromDate, toDate },
    				new RowMapper<CCSSCoverageData>() {
    			@Override
    			public CCSSCoverageData mapRow(ResultSet rs, int rowNum) throws SQLException {
    				return new CCSSCoverageData(rs.getString("standard_name"), rs.getInt("student_count"));
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getCCSSGroupCoverageData(): adminId: %d, groupId: %d sql: %s",
    				adminId, groupId, sql), e);
    		throw e;
    	}

    	int minCount = Math.round(studentCount * percentMin / 100);
    	int maxCount = (int) Math.floor(studentCount * percentMax / 100);

    	List<CCSSCoverageData> returnList = new ArrayList<CCSSCoverageData>();
    	for (CCSSCoverageData data : list) {
    		if (minCount <= data.getCount() && maxCount >= data.getCount()) {
    			returnList.add(data);
    		}
    	}
    	
    	return returnList;
    }

    public CCSSDetail getCCSSDetail(String name) throws Exception {

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_DETAIL");
    	List<CCSSDetail> list = null;
    	try {
    		list = getJdbcTemplate().query(sql, new Object[] { name },
    				new RowMapper<CCSSDetail>() {
    			@Override
    			public CCSSDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
    				CCSSDetail detail = new CCSSDetail(rs.getString("name"), rs.getString("original_name"),
    						rs.getString("level_name"), rs.getString("domain_name"),
    						rs.getString("summary"), rs.getString("description")); 
    				return detail;
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getCCSSDetail(): name: %s", name), e);
    		throw e;
    	}
    	if (list.isEmpty()) {
    		list.add(new CCSSDetail(name, "Not Found", "Not Found", "Not Found", "Not Found", "Not Found"));
    	}
    	return list.get(0);
    }

    /**
     * Get student count for specified group
     * 
     * @param adminId
     * @param groupId
     * @return
     */
    protected int getNumberOfStudentsInGroup(int adminId, int groupId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("COUNT_STUDENTS_IN_GROUP");
    	List<Integer> count = null;
    	try {
    		count = getJdbcTemplate().query(sql, new Object[] { adminId, groupId }, new RowMapper<Integer>() {
    			@Override
    			public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
    				return rs.getInt("the_count");
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getNumberOfStudentsInGroup(): adminId: %d, groupId: %d, sql: %s",
    				adminId, groupId, sql), e);
    		throw e;
    	}
    	return (count != null) ? count.get(0) : 0;
    }

	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private static final int MAX_BAR_COUNT = 8;

	public List<CCSSCoverageBar> getStudentAllByPeriodStandardNames(Integer userId,
			Date fromDate, Date toDate) throws Exception {
		List<Integer>userIds = new ArrayList<Integer>();
		userIds.add(userId);
		return getStudentAllByPeriodStandardNamesWithDate(userIds, fromDate, toDate);
	}

	public List<CCSSCoverageData> getStandardNamesForStudent(List<Integer> userIds,
			Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_NAMES_FOR_STUDENT");
        sql = QueryHelper.createInListSQL(sql, userIds);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("+++ getStandardNamesForStudentAndLevel(): sql: " + sql);

        //TODO : change this...
    	if ( toDate == null) toDate = new Date();
    	if ( fromDate == null ) fromDate = new Date(toDate.getTime() - 10L*365L*24L*60L*60L*1000L);

    	List<CCSSCoverageData> list = null;
    	try {
    		list = getJdbcTemplate().query(sql,
    				new Object[] { fromDate, toDate, fromDate, toDate, fromDate, toDate },
    				new RowMapper<CCSSCoverageData>() {
    			@Override
    			public CCSSCoverageData mapRow(ResultSet rs, int rowNum) throws SQLException {
    				CCSSCoverageData item = new CCSSCoverageData(rs.getString("lesson_name"), rs.getString("standard_name_new"));
    				item.getColumnLabels().add(rs.getString("usage_type"));
    				String uid = rs.getString("user_id");
					int userId = Integer.parseInt(uid);
    				item.setUserId(userId);
    				item.setCount(1);
    				return item;
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getStandardNamesForStudentAndLevel(): userIds: %d, fromDate: %s, toDate: %s, sql: %s",
    				userIds.get(0), DATE_FMT.format(fromDate), DATE_FMT.format(toDate), sql), e);
    		throw e;
    	}

        return list;
	}

	public List<CCSSCoverageData> getStandardNamesForStudentAndLevel(List<Integer> userIds,
			String levelName, Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CCSS_NAMES_FOR_STUDENT_AND_LEVEL");
        sql = QueryHelper.createInListSQL(sql, userIds);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("+++ getStandardNamesForStudentAndLevel(): sql: " + sql);

        //TODO : change this...
    	if ( toDate == null) toDate = new Date();
    	if ( fromDate == null ) fromDate = new Date(toDate.getTime() - 10L*365L*24L*60L*60L*1000L);

    	List<CCSSCoverageData> list = null;
    	try {
    		list = getJdbcTemplate().query(sql,
    				new Object[] { levelName, fromDate, toDate, levelName, fromDate, toDate, levelName, fromDate, toDate },
    				new RowMapper<CCSSCoverageData>() {
    			@Override
    			public CCSSCoverageData mapRow(ResultSet rs, int rowNum) throws SQLException {
    				CCSSCoverageData item = new CCSSCoverageData(rs.getString("lesson_name"), rs.getString("standard_name_new"));
    				item.getColumnLabels().add(rs.getString("usage_type"));
    				String uid = rs.getString("user_id");
					int userId = Integer.parseInt(uid);
    				item.setUserId(userId);
    				item.setCount(1);
    				return item;
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getStandardNamesForStudentAndLevel(): userIds: %d, fromDate: %s, toDate: %s, levelName: %s, sql: %s",
    				userIds.get(0), DATE_FMT.format(fromDate), DATE_FMT.format(toDate), levelName, sql), e);
    		throw e;
    	}

        return list;
	}

	public List<CCSSCoverageBar> getStudentAllByPeriodStandardNamesWithDate(List<Integer> userIds,
			Date fromDate, Date toDate) throws Exception {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ALL_CCSS_NAMES_FOR_STUDENT_WITH_DATE");

        sql = QueryHelper.createInListSQL(sql, userIds);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("+++ getStudentAllByWeekStandardNamesWithDate(): sql: " + sql);

        List<CCSSCoverageBar> barList = new ArrayList<CCSSCoverageBar>();
    	List<CCSSCoverageData> list = null;
    	try {
    		list = getJdbcTemplate().query(sql,
    				new Object[] { fromDate, toDate, fromDate, toDate, fromDate, toDate },
    				new RowMapper<CCSSCoverageData>() {
    			@Override
    			public CCSSCoverageData mapRow(ResultSet rs, int rowNum) throws SQLException {
    				CCSSCoverageData item = new CCSSCoverageData(rs.getString("lesson_name"), rs.getString("standard_name_new"));
    				item.getColumnLabels().add(rs.getString("usage_date"));
    				item.getColumnLabels().add(rs.getString("usage_type"));
    				item.setCount(1);
    				return item;
    			}
    		});
    	}
    	catch (DataAccessException e) {
    		LOGGER.error(String.format("getStudentAllByWeekStandardNames(): userIds: %d, fromDate: %s, toDate: %s, sql: %s",
    				userIds, DATE_FMT.format(fromDate), DATE_FMT.format(toDate), sql), e);
    		throw e;
    	}

    	if (list == null || list.isEmpty()) return barList;

    	defineBars(list, barList);

    	for (CCSSCoverageData data : list) {
    		addDataToBarList(data, barList);
    	}

        Set<String> uniqueStds = new HashSet<String>();
        for (CCSSCoverageBar bar : barList) {
            addUniqueStds(bar, bar.getAssignmentStdNames(), uniqueStds);
            addUniqueStds(bar, bar.getLessonStdNames(), uniqueStds);
            addUniqueStds(bar, bar.getQuizStdNames(), uniqueStds);
        }

    	makeBarListCumulative(barList);

    	return barList;
	}

	private void addUniqueStds(CCSSCoverageBar uniqueBar,
			CmList<String> stdNames, Set<String> uniqueStds) {
		int count = 0;
		for(String stdName : stdNames) {
			if (uniqueStds.contains(stdName) == true) continue;
			uniqueStds.add(stdName);
			uniqueBar.getUniqueStdNames().add(stdName);
			count++;
		}
		uniqueBar.setUniqueCount(uniqueBar.getUniqueCount() + count);
	}

	private void defineBars(List<CCSSCoverageData> list,
			List<CCSSCoverageBar> barList) throws Exception {
		Date minDate = null;
		Date maxDate = null;
		for (CCSSCoverageData data : list) {
			Date date = dateFormat.parse(data.getColumnLabels().get(0));
			if (minDate == null || minDate.after(date)) minDate = date;
			if (maxDate == null || maxDate.before(date)) maxDate = date;
		}

		GregorianCalendar cal = new GregorianCalendar();
    	cal.setTime(minDate);
    	int minDay = cal.get(GregorianCalendar.DAY_OF_YEAR);
    	int minYear = cal.get(GregorianCalendar.YEAR);

    	cal.setTime(maxDate);
    	int maxDay = cal.get(GregorianCalendar.DAY_OF_YEAR);
    	int maxYear = cal.get(GregorianCalendar.YEAR);

    	int numberOfDays = maxDay - minDay + 1 + (maxYear - minYear) * 365;
    	for (int year=minYear; year<=maxYear; year++) {
    		numberOfDays += cal.isLeapYear(year)?1:0;
    	}

    	int numberOfBars = (numberOfDays >= MAX_BAR_COUNT) ? MAX_BAR_COUNT : numberOfDays;

        int numberOfDaysPerBar = numberOfDays / numberOfBars;
        int additionalDaysLastBar = numberOfDays % numberOfBars;

        // adjust maxDay to beginning of last period
        if (maxDay > (numberOfDaysPerBar + additionalDaysLastBar - 1))
            maxDay -= (numberOfDaysPerBar + additionalDaysLastBar - 1);
        else {
        	maxYear--;
            maxDay = 365 - (maxDay - (numberOfDaysPerBar + additionalDaysLastBar - 1));
            maxDay += cal.isLeapYear(maxYear)?1:0;
        }
    	cal.set(GregorianCalendar.DAY_OF_YEAR, maxDay);
    	cal.set(GregorianCalendar.YEAR, maxYear);
    	Date endDate = cal.getTime();

    	cal.set(GregorianCalendar.DAY_OF_YEAR, minDay);
    	cal.set(GregorianCalendar.YEAR, minYear);
    	Date beginDate = cal.getTime();

    	Date date = beginDate;
    	while (date.before(endDate) || date.compareTo(endDate)==0) {
    		CCSSCoverageBar bar = new CCSSCoverageBar();
    		bar.setBeginDate(date);
    		bar.setNumberOfDays(numberOfDaysPerBar);
    		bar.setLabel(dateFormat.format(date));
    		cal.add(GregorianCalendar.DAY_OF_YEAR, numberOfDaysPerBar-1);
    		date = cal.getTime();
    		bar.setEndDate(date);
    		barList.add(bar);
    		cal.add(GregorianCalendar.DAY_OF_YEAR, 1);
    		date = cal.getTime();
    	}

    	CCSSCoverageBar bar = barList.get(barList.size()-1);
    	bar.setNumberOfDays(bar.getNumberOfDays() + additionalDaysLastBar);
	}

	private void makeBarListCumulative(List<CCSSCoverageBar> barList) {
		CCSSCoverageBar barPrev = barList.get(0);
		for (int idx=1; idx<barList.size(); idx++) {
			CCSSCoverageBar barCurr = barList.get(idx);
			barCurr.setAssignmentCount(barCurr.getAssignmentCount() + barPrev.getAssignmentCount());
			barCurr.setLessonCount(barCurr.getLessonCount() + barPrev.getLessonCount());
			barCurr.setQuizCount(barCurr.getQuizCount() + barPrev.getQuizCount());
			barCurr.setUniqueCount(barCurr.getUniqueCount() + barPrev.getUniqueCount());
			barPrev = barCurr;
		}
	}

	/**
	 * 
	 * @param data
	 * @param list
	 */
	private void addDataToBarList(CCSSCoverageData data, List<CCSSCoverageBar> list) throws Exception {
        String date = data.getColumnLabels().get(0);
		GregorianCalendar cal = new GregorianCalendar();
    	Date ccssDate = dateFormat.parse(date);
        for (CCSSCoverageBar bar : list) {
        	Date barDate = dateFormat.parse(bar.getLabel());
        	if (datesMatch(ccssDate, barDate, bar.getNumberOfDays(), cal)) {
        		assignData(bar, data);
        		break;
        	}
        }		
	}

    private boolean datesMatch(Date ccssDate, Date barDate, int numberOfDays, GregorianCalendar cal) {
    	cal.setTime(ccssDate);
        int dataDay = cal.get(GregorianCalendar.DAY_OF_YEAR);
        int dataYear = cal.get(GregorianCalendar.YEAR);
        
        cal.setTime(barDate);
        int barDay = cal.get(GregorianCalendar.DAY_OF_YEAR);
        int barYear = cal.get(GregorianCalendar.YEAR);
        
        return (dataDay >= barDay && dataDay <= (barDay + numberOfDays - 1) && dataYear == barYear);
	}

	/**
     * 
     * @param bar
     * @param data
     */
	private void assignData(CCSSCoverageBar bar, CCSSCoverageData data) {
		if ("ASSIGNMENT".equalsIgnoreCase(data.getColumnLabels().get(1))) {
		    bar.setAssignmentCount(bar.getAssignmentCount() + 1);
		    bar.getAssignmentStdNames().add(data.getName());
    		return;
		}
		if ("LESSON".equalsIgnoreCase(data.getColumnLabels().get(1))) {
		    bar.setLessonCount(bar.getLessonCount() + 1);
		    bar.getLessonStdNames().add(data.getName());
    		return;
		}
		if ("QUIZ".equalsIgnoreCase(data.getColumnLabels().get(1))) {
		    bar.setQuizCount(bar.getQuizCount() + 1);
		    bar.getQuizStdNames().add(data.getName());
		}
	}

	
}
