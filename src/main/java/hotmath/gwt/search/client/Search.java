package hotmath.gwt.search.client;

import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.event.CmLogoutEvent;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.CmIdleTimeWatcher;
import hotmath.gwt.cm_core.client.util.CmBusyManager.BusyHandler;
import hotmath.gwt.cm_core.client.util.CmBusyManager.BusyState;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.search.TopicExplorerSearchPanel;
import hotmath.gwt.search.client.ui.HeaderPanel;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Search implements EntryPoint {

    public Viewport _mainPort;
    BorderLayoutContainer _mainPortWrapper = new BorderLayoutContainer();
    public SimpleContainer _mainContainer;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        CmLogger.info("Catchup Math Search Startup");
        
        
        HeaderPanel headerPanel = new HeaderPanel();

        // GXT.setDefaultTheme(Theme.GRAY, true);

        _mainPort = new Viewport() {
            protected void onWindowResize(int width, int height) {
                super.onWindowResize(width, height);
                if (CmMainPanel.getActiveInstance() != null && CmMainPanel.getActiveInstance()._mainContentWrapper != null) {
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_WINDOW_RESIZED));
                }
                if (CmRpcCore.EVENT_BUS != null) {
                    CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
                }
            }
        };
        _mainPort.setWidget(_mainPortWrapper);

        CmBusyManager.setBusyHandler(new BusyHandler() {
            
            @Override
            public void showMask(BusyState state) {
                _mainPort.mask();
            }
            
            @Override
            public void hideMask() {
                _mainPort.unmask();
            }
        });

        _mainContainer = new SimpleContainer();
        _mainContainer.setStyleName("main-container");
        _mainContainer.getElement().setId("main-container");

        _mainPortWrapper.setNorthWidget(headerPanel, new BorderLayoutData(38));

        /** Turn on debugging CSS */
        if (CmCore.getQueryParameter("debug") != null) {
                _mainPortWrapper.addStyleName("debug-mode");
        }
        
        _mainPortWrapper.setCenterWidget(new TopicExplorerSearchPanel());
        RootPanel.get("main-content").add(_mainPort);


        /** add a low level down handler to catch any mouse down event
         *
         */
        _mainContainer.addDomHandler(new MouseDownHandler() {
            @Override
            public void onMouseDown(MouseDownEvent event) {
                CmIdleTimeWatcher.getInstance().didKeyBoardActivity();
            }
        }, MouseDownEvent.getType());
        CmIdleTimeWatcher.getInstance();

        Window.addCloseHandler(new CloseHandler<Window>() {
            @Override
            public void onClose(CloseEvent<Window> event) {
                CmRpcCore.EVENT_BUS.fireEvent(new CmLogoutEvent());
            }
        });
        
        CmBusyManager.showLoading(false);
    }
}
