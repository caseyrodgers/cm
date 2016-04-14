package hotmath.cm.assignment;

import hotmath.ProblemID;
import hotmath.cm.exam.ExamDao;
import hotmath.cm.server.model.StudentAssignmentStatus;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.CompressHelper;
import hotmath.cm.util.PropertyLoadFileException;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_core.client.model.GroupCopyModel;
import hotmath.gwt.cm_core.client.model.StudentAssignmentProblemStat;
import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.model.AssignmentStatus;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc.client.model.GroupModel;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStudentsAction.TYPE;
import hotmath.gwt.cm_rpc.client.rpc.GetCorrelatedTopicsPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStats;
import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStatsUsers;
import hotmath.gwt.cm_rpc_assignments.client.model.PidStats;
import hotmath.gwt.cm_rpc_assignments.client.model.ProblemStatus;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentGradeDetailInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.LessonDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemAnnotation;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentStatuses;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentUserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentLessonDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemExtended;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.ui.assignment.GradeBookUtils;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.SolutionDao;
import hotmath.util.sql.SqlUtilities;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import sb.util.SbUtilities;

public class AssignmentDao extends SimpleJdbcDaoSupport {

	static Logger __logger = Logger.getLogger(AssignmentDao.class);

	static private AssignmentDao __instance;

	private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat(
			"yyyy-MM-dd");

	static public AssignmentDao getInstance() throws Exception {
		if (__instance == null) {
			__instance = (AssignmentDao) SpringManager.getInstance()
					.getBeanFactory().getBean(AssignmentDao.class.getName());
		}
		return __instance;
	}

	private AssignmentDao() {
		/** empty */
	}

