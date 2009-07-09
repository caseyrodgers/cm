package hotmath.gwt.cm.util;


import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import junit.framework.TestCase;

public class CmCacheManager_Test extends TestCase {
    
    public CmCacheManager_Test(String name) {
        super(name);
    }

    public void testCreate() throws Exception {
        CmCacheManager manager = CmCacheManager.getInstance();
        assertNotNull(manager);
    }
    
    
    public void testReadNotExist() throws Exception {
        Object o = CmCacheManager.getInstance().retrieveFromCache(CacheName.TEST_DEF, "NOT_EXIST");
        assertNull(o);
    }
}
