package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.BaseModel;


    public class SolutionSearchModel extends BaseModel implements Response {
        
    
        public SolutionSearchModel(){
            /** empty */
        }
        
        public SolutionSearchModel(String pid, boolean isActive) {
            setPid(pid);
            setIsActive(isActive);
        }
        
        public void setPid(String pid) {
            set("pid", pid);
        }
        
        public String getPid() {
            return get("pid");
        }
        
        public void setIsActive(boolean isActive) {
            set("active", isActive);
        }
        
        public boolean isActive() {
            return get("active");
        }
       
}
