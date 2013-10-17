package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_admin.client.ui.AccountInfoPanel;
import hotmath.gwt.cm_admin.client.ui.AssignmentManagerDialog2;
import hotmath.gwt.cm_admin.client.ui.AssignmentStatusDialog;
import hotmath.gwt.cm_admin.client.ui.FooterPanel;
import hotmath.gwt.cm_admin.client.ui.HeaderPanel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_admin.client.ui.StudentShowWorkPanel;
import hotmath.gwt.cm_admin.client.ui.WebLinkEditorDialog;
import hotmath.gwt.cm_admin.client.ui.WebLinksManager;
import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentQuestionViewerPanel;
import hotmath.gwt.cm_admin.client.ui.assignment.EditAssignmentDialog;
import hotmath.gwt.cm_admin.client.ui.assignment.FinalExamCreationManager;
import hotmath.gwt.cm_admin.client.ui.highlights.HighlightsDataWindow;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.CallbackGeneric;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MessageOfTheDayDialog;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.UserActivityLogDialog;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageWindow;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
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

        
//        String launchSub = CmShared.getQueryParameter("load");
//        if(launchSub != null) {
//            if(launchSub.startsWith("student_details")) {
//                
//                Log.info("Launching tool Student Details");
//                int studentUid = Integer.parseInt(launchSub.split(":")[1]);
//                launchOnlyStudentDetails(studentUid);
//                return;
//            }
//            else if(launchSub.startsWith("student_registration")) {
//                Log.info("Launching tool Student Registration");
//                int studentUid = Integer.parseInt(launchSub.split(":")[1]);
//                launchOnlyStudentRegistration(studentUid);
//                return;
//            }
//        }

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
        
        if(CmShared.getQueryParameterValue("test").equals("final")) {
            FinalExamCreationManager.startTest();
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("assignments")) {
            AssignmentManagerDialog2.startTest();
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("assignment_status")) {
            AssignmentStatusDialog.startTest();
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("assignment_edit")) {
            EditAssignmentDialog.startTest();
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("ccss")) {
            CCSSCoverageWindow.startTest();
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("highlights")) {
            HighlightsDataWindow.getSharedInstance(2).setVisible(true);
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("register")) {
            RegisterStudent.startTest();
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("activity")) {
            UserActivityLogDialog.startTest();
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("weblinks")) {
            WebLinksManager.startTest();
            return;
        }
        else if(CmShared.getQueryParameterValue("test").equals("weblinkeditor")) {
            WebLinkEditorDialog.startTest();
            return;
        }        


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
//                        CmShared.getCmService().execute(action, this);
//                    }
//
//                    public void oncapture(StudentModelI student) {
//                        try {
//                            CmBusyManager.setBusy(false);
//                            CmAdminModel adminModel = new CmAdminModel();
//                            adminModel.setUid(2);
//                            
//                            RegisterStudent rs = new RegisterStudent(student, adminModel, true, true);
//                    		/**
//                    		 * Assign buttons to the button bar on the Window
//                    		 */
//                            ButtonBar bb = new ButtonBar();
//                    		for (TextButton btn : rs.getActionButtons()) {
//                    			btn.addStyleName("register-student-btn");
//                    			String txt = btn.getText();
//                    			if(txt.equals("Save")) {
//                    				bb.add(btn);
//                    			}
//                    		}
//                    		mainPort.add(rs);
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
//                      CmShared.getCmService().execute(action, this);
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
    
    

    private void setupAnyTests() {

         GWindow gw = new GWindow(true);
         AssignmentQuestionViewerPanel pan = new
         AssignmentQuestionViewerPanel();
         gw.setWidget(pan);
         gw.setVisible(true);
         
         if(true)
             return;
         
//        //
//        //
//        // final int uid = UserInfoBase.getInstance().getUid();
//        // String pid="alg2ptests3_coursetest_1_algebra2practicetest_20_1";
//        // int groupId=10;
//        // String name="Test";
//        // String comments = "Test";
//        // Date dueDate = new Date();
//        // CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
//        // List<Integer> uids = new ArrayList<Integer>();
//        // final String status="";
//        // Assignment ass = new
//        // Assignment(UserInfoBase.getInstance().getUid(),groupId, name,
//        // comments, dueDate,pids,uids,status);
//        //
//        // final ProblemDto problem = new ProblemDto(0,"lesson","label", pid);
//        //
//        // CmList<StudentProblemDto> statuses = new
//        // CmArrayList<StudentProblemDto>() {{
//        // add(new StudentProblemDto(uid,problem,status));
//        // }};
//        // StudentAssignment studentAssignment = new StudentAssignment(uid,ass,
//        // statuses);
//        //
//        // pan.viewQuestion(studentAssignment, problem);
//
//        if (false) {
//            final int uid = 2;
//            String pid = "test_dynamic_graphs_1_2_3$4";
//            int groupId = 10;
//            String name = "Test";
//            String comments = "Test";
//            Date dueDate = new Date();
//            CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
//            List<Integer> uids = new ArrayList<Integer>();
//            final String status = "";
//            Assignment ass = new Assignment(UserInfoBase.getInstance().getUid(), groupId, name, comments, dueDate,
//                    pids, uids, status);
//
//            final ProblemDto problem = new ProblemDto(0, "lesson", "label", pid, null, 0);
//
//            CmList<StudentProblemDto> statuses = new CmArrayList<StudentProblemDto>() {
//                {
//                    add(new StudentProblemDto(uid, problem, status, true, true));
//                }
//            };
//            StudentAssignment stuAssignment = new StudentAssignment(uid, ass, statuses);
//            GradeBookDialog gb = new GradeBookDialog(stuAssignment, new CallbackOnComplete() {
//
//                @Override
//                public void isComplete() {
//                }
//            });
//            // AssignmentGradingPanel agp = new
//            // AssignmentGradingPanel(stuAssignment);
//
//            return;
//        }
//
//        if (false) {
//            StudentModelExt sme = new StudentModelExt();
//            StudentActivityModel activityModel = new StudentActivityModel();
//            activityModel.setRunId(1196000);
//            activityModel.setTestId(0);
//            sme.setUid(27554);
//            new StudentShowWorkWindow(sme, activityModel);
//
//            return;
//        }
//
//        if (true) {
//            // new AssignmentManagerDialog2(2);
//            // return;
//            AddProblemDialog.showDialog(new AddProblemsCallback() {
//                @Override
//                public void problemsAdded(List<ProblemDto> problemsAdded) {
//                }
//            });
//            return;
//        }
    }
    
}
