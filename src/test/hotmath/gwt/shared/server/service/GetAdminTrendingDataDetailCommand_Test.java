package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher;
import hotmath.gwt.shared.client.CmProgram;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.server.service.command.GetAdminTrendingDataDetailCommand;

public class GetAdminTrendingDataDetailCommand_Test extends CmDbTestCase{
    
    public GetAdminTrendingDataDetailCommand_Test(String name) {
        super(name);
    }

    GetStudentGridPageAction pageAction=null;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        pageAction = new GetStudentGridPageAction(2, null);
        
    }
    public void testCreate() throws Exception {
        CmList<StudentModelI> students = new GetAdminTrendingDataDetailCommand().execute(conn,new GetAdminTrendingDataDetailAction(2,pageAction,CmProgram.GEOM_PROF.getDefId(),0));
        assertTrue(students.size() > 0);
        
        students = new GetAdminTrendingDataDetailCommand().execute(conn,new GetAdminTrendingDataDetailAction(pageAction,"Vectors"));
        assertTrue(students.size() > 0);
    }
    
    public void testCreateActionDispatcher() throws Exception {
        CmList<StudentModelI> students = ActionDispatcher.getInstance().execute(new GetAdminTrendingDataDetailAction(2,pageAction,CmProgram.GEOM_PROF.getDefId(),0));
        assertTrue(students.size() > 0);
    }
}
