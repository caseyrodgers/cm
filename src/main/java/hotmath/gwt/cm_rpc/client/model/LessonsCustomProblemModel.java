package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/** Create map of lessons and the custom problems
 *  that reference them.
 * 
 * @author casey
 *
 */
public class LessonsCustomProblemModel implements Response {
    
    Map<LessonModel, List<String>> _map;
    public LessonsCustomProblemModel(){}
    
    public LessonsCustomProblemModel(Map<LessonModel, List<String>> map) {
        this._map = map;
    }

    public List<LessonModel> getAllLessons() {
        Iterator<LessonModel> it = _map.keySet().iterator();
        List<LessonModel> models = new ArrayList<LessonModel>();
        while(it.hasNext()) {
            LessonModel clm = it.next();
            models.add(clm);
        }
        
        return models;
    }
    
    
}
