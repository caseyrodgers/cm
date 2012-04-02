package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.JsonUtil;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.activity.StudentActivitySummaryModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * 
 * @author bob
 *
 */

public class StudentActivityDao extends SimpleJdbcDaoSupport {

	private static final Logger LOGGER = Logger.getLogger(StudentActivityDao.class);

	static private StudentActivityDao __instance;
	static public StudentActivityDao getInstance() throws Exception {
		if(__instance == null) {
			__instance = (StudentActivityDao)SpringManager.getInstance().getBeanFactory().getBean("StudentActivityDao");
		}
		return __instance;
	}

	private StudentActivityDao() {
	}

	/**
	 * Temporary
	 * 
	 * @param conn
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public List<StudentActivityModel> getStudentActivity(final Connection conn, int uid) throws Exception {
		return getStudentActivity(conn, uid, null, null);
	}
	
	/** Return list of activity records
	 *
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public List<StudentActivityModel> getStudentActivity(final Connection conn, int uid, Date fromDate, Date toDate) throws Exception {
		List<StudentActivityModel> l = null;

		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_ACTIVITY"));
			ps.setInt(1, uid);
			ps.setInt(2, uid);
			ps.setInt(3, uid);
			ps.setInt(4, uid);
			ps.setInt(5, uid);
			ps.setInt(6, uid);
			ps.setInt(7, uid);
			rs = ps.executeQuery();

			l = loadStudentActivity(conn, rs);
			
			Map<String, Integer> totMap = getTimeOnTaskForRunIDs(conn, l, uid);
			
			updateTimeOnTask(totMap, l);
			
		} catch (Exception e) {
			LOGGER.error(String.format("*** Error getting student details for student uid: %d", uid), e);
			throw new Exception("*** Error getting student details ***");
		} finally {
			SqlUtilities.releaseResources(rs, ps, null);
		}
		return l;
	}

	public  Map<String, Integer> getTimeOnTaskForRunIDs(final Connection conn, List<StudentActivityModel> samList,
			int uid) throws Exception {

		if (samList == null || samList.size() == 0) {
			// nothing to do
			return null;
		}

		Map<String, Integer> totMap = new HashMap<String, Integer>();
		
        Map<ActivityTypeEnum, ActivityTime> map = getActivityTimeMap();

		Statement stmt = null;
		ResultSet rs = null;
		try {
			String sql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_ACTIVITY_TIME_ON_TASK");
			StringBuilder sb = new StringBuilder();
			
			boolean first = true;
			int runId = -1;
			int prevRunId = -1;
			for (StudentActivityModel sam : samList) {
				runId = sam.getRunId();
				if (runId < 1 || runId == prevRunId) continue;
				if (! first) sb.append(", ");
			    first = false;
			    prevRunId = runId;
				sb.append(runId);
			}
			stmt = conn.createStatement();
			
			if (sb.length() == 0) return totMap;
			
			rs = stmt.executeQuery(sql.replace("$$RUNID_LIST$$", sb.toString()));
			
			runId = -1;
			int totalTime = 0;
			String useDate = null;
			while (rs.next()) {
				
				if (useDate != null && 
				    (runId != rs.getInt("run_id") || ! useDate.equals(rs.getString("use_date")))) {
					String key = String.format("%d.%s", runId, useDate);
					totMap.put(key, totalTime);
					totalTime = 0;
				}
				runId = rs.getInt("run_id");
				useDate = rs.getString("use_date");
				
				int count = rs.getInt("activity_count");
				String type = rs.getString("activity_type");
				int time = map.get(ActivityTypeEnum.valueOf(type.toUpperCase())).timeOnTask;
				totalTime += time * count;
			}
			String key = String.format("%d.%s", runId, useDate);
			totMap.put(key, totalTime);
			
		} catch (Exception e) {
			LOGGER.error(String.format("*** Error getting time-on-task for student uid: %d", uid), e);
			throw new Exception("*** Error getting time-on-task ***");
		} finally {
			SqlUtilities.releaseResources(rs, stmt, null);
		}
		
		return totMap;
	}
	
	public List<StudentActivityModel> getStudentActivity(final int uid, final boolean includeTimeOnTask) throws Exception {
		final CmAdminDao cmaDao = CmAdminDao.getInstance();
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("STUDENT_ACTIVITY");
        List<StudentActivityModel> list = this.getJdbcTemplate().query(
                sql,
                new Object[]{uid, uid, uid, uid, uid, uid, uid, uid},
                new RowMapper<StudentActivityModel>() {
                    public StudentActivityModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        StudentActivityModel model;
                        try {
                			model = loadStudentActivityRow(rs, cmaDao);
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Student Activity for uid: %d", uid), e);
                            throw new SQLException(e.getMessage());
                        }
                        return model;
                    }});

		fixReviewSectionNumbers(list);

		// reverse order of list
		List<StudentActivityModel> mList = new ArrayList<StudentActivityModel>(list.size());
		for (int i = (list.size() - 1); i >= 0; i--) {
			mList.add(list.get(i));
		}

		return mList;
	}

	public List<StudentActivitySummaryModel> getStudentActivitySummary(int uid) throws Exception {
		
		List<StudentActivityModel> samList = getStudentActivity(uid, false);
		
		List<StudentActivitySummaryModel> sasList = new ArrayList<StudentActivitySummaryModel>();
		
		// summarize student activity
		String progName = "";
		int passingQuizTotal = 0;
		int passingQuizCount = 0;
		int allQuizTotal = 0;
		int quizCount = 0;
		
		StudentActivitySummaryModel model = null;
		for (StudentActivityModel sam : samList) {
			
			if (! progName.equals(sam.getProgramDescr())) {
				
				if (model != null) {
					// set Quiz data
					if (quizCount > 0) {
						double avg = (double) allQuizTotal / (double) quizCount;
						model.setAllQuizAvg((int)Math.round(avg));
						model.setTotalQuizzes(quizCount);
					}
					if (passingQuizCount > 0) {
						double avg = (double) passingQuizTotal / (double) passingQuizCount;
						model.setPassedQuizAvg((int)Math.round(avg));
						model.setPassedQuizzes(passingQuizCount);
					}
				}
				model = new StudentActivitySummaryModel();
				progName = sam.getProgramDescr();
				model.setProgramName(progName);
				model.setProgramType(sam.getProgramType());
				int sectionCount = (sam.getSectionCount() != null) ? sam.getSectionCount() : 0;
				int sectionNum = (sam.getSectionNum() != null) ? sam.getSectionNum() : 0;
				model.setSectionNum(sectionNum);
				model.setUseDate(sam.getUseDate());

				String status = " ";
				if (logger.isDebugEnabled()) logger.debug("+++ progName: " + progName);

				if (progName.startsWith("CP")) {
    				model.setSectionNum(sam.getLessonsViewed());
    				if (logger.isDebugEnabled()) logger.debug("+++ lessonCount: " + sam.getLessonCount());
					status = (sam.getLessonsViewed() != sam.getLessonCount()) ?
							String.format("%d of %d Lessons", sam.getLessonsViewed(), sam.getLessonCount()):"Completed";
				}
    			else if (progName.startsWith("CQ")) {
    				model.setSectionNum(sam.getLessonCount());
    				if (logger.isDebugEnabled()) logger.debug("+++ questionCount: " + sam.getLessonCount());
					status = (sam.getLessonCount() != sam.getSectionCount()) ?
							String.format("%d of %d Questions", sam.getLessonCount(), sam.getSectionCount()):"Completed";
				}
    			else {
					model.setSectionCount(sectionCount);
				    if (sectionCount > 0 && sectionNum == sectionCount) {
					    status = "Completed";
				    }
				    else if (sectionNum > 0) {
					    if (sectionCount > 0)
						    status = String.format("Section %d of %d", sectionNum, sectionCount);
					    else
						    status = String.format("Section %d", sectionNum);
				    }
				}

				model.setStatus(status);
				sasList.add(model);

				// reset stats
				quizCount = 0;
				sectionCount = 0;
				allQuizTotal = 0;
				passingQuizTotal = 0;
				passingQuizCount = 0;
			}

			if (sam.getIsQuiz() && !sam.getIsCustomQuiz() && sam.getRunId() > 0) {
				logger.debug("runId: " + sam.getRunId());
				quizCount++;
				String result = sam.getResult();
				int offset = result.indexOf("%");
				if (offset < 0) continue;
				result = result.substring(0, offset);
				int score = Integer.parseInt(result);
				allQuizTotal += score;
				if (sam.getIsPassing()) {
					passingQuizCount++;
					passingQuizTotal += score;
				}
				if (quizCount <= 10) {
					model.getQuizScores().add(score);
				}
			}
			
		}
		if (quizCount > 0) {
			double avg = (double) allQuizTotal / (double) quizCount;
			model.setAllQuizAvg((int)Math.round(avg));
			model.setTotalQuizzes(quizCount);
		}
		if (passingQuizCount > 0) {
			double avg = (double) passingQuizTotal / (double) passingQuizCount;
			model.setPassedQuizAvg((int)Math.round(avg));
			model.setPassedQuizzes(passingQuizCount);
		}

		return sasList;
	}

	private List<StudentActivityModel> loadStudentActivity(final Connection conn, ResultSet rs) throws Exception {

		List<StudentActivityModel> l = new ArrayList<StudentActivityModel>();
		CmAdminDao cmaDao = CmAdminDao.getInstance();

		while (rs.next()) {
			StudentActivityModel m = loadStudentActivityRow(rs, cmaDao);
			l.add(m);
		}

		fixReviewSectionNumbers(l);
		
		// reverse order of list
		List<StudentActivityModel> m = new ArrayList<StudentActivityModel>(l.size());
		for (int i = (l.size() - 1); i >= 0; i--) {
			m.add(l.get(i));
		}

		return m;
	}

	int currentRunId = 0;
	int lessonsCompleted = 0;
	
	private StudentActivityModel loadStudentActivityRow(ResultSet rs, CmAdminDao cmaDao)
			throws SQLException, Exception {
		StudentActivityModel m = new StudentActivityModel();
		boolean isCustomQuiz = (rs.getInt("is_custom_quiz") > 0);
		m.setIsCustomQuiz(rs.getBoolean("is_custom_quiz"));
		m.setProgramDescr(rs.getString("program"));
		m.setUseDate(rs.getString("use_date"));
		m.setStart(rs.getString("start_time"));
		m.setStop(rs.getString("stop_time"));
		m.setTestId(rs.getInt("test_id"));
		int sectionNum = rs.getInt("test_segment");
		m.setSectionNum(sectionNum);
		int sectionCount = rs.getInt("segment_count");
		m.setSectionCount(sectionCount);
		String progId = rs.getString("prog_id");
		m.setTimeOnTask(rs.getInt("time_on_task"));
		m.setProgramType(rs.getString("prog_type"));
		m.setIsArchived(rs.getInt("is_archived"));

		if (progId.equalsIgnoreCase("chap")) {
			String subjId = rs.getString("subj_id");
			String chapter = JsonUtil.getChapter(rs.getString("test_config_json"));
			List <ChapterModel> cmList = cmaDao.getChaptersForProgramSubject("Chap", subjId);
			for (ChapterModel cm : cmList) {
				if (cm.getTitle().equals(chapter)) {
					m.setProgramDescr(new StringBuilder(m.getProgramDescr()).append(" ").append(cm.getNumber()).toString());
					break;
				}
			}
		}

		int runId = rs.getInt("test_run_id");
		
		if (runId != currentRunId) {
			currentRunId = runId;
			lessonsCompleted = 0;
		}
		m.setRunId(runId);

		StringBuilder sb = new StringBuilder();
		sb.append(rs.getString("activity"));

		boolean isQuiz = (rs.getInt("is_quiz") > 0);
		m.setIsQuiz(isQuiz);
		if (isQuiz && !isCustomQuiz) {
			sb.append(sectionNum);
		}
		m.setActivity(sb.toString());

		// TODO: flag re-takes?
		sb.delete(0, sb.length());
		int totalSessions = rs.getInt("total_sessions");
		m.setLessonCount(totalSessions);
		if (isQuiz) {
			int numCorrect = rs.getInt("answered_correct");
			int numIncorrect = rs.getInt("answered_incorrect");
			int notAnswered = rs.getInt("not_answered");
			boolean isPassing = (rs.getInt("is_passing") > 0);
			m.setIsPassing(isPassing);
			if ((numCorrect + numIncorrect + notAnswered) > 0) {
				double percent = (double) (numCorrect * 100) / (double) (numCorrect + numIncorrect + notAnswered);
				sb.append(Math.round(percent)).append("% correct");
			}
			else if (isCustomQuiz == false) {
				sb.append("Started");
			}
			else {
				m.setIsQuiz(false);
				sb.append(totalSessions).append(" out of ").append(sectionCount).append(" answered");
			}
		} else {
	        int inProgress = 0; // lessonsViewed % problemsPerLesson;

	        int lessonsViewed = 0;
			if (m.getUseDate() != null) {
				lessonsViewed = rs.getInt("problems_viewed");
			}
			else {
				m.setUseDate(rs.getString("run_date"));
			}
			m.setLessonsViewed(lessonsViewed);
			lessonsCompleted += lessonsViewed;
			
			m.setTimeOnTask(rs.getInt("time_on_task") * lessonsViewed);

			if (lessonsCompleted >= 0) {
				if (totalSessions < 1) {
					sb.append("total of ").append(lessonsCompleted);
					if (lessonsCompleted > 1)
						sb.append(" reviews completed");
					else
						sb.append(" review completed");
					if (inProgress != 0) {
						sb.append(", 1 in progress");
					}
				} else {
					sb.append(lessonsCompleted).append(" out of ");
					sb.append(totalSessions).append(" completed");
				}
			} else {
				if (inProgress != 0) {
					sb.append("1 review in progress");
				}
			}
		}
		m.setResult(sb.toString());
		return m;
	}

	private void updateTimeOnTask(Map<String, Integer> totMap, List<StudentActivityModel> samList) {
		
		if (samList == null || samList.isEmpty() || totMap == null) {
			// nothing to do
			return;
		}
		
		for (StudentActivityModel sam : samList) {
			if (sam.getIsQuiz() || sam.getIsCustomQuiz()) continue;
			String key = String.format("%d.%s", sam.getRunId(), sam.getUseDate());
			int totalTime = sam.getTimeOnTask() + ((totMap.get(key) == null) ? 0 : totMap.get(key));
			sam.setTimeOnTask(totalTime);
		}
		
	}

	private void fixReviewSectionNumbers(List<StudentActivityModel> l) {
		Map<Integer, StudentActivityModel> h = new HashMap<Integer, StudentActivityModel>();
		for (StudentActivityModel m : l) {
			if (m.getIsQuiz()) {
				h.put(m.getRunId(), m);
			}
		}

		for (StudentActivityModel m : l) {
			if (!m.getIsQuiz() && !m.getIsCustomQuiz()) {
				Integer runId = m.getRunId();
				StudentActivityModel q = h.get(runId);
				if (q != null) {
					String[] t = q.getActivity().split("-");
					if (t.length > 1) {
						StringBuilder sb = new StringBuilder(m.getActivity());
						sb.append(t[1]);
						m.setActivity(sb.toString());
						m.setSectionNum(q.getSectionNum());
					}
				}
			}
		}
	}

    /** Return true if this student activity model is
     *  for a custom quiz
     *
     * @param sm
     * @return
     */
    private boolean isCustomQuiz(StudentActivityModel sm) {
        return sm.getProgramDescr().startsWith("CQ:");
    }
    
