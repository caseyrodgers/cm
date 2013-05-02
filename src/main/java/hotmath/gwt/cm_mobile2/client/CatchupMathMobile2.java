package hotmath.gwt.cm_mobile2.client;

import hotmath.gwt.cm_mobile2.client.TopicViewPagePanel.Callback;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.CmMobileResourceViewer;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ControlPanel;
import hotmath.gwt.cm_mobile_shared.client.Controller;
import hotmath.gwt.cm_mobile_shared.client.ScreenOrientation;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.CmEvent;
import hotmath.gwt.cm_mobile_shared.client.event.CmEventListener;
import hotmath.gwt.cm_mobile_shared.client.event.EventBus;
import hotmath.gwt.cm_mobile_shared.client.event.EventType;
import hotmath.gwt.cm_mobile_shared.client.event.EventTypes;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetMobileLessonInfoAction;
import hotmath.gwt.cm_mobile_shared.client.rpc.MobileLessonInfo;
import hotmath.gwt.cm_mobile_shared.client.util.ObservableStack;
import hotmath.gwt.cm_mobile_shared.client.util.Screen;
import hotmath.gwt.cm_mobile_shared.client.util.Screen.OrientationChangedHandler;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionData;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
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
        
        /** add the floater
         */
        /** Add default actions */
        List<ControlAction> defaultActions = new ArrayList<ControlAction>();
        defaultActions.add(new ControlAction("Search for a lesson") {
            @Override
            public void doAction() {
                Controller.navigateToTopicList();
            }
        });
        defaultActions.add(new ControlAction("Login") {
            @Override
            public void doAction() {
                Controller.navigateToLogin();
            }
        });
        _controlPanel = new ControlPanel(defaultActions);
        _rootPanel.add(_controlPanel);     
        _rootPanel.add(createApplicationPanel());
   
        
        //_rootPanel.add(createTestPanel());

        Screen screen = new Screen();
        screen.addHandler(this);
        orientationChanged(screen.getScreenOrientation());
        
        CatchupMathMobileShared.__instance.hideBusyPanel();

        _rootPanel.getElement().getStyle().setProperty("display", "inline");
        
        //initializeExternalJs();

        SharedData.setData(new CmMobileUser());
        
        History.addValueChangeHandler(new CatchupMathMobileHistoryListener());
        
        History.fireCurrentHistoryState();
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

    ObservableStack<IPage> _pageStack;
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

        _pageStack = new ObservableStack<IPage>();
        pagesPanel.bind(_pageStack);
        headerPanel.bind(_pageStack);
        Controller.init(_pageStack);

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
                    History.newItem(new TokenParser("res_object",d.getFile(),0).getHistoryTag());
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
        History.newItem(new TokenParser("res_object",file,0).getHistoryTag());
    }
    
    static public void backToLesson() {
        Controller.navigateBack();
    }

    static private native void publishNative() /*-{
        $wnd.doLoadResource_Gwt = @hotmath.gwt.cm_mobile2.client.CatchupMathMobile2::doResourceLoad(Ljava/lang/String;Ljava/lang/String;);
        $wnd.gwt_backToLesson = @hotmath.gwt.cm_mobile2.client.CatchupMathMobile2::backToLesson();
    }-*/;
    
    
    static PrescriptionData pData;
    static public void loadLessonData(String file, final Callback callback) {
        
        if(pData != null && pData.getCurrSession().getInmhResources().get(0).getItems().get(0).getFile().equals(file)) {
            callback.isComplete(pData);
            return;
        }
        
        EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_START));
        GetMobileLessonInfoAction action = new GetMobileLessonInfoAction(file);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<MobileLessonInfo>() {
            @Override
            public void onSuccess(MobileLessonInfo lessonInfo) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                pData = lessonInfo.getPresData();
                SharedData.getMobileUser().setPrescripion(lessonInfo.getPresData());
                callback.isComplete(pData);
            }
            
            @Override
            public void onFailure(Throwable arg0) {
                EventBus.getInstance().fireEvent(new CmEvent(EventTypes.EVENT_SERVER_END));
                Window.alert(arg0.getMessage());
            }
        });
    }



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


