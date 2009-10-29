package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction.ActionType;
import hotmath.gwt.shared.client.util.RpcData;

public class GroupManagerCommand_Test extends CmDbTestCase {
    

    public GroupManagerCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        GroupModel group = new GroupModel();
        group.setName("demo_" + System.currentTimeMillis());
        group.setDescription("Demo Group");
        
        if(_user == null)
            setupDemoAccount();
        
        _groupModel = new CmAdminDao().addGroup(conn, _user.getAid(), group);
    }
    
    @Override
    protected void tearDown() throws Exception {
        new CmAdminDao().deleteGroup(conn,_user.getAid(), Integer.parseInt(_groupModel.getId()));
        super.tearDown();
    }
    
    
    public void testUpdateGroupProperties() throws Exception {
        GroupManagerAction action = new GroupManagerAction(ActionType.GROUP_PROPERTY_SET,_user.getAid());
        action.setGroupId(Integer.parseInt(_groupModel.getId()));
        action.setDisallowTutoring(true);
        action.setShowWorkRequired(true);
        
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
    }
    
    public void testCreate() throws Exception {
        GroupManagerAction action = new GroupManagerAction(ActionType.DELETE,_user.getAid());
        action.setGroupId(Integer.parseInt(_groupModel.getId()));
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
    }
    
    public void testUnregister() throws Exception {
        GroupManagerAction action = new GroupManagerAction(ActionType.UNREGISTER_STUDENTS,_user.getAid());
        action.setGroupId(Integer.parseInt(_groupModel.getId()));
        RpcData rdata = ActionDispatcher.getInstance().execute(action);
        assertTrue(rdata.getDataAsString("status").equals("OK"));
    }

    
}
