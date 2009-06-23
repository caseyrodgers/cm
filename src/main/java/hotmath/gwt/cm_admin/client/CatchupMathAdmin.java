package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_admin.client.service.RegistrationService;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_admin.client.ui.AccountInfoPanel;
import hotmath.gwt.cm_admin.client.ui.FooterPanel;
import hotmath.gwt.cm_admin.client.ui.HeaderPanel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_admin.client.ui.StudentShowWorkPanel;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMathAdmin implements EntryPoint, ValueChangeHandler<String> {

    Viewport mainPort;
    LayoutContainer mainContainer;
    HeaderPanel headerPanel;
    FooterPanel footerPanel;
    int userId;
    CmAdminModel cmAdminMdl;
    AccountInfoPanel infoPanel;
    StudentGridPanel sgp;
    

    static CatchupMathAdmin instance;

    public void onModuleLoad() {
        
        Log.info("CatchupMathAdmin is starting");

        instance = this;

        // GXT.setDefaultTheme(Theme.GRAY, true);

        try {
            userId = CmShared.handleLoginProcess();
            UserInfoBase user = UserInfoBase.getInstance();
            if (user != null) {
                if (!user.isAdmin()) {
                    // TODO: restore Admin check
                    // throw new Exception("Not an admin");
                }
            } else {
                throw new Exception("Login failed!");
            }
            cmAdminMdl = new CmAdminModel();
            cmAdminMdl.setId(userId);
        } catch (Exception e) {
            CatchupMathAdmin.showAlert(e.getMessage(), new CmAsyncRequest() {
                public void requestComplete(String requestData) {
                    Window.Location.assign("/"); // goto home
                }

                public void requestFailed(int code, String text) {
                }
            });
            return;
        }

        registerRpcServices();
        
        
    
        mainPort = new Viewport();
        mainPort.setLayout(new BorderLayout());
        mainPort.setEnableScroll(false);

        BorderLayoutData bdata = new BorderLayoutData(LayoutRegion.NORTH, 40);
        headerPanel = new HeaderPanel();
        mainPort.add(headerPanel, bdata);

        mainContainer = new LayoutContainer();
        mainContainer.setStyleName("main-container");
        mainContainer.setLayout(new FitLayout());

        mainPort.add(mainContainer, new BorderLayoutData(LayoutRegion.CENTER));

        /**
         * Monitor size of main area, to keep grid centered
         * 
         * @TODO: move this closer to StudentGridPanel (out of here?)
         */
        mainPort.addListener(Events.Resize, new Listener() {
            public void handleEvent(BaseEvent be) {
                if (StudentGridPanel.instance != null) {
                    StudentGridPanel.instance.resizeChildren();
                }
            }
        });

        RootPanel.get().add(mainPort);


        infoPanel = new AccountInfoPanel(cmAdminMdl);
        sgp = new StudentGridPanel(cmAdminMdl);
        CmAdminDataReader.getInstance().fireRefreshData();

        // If the application starts with no history token, redirect to a new
        String initToken = History.getToken();
        if (initToken.length() == 0) {
            History.newItem("main");
        }
        History.addValueChangeHandler(this);
        History.fireCurrentHistoryState();

    }

    private void loadMainPage() {
        Log.info("Loading CMAdmin main page");
        mainContainer.removeAll();
        mainContainer.setLayout(new BorderLayout());

        
        mainContainer.add(infoPanel, new BorderLayoutData(LayoutRegion.NORTH, 150));
        
        mainContainer.add(sgp, new BorderLayoutData(LayoutRegion.CENTER));
        
        mainContainer.layout();
        
        StudentGridPanel.instance.resizeChildren();
    }
    
    private void loadShowWorkPage() {
        Log.info("Loading CMAdmin show work page");
        mainContainer.removeAll();
        mainContainer.setLayout(new FitLayout());
        
        mainContainer.add(new StudentShowWorkPanel());
        mainContainer.layout();
    }
    

    /**
     * Register RPC services
     */
    private void registerRpcServices() {

        final RegistrationServiceAsync regService = (RegistrationServiceAsync) GWT.create(RegistrationService.class);

        String point = GWT.getModuleBaseURL();
        if (!point.endsWith("/"))
            point += "/";
        point += "services/registrationService";

        ((ServiceDefTarget) regService).setServiceEntryPoint(point);
        Registry.register("registrationService", regService);
    }

    @SuppressWarnings("unchecked")
    public static void showAlert(String msg) {
        MessageBox.alert("Info", msg, new Listener() {
            public void handleEvent(BaseEvent be) {
                // empty
            }
        });
    }

    public static void showAlert(String msg, final CmAsyncRequest callback) {
        // setBusy(false);
        MessageBox.alert("Info", msg, new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (callback != null)
                    callback.requestComplete(be.getValue());
            }
        });
    }

    public void showMe() {
        ;
    }

    public static CatchupMathAdmin getInstance() {
        if (instance == null)
            instance = new CatchupMathAdmin();
        return instance;
    }
    
    @Override
    public void onValueChange(ValueChangeEvent<String> history) {
        
        Log.info("CatchupMathAdmin: history changed: " + history);
        
        if (history.getValue().equals("sw")) {
            loadShowWorkPage();
        } else {
            loadMainPage();
        }
    }
}
