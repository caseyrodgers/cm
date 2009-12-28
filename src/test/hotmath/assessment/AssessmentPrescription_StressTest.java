package hotmath.assessment;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.testset.ha.HaTestDef;
import hotmath.testset.ha.HaTestDefDao;
import hotmath.testset.ha.HaTestDefDescription;

import java.util.List;

public class AssessmentPrescription_StressTest extends CmDbTestCase {
    

    public AssessmentPrescription_StressTest(String name) {
        super(name);
    }
    
    
    public void testStressAllTests() throws Exception {
        List<String> testNames = new HaTestDefDao().getTestNames(conn);
        for(String tn: testNames) {

            HaTestDef testDef = new HaTestDefDao().getTestDef(conn, tn);
            
            /** The all have at least 4 sections
             * 
             */
            int segments = testDef.getTotalSegmentCount();
            for(int x=1;x<segments;x++) {
                HaTestDefDescription desc =  null; // HaTestDefDescription.getHaTestDefDescription(testDef.getName(), x);
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
