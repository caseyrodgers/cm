package hotmath.cm.util;

import junit.framework.TestCase;

public class CmMessagePropertyReader_Test extends TestCase {
    
    public CmMessagePropertyReader_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        CmMessagePropertyReader mr = CmMessagePropertyReader.getInstance();
        assertTrue(mr != null);
    }

}
