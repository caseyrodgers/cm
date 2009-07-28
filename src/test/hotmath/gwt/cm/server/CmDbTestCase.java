package hotmath.gwt.cm.server;

import hotmath.gwt.shared.server.service.CmTestUtils;

public class CmDbTestCase extends DbTestCase {
    
    public CmDbTestCase(String name) {
        super(name);
    }
    
    
    /** Create a new test account to use for test/samples/demo
     * 
     * @return The userid of the test account
     * 
     * @throws Exception
     */
    public int setupDemoAccount() throws Exception {
        return CmTestUtils.setupDemoAccount();
    }
}
