package hotmath.assessment;

import hotmath.testset.ha.HaTest;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestRun;
import hotmath.testset.ha.HaUser;
import junit.framework.TestCase;

public class AssessmentPrescription_Test extends TestCase {
    
    
    HaUser user;
    int CA_STATE_TEST=28;
    int PRE_ALG_PROF=16;
    HaTestDef testDef;
    public AssessmentPrescription_Test(String name) throws Exception {
        super(name);
        
        user = HaUser.lookUser(700,null);
        testDef = new HaTestDef(CA_STATE_TEST);
    }

    HaTestRun testRun;
    
    
    @Override
    protected void setUp() throws Exception {
        
        String guids[] = {"cahseehm_1_1_PracticeTest_1_1",
                          "cahseehm_1_1_PracticeTest_10_1",
                          "cahseehm_1_1_PracticeTest_2_1",
                          "cahseehm_1_1_PracticeTest_3_1",
                          "cahseehm_1_1_PracticeTest_4_1",
                          "cahseehm_1_1_PracticeTest_5_1",
                          "cahseehm_1_1_PracticeTest_6_1",
                          "cahseehm_1_1_PracticeTest_7_1",
                          "cahseehm_1_1_PracticeTest_8_1",
                          "cahseehm_1_1_PracticeTest_9_1"};
        HaTest test = HaTest.createTest(user.getUid(), testDef, 1);
        testRun = test.createTestRun(guids, 0, 1, 9, 1);
    }
    
    public void testCreate() throws Exception {
        AssessmentPrescription assTest = new AssessmentPrescription(testRun);
        
        assertTrue(assTest.getSessions().size() > 1);
    }
}