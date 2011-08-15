package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.CmEventListener;
import hotmath.gwt.cm_mobile_shared.client.event.EventType;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * Provide minimal CM for mobile access.
 * 
 * @author casey
 * 
 */
public class CatchupMathMobileShared implements EntryPoint, Screen.OrientationChangedHandler {

    public static CatchupMathMobileShared __instance;

    RootPanel _rootPanel;

    public CmMobileUser user;
    SimplePanel mainPanel;

    public CatchupMathMobileShared() {
        __instance = this;
    }

    public void onModuleLoad() {
    
    }
    
    
    static public CmMobileUser getUser() {
        return __instance.user;
    }

    
    public void hideBusyPanel() {
        /** hide the startup spinner */
        Element startup = Document.get().getElementById("startup");
        startup.setClassName("hide");        
    }
    
    public void showBusyPanel() {
        /** hide the startup spinner */
        Element startup = Document.get().getElementById("startup");
        startup.removeClassName("hide");        
    }
        

    /**
     * Static routines used throughout app
     * 
     * TODO: move to separate module
     * 
     * @return
     */
    static public CmServiceAsync getCmService() {
        return _cmService;
    }

    static CmServiceAsync _cmService;

    static {
        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";

        point = "/cm/";
        
        _cmService = (CmServiceAsync) GWT.create(CmService.class);
        ((ServiceDefTarget) _cmService).setServiceEntryPoint(point + "services/cmService");

        _queryParameters = readQueryString();
    }

    /**
     * Return the parameter passed on query string
     * 
     * returns null if parameter not set
     * 
     * @param name
     * @return
     */
    static public String getQueryParameter(String name) {
        return _queryParameters.get(name);
    }

    static public int getQueryParameterInt(String name) {
        try {
            String val = getQueryParameter(name);
            if (val != null)
                return Integer.parseInt(val);
        } catch (Exception e) {
            /* silent */
        }
        return 0;
    }

    /**
     * Convert string+list to string+string of all URL parameters
     * 
     */
    static Map<String, String> _queryParameters;

    static private Map<String, String> readQueryString() {
        Map<String, String> m = new HashMap<String, String>();
        Map<String, List<String>> query = Window.Location.getParameterMap();
        for (String s : query.keySet()) {
            m.put(s, query.get(s).get(0));
        }
        return m;
    }

    @Override
    public void orientationChanged(ScreenOrientation newOrientation) {
        // Window.alert("Orientation Changed");
        if (newOrientation == ScreenOrientation.Portrait) {
            _rootPanel.removeStyleName("landscape");
            _rootPanel.addStyleName("portrait");
        } else {
            _rootPanel.addStyleName("landscape");
            _rootPanel.removeStyleName("portrait");
        }
    }

    
    static public native int resetViewPort() /*-{
       var scrollHeight=0;
       try {
           if($wnd.f_scrollTop) {
              scrollHeight = $wnd.f_scrollTop();
              $wnd.scrollTo(0,0);
              return scrollHeight;
           }
           return scrollHeight;
       }
       catch(e) {
           alert('error resetting view: ' + e);
           return 0;
       }
    }-*/;

    

    
    static private native void scrollToTop()  /*-{
        window.scrollTo(0, 1);
    }-*/;

    
    static {
        hotmath.gwt.cm_mobile_shared.client.event.EventBus.getInstance().addEventListener(new CmEventListener() {
            @Override
            public void handleEvent(CmEvent event) {
                EventType type = event.getEventType();
                if(type == EventTypes.EVENT_PAGE_LOADED) {
                }
                else if(type == EventTypes.EVENT_PAGE_REMOVED) {
                }
                else if(type == EventTypes.EVENT_PAGE_ACTIVATED) {
                }
            }
        });
    }
}
