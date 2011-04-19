package hotmath.cm.test;


import hotmath.SolutionManager;
import hotmath.solution.Solution;
import hotmath.solution.SolutionPostProcess;
import hotmath.util.sql.SqlUtilities;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates HTML for each question identified by PID
 *  
 * @author Casey
 * 
 */
public class HaTestSet implements Serializable{
    
    List<HaTestSetQuestion> questions = new ArrayList<HaTestSetQuestion>();
    
    String textCode;
    String _server;

    /** Create a HaTestSet that contains the HTML for each question in pidList
     * 
     * @param conn
     * @param pidList
     * @throws Exception
     */
    public HaTestSet(final Connection conn, List<String> pidList) throws Exception {
        Statement stmt=null;
        try {
            SolutionPostProcess postProcessor = new SolutionPostProcess();
            // get all solutions for this textcode and
            // extract the problem statement for each
            if(pidList.size() > 0) {
                String pidInList = "";
                for (String p : pidList) {
                    if (pidInList.length() > 0)
                        pidInList += ",";
                    pidInList += "'" + p + "'";
                }
                String sql = " select PROBLEMINDEX, cast(problemnumber as unsigned int) as pn" +
                             " FROM SOLUTIONS " +
                             " where problemindex in (" +
                               pidInList +
                             ")" + " ORDER BY booktitle, pn";
                
                ResultSet rs = conn.createStatement().executeQuery(sql);
                while(rs.next()) {
                    Solution sol = SolutionManager.getSolution(conn, rs.getString("problemindex"),true);
                    String statement = postProcessor.processHTML_SolutionImagesAbsolute(sol.getStatement(), sol.getSolutionImagesURI(), null);
                    questions.add(new HaTestSetQuestion(sol.getGUID(), statement));
                }
            }
        }
        finally {
        SqlUtilities.releaseResources(null,stmt,null);
        }
    }
    
    /** return array of indexes to the correct answer for each question
     * 
     * @return
     */
    public List<Integer> getAnswers() {
        List<Integer> answers = new ArrayList<Integer>();
        for(HaTestSetQuestion q: questions) {
            answers.add(q.getCorrectAnswer());
        }
        return answers;
    }
    
    public List<HaTestSetQuestion> getQuestions() {
        return this.questions;
    }
}
