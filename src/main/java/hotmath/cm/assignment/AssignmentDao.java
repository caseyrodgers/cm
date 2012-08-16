package hotmath.cm.assignment;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.cm.util.PropertyLoadFileException;
import hotmath.cm.util.QueryHelper;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.model.Assignment;
import hotmath.gwt.cm_rpc.client.model.AssignmentLessonData;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentDto;
import hotmath.gwt.cm_rpc.client.model.assignment.SubjectDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
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
                    String sql = "insert into CM_ASSIGNMENT(aid,group_id,name,due_date,comments,last_modified)values(?,?,?,?,?,?)";
                    PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, aid);
                    ps.setInt(2, ass.getGroupId());
                    ps.setString(3, ass.getAssignmentName());
                    ps.setDate(4, new java.sql.Date(ass.getDueDate().getTime()));
                    ps.setString(5, ass.getComments());
                    ps.setDate(6, new Date(System.currentTimeMillis()));
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
                    String sql = "update CM_ASSIGNMENT set aid = ?, name = ?, due_date = ?, comments = ?, last_modified = now() where assign_key = ?";
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
                    return new Assignment(rs.getInt("assign_key"),rs.getInt("group_id"), rs.getString("name"), rs.getString("comments"), rs
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

    public List<Assignment> getAssignments(int aid, int groupId) throws PropertyLoadFileException {
        String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENTS_FOR_GROUP");

        List<Assignment> problems = getJdbcTemplate().query(sql, new Object[] {groupId }, new RowMapper<Assignment>() {
            @Override
            public Assignment mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                // create a psudo name
                String comments = rs.getString("comments");
                String assignmentName = rs.getDate("due_date") + (comments!=null?" - " + comments:"");
                
                Assignment ass = new Assignment(rs.getInt("assign_key"),rs.getInt("group_id"), assignmentName,
                        rs.getString("comments"), rs.getDate("due_date"), null, null);
                
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

    public void unassignStudents(final int assKey, final CmList<StudentDto> students) {
        getJdbcTemplate().batchUpdate("delete from CM_ASSIGNMENT_USERS where assign_key = ? and uid = ?", new BatchPreparedStatementSetter() {

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
            for(GroupInfoModel gm: groupModels) {
                inList.add(gm.getId());
            }
            
            
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ASSIGNMENT_GROUP_INFO");
            sql = QueryHelper.createInListSQL(sql, inList);
            groups.addAll(getJdbcTemplate().query(sql, new Object[] {  }, new RowMapper<GroupDto>() {
                @Override
                public GroupDto mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String label = rs.getString("name") + " [" + rs.getInt("student_count") + ", " + rs.getInt("assignment_count") + "]";
                    return new GroupDto(rs.getInt("group_id"), label);
                }
            }));        
           
            
        }
        catch(Exception e) {
            __logger.error("Error getting assignments", e);
        }
        return groups;
        
    }

}
