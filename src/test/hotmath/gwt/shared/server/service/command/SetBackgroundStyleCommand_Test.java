package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.rpc.action.SetBackgroundStyleAction;

public class SetBackgroundStyleCommand_Test extends CmDbTestCase {
    
    public SetBackgroundStyleCommand_Test(String name) throws Exception {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null)
            setupDemoAccount();
    }
    
    public void testSet() throws Exception {
        SetBackgroundStyleAction action = new SetBackgroundStyleAction(_user.getUid(), "test-style");
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
        
        assertTrue(CmStudentDao.getInstance().getStudentModel(_user.getUid()).getBackgroundStyle().equals("test-style"));
    }
}
