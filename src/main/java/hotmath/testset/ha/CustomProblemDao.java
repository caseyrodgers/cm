package hotmath.testset.ha;

import hotmath.ProblemID;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.model.SolutionMetaStep;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;
import hotmath.gwt.solution_editor.client.StepUnitPair;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorProblem;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;
import hotmath.gwt.solution_editor.server.solution.TutorStepUnit;
import hotmath.spring.SpringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

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

        SolutionInfo solution = null;
        /**
         * get unique identifier for this teacher
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
        if (probNum.size() == 0) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "insert into CM_CUSTOM_PROBLEM_TEACHER(admin_id, teacher_name, max_problem_number)values(?,?,?)";
                    PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, problem.getTeacher().getAdminId());
                    ps.setString(2, problem.getTeacher().getTeacherName());
                    ps.setInt(3, 1);
                    return ps;
                }
            }, keyHolder);
            problem.getTeacher().setTeacherId(keyHolder.getKey().intValue());
            problemNumber = 1;
        }
        else {
            final int nextProbNum = probNum.get(0) + 1;
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

        /**
         * Create the actual solution and store in SOLUTIONS
         * 
         */
        final String newPid = createNewCustomProblemPid(problem.getTeacher().getTeacherId(), problemNumber);
        Connection conn = null;
        try {
            String CUSTOM_WHITEBOARD_XML_PROBLEM_STATMENT = "<![CDATA[<div class='cm_whiteboard' wb_id='wb_ps'></div>]]>";
            conn = getJdbcTemplate().getDataSource().getConnection();
            new CmSolutionManagerDao().createNewSolution(getJdbcTemplate().getDataSource().getConnection(), newPid, CUSTOM_WHITEBOARD_XML_PROBLEM_STATMENT);

            GetSolutionAction action = new GetSolutionAction(0, 0, newPid);
            solution = new GetSolutionCommand().execute(conn, action);
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }

        /**
         * Create the associated whiteboard object.
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
                ps.setString(4, "");
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
                ps.setInt(3, problemNumber);
                ps.setString(4, problem.getComments());

                return ps;
            }
        });

        return solution;
    }

    /**
     * Create a new solution PID
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
        ProblemID newPid = new ProblemID(textCode, chapter, section, probNum, probSet, pageNum);

        return newPid.getGUID();
    }

    public List<CustomProblemModel> getCustomProblemsFor(final TeacherIdentity teacher) {
        String sql =
                "select cp.*, ct.admin_id, ct.teacher_name" +
                        " from   CM_CUSTOM_PROBLEM cp " +
                        " JOIN CM_CUSTOM_PROBLEM_TEACHER ct " +
                        " on ct.teacher_id = cp.teacher_id " +
                        " where  ct.admin_id = ? " +
                        " order  by teacher_problem_number ";

        List<CustomProblemModel> problems = getJdbcTemplate().query(sql, new Object[] { teacher.getAdminId() }, new RowMapper<CustomProblemModel>() {
            @Override
            public CustomProblemModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CustomProblemModel(rs.getString("pid"), rs.getInt("teacher_problem_number"), new TeacherIdentity(rs.getInt("admin_id"), rs.getString("teacher_name"), rs.getInt("teacher_id")), rs.getString("comments"));
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
    
    
    static public String extractWidgetJson(String html) {

        String START_TOKEN="hm_flash_widget_def";
        
        int startPos = html.indexOf(START_TOKEN);
        if(startPos == -1) {
            return null;
        }
        
        startPos = html.indexOf("{", startPos);
        int endPos = html.indexOf("}", startPos);
        String json = html.substring(startPos, endPos+1);
        
        return json;
     }    

    public void saveProblemWidget(Connection conn, String pid, String data) throws Exception {
        CmSolutionManagerDao dao = new CmSolutionManagerDao();
        TutorSolution solution = dao.getTutorSolution(conn, pid);
        
        
        String jsonHtml = "";
        if(data != null) {
            jsonHtml = "<div id='hm_flash_widget'><div id='hm_flash_widget_def' style='display: none'>";
            jsonHtml += data;
            jsonHtml += "</div></div>";
        }

        TutorProblem prob = solution.getProblem();
        String htmlNoWidget = stripWidgetFromHtml(prob.getStatement());
        
        solution.getProblem().setStatement(htmlNoWidget + jsonHtml);
        SolutionDef def = new SolutionDef(pid);
        
        
        List<StepUnitPair> steps = new ArrayList<StepUnitPair>();
        for(int i=0, t=prob.getStepUnits().size();i<t;i+=2) {
            TutorStepUnit hint = prob.getStepUnits().get(i);
            TutorStepUnit step = prob.getStepUnits().get(i+1);
            steps.add(new StepUnitPair(hint.getContentAsString(), step.getContentAsString(), step.getFigure()));
        }
        
        TutorSolution ts = new TutorSolution("sm", def, prob.getStatement(),prob.getStatementFigure(),steps ,true);
        dao.saveSolutionXml(conn, pid, ts.toXml(), solution.getTutorDefine(), true);
    }
    
    static public String stripWidgetFromHtml(String html) {
        String START_TOKEN="<div id='hm_flash_widget'";
        int startPos = html.indexOf(START_TOKEN);
        if(startPos == -1) {
            return html;
        }
        int endPos = html.indexOf("</div>", startPos);
        endPos = html.indexOf("</div>", endPos+1);

        String htmlNew = html.substring(0, startPos);
        htmlNew += html.substring(endPos+6);
        
        return htmlNew;    
    }

    public void saveProblemHintStep(Connection conn, String pid, SolutionMeta solutionMeta) throws Exception {
        CmSolutionManagerDao dao = new CmSolutionManagerDao();
        List<StepUnitPair> steps = new ArrayList<StepUnitPair>();
        for(int i=0, t=solutionMeta.getSteps().size();i<t;i++) {
            SolutionMetaStep sh = solutionMeta.getSteps().get(i);
            steps.add(new StepUnitPair(sh.getHint(), sh.getText(), sh.getFigure()));
        }
        
        SolutionDef def = new SolutionDef(pid);
        TutorSolution ts = new TutorSolution("sm", def, solutionMeta.getProblemStatement(),solutionMeta.getFigure(),steps ,true);
        dao.saveSolutionXml(conn, pid, ts.toXml(), solutionMeta.getTutorDefine(), true);
    }
}
