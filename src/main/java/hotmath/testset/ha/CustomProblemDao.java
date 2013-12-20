package hotmath.testset.ha;

import hotmath.ProblemID;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Provide DAO functionality for Whiteboards
 * 
 * @author casey
 * 
 */
public class CustomProblemDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(CustomProblemDao.class);

    static private CustomProblemDao __instance;

    static public CustomProblemDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (CustomProblemDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(CustomProblemDao.class.getName());
        }
        return __instance;
    }

    private CustomProblemDao() {
        /** Empty */
    }

    public SolutionInfo createNewCustomProblem(final CustomProblemModel problem) throws Exception {
        
        SolutionInfo solution=null;
        /** get unique identifier for this teacher
         * 
         * 
         * record will never be zero ..? 
         */
        String sql = "select max_problem_number from CM_CUSTOM_PROBLEM_TEACHER where teacher_id = ?";
        List<Integer> probNum = getJdbcTemplate().query(sql, new Object[] { problem.getTeacher().getTeacherId() }, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("max_problem_number");
            }
        });
        
        final int problemNumber;
        if(probNum.size() == 0) {
            // add new record
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "insert into CM_CUSTOM_PROBLEM_TEACHER(teacher_id, teacher_name, max_problem_number)values(?,?,?)";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, problem.getTeacher().getTeacherId());
                    ps.setString(2, problem.getTeacher().getTeacherName());
                    ps.setInt(3, 1);
                    return ps;
                }
            });
            problemNumber=1;
        }
        else {
            final int nextProbNum = probNum.get(0)+1;
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "update CM_CUSTOM_PROBLEM_TEACHER set max_problem_number = ?  where teacher_id = ?";
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, nextProbNum);
                    ps.setInt(2, problem.getTeacher().getTeacherId());
                    return ps;
                }
            });
            
            problemNumber = nextProbNum;
        }
        
        /** Create the actual solution and store in SOLUTIONS
         * 
         */
        final String newPid = createNewCustomProblemPid(problem.getTeacher().getTeacherId(), problemNumber);
        Connection conn=null;
        try {
            String CUSTOM_WHITEBOARD_XML_PROBLEM_STATMENT = "<![CDATA[<div style='height: 300px;' class='cm_whiteboard' id='wb_ps'></div>]]>";
            conn = getJdbcTemplate().getDataSource().getConnection();
            new CmSolutionManagerDao().createNewSolution(getJdbcTemplate().getDataSource().getConnection(), newPid, CUSTOM_WHITEBOARD_XML_PROBLEM_STATMENT);
            
            GetSolutionAction action = new GetSolutionAction(0, 0, newPid);
            solution = new GetSolutionCommand().execute(conn, action);
        }
        finally {
            SqlUtilities.releaseResources(null,  null,  conn);
        }
        
        
        /** Create the associated whiteboard object. 
         * 
         * for now, only problems with a static whiteboard are custom problems.
         * 
         */
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into SOLUTION_WHITEBOARD(pid, wb_id, wb_command, wb_data, insert_time_mills)values(?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, newPid);
                ps.setString(2, "wb_ps");
                ps.setString(3, "clear");
                ps.setString(4,  "");
                ps.setLong(5, System.currentTimeMillis());
                
                return ps;
            }
        });        
        
        
        // track new problems created by this teacher
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into CM_CUSTOM_PROBLEM(pid,teacher_id, teacher_problem_number, comments)values(?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, newPid);
                ps.setInt(2, problem.getTeacher().getTeacherId());
                ps.setInt(3,  problemNumber);
                ps.setString(4, problem.getComments());
                
                return ps;
            }
        });
                
        
        return solution;
    }

    /** Create a new solution PID
     * 
     * @param teacherId
     * @param problemNumber
     * @return
     * @throws Exception
     */
    private String createNewCustomProblemPid(int teacherId, int problemNumber) throws Exception {
        
        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
        String textCode = "custom";
        String chapter = teacherId + "";
        String section = format.format(new Date());
        String probNum = problemNumber + "";
        String probSet = "set1";
        int pageNum = 1;
        ProblemID newPid = new ProblemID(textCode, chapter, section,probNum, probSet,pageNum);

        return newPid.getGUID();
    }

    public List<CustomProblemModel> getCustomProblemsFor(final TeacherIdentity teacher) {
        String sql = 
                "select cp.*, ct.teacher_name" +
                " from   CM_CUSTOM_PROBLEM cp " +
                " JOIN CM_CUSTOM_PROBLEM_TEACHER ct " +
                " on ct.teacher_id = cp.teacher_id " +
                " where  ct.teacher_id = ? " +
                " order  by teacher_problem_number "; 

        List<CustomProblemModel> problems = getJdbcTemplate().query(sql, new Object[] { teacher.getTeacherId() }, new RowMapper<CustomProblemModel>() {
            @Override
            public CustomProblemModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CustomProblemModel(rs.getString("pid"), rs.getInt("teacher_problem_number"), new TeacherIdentity(rs.getInt("teacher_id"),rs.getString("teacher_name")), rs.getString("comments"));
            }
        });   
        return problems;
    }

    public void deleteCustomProblem(final CustomProblemModel problem) throws Exception {
        
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from CM_CUSTOM_PROBLEM where pid = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, problem.getPid());
                return ps;
            }
        });
        
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from SOLUTIONS where problemindex = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, problem.getPid());
                return ps;
            }
        });
        
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from SOLUTION_WHITEBOARD where pid = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, problem.getPid());
                return ps;
            }
        });


        
    }
}
