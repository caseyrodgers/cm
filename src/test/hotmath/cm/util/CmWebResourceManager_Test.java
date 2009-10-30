package hotmath.cm.util;

import hotmath.gwt.cm.server.CmDbTestCase;

public class CmWebResourceManager_Test extends CmDbTestCase {
    
    public CmWebResourceManager_Test(String name) {
        super(name);
    }
    
    
    public void testCreate() throws Exception {
        
        CmWebResourceManager m = CmWebResourceManager.getInstance();
        assertTrue(m != null);
        assertNotNull(m.getWebBase());
        m.flush();
    }

}
