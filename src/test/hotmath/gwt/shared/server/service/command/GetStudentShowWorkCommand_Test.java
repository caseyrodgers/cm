package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.shared.client.rpc.action.GetStudentShowWorkAction;

public class GetStudentShowWorkCommand_Test extends CmDbTestCase {
    
    public GetStudentShowWorkCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        
        if(_testRun == null)
            setupDemoAccountTestRun();
    }
    
    
    public void testGetWhiteboards() throws Exception {
        GetStudentShowWorkAction action = new GetStudentShowWorkAction(_user.getUid(), _testRun.getRunId());
        CmList<StudentShowWorkModel> models = new GetStudentShowWorkCommand().execute(conn, action);
        assertNotNull(models);
    }
    
    
    public void testGetWhiteboardsInActionDispatcher() throws Exception {
        GetStudentShowWorkAction action = new GetStudentShowWorkAction(_user.getUid(), _testRun.getRunId());
        CmList<StudentShowWorkModel> models = ActionDispatcher.getInstance().execute(action);
        assertNotNull(models);
    }

}
