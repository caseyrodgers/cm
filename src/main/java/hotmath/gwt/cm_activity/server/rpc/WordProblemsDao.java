package hotmath.gwt.cm_activity.server.rpc;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.gwt.cm_activity.client.model.WordProblem;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import sb.util.SbFile;

/** test of new style of DAO that
 *  passes connection into constructor
 *  and all methods access instance variable
 *  _connection;
 *  
 * @author casey
 *
 */
public class WordProblemsDao {
    Connection _connection;
    public WordProblemsDao(final Connection conn) {
        _connection = conn;
    }
    
    public List<WordProblem> getWordProblems() throws Exception {
        
        List<WordProblem> wordProblems = new ArrayList<WordProblem>();
        /** read from XML file 
         * 
         */
        String fileBase = CmWebResourceManager.getInstance().getFileBase();
        File activitiesFile = new File(fileBase,"../activities/word_problems/word_problem_def_mathml.xml");
        
        WordProblemsParser wp = WordProblemsParser.parseXml(new SbFile(activitiesFile).getFileContents().toString("\n"));
        return wp.getProblems();
    }
}
