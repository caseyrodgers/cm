package hotmath.gwt.solution_editor.client;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;
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

        public void setIsActive(Boolean isActive) {
            set("active", isActive);
        }

        public Boolean isActive() {
            return get("active");
        }

}
