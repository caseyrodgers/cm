package hotmath.cm.util;

import junit.framework.TestCase;

public class CatchupMathProperties_Test extends TestCase {
    
    public CatchupMathProperties_Test(String name) {
        super(name);
    }


    
    public void testReadProp() throws Exception {
        String noValue = CatchupMathProperties.getInstance().getProperty("client.version");
        assertNotNull(noValue);
    }
}
