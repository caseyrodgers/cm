package hotmath.gwt.shared.server.service.command;

import hotmath.gwt.cm.server.CmDbTestCase;
import hotmath.gwt.cm_rpc.client.model.QaEntryModel;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetQaItemsAction;

public class GetQaItemsCommand_Test extends CmDbTestCase {
    
    public GetQaItemsCommand_Test(String name) {
        super(name);
    }
    
    public void testGetItems() throws Exception {
        GetQaItemsAction action = new GetQaItemsAction("home_pages");
        CmList<QaEntryModel> models = new GetQaItemsCommand().execute(conn,action);
        assert(models.get(0).getItem() != null);
    }
}
