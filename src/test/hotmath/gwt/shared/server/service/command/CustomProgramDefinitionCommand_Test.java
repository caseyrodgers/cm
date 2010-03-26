package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction.ActionType;

public class CustomProgramDefinitionCommand_Test extends CmDbTestCase {
    
    public CustomProgramDefinitionCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        if(_user == null)
            setupDemoAccount();
    }
    public void testGet() throws Exception {
        CustomProgramDefinitionAction action = new CustomProgramDefinitionAction(ActionType.GET,_user.getAid());
        CmList<CustomProgramModel> models = new CustomProgramDefinitionCommand().execute(conn,action);
        assertNotNull(models);
    }
    
    public void testDelete() throws Exception {
        CustomProgramDefinitionAction action = new CustomProgramDefinitionAction(ActionType.DELETE,_user.getAid());
        CmList<CustomProgramModel> models = new CustomProgramDefinitionCommand().execute(conn,action);
        assertTrue(models == null);
    }

}
