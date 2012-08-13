package hotmath.cm.assignment;

import hotmath.gwt.cm_rpc.client.model.Assignment;
import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

public class AssignmentDao extends SimpleJdbcDaoSupport {
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
                    String sql = "insert into CM_ASSIGNMENT(aid,name,due_date,comments)values(?,?,?,?)";
                    PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, aid);
                    ps.setString(2, ass.getAssignmentName());
                    ps.setDate(3, new java.sql.Date(ass.getDueDate().getTime()));
                    ps.setString(4, ass.getComments());
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
                    String sql = "update CM_ASSIGNMENT set aid = ?, name = ?, due_date = ?, comments = ? where assign_key = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, aid);
                    ps.setString(2, ass.getAssignmentName());
                    ps.setDate(3, new java.sql.Date(ass.getDueDate().getTime()));
                    ps.setString(4, ass.getComments());
                    ps.setInt(5, ass.getAssignKey());
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

        // /**
        // * save the UIDS who have been assigned this Assignment.
        // *
        // MOVE TO AssignStudents
        // */
        // String sqlUids =
        // "insert into CM_ASSIGNMENT_USERS(assign_key, uid)values(?,?)";
        // getJdbcTemplate().batchUpdate(sqlUids, new
        // BatchPreparedStatementSetter() {
        //
        // @Override
        // public void setValues(PreparedStatement ps, int i) throws
        // SQLException {
        // ps.setInt(1, assKey);
        // ps.setInt(2, ass.getUids().get(i).intValue());
        // }
        //
        // @Override
        // public int getBatchSize() {
        // return ass.getUids().size();
        // }
        // });

        return assKey;

    }

    /**
     * Fetch a persisted Assignment from DB
     * 
     * @param assKey
     * @return
     */
    public Assignment getAssignment(int assKey) throws CmException {

        String sql = "select * from CM_ASSIGNMENT where assign_key = ?";
        Assignment assignment = null;
        try {
            assignment = getJdbcTemplate().queryForObject(sql, new Object[] { assKey }, new RowMapper<Assignment>() {
                @Override
                public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new Assignment(rs.getInt("assign_key"), rs.getString("name"), rs.getString("comments"), rs
                            .getDate("due_date"), null, null);
                }
            });
        } catch (Exception e) {
            throw new CmException("Assignment not found: " + assKey, e);
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
                return new ProblemDto(0, rs.getString("lesson"), label, rs.getString("pid"));
            }
        });
        CmList<ProblemDto> cmPids = new CmArrayList<ProblemDto>();
        cmPids.addAll(pids);
        assignment.setPids(cmPids);

        /**
         * Find all users assigned to this assignemnt
         * 
         */
        sql = "select * from CM_ASSIGNMENT_USERS where assign_key = ? order by id";
        List<Integer> uids = getJdbcTemplate().query(sql, new Object[] { assKey }, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new Integer(rs.getInt("uid"));
            }
        });
        assignment.setUids(uids);

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

    public List<Assignment> getAssignments(int aid) {
        String sql = "select * from CM_ASSIGNMENT where aid  = ? order by assign_key";

        List<Assignment> problems = getJdbcTemplate().query(sql, new Object[] { aid }, new RowMapper<Assignment>() {
            @Override
            public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {
                Assignment ass = new Assignment(rs.getInt("assign_key"), rs.getString("name"),
                        rs.getString("comments"), rs.getDate("due_date"), null, null);
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
        
        /** Delete the assignment record
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

    public List<StudentDto> getAssignmentGradeBook(final int assignKey) {
        String sql = "select u.uid, u.user_name " +
                     "from HA_USER u " + 
                     " join CM_ASSIGNMENT_USERS au on au.uid = u.uid " +
                     " where au.assign_key = ? " +
                     " order by u.user_name";
        List<StudentDto> students = getJdbcTemplate().query(sql, new Object[] { assignKey }, new RowMapper<StudentDto>() {
            @Override
            public StudentDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new StudentDto(rs.getInt("uid"), rs.getString("user_name"));
            }
        });        
        return students;
    }

    /** Assign students to assignment.  Return messages indicating each error
     *  s
     * @param assignKey
     * @param students
     * @return
     */
    public AssignmentInfo assignStudents(final int assignKey, List<StudentDto> students) {

        int assignCount=0,errorCount=0;
        AssignmentInfo assignmentInfo = new AssignmentInfo();
        String messages=null;
        for(final StudentDto s: students) {
            
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
            }
            catch(Exception e) {
                errorCount++;
                if(messages == null) {
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
}
