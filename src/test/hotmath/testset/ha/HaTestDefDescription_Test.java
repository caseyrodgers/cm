package hotmath.testset.ha;


import hotmath.assessment.InmhItemData;
import junit.framework.TestCase;

public class HaTestDefDescription_Test extends TestCase {
    
    //String TEST_NAME = "Algebra 1 Proficiency";
    String TEST_NAME_PROF = "Pre-Algebra Proficiency";
    String TEST_NAME_CHAP = "Pre-Algebra Chapters";

    public HaTestDefDescription_Test(String name) {
        super(name);
    }
    
    public void testGetLessonsForChapters() throws Exception {
        
        HaTestConfig config = new HaTestConfig("{segments:2,chapters:['Rational Numbers']}");
        HaTestDefDescription tdDesc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME_CHAP, 1,config);
        for (InmhItemData item : tdDesc.getLessonItems()) {
            String file = item.getInmhItem().getFile();
            assertNotNull(file);
        }        
    }    

    
    public void testGetLessonFiles() throws Exception {
        HaTestDefDescription tdDesc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME_PROF, 1);
        for (InmhItemData item : tdDesc.getLessonItems()) {
            String file = item.getInmhItem().getFile();
            assertNotNull(file);
        }        
    }
    

    public void testGetLessonNames() throws Exception {
        HaTestDefDescription desc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME_PROF, 1);
        
        /**
        for(InmhItemData i: desc.getLessonItems()) {
            System.out.println(i.getInmhItem().getTitle());
        }
        */
        assertTrue(desc.getLessonItems().size() > 0);
    }
    
    
    public void testGetLessonNames2() throws Exception {
        HaTestDefDescription desc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME_PROF, 3);
        assertTrue(desc.getLessonItems().size() > 0);
    }
    
    
    
    /** zero quiz should return empty (but not null)
     * 
     * @throws Exception
     */
    public void testGetLessonNamesZero() throws Exception {
        HaTestDefDescription desc = HaTestDefDescription.getHaTestDefDescription(TEST_NAME_PROF, 0);
        assertTrue(desc.getLessonItems().size() == 0);
    }

}
