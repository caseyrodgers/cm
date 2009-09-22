package hotmath.testset.ha;


import hotmath.assessment.InmhItemData;
import hotmath.gwt.cm.server.CmDbTestCase;

public class HaTestDefDescription_Test extends CmDbTestCase {
    
    //String TEST_NAME = "Algebra 1 Proficiency";
    String TEST_NAME_PROF = "Pre-Algebra Proficiency";
    String TEST_NAME_CHAP = "Pre-Algebra Chapters";

    public HaTestDefDescription_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_testRun == null)
            _testRun = setupDemoAccountTestRun();
    }
    public void testGetLessonsForChapters() throws Exception {
        HaTestDefDescription tdDesc = HaTestDefDescription.getHaTestDefDescription(conn, _testRun.getRunId());
        for (InmhItemData item : tdDesc.getLessonItems()) {
            String file = item.getInmhItem().getFile();
            assertNotNull(file);
        }        
    }    

    
    
    /** zero quiz should return empty (but not null)
     * 
     * @throws Exception
     */
    public void testGetLessonNamesZero() throws Exception {
        HaTestDefDescription tdDesc = HaTestDefDescription.getHaTestDefDescription(conn, -1);
        assertTrue(tdDesc.getLessonItems().size() == 0);
    }

}
