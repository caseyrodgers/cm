package hotmath.cm.assignment;

import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;

import java.util.List;

public class Cm2Assignment {
    
    private Assignment assignment;
    private List<PrescriptionSessionDataResource> cm2Resources;

    public Cm2Assignment() {}
    
    public Cm2Assignment(Assignment assignment, List<PrescriptionSessionDataResource> cm2Resources2) {
        this.assignment = assignment;
        this.cm2Resources = cm2Resources2;
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public List<PrescriptionSessionDataResource> getCm2Resources() {
        return cm2Resources;
    }

    public void setCm2Resources(List<PrescriptionSessionDataResource> cm2Resources) {
        this.cm2Resources = cm2Resources;
    }
}
