package hotmath.gwt.cm_rpc.client.model.assignment;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class AssignmentStatusDto implements Response {
    
    private String status;
    private String statusLabel;

    public AssignmentStatusDto(){}
    
    public AssignmentStatusDto(String status) {
        this.status = status;
        this.statusLabel = status;
    }

    public String getStatusLabel() {
        return statusLabel;
    }

    public void setStatusLabel(String statusLabel) {
        this.statusLabel = statusLabel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "AssignmentStatusDto [status=" + status + "]";
    }

}
