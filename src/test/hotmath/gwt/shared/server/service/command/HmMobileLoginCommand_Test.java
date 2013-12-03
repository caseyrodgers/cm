package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;
import hotmath.gwt.hm_mobile.client.rpc.HmMobileLoginAction;
import junit.framework.TestCase;

public class HmMobileLoginCommand_Test extends TestCase {
    
    public HmMobileLoginCommand_Test(String name) {
        super(name);
    }
    
    public void testIt() throws Exception{
        HmMobileLoginInfo loginInfo = new HmMobileLoginCommand().execute(null, new HmMobileLoginAction("vtest",null));
        assertTrue(!loginInfo.isExpired());
    }

}
