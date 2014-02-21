package hotmath.testset.ha;

import hotmath.ProblemID;
import hotmath.SolutionManager;
import hotmath.cm.assignment.AssignmentDao;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.model.LessonModel;
import hotmath.gwt.cm_rpc.client.model.SolutionMeta;
import hotmath.gwt.cm_rpc.client.model.SolutionMetaStep;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.shared.server.service.command.GetSolutionCommand;
import hotmath.gwt.solution_editor.client.StepUnitPair;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.solution.TutorProblem;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;
import hotmath.gwt.solution_editor.server.solution.TutorStepUnit;
import hotmath.solution.Solution;
import hotmath.spring.SpringManager;
import hotmath.util.HMConnectionPool;
import hotmath.util.HmContentExtractor;
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
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
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
         * If teacher_id not set, try to look up before search
         * 
         */
        if (problem.getTeacher().getTeacherId() == 0) {
            String sql = "select teacher_id from CM_CUSTOM_PROBLEM_TEACHER where admin_id = ? and teacher_name = ?";
            List<Integer> tIds = getJdbcTemplate().query(sql, new Object[] { problem.getTeacher().getAdminId(), problem.getTeacher().getTeacherName() },
                    new RowMapper<Integer>() {
                        @Override
                        public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return rs.getInt("teacher_id");
                        }
                    });
            if (tIds.size() > 0) {
                problem.getTeacher().setTeacherId(tIds.get(0));
            }
        }

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
            addNewTeacher(problem.getTeacher().getAdminId(), problem.getTeacherName());
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

    public List<CustomProblemModel> getCustomProblemsFor(final TeacherIdentity teacher) throws Exception {
        String sql =
                "select cp.*, ct.admin_id, ct.teacher_name, s.solutionxml " +
                        " from   CM_CUSTOM_PROBLEM cp " +
                        " JOIN CM_CUSTOM_PROBLEM_TEACHER ct " +
                        " on ct.teacher_id = cp.teacher_id " +
                        " JOIN SOLUTIONS s " +
                        " on s.problemindex = cp.pid " +
                        " where  (ct.admin_id = ?) " +
                        " order  by ct.teacher_name, teacher_problem_number ";

        List<CustomProblemModel> problems = getJdbcTemplate().query(sql, new Object[] { teacher.getAdminId() }, new RowMapper<CustomProblemModel>() {
            @Override
            public CustomProblemModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                CustomProblemModel cp = new CustomProblemModel(rs.getString("pid"), rs.getInt("teacher_problem_number"), new TeacherIdentity(rs
                        .getInt("admin_id"), rs.getString("teacher_name"), rs.getInt("teacher_id")), rs.getString("comments"), SolutionDao.determineProblemType(rs.getString("solutionxml")));
                cp.setLinkedLessons(getCustomProblemLinkedLessons(cp.getPid()));
                return cp;
            }
        });

        // transfer into shared object, and call to
        // set problem types.
        //
        // TODO: make generic way of getting problem type
        List<ProblemDto> probs = new ArrayList<ProblemDto>();
        for (CustomProblemModel cpm : problems) {
            probs.add(new ProblemDto(0, 0, null, null, cpm.getPid(), 0));
        }
        AssignmentDao.getInstance().updateProblemTypes(probs);
        for (int i = 0, t = problems.size(); i < t; i++) {
            problems.get(i).setProblemType(probs.get(i).getProblemType());
        }
        return problems;
    }

    protected List<LessonModel> getLinkedLessons(TeacherIdentity teacher, String pid) {
        // TODO Auto-generated method stub
        return null;
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

        String START_TOKEN = "hm_flash_widget_def";

        int startPos = html.indexOf(START_TOKEN);
        if (startPos == -1) {
            return null;
        }

        startPos = html.indexOf("{", startPos);
        int endPos = html.indexOf("}", startPos);
        String json = html.substring(startPos, endPos + 1);

        return json;
    }

    public void saveProblemWidget(Connection conn, String pid, String data) throws Exception {
        CmSolutionManagerDao dao = new CmSolutionManagerDao();
        TutorSolution solution = dao.getTutorSolution(conn, pid);

        String jsonHtml = "";
        if (data != null) {
            jsonHtml = "<div id='hm_flash_widget'><div id='hm_flash_widget_def' style='display: none'>";
            jsonHtml += data;
            jsonHtml += "</div></div>";
        }

        TutorProblem prob = solution.getProblem();
        String htmlNoWidget = stripWidgetFromHtml(prob.getStatement());

        solution.getProblem().setStatement(htmlNoWidget + jsonHtml);
        SolutionDef def = new SolutionDef(pid);

        List<StepUnitPair> steps = new ArrayList<StepUnitPair>();
        for (int i = 0, t = prob.getStepUnits().size(); i < t; i += 2) {
            TutorStepUnit hint = prob.getStepUnits().get(i);
            TutorStepUnit step = prob.getStepUnits().get(i + 1);
            steps.add(new StepUnitPair(hint.getContentAsString(), step.getContentAsString(), step.getFigure()));
        }

        TutorSolution ts = new TutorSolution("sm", def, prob.getStatement(), prob.getStatementFigure(), steps, true);
        dao.saveSolutionXml(conn, pid, ts.toXml(), solution.getTutorDefine(), true);
    }

    static public String stripWidgetFromHtml(String html) {
        String START_TOKEN = "<div id='hm_flash_widget'";
        int startPos = html.indexOf(START_TOKEN);
        if (startPos == -1) {
            return html;
        }
        int endPos = html.indexOf("</div>", startPos);
        endPos = html.indexOf("</div>", endPos + 1);

        String htmlNew = html.substring(0, startPos);
        htmlNew += html.substring(endPos + 6);

        return htmlNew;
    }

    public void saveProblemHintStep(Connection conn, String pid, SolutionMeta solutionMeta) throws Exception {
        CmSolutionManagerDao dao = new CmSolutionManagerDao();
        List<StepUnitPair> steps = new ArrayList<StepUnitPair>();
        for (int i = 0, t = solutionMeta.getSteps().size(); i < t; i++) {
            SolutionMetaStep sh = solutionMeta.getSteps().get(i);
            steps.add(new StepUnitPair(sh.getHint(), sh.getText(), sh.getFigure()));
        }

        SolutionDef def = new SolutionDef(pid);
        TutorSolution ts = new TutorSolution("sm", def, solutionMeta.getProblemStatement(), solutionMeta.getFigure(), steps, true);
        dao.saveSolutionXml(conn, pid, ts.toXml(), solutionMeta.getTutorDefine(), true);
    }

    public void setCustomProblemLinkedLessons(int adminId, final String pid, final CmList<LessonModel> lessons) {

        // Delete any existing linked lessons
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from CM_CUSTOM_PROBLEM_LINKED_LESSONS where pid = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, pid);
                return ps;
            }
        });

        // insert each linked lesson
        String sql = "insert into CM_CUSTOM_PROBLEM_LINKED_LESSONS(pid, lesson_name, lesson_file)values(?,?,?)";
        getJdbcTemplate().batchUpdate(sql,
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        LessonModel lesson = lessons.get(i);

                        ps.setString(1, pid);
                        ps.setString(2, lesson.getLessonName());
                        ps.setString(3, lesson.getLessonFile());
                    }

                    @Override
                    public int getBatchSize() {
                        return lessons.size();
                    }
                });
    }

    public void setCustomProblemComment(final String pid, final String comments) {
        // Delete any existing linked lessons
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "update CM_CUSTOM_PROBLEM set comments = ? where pid = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, comments);
                ps.setString(2, pid);
                return ps;
            }
        });
    }

    public List<LessonModel> getCustomProblemLinkedLessons(String pid) {
        try {
            String sql = "select * from CM_CUSTOM_PROBLEM_LINKED_LESSONS where pid = ? order by lesson_name";
            List<LessonModel> problems = getJdbcTemplate().query(sql, new Object[] { pid }, new RowMapper<LessonModel>() {
                @Override
                public LessonModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new LessonModel(rs.getString("lesson_name"), rs.getString("lesson_file"));
                }
            });
            return problems;
        } catch (Exception e) {
            __logger.error("Error getting custom pid linked lessons", e);
        }

        return new ArrayList<LessonModel>();
    }

    public List<CustomProblemModel> getCustomProblemsLinkedToLesson(String lessonFile) {
        String sql = "select distinct cp.pid, cp.teacher_id, cp.comments, cp.teacher_problem_number, cl.lesson_name, cl.lesson_file, s.solutionxml" +
                     " from CM_CUSTOM_PROBLEM_LINKED_LESSONS cl " +
                     " JOIN CM_CUSTOM_PROBLEM  cp " +
                     " on cp.pid = cl.pid " +
                     " JOIN SOLUTIONS s " +
                     " on s.problemindex = cp.pid " +
                     " where cl.lesson_file = ?";
        
        List<CustomProblemModel> problems = getJdbcTemplate().query(sql, new Object[] { lessonFile }, new RowMapper<CustomProblemModel>() {
            @Override
            public CustomProblemModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CustomProblemModel(rs.getString("pid"), rs.getInt("teacher_problem_number"), getTeacherIdentity(rs.getInt("teacher_id")), rs.getString("comments"), SolutionDao.determineProblemType(rs.getString("solutionxml")));
            }
        });
        return problems;
    }

    protected TeacherIdentity getTeacherIdentity(int teacherId) {
        String sql = "select * from CM_CUSTOM_PROBLEM_TEACHER where teacher_id = ?";
        return getJdbcTemplate().queryForObject(sql, new Object[]{teacherId}, new RowMapper<TeacherIdentity>() {
            public TeacherIdentity mapRow(ResultSet rs, int rowNum) throws SQLException {
                return _extractTeacherIdentity(rs);
            }
        });
    }

    /** Return list of teachers defined for this admin
     * 
     * @param adminId
     * @return
     */
    public List<TeacherIdentity> getAdminTeachers(int adminId) {
        String sql = "select * from CM_CUSTOM_PROBLEM_TEACHER where admin_id = ? order by teacher_name";
        return getJdbcTemplate().query(sql, new Object[]{adminId}, new RowMapper<TeacherIdentity>() {
            public TeacherIdentity mapRow(ResultSet rs, int rowNum) throws SQLException {
                return _extractTeacherIdentity(rs);
            }
        });
    }

    protected TeacherIdentity _extractTeacherIdentity(ResultSet rs) throws SQLException {
        return new TeacherIdentity(rs.getInt("admin_id"), rs.getString("teacher_name"), rs.getInt("teacher_id"));        
    }

    /** Add a new teacher and return the next problem_number
     * 
     * @param adminId
     * @param teacherName
     * @return
     */
    public void addNewTeacher(final int adminId, final String teacherName) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into CM_CUSTOM_PROBLEM_TEACHER(admin_id, teacher_name, max_problem_number)values(?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, adminId);
                ps.setString(2, teacherName);
                ps.setInt(3, 1);
                return ps;
            }
        }, keyHolder);
    }

    /** Setup an editable whitebaord in
     * SOLUTION_WHITEBOARD 
     * 
     *  Return a unique key used to update this temporary whiteboard
     *  
     * @param pid
     */
    public String setupForWhiteboardEditing(final String pid) {
        final String pidEdit = pid + "!" + System.currentTimeMillis();
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into SOLUTION_WHITEBOARD_temp (pid_edit,pid,wb_id,wb_command, wb_data,insert_time_mills) " +
                             " select ? as pid_edit, pid, wb_id,wb_command, wb_data, insert_time_mills " +
                             " from  SOLUTION_WHITEBOARD " +
                             " where pid = ? " + 
                             " order by insert_time_mills";
                
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1, pidEdit);
                ps.setString(2, pid);
                return ps;
            }
        });
        return pidEdit;
    }

    /** Commit changes to temporary whiteboard into main whiteboard table
     *  and remove work entries for whiteboard changes.
     * 
     * @param pid
     */
    public void commitWhiteboardEditing(final String pidEdit, final String pid) {
        
        /** Delete existing whiteboard
         * 
         */
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from SOLUTION_WHITEBOARD where pid = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1,  pid);
                return ps;
            }
        });
        
        /** Insert new records into main whiteboard table
         * 
         */
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into SOLUTION_WHITEBOARD(pid,wb_id,wb_command, wb_data,insert_time_mills) " +
                        " select pid, wb_id,wb_command, wb_data, insert_time_mills " +
                        " from  SOLUTION_WHITEBOARD_temp " +
                        " where pid_edit = ? " + 
                        " order by insert_time_mills";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1,  pidEdit);
                return ps;
            }
        });
        
        /** Remove the temporary/work area for this pid
         * 
         */
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "delete from SOLUTION_WHITEBOARD_temp where pid_edit = ?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1,  pidEdit);
                return ps;
            }
        });
    }

    public String copyCustomProblem(String pid) throws Exception {
        /** Extract information about this existing Custom Problem
         * 
         */
        final String solutionXml = SolutionDao.getInstance().getSolutionXML(pid);
        String sql = "select ct.admin_id, ct.teacher_name, cp.* from CM_CUSTOM_PROBLEM cp JOIN CM_CUSTOM_PROBLEM_TEACHER ct on ct.teacher_id = cp.teacher_id where pid = ?";
        final CustomProblemModel problemToCopy = getJdbcTemplate().queryForObject(sql, new Object[]{pid}, new RowMapper<CustomProblemModel>() {
            public CustomProblemModel mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new CustomProblemModel(rs.getString("pid"),  rs.getInt("teacher_problem_number"),new TeacherIdentity(rs.getInt("admin_id"), rs.getString("teacher_name"), rs.getInt("teacher_id")), rs.getString("comments"), SolutionDao.determineProblemType(solutionXml));
            }
        });
        
        CustomProblemModel newProblem = new CustomProblemModel(null, 0, problemToCopy.getTeacher(), null, problemToCopy.getProblemType());
        final SolutionInfo newSolution = createNewCustomProblem(newProblem);
        
        /** copy the existing solutionxml into new solution
         * 
         */
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            new CmSolutionManagerDao().saveSolutionXml(conn, newSolution.getPid(), solutionXml,null,true );
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        
        
        /** Copy over any static whiteboard
         * 
         */
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                String sql = "insert into SOLUTION_WHITEBOARD(pid,wb_id,wb_command, wb_data,insert_time_mills) " +
                        " select ? as pid, wb_id,wb_command, wb_data, insert_time_mills " +
                        " from  SOLUTION_WHITEBOARD " +
                        " where pid = ? " + 
                        " order by insert_time_mills";

                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1,  newSolution.getPid());
                ps.setString(2,  problemToCopy.getPid());
                return ps;
            }
        });
        
        
        
        return newSolution.getPid();
    }    
    
}
