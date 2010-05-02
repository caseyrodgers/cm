package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.rpc.action.GetGroupAggregateInfoAction;

public class GetGroupAggregateInfoCommand_Test extends CmDbTestCase {
    

    public GetGroupAggregateInfoCommand_Test(String name) {
        super(name);
    }
    
    static final int TEST_AID=2;  // known to have groups
   
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        
        //if(_user == null)
        //    setupDemoAccount();
    }
    
    public void testCreate() throws Exception {
        GetGroupAggregateInfoAction action = new GetGroupAggregateInfoAction(TEST_AID);
        CmList<GroupInfoModel> clm = ActionDispatcher.getInstance().execute(action);
        
        assertTrue(clm.size() > 0);
    }
}
