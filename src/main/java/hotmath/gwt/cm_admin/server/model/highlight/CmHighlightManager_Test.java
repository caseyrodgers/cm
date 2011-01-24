package hotmath.gwt.cm_admin.server.model.highlight;

import hotmath.gwt.cm.server.CmDbTestCase;

public class CmHighlightManager_Test extends CmDbTestCase {
    
    public CmHighlightManager_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        CmHighLightManager hm = new CmHighLightManager(conn);
        assertTrue(hm.getStats().size() > 0);
    }

    public void testRun() throws Exception {
        CmHighLightManager hm = new CmHighLightManager(conn);
        boolean ok = hm.generateAllStats(conn);
        assertTrue(ok);
    }
}
