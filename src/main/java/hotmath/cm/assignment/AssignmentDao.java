package hotmath.cm.assignment;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.PropertyLoadFileException;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentLessonDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class AssignmentDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(AssignmentDao.class);

    static private AssignmentDao __instance;

    static public AssignmentDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (AssignmentDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(AssignmentDao.class.getName());
        }
        return __instance;
    }

    private AssignmentDao() {
        /** empty */
    }

    public int saveAssignement(final int aid, final Assignment ass) {
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
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "insert into CM_ASSIGNMENT(aid,group_id,name,due_date,comments,last_modified,status)values(?,?,?,?,?,?,?)";
                    PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, aid);
                    ps.setInt(2, ass.getGroupId());
                    ps.setString(3, ass.getAssignmentName());
                    ps.setDate(4, new java.sql.Date(ass.getDueDate().getTime()));
                    ps.setString(5, ass.getComments());
                    ps.setDate(6, new Date(System.currentTimeMillis()));
                    ps.setString(7, ass.getStatus());
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
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "update CM_ASSIGNMENT set aid = ?, name = ?, due_date = ?, comments = ?, last_modified = now(), status = ? where assign_key = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, aid);
                    ps.setString(2, ass.getAssignmentName());
                    ps.setDate(3, new java.sql.Date(ass.getDueDate().getTime()));
                    ps.setString(4, ass.getComments());
                    ps.setString(5, ass.getStatus());
                    ps.setInt(6, ass.getAssignKey());

                    return ps;
                }
            });
            assignKey[0] = ass.getAssignKey();
        }
        final int assKey = assignKey[0];

        deleteAssignmentPids(assKey);

        /**
         * save the PIDS contained in this Assignment
         * 
         */
        if (ass.getPids() != null && ass.getPids().size() > 0) {
            String sqlPids = "insert into CM_ASSIGNMENT_PIDS(assign_key, pid, label, lesson)values(?,?,?,?)";
            getJdbcTemplate().batchUpdate(sqlPids, new BatchPreparedStatementSetter() {

                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, assKey);
                    ProblemDto p = ass.getPids().get(i);
                    ps.setString(2, p.getPid());
                    ps.setString(3, p.getLabel());
                    ps.setString(4, p.getLesson());
                }

                @Override
                public int getBatchSize() {
                    return ass.getPids().size();
                }
            });
        }

        return assKey;

    }

    /**
     * Fetch a persisted Assignment from DB
     * 
     * @param assKey
     * @return
     */
    public Assignment getAssignment(int assKey) throws AssignmentNotFoundException {

        String sql = "select * from CM_ASSIGNMENT where assign_key = ?";
        Assignment assignment = null;
        try {
            assignment = getJdbcTemplate().queryForObject(sql, new Object[] { assKey }, new RowMapper<Assignment>() {
                @Override
                public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Assignment(rs.getInt("assign_key"), rs.getInt("group_id"), rs.getString("name"), rs
                            .getString("comments"), rs.getDate("due_date"), null, null, rs.getString("status"));
                }
            });
        } catch (Exception e) {
            throw new AssignmentNotFoundException(assKey, e);
        }

        /**
         * now read all pids assigned to this Assignment
         * 
         */
        final int count[] = new int[1];
        sql = "select * from CM_ASSIGNMENT_PIDS where assign_key = ? order by id";
        List<ProblemDto> pids = getJdbcTemplate().query(sql, new Object[] { assKey }, new RowMapper<ProblemDto>() {
            @Override
            public ProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                String label = rs.getString("lesson") + " #" + (++count[0]);
                String pid = rs.getString("pid");
                return new ProblemDto(0, rs.getString("lesson"), label, pid);
            }
        });
        CmList<ProblemDto> cmPids = new CmArrayList<ProblemDto>();
        cmPids.addAll(pids);
        assignment.setPids(cmPids);

        return assignment;
    }

    public AssignmentLessonData getAssignmentLessonData() {

        final AssignmentLessonData data = new AssignmentLessonData();

        /**
         * get list of each prof program *
         * 
         */
        String sql = "select * from HA_TEST_DEF where is_active  = 1 and prog_id = 'Prof' order by load_order";
        List<String> pids = getJdbcTemplate().query(sql, new Object[] {}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                data.getSubjects().add(
                        new SubjectDto(0, rs.getInt("test_def_id"), rs.getString("test_name"), rs
                                .getString("test_name")));
                return "";
            }
        });

        return data;

    }

    /**
     * Return list of problems associated with named lesson
     * 
     * @param conn
     * @param lesson
     * @param gradeLevel
     * @return
     */
    public List<ProblemDto> getLessonProblemsFor(Connection conn, String lesson, String subject) {
        String sql = "select * from HA_PROGRAM_LESSONS where lesson = ? and subject = ? order by id";

        final int count[] = new int[1];
        List<ProblemDto> problems = getJdbcTemplate().query(sql, new Object[] { lesson, subject },
                new RowMapper<ProblemDto>() {
                    @Override
                    public ProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {

                        String defaultLabel = rs.getString("lesson") + ": #" + (++count[0]);

                        return new ProblemDto(0, rs.getString("lesson"), defaultLabel, rs.getString("pid"));
                    }
                });
        return problems;
    }


    public List<Assignment> getAssignments(int aid, int groupId) throws PropertyLoadFileException {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENTS_FOR_GROUP");

        List<Assignment> problems = getJdbcTemplate().query(sql, new Object[] { groupId }, new RowMapper<Assignment>() {
            @Override
            public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {

                // create a psudo name
                String comments = rs.getString("comments");
                String assignmentName = _createAssignmentName(rs.getDate("due_date"), comments);

                Assignment ass = new Assignment(rs.getInt("assign_key"), rs.getInt("group_id"), assignmentName, rs
                        .getString("comments"), rs.getDate("due_date"), null, null, rs.getString("status"));

                ass.setProblemCount(rs.getInt("problem_count"));
                return ass;
            }
        });
        return problems;
    }

    /**
     * delete the PIDS contained in this Assignment
     * 
     */
    private void deleteAssignmentPids(final int assKey) {
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from CM_ASSIGNMENT_PIDS where assign_key = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, assKey);
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
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from CM_ASSIGNMENT where assign_key = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1, assKey);
                return ps;
            }
        });
    }

    
    public CmList<StudentAssignment> getAssignmentGradeBook(final int assignKey) throws Exception {
        CmList<StudentAssignment> stuAssignments = new CmArrayList<StudentAssignment>();
        Assignment assignment = getAssignment(assignKey);
        
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_GRADE_BOOK_DATA_3");
        
        /**
         *  get assignment problem status list for all users
         */
        final Map<Integer, String> nameMap = new HashMap<Integer,String>();
        List<StudentProblemDto> problemStatuses = getJdbcTemplate().query(sql, new Object[] {assignKey}, new RowMapper<StudentProblemDto>() {
            @Override
            public StudentProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                StudentProblemDto prob = new StudentProblemDto();
                Integer uid = rs.getInt("uid");
                if (! nameMap.containsKey(uid)) {
                    nameMap.put(uid, rs.getString("user_name"));
                }
                prob.setUid(uid);
                ProblemDto dummy = new ProblemDto(rs.getInt("problem_id"), rs.getString("lesson"), rs.getString("label"), rs.getString("pid"));
                prob.setProblem(dummy);
                prob.setStatus(rs.getString("status"));
                return prob;
            }
        });

        /**
         * create student assignments for all users
         */
        int uid = 0;
        CmList<StudentProblemDto> probList = null;
        Map<Integer, StudentAssignment> stuAssignMap = new HashMap<Integer, StudentAssignment>();
        for (StudentProblemDto probDto : problemStatuses) {
            if (probDto.getUid() != uid) {
                probList = new CmArrayList<StudentProblemDto>();
                uid = probDto.getUid();
                StudentAssignment stuAssignment = new StudentAssignment(uid, assignment, probList);
                stuAssignment.setStudentName(nameMap.get(uid));
                stuAssignments.add(stuAssignment);
                stuAssignMap.put(uid,  stuAssignment);
            }
            probList.add(probDto);
        }

        /**
         * add lesson status for each user/lesson
         * add assignment status
         */
        uid = 0;
        String lessonName = "";
        int completed = 0;
        int pending = 0;
        int count = 0;
        int totCompleted = 0;
        int totPending = 0;
        int totCount = 0;
        CmList<StudentLessonDto> lessonList = null;
        StudentLessonDto lessonStatus = null;

        for (StudentProblemDto probDto : problemStatuses) {

        	if (probDto.getUid() != uid) {
        		if (lessonStatus != null) {
                	lessonStatus.setStatus(getLessonStatus(count, completed, pending));
        			stuAssignMap.get(uid).setHomeworkStatus(getHomeworkStatus(totCount, totCompleted, totPending));
        		}
    			lessonName = "";
    			totCount = 0;
    			totCompleted = 0;
    			totPending = 0;
    			uid = probDto.getUid();
    			lessonList = new CmArrayList<StudentLessonDto>();
    			stuAssignMap.get(uid).setLessonStatuses(lessonList);
        	}

        	if (! lessonName.equals(probDto.getProblem().getLesson())) {
        		if (lessonName.trim().length() > 0) {
        			if (lessonStatus != null) {
                    	lessonStatus.setStatus(getLessonStatus(count, completed, pending));
        			}
        		}
        		completed = 0;
        		pending = 0;
        		count = 0;
        		lessonName = probDto.getProblem().getLesson();
        		lessonStatus = new StudentLessonDto(uid, lessonName, null);
        		lessonList.add(lessonStatus);
        	}

        	count++;
        	totCount++;
        	String probStatus = probDto.getStatus().trim();
        	if ("-".equals(probStatus)) continue;
        	if ("answered".equalsIgnoreCase(probStatus) ||
        		"viewed".equalsIgnoreCase(probStatus)) {
        		completed++;
        		totCompleted++;
        	}
        	if ("pending".equalsIgnoreCase(probStatus)) {
        		pending++;
        		totPending++;
        	}
        }

        if (lessonStatus != null) {
        	lessonStatus.setStatus(getLessonStatus(count, completed, pending));
        }
		stuAssignMap.get(uid).setHomeworkStatus(getHomeworkStatus(totCount, totCompleted, totPending));

		if (__logger.isDebugEnabled())
    		__logger.debug("getAssignmentGradeBook(): stuAssignments.size(): " + stuAssignments.size());

        return stuAssignments;
    }

    private String getLessonStatus(int count, int completed, int pending) {
		return (pending != 0) ?
			String.format("%d of %d completed, %d pending", completed, count, pending) :
			String.format("%d of %d completed", completed, count);        				
    }

    private String getHomeworkStatus(int totCount, int totCompleted, int totPending) {
    	String status = "Not Started";
		if ((totCompleted + totPending) > 0) {
			status = ((totCompleted + totPending) < totCount) ? "In Progress" : "Ready to Grade";
		}
        return status;
    }

    /**
     * Assign students to assignment. Return messages indicating each error s
     * 
     * @param assignKey
     * @param students
     * @return
     */
    public AssignmentInfo assignStudents(final int assignKey, List<StudentDto> students) {

        int assignCount = 0, errorCount = 0;
        AssignmentInfo assignmentInfo = new AssignmentInfo();
        String messages = null;
        for (final StudentDto s : students) {

            try {
                getJdbcTemplate().update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        String sql = "insert into CM_ASSIGNMENT_USERS(assign_key, uid)values(?,?)";
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
                messages += "Error assigning student (" + s.getName() + "): " + e.getMessage() + "\n";
            }
        }
        assignmentInfo.setAssigned(assignCount);
        assignmentInfo.setErrors(errorCount);
        assignmentInfo.setMessage(messages);
        return assignmentInfo;
    }

    public void unassignStudents(final int assKey, final CmList<StudentAssignment> students) {
        getJdbcTemplate().batchUpdate("delete from CM_ASSIGNMENT_USERS where assign_key = ? and uid = ?",
                new BatchPreparedStatementSetter() {

                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setInt(1, assKey);
                        ps.setInt(2, students.get(i).getUid());
                    }

                    @Override
                    public int getBatchSize() {
                        return students.size();
                    }
                });

    }

    public List<GroupDto> getAssignmentGroups(int aid) {
        List<GroupDto> groups = new ArrayList<GroupDto>();
        try {
            List<GroupInfoModel> groupModels = CmAdminDao.getInstance().getActiveGroups(aid);

            List<Integer> inList = new ArrayList<Integer>();
            for (GroupInfoModel gm : groupModels) {
                inList.add(gm.getId());
            }

            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_GROUP_INFO");
            sql = QueryHelper.createInListSQL(sql, inList);
            groups.addAll(getJdbcTemplate().query(sql, new Object[] {}, new RowMapper<GroupDto>() {
                @Override
                public GroupDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String label = rs.getString("name") + " [" + rs.getInt("student_count") + ", "
                            + rs.getInt("assignment_count") + "]";
                    return new GroupDto(rs.getInt("group_id"), label);
                }
            }));

        } catch (Exception e) {
            __logger.error("Error getting assignments", e);
        }
        return groups;

    }

    public int getNumberOfIncompleteAssignments(int uid) {

        __logger.debug("Getting number of of assignments for '" + uid + "'");

        String sql = "select count(*) as cnt " + " from HA_USER u " + " JOIN CM_GROUP g on g.id = u.group_id "
                + " JOIN CM_ASSIGNMENT a on a.group_id = u.group_id " + " where u.uid = ? ";

        List<Integer> cnt = getJdbcTemplate().query(sql, new Object[] { uid }, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("cnt");
            }
        });

        __logger.warn("Assignments for '" + uid + "': " + cnt.get(0));
        return cnt.get(0);

    }

    public List<Assignment> getAssignmentsForUser(int uid) {

        String sql = "select a.* " + " from HA_USER u " + " JOIN CM_GROUP g on g.id = u.group_id "
                + " JOIN CM_ASSIGNMENT a on a.group_id = u.group_id " + " where u.uid = ? ";

        List<Assignment> problems = getJdbcTemplate().query(sql, new Object[] { uid }, new RowMapper<Assignment>() {
            @Override
            public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {

                // create a psudo name
                String comments = rs.getString("comments");
                String assignmentName = _createAssignmentName(rs.getDate("due_date"), comments);

                Assignment ass = new Assignment(rs.getInt("assign_key"), rs.getInt("group_id"), assignmentName, rs
                        .getString("comments"), rs.getDate("due_date"), null, null, rs.getString("status"));
                return ass;
            }
        });
        return problems;
    }

    private String _createAssignmentName(Date dueDate, String comments) {
        return "Due Date: " + dueDate + (comments != null ? " - " + comments : "");
    }

    public StudentAssignment getStudentAssignment(final int uid, int assignKey) throws Exception {

        StudentAssignment studentAssignment = new StudentAssignment();

        Assignment assignment = getAssignment(assignKey);
        studentAssignment.setAssignment(assignment);

        /**
         * Read list of assignment problem statues for this user
         * 
         * 
         */
        String sql = "select * from  CM_ASSIGNMENT_PID_STATUS where assign_key = ? and uid = ?";

        List<StudentProblemDto> problemStatuses = getJdbcTemplate().query(sql, new Object[] { assignKey, uid },
                new RowMapper<StudentProblemDto>() {
                    @Override
                    public StudentProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        StudentProblemDto prob = new StudentProblemDto();
                        prob.setUid(uid);
                        ProblemDto dummy = new ProblemDto(0, null, null, rs.getString("pid"));
                        prob.setProblem(dummy);
                        prob.setStatus(rs.getString("status"));
                        return prob;
                    }
                });

        /**
         * Make sure there is a status for each
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

        studentAssignment.setAssigmentStatuses(allStatus);
        return studentAssignment;
    }

    /**
     * Get Assignment data for specified assignment and student UIDs
     * 
     * @param assignKey
     * @param userIds
     * @return
     * @throws Exception
     */
    public CmList<StudentAssignment> getAssignmentForStudents(int assignKey, List<Integer> userIds) throws Exception {

        CmList<StudentAssignment> stuAssignments = new CmArrayList<StudentAssignment>();
        Assignment assignment = getAssignment(assignKey);

        String sqlTemplate = CmMultiLinePropertyReader.getInstance().getProperty("GET_GRADE_BOOK_DATA_2");

        String sql = QueryHelper.createInListSQL(sqlTemplate, userIds);
        if (__logger.isDebugEnabled())
            __logger.debug("+++ getStudentsWithActivityInDateRange(): sql: " + sql);

        /**
         * get assignment problem status list for all users
         */
        final Map<Integer, String> nameMap = new HashMap<Integer, String>();
        List<StudentProblemDto> problemStatuses = getJdbcTemplate().query(sql, new Object[] { assignKey },
                new RowMapper<StudentProblemDto>() {
                    @Override
                    public StudentProblemDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                        StudentProblemDto prob = new StudentProblemDto();
                        Integer uid = rs.getInt("uid");
                        if (!nameMap.containsKey(uid)) {
                            nameMap.put(uid, rs.getString("user_name"));
                        }
                        prob.setUid(uid);
                        ProblemDto dummy = new ProblemDto(rs.getInt("problem_id"), rs.getString("lesson"), rs
                                .getString("label"), rs.getString("pid"));
                        prob.setProblem(dummy);
                        prob.setStatus(rs.getString("status"));
                        return prob;
                    }
                });

        /**
         * create student assignments for all users
         */
        int uid = 0;
        CmList<StudentProblemDto> probList = null;
        for (StudentProblemDto probDto : problemStatuses) {
            if (probDto.getUid() != uid) {
                probList = new CmArrayList<StudentProblemDto>();
                uid = probDto.getUid();
                StudentAssignment stuAssignment = new StudentAssignment(uid, assignment, probList);
                stuAssignment.setStudentName(nameMap.get(uid));
                stuAssignments.add(stuAssignment);
            }
            probList.add(probDto);
        }

        return stuAssignments;
    }

    /**
     * Make sure there is a status record for this assignment PID
     * 
     * @param assignKey
     * @param uid
     * @param pid
     */
    public void setAssignmentPidStatus(final int assignKey, final int uid, final String pid) {
        try {
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
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
    }

    /**
     * Update a Student's Assignment status
     * 
     * @param studentAssignment
     * @return
     */
    public int[] updateStudentAssignmentStatus(StudentAssignment studentAssignment) {
    	List<StudentProblemDto> list = studentAssignment.getAssigmentStatuses();
    	StringBuilder sb = new StringBuilder();
    	for (StudentProblemDto sp : list) {
    		sb.append(String.format("label: %s, status: %s\n", sp.getPidLabel(), sp.getStatus()));
    	}
    	__logger.debug("problem-status: " + sb.toString());

    	List<Object[]> batch = new ArrayList<Object[]>();
    	for (StudentProblemDto sp : studentAssignment.getAssigmentStatuses()) {
    		Object[] values = new Object[] {
    				sp.getStatus(),
    				studentAssignment.getAssignment().getAssignKey(),
    				sp.getPid(),
    				studentAssignment.getUid()};
    		batch.add(values);
    	}
    	SimpleJdbcTemplate template = new SimpleJdbcTemplate(this.getDataSource());
    	int[] updateCounts = template.batchUpdate(
    			"update CM_ASSIGNMENT_PID_STATUS set status = ? where assign_key = ? and pid = ? and uid = ?",
    			batch);

    	//TODO: also update CM_ASSIGNMENT_PID_ANSWERS

    	return updateCounts;
    }

}
