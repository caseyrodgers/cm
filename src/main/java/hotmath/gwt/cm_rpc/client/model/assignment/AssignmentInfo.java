package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class AssignmentInfo implements Response{
    
    int assigned;
    int errors;
    String message;

    public AssignmentInfo(){}
    
    public AssignmentInfo(int assigned, int errors, String message) {
        this.assigned = assigned;
        this.errors = errors;
        this.message = message;
    }

    public Object getAssigned() {
        return assigned;
    }

    public void setAssigned(int assigned) {
        this.assigned = assigned;
    }

    public int getErrors() {
        return errors;
    }

    public void setErrors(int errors) {
        this.errors = errors;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "AssignmentInfo [assigned=" + assigned + ", errors=" + errors + ", message=" + message + "]";
    }

}
