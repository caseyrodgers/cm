package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQaItemAction;

public class SaveQaItemCommand_Test extends CmDbTestCase {
    
    public SaveQaItemCommand_Test(String name) {
        super(name);
    }
    
    
    public void testSaveQaItem() throws Exception {
        SaveQaItemAction action = new SaveQaItemAction("home_pages", "Sales map", true, false);
        RpcData data = new SaveQaItemCommand().execute(conn,action);
        assertTrue(data.getDataAsString("status").equals("OK"));
    }

}
