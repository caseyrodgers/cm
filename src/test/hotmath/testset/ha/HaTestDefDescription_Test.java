package hotmath.testset.ha;

import junit.framework.TestCase;

public class HaTestDefDescription_Test extends TestCase {
    
    String TEST_NAME = "Algebra 1 Proficiency";

    public HaTestDefDescription_Test(String name) {
        super(name);
    }
    
    
    public void testGetLessonNames() throws Exception {
        HaTestDefDescription desc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME);
        assertTrue(desc.getLessonItems().size() > 0);
    }
    
    public void testGetLessonNames2() throws Exception {
        
        // should get it from cache
        HaTestDefDescription desc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME);
        assertTrue(desc.getLessonItems().size() > 0);
    }
}
