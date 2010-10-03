package hotmath.gwt.cm_mobile2.client;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
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
    public void onModuleLoad() {

        _rootPanel = RootPanel.get("main-content");
        /** add the floater
         */
        _controlPanel = new ControlPanel();
        List<ControlAction> actions = new ArrayList<ControlAction>();
        actions.add(new ControlAction("Search for a lesson") {
            
            @Override
            public void doAction() {
                Controller.navigateToTopicList();
            }
        });
        _controlPanel.setControlActions(actions);
        
        _rootPanel.add(_controlPanel);
        _rootPanel.add(createApplicationPanel());

        Screen screen = new Screen();
        screen.addHandler(this);
        orientationChanged(screen.getScreenOrientation());
        
        CatchupMathMobileShared.__instance.hideBusyPanel();

        _rootPanel.getElement().getStyle().setProperty("display", "inline");
        
        
        

        initializeExternalJs();

        CatchupMathMobileShared.__instance.user = new CmMobileUser();
        
        History.addValueChangeHandler(new CatchupMathMobileHistoryListener());
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
                }
                else if(type == EventTypes.EVENT_SERVER_START) {
                    CatchupMathMobile2._controlPanel.showBusy(true);
                }
                else if(type == EventTypes.EVENT_SERVER_END) {
                    CatchupMathMobile2._controlPanel.showBusy(false);
                }
            }
        });
    }

    @Override
    public void orientationChanged(ScreenOrientation newOrientation) {
    }
}
