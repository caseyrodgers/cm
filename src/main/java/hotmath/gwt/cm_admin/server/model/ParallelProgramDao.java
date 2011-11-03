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
        boolean isParallelProgramStudent = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{parallelProgId, studentPassword},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean isParallelProgramStudent;
                        try {
                            isParallelProgramStudent = (rs.getInt("is_parallel_prog_student") > 0);

                            return isParallelProgramStudent;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for Parallel Program, parallelProgId: %d, password: %s", parallelProgId, studentPassword), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return isParallelProgramStudent;
    }

    public boolean isParallelProgramAssignedToStudent(final Integer parallelProgId, final Integer userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_PARALLEL_PROGRAM_ASSIGNED");
        boolean isAssigned = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId, parallelProgId},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean isAssigned;
                        try {
                            isAssigned = (rs.getInt("is_assigned") > 0);

                            return isAssigned;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for Parallel Program assignment, parallelProgId: %d, userId: %s", parallelProgId, userId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return isAssigned;
    }

    public boolean isStudentInParallelProgram(final Integer userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("IS_IN_PARALLEL_PROGRAM");
        boolean isInParallelProgram = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean isInParallelProgram;
                        try {
                            isInParallelProgram = (rs.getInt("is_in_parallel_program") > 0);

                            return isInParallelProgram;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking in Parallel Program, userId: %d", userId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return isInParallelProgram;
    }

    public boolean parallelProgramPrevAssignedToStudent(final Integer parallelProgId, final Integer userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("PARALLEL_PROGRAM_PREV_ASSIGNED");
        boolean prevAssigned = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId, parallelProgId},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean prevAssigned;
                        try {
                            prevAssigned = (rs.getInt("prev_assigned") > 0);

                            return prevAssigned;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for Previous Program assignment, parallelProgId: %d, userId: %d", parallelProgId, userId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return prevAssigned;
    }

    public CmProgramAssign getProgramAssignForParallelProgIdAndUserId(final int parallelProgId, final int userId) throws Exception {
    	
    	if (LOGGER.isDebugEnabled())
    		LOGGER.debug(String.format("+++ getProgramAssignForParallelProgIdAndUserId(): ppID: %d, userId: %d",
    			parallelProgId, userId));

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_ASSIGN_FOR_PPID_AND_USERID");
        CmProgramAssign cmProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{parallelProgId, userId},
                new RowMapper<CmProgramAssign>() {
                    public CmProgramAssign mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmProgramAssign cmProgAssign;
                        try {
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
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting CM Program Assign, parallelProgId: %d", parallelProgId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return cmProg;
    }

    public int getStudentUserId(final Integer parallelProgId, final String studentPassword) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_STUDENT_UID");
        int studentUserId = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{parallelProgId, studentPassword},
                new RowMapper<Integer>() {
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Integer studentUserId;
                        try {
                            studentUserId = rs.getInt("student_uid");

                            return studentUserId;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Student UID, parallelProgId: %d, password: %s", parallelProgId, studentPassword), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return studentUserId;
    }

    public CmProgram getCmProgramForParallelProgramId(final Integer parallelProgId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CM_PROGRAM_FOR_PP");
        CmProgram cmProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{parallelProgId},
                new RowMapper<CmProgram>() {
                    public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmProgram cmProg;
                        try {
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
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting TestDef ID, parallelProgId: %d", parallelProgId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return cmProg;
    }

    public void addProgram(final CmProgram model) {
    	
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PROGRAM");
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
                    ps.setInt(1, model.getAdminId());
                    ps.setInt(2, model.getPassPercent());
                    ps.setInt(3, model.getCustomProgId());
                    ps.setInt(4, model.getCustomQuizId());
                    ps.setString(5, model.getTestConfigJson());
                    ps.setString(6, model.getCmProgInfo().getProgramType().getType());
                    ps.setString(7, model.getCmProgInfo().getSubjectId());

                    return ps;
                } catch (Exception e) {
                    LOGGER.error("Error adding: " + model.toString(), e);
                    throw new SQLException("Error adding CM_PROGRAM", e);
                }
            }
        }, keyHolder);

        // extract the auto created pk
        final int id = keyHolder.getKey().intValue();
        model.setId(id);
    }

    public void addParallelProgram(final CmParallelProgram model) {
    	
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PARALLEL_PROGRAM");
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
                    ps.setInt(1, model.getAdminId());
                    ps.setInt(2, model.getCmProgId());
                    ps.setString(3, model.getPassword());
                    ps.setString(4, model.getName());

                    return ps;
                } catch (Exception e) {
                    LOGGER.error("Error adding: " + model.toString(), e);
                    throw new SQLException("Error adding CM_PARALLEL_PROGRAM", e);
                }
            }
        }, keyHolder);

        // extract the auto created pk
        final int id = keyHolder.getKey().intValue();
        model.setId(id);
    }

    public void addProgramAssignment(final CmProgramAssign model) {
    	
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("CREATE_CM_PROGRAM_ASSIGN");
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

                    return ps;
                } catch (Exception e) {
                    LOGGER.error("Error adding: " + model.toString(), e);
                    throw new SQLException("Error adding CM_PROGRAM_ASSIGN", e);
                }
            }
        }, keyHolder);

        // extract the auto created pk
        final int id = keyHolder.getKey().intValue();
        model.setId(id);
    }
    
    public List<CmParallelProgram> getParallelProgramsForAdminId(final int adminId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_PARALLEL_PROGRAMS_FOR_ADMIN");
        List<CmParallelProgram> ppList = this.getJdbcTemplate().query(
                sql,
                new Object[]{adminId},
                new RowMapper<CmParallelProgram>() {
                    public CmParallelProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmParallelProgram parallelProg;
                        try {
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
                				CmAdminDao cmaDao = CmAdminDao.getInstance();
                				List <ChapterModel> cmList = cmaDao.getChaptersForProgramSubject("Chap", subjId);
                				for (ChapterModel cm : cmList) {
                					if (cm.getTitle().equals(chapter)) {
                						parallelProg.setCmProgName(new StringBuilder(parallelProg.getCmProgName()).append(" ").append(cm.getNumber()).toString());
                						break;
                					}
                				}
                			}
                            return parallelProg;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Parallel Programs for adminId: %d", adminId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return ppList;
    }

    public List<Integer> getStudentsForParallelProgram(final int parallelProgId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_STUDENTS_FOR_PARALLEL_PROGRAM");
        List<Integer> uidList = this.getJdbcTemplate().query(
                sql,
                new Object[]{parallelProgId},
                new RowMapper<Integer>() {
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                            return rs.getInt("user_id");
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Parallel Program Students for parallel prog Id: %d", parallelProgId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return uidList;
    }
    
    public List<ParallelProgramUsageModel> getUsageForParallelProgram(final int parallelProgId) throws Exception {

    	List<Integer> uidList = getStudentsForParallelProgram(parallelProgId);

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_USAGE_FOR_PARALLEL_PROGRAM", createInListMap(createInList(uidList)));
    	
        List<ParallelProgramUsageModel> ppuList = this.getJdbcTemplate().query(
                sql,
                new Object[]{parallelProgId, parallelProgId, parallelProgId, parallelProgId},
                new RowMapper<ParallelProgramUsageModel>() {
                    public ParallelProgramUsageModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                        ParallelProgramUsageModel parallelProgUsage;
                        try {
                        	parallelProgUsage = new ParallelProgramUsageModel();

                        	parallelProgUsage.setStudentName(rs.getString("user_name"));
                        	parallelProgUsage.setUserId(rs.getInt("user_id"));
                			parallelProgUsage.setUseDate(rs.getString("use_date"));

                			StringBuilder sb = new StringBuilder();
                			sb.append(rs.getString("activity"));

                			boolean isQuiz = (rs.getInt("is_quiz") > 0);
                			if (isQuiz) {
                				sb.append(rs.getInt("test_segment"));
                			}
                			parallelProgUsage.setActivity(sb.toString());
                			parallelProgUsage.setIsQuiz(isQuiz);

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
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Parallel Program Usage for parallel prog Id: %d", parallelProgId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        
        // We only want the most recent Parallel Program usage for each user/student
        // it may be possible to perform this in SQL, but for now...
    	int userId = 0;
    	
    	List<ParallelProgramUsageModel> list = new ArrayList<ParallelProgramUsageModel>();
    	
        for (ParallelProgramUsageModel ppum : ppuList) {
        	if (ppum.getUserId() != userId) {
        		list.add(ppum);
        		userId = ppum.getUserId();
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
        boolean progExists = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean progExists;
                        try {
                            progExists = (rs.getInt("prog_exists") > 0);

                            return progExists;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for CM Program existence, userId: %d", userId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return progExists;
    }

    /**
     * is User's current Program in CM_PROGRAM_ASSIGN?
     * 
     * @param userId
     */
    public boolean programAssignmentExistsForStudent(final int userId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("CM_PROGRAM_ASSIGN_EXISTS_FOR_STUDENT");
        boolean progExists = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId},
                new RowMapper<Boolean>() {
                    public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Boolean progExists;
                        try {
                            progExists = (rs.getInt("prog_exists") > 0);

                            return progExists;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error checking for CM Program Assign existence, userId: %d", userId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return progExists;
    }

    /**
     * add User's current Program to CM_PROGRAM
     * 
     * @param userId
     */
    public CmProgram addCurrentProgramForStudent(final int userId) throws Exception {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("ADD_CM_PROGRAM_FOR_STUDENT");
                    PreparedStatement ps = connection.prepareStatement(sql, new String[] { "id" });
                    ps.setInt(1, userId);

                    return ps;
                } catch (Exception e) {
                    LOGGER.error("Error adding CM Program for userId: " + userId, e);
                    throw new SQLException("Error adding CM_PROGRAM", e);
                }
            }
        }, keyHolder);

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
        CmProgram cmProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId},
                new RowMapper<CmProgram>() {
                    public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmProgram cmProg = new CmProgram();
                        try {
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
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting CM Main Program, userId: %d", userId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
    	
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
    	updateProgramAssign(userId, existingCP);

    	updateProgramAssign(userId, cmProg);
    	
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
        CmProgram cmProg = getCmProgramForUserId(userId);
        updateProgramAssign(userId, cmProg);
    }

    /**
     * update Active Info in CM_PROGRAM_ASSIGN
     * 
     * @param userId
     * @param cmProg
     *
     * @throws Exception
     */
    public void updateProgramAssign(final int userId, final CmProgram cmProg) throws Exception {

        LOGGER.debug(String.format("Updating CM Program Assign; userId: %d, user_progId: %d", userId, cmProg.getUserProgId()));

        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                	String sql = CmMultiLinePropertyReader.getInstance().getProperty("UPDATE_CM_PROGRAM_ASSIGN");
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
                } catch (Exception e) {
                    LOGGER.error("Error updating CM Program Assign for userId: " + userId, e);
                    throw new SQLException("Error updating CM_PROGRAM_ASSIGN", e);
                }
            }
        });
    	
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
        CmProgram cmProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{id},
                new RowMapper<CmProgram>() {
                    public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmProgram cmProg;
                        try {
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
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting CM Program for ID: %d", id), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
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
        CmProgram cmProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{userId},
                new RowMapper<CmProgram>() {
                    public CmProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmProgram cmProg;
                        try {
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
                            cmProg.setUserProgId(rs.getInt("user_prog_id"));

                            return cmProg;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting CM Program for UserId: %d", userId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return cmProg;
    }

	public CmParallelProgram getParallelProgramForId(final int ppId) throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_PARALLEL_PROGRAM");

        CmParallelProgram parallelProg = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{ppId},
                new RowMapper<CmParallelProgram>() {
                    public CmParallelProgram mapRow(ResultSet rs, int rowNum) throws SQLException {
                        CmParallelProgram parallelProg;
                        try {
                        	parallelProg = new CmParallelProgram();
                        	parallelProg.setId(rs.getInt("id"));
                        	parallelProg.setAdminId(rs.getInt("admin_id"));
                        	parallelProg.setName(rs.getString("name"));
                        	parallelProg.setPassword(rs.getString("password"));
                        	parallelProg.setCmProgId(rs.getInt("prog_inst_id"));

                            return parallelProg;
                        }
                        catch(Exception e) {
                            LOGGER.error(String.format("Error getting Parallel Program for ppId: %d", ppId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return parallelProg;
	}
    
    /**
     * delete Parallel Program for specified ppId
     * 
     * @param ppId
     * @throws Exception
     */
    public void deleteParallelProgram(final int ppId) throws Exception {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("DELETE_PARALLEL_PROGRAM");
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1, ppId);
                    return ps;
                }
                catch(Exception e) {
                    LOGGER.error(String.format("Error deleting Parallel Program identified by ID: %d", ppId), e);
                    throw new SQLException(e.getMessage());
                }
            }
        });
    }

    public StudentModelExt getStudentModelForParallelProgram(int parallelProgId) throws Exception {
    	CmParallelProgram pp = this.getParallelProgramForId(parallelProgId);
    	CmProgram cmProg = this.getCmProgramForParallelProgramId(parallelProgId);
    	StudentModelExt sm = this.parallelProgramToStudentModel(cmProg, pp);
        return sm;    	
    }

	public void resetPreviousMainProgram(final int userId, final int progAssignId) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(final Connection connection) throws SQLException {
                try {
                    String sql = CmMultiLinePropertyReader.getInstance().getProperty("RESET_PREV_MAIN_PROGRAM");
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1, userId);
                    ps.setInt(2, progAssignId);
                    return ps;
                }
                catch(Exception e) {
                    LOGGER.error(String.format("Error reseting Main Program identified by userId: %d, (not)id: %d",
                    		userId, progAssignId), e);
                    throw new SQLException(e.getMessage());
                }
            }
        });		
	}

	public void updateProgram(CmProgram prog, Integer parallelProgId) {
		// TODO Auto-generated method stub
		
	}

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
        LOGGER.debug("+++ subjectId: " + progInfo.getSubjectId());

        progMdl.getCustom().setCustomProgramId(cmProg.getCustomProgId());
        if (cmProg.getCustomProgId() > 0) {
        	CustomProgramModel cp = CmCustomProgramDao.getInstance().getCustomProgram(cmProg.getCustomProgId());
        	progMdl.getCustom().setCustomProgramName(cp.getProgramName());
            LOGGER.debug("+++ custom program name: " + cp.getProgramName());
        }

        progMdl.getCustom().setCustomQuizId(cmProg.getCustomQuizId());
        if (cmProg.getCustomQuizId() > 0) {
        	CustomQuizDef cq = CmQuizzesDao.getInstance().getCustomQuiz(cmProg.getCustomQuizId());
        	progMdl.getCustom().setCustomQuizName(cq.getQuizName());
            LOGGER.debug("+++ custom quiz name: " + cq.getQuizName());
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