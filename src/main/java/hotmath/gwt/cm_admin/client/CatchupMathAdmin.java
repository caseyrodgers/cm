package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_admin.client.ui.AccountInfoPanel;
import hotmath.gwt.cm_admin.client.ui.FooterPanel;
import hotmath.gwt.cm_admin.client.ui.HeaderPanel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_admin.client.ui.StudentShowWorkPanel;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
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

        CmLogger.info("CatchupMathAdmin is starting");

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
        
        
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                CmShared.handleLoginProcessAsync(new CmLoginAsync() {
                    @Override
                    public void loginSuccessful(Integer uid) {
                        completeLoginProcess(uid);
                    }
                });        
            }
        });
    }

    private void completeLoginProcess(int uid) {
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
        
        
        if(UserInfoBase.getInstance().getEmail() == null || UserInfoBase.getInstance().getEmail().length() == 0) {
        	new CollectEmailFromUserDialog();
        }
    }

    
    private void loadMainPage() {
        CmLogger.info("Loading CMAdmin main page");
        mainContainer.removeAll();


        mainContainer.setLayout(new BorderLayout());
        
        mainContainer.add(infoPanel, new BorderLayoutData(LayoutRegion.NORTH, 120));
        mainContainer.add(studentGrid, new BorderLayoutData(LayoutRegion.CENTER));

        mainContainer.layout();
    }

    private void loadShowWorkPage() {
        CmLogger.info("Loading CMAdmin show work page");
        mainContainer.removeAll();
        mainContainer.setLayout(new FitLayout());

        mainContainer.add(new StudentShowWorkPanel());
        mainContainer.layout();
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

        CmLogger.info("CatchupMathAdmin: history changed: " + history);

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


