package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.event.CmEvent;
import hotmath.gwt.cm_mobile.client.event.CmEventListener;
import hotmath.gwt.cm_mobile.client.event.EventBus;
import hotmath.gwt.cm_mobile.client.event.EventType;
import hotmath.gwt.cm_mobile.client.event.EventTypes;
import hotmath.gwt.cm_mobile.client.page.IPage;
import hotmath.gwt.cm_mobile.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile.client.util.ObservableStack;
import hotmath.gwt.cm_mobile.client.util.Screen;
import hotmath.gwt.cm_rpc.client.rpc.CmService;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provide minimal CM for mobile access.
 * 
 * @author casey
 * 
 */
public class CatchupMathMobile implements EntryPoint, Screen.OrientationChangedHandler {

    public static CatchupMathMobile __instance;

    RootPanel _rootPanel;

    public CmMobileUser user;
    SimplePanel mainPanel;
    ControlPanel controlPanel;

    public CatchupMathMobile() {
        __instance = this;
    }

    public void onModuleLoad() {

        _rootPanel = RootPanel.get("main-content");

        /** add the floater
         */
        controlPanel = new ControlPanel();
        _rootPanel.add(controlPanel);
        _rootPanel.add(createApplicationPanel());

        Screen screen = new Screen();
        screen.addHandler(this);
        orientationChanged(screen.getScreenOrientation());
        
        hideBusyPanel();

        _rootPanel.getElement().getStyle().setProperty("display", "inline");

        initializeExternalJs();

        History.addValueChangeHandler(new CatchupMathMobileHistoryListener());

        // showTestSolution();
        // if(true)return;

//        int uid = CatchupMathMobile.getQueryParameterInt("uid");
//        if (uid > 0) {
//            int testId = CatchupMathMobile.getQueryParameterInt("testId");
//            int testSegment = CatchupMathMobile.getQueryParameterInt("testSegment");
//            user = new CmMobileUser(uid, testId, testSegment, 0, 0);
//
//            History.newItem("quiz");
//        } else {
//            History.newItem("login:" + System.currentTimeMillis());
//        }
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

    /** call global JS function to intialize any external resources
     * 
     */
    private native void initializeExternalJs()/*-{
        $wnd.initializeExternalJs();
    }-*/;
    
    
    public ControlPanel getControlPanel() {
        return controlPanel;
    }

    static public CmMobileUser getUser() {
        return __instance.user;
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

    /**
     * Create a panel with a static header and scrollable body area.
     * 
     * The header will contain buttons and meta data. The body will container
     * the app data.
     * 
     * @return
     */
    private Widget createApplicationPanel() {
        /**
         * we want this to have an absolute size and be added as the top
         * component.
         * 
         */
        HeaderPanel headerPanel = new HeaderPanel();
        PagesContainerPanel pagesPanel = new PagesContainerPanel();

        FlowPanel fp = new FlowPanel();
        fp.setStyleName("app-panel");

        fp.add(headerPanel);
        fp.add(pagesPanel);

        ObservableStack<IPage> pageStack = new ObservableStack<IPage>();
        pagesPanel.bind(pageStack);
        headerPanel.bind(pageStack);
        Controller.init(pageStack);

        return fp;
    }
    
    static private native void scrollToTop()  /*-{
        window.scrollTo(0, 1);
    }-*/;

    
    
    static {
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                EventType type = event.getEventType();
                if(type == EventTypes.EVENT_PAGE_LOADED) {
                }
                else if(type == EventTypes.EVENT_PAGE_REMOVED) {
                }
                else if(type == EventTypes.EVENT_PAGE_ACTIVATED) {
                    IPage page = (IPage)event.getEventData();
                    scrollToTop();
                    page.setupControlFloater();
                }
            }
        });
    }
}
