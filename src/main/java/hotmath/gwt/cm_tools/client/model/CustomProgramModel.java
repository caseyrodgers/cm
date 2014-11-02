package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class CustomProgramModel  implements Response {
	
    private String programName;
	private Integer programId;
	private int assignedCount;
	private int inUseCount;
	private Boolean isTemplate;
	private String styleName;
	private Boolean isArchived;

	public CustomProgramModel(){/** empty */  }
    
    public CustomProgramModel(String programName, Integer programId, Integer assignedCount, Integer inUseCount, Boolean isTemplate,Boolean isArchived) {
        setProgramName(programName);
        setProgramId(programId);
        setAssignedCount(assignedCount);
        setInUseCount(inUseCount);
        setIsTemplate(isTemplate);
        setIsArchived(isArchived);

        if(isTemplate)
            setStyleName("program-read-only");
        
        if(isArchived)
        	setStyleName("custom-archived");
        
    }

    public void setProgramName(String programName) {
    	this.programName = programName;
    }
    
    public String getProgramName() {
        return this.programName;
    }
    
    public void setProgramId(Integer programId) {
        this.programId = programId;
    }
    
    public Integer getProgramId() {
        return this.programId;
    }
    
    public void setAssignedCount(int ac) {
        this.assignedCount = ac;
    }
    
    public Integer getAssignedCount() {
        return this.assignedCount;
    }
    
    
    public void setInUseCount(int ic) {
        this.inUseCount = ic;
    }
    
    public Integer getInUseCount() {
        return this.inUseCount;
    }
    
    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }
    
    public Boolean getIsTemplate() {
        return this.isTemplate;
    }
    
    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }
    
    public Boolean getIsArchived() {
        return this.isArchived;
    }
    
    public String getStyleName() {
        return this.styleName;
    }
    
    public void setStyleName(String style) {
    	this.styleName = style;
    }
}
