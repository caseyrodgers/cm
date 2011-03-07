package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.shared.client.model.CustomQuizId;
import hotmath.gwt.shared.client.rpc.action.SaveCustomQuizAction;

import java.util.ArrayList;
import java.util.List;

public class SaveCustomQuizCommand_Test extends CmDbTestCase {
    public SaveCustomQuizCommand_Test(String name) {
        super(name);
    }
    
    
    
    public void testCreate() throws Exception {
        int adminId = 2;
        String cpName = "Custom Quiz: " + System.currentTimeMillis();
        List<CustomQuizId> ids = new ArrayList<CustomQuizId>();
        ids.add(new CustomQuizId("pid: " + System.currentTimeMillis(),0));
        ids.add(new CustomQuizId("pid: " + System.currentTimeMillis(),1));
        SaveCustomQuizAction action = new SaveCustomQuizAction(adminId, cpName, ids);
        
        RpcData data = new SaveCustomQuizCommand().execute(conn, action);
        assertTrue(data != null && data.getDataAsString("status").equals("ok"));
    }
}
