package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.shared.client.rpc.action.SaveCmLoggerTextAction;

public class SaveCmLoggerTextCommand_Test extends CmDbTestCase {
    
    public SaveCmLoggerTextCommand_Test(String name) {
        super(name);
    }

    public void testSaveLogMessges() throws Exception {
        SaveCmLoggerTextAction action = new SaveCmLoggerTextAction(1,"THIS IS THE LOG MESSAGES");
        RpcData rdata = new SaveCmLoggerTextCommand().execute(conn,action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
    }
}
