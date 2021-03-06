package hotmath.gwt.cm_rpc_assignments.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class AssignmentWhiteboardData implements Response {
    StudentProblemDto studentProblem;
    CmList<WhiteboardCommand> commands;
    private String problemStatement;
    
    public AssignmentWhiteboardData(){}
    
    public AssignmentWhiteboardData(StudentProblemDto studentProblem, CmList<WhiteboardCommand> commands, String problemStatement) {
        this.studentProblem = studentProblem;
        this.commands = commands;
        this.problemStatement = problemStatement;
    }

    public StudentProblemDto getStudentProblem() {
        return studentProblem;
    }

    public void setStudentProblem(StudentProblemDto studentProblem) {
        this.studentProblem = studentProblem;
    }

    public CmList<WhiteboardCommand> getCommands() {
        return commands;
    }

    public void setCommands(CmList<WhiteboardCommand> commands) {
        this.commands = commands;
    }

    public String getProblemStatement() {
        return this.problemStatement;
    }
}
