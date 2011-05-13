package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction.ActionType;

public class GroupManagerCommand_Test extends CmDbTestCase {
    

    public GroupManagerCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        GroupInfoModel group = new GroupInfoModel();
        group.setGroupName("demo_" + System.currentTimeMillis());
        group.setDescription("Demo Group");
        
        if(_user == null)
            setupDemoAccount();
        
        _groupModel = CmAdminDao.getInstance().addGroup(conn, _user.getAid(), group);
    }
    
    @Override
    protected void tearDown() throws Exception {
        CmAdminDao.getInstance().deleteGroup(conn,_user.getAid(), _groupModel.getId());
        super.tearDown();
    }
    
    
    public void testUpdateGroupPropertiesGroup() throws Exception {
        GroupManagerAction action = new GroupManagerAction(ActionType.GROUP_PROPERTY_SET,_user.getAid());
        action.setGroupId(_groupModel.getId());
        action.setDisallowTutoring(true);
        action.setShowWorkRequired(true);
        action.setLimitGames(false);
        
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
    }
    
    public void testUpdateGroupPropertiesAll() throws Exception {
        GroupManagerAction action = new GroupManagerAction(ActionType.GROUP_PROPERTY_SET,_user.getAid());
        action.setGroupId(-1);
        action.setDisallowTutoring(true);
        action.setShowWorkRequired(true);
        action.setPassPercent(80);
        action.setLimitGames(false);
        action.setStopAtProgramEnd(true);
        
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
    }

    
    public void testCreate() throws Exception {
        GroupManagerAction action = new GroupManagerAction(ActionType.DELETE,_user.getAid());
        action.setGroupId(_groupModel.getId());
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
    }
    
    public void testUnregister() throws Exception {
        GroupManagerAction action = new GroupManagerAction(ActionType.UNREGISTER_STUDENTS,_user.getAid());
        action.setGroupId(_groupModel.getId());
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
    }

    
}
