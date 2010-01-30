package hotmath.gwt.shared.server.service;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.shared.client.model.CmAdminTrendingDataI;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.server.service.command.GetAdminTrendingDataCommand;

public class GetAdminTrendingDataCommand_Test extends CmDbTestCase{
    
    public GetAdminTrendingDataCommand_Test(String name) {
        super(name);
    }

    GetStudentGridPageAction pageAction=null;
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        pageAction = new GetStudentGridPageAction(2, null);
        
    }
    public void testCreate() throws Exception {
        CmAdminTrendingDataI td = new GetAdminTrendingDataCommand().execute(conn,new GetAdminTrendingDataAction(2,pageAction));
        assertTrue(td.getTrendingData().size() > 0);
        assertTrue(td.getProgramData().size() > 0);
    }
    
    public void testCreateActionDispatcher() throws Exception {
        CmAdminTrendingDataI td = ActionDispatcher.getInstance().execute(new GetAdminTrendingDataAction(2,pageAction));
        assertTrue(td.getTrendingData().size() > 0);
        assertTrue(td.getProgramData().size() > 0);
    }
}
