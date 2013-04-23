package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc.client.model.QaEntryModel;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

public class GetQaItemsAction implements Action<CmList<QaEntryModel>>{
    
    String category;
    
    public GetQaItemsAction(){}
    
    public GetQaItemsAction(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "GetQaItemsAction [category=" + category + "]";
    }
}
