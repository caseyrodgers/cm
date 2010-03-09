package hotmath.cm.status;

import hotmath.assessment.AssessmentPrescription;
import hotmath.assessment.AssessmentPrescriptionManager;
import hotmath.gwt.cm.server.CmDbTestCase;

public class StatusPie_Test extends CmDbTestCase {
    public StatusPie_Test(String name) {
        super(name);
    }

    String base = "/temp/cm_web_resource/status";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(_testRun == null)
            setupDemoAccountTestRun();
    }

    
    public void testCreateTestRun() throws Exception {
        StatusPie pie = new StatusPie(base);
        AssessmentPrescription prescription = AssessmentPrescriptionManager.getInstance().getPrescription(conn, _testRun.getRunId());
        pie.createPrescriptionStatusChart(prescription);
        assertTrue(pie.getImageFile() != null);
    }
    
    public void testCreateTest() throws Exception {
        StatusPie pie = new StatusPie(base);
        pie.createProgramStatusChart(_test);
        assertTrue(pie.getImageFile() != null);
    }
}
