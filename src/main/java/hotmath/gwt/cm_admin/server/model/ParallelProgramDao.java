package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.JsonUtil;
import hotmath.gwt.cm_rpc.client.model.CmParallelProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgram;
import hotmath.gwt.cm_rpc.client.model.CmProgramAssign;
import hotmath.gwt.cm_rpc.client.model.CmProgramInfo;
import hotmath.gwt.cm_rpc.client.model.CmProgramType;
import hotmath.gwt.cm_rpc.client.model.StudentActiveInfo;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.model.ParallelProgramUsageModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

/**
 * <codeParallelProgramDao</code> supports Parallel Programs
 * 
 * @author bob
 *
 */

public class ParallelProgramDao extends SimpleJdbcDaoSupport {

	private static final Logger LOGGER = Logger.getLogger(ParallelProgramDao.class);
	
    static private ParallelProgramDao __instance;

    static public ParallelProgramDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (ParallelProgramDao)SpringManager.getInstance().getBeanFactory().getBean("parallelProgramDao");
        }
        return __instance;
    }
	
    public boolean isParallelProgramStudent(final Integer parallelProgId, final String studentPassword) throws Exception {

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_PARALLEL_PROGRAM_STUDENT");
    	boolean isParallelProgramStudent = false;
    	try {
    		isParallelProgramStudent = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{parallelProgId, studentPassword},
    				new RowMapper<Boolean>() {
    					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
    						Boolean isParallelProgramStudent;
    						isParallelProgramStudent = (rs.getInt("is_parallel_prog_student") > 0);

    						return isParallelProgramStudent;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error checking for Parallel Program, parallelProgId: %d, password: %s", parallelProgId, studentPassword), e);
    		throw new Exception(e.getMessage());
    	}
    	return isParallelProgramStudent;
    }

    public boolean isParallelProgramAssignedToStudent(final Integer parallelProgId, final Integer userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_PARALLEL_PROGRAM_ASSIGNED");
    	boolean isAssigned = false;
    	try {
    		isAssigned = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{userId, parallelProgId},
    				new RowMapper<Boolean>() {
    					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
    						Boolean isAssigned;
    						isAssigned = (rs.getInt("is_assigned") > 0);

    						return isAssigned;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error checking for Parallel Program assignment, parallelProgId: %d, userId: %s", parallelProgId, userId), e);
    		throw new Exception(e.getMessage());
    	}
    	return isAssigned;
    }

    public boolean isStudentInParallelProgram(final Integer userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_IN_PARALLEL_PROGRAM");
    	boolean isInParallelProgram = false;
    	try {
    		isInParallelProgram = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{userId},
    				new RowMapper<Boolean>() {
    					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
    						Boolean isInParallelProgram;
    						isInParallelProgram = (rs.getInt("is_in_parallel_program") > 0);

    						return isInParallelProgram;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error checking in Parallel Program, userId: %d", userId), e);
    		throw new Exception(e.getMessage());
    	}
    	if (LOGGER.isDebugEnabled()) {
    		LOGGER.debug(String.format("isStudentInParallelProgram: userId: %d, isInParallelProgram: %s",
    				userId, isInParallelProgram));
    	}
    	return isInParallelProgram;
    }

    public boolean parallelProgramPrevAssignedToStudent(final Integer parallelProgId, final Integer userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("PARALLEL_PROGRAM_PREV_ASSIGNED");
    	boolean prevAssigned = false;
    	try {
    		prevAssigned = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{userId, parallelProgId},
    				new RowMapper<Boolean>() {
    					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
    						Boolean prevAssigned;
    						prevAssigned = (rs.getInt("prev_assigned") > 0);

    						return prevAssigned;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error checking for Previous Program assignment, parallelProgId: %d, userId: %d", parallelProgId, userId), e);
    		throw new Exception(e.getMessage());
    	}
    	return prevAssigned;
    }

    public CmProgramAssign getProgramAssignForParallelProgIdAndUserId(final int parallelProgId, final int userId) throws Exception {

    	if (LOGGER.isDebugEnabled())
    		LOGGER.debug(String.format("+++ getProgramAssignForParallelProgIdAndUserId(): ppID: %d, userId: %d",
    				parallelProgId, userId));

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_ASSIGN_FOR_PPID_AND_USERID");
    	CmProgramAssign cmProg = null;
    	try {
    		cmProg = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{parallelProgId, userId},
    				new RowMapper<CmProgramAssign>() {
    					public CmProgramAssign mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmProgramAssign cmProgAssign;
    						cmProgAssign = new CmProgramAssign();
    						cmProgAssign.setId(rs.getInt("id"));
    						cmProgAssign.setProgSegment(rs.getInt("prog_segment"));
    						cmProgAssign.setRunId(rs.getInt("run_id"));
    						cmProgAssign.setRunSession(rs.getInt("run_session"));
    						cmProgAssign.setTestId(rs.getInt("test_id"));
    						cmProgAssign.setSegmentSlot(rs.getInt("segment_slot"));
    						cmProgAssign.setUserId(rs.getInt("user_id"));
    						cmProgAssign.setUserProgId(rs.getInt("user_prog_id"));

    						return cmProgAssign;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting CM Program Assign, userId: %d, parallelProgId: %d", userId, parallelProgId), e);
    		throw new Exception(e.getMessage());
    	}
    	return cmProg;
    }

    public int getStudentUserId(final Integer parallelProgId, final String studentPassword) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_STUDENT_UID");
    	int studentUserId = 0;
    	try {
    		studentUserId = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{parallelProgId, studentPassword},
    				new RowMapper<Integer>() {
    					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
    						Integer studentUserId;
    						studentUserId = rs.getInt("student_uid");

    						return studentUserId;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting Student UID, parallelProgId: %d, password: %s", parallelProgId, studentPassword), e);
    		throw new Exception(e.getMessage());
    	}
    	return studentUserId;
    }

    public CmProgram getCmProgramForParallelProgramId(final Integer parallelProgId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_FOR_PP");
    	CmProgram cmProg = null;
    	try {
    		cmProg = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{parallelProgId},
    				new RowMapper<CmProgram>() {
    					public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmProgram cmProg;
    						cmProg = new CmProgram();
    						CmProgramInfo progInfo = cmProg.getCmProgInfo();
    						cmProg.setId(rs.getInt("id"));
    						cmProg.setAdminId(rs.getInt("admin_id"));
    						cmProg.setPassPercent(rs.getInt("pass_percent"));
    						progInfo.setTestDefId(rs.getInt("test_def_id"));
    						progInfo.setSubjectId(rs.getString("subj_id"));
    						progInfo.setSegmentCount(rs.getInt("segment_count"));
    						progInfo.setProgramType(CmProgramType.lookup(rs.getString("prog_id")));
    						cmProg.setTestConfigJson(rs.getString("test_config_json"));
    						cmProg.setCustomProgId(rs.getInt("custom_prog_id"));
    						cmProg.setCustomQuizId(rs.getInt("custom_quiz_id"));

    						return cmProg;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting TestDef ID, parallelProgId: %d", parallelProgId), e);
    		throw new Exception(e.getMessage());
    	}
    	return cmProg;
    }

    public void addProgram(final CmProgram model) throws Exception {

    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	final String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PROGRAM");
    	try {
    		getJdbcTemplate().update(new PreparedStatementCreator() {
    			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
    				PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
    				ps.setInt(1, model.getAdminId());
    				ps.setInt(2, model.getPassPercent());
    				ps.setInt(3, model.getCustomProgId());
    				ps.setInt(4, model.getCustomQuizId());
    				ps.setString(5, model.getTestConfigJson());
    				ps.setString(6, model.getCmProgInfo().getProgramType().getType());
    				ps.setString(7, model.getCmProgInfo().getSubjectId());

    				return ps;
    			}
    		}, keyHolder);
    	} catch (Exception e) {
    		LOGGER.error("Error adding: " + model.toString(), e);
    		throw new Exception("Error adding CM_PROGRAM", e);
    	}

    	// extract the auto created pk
    	final int id = keyHolder.getKey().intValue();
    	model.setId(id);
    }

    public void addParallelProgram(final CmParallelProgram model) throws Exception {

    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	final String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PARALLEL_PROGRAM");
    	try {
    		getJdbcTemplate().update(new PreparedStatementCreator() {
    			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {

    				PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
    				ps.setInt(1, model.getAdminId());
    				ps.setInt(2, model.getCmProgId());
    				ps.setString(3, model.getPassword());
    				ps.setString(4, model.getName());

    				return ps;
    			}
    		}, keyHolder);
    	} catch (Exception e) {
    		LOGGER.error("Error adding: " + model.toString(), e);
    		throw new Exception("Error adding CM_PARALLEL_PROGRAM", e);
    	}

    	// extract the auto created pk
    	final int id = keyHolder.getKey().intValue();

    	model.setId(id);
    }

    public void addProgramAssignment(final CmProgramAssign model) throws Exception {

    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	final String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PROGRAM_ASSIGN");
    	try {
    		getJdbcTemplate().update(new PreparedStatementCreator() {
    			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
    				PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });

    				ps.setInt(1, model.getUserId());
    				ps.setInt(2, model.getUserProgId());
    				ps.setInt(3, model.getCmProgram().getId());
    				ps.setInt(4, model.getProgSegment());
    				ps.setInt(5, model.getRunId());
    				ps.setInt(6, model.getRunSession());
    				ps.setInt(7, model.getTestId());
    				ps.setInt(8, model.getSegmentSlot());
    				ps.setInt(9, model.isParallelProg()?1:0);
    				ps.setInt(10, model.isCurrentMainProg()?1:0);
    				//ps .setInt(11, model.getParallelProgId());

    				return ps;
    			}
    		}, keyHolder);
    	} catch (Exception e) {
    		LOGGER.error("Error adding: " + model.toString(), e);
    		throw new Exception("Error adding CM_PROGRAM_ASSIGN", e);
    	}

    	// extract the auto created pk
    	final int id = keyHolder.getKey().intValue();
    	model.setId(id);
    }
    
    public List<CmParallelProgram> getParallelProgramsForAdminId(final int adminId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_PARALLEL_PROGRAMS_FOR_ADMIN");
    	List<CmParallelProgram> ppList = null;
    	try {
    		ppList = this.getJdbcTemplate().query(
    				sql,
    				new Object[]{adminId},
    				new RowMapper<CmParallelProgram>() {
    					public CmParallelProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmParallelProgram parallelProg;
    						parallelProg = new CmParallelProgram();
    						parallelProg.setAdminId(adminId);
    						parallelProg.setCmProgId(rs.getInt("prog_inst_id"));
    						parallelProg.setId(rs.getInt("id"));
    						parallelProg.setName(rs.getString("name"));
    						parallelProg.setPassword(rs.getString("password"));
    						parallelProg.setStudentCount(rs.getInt("student_count"));

    						parallelProg.setCmProgName(rs.getString("program"));

    						String progId = rs.getString("prog_id");
    						if (progId.equalsIgnoreCase("chap")) {
    							String subjId = rs.getString("subj_id");
    							String chapter = JsonUtil.getChapter(rs.getString("test_config_json"));
    							try {
        							CmAdminDao cmaDao = CmAdminDao.getInstance();
        							List <ChapterModel> cmList = cmaDao.getChaptersForProgramSubject("Chap", subjId);
    	    						for (ChapterModel cm : cmList) {
    		    						if (cm.getTitle().equals(chapter)) {
    			    						parallelProg.setCmProgName(new StringBuilder(parallelProg.getCmProgName()).append(" ").append(cm.getNumber()).toString());
    				    					break;
    					    			}
    						    	}
    							}
    							catch (Exception e) {
    								throw new SQLException(e);
    							}
    						}
    						return parallelProg;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting Parallel Programs for adminId: %d", adminId), e);
    		throw new Exception(e.getMessage());
    	}
    	return ppList;
    }

    public List<Integer> getStudentsForParallelProgram(final int parallelProgId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_STUDENTS_FOR_PARALLEL_PROGRAM");
    	List<Integer> uidList = null;
    	try {
    		uidList = this.getJdbcTemplate().query(
    				sql,
    				new Object[]{parallelProgId},
    				new RowMapper<Integer>() {
    					public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
    						return rs.getInt("user_id");
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting Parallel Program Students for parallel prog Id: %d", parallelProgId), e);
    		throw new Exception(e.getMessage());
    	}
    	return uidList;
    }
    
    public List<ParallelProgramUsageModel> getUsageForParallelProgram(final int parallelProgId) throws Exception {

    	List<Integer> uidList = getStudentsForParallelProgram(parallelProgId);

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_USAGE_FOR_PARALLEL_PROGRAM", createInListMap(createInList(uidList)));
    	List<ParallelProgramUsageModel> ppuList = null;

    	try {
    		ppuList = this.getJdbcTemplate().query(
    				sql,
    				new Object[]{parallelProgId, parallelProgId, parallelProgId, parallelProgId},
    				new RowMapper<ParallelProgramUsageModel>() {
    					public ParallelProgramUsageModel mapRow(ResultSet rs, int rowNum) throws SQLException {
    						ParallelProgramUsageModel parallelProgUsage;
    						parallelProgUsage = new ParallelProgramUsageModel();

    						parallelProgUsage.setStudentName(rs.getString("user_name"));
    						parallelProgUsage.setUserId(rs.getInt("user_id"));
    						parallelProgUsage.setUseDate(rs.getString("use_date"));

    						StringBuilder sb = new StringBuilder();
    						sb.append(rs.getString("activity"));

    						boolean isQuiz = (rs.getInt("is_quiz") > 0);
    						if (isQuiz)
    							sb.append(rs.getInt("test_segment"));
    						parallelProgUsage.setActivity(sb.toString());
    						parallelProgUsage.setQuiz(isQuiz);

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
    						parallelProgUsage.setResult(sb.toString());

    						return parallelProgUsage;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting Parallel Program Usage for parallel prog Id: %d", parallelProgId), e);
    		throw new Exception(e.getMessage());
    	}

    	// We only want the most recent Parallel Program usage for each user/student
    	// and the section number needs to be set for "Review-" activities;
    	// it may be possible to perform this in SQL, but for now...
    	int userId = 0;

    	List<ParallelProgramUsageModel> list = new ArrayList<ParallelProgramUsageModel>();

    	boolean isReview = false;
    	ParallelProgramUsageModel mdl = null;

    	for (ParallelProgramUsageModel ppum : ppuList) {
    		if (ppum.getUserId() != userId) {
    			list.add(ppum);
    			userId = ppum.getUserId();
    			isReview = ppum.getActivity().equals("Review-");
    			mdl = ppum;
    		}
    		else {
    			// update "activity" with section number if isReview == true
    			if (isReview == true) {
    				if (ppum.getActivity().startsWith("Quiz-")) {
    					isReview = false;
    					String sectionNum = ppum.getActivity().substring(5);
    					if (! sectionNum.equals("0")) {
    						String activity = "Review-" + sectionNum;
    						mdl.setActivity(activity);
    					}
    				}
    			}
    		}
    	}

    	return list;
    }

    /**
     * is User's current Program in CM_PROGRAM?
     * 
     * @param userId
     */
    public boolean currentProgramExistsForStudent(final int userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("CM_PROGRAM_EXISTS_FOR_STUDENT");
    	boolean progExists = false;
    	try {
    		progExists = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{userId},
    				new RowMapper<Boolean>() {
    					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
    						Boolean progExists;
    						progExists = (rs.getInt("prog_exists") > 0);

    						return progExists;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error checking for CM Program existence, userId: %d", userId), e);
    		throw new Exception(e.getMessage());
    	}
    	return progExists;
    }

    /**
     * is User's current Program in CM_PROGRAM_ASSIGN?
     * 
     * @param userId
     */
    public boolean programAssignmentExistsForStudent(final int userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("CM_PROGRAM_ASSIGN_EXISTS_FOR_STUDENT");
    	boolean progExists = false;
    	try {
    		progExists = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{userId},
    				new RowMapper<Boolean>() {
    					public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
    						Boolean progExists;
    						progExists = (rs.getInt("prog_exists") > 0);

    						return progExists;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error checking for CM Program Assign existence, userId: %d", userId), e);
    		throw new Exception(e.getMessage());
    	}
    	return progExists;
    }

    /**
     * add User's current Program to CM_PROGRAM
     * 
     * @param userId
     */
    public CmProgram addCurrentProgramForStudent(final int userId) throws Exception {
    	final String sql = CmMultiLinePropertyReader.getInstance().getProperty("ADD_CM_PROGRAM_FOR_STUDENT");
    	KeyHolder keyHolder = new GeneratedKeyHolder();
    	try {
    		getJdbcTemplate().update(new PreparedStatementCreator() {
    			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
    				PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
    				ps.setInt(1, userId);
    				return ps;
    			}
    		}, keyHolder);
    	} catch (Exception e) {
    		LOGGER.error("Error adding CM Program for userId: " + userId, e);
    		throw new Exception("Error adding CM_PROGRAM", e);
    	}

    	// extract the auto created pk
    	final int id = keyHolder.getKey().intValue();

    	return getCmProgramForId(id);
    }

    /**
     * get Main Program for Student
     * 
     * @param userId
     */
    public CmProgram getMainProgramForStudent(final int userId) throws Exception {

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_MAIN_PROGRAM_FOR_USERID");
    	CmProgram cmProg = null;
    	try {
    		cmProg = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{userId},
    				new RowMapper<CmProgram>() {
    					public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmProgram cmProg = new CmProgram();
    						StudentActiveInfo activeInfo = cmProg.getActiveInfo();

    						activeInfo.setActiveRunId(rs.getInt("run_id"));
    						activeInfo.setActiveRunSession(rs.getInt("run_session"));
    						activeInfo.setActiveSegment(rs.getInt("prog_segment"));
    						activeInfo.setActiveSegmentSlot(rs.getInt("segment_slot"));
    						activeInfo.setActiveTestId(rs.getInt("test_id"));

    						cmProg.setId(rs.getInt("prog_inst_id"));
    						cmProg.setCustomProgId(rs.getInt("custom_prog_id"));
    						cmProg.setCustomQuizId(rs.getInt("custom_quiz_id"));
    						cmProg.setUserProgId(rs.getInt("user_prog_id"));                        	

    						return cmProg;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting CM Main Program, userId: %d", userId), e);
    		throw new Exception("Error getting CM Main Program", e);
    	}

    	return cmProg;

    }
    
    /**
     * reassign User to Main Program
     * 
     * @param userId
     * 
     * @throws Exception
     */
    public void reassignMainProgram(final int userId) throws Exception {

    	CmProgram cmProg = getMainProgramForStudent(userId);
    	reassignProgram(userId, cmProg);
    }

    /**
     * reassign User to a Program
     * 
     * @param userId
     * @param cmProg
     * 
     * @throws Exception
     */
    public void reassignProgram(int userId, CmProgram cmProg) throws Exception {

        CmProgram existingCP = getCmProgramForUserId(userId);

    	updateProgramAssign(userId, existingCP, false);

        updateProgramAssign(userId, cmProg, false);

        CmStudentDao stuDao = CmStudentDao.getInstance();
        stuDao.setActiveInfoAndUserProgId(userId, cmProg.getActiveInfo(),cmProg.getUserProgId());
    }

    /**
     * update Active Info in CM_PROGRAM_ASSIGN
     * 
     * @param userId
     *
     * @throws Exception
     */
    public void updateProgramAssign(final int userId) throws Exception {
    	//TODO: handle auto-enroll transition
        CmProgram cmProg = getCmProgramForUserId(userId);
        updateProgramAssign(userId, cmProg, false);
    }

    /**
     * update Active Info in CM_PROGRAM_ASSIGN
     * 
     * @param userId
     *
     * @throws Exception
     */
    public void updateProgramAssign(final int userId, boolean continueParallelProgram) throws Exception {
    	//TODO: handle auto-enroll transition
        CmProgram cmProg = getCmProgramForUserId(userId);
        updateProgramAssign(userId, cmProg, continueParallelProgram);
    }

    /**
     * update Active Info in CM_PROGRAM_ASSIGN
     * 
     * @param userId
     * @param cmProg
     * @param continueParallelProgram  (for future auto-enroll support)  
     * 
     *
     * @throws Exception
     */
    public void updateProgramAssign(final int userId, final CmProgram cmProg, boolean continueParallelProgram) throws Exception {

    	if (LOGGER.isDebugEnabled())
    		LOGGER.debug(String.format("Updating CM Program Assign; userId: %d, user_progId: %d, continueParallelProgram: %s",
    				userId, cmProg.getUserProgId(), continueParallelProgram));

    	final String sql = CmMultiLinePropertyReader.getInstance().getProperty("UPDATE_CM_PROGRAM_ASSIGN");
    	try {
    		getJdbcTemplate().update(new PreparedStatementCreator() {
    			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
    				PreparedStatement ps = connection.prepareStatement(sql);

    				StudentActiveInfo activeInfo = cmProg.getActiveInfo();

    				ps.setInt(1, activeInfo.getActiveTestId());
    				ps.setInt(2, activeInfo.getActiveRunId());
    				ps.setInt(3, activeInfo.getActiveRunSession());
    				ps.setInt(4, activeInfo.getActiveSegment());
    				ps.setInt(5, activeInfo.getActiveSegmentSlot());
    				ps.setInt(6, userId);
    				ps.setInt(7, cmProg.getUserProgId());

    				return ps;
    			}
    		});
    	} catch (Exception e) {
    		LOGGER.error("Error updating CM Program Assign for userId: " + userId, e);
    		throw new Exception("Error updating CM_PROGRAM_ASSIGN", e);
    	}

    }

    /**
     * update Active Info in CM_PROGRAM_ASSIGN
     * 
     * @param userId
     * @param cmProg
     * @param continueParallelProgram
     *
     * @throws Exception
     */
    public CmProgramAssign getProgramAssignForUserIdAndUserProgId(final int userId, final int userProgId) throws Exception {

    	if (LOGGER.isDebugEnabled())
    		LOGGER.debug(String.format("+++ getProgramAssignForUserIdAndUserProgId(): ppId: %d, userId: %d",
    				userProgId, userId));

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_ASSIGN_FOR_USERID_AND_USER_PROG_ID");
    	CmProgramAssign cmProg = null;
    	try {
    		cmProg = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{userProgId, userId},
    				new RowMapper<CmProgramAssign>() {
    					public CmProgramAssign mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmProgramAssign cmProgAssign;
    						cmProgAssign = new CmProgramAssign();
    						cmProgAssign.setId(rs.getInt("id"));
    						//cmProgAssign.setParallelProgId(rs.getInt("parallel_prog_id"));
    						cmProgAssign.setProgSegment(rs.getInt("prog_segment"));
    						cmProgAssign.setRunId(rs.getInt("run_id"));
    						cmProgAssign.setRunSession(rs.getInt("run_session"));
    						cmProgAssign.setTestId(rs.getInt("test_id"));
    						cmProgAssign.setSegmentSlot(rs.getInt("segment_slot"));
    						cmProgAssign.setUserId(rs.getInt("user_id"));
    						cmProgAssign.setUserProgId(rs.getInt("user_prog_id"));

    						return cmProgAssign;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting CM Parallel Program Assign, userProgId: %d", userProgId), e);
    		throw new Exception(e.getMessage());
    	}
    	return cmProg;
    }

    /**
     * get CM Program for specified ID
     * 
     * @param id
     * @return
     * @throws Exception
     */
    public CmProgram getCmProgramForId(final int id) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_FOR_ID");
    	CmProgram cmProg = null;
    	try {
    		cmProg = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{id},
    				new RowMapper<CmProgram>() {
    					public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmProgram cmProg;
    						cmProg = new CmProgram();
    						CmProgramInfo progInfo = cmProg.getCmProgInfo();
    						cmProg.setId(rs.getInt("id"));
    						cmProg.setAdminId(rs.getInt("admin_id"));
    						cmProg.setPassPercent(rs.getInt("pass_percent"));
    						progInfo.setTestDefId(rs.getInt("test_def_id"));
    						progInfo.setSubjectId(rs.getString("subj_id"));
    						progInfo.setSegmentCount(rs.getInt("segment_count"));
    						progInfo.setProgramType(CmProgramType.lookup(rs.getString("prog_id")));
    						cmProg.setTestConfigJson(rs.getString("test_config_json"));
    						cmProg.setCustomProgId(rs.getInt("custom_prog_id"));
    						cmProg.setCustomQuizId(rs.getInt("custom_quiz_id"));

    						return cmProg;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting CM Program for ID: %d", id), e);
    		throw new Exception(e.getMessage());
    	}
    	return cmProg;
    }

    /**
     * get CM Program for specified UserId
     * 
     * @param userId
     * @return
     * @throws Exception
     */
    public CmProgram getCmProgramForUserId(final int userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_FOR_USERID");
    	CmProgram cmProg = null;
    	try {
    		cmProg = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{userId},
    				new RowMapper<CmProgram>() {
    					public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmProgram cmProg;
    						cmProg = new CmProgram();
    						cmProg.setId(rs.getInt("id"));
    						cmProg.setAdminId(rs.getInt("admin_id"));
    						cmProg.setPassPercent(rs.getInt("pass_percent"));
    						cmProg.setTestConfigJson(rs.getString("test_config_json"));
    						cmProg.setCustomProgId(rs.getInt("custom_prog_id"));
    						cmProg.setCustomQuizId(rs.getInt("custom_quiz_id"));
    						cmProg.setUserProgId(rs.getInt("user_prog_id"));

    						CmProgramInfo progInfo = cmProg.getCmProgInfo();
    						progInfo.setTestDefId(rs.getInt("test_def_id"));
    						progInfo.setSubjectId(rs.getString("subj_id"));
    						progInfo.setSegmentCount(rs.getInt("segment_count"));
    						progInfo.setProgramType(CmProgramType.lookup(rs.getString("prog_id")));

    						StudentActiveInfo activeInfo = cmProg.getActiveInfo();
    						activeInfo.setActiveRunId(rs.getInt("active_run_id"));
    						activeInfo.setActiveRunSession(rs.getInt("active_run_session"));
    						activeInfo.setActiveSegment(rs.getInt("active_segment"));
    						activeInfo.setActiveSegmentSlot(rs.getInt("active_segment_slot"));
    						activeInfo.setActiveTestId(rs.getInt("active_test_id"));

    						return cmProg;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting CM Program for UserId: %d", userId), e);
    		throw new Exception(e.getMessage());
    	}
    	return cmProg;
    }

    public CmParallelProgram getParallelProgramForId(final int ppId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_PARALLEL_PROGRAM");
    	CmParallelProgram parallelProg = null;

    	try {
    		parallelProg = this.getJdbcTemplate().queryForObject(
    				sql,
    				new Object[]{ppId},
    				new RowMapper<CmParallelProgram>() {
    					public CmParallelProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmParallelProgram parallelProg;
    						parallelProg = new CmParallelProgram();
    						parallelProg.setId(rs.getInt("id"));
    						parallelProg.setAdminId(rs.getInt("admin_id"));
    						parallelProg.setName(rs.getString("name"));
    						parallelProg.setPassword(rs.getString("password"));
    						parallelProg.setCmProgId(rs.getInt("prog_inst_id"));

    						return parallelProg;
    					}
    				});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error getting Parallel Program for ppId: %d", ppId), e);
    		throw new Exception(e.getMessage());
    	}
    	return parallelProg;
    }
    
    /**
     * delete Parallel Program for specified ppId
     * 
     * @param ppId
     * @throws Exception
     */
    public void deleteParallelProgram(final int ppId) throws Exception {
    	final String sql = CmMultiLinePropertyReader.getInstance().getProperty("DELETE_PARALLEL_PROGRAM");
    	try {
    		getJdbcTemplate().update(new PreparedStatementCreator() {
    			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
    				PreparedStatement ps = connection.prepareStatement(sql);
    				ps.setInt(1, ppId);
    				return ps;
    			}
    		});
    	}
    	catch(Exception e) {
    		LOGGER.error(String.format("Error deleting Parallel Program identified by ID: %d", ppId), e);
    		throw new Exception(e.getMessage());
    	}
    }

    public StudentModelExt getStudentModelForParallelProgram(int parallelProgId) throws Exception {
    	CmParallelProgram pp = this.getParallelProgramForId(parallelProgId);
    	CmProgram cmProg = this.getCmProgramForParallelProgramId(parallelProgId);
    	StudentModelExt sm = this.parallelProgramToStudentModel(cmProg, pp);
        return sm;    	
    }

    public void resetMainProgram(final int userId) throws Exception {
    	final String sql = CmMultiLinePropertyReader.getInstance().getProperty("RESET_MAIN_PROGRAM");
    	try {
    		getJdbcTemplate().update(new PreparedStatementCreator() {
    			public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
    				PreparedStatement ps = connection.prepareStatement(sql);

    				ps.setInt(1, userId);

    				return ps;
    			}
    		});		
    	} catch (Exception e) {
    		LOGGER.error("Error resetting Main Program for userId: " + userId, e);
    		throw new Exception("Error resetting Main Program", e);
    	}
    }

	public void resetPreviousMainProgram(final int userId, final int progAssignId) throws Exception {
        final String sql = CmMultiLinePropertyReader.getInstance().getProperty("RESET_PREV_MAIN_PROGRAM");
        try {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1, userId);
                    ps.setInt(2, progAssignId);
                    return ps;
            }
        });		
        }
        catch(Exception e) {
            LOGGER.error(String.format("Error resetting Main Program identified by userId: %d, (not)id: %d",
            		userId, progAssignId), e);
            throw new Exception(e.getMessage());
        }
	}

    /**
     * replace a Student's Main Program
     */
	public void replaceMainProgram(int userId) throws Exception {
		resetMainProgram(userId);
	}

	/**
	 * 
	 * @param prog
	 * @param parallelProgId
	 */
	public void updateProgram(CmProgram prog, Integer parallelProgId) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param pp
	 * @param parallelProgId
	 */
	public void updateParallelProgram(CmParallelProgram pp,
			Integer parallelProgId) {
		// TODO Auto-generated method stub
		
	}
    
    private Map<String,String> createInListMap(String list) {
        Map<String,String> map = new HashMap<String, String>();
        map.put("UID_LIST", list);
        return map;
    }
    
    private String createInList(List<Integer> uids) {
    	StringBuilder sb = new StringBuilder();
        for(Integer uid: uids) {
            if(sb.length() > 0 )
                sb.append(", ");
            sb.append(uid);
        }
        return sb.toString();
    }

    private StudentModelExt parallelProgramToStudentModel(
			CmProgram cmProg, CmParallelProgram ppMdl) throws Exception {
    	
    	StudentModelExt sm = new StudentModelExt();
    	
        CmProgramInfo progInfo = cmProg.getCmProgInfo();
        
        sm.setAdminUid(ppMdl.getAdminId());
        sm.setName(ppMdl.getName());
        sm.setPasscode(ppMdl.getPassword());
        
        StudentProgramModel progMdl =  new StudentProgramModel();
        progMdl.setProgramType(progInfo.getProgramType());
        progMdl.setSubjectId(progInfo.getSubjectId());
        if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ subjectId: " + progInfo.getSubjectId());

        progMdl.getCustom().setCustomProgramId(cmProg.getCustomProgId());
        if (cmProg.getCustomProgId() > 0) {
        	CustomProgramModel cp = CmCustomProgramDao.getInstance().getCustomProgram(cmProg.getCustomProgId());
        	progMdl.getCustom().setCustomProgramName(cp.getProgramName());
        	if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ custom program name: " + cp.getProgramName());
        }

        progMdl.getCustom().setCustomQuizId(cmProg.getCustomQuizId());
        if (cmProg.getCustomQuizId() > 0) {
        	CustomQuizDef cq = CmQuizzesDao.getInstance().getCustomQuiz(cmProg.getCustomQuizId());
        	progMdl.getCustom().setCustomQuizName(cq.getQuizName());
        	if (LOGGER.isDebugEnabled()) LOGGER.debug("+++ custom quiz name: " + cq.getQuizName());
        }

        sm.setProgram(progMdl);
        sm.setJson(cmProg.getTestConfigJson());
        sm.setPassPercent("70");

		return sm;
	}
    
    private String getStdProgName(String progId, String subjId, String cpName, String cqName) {
    	StringBuilder sb = new StringBuilder();
    	if (progId.startsWith("Custom")) {
    	    if (cpName.trim().length() > 0) {
    	    	sb.append("CP: ").append(cpName);
    	    }
    	    else if (cqName.trim().length() > 0) {
    	    	sb.append("CQ: ").append(cqName);
    	    }
    	}
    	else {
    		sb.append(progId).append(" ").append(subjId);
    	}
    	return sb.toString();
    }

}