    public enum ActivityTypeEnum {
    	ACTIVITY, ACTIVITY_STANDARD, CMEXTRA, EPP_WB, FLASHCARD, FLASHCARD_SPANISH,
    	PRACTICE, QUIZ, RESULTS, REVIEW, RPP_WB, SOLUTION, VIDEO, WHITEBOARD, WORKBOOK, UNKNOWN
    };

    class ActivityTime {

    	ActivityTypeEnum type;

    	int timeOnTask;

    	String description;
    	
    	ActivityTime(ActivityTypeEnum type, int timeOnTask, String description) {
    		this.type = type;
    		this.description = description;
    		this.timeOnTask = timeOnTask;
    	}
    }

    public Map<ActivityTypeEnum, ActivityTime> getActivityTimeMap() {
		@SuppressWarnings("unchecked")
    	Map<ActivityTypeEnum, ActivityTime> activityTimeMap =
    		(Map<ActivityTypeEnum, ActivityTime>) CmCacheManager.getInstance().retrieveFromCache(CacheName.ACTIVITY_TIMES, CacheName.ACTIVITY_TIMES);

		if(activityTimeMap != null) {
            return activityTimeMap;
		}

        activityTimeMap = new HashMap<ActivityTypeEnum, ActivityTime>();
        
        String sql = "select type, description, task_time from HA_ACTIVITY_TIME";

    	List<ActivityTime> activityTimeList = getJdbcTemplate().query(
    			sql,
    			new Object[]{},
    			new RowMapper<ActivityTime>() {
    				@Override
    				public ActivityTime mapRow(ResultSet rs, int rowNum) throws SQLException {
    					ActivityTypeEnum type = ActivityTypeEnum.valueOf(rs.getString("type").toUpperCase());
    					int timeOnTask;
    					String description;
    					if (type == null) {
    						type = ActivityTypeEnum.valueOf("UNKNOWN");
    						timeOnTask = 0;
    						description = "Unknown Activity Type";
    					}
    					else {
    						timeOnTask = rs.getInt("task_time");
    						description = rs.getString("description");
    					}
    					return new ActivityTime(type, timeOnTask, description);
    				}
    			});

    	// transfer List to Map
    	for (ActivityTime at : activityTimeList) {
    		activityTimeMap.put(at.type, at);
    	}

        CmCacheManager.getInstance().addToCache(CacheName.ACTIVITY_TIMES, CacheName.ACTIVITY_TIMES, activityTimeMap);

    	return activityTimeMap;
    }
    
