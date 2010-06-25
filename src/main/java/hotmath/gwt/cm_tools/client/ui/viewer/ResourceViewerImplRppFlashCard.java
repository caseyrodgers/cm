package hotmath.gwt.cm_tools.client.ui.viewer;

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

    @Override
    public Widget getResourcePanel() {
        Widget widget = super.getResourcePanel();
        
        getResourceItem().setViewed(true);
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REQUIRED_COMPLETE,getResourceItem()));
        return widget;
    }

    static public String flash_Rpp_getCompletionRule() {
        CmLogger.info("flash_Rpp_getCompletionRule called");
        return "{\"rule\":\"time\",\"limit\":\"45\"}";
    }
    
    static public void flash_RppComplete() {
    	CmLogger.info("flash_RppComplete called");
    }
    
    static private native void publishNative() /*-{
        $wnd.flash_Rpp_getCompletionRule  = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplRppFlashCard::flash_Rpp_getCompletionRule();
        $wnd.flash_RppComplete  = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplRppFlashCard::flash_RppComplete();
     }-*/;
}
