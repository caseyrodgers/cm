package hotmath.assessment;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestDefDescription;

import java.util.List;

public class AssessmentPrescription_StressTest extends CmDbTestCase {
    

    public AssessmentPrescription_StressTest(String name) {
        super(name);
    }
    
    
    public void testStressAllTests() throws Exception {
        List<String> testNames = new HaTestDefDao().getTestNames();
        for(String tn: testNames) {

            /** The all have at least 4 sections
             * 
             */
            for(int x=1;x<5;x++) {
                HaTestDefDescription desc = HaTestDefDescription.getHaTestDefDescription(tn, x);
                System.out.println("Quiz: " + tn + ", " + x);
                int cnt = desc.getLessonItems().size();
                for(int i=0;i<cnt;i++) {
                    System.out.println(desc.getLessonItems().get(i).getInmhItem().getTitle());
                }
                System.out.println("----------");
                if(cnt == 0) {
                    System.out.println("WARNING: no lessions!");
                }
            }
        }
    }

}
