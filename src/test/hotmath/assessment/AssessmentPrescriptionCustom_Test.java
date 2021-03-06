package hotmath.assessment;

import hotmath.cm.server.model.CmUserProgramDao;
import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentSettingsModel;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDao;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.StudentUserProgramModel;

public class AssessmentPrescriptionCustom_Test extends CmDbTestCase {
     
    public AssessmentPrescriptionCustom_Test(String name) {
        super(name);
    }
    
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null) {
            setupDemoAccount();
        }
    }
    
    public void testCreateAndCheck() throws Exception {

        int KNOWN_CUSTOM_PROGRAM_ID = 188;
        
        
        CmProgram program = CmProgram.CUSTOM_PROGRAM;
        StudentProgramModel studentProgram = new StudentProgramModel();
        studentProgram.getCustom().setCustomProgram(KNOWN_CUSTOM_PROGRAM_ID,"Test Custom Program");
        studentProgram.setSubjectId("");
        studentProgram.setProgramType("Custom");
        
        CmStudentDao.getInstance().assignProgramToStudent(conn, _user.getUid(), studentProgram,null, "80%", new StudentSettingsModel(),false, 1);
        
        CmUserProgramDao upDao = CmUserProgramDao.getInstance();
        
        StudentUserProgramModel userProgram = upDao.loadProgramInfoCurrent(_user.getUid());
        
        userProgram.setCustomProgramId(KNOWN_CUSTOM_PROGRAM_ID);
        userProgram.setTestDef(HaTestDefDao.getInstance().getTestDef(CmProgram.CUSTOM_PROGRAM.getDefId()));

        HaTest custTest = HaTestDao.getInstance().createTest(_user.getUid(),userProgram.getTestDef(), 0);
        
        custTest.setProgramInfo(userProgram);
        custTest.setTestDef(custTest.getTestDef());
        HaTestRun testRun = HaTestDao.getInstance().createTestRun(conn, _user.getUid(), custTest.getTestId(), 10,0,0);
        testRun.setHaTest(custTest);
        
        AssessmentPrescriptionCustom custom = new AssessmentPrescriptionCustom(conn, testRun);
        assertTrue(custom.getSessions().size() > 0);
        
        
        
        /** now get 2nd test
         * 
         */
        
        HaTest custTest2 = HaTestDao.getInstance().createTest(_user.getUid(),userProgram.getTestDef(), 1);
        
        custTest2.setProgramInfo(userProgram);
        custTest2.setTestDef(custTest2.getTestDef());
        HaTestRun testRun2 = HaTestDao.getInstance().createTestRun(conn, _user.getUid(), custTest2.getTestId(), 10,0,0);
        testRun.setHaTest(custTest2);
        
        AssessmentPrescriptionCustom custom2 = new AssessmentPrescriptionCustom(conn, testRun2);
        assertTrue(custom2.getSessions().size() > 0);        
    }

}
