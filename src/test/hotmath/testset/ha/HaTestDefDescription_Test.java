package hotmath.testset.ha;


import hotmath.assessment.InmhItemData;
import junit.framework.TestCase;

public class HaTestDefDescription_Test extends TestCase {
    
    //String TEST_NAME = "Algebra 1 Proficiency";
    String TEST_NAME = "Pre-Algebra Proficiency";

    public HaTestDefDescription_Test(String name) {
        super(name);
    }
    
    
    public void testGetLessonNames() throws Exception {
        HaTestDefDescription desc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME, 1);
        
        for(InmhItemData i: desc.getLessonItems()) {
            System.out.println(i.getInmhItem().getTitle());
        }
        assertTrue(desc.getLessonItems().size() > 0);
    }
    
    
    public void testGetLessonNames2() throws Exception {
        HaTestDefDescription desc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME, 3);
        
        for(InmhItemData i: desc.getLessonItems()) {
            System.out.println(i.getInmhItem().getTitle());
        }
        assertTrue(desc.getLessonItems().size() > 0);
    }

}
