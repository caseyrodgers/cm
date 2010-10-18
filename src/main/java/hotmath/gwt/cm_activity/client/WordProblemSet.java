package hotmath.gwt.cm_activity.client;

import hotmath.gwt.cm_activity.client.model.WordProblem;
import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.List;

/** Data access object
 * 
 * @author casey
 *
 */
public class WordProblemSet implements Response{
    
    List<WordProblem> problems;

    public WordProblemSet() {}
    
    public List<WordProblem> getProblems() {
        return problems;
    }

    public void setProblems(List<WordProblem> problems) {
        this.problems = problems;
    }
}
