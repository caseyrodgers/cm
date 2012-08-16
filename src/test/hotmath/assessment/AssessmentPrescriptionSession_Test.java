package hotmath.assessment;

import hotmath.gwt.cm.server.CmDbTestCase;


public class AssessmentPrescriptionSession_Test extends CmDbTestCase {
    public AssessmentPrescriptionSession_Test(String name) {
        super(name);
    }
    
    
    public void testCreateAssessmentPrescription() throws Exception {
        AssessmentPrescription ap = new AssessmentPrescription(conn);
        AssessmentPrescriptionSession ac = new AssessmentPrescriptionSession(ap);
        assertTrue(ac != null);
    }
    
    
}
