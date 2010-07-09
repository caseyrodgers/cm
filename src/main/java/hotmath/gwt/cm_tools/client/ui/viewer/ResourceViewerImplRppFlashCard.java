package hotmath.gwt.cm_tools.client.ui.viewer;


import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.user.client.ui.Widget;


/** Holds the RPP as Flash Card widget
 * 
 * @author casey
 *
 */
public class ResourceViewerImplRppFlashCard extends ResourceViewerImplActivity  {
    static {
        publishNative();
    }

    static InmhItemData __lastItemData;
    
    @Override
    public Widget getResourcePanel() {
    	__lastItemData = getResourceItem();
        return super.getResourcePanel();
    }

    // {\"rule\":\"time\",\"limit\":\"5\"}";
    /** Return activity configuration JSON to caller.
     *  
     */
    static public String flash_Rpp_getCompletionRule() {
    	String json = __lastItemData.getWidgetJsonArgs();
    	json = "{\"rule\":\"time\",\"limit\":\"5\"}";
        CmLogger.info("flash_Rpp_getCompletionRule called, and returned with '" + json);    	
    	return json;
    }
    
    static public void flash_RppComplete() {
    	CmLogger.info("flash_RppComplete called");
    	__lastItemData.setViewed(true);
    	EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REQUIRED_COMPLETE,__lastItemData));
    	__lastItemData.setViewed(true);
    }
    
    /** Register two methods to handle the Flash RPA integration.
     * 
     *  First wnd.flash_Rpp_getCompletionRule is called to return JSON config.
     *  Then wnd.flash_Rpp_getCompletionRule is called after rule has been completed. 
     */
    static private native void publishNative() /*-{
        $wnd.flash_RppComplete = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplRppFlashCard::flash_RppComplete();
        $wnd.flash_Rpp_getCompletionRule  = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplRppFlashCard::flash_Rpp_getCompletionRule();
     }-*/;
}
