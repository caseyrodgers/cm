package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Html;
import com.google.gwt.user.client.ui.Widget;

public class ResourceViewerImplActivity extends ResourceViewerImplFlash {
    public ResourceViewerImplActivity() {
        addStyleName("resource-viewer-impl-activity");
        setScrollMode(Scroll.AUTOY);
        
        __useRpaInfrastructure=false;
    }

    Widget panel = null;
    static boolean __useRpaInfrastructure;

    public Widget getResourcePanel() {
        __lastItemData = getResourceItem();

        if (panel == null) {
            if (!SWFObjectUtil.isVersionIsValid(new PlayerVersion(CmShared.FLASH_MIN_VERSION))) {
                Html html = new Html(CmShared.FLASH_ALT_CONTENT);
                addResource(html, getResourceItem().getTitle());
            } else {
                SWFSettings s = new SWFSettings();
                s.setMinPlayerVersion(new PlayerVersion(CmShared.FLASH_MIN_VERSION));
                String t = getResourceItem().getFile();
                SWFWidget swfWidget = new SWFWidget(t, "100%", "100%", s);
                swfWidget.addParam("wmode", "opaque");

                swfWidget.setStyleName("activity-widget");
                addResource(swfWidget, getResourceItem().getTitle());
            }
            panel = this;
        }
        return panel;
    }

    
    static {
        publishNativeRpaFlashInfrastructure();
    }

    
    /** Store the last loaded __lastItemData 
     *  ONLY when operating in a RPA configuration.
     *  
     *  If viewing as non-constrained (Flashcard)
     *  the do not set and make sure null is returned
     *  to Flash widget.
     *  
     */
    static InmhItemData __lastItemData;
    
    /** Return activity configuration JSON to caller.
     *  
     */
    static public String flash_Rpp_getCompletionRule() {
        if(!__useRpaInfrastructure) {
            return null;
        }
        else {
            String json = __lastItemData.getWidgetJsonArgs();
            // json = "{\"rule\":\"time\",\"limit\":\"5\"}";
            CmLogger.info("flash_Rpp_getCompletionRule called, and returned with '" + json);        
            return json;
        }
    }
    
    
    /** Call when the completion rule has been satisified
     * 
     */
    static public void flash_RppComplete() {
        CmLogger.info("flash_RppComplete called");
        if(!__lastItemData.isViewed()) {
            __lastItemData.setViewed(true);
            EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REQUIRED_COMPLETE,__lastItemData));
            __lastItemData.setViewed(true);
        }
    }
    
    /** Register two methods to handle the Flash RPA integration.
     * 
     *  First wnd.flash_Rpp_getCompletionRule is called to return JSON config.
     *  Then wnd.flash_Rpp_getCompletionRule is called after rule has been completed. 
     */
    static private native void publishNativeRpaFlashInfrastructure() /*-{
        $wnd.flash_RppComplete = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplRppFlashCard::flash_RppComplete();
        $wnd.flash_Rpp_getCompletionRule  = @hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplRppFlashCard::flash_Rpp_getCompletionRule();
     }-*/;
    
}
