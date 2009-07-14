package hotmath.testset.ha;

import junit.framework.TestCase;

public class HaTestConfig_Test extends TestCase {
    
    public HaTestConfig_Test(String name) {
        super(name);
    }
    
    
    public void testCreate() throws Exception {
        HaTestConfig config = new HaTestConfig(null);
        assertNotNull(config);
    }
    
    public void testCreateError() throws Exception {
        HaTestConfig config = new HaTestConfig("ERROR");
        assertTrue(config.getSegmentCount() != 0);
    }

}
