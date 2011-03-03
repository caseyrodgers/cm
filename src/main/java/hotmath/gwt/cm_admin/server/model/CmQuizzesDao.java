package hotmath.gwt.cm_admin.server.model;

import hotmath.SolutionManager;
import hotmath.cm.test.HaTestSet;
import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.solution.Solution;
import hotmath.solution.SolutionPostProcess;
import hotmath.util.VelocityTemplateFromStringManager;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * try to centralize quiz db operations
 * 
 * @author casey
 * 
 */

public class CmQuizzesDao {

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
        CmList<QuizQuestion> list = new CmArrayList<QuizQuestion>();

        PreparedStatement ps = null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_LESSON_QUIZZES");
            ps = conn.prepareStatement(sql);
            ps.setString(1, lesson);
            int gradeLevel = 999; // get all for now getGradeLevelFor(subject);
            ps.setInt(2, gradeLevel);

            int num=1;
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String questionId = rs.getString("program_name") + ": " + (num++);
                String pid = rs.getString("guid");
                String quizHtml = getQuestionHtml(pid);
                
                list.add(new QuizQuestion(questionId,lesson, rs.getString("program_name"), pid, quizHtml));
            }
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        return list;
    }
    
    SolutionPostProcess postProcessor = new SolutionPostProcess();
    
    private String getQuestionHtml(String pid) throws Exception {
        Solution sol = SolutionManager.getSolution(pid,true);
        String statement = postProcessor.processHTML_SolutionImagesAbsolute(sol.getStatement(), sol.getSolutionImagesURI(), null);
        
        return statement;
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
