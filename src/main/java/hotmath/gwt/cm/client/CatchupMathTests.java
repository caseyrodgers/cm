package hotmath.gwt.cm.client;

import hotmath.gwt.cm_tools.client.CatchupMathSharedTests;


/** Run test identified by test URL param 
 * 
 * @return
 */
public class CatchupMathTests extends CatchupMathSharedTests {
    public static boolean runTest() {
        
        if(!CatchupMathSharedTests.runTest()) {
            /** local CM only tests
             * 
             */
            return false;
        }
        else {
            return true;
        }
        
    }

}

