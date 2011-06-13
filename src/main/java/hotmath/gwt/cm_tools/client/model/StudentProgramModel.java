package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.CmProgramType;

import com.google.gwt.user.client.rpc.IsSerializable;

/** represents a uniquely identified student user program
 *
 * @author casey
 *
 */
public class StudentProgramModel implements IsSerializable{

    Integer programId;
    String  subjectId;
    CmProgramType  programType;
    Integer sectionCount;

    CustomProgramComposite custom = new CustomProgramComposite();

    String programDescription;

    public StudentProgramModel() {}

    /**
     *
     * @param programId The PK for the CM_USER_PROGRAM
     * @param subjectId The subject id
     * @param programType The type of program (Prof, Chap ,etc..)
     * @param sectionCount The number of sections / segments
     */
    public StudentProgramModel(Integer programId, String subjectId, CmProgramType programType, Integer sectionCount) {
        this.programId = programId;
        this.programType = programType;
        this.subjectId = subjectId;
        this.sectionCount = sectionCount;
    }

    public CustomProgramComposite getCustom() {
        return custom;
    }

    public void setCustom(CustomProgramComposite customProgram) {
        this.custom = customProgram;
    }

    public String getProgramDescription() {
        return programDescription;
    }

    public void setProgramDescription(String programDescription) {
        this.programDescription = programDescription;
    }

    public boolean isCustom() {
        return custom.isCustom();
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

    public CmProgramType getProgramType() {
        return programType;
    }

    public void setProgramType(CmProgramType programType) {
        this.programType = programType;
    }

    public void setProgramType(String programType) {
        this.programType = CmProgramType.lookup(programType);
    }

    public Integer getSectionCount() {
        return sectionCount;
    }

    public void setSectionCount(Integer sectionCount) {
        this.sectionCount = sectionCount;
    }

    @Override
    public String toString() {
        return "StudentProgramModel [programId=" + programId + ", subjectID=" + subjectId
                + ", programType=" + programType + ", " + custom.toString() + "]";
    };

}
