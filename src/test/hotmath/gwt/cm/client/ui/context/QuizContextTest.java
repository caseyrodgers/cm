package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.server.service.CmTestUtils;
import hotmath.testset.ha.CmProgram;

public class QuizContextTest extends CmDbTestCase{
    public QuizContextTest(String name) {
        super(name);
    }
    
    public void testAutoAdvance1() throws Exception {
        
        int uid = CmTestUtils.setupDemoAccount(CmProgram.AUTO_ENROLL);
        System.out.println(uid);
    }
}