    public List<Integer> getStudentsWithActivityInDateRange(List<Integer> userIds, final String dateRange,
    		String options) throws Exception {
    	if (userIds == null)
    		throw new IllegalArgumentException("userIds cannot be null");
    	if (dateRange == null || dateRange.trim().length() == 0)
    		throw new IllegalArgumentException("dateRange cannot be null or empty");
    	
    	if (userIds.size() == 0) return userIds;

    	String[] dates = dateRange.split(" - ");
    	if (logger.isDebugEnabled()) logger.debug("dates[]: " + dates[0] + ", " + dates[1]);

    	StringBuilder sb = buildSQL(options);

    	final String sql = QueryHelper.createInListSQL(sb.toString(), userIds);
    	if (logger.isDebugEnabled()) logger.debug("+++ getStudentsWithActivityInDateRange(): sql: " + sql);

    	Object[] dateArray = new Object[2*dateRangeCount];
    	int j = 0;
    	for (int i=0; i<dateRangeCount; i++) {
    		dateArray[j++] = dates[0];
    		dateArray[j++] = dates[1];
    	}

        List<Integer> list = this.getJdbcTemplate().query(
                sql,
                dateArray,
                new RowMapper<Integer>() {
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Integer userId;
                        try {
                			userId = rs.getInt("user_id");
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Students with Activity for SQL: %s, dateRange: %s",
                            		sql, dateRange), e);
                            throw new SQLException(e.getMessage());
                        }
                        return userId;
                    }});
        if (logger.isDebugEnabled()) logger.debug("+++ getStudentsWithActivityInDateRange(): list.size(): " + list.size());
    	return list;
    }

    private int dateRangeCount;

	private StringBuilder buildSQL(String options) throws Exception {
		boolean includeLogin    = true;
    	boolean includeQuizView = true;
    	boolean includeQuizTake = true;
    	boolean includeResource = true;
    	boolean includeLesson   = true;
    	boolean includeRegister = true;
    	dateRangeCount = 0;

    	if (options != null && options.trim().length() > 0) {
        	if (logger.isDebugEnabled()) logger.debug("options: " + options);
    		String[] option = options.split(":");
    		if (logger.isDebugEnabled()) {
    			logger.debug("option[0]: " + option[0]);
    			logger.debug("option[1]: " + option[1]);
    			logger.debug("option[2]: " + option[2]);
    			logger.debug("option[3]: " + option[3]);
    			logger.debug("option[4]: " + option[4]);
    			logger.debug("option[5]: " + option[5]);
    		}
    		if (option != null) {
    		    if (option.length > 0) includeLogin    = new Boolean(option[0]);
    		    if (option.length > 1) includeQuizView = new Boolean(option[1]);
    		    if (option.length > 2) includeQuizTake = new Boolean(option[2]);
    		    if (option.length > 3) includeLesson   = new Boolean(option[3]);
    		    if (option.length > 4) includeResource = new Boolean(option[4]);
    		    if (option.length > 5) includeRegister = new Boolean(option[5]);
    		}
    		if (logger.isDebugEnabled()) {
    			logger.debug("includeLogin:    " + includeLogin);
    			logger.debug("includeQuizView: " + includeQuizView);
    			logger.debug("includeQuizTake: " + includeQuizTake);
    			logger.debug("includeLesson:   " + includeLesson);
    			logger.debug("includeResource: " + includeResource);
    			logger.debug("includeRegister: " + includeRegister);
    		}
    	}

    	StringBuilder sb = new StringBuilder();
    	if (includeLogin) {
    		dateRangeCount++;
    		sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_WITH_LOGIN_ACTIVITY_IN_DATE_RANGE"));
    	}
    	if (includeQuizView) {
    		if (sb.length() > 0) sb.append(" UNION ");
    		dateRangeCount++;
    		sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_WITH_QUIZ_VIEW_ACTIVITY_IN_DATE_RANGE"));
    	}
    	if (includeQuizTake) {
    		if (sb.length() > 0) sb.append(" UNION ");
    		dateRangeCount++;
    		sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_WITH_QUIZ_TAKE_ACTIVITY_IN_DATE_RANGE"));
    	}
    	if (includeLesson) {
    		if (sb.length() > 0) sb.append(" UNION ");
    		dateRangeCount++;
    		sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_WITH_LESSON_ACTIVITY_IN_DATE_RANGE"));
    	}
    	if (includeResource) {
    		if (sb.length() > 0) sb.append(" UNION ");
    		dateRangeCount++;
    		sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_WITH_RESOURCE_ACTIVITY_IN_DATE_RANGE"));
    	}
    	if (includeRegister) {
    		if (sb.length() > 0) sb.append(" UNION ");
    		dateRangeCount++;
    		sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_WITH_ACTIVE_REGISTRATION_IN_DATE_RANGE"));
    	}

    	if (sb.length() == 0) {
    		if (logger.isDebugEnabled()) logger.debug("defaulting to full query");
    		dateRangeCount = 6;
    		sb.append(CmMultiLinePropertyReader.getInstance().getProperty("STUDENTS_WITH_ACTIVITY_IN_DATE_RANGE"));
    	}
		return sb;
	}

}
