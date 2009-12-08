package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.shared.client.rpc.action.GetStudentActivityAction;

public class GetStudentActivityCommand_Test extends CmDbTestCase {
    
    public GetStudentActivityCommand_Test(String name) {
        super(name);
    }
    
    @Override
    protected void setUp() throws Exception {
        if(_test == null)
            setupDemoAccountTest();
    }
    
    public void testCreate() throws Exception  {
        StudentModel sm = new CmStudentDao().getStudentModel(_user.getUid());
        GetStudentActivityAction action = new GetStudentActivityAction(sm);
        
        new GetStudentActivityCommand().execute(conn,action);
    }

}
