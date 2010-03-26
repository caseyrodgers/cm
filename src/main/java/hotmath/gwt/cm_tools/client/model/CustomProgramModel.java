package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.shared.client.rpc.Response;

public class CustomProgramModel extends BaseModel implements Response {
    
    public CustomProgramModel(){
        /** empty */
    }
    
    public CustomProgramModel(String programName, Integer programId) {
        setProgramName(programName);
        setProgramId(programId);
    }
    
    public void setProgramName(String programName) {
        set("programName", programName);
    }
    
    public String getProgramName() {
        return get("programName");
    }
    
    public void setProgramId(Integer programId) {
        set("programId", programId);
    }
    
    public Integer getProgramId() {
        return get("programId");
    }
}
