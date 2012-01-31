package hotmath.gwt.cm_admin.server.model;

import hotmath.SolutionManager;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizId;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.solution.Solution;
import hotmath.solution.SolutionPostProcess;
import hotmath.spring.SpringManager;
import hotmath.util.VelocityTemplateFromStringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import sb.util.SbFile;

/**
 * try to centralize quiz db operations
 * 
 * @author casey
 * 
 */

public class CmQuizzesDao extends SimpleJdbcDaoSupport  {
    
    final static Logger __logger = Logger.getLogger(CmQuizzesDao.class);
    
    
    static private CmQuizzesDao __instance;
    static public CmQuizzesDao getInstance() throws Exception {
        if(__instance == null) {
            __instance = (CmQuizzesDao)SpringManager.getInstance().getBeanFactory().getBean("cmQuizzesDao");
        }
        return __instance;
    }
    
    private CmQuizzesDao() {/* empty */}
    
    public int saveCustomQuiz(final Connection conn, int adminId, String cpName, List<CustomQuizId> ids) throws Exception {
        

        deleteCustomQuiz(conn, adminId, cpName);
        
        /** add custom quiz def */
        PreparedStatement ps = null;
        int quizId=0;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("ADD_CUSTOM_QUIZ");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);
            ps.setString(2, cpName);
            int result = ps.executeUpdate();
            __logger.debug("Added custom quiz: " + result);
            
            if(result != 1) {
                throw new Exception("Could not create new custom problem, see server for details.");
            }
            
            quizId = SqlUtilities.getLastInsertId(conn);
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
        if(quizId == 0) {
            throw new Exception("Could not find quiz id");
        }
        
        /** add custom quiz ids */
        ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("ADD_CUSTOM_QUIZ_IDS");
            ps = conn.prepareStatement(sql);
            
