package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;

public class CreateFinalExamAction implements Action<Assignment> {
    
    private String programType;
    private int numberProblems;
    private int adminId;
    private GroupDto group;

    public CreateFinalExamAction() {}
    
    public CreateFinalExamAction(int adminId, GroupDto group, String programType, int numberProblems) {
        this.adminId = adminId;
        this.group = group;
        this.programType = programType;
        this.numberProblems = numberProblems;
    }

    public String getProgramType() {
        return programType;
    }

    public void setProgramType(String programType) {
        this.programType = programType;
    }

    public int getNumberProblems() {
        return numberProblems;
    }

    public void setNumberProblems(int numberProblems) {
        this.numberProblems = numberProblems;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public GroupDto getGroup() {
        return group;
    }

    public void setGroup(GroupDto group) {
        this.group = group;
    }


    @Override
    public String toString() {
        return "CreateFinalExamAction [programType=" + programType + ", numberProblems=" + numberProblems + ", adminId=" + adminId + ", group=" + group + "]";
    }

}
