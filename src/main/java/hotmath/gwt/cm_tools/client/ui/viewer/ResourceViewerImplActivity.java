package hotmath.gwt.cm_tools.client.ui.viewer;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

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
        
        CmLogger.debug("Resource: " + __lastItemData);
        // for testing
        //String t = "http://test.catchupmath.com/hotmath_help/games/factortris/factortris_hotmath_sound.swf";
        //__lastItemData.setFile(t);
        if (panel == null) {
            String flashFile = getResourceItem().getFile();
            HTML htmlDisplay = new HTML("<div style='width: 100%; height: 100%;' id=\"flash_activity\" src=\"" +  flashFile + "\" ></div>");
            addResource(htmlDisplay, getResourceItem().getTitle());
            panel = this;
        }
        return panel;
    }
    
    static {
        publishNativeRpaFlashInfrastructure();
        
    	EventBus.getInstance().addEventListener(new CmEventListener() {
			@Override
			public void handleEvent(hotmath.gwt.shared.client.eventbus.CmEvent event) {
				if(event.getEventType().equals(EventType.EVENT_TYPE_RESOURCE_VIEWER_OPEN)) {
					invokeRuffle();
				}
			}
		});        
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

    
    static private native void invokeRuffle()  /*-{
        $wnd.doRuffleTest();
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
