package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.JsonUtil;
import hotmath.cm.util.CmCacheManager.CacheName;
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

	/** Return list of activity records
	 *
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public List<StudentActivityModel> getStudentActivity(final Connection conn, int uid) throws Exception {
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
			rs = ps.executeQuery();

			l = loadStudentActivity(conn, rs);
			
			Map<Integer, Integer> totMap = getTimeOnTaskForRunIDs(conn, l, uid);
			
			updateTimeOnTask(totMap, l);
			
		} catch (Exception e) {
			LOGGER.error(String.format("*** Error getting student details for student uid: %d", uid), e);
			throw new Exception("*** Error getting student details ***");
		} finally {
			SqlUtilities.releaseResources(rs, ps, null);
		}
		return l;
	}

	public  Map<Integer, Integer> getTimeOnTaskForRunIDs(final Connection conn, List<StudentActivityModel> samList,
			int uid) throws Exception {

		if (samList == null || samList.size() == 0) {
			// nothing to do
			return null;
		}

		Map<Integer, Integer> totMap = new HashMap<Integer, Integer>();
		
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
			while (rs.next()) {
				
				if (runId > 0 && runId != rs.getInt("run_id")) {
					totMap.put(runId, totalTime);
					totalTime = 0;
				}
				runId = rs.getInt("run_id");
				int count = rs.getInt("activity_count");
				String type = rs.getString("activity_type");
				int time = map.get(ActivityTypeEnum.valueOf(type.toUpperCase())).timeOnTask;
				totalTime += time * count;
			}
			totMap.put(runId, totalTime);
			
		} catch (Exception e) {
			LOGGER.error(String.format("*** Error getting time-on-task for student uid: %d", uid), e);
			throw new Exception("*** Error getting time-on-task ***");
		} finally {
			SqlUtilities.releaseResources(rs, stmt, null);
		}
		
		return totMap;
	}

	private List<StudentActivityModel> loadStudentActivity(final Connection conn, ResultSet rs) throws Exception {

		List<StudentActivityModel> l = new ArrayList<StudentActivityModel>();
		CmAdminDao cmaDao = CmAdminDao.getInstance();

		while (rs.next()) {
			StudentActivityModel m = new StudentActivityModel();
			m.setIsCustomQuiz(rs.getBoolean("is_custom_quiz"));
			m.setProgramDescr(rs.getString("program"));
			m.setUseDate(rs.getString("use_date"));
			m.setStart(rs.getString("start_time"));
			m.setStop(rs.getString("stop_time"));
			m.setTestId(rs.getInt("test_id"));
			int sectionNum = rs.getInt("test_segment");
			String progId = rs.getString("prog_id");
			m.setTimeOnTask(rs.getInt("time_on_task"));
		    m.setProgramType(rs.getString("prog_type"));

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
			m.setRunId(runId);

			StringBuilder sb = new StringBuilder();
			sb.append(rs.getString("activity"));

			boolean isQuiz = (rs.getInt("is_quiz") > 0);
			m.setIsQuiz(isQuiz);
			if (isQuiz) {
				sb.append(sectionNum);
			}
			m.setActivity(sb.toString());

			// TODO: flag re-takes?
			sb.delete(0, sb.length());
			if (isQuiz) {
				int numCorrect = rs.getInt("answered_correct");
				int numIncorrect = rs.getInt("answered_incorrect");
				int notAnswered = rs.getInt("not_answered");
				if ((numCorrect + numIncorrect + notAnswered) > 0) {
					double percent = (double) (numCorrect * 100) / (double) (numCorrect + numIncorrect + notAnswered);
					sb.append(Math.round(percent)).append("% correct");
				}
				else {
					sb.append("Started");
				}
			} else {
				int inProgress = 0; // lessonsViewed % problemsPerLesson;
				int totalSessions = rs.getInt("total_sessions");

				int lessonsViewed = rs.getInt("session_number") + 1;
				
				m.setTimeOnTask(rs.getInt("time_on_task") * lessonsViewed);

				if (lessonsViewed >= 0) {
					if (totalSessions < 1) {
						sb.append("total of ").append(lessonsViewed);
						if (lessonsViewed > 1)
							sb.append(" reviews completed");
						else
							sb.append(" review completed");
						if (inProgress != 0) {
							sb.append(", 1 in progress");
						}
					} else {
						sb.append(lessonsViewed).append(" out of ");
						sb.append(totalSessions).append(" reviewed");
					}
				} else {
					if (inProgress != 0) {
						sb.append("1 review in progress");
					}
				}
			}
			m.setResult(sb.toString());
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

	private void updateTimeOnTask(Map<Integer, Integer> totMap, List<StudentActivityModel> samList) {
		
		if (samList == null || samList.isEmpty() || totMap == null) {
			// nothing to do
			return;
		}
		
		for (StudentActivityModel sam : samList) {
			if (sam.getIsQuiz() || sam.getIsCustomQuiz()) continue;
			int totalTime = sam.getTimeOnTask() + ((totMap.get(sam.getRunId()) == null) ? 0 : totMap.get(sam.getRunId()));
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
			if(m.getIsCustomQuiz()) {
				m.setActivity(m.getRunId()!= null?"Completed":"Taking");
			}
			else if (!m.getIsQuiz()) {
				Integer runId = m.getRunId();
				StudentActivityModel q = h.get(runId);
				if (q != null) {
					String[] t = q.getActivity().split("-");
					if (t.length > 1) {
						StringBuilder sb = new StringBuilder(m.getActivity());
						sb.append(t[1]);
						m.setActivity(sb.toString());
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
    


}
