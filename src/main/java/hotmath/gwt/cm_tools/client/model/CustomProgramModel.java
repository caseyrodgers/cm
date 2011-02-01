package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class CustomProgramModel extends BaseModel implements Response {
    public CustomProgramModel(){
        /** empty */
    }
    
    public CustomProgramModel(String programName, Integer programId, Integer assignedCount, Integer inUseCount, Boolean isTemplate) {
        setProgramName(programName);
        setProgramId(programId);
        setAssignedCount(assignedCount);
        setInUseCount(inUseCount);
        setIsTemplate(isTemplate);

        if(isTemplate)
            setStyleName("program-read-only");
        
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
    
    public void setAssignedCount(int ac) {
        set("assignedCount", ac);
    }
    
    public Integer getAssignedCount() {
        return get("assignedCount");
    }
    
    
    public void setInUseCount(int ic) {
        set("inUseCount", ic);
    }
    
    public Integer getInUseCount() {
        return get("inUseCount");
    }
    
    public void setIsTemplate(Boolean isTemplate) {
        set("isTemplate",isTemplate);
    }
    
    public Boolean getIsTemplate() {
        return get("isTemplate");
    }
    
    public String getStyleName() {
        return get("styleName");
    }
    
    public void setStyleName(String style) {
        set("styleName", style);
    }
}
