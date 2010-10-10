package hotmath.gwt.cm_mobile2.client;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.CmMobileResourceViewer;
import hotmath.gwt.cm_mobile_shared.client.ControlPanel;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.HeaderPanel;
import hotmath.gwt.cm_mobile_shared.client.PagesContainerPanel;
import hotmath.gwt.cm_mobile_shared.client.ScreenOrientation;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.CmEventListener;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventType;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_mobile_shared.client.util.Screen.OrientationChangedHandler;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provide minimal CM for mobile access.
 * 
 * @author casey
 * 
 */
public class CatchupMathMobile2 implements EntryPoint, OrientationChangedHandler {

    RootPanel _rootPanel;
    static ControlPanel _controlPanel;
    static public CatchupMathMobile2 __instance;
    public void onModuleLoad() {

        __instance = this;
        _rootPanel = RootPanel.get("main-content");
        
        
        if(true) {
            _rootPanel.add(new TextBox());
            Window.alert("Text Box Created");
            return;
        }
        
        
        /** add the floater
         */
        _controlPanel = new ControlPanel();
        
        _rootPanel.add(_controlPanel);
        _rootPanel.add(createApplicationPanel());
        
        //_rootPanel.add(createTestPanel());

        Screen screen = new Screen();
        screen.addHandler(this);
        orientationChanged(screen.getScreenOrientation());
        
        CatchupMathMobileShared.__instance.hideBusyPanel();

        _rootPanel.getElement().getStyle().setProperty("display", "inline");
        
        initializeExternalJs();

        CatchupMathMobileShared.__instance.user = new CmMobileUser();
        
        orientationChanged(ScreenOrientation.Portrait);
        
        History.addValueChangeHandler(new CatchupMathMobileHistoryListener());
    }

    private native void gotoBottomOfDoc()/*-{
        $wnd.scrollTo(0,10000);
    }-*/;
    
    private native void gotoTopOfDoc()/*-{
        $wnd.scrollTo(0,0);
    }-*/;    

    
    private FlowPanel createTestPanel() {
        FlowPanel fp = new FlowPanel();
        String html = "";
        fp.add(new Button("End",new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                gotoBottomOfDoc();
            }
        }));
        for(int i=0;i<100;i++) {
            fp.add(new HTML("<p>as dfaskdfas;fasdf askdf asf;asf sadf asfsadf adfkasdfasdf TEST: " + i + "</h1>"));
        }
        fp.add(new HTML(html));
        return fp;
    }


    /** call global JS function to intialize any external resources
     * 
     */
    private native void initializeExternalJs()/*-{
        $wnd.initializeExternalJs();
    }-*/;    

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
    
    static CmMobileResourceViewer __lastViewer;
    static {
        publishNative();
        EventBus.getInstance().addEventListener(new CmEventListener() {
            
            @Override
            public void handleEvent(CmEvent event) {
                EventType type = event.getEventType();
                if(type == EventTypes.EVENT_PAGE_LOADED) {
                    _controlPanel.setControlActions(((IPage)event.getEventData()).getControlFloaterActions());
                }
                else if(type == EventTypes.EVENT_PAGE_REMOVED) {
                }
                else if(type == EventTypes.EVENT_PAGE_ACTIVATED) {
                }
                else if(type == EventTypes.EVENT_SERVER_START) {
                    CatchupMathMobile2._controlPanel.showBusy(true);
                }
                else if(type == EventTypes.EVENT_SERVER_END) {
                    CatchupMathMobile2._controlPanel.showBusy(false);
                }
                else if(type == EventTypes.EVENT_LOAD_RESOURCE) {
                    InmhItemData d = (InmhItemData)event.getEventData();
                    
                    History.newItem("res_object:" + d.getFile());
                    Controller.navigateToPrescriptionResource(null, d, 0);
                }
                else if(type == EventTypes.EVENT_RES_VIEW_LOADED) {
                    __lastViewer = (CmMobileResourceViewer)event.getEventData();
                }
            }
        });
    }
    
    /** External JS request to load a resource
     * 
     * @param type
     * @param file
     */
    static public void doResourceLoad(String type, String file) {
        History.newItem("res_object:" + type + ":" + file);
        //InmhItemData inmh = new InmhItemData(type,file,"");
        //Controller.navigateToPrescriptionResource(null, inmh,-1);
    }
    
    static public void backToLesson() {
        Controller.navigateBack();
    }
    
    static private native void publishNative() /*-{
        $wnd.doLoadResource_Gwt = @hotmath.gwt.cm_mobile2.client.CatchupMathMobile2::doResourceLoad(Ljava/lang/String;Ljava/lang/String;);
        $wnd.gwt_backToLesson = @hotmath.gwt.cm_mobile2.client.CatchupMathMobile2::backToLesson();
    }-*/;


    @Override
    public void orientationChanged(ScreenOrientation newOrientation) {
        if (newOrientation == ScreenOrientation.Portrait) {
            _rootPanel.removeStyleName("landscape");
            _rootPanel.addStyleName("portrait");
        } else {
            _rootPanel.addStyleName("landscape");
            _rootPanel.removeStyleName("portrait");
        }
    }
}
