package hotmath.gwt.cm_admin.client;

import hotmath.gwt.cm_admin.client.ui.AccountInfoPanel;
import hotmath.gwt.cm_admin.client.ui.FooterPanel;
import hotmath.gwt.cm_admin.client.ui.HeaderPanel;
import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_admin.client.ui.StudentShowWorkPanel;
import hotmath.gwt.cm_admin.client.ui.assignment.AddProblemDialog;
import hotmath.gwt.cm_admin.client.ui.assignment.AddProblemDialog.AddProblemsCallback;
import hotmath.gwt.cm_admin.client.ui.assignment.GradeBookDialog;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.ui.CallbackGeneric;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.MessageOfTheDayDialog;
import hotmath.gwt.cm_tools.client.ui.StudentShowWorkWindow;
import hotmath.gwt.shared.client.CmLoginAsync;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
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

        mainPort = new Viewport() {
            protected void onWindowResize(int width, int height) {
                super.onWindowResize(width,  height);
                CmRpc.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        };
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

    
    private void completeLoginProcess(final int uid) {

        if(CmShared.getQueryParameter("setup_tests") != null) {
            Log.debug("Running manual setup tests.");
            setupAnyTests();
            return;
        }
                
        
        new MessageOfTheDayDialog(new CallbackGeneric() {
            @Override
            public void callbackReady() {
                Log.debug("Message of day callback complete.");
            }
        });
        
        
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
    
    
    private void setupAnyTests() {
        
//        GWindow gw = new GWindow(true);
//        AssignmentQuestionViewerPanel pan = new AssignmentQuestionViewerPanel();
//        gw.setWidget(pan);
//        gw.setVisible(true);
//
//        
//      final int uid = UserInfoBase.getInstance().getUid();
//      String pid="alg2ptests3_coursetest_1_algebra2practicetest_20_1";
//      int groupId=10;
//      String name="Test";
//      String comments = "Test";
//      Date dueDate = new Date();
//      CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
//      List<Integer> uids = new ArrayList<Integer>();
//      final String status="";
//      Assignment ass = new Assignment(UserInfoBase.getInstance().getUid(),groupId, name, comments, dueDate,pids,uids,status);
//      
//      final ProblemDto problem = new ProblemDto(0,"lesson","label", pid);
//              
//      CmList<StudentProblemDto> statuses = new CmArrayList<StudentProblemDto>() {{
//          add(new StudentProblemDto(uid,problem,status));  
//      }};
//      StudentAssignment studentAssignment = new StudentAssignment(uid,ass, statuses);
//        
//        pan.viewQuestion(studentAssignment, problem);
        
      if(true) {
          final int uid=2;
          String pid="test_dynamic_graphs_1_2_3$4";
          int groupId=10;
          String name="Test";
          String comments = "Test";
          Date dueDate = new Date();
          CmList<ProblemDto> pids = new CmArrayList<ProblemDto>();
          List<Integer> uids = new ArrayList<Integer>();
          final String status="";
          Assignment ass = new Assignment(UserInfoBase.getInstance().getUid(),groupId, name, comments, dueDate,pids,uids,status);
          
          final ProblemDto problem = new ProblemDto(0,"lesson","label", pid);
                  
          CmList<StudentProblemDto> statuses = new CmArrayList<StudentProblemDto>() {{
              add(new StudentProblemDto(uid,problem,status, true, true));  
          }};
          StudentAssignment stuAssignment = new StudentAssignment(uid,ass, statuses);
          GradeBookDialog gb = new GradeBookDialog(stuAssignment,new CallbackOnComplete() {
            
            @Override
            public void isComplete() {
            }
        });
          //AssignmentGradingPanel agp = new AssignmentGradingPanel(stuAssignment);
          
          return;
      }
      
      if(false) {
          StudentModelExt sme = new StudentModelExt();
          StudentActivityModel activityModel = new StudentActivityModel();
          activityModel.setRunId(1196000);
          activityModel.setTestId(0);
          sme.setUid(27554);
          new StudentShowWorkWindow(sme, activityModel);
          
          return;
      }
      
      
      
      
      if(false) {
//          new AssignmentManagerDialog2(2);
//          return;
          AddProblemDialog.showDialog(new AddProblemsCallback() {
              @Override
              public void problemsAdded(List<ProblemDto> problemsAdded) {
              }
          });
          return;
      }        
    }
}