            for(CustomQuizId id: ids) {
                ps.setInt(1, quizId);
                ps.setString(2, id.getPid());
                ps.setInt(3, id.getLoadOrder());
                int result = ps.executeUpdate();
                __logger.debug("Added custom quiz id: " + result);
                
                if(result != 1) {
                    throw new Exception("Could not add new custom quiz id, see server for details.");
                }
            }
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }  
        
        return quizId;
    }

    /**
     * Return question html for questions from texts that are at or below the
     * named grade level.
     * 
     * @param conn
     * @param lesson
     * @param gradeLevel
     * @return
     * @throws Exception
     */
    public CmList<QuizQuestion> getQuestionsFor(final Connection conn, String lesson, String subject) throws Exception {
        return getQuestionsFor(conn, lesson, subject, true);
    }

    
    public CmList<QuizQuestion> getQuestionsFor(final Connection conn, String lessonFile, String subject, boolean getHtml) throws Exception {
        CmList<QuizQuestion> list = new CmArrayList<QuizQuestion>();

        PreparedStatement ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_LESSON_QUIZZES");
            ps = conn.prepareStatement(sql);
            ps.setString(1, lessonFile);
            int gradeLevel = 999; // get all for now getGradeLevelFor(subject);
            ps.setInt(2, gradeLevel);

            int num=1;
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String questionId = rs.getString("program_name") + ": " + (num);
                String pid = rs.getString("guid");
                String quizHtml="";
                int correctAnswer=-1;
                if(getHtml) {
                    QuizQuestionParsed quizQuestion = getQuestionHtml(conn, num, pid);
                    quizHtml = quizQuestion.getStatement();
                    correctAnswer = quizQuestion.getCorrectAnswer();
                }
                num++;
                
                list.add(new QuizQuestion(questionId,lessonFile, rs.getString("program_name"), pid, quizHtml, correctAnswer));
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        return list;
    }
    
    
    /** Remove named custom query from admin's list
     * 
     * @param conn
     * @param adminId
     * @param name
     * @throws Exception
     */
    public boolean deleteCustomQuiz(final Connection conn, int adminId, String cpName) throws Exception {
        
        PreparedStatement ps=null;
        
        boolean wasDeleted=false;
        
        /** delete custom quiz ids */
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("DELETE_CUSTOM_QUIZ_IDS");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);
            ps.setString(2, cpName);
            
            int result = ps.executeUpdate();
            __logger.debug("Removed custom quiz ids: " + result);
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
        /** delete custom quiz def */
        ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("DELETE_CUSTOM_QUIZ");
            ps = conn.prepareStatement(sql);
            ps.setInt(1, adminId);
            ps.setString(2, cpName);
            int result = ps.executeUpdate();
            __logger.debug("Removed custom quiz: " + result);
            
            wasDeleted = (result == 1);
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
        return wasDeleted;
    }
    
    /** Return list of question HTML for custom quiz
     * 
     * @param conn
     * @param adminId
     * @param cpName
     * @return
     * @throws Exception
     */
    public CmList<QuizQuestion> getCustomQuizQuestions(final Connection conn, int customQuizId) throws Exception {
        CmList<QuizQuestion> list = new CmArrayList<QuizQuestion>();

        PreparedStatement ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CUSTOM_QUIZ_IDS");
            ps = conn.prepareStatement(sql);
            
            
            ps.setInt(1, customQuizId);

            ResultSet rs = ps.executeQuery();
            int problemNumber=1;
            while (rs.next()) {
                String pid = rs.getString("pid");
                
                QuizQuestionParsed question = getQuestionHtml(conn, problemNumber++, pid);
                String quizHtml = question.getStatement();
                int correctAnswer = question.getCorrectAnswer();
                
                QuizQuestion quizQuestion = new QuizQuestion();
                quizQuestion.setPid(pid);
                quizQuestion.setQuizHtml(quizHtml);
                quizQuestion.setCorrectAnswer(correctAnswer);
                
                list.add(quizQuestion);
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        return list;
    }

    
    SolutionPostProcess postProcessor = new SolutionPostProcess();
    
    /** Read and process the problemstatement for the named PID.
     * 
     * This solution is expected to have a problem statement using the 
     * question format.
     * 
     * @param pid
     * @return
     * @throws Exception
     */
    private QuizQuestionParsed getQuestionHtml(final Connection conn, int problemNumber, String pid) throws Exception {
        Solution sol = SolutionManager.getSolution(conn, pid,true);
        String statement = postProcessor.processHTML_SolutionImagesAbsolute(sol.getStatement(), sol.getSolutionImagesURI(), null);
        
        QuizQuestionParsed quizQuestion = new QuizQuestionParsed(getQuizHtml(problemNumber,pid,statement ));
        return quizQuestion;
    }

    

    public CmList<CustomQuizDef> getCustomQuizDefinitions(final int adminId) throws Exception {
        
        List<CustomQuizDef> quizIds = getJdbcTemplate().query(
                CmMultiLinePropertyReader.getInstance().getProperty("GET_CUSTOM_QUIZ_DEFS"),
                new Object[]{adminId},
                new RowMapper<CustomQuizDef>() {
                    @Override
                    public CustomQuizDef mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new CustomQuizDef(
                                rs.getInt("quiz_id"), 
                                rs.getString("quiz_name"),
                                adminId);
                    }
                });
        
        CmList<CustomQuizDef> list = new CmArrayList<CustomQuizDef>();
        list.addAll(quizIds);
        return list;
    }

    public CustomQuizDef getCustomQuiz(final int quizId) throws Exception {

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CUSTOM_QUIZ_BYID");

        CustomQuizDef customQuiz = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{quizId},
                new RowMapper<CustomQuizDef>() {
                    public CustomQuizDef mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                                return new CustomQuizDef(
                                        rs.getInt("quiz_id"), 
                                        rs.getString("quiz_name"),
                                        rs.getInt("admin_id"));
                        }
                        catch(Exception e) {
                            __logger.error(String.format("Error getting Custom Quiz for Id: %d", quizId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return customQuiz;
    	
    }

    public CustomQuizDef getCustomQuizByTestId(final int testId) throws Exception {

    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_CUSTOM_QUIZ_BY_TESTID");

        CustomQuizDef customQuiz = this.getJdbcTemplate().queryForObject(
                sql,
                new Object[]{testId},
                new RowMapper<CustomQuizDef>() {
                    public CustomQuizDef mapRow(ResultSet rs, int rowNum) throws SQLException {
                        try {
                                return new CustomQuizDef(
                                        rs.getInt("quiz_id"), 
                                        rs.getString("quiz_name"),
                                        rs.getInt("admin_id"));
                        }
                        catch(Exception e) {
                            __logger.error(String.format("Error getting Custom Quiz for runId: %d", testId), e);
                            throw new SQLException(e.getMessage());
                        }
                    }
                });
        return customQuiz;
    	
    }

    /** Return list of question HTML for custom quiz
     *
     * TODO: return List instead of CmList
     * 
     * @param conn
     * @param adminId
     * @param cpName
     * @return
     * @throws Exception
     */
    public CmList<CustomQuizId> getCustomQuizIds(int customQuizId) throws Exception {
        
        List<CustomQuizId> quizIds = getJdbcTemplate().query(
                CmMultiLinePropertyReader.getInstance().getProperty("GET_CUSTOM_QUIZ_IDS"),
                new Object[]{customQuizId},
                new RowMapper<CustomQuizId>() {
                    @Override
                    public CustomQuizId mapRow(ResultSet rs, int rowNum) throws SQLException {

                        String pid = rs.getString("pid");
                        return new CustomQuizId(pid, rowNum);
                    }
                });
        
        CmList<CustomQuizId> list = new CmArrayList<CustomQuizId>();
        list.addAll(quizIds);
        return list;
    }
    
    
    
    private String getQuizHtml(int num, String pid, String statement) throws Exception {
        try {
            String questionTemplate = readQuestionTemplate();
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("problemNumber", new Integer(num));
            map.put("questionStatement",statement);
            map.put("pid",pid);

            String quizHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(questionTemplate, map);
            return quizHtml;
        }
        finally {
            //System.out.println("getQuizHtml");
        }
    }
    
    private String readQuestionTemplate() throws Exception {
        String questionTemplate = new SbFile(CatchupMathProperties.getInstance().getCatchupRuntime() 
                + "/template/question_template.vm").getFileContents().toString("\n");
        return questionTemplate;
    }

    /** Return grade level for named subject.  Only 
     * lessons at named level or below will be shown.
     *  
     * If lesson is not found, then it matches all.
     * 
     * @param subject
     * @return
     */
    private int getGradeLevelFor(String subject) {

        subject = subject.toLowerCase();
        if (subject.equals("ess")) {
            // Essentials
            return 1;
        } else if (subject.equals("pre-alg")) {
            // Pre-Algebra
            return 9;
        } else if (subject.equals("alg1")) {
            // Algebra 1
            return 10;
        } else if (subject.equals("geom")) {
            // Geometry
            return 11;
        } else if (subject.equals("alg2")) {
            // Algebra 2
            return 12;
        } else {
            return 0;
        }
    }    
}
