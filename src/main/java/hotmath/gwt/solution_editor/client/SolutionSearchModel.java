package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.BaseModel;


    public class SolutionSearchModel extends BaseModel implements Response {
        
    
        public SolutionSearchModel(){
            /** empty */
        }
        
        public SolutionSearchModel(String pid) {
            setPid(pid);
        }
        
        public void setPid(String pid) {
            set("pid", pid);
        }
        
        public String getPid() {
            return get("pid");
        }
}
