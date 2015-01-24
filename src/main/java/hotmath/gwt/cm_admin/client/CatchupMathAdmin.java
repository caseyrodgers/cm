package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_admin.client.ui.AccountInfoPanel;
import hotmath.gwt.cm_admin.client.ui.FooterPanel;
import hotmath.gwt.cm_admin.client.ui.HeaderPanel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_admin.client.ui.StudentShowWorkPanel;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.CallbackGeneric;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.MessageOfTheDayDialog;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.RootPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.Viewport;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class CatchupMathAdmin implements EntryPoint, ValueChangeHandler<String> {

    Viewport mainPort;
    BorderLayoutContainer mainContainer;
    HeaderPanel headerPanel;
    FooterPanel footerPanel;
    CmAdminModel cmAdminMdl;
    AccountInfoPanel infoPanel;
    StudentGridPanel studentGrid;

    static CatchupMathAdmin instance;

    public void onModuleLoad() {

        CmLogger.info("CatchupMathAdmin is starting");

        instance = this;

        mainPort = new Viewport() {
            protected void onWindowResize(int width, int height) {
                super.onWindowResize(width, height);
                CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        };
        CmBusyManager.setViewPort(mainPort);


        /** 
        if(CatchupMathAdminTests.runTest()) {
            CmBusyManager.setBusy(false);
            return;
        }
        */
        
        BorderLayoutContainer borderMain = new BorderLayoutContainer();

        BorderLayoutData bdata = new BorderLayoutData(40);
        headerPanel = new HeaderPanel();
        borderMain.setNorthWidget(headerPanel, bdata);

        mainContainer = new BorderLayoutContainer();
        mainContainer.addStyleName("main-container");


        //borderMain.getElement().setAttribute("style",  "background-color: red");

        borderMain.setCenterWidget(mainContainer);
        mainPort.setWidget(borderMain);

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


    private void completeLoginProcess(final int uid) {

        Log.debug("CatchupMathAdmin: login complete, is Mobile: " + UserInfoBase.getInstance().isMobile());
        new MessageOfTheDayDialog(new CallbackGeneric() {
            @Override
            public void callbackReady() {
                Log.debug("Message of day callback complete.");
            }
        });


        cmAdminMdl = new CmAdminModel();
        cmAdminMdl.setUid(uid);

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

        if (UserInfoBase.getInstance().getEmail() == null || UserInfoBase.getInstance().getEmail().length() == 0) {
            new CollectEmailFromUserDialog();
        }
    }

    private void loadMainPage() {
        CmLogger.info("Loading CMAdmin main page");
        mainContainer.clear();

        mainContainer.setNorthWidget(infoPanel, new BorderLayoutData(120));
        mainContainer.setCenterWidget(studentGrid);

        mainContainer.forceLayout();
    }

    private void loadShowWorkPage() {
        CmLogger.info("Loading CMAdmin show work page");
        mainContainer.clear();

        mainContainer.setCenterWidget(new StudentShowWorkPanel());
        mainContainer.forceLayout();
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


//    private void launchOnlyStudentRegistration(final int studentUid) {
//        GWT.runAsync(new CmRunAsyncCallback() {
//            @Override
//            public void onSuccess() {
//                new RetryAction<StudentModelI>() {
//
//                    @Override
//                    public void attempt() {
//                        CmBusyManager.setBusy(true);
//                        GetStudentModelAction action = new GetStudentModelAction(studentUid);
//                        setAction(action);
//                        CmRpcCore.getCmService().execute(action, this);
//                    }
//
//                    public void oncapture(StudentModelI student) {
//                        try {
//                            CmBusyManager.setBusy(false);
//                            CmAdminModel adminModel = new CmAdminModel();
//                            adminModel.setUid(2);
//
//                            RegisterStudent rs = new RegisterStudent(student, adminModel, true, true);
//                              /**
//                               * Assign buttons to the button bar on the Window
//                               */
//                            ButtonBar bb = new ButtonBar();
//                              for (TextButton btn : rs.getActionButtons()) {
//                                      btn.addStyleName("register-student-btn");
//                                      String txt = btn.getText();
//                                      if(txt.equals("Save")) {
//                                              bb.add(btn);
//                                      }
//                              }
//                              mainPort.add(rs);
//                            RootPanel.get().add(mainPort);
//                        } finally {
//                            CmBusyManager.setBusy(false);
//                        }
//                    }
//                }.register();
//            }
//        });
//    }

//    private void launchOnlyStudentDetails(final int studentUid) {
//      GWT.runAsync(new CmRunAsyncCallback() {
//
//          @Override
//          public void onSuccess() {
//              new RetryAction<StudentModelI>() {
//                  @Override
//                  public void attempt() {
//                      CmBusyManager.setBusy(true);
//                      GetStudentModelAction action = new GetStudentModelAction(studentUid);
//                      setAction(action);
//                      CmRpcCore.getCmService().execute(action, this);
//                  }
//
//                  public void oncapture(StudentModelI student) {
//                      CmBusyManager.setBusy(false);
//                      mainPort.setLayout(new FitLayout());
//                      mainPort.add(new StudentDetailsPanel(new StudentModelExt(student)));
//                      RootPanel.get().add(mainPort);
//                  }
//              }.register();
//          }
//      });
//    }


}
