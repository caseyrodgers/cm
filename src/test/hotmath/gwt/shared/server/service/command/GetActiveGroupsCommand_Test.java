package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;

public class GetActiveGroupsCommand_Test extends CmDbTestCase {
    
    public GetActiveGroupsCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        if(_user == null)
            setupDemoAccount();        
    }
    
    public void testRead() throws Exception {
        GetActiveGroupsAction a = new GetActiveGroupsAction(_user.getAid());
        CmList<GroupInfoModel> groups = new GetActiveGroupsCommand().execute(conn, a);
        assertTrue(groups.size() > 0);
    }
}
