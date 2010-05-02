package hotmath.cm.util;

import sb.util.SbUtilities;

/** attempt to stress test the request infrastructure
 * 
 * @author casey
 *
 */
public class CmRequestStressTest extends Thread {
    
    int countNumber;
    int loops;
    
    public CmRequestStressTest(int num, int loops) {
        this.countNumber = num;
        this.loops = loops;
    }
    
    public void startTest() {
        System.out.println("Starting test: " + this.countNumber);
        start();
    }
    
    @Override
    public void run() {
        
    }
    
    

    static public void main(String as[]) {
        int cnt = SbUtilities.getInt(as[0]);
        
        for(int i=0;i<cnt;i++) {
            //new CmRequestStressTest(i).startTest();
        }
    }
}
