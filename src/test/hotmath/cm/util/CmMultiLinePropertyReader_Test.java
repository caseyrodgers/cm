package hotmath.cm.util;

import junit.framework.TestCase;

public class CmMultiLinePropertyReader_Test extends TestCase {
    
    public CmMultiLinePropertyReader_Test(String name) {
        super(name);
    }
    
    public void testSingleProperty() throws Exception {
        String p = CmMultiLinePropertyReader.getInstance().getProperty("CmWebResourceManager.baseDirectory").trim();
        assertNotNull(p);
    }

}
