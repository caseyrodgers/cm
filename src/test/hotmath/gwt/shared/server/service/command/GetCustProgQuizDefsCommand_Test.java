package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_admin.client.ui.GetCustProgQuizDefsAction;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.CustomQuizDef;

public class GetCustProgQuizDefsCommand_Test extends CmDbTestCase{
    
    public GetCustProgQuizDefsCommand_Test(String name) {
        super(name);
    }
    
    public void testCreate() throws Exception {
        GetCustProgQuizDefsAction action = new GetCustProgQuizDefsAction(2);
        CmList<CustomQuizDef> defs = new GetCustProgQuizDefsCommand().execute(conn,action);
        assertTrue(defs.size() > 0);
    }

}
