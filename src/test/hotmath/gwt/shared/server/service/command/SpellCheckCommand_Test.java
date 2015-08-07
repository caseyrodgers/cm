package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm_rpc.client.model.SpellCheckResults;
import hotmath.gwt.cm_rpc.client.rpc.SpellCheckAction;
import junit.framework.TestCase;

public class SpellCheckCommand_Test extends TestCase {
    
    public SpellCheckCommand_Test(String name) {
        super(name);
    }
    
    public void testIt() throws Exception {
       SpellCheckResults result = new SpellCheckCommand().execute(null,  new SpellCheckAction("incorrecttly spelled"));
       assertTrue(result.getCmList().size() > 0);
    }

}
