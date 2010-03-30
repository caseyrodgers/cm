package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/** represents a uniquely identified student user program
 * 
 * @author casey
 *
 */
public class StudentProgramModel implements IsSerializable{

    Integer programId;
    String subjectId;
    String programType;
    Integer customProgramId;
    String customProgramName;

    public String getCustomProgramName() {
        return customProgramName;
    }

    public void setCustomProgramName(String customProgramName) {
        this.customProgramName = customProgramName;
    }

    public StudentProgramModel() {}

    public Integer getCustomProgramId() {
        return customProgramId;
    }

    public void setCustomProgramId(Integer customProgramId) {
        this.customProgramId = customProgramId;
    }

    public StudentProgramModel(Integer programId, String subjectId, String programType) {
        this.programId = programId;
        this.programType = programType;        
        this.subjectId = subjectId;
    }

    
    public boolean isCustomProgram() {
        return this.customProgramId != null && this.customProgramId > 0?true:false;
    }
    
    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }
    
    @Override
    public String toString() {
        return "StudentProgramModel [customProgramId=" + customProgramId + ", customProgramName=" + customProgramName
                + ", programId=" + programId + ", programType=" + programType + ", subjectId=" + subjectId + "]";
    }    
}