	/**
	 * Only assignments in draft mode will have their problems saved.
	 * 
	 * @param ass
	 * @return
	 */
	public int saveAssignment(final Assignment ass) {
		/**
		 * insert or update the new Assignment record and save the key
		 * 
		 * make sure to set the assignKey[0] variable to assign_key
		 */

		if (ass.getStatus() == null) {
			ass.setStatus("Open"); // default status
		}

		final int assignKey[] = new int[1];
		if (ass.getAssignKey() == 0) {
			KeyHolder keyHolder = new GeneratedKeyHolder();
			/**
			 * is new
			 * 
			 */
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					String sql = "insert into CM_ASSIGNMENT(aid,group_id,name,due_date,comments,last_modified,status,close_past_due,is_graded, auto_release_grades, is_personalized, is_prevent_lesson)values(?,?,?,?,?,?,?,?,?,?,?,?)";
					PreparedStatement ps = con.prepareStatement(sql,
							PreparedStatement.RETURN_GENERATED_KEYS);
					ps.setInt(1, ass.getAdminId());
					ps.setInt(2, ass.getGroupId());
					ps.setString(3, ass.getAssignmentName());
					ps.setDate(4, new java.sql.Date(ass.getDueDate().getTime()));
					ps.setString(5, ass.getComments());
					ps.setDate(6, new java.sql.Date(System.currentTimeMillis()));
					ps.setString(7, ass.getStatus());
					ps.setInt(8, ass.isAllowPastDueSubmits() ? 0 : 1);
					ps.setInt(9, ass.isGraded() ? 1 : 0);
					ps.setInt(10, ass.isAutoRelease() ? 1 : 0);
					ps.setInt(11, ass.isPersonalized() ? 1 : 0);
					ps.setInt(12, ass.isPreventLessonAccess() ? 1 : 0);
					return ps;
				}
			}, keyHolder);
			assignKey[0] = keyHolder.getKey().intValue();
		} else {
			/**
			 * is update
			 * 
			 */
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					String sql = "update CM_ASSIGNMENT set aid = ?, name = ?, due_date = ?, comments = ?, last_modified = now(), status = ?, close_past_due = ?, is_graded = ?, auto_release_grades = ?, is_personalized = ?, is_prevent_lesson = ? where assign_key = ?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, ass.getAdminId());
					ps.setString(2, ass.getAssignmentName());
					ps.setDate(3, new java.sql.Date(ass.getDueDate().getTime()));
					ps.setString(4, ass.getComments());
					ps.setString(5, ass.getStatus());
					ps.setInt(6, ass.isAllowPastDueSubmits() ? 0 : 1);
					ps.setInt(7, ass.isGraded() ? 1 : 0);
					ps.setInt(8, ass.isAutoRelease() ? 1 : 0);
					ps.setInt(9, ass.isPersonalized() ? 1 : 0);
					ps.setInt(10, ass.isPreventLessonAccess() ? 1 : 0);

					ps.setInt(11, ass.getAssignKey());
					return ps;
				}
			});
			assignKey[0] = ass.getAssignKey();
		}
		final int assKey = assignKey[0];

		saveAssignmentPids(assKey, ass);

		return assKey;

	}

	/**
	 * Remove existing and recreate the entire PID set
	 * 
	 * @param assKey
	 * @param ass
	 */
	private void saveAssignmentPids(final int assKey, final Assignment ass) {

		__logger.debug("Saving assignment pids for: " + ass.getAssignKey());
		/**
		 * save the PIDS contained in this Assignment
		 * 
		 */
		if (ass.getPids() != null && ass.getPids().size() > 0) {

			/**
			 * Only save pids if there are currently ZERO
			 * 
			 */
			String sql = "select count(*) as count_pids from CM_ASSIGNMENT_PIDS where assign_key = ?";
			Integer cnt = getJdbcTemplate().queryForObject(sql,
					new Object[] { assKey }, new RowMapper<Integer>() {
						@Override
						public Integer mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return rs.getInt("count_pids");
						}
					});
			if (ass.getStatus().equals("Draft") || cnt == 0) {
				/**
				 * only if there are not already pids assigned.
				 * 
				 */
				deleteAssignmentPids(assKey);

				String sqlPids = "insert into CM_ASSIGNMENT_PIDS(assign_key, pid, label, lesson, lesson_file, ordinal_number)values(?,?,?,?,?,?)";
				final int counter[] = new int[1];
				getJdbcTemplate().batchUpdate(sqlPids,
						new BatchPreparedStatementSetter() {

							@Override
							public void setValues(PreparedStatement ps, int i)
									throws SQLException {
								ps.setInt(1, assKey);
								ProblemDto p = ass.getPids().get(i);

								String pidParts[] = p.getPid().split("\\$");
								String fullPid = pidParts[0];
								if (pidParts.length > 1) {
									try {
										fullPid = SolutionDao
												.getInstance()
												.getGlobalSolutionContext(
														pidParts[0] + "$"
																+ pidParts[1])
												.getPid();

									} catch (Exception e) {
										__logger.error(
												"Error getting global context name",
												e);
									}
								}
								ps.setString(2, fullPid);
								ps.setString(3, p.getLabel());
								ps.setString(4, null);
								ps.setString(5, null);
								ps.setInt(6, ++counter[0]);
							}

							@Override
							public int getBatchSize() {
								return ass.getPids().size();
							}
						});
			}
		}
	}

	/**
	 * Fetch a persisted Assignment from DB
	 * 
	 * @param assKey
	 * @return
	 */
	public Assignment getAssignment(int assKey)
			throws AssignmentNotFoundException {
		Assignment assignment = null;
		try {
			String sql = CmMultiLinePropertyReader.getInstance().getProperty(
					"GET_ASSIGNMENT");
			assignment = getJdbcTemplate().queryForObject(sql,
					new Object[] { assKey }, new RowMapper<Assignment>() {
						@Override
						public Assignment mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return extractAssignmentFromRs(rs);
						}
					});
		} catch (Exception e) {
			throw new AssignmentNotFoundException(assKey, e);
		}

		/**
		 * now read all pids assigned to this Assignment
		 * 
		 */
		List<ProblemDto> pids = getProblemsForAssignment(assKey);
		CmList<ProblemDto> cmPids = new CmArrayList<ProblemDto>();
		cmPids.addAll(pids);
		assignment.setPids(cmPids);

		updateProblemTypes(assignment.getPids());

		return assignment;
	}

	/**
	 * 
	 * @param assignKey
	 * @return List of ProblemDto
	 */
	public List<ProblemDto> getProblemsForAssignment(int assignKey) {
		String sql = "select * from CM_ASSIGNMENT_PIDS where assign_key = ? order by id";
		List<ProblemDto> list = getJdbcTemplate().query(sql,
				new Object[] { assignKey }, new RowMapper<ProblemDto>() {
					@Override
					public ProblemDto mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						String pid = rs.getString("pid");
						return new ProblemDto(rs.getInt("ordinal_number"), rs
								.getInt("id"), rs.getString("label"), pid, 0);
					}
				});

		return list;
	}

	/**
	 * Set the appropriate problem types for all problems in assignment. THis is
	 * dynamic because the type can be changed by the author at any time.
	 * 
	 * 
	 * @param assignment
	 */
	public void updateProblemTypes(final List<ProblemDto> problems) {

		if (problems.size() == 0) {
			return;
		}

		String list = "";
		for (ProblemDto problem : problems) {
			if (list.length() > 0) {
				list += ",";
			}

			list += "'" + problem.getPidOnly() + "'";
		}

		String sql = "select problemindex, solutionxml from SOLUTIONS where problemindex in ("
				+ list + ")";
		getJdbcTemplate().query(sql, new Object[] {}, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String pid = rs.getString("problemindex");
				String xml = rs.getString("solutionxml");

				boolean found = false;
				for (ProblemDto problem : problems) {
					if (problem.getPidOnly().equals(pid)) {
						problem.setProblemType(SolutionDao
								.determineProblemType(xml));
						found = true;
					}
				}
				assert (found);
				return "OK";
			}
		});
	}

	public AssignmentLessonData getAssignmentLessonData() {

		final AssignmentLessonData data = new AssignmentLessonData();

		/**
		 * get list of each prof program *
		 * 
		 */
		String sql = "select * from HA_TEST_DEF where is_active  = 1 and prog_id = 'Prof' order by load_order";
		List<String> pids = getJdbcTemplate().query(sql, new Object[] {},
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						data.getSubjects().add(
								new SubjectDto(0, rs.getInt("test_def_id"), rs
										.getString("test_name"), rs
										.getString("test_name")));
						return "";
					}
				});

		return data;

	}

	/**
	 * Return list of problems associated with named lesson.
	 * 
	 * 
	 * Makes sure that solutions with widgets are returned first
	 * 
	 * 
	 * @param conn
	 * @param lesson
	 * @param gradeLevel
	 * @return
	 */
	public List<ProblemDto> getLessonProblemsFor(Connection conn,
			final String lessonName, String lessonFile, String subject)
			throws Exception {
		return new AssignmentLessonPidSelector(conn, lessonName, lessonFile,
				subject).getProblems();
	}

	public List<Assignment> getAssignments(int adminId, int groupId)
			throws PropertyLoadFileException {
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_ASSIGNMENTS_FOR_GROUP");
		List<Assignment> problems = getJdbcTemplate().query(sql,
				new Object[] { groupId }, new RowMapper<Assignment>() {
					@Override
					public Assignment mapRow(ResultSet rs, int rowNum)
							throws SQLException {

						// create a pseudo name
						String comments = rs.getString("comments");
						Date dueDate = rs.getDate("due_date");
						String assignmentName = _createAssignmentName(dueDate,
								comments);

						Assignment ass = extractAssignmentFromRs(rs);
						try {
							ass.setGradedInfo(getAssignmentGradedInfo(ass
									.getAssignKey()));
						} catch (Exception e) {
							__logger.error("Error getting graded info: " + ass,
									e);
						}

						ass.setAssignmentName(assignmentName);
						ass.setProblemCount(rs.getInt("problem_count"));
						return ass;
					}
				});
		return problems;
	}

	protected AssignmentGradeDetailInfo getAssignmentGradedInfo(int assignKey)
			throws Exception {
		// GET_ASSIGNMENTS_GRADED_INFO
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_ASSIGNMENTS_GRADED_INFO");
		List<AssignmentGradeDetailInfo> infos = getJdbcTemplate().query(sql,
				new Object[] { assignKey },
				new RowMapper<AssignmentGradeDetailInfo>() {
					@Override
					public AssignmentGradeDetailInfo mapRow(ResultSet rs,
							int rowNum) throws SQLException {

						int inGroup = rs.getInt("num_students_in_group");
						int specified = rs.getInt("num_students_specified");
						int inAssignment;
						if (specified > 0) {
							inAssignment = specified;
						} else {
							inAssignment = inGroup;
						}
						return new AssignmentGradeDetailInfo(rs
								.getInt("num_students_graded"), inAssignment);
					}
				});

		if (infos.size() > 0) {
			return infos.get(0);
		} else {
			return new AssignmentGradeDetailInfo();
		}
	}

	public List<Assignment> getAssignments(int adminId, int groupId,
			java.util.Date fromDate, java.util.Date toDate) throws Exception {
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_ASSIGNMENTS_FOR_GROUP_AND_DATE_RANGE");

		String dates[] = QueryHelper.getDateTimeRange(fromDate, toDate);
		List<Assignment> assignments = getJdbcTemplate().query(sql,
				new Object[] { groupId, dates[0], dates[1] },
				new RowMapper<Assignment>() {
					@Override
					public Assignment mapRow(ResultSet rs, int rowNum)
							throws SQLException {

						// create a pseudo name
						String comments = rs.getString("comments");
						Date dueDate = rs.getDate("due_date");
						String assignmentName = _createAssignmentName(dueDate,
								comments);
						Assignment ass = extractAssignmentFromRs(rs);
						ass.setAssignmentName(assignmentName);
						ass.setProblemCount(rs.getInt("problem_count"));
						return ass;
					}
				});
		return assignments;
	}

	private Assignment extractAssignmentFromRs(ResultSet rs)
			throws SQLException {

		boolean allowPastDueSubmits = rs.getInt("close_past_due") == 0;
		boolean hasSpecifiedUsers = rs.getInt("has_specified_users") != 0;

		return new Assignment(rs.getInt("aid"), rs.getInt("assign_key"),
				rs.getInt("group_id"), rs.getString("name"),
				rs.getString("comments"), rs.getDate("due_date"), null,
				rs.getString("status"), allowPastDueSubmits,
				rs.getInt("is_graded") != 0, rs.getTimestamp("last_modified"),
				rs.getInt("auto_release_grades") != 0,
				rs.getInt("is_personalized") != 0,
				rs.getInt("is_prevent_lesson") != 0, hasSpecifiedUsers);
	}

	/**
	 * delete the PIDS contained in this Assignment and any already assigned
	 * PIDS in CM_ASSIGNMENT_PIDS_USER
	 * 
	 */
	private void deleteAssignmentPids(final int assKey) {
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "delete from CM_ASSIGNMENT_PIDS where assign_key = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, assKey);
				return ps;
			}
		});

		boolean deleteAlreadyAssigned = true;
		if (deleteAlreadyAssigned) {
			/**
			 * delete existing, already assigned to user pids
			 * 
			 */
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					String sql = "delete from CM_ASSIGNMENT_PIDS_USER where assign_key = ?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, assKey);
					return ps;
				}
			});

			markAssignmentHasSpecifiedUsers(assKey, false);
		}

	}

	/**
	 * Mark the assignment as either having specified users or not
	 * 
	 * @param assKey
	 * @param yesNo
	 */
	private void markAssignmentHasSpecifiedUsers(final int assKey,
			final boolean yesNo) {
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "update CM_ASSIGNMENT set has_specified_users =  ? where assign_key = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, yesNo ? 1 : 0);
				ps.setInt(2, assKey);
				return ps;
			}
		});
	}

	public void deleteAssignment(final int assKey) {

		deleteAssignmentPids(assKey);

		/**
		 * Delete the assignment record
		 * 
		 */
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "delete from CM_ASSIGNMENT where assign_key = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, assKey);
				return ps;
			}
		});
	}

	public CmList<StudentAssignment> getAssignmentGradeBook(final int assignKey)
			throws Exception {
		CmList<StudentAssignment> stuAssignments = new CmArrayList<StudentAssignment>();
		final Assignment assignment = getAssignment(assignKey);

		/**
		 * assignments are either ALL students in group, or just the ones
		 * specified in CM_ASSIGNMENT_USERS_SPECIFIED.
		 * 
		 * TODO: a way to specified directly in SQL
		 */
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_GRADE_BOOK_DATA_3");
		// sql = getStudentsInAssignmentSqlRestriction(assignKey, sql);

		/**
		 * get assignment problem status list for all users
		 */
		final Map<Integer, String> nameMap = new HashMap<Integer, String>();
		List<StudentProblemDto> problemStatuses = getJdbcTemplate().query(sql,
				new Object[] { assignKey, assignKey, assignKey, assignKey, assignKey },
				new RowMapper<StudentProblemDto>() {
					@Override
					public StudentProblemDto mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						Integer uid = rs.getInt("uid");
						if (!nameMap.containsKey(uid)) {
							nameMap.put(uid, rs.getString("user_name"));
						}

						/**
						 * if individualized, then pid might be different for
						 * each user
						 * 
						 */
						String realPid = rs.getString("pid");
						String realStatus = rs.getString("status");
						String gradeStatus = rs.getString("grade_status");
						if (assignment.isPersonalized()) {
							realPid = getPersonalizedPid(assignKey, uid,
									rs.getInt("problem_id"));
							realStatus = getPersonalizedPidStatus(assignKey,
									uid, realPid);
							
							
							gradeStatus = realStatus;
						}
						ProblemDto probDto = new ProblemDto(rs
								.getInt("ordinal_number"), rs
								.getInt("problem_id"), rs.getString("label"),
								realPid, 0);

						boolean hasShowWork = rs.getInt("has_show_work") != 0;
						boolean hasShowWorkAdmin = rs
								.getInt("has_show_work_admin") != 0;
						boolean isAssignmentClosed = assignment.getStatus()
								.equals("Closed");
						boolean isProblemGraded = rs
								.getInt("is_problem_graded") != 0 ? true
								: false;
						boolean isStudentGraded = rs
								.getInt("is_student_graded") != 0 ? true
								: false;
						StudentProblemDto prob = new StudentProblemDto(uid,
								probDto, realStatus, gradeStatus, hasShowWork,
								hasShowWorkAdmin, isAssignmentClosed,
								isStudentGraded, isProblemGraded);
						
						return prob;
					}
				});

		__logger.info(String.format("assignKey: %d, problemStatuses.size(): %d", assignKey, problemStatuses.size()));
		/**
		 * create student assignments for all users
		 */
		int uid = 0;
		CmList<StudentProblemDto> probList = null;
		Map<Integer, StudentAssignment> stuAssignMap = new HashMap<Integer, StudentAssignment>();
		Map<String, StudentAssignmentUserInfo> userInfos = new HashMap<String, StudentAssignmentUserInfo>();
		for (StudentProblemDto probDto : problemStatuses) {
			if (probDto.getUid() != uid) {
				probList = new CmArrayList<StudentProblemDto>();
				uid = probDto.getUid();

				StudentAssignmentUserInfo userInfo = getStudentAssignmentUserInfo(
						uid, assignKey);

				StudentAssignment stuAssignment = new StudentAssignment(uid,
						assignment, userInfo.getTurnInDate(),
						userInfo.isGraded());
				stuAssignment.setStudentStatuses(new StudentAssignmentStatuses(
						stuAssignment, probList, null));
				stuAssignment.setStudentName(nameMap.get(uid));
				stuAssignments.add(stuAssignment);
				stuAssignMap.put(uid, stuAssignment);
			}
			probList.add(probDto);
		}
		__logger.info("probList.size(): " + ((probList != null)?probList.size():"NULL"));

		/**
		 * add lesson status for each user/lesson and add assignment status
		 */
		uid = 0;
		String lessonName = "";
		int completed = 0;
		int pending = 0;
		int count = 0;
		int graded = 0;
		int viewed = 0;
		int totGraded = 0;
		int totCompleted = 0;
		int totPending = 0;
		int totCount = 0;
		int totCorrect = 0;
		int totIncorrect = 0;
		int totHalfCredit = 0;
		int totViewed = 0;

		CmList<StudentLessonDto> lessonList = null;
		StudentLessonDto lessonStatus = null;

		for (StudentProblemDto probDto : problemStatuses) {
			if (probDto.getUid() != uid) {
				if (lessonStatus != null) {

					lessonStatus.setStatus(getAssgnLessonStatus(count, completed,
							pending, viewed, graded));
					StudentAssignment sa = stuAssignMap.get(uid);
					if (sa.isGraded() == false) {
                        sa.setStudentDetailStatus(getAssgnLessonStatus(count, completed,
							pending, viewed, graded));
						sa.setHomeworkStatus(getHomeworkStatus(totCount,
								totCompleted, totPending, totGraded, totViewed));
						__logger.info(String.format("studentDetailStatus[%d]: %s", uid, sa.getStudentDetailStatus()));
					} else {
						sa.setHomeworkStatus("Graded");
					}
					sa.setHomeworkGrade(GradeBookUtils.getHomeworkGrade(
							totCount, totCorrect, totIncorrect, totHalfCredit,
							sa.isGraded()));

				}
				lessonName = "";
				totCount = 0;
				totCorrect = 0;
				totIncorrect = 0;
				totCompleted = 0;
				totPending = 0;
				totGraded = 0;
				totViewed = 0;
				totHalfCredit = 0;
			    completed = 0;
			    pending = 0;
			    graded = 0;
			    count = 0;
			    viewed = 0;
				uid = probDto.getUid();
				lessonList = new CmArrayList<StudentLessonDto>();
			}

			if (!lessonName.equals(probDto.getProblem().getName())) {
			    if (lessonName.trim().length() > 0) {
			        if (lessonStatus != null) {
			            lessonStatus.setStatus(getAssgnLessonStatus(count, completed, pending, viewed, graded));
			        }
			    }
			    lessonName = probDto.getProblem().getName();
			    lessonStatus = new StudentLessonDto(probDto.getUid(), lessonName, null);
			    lessonList.add(lessonStatus);
			}

			count++;
			totCount++;
			String probStatus = probDto.getStatus().trim();
			String gradeStatus = probDto.getGradeStatus().trim();

			String gsl = gradeStatus.toLowerCase();
			
			boolean assGraded = assignment.isGraded();
			if ("answered".equals(gsl) || "correct".equals(gsl) ||
				"incorrect".equals(gsl) || "half credit".equals(gsl)) {
				graded += (probDto.isGraded()) ? 1 : 0;
				totGraded += (probDto.isGraded()) ? 1 : 0;
				totCorrect += ("correct".equals(gsl)) ? 1 : 0;
				totIncorrect += ("incorrect".equals(gsl)) ? 1 : 0;
				totHalfCredit += ("half credit".equals(gsl)) ? 1 : 0;
			}
			String psl = probStatus.toLowerCase();
			if ("answered".equals(psl) || "correct".equals(psl) ||
				"incorrect".equals(psl) || "half credit".equals(psl)) {
				completed++;
				totCompleted++;
			}
			else if ("submitted".equals(psl)) {
				pending++;
				totPending++;
			} else if ("viewed".equals(psl)) {
				viewed++;
				totViewed++;
			}
			else {
				// System.out.println("Unknown type: " + psl);
			}
		}

		if (lessonStatus != null) {
			lessonStatus.setStatus(getAssgnLessonStatus(count, completed, pending,
					viewed, graded));
		}
		if (stuAssignMap.size() > 0) {
			StudentAssignment sa = stuAssignMap.get(uid);
			if (sa.isGraded() == false) {
				sa.setStudentDetailStatus(getAssgnLessonStatus(count, completed,
            			pending, viewed, graded));
				sa.setHomeworkStatus(getHomeworkStatus(totCount, totCompleted,
						totPending, totGraded, totViewed));
				__logger.info(String.format("studentDetailStatus[%d]: %s", uid, sa.getStudentDetailStatus()));				
			} else {
				sa.setHomeworkStatus("Graded");
			}
			sa.setHomeworkGrade(GradeBookUtils.getHomeworkGrade(totCount,
					totCorrect, totIncorrect, totHalfCredit, sa.isGraded()));
		}

		__logger.info("getAssignmentGradeBook(): stuAssignments.size(): "
				+ stuAssignments.size());
		if (__logger.isDebugEnabled())
			__logger.debug("getAssignmentGradeBook(): stuAssignments.size(): "
					+ stuAssignments.size());

		/**
		 * Grade was not being set, so I generalized it a bit.
		 * 
		 * Also, really just want an aggregate .. values for each lesson is too
		 * much.
		 * 
		 * 
		 */

		for (StudentAssignment sa : stuAssignments) {
			//setStudentDetailStatus(sa);

			sa.setStudentStatuses(null); // don't need in gradebook, need on
											// student/assignment at time

			// for (StudentProblemDto sd : sa.getAssigmentStatuses()) {
			//
			// /**
			// * Make sure the problem type is set for each student problem.
			// * The information is shared from the assignment base list
			// * because problem type is determined at runtime.
			// *
			// */
			// ProblemDto studentProblem = sd.getProblem();
			// String pid = studentProblem.getPidOnly();
			// for (ProblemDto pd : assignment.getPids()) {
			// if (pd.getPidOnly().equals(pid)) {
			// studentProblem.setProblemType(pd.getProblemType());
			// }
			// }
			// }

		}

		return stuAssignments;
	}

	/**
	 * Returns the personalized pid for this assignment, user or returns null if
	 * not found.
	 * 
	 * @param assignKey
	 * @param uid
	 * @param problemId
	 * @return
	 */
	protected String getPersonalizedPid(int assignKey, int uid, int problemId) {
		String sql = "select pid from CM_ASSIGNMENT_PIDS_USER where assign_key = ? and uid = ? and apid_id = ?";
		List<String> pids = getJdbcTemplate().query(sql,
				new Object[] { assignKey, uid, problemId },
				new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString("pid");
					}
				});

		if (pids.size() > 0) {
			return pids.get(0);
		} else {
			return null;
		}
	}

	protected String getPersonalizedPidStatus(int assignKey, int uid, String pid) {
		String sql = "select status from CM_ASSIGNMENT_PID_STATUS where assign_key = ? and uid = ? and pid = ?";
		List<String> pids = getJdbcTemplate().query(sql,
				new Object[] { assignKey, uid, pid }, new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString("status");
					}
				});

		if (pids.size() > 0) {
			return pids.get(0);
		} else {
			return "Not viewed";
		}
	}

	/**
	 * Replaces token $$LIMIT_TO_SPECIFIED_USERS$$ with sql IN list of specified
	 * users for this assignment.
	 * 
	 * 
	 * @param assignKey
	 * @param sql
	 * @return
	 */
	private String getStudentsInAssignmentSqlRestriction(int assignKey,
			String sql) {
		sql = SbUtilities
				.replaceSubString(
						sql,
						"$$LIMIT_TO_SPECIFIED_USERS$$",
						(hasAnySpecifiedUsers(assignKey) ? " and u.uid in (select uid from CM_ASSIGNMENT_USERS_SPECIFIED where assign_key = "
								+ assignKey + ")"
								: ""));
		return sql;
	}

	/**
	 * does this assignment have ANY specifically specified users?
	 * 
	 * If so, then only those users will have access to the assignment. If no
	 * specific users specified, then all users in group are assigned.
	 * 
	 * 
	 * @param assignKey
	 * @return
	 */
	private boolean hasAnySpecifiedUsers(int assignKey) {
		String sql = "select count(*) from CM_ASSIGNMENT_USERS_SPECIFIED where assign_key = ?";
		int cnt = getJdbcTemplate().queryForObject(sql,
				new Object[] { assignKey }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt(1);
					}
				});
		return cnt > 0;
	}

	public List<Integer> getAssignmentsForGroup(int groupId, Date fromDate,
			Date toDate) {
		List<Integer> assignmentList = new ArrayList<Integer>();

		return assignmentList;
	}

	private void setStudentDetailStatus(StudentAssignment sa) {
		int correct = 0, inCorrect = 0, halfCredit = 0, complete = 0, submitted = 0, viewed = 0;
		for (StudentProblemDto spd : sa.getStudentStatuses()
				.getAssigmentStatuses()) {

			String s = spd.getStatus().toLowerCase();
			if (s.equals("correct")) {
				correct++;
			} else if (s.equals("incorrect")) {
				inCorrect++;
			} else if (s.equals("half credit")) {
				halfCredit++;
			}

			if (s.equals("correct") || s.equals("incorrect")
					|| s.equals("half credit")) {
				complete++;
			}

			if (s.equals("viewed")) {
				viewed++;
			}

			if (s.equals("submitted")) {
				submitted++;
			}

		}
		sa.setHomeworkGrade(GradeBookUtils.getHomeworkGrade(sa
				.getStudentStatuses().getAssigmentStatuses().size(), correct,
				inCorrect, halfCredit, sa.isGraded()));
		sa.setStudentDetailStatus(getLessonStatus(sa.getStudentStatuses()
				.getAssigmentStatuses().size(), complete, submitted, viewed));
	}

	private String getLessonStatus(int count, int completed, int submitted,
			int viewed) {
		String ret;

		completed += submitted;
		if (submitted != 0) {
			ret = String.format("%d of %d submitted, %d ungraded", completed,
					count, submitted);
		} else {
			ret = String.format("%d of %d submitted", completed, count);
		}

		return ret;
	}

	private String getAssgnLessonStatus(int count, int completed, int pending,
			int viewed, int graded) {
		return String.format("%d of %d submitted, %d graded", completed+pending, count, graded);
	}

	private String getHomeworkStatus(int totCount, int totCompleted,
			int totPending, int totGraded, int totViewed) {
		String status = "Not Started";
		if (totGraded > 0 && totGraded == totCount) {
			status = "Graded";
		}
		// TODO: should this include totViewed?
		else if ((totCompleted + totPending + totViewed) > 0) {
			status = ((totCompleted + totPending) < totCount) ? "In Progress"
					: "Ready to Grade";
		}
		return status;
	}

	public List<StudentAssignment> getAssignmentWorkForStudent(int userId)
			throws Exception {
		return getAssignmentWorkForStudent(userId, null, null);
	}

	public List<StudentAssignment> getAssignmentWorkForStudent(int userId,
			Date fromDate, Date toDate) throws Exception {

		if (__logger.isDebugEnabled())
			__logger.debug("in getAssignmentWorkForStudent(" + userId + ")");

		String stuName = null;

		final Map<Integer, Boolean> asgGradedMap = new HashMap<Integer, Boolean>();
		List<StudentProblemExtended> problemStatuses = getProblemStatusesForStudent(
				userId, fromDate, toDate, asgGradedMap);

		/**
		 * create student/assignments for all assignments
		 */
		CmList<StudentAssignment> stuAssignments = new CmArrayList<StudentAssignment>();

		int lastAssignKey = 0;
		CmList<StudentProblemDto> probList = null;
		Map<Integer, StudentAssignment> stuAssignMap = new HashMap<Integer, StudentAssignment>();
		for (StudentProblemDto probDto : problemStatuses) {
			if (probDto.getProblem().getAssignKey() != lastAssignKey) {
				probList = new CmArrayList<StudentProblemDto>();
				lastAssignKey = probDto.getProblem().getAssignKey();
				Assignment assignment = getAssignment(lastAssignKey);
				// StudentAssignment su = getStudentAssignment(probDto.getUid(),
				// lastAssignKey, false);
				Date dateTurnedIn = null;

				boolean isGraded = asgGradedMap.get(lastAssignKey);
				StudentAssignment stuAssignment = new StudentAssignment(
						probDto.getUid(), assignment, dateTurnedIn, isGraded);
				stuAssignment.setStudentStatuses(new StudentAssignmentStatuses(
						stuAssignment, probList, null));
				stuAssignment.setStudentName(stuName);
				stuAssignments.add(stuAssignment);
				stuAssignMap.put(lastAssignKey, stuAssignment);
			}
			probList.add(probDto);
		}

		/**
		 * add lesson status for each lesson add assignment status
		 */
		String lessonName = "";
		int completed = 0;
		int pending = 0;
		int count = 0;
		int viewed = 0;
		int totGraded = 0;
		int totCompleted = 0;
		int totPending = 0;
		int totCount = 0;
		int totCorrect = 0;
		int totHalfCredit = 0;
		int totIncorrect = 0;
		int totViewed = 0;
		lastAssignKey = 0;
		CmList<StudentLessonDto> lessonList = null;
		StudentLessonDto lessonStatus = null;

		for (StudentProblemDto probDto : problemStatuses) {

			if (probDto.getProblem().getAssignKey() != lastAssignKey) {
				if (lessonStatus != null) {
					lessonStatus.setStatus(getLessonStatus(count, completed,
							pending, viewed));

					StudentAssignment sa = stuAssignMap.get(lastAssignKey);
					sa.setProblemCount(totCount);
					sa.setProblemPendingCount(totPending);
					sa.setProblemCompletedCount(totCompleted);
					sa.setHomeworkStatus(getHomeworkStatus(totCount,
							totCompleted, totPending, totGraded, totViewed));
					setStudentDetailStatus(sa);
					sa.setHomeworkGrade(GradeBookUtils.getHomeworkGrade(
							totCount, totCorrect, totIncorrect, totHalfCredit,
							sa.isGraded()));
					if (__logger.isDebugEnabled())
						__logger.debug(String
								.format("getAssignmentWorkForStudent(): totCount: %d, totCompleted: %d, totPending: %d, totGraded: %d, totViewed: %d",
										totCount, totCompleted, totPending,
										totGraded, totViewed));
				}
				lessonName = "";
				totCount = 0;
				totCorrect = 0;
				totIncorrect = 0;
				totCompleted = 0;
				totPending = 0;
				totGraded = 0;
				totViewed = 0;
				totHalfCredit = 0;
				lastAssignKey = probDto.getProblem().getAssignKey();
				lessonList = new CmArrayList<StudentLessonDto>();
				stuAssignMap.get(lastAssignKey).getStudentStatuses()
						.setLessonStatuses(lessonList);
			}

			if (!lessonName.equals(probDto.getProblem().getName())) {
			    if (lessonName.trim().length() > 0) {
			        if (lessonStatus != null) {
			            lessonStatus.setStatus(getLessonStatus(count, completed, pending, viewed));
			        }
			    }
			    completed = 0;
			    pending = 0;
			    count = 0;
			    viewed = 0;
			    lessonName = probDto.getProblem().getName();
			    lessonStatus = new StudentLessonDto(probDto.getUid(), lessonName, null);
			    lessonList.add(lessonStatus);
			}

			count++;
			totCount++;
			
			String probStatus = probDto.getStatus().trim();
			String gradeStatus = probDto.getGradeStatus().trim();

			String gsl = gradeStatus.toLowerCase();
			if ("answered".equals(gsl) || "correct".equals(gsl) ||
				"incorrect".equals(gsl) || "half credit".equals(gsl)) {
				totGraded += (probDto.isGraded()) ? 1 : 0;
				totCorrect += ("correct".equals(gsl)) ? 1 : 0;
				totIncorrect += ("incorrect".equals(gsl)) ? 1 : 0;
				totHalfCredit += ("half credit".equals(gsl)) ? 1 : 0;
			}
			String psl = probStatus.toLowerCase();
			if ("answered".equals(psl) || "correct".equals(psl) ||
				"incorrect".equals(psl) || "half credit".equals(psl)) {
				completed++;
				totCompleted++;
			}
			else if ("submitted".equals(psl)) {
				pending++;
				totPending++;
			} else if ("viewed".equals(psl)) {
				viewed++;
				totViewed++;
			}
			
		}

		if (lessonStatus != null) {
			lessonStatus.setStatus(getLessonStatus(count, completed, pending,
					viewed));
		}
		if (stuAssignMap.size() > 0) {
			StudentAssignment sa = stuAssignMap.get(lastAssignKey);
			sa.setProblemCount(totCount);
			sa.setProblemPendingCount(totPending);
			sa.setProblemCompletedCount(totCompleted);
			sa.setHomeworkStatus(getHomeworkStatus(totCount, totCompleted,
					totPending, totGraded, totViewed));
			setStudentDetailStatus(sa);
			sa.setHomeworkGrade(GradeBookUtils.getHomeworkGrade(totCount,
					totCorrect, totIncorrect, totHalfCredit, sa.isGraded()));
			if (__logger.isDebugEnabled())
				__logger.debug(String
						.format("getAssignmentWorkForStudent(): totCount: %d, totCompleted: %d, totPending: %d, totGraded: %d, totViewed: %d",
								totCount, totCompleted, totPending, totGraded,
								totViewed));
		}

		if (__logger.isDebugEnabled())
			__logger.debug("getAssignmentGradeBook(): stuAssignments.size(): "
					+ stuAssignments.size());

		return stuAssignments;
	}

	/**
	 * Assign students to assignment. Return messages indicating each error s
	 * 
	 * @param assignKey
	 * @param students
	 * @return
	 */
	public AssignmentInfo assignStudents(final int assignKey,
			List<StudentDto> students) {

		int assignCount = 0, errorCount = 0;
		AssignmentInfo assignmentInfo = new AssignmentInfo();
		String messages = null;

		unassignStudentsFromAssignment(assignKey);

		for (final StudentDto s : students) {

			try {
				getJdbcTemplate().update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(
							Connection con) throws SQLException {
						String sql = "insert into CM_ASSIGNMENT_USERS_SPECIFIED(assign_key, uid)values(?,?)";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setInt(1, assignKey);
						ps.setInt(2, s.getUid());
						return ps;
					}
				});

				++assignCount;
			} catch (Exception e) {
				errorCount++;
				if (messages == null) {
					messages = "";
				}
				messages += "Error assigning student (" + s.getName() + "): "
						+ e.getMessage() + "\n";
			}
		}

		markAssignmentHasSpecifiedUsers(assignKey, true);

		assignmentInfo.setAssigned(assignCount);
		assignmentInfo.setErrors(errorCount);
		assignmentInfo.setMessage(messages);
		return assignmentInfo;
	}

	public List<StudentActivityModel> getAssignmentActivityForStudent(int userId)
			throws Exception {
		return getAssignmentActivityForStudent(userId, null, null);
	}

	public List<StudentActivityModel> getAssignmentActivityForStudent(
			int userId, Date fromDate, Date toDate) throws Exception {

		final Map<Integer, Boolean> asgGradedMap = new HashMap<Integer, Boolean>();

		List<StudentProblemExtended> problemStatuses = getProblemStatusesForStudent(
				userId, fromDate, toDate, asgGradedMap);

		problemStatuses = removeUnansweredProblems(problemStatuses);

		sortAssignmentActivity(problemStatuses);

		// for each date, count number of problems submitted
		List<Date> dateList = new ArrayList<Date>();
		List<Integer> countList = new ArrayList<Integer>();
		int index = 0;
		Date curDate = (problemStatuses.size() > 0) ? problemStatuses.get(0)
				.getCreateDate() : null;
		int submittedCount = 0;
		for (StudentProblemExtended prob : problemStatuses) {
			if (curDate.compareTo(prob.getCreateDate()) != 0) {
				dateList.add(index, curDate);
				countList.add(index++, submittedCount);
				submittedCount = 0;
				curDate = prob.getCreateDate();
			}
			submittedCount += 1;
		}
		if (submittedCount > 0) {
			dateList.add(index, curDate);
			countList.add(index, submittedCount);
		}

		// create Activity data
		List<StudentActivityModel> activityList = new ArrayList<StudentActivityModel>();
		index = 0;
		for (Date date : dateList) {
			StudentActivityModel model = new StudentActivityModel();
			model.setProgramDescr("Assignments");
			model.setProgramType("N/A");
			model.setActivity("Solving");
			model.setResult("Submitted " + countList.get(index++));
			model.setUseDate(DATE_FMT.format(date));
			model.setAssignment(true);
			activityList.add(model);
		}

		return activityList;
	}

	private List<StudentProblemExtended> removeUnansweredProblems(
			List<StudentProblemExtended> problemStatuses) {
		List<StudentProblemExtended> keep = new ArrayList<StudentProblemExtended>();
		for (StudentProblemExtended problemStatus : problemStatuses) {
			if (problemStatus.getStatus().toLowerCase().indexOf("viewed") < 0) {
				keep.add(problemStatus);
			}
		}
		return keep;
	}

	/**
	 * sort by date and assign key
	 * 
	 * @param problemStatuses
	 */
	private void sortAssignmentActivity(
			List<StudentProblemExtended> problemStatuses) {
		Collections.sort(problemStatuses,
				new Comparator<StudentProblemExtended>() {

					@Override
					public int compare(StudentProblemExtended o1,
							StudentProblemExtended o2) {
						int rval = o1.getCreateDate().compareTo(
								o2.getCreateDate());
						if (rval == 0) {
							ProblemDto p1 = o1.getProblem();
							ProblemDto p2 = o2.getProblem();
							rval = p1.getAssignKey() - p2.getAssignKey();
						}
						return rval;
					}

				});
	}

	private List<StudentProblemExtended> getProblemStatusesForStudent(
			int userId, Date fromDate, Date toDate,
			final Map<Integer, Boolean> asgGradedMap) throws Exception {
		List<StudentProblemExtended> problemStatuses = new ArrayList<StudentProblemExtended>();
		String dates[] = QueryHelper.getDateTimeRange(fromDate, toDate);
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_ASSIGNMENT_WORK_FOR_STUDENT");

		try {
			problemStatuses = getJdbcTemplate()
					.query(sql,
							new Object[] { dates[0], dates[1], userId, userId,
									userId },
							new RowMapper<StudentProblemExtended>() {
								@Override
								public StudentProblemExtended mapRow(
										ResultSet rs, int rowNum)
										throws SQLException {
									StudentProblemExtended prob = new StudentProblemExtended();
									int uid = rs.getInt("uid");
									prob.setUid(uid);
									ProblemDto probDto = new ProblemDto(rs
											.getInt("ordinal_number"), rs
											.getInt("problem_id"), rs
											.getString("label"), rs
											.getString("pid"), rs
											.getInt("assign_key"));
									prob.setProblem(probDto);
									prob.setStatus(rs.getString("status"));
									prob.setGradeStatus(rs.getString("grade_status"));
									prob.setGraded(rs.getInt("is_graded") > 0);
									prob.setCreateDate(rs
											.getDate("update_datetime"));
									asgGradedMap.put(rs.getInt("assign_key"),
											rs.getInt("assignment_graded") > 0);
									return prob;
								}
							});
		} catch (Exception e) {
			__logger.error("Failed to load problem statuses", e);
			throw e;
		}
		return problemStatuses;
	}

	private void unassignStudentsFromAssignment(final int assignKey) {
		getJdbcTemplate()
				.update("delete from CM_ASSIGNMENT_USERS_SPECIFIED where assign_key = ?",
						new PreparedStatementSetter() {
							@Override
							public void setValues(PreparedStatement ps)
									throws SQLException {
								ps.setInt(1, assignKey);
							}
						});

		markAssignmentHasSpecifiedUsers(assignKey, false);
	}

	public void unassignStudents(final int assKey,
			final CmList<StudentAssignment> students) {
		getJdbcTemplate()
				.batchUpdate(
						"delete from CM_ASSIGNMENT_USERS where assign_key = ? and uid = ?",
						new BatchPreparedStatementSetter() {

							@Override
							public void setValues(PreparedStatement ps, int i)
									throws SQLException {
								ps.setInt(1, assKey);
								ps.setInt(2, students.get(i).getUid());
							}

							@Override
							public int getBatchSize() {
								return students.size();
							}
						});

	}

	public List<GroupDto> getAssignmentGroups(final int aid) {
		List<GroupDto> groups = new ArrayList<GroupDto>();
		try {
			List<GroupInfoModel> groupModels = CmAdminDao.getInstance().getActiveGroups(aid);

			List<Integer> inList = new ArrayList<Integer>();
			for (GroupInfoModel gm : groupModels) {
				inList.add(gm.getId());
			}

			String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_GROUP_INFO");
			sql = QueryHelper.createInListSQL(sql, inList);
			groups.addAll(getJdbcTemplate().query(sql, new Object[] {},
					new RowMapper<GroupDto>() {
						@Override
						public GroupDto mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							String info = "students: "
									+ rs.getInt("student_count")
									+ ", assignments: "
									+ rs.getInt("assignment_count");
							return new GroupDto(rs.getInt("group_id"), rs
									.getString("name"), info, aid);
						}
					}));

		} catch (Exception e) {
			__logger.error("Error getting assignments", e);
		}
		return groups;

	}

	/**
	 * Fills up metaInfo with meta information about this student's assignment
	 * statuses.
	 * 
	 * @param uid
	 * @param metaInfo
	 * @throws Exception
	 */
	private void getAssignmentStatuses(int uid,
			final AssignmentUserInfo metaInfo) throws Exception {

		if (__logger.isDebugEnabled()) {
			__logger.debug("Getting assignment statuses for '" + uid + "'");
		}
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"STUDENT_ASSIGNMENT_STATUS");
		String assignKeyInList = createStudentAssignmentInList(uid);
		String assignmentsSql = "";
		if (assignKeyInList.length() > 0) {
			assignmentsSql = " and a.assign_key in (" + assignKeyInList + ")";
		}
		sql = SbUtilities.replaceSubString(sql, "$$STUDENT_ASSIGN_KEYS$$",
				assignmentsSql);

		getJdbcTemplate().query(sql, new Object[] { uid },
				new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						String status = rs.getString("status");
						if (status.equalsIgnoreCase("open")) {
							metaInfo.setActiveAssignments(rs.getInt("cnt"));
						} else if (status.equalsIgnoreCase("closed")) {
							metaInfo.setClosedAssignments(rs.getInt("cnt"));
						} else if (status.equalsIgnoreCase("past due")) {
							metaInfo.setExpiredAssignments(rs.getInt("cnt"));
						}
						return 0;
					}
				});
	}

	/**
	 * Create an SQL IN list that has all the assignments this student has
	 * access it.
	 * 
	 * This is all assignments in which this user has been specially added.
	 * 
	 * And all the assignments in this group that are 'default'. Meaning they do
	 * not have ANY specific user specified.
	 * 
	 * 'default' assignments are assigned to all students in a group.
	 * 
	 * If no assignments are available to student, then empty string is
	 * returned.
	 * 
	 * 
	 * @param uid
	 * @return
	 */
	private String createStudentAssignmentInList(int uid) {

		/**
		 * Get list of assignments that have been specifically assigned to me.
		 */
		String sql = "select distinct assign_key from CM_ASSIGNMENT_USERS_SPECIFIED where uid = ?";
		List<Integer> assignKeysSpecific = getJdbcTemplate().query(sql,
				new Object[] { uid }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt("assign_key");
					}
				});

		/**
		 * Get a list of assignments that do not have ANY specified users. These
		 * are the assignments that are assigned to ALL users.
		 */
		sql = "select a.assign_key " + "from    CM_ASSIGNMENT a "
				+ "   JOIN HA_USER u " + "     on u.group_id = a.group_id "
				+ "   LEFT JOIN CM_ASSIGNMENT_USERS_SPECIFIED us "
				+ " on us.assign_key = a.assign_key " + " where u.uid = ? "
				+ " and us.assign_key is null ";
		List<Integer> assignKeysDefault = getJdbcTemplate().query(sql,
				new Object[] { uid }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt("assign_key");
					}
				});

		String inList = "";
		assignKeysSpecific.addAll(assignKeysDefault);
		for (Integer ak : assignKeysSpecific) {
			if (inList.length() > 0) {
				inList += ",";
			}
			inList += ak;
		}
		return inList;
	}

	public List<StudentAssignmentInfo> getAssignmentsForUser(final int uid)
			throws Exception {

		/**
		 * Sort so active/not-expired assignments are on top
		 * 
		 */
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_STUDENT_ASSIGNMENTS");
		List<StudentAssignmentInfo> assInfos = getJdbcTemplate().query(sql,
				new Object[] { uid, uid }, new RowMapper<StudentAssignmentInfo>() {
					@Override
					public StudentAssignmentInfo mapRow(ResultSet rs, int rowNum)
							throws SQLException {

						int assignKey = rs.getInt("assign_key");
						if (hasAnySpecifiedUsers(assignKey)) {
							// then only include this assignment
							// if this user has been specifically added
							// to CM_ASSIGNMENT_USERS_SPECIFIED
							if (!isUserSpecificallyAddedToAssignment(uid,
									assignKey)) {
								return null; // skip it.
							}
						}

						int cntSubmitted = rs.getInt("cnt_submitted");
						int cntProblems = rs.getInt("cnt_problems");
						String status = rs.getString("status");
						Date dueDate = rs.getDate("due_date");
						Date turnInDate = rs.getDate("turn_in_date");

						if (status.equals("Open") && turnInDate != null) {
							status = "Turned In";
						}
						boolean isGraded = rs.getInt("is_graded") != 0 ? true
								: false;

						String score = "";
						if (isGraded) {
							try {
								score = getUserScore(uid, assignKey);
								if (score != null && score.equals("-")) {
									String sl = status.toLowerCase();
									score = "0%";
								}

								if (!status.equals("Closed")) {
									status = "Graded";
								}
							} catch (Exception e) {
								__logger.error(
										String.format(
												"Error getting score: uid: %d, assignKey: %d",
												uid, assignKey), e);
							}
						}

						boolean assignmentHasChanged=determineIfAssignmentHasChanged(assignKey, uid);

						StudentAssignmentInfo info = new StudentAssignmentInfo(
								assignKey, uid, isGraded, turnInDate, status,
								dueDate, rs.getString("comments"), cntProblems,
								cntSubmitted, score, assignmentHasChanged);
						return info;
					}
				});

		List<ProblemAnnotation> unreadAnnotations = getUnreadAnnotatedProblems(uid);

		List<StudentAssignmentInfo> assInfosNoNull = new ArrayList<StudentAssignmentInfo>();
		// aggregate the number of unread annotations for each assignment
		for (StudentAssignmentInfo ass : assInfos) {
			if (ass == null) {
				continue;
			}
			assInfosNoNull.add(ass);
			for (ProblemAnnotation a : unreadAnnotations) {
				int cnt = 0;
				if (a.getAssignKey() == ass.getAssignKey()) {
					cnt++;
				}
				ass.setNumUnreadAnnotations(cnt);
			}
		}

		return assInfosNoNull;
	}

	protected boolean isUserSpecificallyAddedToAssignment(int uid, int assignKey) {
		String sql = "select count(*) "
				+ " from    CM_ASSIGNMENT_USERS_SPECIFIED where assign_key = ? and uid = ?";
		int cnt = getJdbcTemplate().queryForObject(sql,
				new Object[] { assignKey, uid }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt(1);
					}
				});
		return cnt > 0;
	}

	/**
	 * has this assignment been 'changed' since the last time this student
	 * 'viewed' it?
	 */
	protected boolean determineIfAssignmentHasChanged(int assignKey, int uid) {
		try {
			StudentAssignmentUserInfo saui = getStudentAssignmentUserInfo(uid,
					assignKey);
			Date lastStudentView = saui.getViewDateTime();

			Date lastAssignmentModification = getLastTeacherAssignmentModification(assignKey);
			if (lastStudentView == null
					|| lastStudentView.getTime() < lastAssignmentModification
							.getTime()) {
				return true;
			}

			Date lastStatusModification = getLastTeacherModfiedStudentStatus(
					assignKey, uid);
			if (lastStatusModification != null) {
				if (lastStudentView.getTime() < lastStatusModification
						.getTime()) {
					return true;
				}
			}
		} catch (Exception e) {
			__logger.error("Error checking of assignment changed: " + assignKey
					+ ", " + uid);
		}

		return false;
	}

	private Date getLastTeacherModfiedStudentStatus(final int assignKey,
			final int uid) {
		String sql = "select max(last_teacher_change_date) from CM_ASSIGNMENT_PID_STATUS where assign_key = ? and uid = ?";
		Date date = getJdbcTemplate().queryForObject(sql,
				new Object[] { assignKey, uid }, new RowMapper<Date>() {
					@Override
					public Date mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getTimestamp(1);
					}
				});
		return date;
	}

	private Date getLastTeacherAssignmentModification(int assignKey)
			throws Exception {
		Assignment ass = getAssignment(assignKey);
		return ass.getModifiedTime();
	}

	public List<StudentAssignmentInfo> getCompletedAssignmentsForUserDateRange(
			final int uid, Date fromDate, Date toDate) throws Exception {

		/**
		 * Sort so active/not-expired assignments are on top
		 * 
		 */
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_COMPLETED_ASSIGNMENTS_IN_DATE_RANGE");
		if (__logger.isDebugEnabled())
			__logger.debug("getCompletedAssignmentsForUserDateRange(): sql: "
					+ sql);
		String[] dates = QueryHelper.getDateTimeRange(fromDate, toDate);
		List<StudentAssignmentInfo> saInfoList = null;
		try {
			saInfoList = getJdbcTemplate().query(sql,
					new Object[] { uid, uid, dates[0], dates[1] },
					new RowMapper<StudentAssignmentInfo>() {
						@Override
						public StudentAssignmentInfo mapRow(ResultSet rs,
								int rowNum) throws SQLException {

							int cntSubmitted = rs.getInt("cnt_submitted");
							int cntProblems = rs.getInt("cnt_problems");
							String status = rs.getString("status");
							Date dueDate = rs.getDate("due_date");
							Date turnInDate = rs.getDate("turn_in_date");

							if (status.equals("Open") && turnInDate != null) {
								status = "Turned In";
							}

							boolean isGraded = (rs.getInt("is_graded") != 0) ? true
									: false;
							int assignKey = rs.getInt("assign_key");
							String score = "";
							if (isGraded) {
								try {
									score = getUserScore(uid, assignKey);
									if (score != null && score.equals("-")) {
										String sl = status.toLowerCase();
										if (sl.equals("closed") || isGraded) {
											score = "0%";
										}
									}
								} catch (Exception e) {
									__logger.error(
											String.format(
													"Error getting score: uid: %d, assignKey: %d",
													uid, assignKey), e);
								}
							}

							/*
							 * TODO: verify that assignment is completed and
							 * cannot be changed
							 */
							boolean assignmentHasChanged = false; // determineIfAssignmentHasChanged(assignKey,
																	// uid);

							StudentAssignmentInfo info = new StudentAssignmentInfo(
									assignKey, uid, isGraded, turnInDate,
									status, dueDate, rs.getString("comments"),
									cntProblems, cntSubmitted, score,
									assignmentHasChanged);
							return info;
						}
					});
		} catch (Exception e) {
			__logger.error("Error getting completed assignments", e);
			throw e;
		}

		List<ProblemAnnotation> unreadAnnotations = getUnreadAnnotatedProblems(uid);

		// aggregate the number of unread annotations for each assignment
		for (StudentAssignmentInfo ass : saInfoList) {
			for (ProblemAnnotation a : unreadAnnotations) {
				int cnt = 0;
				if (a.getAssignKey() == ass.getAssignKey()) {
					cnt++;
				}
				ass.setNumUnreadAnnotations(cnt);
			}
		}

		return saInfoList;
	}

	public List<StudentAssignmentStatus> getCompletedAssignmentsForUsersDateRange(
			List<String> uids, Date fromDate, Date toDate) throws Exception {

		String[] vals = QueryHelper.getDateTimeRange(fromDate, toDate);

		List<StudentAssignmentStatus> list = null;
		try {
			list = getJdbcTemplate().query(
					CmMultiLinePropertyReader.getInstance().getProperty(
							"COMPLETED_ASSIGNMENT_GRADES_IN_DATE_RANGE",
							QueryHelper.createInListMap(QueryHelper
									.createInList(uids))),
					new Object[] { vals[0], vals[1] },
					new RowMapper<StudentAssignmentStatus>() {

						@Override
						public StudentAssignmentStatus mapRow(ResultSet rs,
								int rowNum) throws SQLException {

							int userId = rs.getInt("uid");
							String userName = rs.getString("user_name");
							int assignKey = rs.getInt("assign_key");
							int numProblems = rs.getInt("num_problems");
							int numCorrect = rs.getInt("num_correct");
							int numHalfCredit = rs.getInt("num_halfcredit");
							int numIncorrect = rs.getInt("num_incorrect");

							int score = getHomeworkGradeValue(numProblems,
									numCorrect, numIncorrect, numHalfCredit);
							StudentAssignmentStatus status = new StudentAssignmentStatus(
									userName, userId, score, assignKey,
									numCorrect, numHalfCredit, numCorrect
											+ numHalfCredit + numIncorrect);
							return status;
						}
					});
		} catch (Exception e) {
			__logger.error("Error getting completed assignments", e);
			throw e;
		}
		return list;
	}

	public String getUserScore(int uid, int assignKey) throws Exception {
		final double counts[] = new double[4];
		final int TOTAL = 0, CORRECT = 1, HALFCREDIT = 2, INCORRECT = 3;
		String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_STUDENT_ASSIGNMENT_SCORE");
		getJdbcTemplate().query(sql, new Object[] { assignKey, uid, assignKey, uid },
				new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						String status = rs.getString("status").toLowerCase();
						if (status.equals("correct")) {
							counts[CORRECT] += 1;
						} else if (status.equals("half credit")) {
							counts[HALFCREDIT] += 1;
						} else if (status.equals("incorrect")) {
							counts[INCORRECT] += 1;
						}

						counts[TOTAL]++;
						return 0; // unused
					}
				});

		return GradeBookUtils.getHomeworkGrade((int) counts[TOTAL],
				(int) counts[CORRECT], (int) counts[INCORRECT],
				(int) counts[HALFCREDIT]);
	}

	private int getHomeworkGradeValue(int totCount, int totCorrect,
			int totIncorrect, int totHalfCredit) {

		// add .5 for each half correct answer
		float totCorrectF = (float) totCorrect
				+ (totHalfCredit > 0 ? totHalfCredit / 2 : 0);
		if ((totCorrect + totIncorrect + totHalfCredit) > 0) {
			return Math
					.round(((float) totCorrectF / (float) totCount) * 100.0f);
		}
		return 0;
	}

	private String _createAssignmentName(Date dueDate, String comments) {
		return "Due Date: " + dueDate
				+ (comments != null ? " - " + comments : "");
	}

	/**
	 * Return the assignment for a given student
	 * 
	 * @param uid
	 * @param assignKey
	 * @return
	 * @throws Exception
	 */
	public StudentAssignment getStudentAssignment(final int uid, int assignKey,
			boolean updateViewedTime) throws Exception {

		StudentAssignment studentAssignment = new StudentAssignment();

		final Assignment assignment = getAssignment(assignKey);
		studentAssignment.setAssignment(assignment);

		if (assignment.isPersonalized()) {
			CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
			pids.addAll(getStudentAssignmentProblems(assignKey, uid));
			assignment.setPids(pids);
		}

		StudentAssignmentUserInfo studentInfo = getStudentAssignmentUserInfo(
				uid, assignKey);

		/**
		 * Read list of assignment problems that have statuses for this user
		 * 
		 * 
		 */
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_STUDENT_ASSIGNMENT_PROBLEM_STATUSES");

		final List<StudentProblemDto> problemStatuses = getJdbcTemplate()
				.query(sql,
						new Object[] { assignKey, uid, assignKey, uid,
								assignKey, uid },
						new RowMapper<StudentProblemDto>() {
							@Override
							public StudentProblemDto mapRow(ResultSet rs,
									int rowNum) throws SQLException {
								ProblemDto prob = null;
								String pid = rs.getString("pid");	
								for (ProblemDto p : assignment.getPids()) {
									if (p.getPid().equals(pid)) {
										prob = p;
										break;
									}
								}
								if (prob == null) {
									__logger.warn("Student Problem not found in assignment problems");
									prob = new ProblemDto(0, 0, null, pid, 0);
								}
								boolean hasShowWork = rs
										.getInt("has_show_work") != 0;
								boolean hasShowWorkAdmin = rs
										.getInt("has_show_work_admin") != 0;
								boolean isAssignmentClosed = assignment
										.getStatus().equals("Closed");
								boolean isAssignmentGraded = rs
										.getInt("assignment_is_graded") != 0 ? true
										: false;
								boolean isGraded = rs.getInt("is_graded") != 0 ? true
										: false;
								StudentProblemDto stuProb = new StudentProblemDto(
										uid, prob, rs.getString("status"),
										hasShowWork, hasShowWorkAdmin,
										isAssignmentClosed, isAssignmentGraded,
										isGraded);
								return stuProb;
							}
						});

		boolean isComplete = true;
		for (StudentProblemDto prob : problemStatuses) {

			if (!prob.isGraded()) {

				/**
				 * get last input value for this problem
				 * 
				 */
				sql = "Select * from CM_ASSIGNMENT_PID_ANSWERS where assign_key = ? and user_id = ? and pid = ? order by id desc limit 1";
				List<Boolean> pidAnswers = getJdbcTemplate().query(sql,
						new Object[] { assignKey, uid, prob.getPid() },
						new RowMapper<Boolean>() {
							@Override
							public Boolean mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return rs.getInt("correct") == 0 ? false : true;
							}
						});

				/**
				 * If there is an answer, then update the status accordingly.
				 * 
				 * TODO: remove status from PID_STATUS? is it needed?
				 */
				if (pidAnswers.size() > 0) {
					// only over
					prob.setStatus(pidAnswers.get(0) ? "Correct" : "Incorrect");
				}
			}

			if (!prob.isComplete() && isComplete) {
				isComplete = false;
			}
		}

		/**
		 * Make sure there is a status for each problem in assignment
		 * 
		 */
		CmList<StudentProblemDto> allStatus = new CmArrayList<StudentProblemDto>();

		for (ProblemDto p : assignment.getPids()) {
			boolean found = false;
			for (StudentProblemDto s : problemStatuses) {
				if (s.getProblem().getPid().equals(p.getPid())) {
					s.setProblem(p); // replace with real problem
					allStatus.add(s);
					found = true;
					break;
				}
			}
			if (!found) {
				StudentProblemDto spd = new StudentProblemDto();
				spd.setProblem(p);
				spd.setUid(uid);

				spd.setStatus("Not Viewed");
				allStatus.add(spd);
			}
		}

		studentAssignment.setTurnInDate(studentInfo.getTurnInDate());
		studentAssignment.setStudentStatuses(new StudentAssignmentStatuses(
				studentAssignment, allStatus, null));
		studentAssignment.setStudentName(studentInfo.getName());
		studentAssignment.setUid(studentInfo.getUid());

		studentAssignment.setGraded(studentInfo.isGraded());
		if (studentInfo.isGraded()) {
			// studentAssignment.setHomeworkGrade(GradeBookUtils.getHomeworkGrade(studentAssignment.getStudentStatuses().getAssigmentStatuses()));
			studentAssignment.setHomeworkGrade(getUserScore(uid, assignKey));
		}

		if (updateViewedTime) {
			updateStudentAssignmentLastView(assignKey, uid);
		}

		return studentAssignment;
	}

	/**
	 * Return the existing user info about this assignment or create a new
	 * object
	 * 
	 * 
	 * 
	 * @param uid
	 * @param assignKey
	 * @return
	 */
	public StudentAssignmentUserInfo getStudentAssignmentUserInfo(
			final int uid, final int assignKey) throws Exception {
		StudentAssignmentUserInfo studentInfo = null;

		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_STUDENT_ASSIGNMENT_USER_INFO");
		List<StudentAssignmentUserInfo> assInfos = getJdbcTemplate().query(sql,
				new Object[] { uid, assignKey },
				new RowMapper<StudentAssignmentUserInfo>() {
					@Override
					public StudentAssignmentUserInfo mapRow(ResultSet rs,
							int rowNum) throws SQLException {
						boolean isGraded = rs.getInt("is_graded") != 0 ? true
								: false;
						boolean isEditable = rs.getString("status").equals(
								"Open") ? true : false;
						return new StudentAssignmentUserInfo(uid, rs
								.getString("user_name"), assignKey, rs
								.getDate("turn_in_date"), isGraded, isEditable,
								rs.getTimestamp("last_access"), rs
										.getTimestamp("due_date"), rs
										.getInt("close_past_due") == 0, rs
										.getInt("is_prevent_lesson") != 0);
					}
				});

		if (assInfos.size() > 0) {
			studentInfo = assInfos.get(0);
		} else {
			/**
			 * Create a user record for this assignment
			 * 
			 */

			int cnt = getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					String sql = "insert into CM_ASSIGNMENT_USER(assign_key,uid)values(?,?)";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, assignKey);
					ps.setInt(2, uid);
					return ps;
				}
			});
			if (cnt != 1) {
				__logger.error("Did not add new CM_ASSIGNMENT_USER record");
			}

			/**
			 * make sure there are no problems currently defined for this
			 * user/assignment
			 * 
			 */
			int count = getJdbcTemplate().update(
					new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(
								Connection con) throws SQLException {
							String sql = "delete from CM_ASSIGNMENT_PIDS_USER where uid = ? and assign_key = ?";
							PreparedStatement ps = con.prepareStatement(sql);
							ps.setInt(1, uid);
							ps.setInt(2, assignKey);
							return ps;
						}
					});

			studentInfo = new StudentAssignmentUserInfo(assignKey, uid);
		}
		return studentInfo;
	}

	/**
	 * This reads the problems for the named assignment user.
	 * 
	 * If this assignment is not personalized, then use the main table of
	 * solutions (shared). Otherwise, we need to read from the personalization
	 * table of pids for this user.
	 * 
	 * If the list of personalized pids have not been created then create and
	 * persist
	 * 
	 * @param assignKey
	 * @param uid
	 * @throws Exception
	 */
	private List<ProblemDto> getStudentAssignmentProblems(final int assignKey,
			final int uid) throws Exception {

		String sql = "select apu.assign_key, apu.pid, ap.id, ap.label, ap.lesson, ap.lesson_file, ap.ordinal_number "
				+ " from CM_ASSIGNMENT_PIDS_USER apu "
				+ " JOIN CM_ASSIGNMENT_PIDS ap on ap.id = apu.apid_id "
				+ " where ap.assign_key = ? "
				+ " and apu.uid = ?"
				+ " order by ap.ordinal_number ";
		List<ProblemDto> studentProblems = getJdbcTemplate().query(sql,
				new Object[] { assignKey, uid }, new RowMapper<ProblemDto>() {
					@Override
					public ProblemDto mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new ProblemDto(rs.getInt("ordinal_number"), rs
								.getInt("id"), rs.getString("label"), rs
								.getString("pid"), rs.getInt("assign_key"));
					}
				});
		if (studentProblems.size() > 0) {
			return studentProblems; // already created, just return
		}

		/**
		 * Otherwise, create and persist
		 * 
		 */

		/**
		 * create the set of assignment pids to be used by this user
		 * 
		 */
		Assignment ass = getAssignment(assignKey);
		final List<ProblemDto> personalPids = new ArrayList<ProblemDto>();
		for (ProblemDto problem : ass.getPids()) {
			ProblemDto problemToUse = null;
			if (ass.isPersonalized()) {
				boolean foundOneToUse = false;
				int maxAttempts = 10;
				while (maxAttempts-- > 0) {
					problemToUse = lookupPersonalizedAlternateProblem(problem);

					if (!personalPids.contains(problemToUse)) {
						foundOneToUse = true;
						break;
					} else {
						// try again
						logger.debug("Found duplicate: " + problemToUse);
					}
				}

				if (!foundOneToUse) {
					logger.error("Could not find alternate problem for: "
							+ problem);
				} else {
					personalPids.add(problemToUse);
				}
			} else {
				personalPids.add(problem);
			}
		}

		logger.info("Adding pids to CM_ASSIGNMENT_PIDS_USER: " + personalPids);

		/**
		 * Store these as the Pids to use for this student/assignment
		 * 
		 */
		String sqlPids = "insert into CM_ASSIGNMENT_PIDS_USER(uid, apid_id, assign_key, pid)values(?,?,?,?)";
		getJdbcTemplate().batchUpdate(sqlPids,
				new BatchPreparedStatementSetter() {
					@Override
					public int getBatchSize() {
						return personalPids.size();
					}

					@Override
					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setInt(1, uid);
						ps.setInt(2, personalPids.get(i).getId());
						ps.setInt(3, assignKey);
						ps.setString(4, personalPids.get(i).getPid());
					}
				});

		return personalPids;
	}

	/**
	 * Given a PID figure out what testdef references this pid and return an
	 * alternate/similar problem pid
	 * 
	 * @param testDef
	 * @param pid
	 * @return
	 * @throws Exception
	 */
	private ProblemDto lookupPersonalizedAlternateProblem(ProblemDto problem)
			throws Exception {
		if (problem.getProblemType() == ProblemType.MULTI_CHOICE) {
			// use info about how stored in quiz to extract alternate problem
			HaTestDef testDef = figureOutAssociatedTestDef(problem);
			if (testDef != null) {
				String newPid = ExamDao.getInstance()
						.lookupAlternateProblem_MultiChoice(testDef,
								problem.getPid());

				if (newPid == null) {
					__logger.info("Personalized pid: could not find alternate for '"
							+ problem + "'");
				} else {
					__logger.info("Personalized pid: " + problem.getPid()
							+ " with " + newPid);
					problem.setPid(newPid);
				}
			}
		} else if (problem.getProblemType() == ProblemType.INPUT_WIDGET) {
			String pid = problem.getPid();
			// String newPid = ExamDao.getInstance().get
		}
		return problem;
	}

	/**
	 * Find out which CM program is associated with a given pid
	 * 
	 * Return null if no CM program found.
	 * 
	 * Returns the subject of FIRST program found with PID.
	 * 
	 * 
	 * @param parent
	 * @return
	 */
	private HaTestDef figureOutAssociatedTestDef(ProblemDto problem)
			throws Exception {

		/**
		 * get all records from HA_PROGRAM_LESSONS
		 * 
		 */
		String textCode = new ProblemID(problem.getPid()).getBook();
		String sql = "select test_def_id from HA_TEST_DEF where textcode = ? and is_active = 1";
		List<Integer> testDefs = getJdbcTemplate().query(sql,
				new Object[] { textCode }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt("test_def_id");
					}
				});
		if (testDefs.size() == 0) {
			return null;
		} else {
			if (testDefs.size() > 1) {
				__logger.warn("More than one test_def found for: " + problem);
			}
			Integer testDefId = testDefs.get(testDefs.size() - 1);

			if (testDefId != null) {
				return HaTestDefDao.getInstance().getTestDef(testDefId);
			}
		}

		return null;
	}

	private void updateStudentAssignmentLastView(final int assignKey,
			final int uid) throws Exception {
		final Date viewTime = new Date(System.currentTimeMillis());
		int cnt = getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "update CM_ASSIGNMENT_USER set last_access = ? where assign_key = ? and uid = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setTimestamp(1, new java.sql.Timestamp(viewTime.getTime()));
				ps.setInt(2, assignKey);
				ps.setInt(3, uid);
				return ps;
			}
		});
		if (cnt != 1) {
			__logger.error("Did not update existing CM_ASSIGNMENT_USER record");
		}
	}

	/**
	 * Make sure there is a status record for this assignment PID
	 * 
	 * @param assignKey
	 * @param uid
	 * @param pid
	 */
	public void makeSurePidStatusExists(final int assignKey, final int uid,
			final String pid) {
		try {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					String sql = "insert into CM_ASSIGNMENT_PID_STATUS(assign_key, uid, pid, status,modify_datetime)values(?,?,?,'Viewed',now())";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, assignKey);
					ps.setInt(2, uid);
					ps.setString(3, pid);

					return ps;
				}
			});
		} catch (Exception e) {
			if (e.getMessage().toLowerCase().indexOf("duplicate") > -1) {
				// duplicate record, already been viewed
				// silent
			} else {
				__logger.error("Error setting assignment pid status", e);
			}
		}

		updateAssignmentProblemAccessTime(assignKey, uid, pid);
	}

	/**
	 * Update the time the last time this assignment solution was accessed
	 * 
	 * @param assignKey
	 * @param uid
	 * @param pid
	 */
	public void updateAssignmentProblemAccessTime(final int assignKey,
			final int uid, final String pid) {

		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "update CM_ASSIGNMENT_PID_STATUS set access_datetime = now() where assign_key = ? and uid = ? and pid = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, assignKey);
				ps.setInt(2, uid);
				ps.setString(3, pid);
				return ps;
			}
		});
	}

	/**
	 * Return whiteboard data associated with this named assignment/pid
	 * 
	 * @param uid
	 * @param assignId
	 * @param pid
	 * @return
	 */
	public List<WhiteboardCommand> getWhiteboardData(int uid, int assignId,
			String pid) throws Exception {
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"ASSIGNMENT_WHITEBOARD");
		return getJdbcTemplate().query(sql,
				new Object[] { uid, assignId, pid },
				new RowMapper<WhiteboardCommand>() {
					@Override
					public WhiteboardCommand mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						WhiteboardCommand wCommand = new WhiteboardCommand(rs
								.getString("command"), loadCommandData(rs), rs
								.getInt("is_admin") != 0 ? true : false);
						return wCommand;
					}
				});
	}

	/**
	 * Update the user's problem status for named assignment.
	 * 
	 * Make sure cache is up to date.
	 * 
	 * @param uid
	 * @param assignKey
	 * @param pid
	 * @param status
	 */
	public void saveAssignmentProblemStatus(final int uid, final int assignKey,
			final String pid, final String status) {
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "update CM_ASSIGNMENT_PID_STATUS set modify_datetime = now(), status = ?, last_teacher_change_date = null where uid = ? and assign_key = ? and pid = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setString(1, status);
				ps.setInt(2, uid);
				ps.setInt(3, assignKey);
				ps.setString(4, pid);

				return ps;
			}
		});
		updateEhCacheIfNecessary(assignKey, uid, pid, status);
	}

	/**
	 * Update the EHCache representation of the student/problem/statuses for
	 * this assignment.
	 * 
	 * @param assignKey
	 * @param uid
	 * @param pid
	 * @param status
	 */
	private void updateEhCacheIfNecessary(int assignKey, int uid, String pid,
			String status) {
		List<StudentDto> students = (List<StudentDto>) CmCacheManager
				.getInstance().retrieveFromCache(CacheName.ASSIGNMENT_STUDENTS,
						assignKey);
		if (students != null) {
			for (StudentDto student : students) {
				if (student.getUid() == uid) {
					for (StudentProblemDto problem : student.getProblems()) {
						if (problem.getPid().equals(pid)) {
							problem.setStatus(status);
							return;
						}
					}
				}
			}
		}
	}

	public void saveTutorInputWidgetAnswer(final int uid, final int assignKey,
			final String pid, final String value, final boolean correct) {
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "insert into CM_ASSIGNMENT_PID_ANSWERS(assign_key, user_id, pid, answer, correct, create_datetime)values(?,?,?,?,?,now()) ";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, assignKey);
				ps.setInt(2, uid);
				ps.setString(3, pid);
				ps.setString(4, clearUpString(value));
				ps.setInt(5, correct ? 1 : 0);
				return ps;
			}
		});
	}

	private String clearUpString(String value) {
		if (value != null) {
			value = value.trim().replace("\\n", "");
		}
		return value;
	}

	public void saveWhiteboardData(final Integer uid, final int assignKey,
			final String pid, CommandType commandType,
			final String commandData, final boolean isAdmin) throws Exception {

		if (commandType == CommandType.DELETE) {

			final int rowToDelete = Integer.parseInt(commandData);

			String sqlD = "select whiteboard_id from  CM_ASSIGNMENT_PID_WHITEBOARD where user_id = ? and assign_key = ? and pid = ? order by whiteboard_id limit ?,1";
			final Integer whiteboardId = getJdbcTemplate().queryForObject(sqlD,
					new Object[] { uid, assignKey, pid, rowToDelete },
					new RowMapper<Integer>() {
						@Override
						public Integer mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return rs.getInt("whiteboard_id");
						}
					});

			int countDeleted = getJdbcTemplate().update(
					new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(
								Connection con) throws SQLException {
							String sqlD = "delete from  CM_ASSIGNMENT_PID_WHITEBOARD where whiteboard_id = ?";
							PreparedStatement ps = con.prepareStatement(sqlD);
							ps.setInt(1, whiteboardId);
							return ps;
						}
					});
		} else if (commandType == CommandType.CLEAR) {
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con)
						throws SQLException {
					String sql = "delete from CM_ASSIGNMENT_PID_WHITEBOARD where user_id = ? and assign_key = ? and pid = ?";
					PreparedStatement ps = con.prepareStatement(sql);
					ps.setInt(1, uid);
					ps.setInt(2, assignKey);
					ps.setString(3, pid);
					return ps;
				}
			});
		} else if (commandType == CommandType.UNDO) {

			/**
			 * Delete the newest command
			 * 
			 */

			String sql = "select max(whiteboard_id) as whiteboard_id "
					+ " from CM_ASSIGNMENT_PID_WHITEBOARD "
					+ " where user_id = ? " + " and assign_key = ?"
					+ " and pid = ? ";

			final Integer maxWhiteboardId = getJdbcTemplate().queryForObject(
					sql, new Object[] { uid, assignKey, pid },
					new RowMapper<Integer>() {
						@Override
						public Integer mapRow(ResultSet rs, int rowNum)
								throws SQLException {
							return rs.getInt(1);
						}
					});

			if (maxWhiteboardId != null) {
				getJdbcTemplate().update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(
							Connection con) throws SQLException {
						String sql = "delete from CM_ASSIGNMENT_PID_WHITEBOARD where whiteboard_id = ?";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setInt(1, maxWhiteboardId);
						return ps;
					}
				});
			}

		}

		else {
			try {
				byte[] inBytes = commandData.getBytes("UTF-8");
				final byte[] outBytes = CompressHelper.compress(inBytes);

				getJdbcTemplate().update(new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(
							Connection con) throws SQLException {
						String sql = "insert into CM_ASSIGNMENT_PID_WHITEBOARD(user_id, pid, command, command_data, insert_time_mills, assign_key, is_admin, write_datetime) "
								+ " values(?,?,?,?,?,?,?, now()) ";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setInt(1, uid);
						ps.setString(2, pid);
						ps.setString(3, "draw");

						ps.setBytes(4, outBytes);

						ps.setLong(5, System.currentTimeMillis());
						ps.setInt(6, assignKey);
						ps.setInt(7, isAdmin ? 1 : 0);

						return ps;
					}
				});
			} catch (UnsupportedEncodingException e) {
				__logger.error(
						String.format(
								"Error creating Assignment Whiteboard Data: userId: %d, pid: %s",
								uid, pid), e);
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	/**
	 * Update a Student's Assignment status
	 * 
	 * @param studentAssignment
	 * @param releaseGrades
	 * @return
	 */
	public int[] updateStudentAssignmentStatus(
			StudentAssignment studentAssignment, boolean releaseGrades)
			throws Exception {
		List<StudentProblemDto> list = studentAssignment.getStudentStatuses()
				.getAssigmentStatuses();
		StringBuilder sb = new StringBuilder();
		for (StudentProblemDto sp : list) {
			sb.append(String.format("label: %s, status: %s\n",
					sp.getPidLabel(), sp.getStatus()));
		}
		if (__logger.isDebugEnabled())
			__logger.debug("problem-status: " + sb.toString());

		List<Object[]> batch = new ArrayList<Object[]>();
		for (StudentProblemDto sp : studentAssignment.getStudentStatuses()
				.getAssigmentStatuses()) {
			Object[] values = new Object[] { sp.getStatus(),
					sp.isGraded() ? 1 : 0,
					studentAssignment.getAssignment().getAssignKey(),
					sp.getPid(), studentAssignment.getUid() };
			batch.add(values);

			/** Keep Cache up to date */
			updateEhCacheIfNecessary(studentAssignment.getAssignment()
					.getAssignKey(), studentAssignment.getUid(), sp.getPid(),
					sp.getStatus());
		}
		SimpleJdbcTemplate template = new SimpleJdbcTemplate(
				this.getDataSource());

		String sql = "update CM_ASSIGNMENT_PID_STATUS set status = ?, is_graded = ?, last_teacher_change_date = now() where assign_key = ? and pid = ? and uid = ?";
		int[] updateCounts = template.batchUpdate(sql, batch);

		if (releaseGrades) {
			markAssignmentAsGraded(studentAssignment.getUid(),
					studentAssignment.getAssignment().getAssignKey());
		}

		return updateCounts;
	}

	public Collection<? extends LessonDto> getAvailableLessons() {
		String sql = "select distinct lesson, file from HA_PROGRAM_LESSONS_static where subject > '' order by lesson, subject";
		List<LessonDto> problems = getJdbcTemplate().query(sql,
				new Object[] {}, new RowMapper<LessonDto>() {
					@Override
					public LessonDto mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new LessonDto(0, 0, null,
								rs.getString("lesson"), rs.getString("file"));
					}
				});
		return problems;
	}

	public String getAssignmentLastWidgetInputValue(int uid, int assignKey,
			String pid) {
		String sql = "select * from  CM_ASSIGNMENT_PID_ANSWERS where user_id = ? and assign_key = ? and pid = ? order by id desc";
		List<String> values = getJdbcTemplate().query(sql,
				new Object[] { uid, assignKey, pid }, new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString("answer");
					}
				});

		String value = null;
		if (values.size() > 0) {
			value = values.get(0);
		}
		return value;
	}

	public void closeAssignment(final int uid, final int assignKey) {
		int cnt = getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "update CM_ASSIGNMENT set status = 'Closed' where assign_key = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, assignKey);
				return ps;
			}
		});

		if (cnt != 1) {
			__logger.debug("Assignment not closed: " + uid + ", " + assignKey);
		}
	}

	/**
	 * Save the solution context for this assignment problem.
	 * 
	 * First make sure to remove any existing variables, then insert.
	 * 
	 * @param uid
	 * @param assignKey
	 * @param pid
	 * @param variables
	 */
	public void saveAssignmentSolutionContext(final int uid,
			final int assignKey, final String pid, final String variables) {

		/** Delete first */
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "delete from CM_ASSIGNMENT_PID_CONTEXT where uid = ? and assign_key = ? and pid = ?";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, uid);
				ps.setInt(2, assignKey);
				ps.setString(3, pid);
				return ps;
			}
		});

		/** Then always insert */
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con)
					throws SQLException {
				String sql = "insert into CM_ASSIGNMENT_PID_CONTEXT(uid, assign_key, pid, variables, time_viewed)values(?,?,?,?,now())";
				PreparedStatement ps = con.prepareStatement(sql);
				ps.setInt(1, uid);
				ps.setInt(2, assignKey);
				ps.setString(3, pid);
				ps.setString(4, variables);

				return ps;
			}
		});
	}

	public String getSolutionContext(int uid, int assignKey, String pid) {
		String sql = "select * from  CM_ASSIGNMENT_PID_CONTEXT where uid = ? and assign_key = ? and pid = ?";
		List<String> values = getJdbcTemplate().query(sql,
				new Object[] { uid, assignKey, pid }, new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString("variables");
					}
				});

		String value = null;
		if (values.size() > 0) {
			value = values.get(0);
		}
		return value;
	}

	/**
	 * Make a copy of Assignment pointed to by assKey
	 * 
	 * @param assKey
	 */
	public String copyAssignment(int assKey) throws Exception {
		Assignment assignmentCopy = getAssignment(assKey);
		assignmentCopy.setAssignKey(0);
		String copyTag = " - copy:  " + new java.util.Date();
		assignmentCopy.setAssignmentName(assignmentCopy.getAssignmentName()
				+ copyTag);
		assignmentCopy.setComments(assignmentCopy.getComments() + copyTag);
		assignmentCopy.setStatus("Draft");

		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = cal.getTime();
		assignmentCopy.setDueDate(tomorrow);

		saveAssignment(assignmentCopy);

		return assignmentCopy.getAssignmentName();
	}

	/**
	 * Extract current use stats for named assignment
	 * 
	 * @param aid
	 * @param assKey
	 * @return
	 */
	public AssignmentStatus getAssignmentStatus(int aid, int assKey) {
		String sql = "select count(*) from CM_ASSIGNMENT_PID_STATUS where assign_key = ?";
		List<Integer> cnt = getJdbcTemplate().query(sql,
				new Object[] { assKey }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt(1);
					}
				});

		return new AssignmentStatus(cnt.get(0) > 0);
	}

	public List<GroupCopyModel> getGroupAssignments(int adminId)
			throws Exception {
		return getJdbcTemplate().query(
				CmMultiLinePropertyReader.getInstance().getProperty(
						"GET_GROUP_ASSIGNMENTS_ALL"), new Object[] { adminId },
				new RowMapper<GroupCopyModel>() {
					@Override
					public GroupCopyModel mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new GroupCopyModel(rs.getString("group_name"),
								rs.getString("assignment_name"), rs
										.getInt("assignment_key"));
					}
				});
	}

	public Assignment importAssignment(int aid, int groupToImportInto,
			int assignmentToImport) throws Exception {
		Assignment assignmentCopy = getAssignment(assignmentToImport);

		assignmentCopy.setAssignKey(0);
		String copyTag = " - imported:  " + new java.util.Date();
		assignmentCopy.setAssignmentName(assignmentCopy.getAssignmentName()
				+ copyTag);
		assignmentCopy.setComments(assignmentCopy.getComments() + copyTag);
		assignmentCopy.setStatus("Draft");
		assignmentCopy.setGroupId(groupToImportInto);

		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		cal.add(Calendar.DAY_OF_YEAR, 1);
		Date tomorrow = cal.getTime();
		assignmentCopy.setDueDate(tomorrow);

		saveAssignment(assignmentCopy);

		return assignmentCopy;
	}

	public void activateAssignment(final int assignKey) throws Exception {
		Assignment assignment = getAssignment(assignKey);
		assignment.setStatus("Open");
		saveAssignment(assignment);

		/**
		 * mark all students as not graded
		 */
		int cntUngraded = getJdbcTemplate().update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(
							Connection con) throws SQLException {
						String sql = "update CM_ASSIGNMENT_USER set is_graded = 0 where assign_key = ?";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setInt(1, assignKey);
						return ps;
					}
				});

		__logger.info("Activated assignment: " + assignKey
				+ ", students ungraded: " + cntUngraded);
	}

	private List<ProblemAnnotation> getUnreadAnnotatedProblems(final int uid)
			throws Exception {
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"ASSIGNMENT_UNREAD_ANNOTATED_PROBLEMS");
		List<ProblemAnnotation> pids = getJdbcTemplate().query(sql,
				new Object[] { uid }, new RowMapper<ProblemAnnotation>() {
					@Override
					public ProblemAnnotation mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new ProblemAnnotation(rs.getInt("assign_key"),
								rs.getString("pid"));
					}
				});
		return pids;
	}

	/**
	 * Return information about this student's assignment use.
	 * 
	 * @param uid
	 * @return
	 * @throws Exception
	 */
	public AssignmentUserInfo getStudentAssignmentMetaInfo(int uid)
			throws Exception {
		AssignmentUserInfo assignmentInfo = new AssignmentUserInfo();
		if (isAdminUsingAssignmentsAtAll(uid)) {
			assignmentInfo.setAdminUsingAssignments(true);
			getAssignmentStatuses(uid, assignmentInfo);
			assignmentInfo
					.setUnreadAnnotations(getUnreadAnnotatedProblems(uid));
			assignmentInfo.setChanged(determineIfAnyAssignmentHasChanged(uid));

			assignmentInfo
					.setLastChanged(getLastTimeUserAssignmentsUpdated(uid));
		}
		return assignmentInfo;
	}

	private Date getLastTimeUserAssignmentsUpdated(int uid) throws Exception {
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"STUDENT_ASSIGNMENT_LAST_MODIFIED");
		Date lastModified = getJdbcTemplate().queryForObject(sql,
				new Object[] { uid }, new RowMapper<Date>() {
					@Override
					public Date mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getTimestamp(1);
					}
				});
		return lastModified;
	}

	private boolean determineIfAnyAssignmentHasChanged(final int uid) {
		String sql = "select assign_key from CM_ASSIGNMENT a JOIN HA_USER u on u.group_id = a.group_id where u.uid = ? and a.status <> 'Draft'";
		List<Integer> assignKeys = getJdbcTemplate().query(sql,
				new Object[] { uid }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						int assignKey = rs.getInt("assign_key");
						return assignKey;
					}
				});
		for (Integer ak : assignKeys) {
			if (determineIfAssignmentHasChanged(ak, uid)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Return true if this admin is using assignments at all.
	 * 
	 * Meaning there are zero assignments created.
	 * 
	 * @param uid
	 * @return
	 */
	private boolean isAdminUsingAssignmentsAtAll(int uid) {
		String sql = "select count(*) " + " from    CM_ASSIGNMENT a "
				+ " JOIN HA_USER u on u.admin_id = a.aid " + " and u.uid = ?";
		Integer countOfAssignments = getJdbcTemplate().queryForObject(sql,
				new Object[] { uid }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt(1);
					}
				});
		return countOfAssignments > 0;
	}

	/**
	 * Mark the assignment as graded if not currently marked as so.
	 * 
	 * @param assignKey
	 */
	public void markAssignmentAsGraded(int assignKey)
			throws AssignmentNotFoundException {
		Assignment assignment = getAssignment(assignKey);
		if (!assignment.isGraded()) {
			assignment.setGraded(true);
			saveAssignment(assignment);
		}
	}

	public void releaseGradesForAssignment(int assignKey) throws Exception {
		Assignment ass = getAssignment(assignKey);
		ass.setGraded(true);
		saveAssignment(ass);

		// for each student in assignment
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"RELEASE_ASSIGNMENT_GRADE");
		List<Integer> uids = getJdbcTemplate().query(sql,
				new Object[] { ass.getGroupId() }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt("uid");
					}
				});

		// set user record marking as graded
		for (Integer uid : uids) {
			markAssignmentAsGraded(uid, assignKey);
		}
	}

	public void markAssignmentAsGraded(final int uid, final int assignKey)
			throws Exception {
		int cntUpdate = getJdbcTemplate().update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(
							Connection con) throws SQLException {
						String sql = "update CM_ASSIGNMENT_USER set is_graded = 1 where uid = ? and assign_key = ?";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setInt(1, uid);
						ps.setInt(2, assignKey);
						return ps;
					}
				});
		if (cntUpdate == 0) {
			/**
			 * does not exist, insert new record
			 * 
			 */
			int cntInsert = getJdbcTemplate().update(
					new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(
								Connection con) throws SQLException {
							String sql = "insert into CM_ASSIGNMENT_USER(uid, assign_key, is_graded)values(?,?,1)";
							PreparedStatement ps = con.prepareStatement(sql);
							ps.setInt(1, uid);
							ps.setInt(2, assignKey);
							return ps;
						}
					});
			if (cntInsert != 1) {
				throw new CmException("Could not mark assignment as graded: "
						+ uid + ", " + assignKey);
			}
		}
	}

	/**
	 * Turn in this assignment for student. Make an entry in the user's
	 * CM_ASSIGNMENT_USER record... create if it does not exist
	 * 
	 * If auto_release_grades is turn, then release grades for this student
	 * 
	 * 
	 * @param uid
	 * @param assignKey
	 */
	public void turnInAssignment(final int uid, final int assignKey)
			throws Exception {
		int cntUpdate = getJdbcTemplate().update(
				new PreparedStatementCreator() {
					@Override
					public PreparedStatement createPreparedStatement(
							Connection con) throws SQLException {
						String sql = "update CM_ASSIGNMENT_USER set turn_in_date = now() where uid = ? and assign_key = ?";
						PreparedStatement ps = con.prepareStatement(sql);
						ps.setInt(1, uid);
						ps.setInt(2, assignKey);
						return ps;
					}
				});
		if (cntUpdate == 0) {
			/**
			 * does not exist, insert new record
			 * 
			 */
			int cntInsert = getJdbcTemplate().update(
					new PreparedStatementCreator() {
						@Override
						public PreparedStatement createPreparedStatement(
								Connection con) throws SQLException {
							String sql = "insert into CM_ASSIGNMENT_USER(uid, assign_key, turn_in_date)values(?,?,now())";
							PreparedStatement ps = con.prepareStatement(sql);
							ps.setInt(1, uid);
							ps.setInt(2, assignKey);
							return ps;
						}
					});
			if (cntInsert != 1) {
				throw new CmException("Could not turn in assignment: " + uid
						+ ", " + assignKey);
			}
		}

		/**
		 * see if auto release grade is enabled for this assignment
		 * 
		 * no need to create a new full Assignment object.
		 */
		String sql = "select auto_release_grades from CM_ASSIGNMENT where assign_key = ?";
		Boolean isAutoGrade = getJdbcTemplate().queryForObject(sql,
				new Object[] { assignKey }, new RowMapper<Boolean>() {
					@Override
					public Boolean mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new Boolean(
								rs.getInt("auto_release_grades") != 0 ? true
										: false);
					}
				});
		if (isAutoGrade) {
			markAssignmentAsGraded(uid, assignKey);
		}
	}

	public int getCountUngradedWhiteboardProblems(int assignKey)
			throws Exception {
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_COUNT_UNGRADED_WHITEBOARD_PROBLEMS");
		Integer count = getJdbcTemplate().queryForObject(sql,
				new Object[] { assignKey }, new RowMapper<Integer>() {
					@Override
					public Integer mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getInt(1);
					}
				});
		return count;
	}

	/**
	 * Return the student problem associated with the named assignment and pid
	 * 
	 * returns null if problem does not exist in assignment
	 * 
	 * This is complicated due to a student's assignment problems might be from
	 * either the shared table CM_ASSIGNMENT_PIDS or if they are personalized
	 * they are in the CM_ASSIGNMENT_PIDS_USER.
	 * 
	 * @param uid
	 * @param assignKey
	 * @param pid
	 * @return
	 */
	public StudentProblemDto getStudentProblem(final int uid,
			final int assignKey, final String pid) throws Exception {

		// List<ProblemDto> p = getStudentAssignmentProblems(assignKey, uid);

		Boolean isPersonalized = getJdbcTemplate()
				.queryForObject(
						"select is_personalized from CM_ASSIGNMENT where assign_key = ? ",
						new Object[] { assignKey }, new RowMapper<Boolean>() {
							@Override
							public Boolean mapRow(ResultSet rs, int rowNum)
									throws SQLException {
								return rs.getInt("is_personalized") != 0 ? true
										: false;
							}
						});

		String sql = null;
		if (isPersonalized) {
			sql = CmMultiLinePropertyReader.getInstance().getProperty(
					"GET_STUDENT_ASSIGNMENT_PROBLEM");
		} else {

			sql = "select a.status as assignment_status, p.ordinal_number, p.id, p.lesson, p.lesson_file, p.label, s.status as problem_status,s.is_graded "
					+ " from CM_ASSIGNMENT a "
					+ " JOIN  CM_ASSIGNMENT_PIDS p on p.assign_key = a.assign_key "
					+ " LEFT JOIN CM_ASSIGNMENT_PID_STATUS s on s.assign_key = p.assign_key and s.uid = ? and s.pid = p.pid "
					+ " where p.assign_key = ? " + " and     p.pid = ? ";
		}

		List<StudentProblemDto> problems = getJdbcTemplate().query(sql,
				new Object[] { uid, assignKey, pid },
				new RowMapper<StudentProblemDto>() {
					@Override
					public StudentProblemDto mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						ProblemDto prob = new ProblemDto(rs
								.getInt("ordinal_number"), rs.getInt("id"), rs
								.getString("label"), pid, 0);
						boolean isClosed = rs.getString("assignment_status")
								.equals("Closed");

						List<ProblemDto> problems = new ArrayList<ProblemDto>();
						problems.add(prob);
						updateProblemTypes(problems);
						boolean isGraded = rs.getInt("is_graded") != 0 ? true
								: false;
						return new StudentProblemDto(uid, prob, rs
								.getString("problem_status"), false, false,
								isClosed, isGraded, isGraded);
					}
				});
		if (problems.size() > 0) {
			return problems.get(0);
		} else {
			throw new CmRpcException("Assignment Problem not found: " + pid);
			// return null;
		}
	}

	public ProblemDto getAssignmentProblem(int assignKey, final String pid)
			throws Exception {
		String sql = "select * from CM_ASSIGNMENT_PIDS where assign_key = ? and pid = ?";
		ProblemDto problem = getJdbcTemplate().queryForObject(sql,
				new Object[] { assignKey, pid }, new RowMapper<ProblemDto>() {
					@Override
					public ProblemDto mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new ProblemDto(rs.getInt("ordinal_number"), rs
								.getInt("id"), rs.getString("label"), pid, 0);
					}
				});
		return problem;
	}

	public boolean isAssignmentGraded(int uid, int assignKey) throws Exception {
		return getStudentAssignmentUserInfo(uid, assignKey).isGraded();
	}

	public String getProblemStatusForStudent(int uid, int assignKey, String pid) {
		String sql = "select status from CM_ASSIGNMENT_PID_STATUS where uid = ? and assign_key = ? and pid = ?";
		List<String> statuses = getJdbcTemplate().query(sql,
				new Object[] { uid, assignKey, pid }, new RowMapper<String>() {
					@Override
					public String mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getString(1);
					}
				});
		if (statuses.size() > 0) {
			return statuses.get(0);
		} else {
			return ProblemStatus.UNANSWERED.toString();
		}
	}

	/**
	 * Return either all the students that are assignable to the assignment or
	 * the users that are specifically assigned.
	 * 
	 * @param assignKey
	 * @param type
	 * @return
	 */
	public List<StudentDto> getStudentsInAssignment(int assignKey, TYPE type) {
		String sql = null;
		switch (type) {
		case ALL_IN_GROUP:
			sql = " select uid,user_name "
					+ " from   HA_USER u "
					+ " join CM_ASSIGNMENT a on (a.group_id = u.group_id and a.aid = u.admin_id) "
					+ " where a.assign_key = ? "
					+ " and is_auto_create_template = 0 "
					+ " and is_active = 1 " + " order  by Lower(user_name)  ";
			break;

		case ASSIGNED:
			sql = "select u.uid, user_name from HA_USER u JOIN CM_ASSIGNMENT_USERS_SPECIFIED us on us.uid = u.uid where assign_key = ? and is_auto_create_template = 0 order by lower(user_name)";
			break;
		}
		List<StudentDto> students = getJdbcTemplate().query(sql,
				new Object[] { assignKey }, new RowMapper<StudentDto>() {
					@Override
					public StudentDto mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new StudentDto(rs.getInt("uid"), rs
								.getString("user_name"));
					}
				});
		return students;
	}

	private String loadCommandData(ResultSet rs) throws SQLException {
		byte[] compressed = rs.getBytes("command_data");
		try {
			if (compressed != null && compressed[0] != "{".getBytes("UTF-8")[0]) {
				return CompressHelper.decompress(compressed);
			} else {
				return rs.getString("command_data");
			}
		} catch (Exception e) {
			throw new SQLException(e.getLocalizedMessage());
		}
	}

	public AssignmentRealTimeStats getAssignmentStats(int assignKey)
			throws Exception {
		try {
			Assignment ass = getAssignment(assignKey);

			List<StudentDto> assStudent = getStudentsInAssignment(assignKey);

			/**
			 * For each each 'base' pid assigned, find the percentage correct
			 * 
			 */

			/**
			 * get list of base pids the admin selected
			 * 
			 */
			CmList<ProblemDto> basePids = ass.getPids();
			List<PidStats> pidStats = new ArrayList<PidStats>();
			for (ProblemDto baseProb : basePids) {
				String basePid = baseProb.getPid();
				double correctCount = 0.0;
				for (StudentDto student : assStudent) {

					try {
						for (StudentProblemDto studentProblem : student
								.getProblems()) {
							if (studentProblem.getBasePid().equals(basePid)) {
								String status = studentProblem.getStatus();
								if (status.equals("Correct")) {
									correctCount += 1;
								} else if (status.equals("Half Correct")) {
									correctCount += .5;
								}
							}
						}
					} catch (Exception ee) {
						ee.printStackTrace();
					}
				}

				int percent = 0;
				if (correctCount > 0) {
					percent = Math
							.round(((float) correctCount / (float) assStudent
									.size()) * 100.0f);
				}

				pidStats.add(new PidStats(basePid, percent));
			}
			return new AssignmentRealTimeStats(pidStats);
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * Get all students in assignment, along with their assigned pids and
	 * statuses
	 * 
	 * @param assignKey
	 * @return
	 * @throws Exception
	 */
	public List<StudentDto> getStudentsInAssignment(final int assignKey)
			throws Exception {

		List<StudentDto> students = (List<StudentDto>) CmCacheManager
				.getInstance().retrieveFromCache(CacheName.ASSIGNMENT_STUDENTS,
						assignKey);
		if (students != null) {
			// updateStudentStatuses(assignKey, students); // not needed,
			// because cache object is updated during save of status
			return students;
		}

		/**
		 * Get all students currently assigned to this assignment
		 * 
		 */
		String sql = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_ASSIGNMENT_STUDENTS");
		students = getJdbcTemplate().query(sql, new Object[] { assignKey },
				new RowMapper<StudentDto>() {
					@Override
					public StudentDto mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new StudentDto(rs.getInt("uid"), rs
								.getString("user_name"));
					}
				});

		/**
		 * For each student assigned, get all problems in assignment.
		 * 
		 */
		String sqlPids = CmMultiLinePropertyReader.getInstance().getProperty(
				"GET_STUDENT_PIDS");
		String sqlStatus = "select * " + " from CM_ASSIGNMENT_PID_STATUS "
				+ " where assign_key = ? and uid = ? and pid = ? ";

		Connection conn = null;
		PreparedStatement psPids = null, psStatus = null;
		try {
			conn = getJdbcTemplate().getDataSource().getConnection();
			psPids = conn.prepareStatement(sqlPids);
			psStatus = conn.prepareStatement(sqlStatus);
			for (StudentDto s : students) {
				psPids.setInt(1, assignKey);
				psPids.setInt(2, assignKey);
				psPids.setInt(3, s.getUid());
				ResultSet rs = psPids.executeQuery();
				while (rs.next()) {
					ProblemDto problem = new ProblemDto(rs.getInt("ordinal"),
							0, rs.getString("label"), rs.getString("pid"),
							assignKey);
					psStatus.setInt(1, assignKey);
					psStatus.setInt(2, s.getUid());
					psStatus.setString(3, problem.getPid());
					ResultSet rsStatus = psStatus.executeQuery();
					String status = null;
					if (rsStatus.first()) {
						status = rsStatus.getString("status");
					}
					StudentProblemDto studentproblem = new StudentProblemDto(
							s.getUid(), problem, status, false, false, false,
							false, false);
					studentproblem.setBasePid(rs.getString("pid_real"));
					s.getProblems().add(studentproblem);
				}

				if (s.getProblems().size() == 0) {
					// user has not viewed assignment yet
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			SqlUtilities.releaseResources(null, psPids, null);
			SqlUtilities.releaseResources(null, psStatus, conn);
		}

		CmCacheManager.getInstance().addToCache(CacheName.ASSIGNMENT_STUDENTS,
				assignKey, students);

		return students;
	}

	static public void main(String as[]) {
		try {
			AssignmentDao ad = AssignmentDao.getInstance();
			ProblemDto problem = new ProblemDto(0, 0, "test",
					"prealgptests_1_2_chapter1practicetest_2_1", 0);
			problem.setProblemType(ProblemType.MULTI_CHOICE);
			ProblemDto res = ad.lookupPersonalizedAlternateProblem(problem);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public AssignmentRealTimeStatsUsers getAssignmentProblemStatsUsers(
			int assignKey, String pid) throws Exception {
		List<StudentDto> students1 = getStudentsInAssignment(assignKey);
		List<StudentAssignmentProblemStat> studentStatus = new ArrayList<StudentAssignmentProblemStat>();
		for (StudentDto student : students1) {
			StudentAssignmentProblemStat stat = new StudentAssignmentProblemStat(
					student.getUid(), student.getName(), null);
			for (StudentProblemDto studentProblem : student.getProblems()) {
				if (studentProblem.getBasePid().equals(pid)) {
					stat.setStatus(studentProblem.getStatus());
					break;
				}
			}
			studentStatus.add(stat);
		}
		return new AssignmentRealTimeStatsUsers(studentStatus);
	}

	public List<LessonModel> getLessonsCorrelatedToCustomProblem(String pid) throws Exception {
		List<String> pidList = new ArrayList<String>();
		pidList.add(pid);
		return getLessonsCorrelatedToCustomProblemList(pidList);
	}

	String LINKED_LESSONS_FOR_PID_LIST =
		"select * from CM_CUSTOM_PROBLEM_LINKED_LESSONS where pid in ($$PID_LIST$$)";

	public List<LessonModel> getLessonsCorrelatedToCustomProblemList(List<String> pidList) throws Exception {

		String sql = QueryHelper.createInListSQL(LINKED_LESSONS_FOR_PID_LIST, "$$PID_LIST$$",  pidList);
		List<LessonModel> lessons = getJdbcTemplate().query(sql,
				new RowMapper<LessonModel>() {
					@Override
					public LessonModel mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return new LessonModel(rs.getString("lesson_name"), rs
								.getString("lesson_file"));
					}
				});

		return lessons;

	}

	public String getLessonTitle(final Connection conn, String file)
			throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "select lesson from HA_PROGRAM_LESSONS_static where file = ? limit 1";
			ps = conn.prepareStatement(sql);
			ps.setString(1, file);
			ResultSet rs = ps.executeQuery();
			if (!rs.first())
				throw new Exception("No title found for lesson '" + file + "'");
			return rs.getString("lesson");
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	public List<AssignmentModel> getAssignmentsWithPid(String pid) {
		String sql = 
				"select a.assign_key, a.name, a.comments, g.id as group_id, g.name as group_name" +
				" from  CM_ASSIGNMENT_PIDS p" +
				"   JOIN CM_ASSIGNMENT a on a.assign_key = p.assign_key" +
				"   JOIN CM_GROUP g on g.id = a.group_id" +
				" where p.pid = ?";
		
		List<AssignmentModel> assignments = getJdbcTemplate().query(sql,
				new Object[] { pid }, new RowMapper<AssignmentModel>() {
					@Override
					public AssignmentModel mapRow(ResultSet rs, int rowNum)	throws SQLException {
						GroupModel group = new GroupModel(rs.getInt("group_id"), rs.getString("group_name"), null);
						return new AssignmentModel(rs.getInt("assign_key"), rs.getString("name"), rs.getString("comments"), group);
					}
				});

		return assignments;		
	}

	
	/** create a distinct set of resources for this 
	 *  assignment.
	 *  
	 *  only return video and review resources for now.
	 * 
	 * @param assignment
	 * @return
	 * @throws Exception
	 */
    public List<PrescriptionSessionDataResource> getAssigmentResources(Assignment assignment) throws Exception {
        
        List<PrescriptionSessionDataResource> resources = new ArrayList<PrescriptionSessionDataResource>();
        PrescriptionSessionDataResource videos = new PrescriptionSessionDataResource();
        videos.setType(CmResourceType.VIDEO);
        PrescriptionSessionDataResource reviews = new PrescriptionSessionDataResource();
        reviews.setType(CmResourceType.REVIEW);

        resources.add(videos);
        resources.add(reviews);
        
        for(ProblemDto pid: assignment.getPids()) {
            GetCorrelatedTopicsPrescriptionAction action = new GetCorrelatedTopicsPrescriptionAction(pid.getPid());
            CmList<PrescriptionSessionResponse> results = ActionDispatcher.getInstance().execute(action);

            // for each resource in lesson type
            for(PrescriptionSessionResponse res: results) {
                
                // for each resource in type
                for(PrescriptionSessionDataResource lesRes: res.getPrescriptionData().getCurrSession().getInmhResources()) {
                    
                    // for each resource that we want to have returned
                    for(PrescriptionSessionDataResource mainResource: resources) {
                        
                        // for each resource being returned
                        if(mainResource.getType().equals(lesRes.getType())) {
                            // return distinct resources of this type
                            
                            // for each potential item in resource type
                            for(InmhItemData id: lesRes.getItems()) {
                                
                                // if not already in list, add it
                                boolean alreadyIn=false;
                                for(InmhItemData dr: mainResource.getItems()) {
                                    // already in list?
                                    if(dr.getFile().equals(id.getFile())) {
                                        alreadyIn=true;
                                        break;
                                    }
                                }
                                if(!alreadyIn) {
                                    mainResource.getItems().add(id);
                                }
                           }
                        }
                    }
                }
            }
        }
        
        return resources;
    }
}
