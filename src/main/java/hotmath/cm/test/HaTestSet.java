package hotmath.cm.test;

import hotmath.SolutionManager;
import hotmath.solution.Solution;
import hotmath.solution.SolutionPostProcess;
import hotmath.util.sql.SqlUtilities;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates HTML for each question identified by PID
 * 
 * @author Casey
 * 
 */
public class HaTestSet implements Serializable {

    List<HaTestSetQuestion> questions = new ArrayList<HaTestSetQuestion>();

    String textCode;
    String _server;

	private List<String> pidList;

    /**
     * Create a HaTestSet that contains the HTML for each question in pidList
     * 
     * @param conn
     * @param pidList
     * @throws Exception
     */
    public HaTestSet(final Connection conn, List<String> pidList) throws Exception {
        Statement stmt = null;
        this.pidList = pidList;
        try {
            SolutionPostProcess postProcessor = new SolutionPostProcess();
            // get all solutions for this textcode and
            // extract the problem statement for each
            for (String pid : pidList) {
                Solution sol = SolutionManager.getSolution(conn, pid, true);
                String statement = postProcessor.processHTML_SolutionImagesAbsolute(sol.getStatement(),
                        sol.getSolutionImagesURI(), null);
                questions.add(new HaTestSetQuestion(sol.getGUID(), statement));
            }

        } finally {
            SqlUtilities.releaseResources(null, stmt, null);
        }
    }
    
    /** Return true or false if choice is the correct one
     *  
     *  if pid not found, return false;
     *  
     *  
     * @param pid
     * @param choice
     * @return
     */
    public boolean isCorrect(String pid, int choice) {
    	 for (HaTestSetQuestion q : questions) {
             if(pid.equals(q.getProblemIndex())) {
            	 return q.getCorrectAnswer() == choice;
             }
         }
    	 
    	 return false;
    }

    /**
     * return array of indexes to the correct answer for each question
     * 
     * @return
     */
    public List<Integer> getAnswers() {
        List<Integer> answers = new ArrayList<Integer>();
        for (HaTestSetQuestion q : questions) {
            answers.add(q.getCorrectAnswer());
        }
        return answers;
    }

    public List<HaTestSetQuestion> getQuestions() {
        return this.questions;
    }
}
