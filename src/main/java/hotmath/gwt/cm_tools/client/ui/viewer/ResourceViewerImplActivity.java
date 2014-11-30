package hotmath.gwt.cm_tools.client.ui.viewer;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import pl.rmalinowski.gwt2swf.client.ui.SWFSettings;
import pl.rmalinowski.gwt2swf.client.ui.SWFWidget;
import pl.rmalinowski.gwt2swf.client.utils.PlayerVersion;
import pl.rmalinowski.gwt2swf.client.utils.SWFObjectUtil;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class ResourceViewerImplActivity extends ResourceViewerImplFlash {
    public ResourceViewerImplActivity() {
        addStyleName("resource-viewer-impl-activity");
        
        
        // setScrollMode(Scroll.AUTOY);
        
        __useRpaInfrastructure=false;
    }

    Widget panel = null;
    static boolean __useRpaInfrastructure;

    public Widget getResourcePanel() {
        __lastItemData = getResourceItem();

        if (panel == null) {
            if (!SWFObjectUtil.isVersionIsValid(new PlayerVersion(CmShared.FLASH_MIN_VERSION))) {
                HTML html = new HTML(CmShared.FLASH_ALT_CONTENT);
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

    
    @Override
    public Boolean allowMaximize() {
        return false;
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
    
    
    /** Call when the completion rule has been satisfied
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

    static public void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                String t = "http://test.catchupmath.com/hotmath_help/games/factortris/factortris_hotmath_sound.swf";
                final GWindow w = new GWindow(true);
                
                
                ResourceViewerImplActivity ra = new ResourceViewerImplActivity();
                InmhItemData item = new InmhItemData(CmResourceType.ACTIVITY, t, "Test");
                ra.setResourceItem(item);
                
                final Widget resourcePanel = ra.getResourcePanel();
                resourcePanel.getElement().setAttribute("style",  "background: black");
                
                w.setWidget(resourcePanel);
                w.forceLayout();
                w.setVisible(true);
             
                new Timer() {
                    @Override
                    public void run() {
                        w.setWidget(resourcePanel);
                    }
                }.schedule(100);
            }
        });
        
        
        
    }
    
}
