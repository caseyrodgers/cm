package hotmath.gwt.shared.server.service.command;

import java.util.List;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelsAction;

public class GetStudentModelsCommand_Test extends CmDbTestCase {

	CmList<Integer> _uids = new CmArrayList<Integer>();

    public GetStudentModelsCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        //if(_user == null)
        //    setupDemoAccount();
    	_uids.add(23469);
    	_uids.add(23470);
    	_uids.add(23471);
    }
    public void testCreate() throws Exception {
        GetStudentModelsAction action = new GetStudentModelsAction(_uids);
        List<StudentModelI> list = new GetStudentModelsCommand().execute(conn, action);
        assertTrue(list.size() == _uids.size());
    }
    
    public void testDispatch() throws Exception {
        GetStudentModelsAction action = new GetStudentModelsAction(_uids);
        List<StudentModelI> list = ActionDispatcher.getInstance().execute(action);
        assertTrue(list.size() == _uids.size());
    }
}
