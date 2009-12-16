package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_admin.client.service.RegistrationService;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_admin.client.ui.AccountInfoPanel;
import hotmath.gwt.cm_admin.client.ui.FooterPanel;
import hotmath.gwt.cm_admin.client.ui.GettingStartedGuideWindow;
import hotmath.gwt.cm_admin.client.ui.HeaderPanel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_admin.client.ui.StudentShowWorkPanel;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMathAdmin implements EntryPoint, ValueChangeHandler<String> {

    Viewport mainPort;
    LayoutContainer mainContainer;
    HeaderPanel headerPanel;
    FooterPanel footerPanel;
    CmAdminModel cmAdminMdl;
    AccountInfoPanel infoPanel;
    StudentGridPanel studentGrid;

    static CatchupMathAdmin instance;

    public void onModuleLoad() {

        Log.info("CatchupMathAdmin is starting");
        
        registerRpcServices();

        instance = this;

        mainPort = new Viewport();
        CmBusyManager.setViewPort(mainPort);
        
        mainPort.setLayout(new BorderLayout());
        
        BorderLayoutData bdata = new BorderLayoutData(LayoutRegion.NORTH, 40);
        headerPanel = new HeaderPanel();
        mainPort.add(headerPanel, bdata);

        
        mainContainer = new LayoutContainer();
        mainContainer.setStyleName("main-container");
        mainContainer.setLayout(new FitLayout());
        
        mainPort.add(mainContainer, new BorderLayoutData(LayoutRegion.CENTER));

        RootPanel.get("main-content").add(mainPort);
        
        CmShared.handleLoginProcessAsync(new CmLoginAsync() {
            @Override
            public void loginSuccessful(Integer uid) {
                completeLoginProcess(uid);
            }
        });        
    }

    private void completeLoginProcess(int uid) {

        UserInfoBase user = UserInfoBase.getInstance();
        cmAdminMdl = new CmAdminModel();
        cmAdminMdl.setId(uid);
        
        infoPanel = new AccountInfoPanel(cmAdminMdl);
        studentGrid = new StudentGridPanel(cmAdminMdl);
        
        
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
        
        mainContainer.add(infoPanel, new BorderLayoutData(LayoutRegion.NORTH, 120));
        mainContainer.add(studentGrid, new BorderLayoutData(LayoutRegion.CENTER));

        mainContainer.layout();
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

    public AccountInfoPanel getAccountInfoPanel() {
        return infoPanel;
    }
}








class AdminTopComponent extends LayoutContainer {
    public AdminTopComponent() {
        add(new Label("Top"));
    }
}


class AdminCenterComponent extends LayoutContainer {
    public AdminCenterComponent() {
        add(new Label("Center"));
    }
}

class AdminBottomComponent extends LayoutContainer {
    public AdminBottomComponent() {
        add(new Label("Bottom"));
    }
}

