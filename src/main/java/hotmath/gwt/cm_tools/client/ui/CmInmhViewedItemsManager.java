package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.util.UserInfo;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Manages the retrieval and update of the currently viewed INMH data
 * 
 * @author casey
 * 
 */
public class CmInmhViewedItemsManager {
    
    static List<InmhViewedItem> viewedItems = new ArrayList<InmhViewedItem>();

    
    /** Return true if this item has been viewed false
     * otherwise.
     * 
     * @param item
     * @return
     */
    static public boolean isViewed(InmhItemData item) {
        for(InmhViewedItem ivi: viewedItems) {
            if(ivi.getFile().equals(item.getFile())) {
                return true;
            }
        }
        return false;
    }
    
    
    /**
     * Make sure the list of valid items is updated
     * 
     */
    static public void fireUpdateViewedInmhItems() {

        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getViewedInmhItems(UserInfo.getInstance().getRunId(), new AsyncCallback() {
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            public void onSuccess(Object arg0) {
                List<RpcData> data = (List<RpcData>) arg0;
                viewedItems.clear();
                for(RpcData d: data) {
                    viewedItems.add(new InmhViewedItem(d.getDataAsString("type"), d.getDataAsString("file")));
                }
                
                ContextController.getInstance().fireContextChanged();
            }
        });
    }
}


class InmhViewedItem {
    String type;
    String file;
    
    public InmhViewedItem(String type, String file) {
        this.type = type;
        this.file = file;
    }
    
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getFile() {
        return file;
    }
    public void setFile(String file) {
        this.file = file;
    }
}
