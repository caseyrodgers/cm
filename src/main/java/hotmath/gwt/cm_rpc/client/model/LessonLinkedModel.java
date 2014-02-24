package hotmath.gwt.cm_rpc.client.model;

import java.util.ArrayList;
import java.util.List;



/** Represents a lesson and the linked problems
 *  
 * @author casey
 *
 */
public class LessonLinkedModel extends LessonModel {
    
    List<String> problems = new ArrayList<String>();
    public LessonLinkedModel() {}

    public LessonLinkedModel(String lessonName, String lessonFile) {
        super(lessonName, lessonFile);
    }

    public List<String> getProblems() {
        return problems;
    }
    
    public int getProblemCount() {
        return problems.size();
    }

}
