package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveQaItemProblemAction;

public class SaveQaItemProblemCommand_Test extends CmDbTestCase {
    
    public SaveQaItemProblemCommand_Test(String name) {
        super(name);
    }
    
    
    public void testSaveQaItemProblem() throws Exception {
        SaveQaItemProblemAction action = new SaveQaItemProblemAction("casey", "Sales map", "Test problem");
        RpcData data = new SaveQaItemProblemCommand().execute(conn,action);
        assertTrue(data.getDataAsString("status").equals("OK"));
    }

}
