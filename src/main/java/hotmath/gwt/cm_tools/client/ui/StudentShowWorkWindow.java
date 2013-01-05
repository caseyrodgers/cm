package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.StudentShowWorkModelPojo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallback;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.CallbackAfterSolutionLoaded;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentShowWorkAction;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * Display students show work 
 * 
 * Allow for general restriction of problems within a single student
 * 
 * 
 * @author casey
 * 
 */
public class StudentShowWorkWindow extends GWindow {

    StudentModelI student;
    Integer runId;
    String programName;
    StudentActivityModel activityModel;
    BorderLayoutContainer _mainBorderPanel = new BorderLayoutContainer();

    public StudentShowWorkWindow(StudentModelI student) {
        this(student, null);
    }
    
    public StudentShowWorkWindow(StudentModelI student, StudentActivityModel activityModel) {
        super(false);
        
        setStyleName("student-show-work-window");
        this.student = student;
        this.activityModel = activityModel;
        
        setPixelSize(700,400);
        setResizable(true);
        setMaximizable(true);

        String title = "Show Work for " + student.getName();
        if(programName != null)
            title += " in program " + programName;
        setHeadingText(title);

        _mainBorderPanel.setWestWidget(createWestPanel(), new BorderLayoutData(200));
        
        centerContainer = createCenterPanel();
        _mainBorderPanel.setCenterWidget(centerContainer);

        TextButton closeBtn = new TextButton("Close");
        closeBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();            }
        });

        addButton(closeBtn);
        
        setWidget(_mainBorderPanel);

        setVisible(true);
    }

    BorderLayoutContainer westContainer;

    /**
     * Create the west component to provide list of solutions with show work
     * 
     * @return
     */
    private Widget createWestPanel() {
        westContainer = new BorderLayoutContainer();
        
        SimpleContainer head = new SimpleContainer();
        head.setWidget(new HTML("<div style='margin: 10px;'>Click on a date/time below to view the student show-work effort.</div>"));
        
        westContainer.setNorthWidget(head, new BorderLayoutData(75));

        getStudentShowWorkRPC();

        return westContainer;
    }

    CenterLayoutContainer centerContainer = new CenterLayoutContainer();

    private CenterLayoutContainer createCenterPanel() {
        String html = "<h1 style='color: blue;width: 200;'>Select an item from the list at left.</h1>";
        centerContainer.setWidget(new HTML(html));
        return centerContainer;
    }
    
    
    ShowWorkPanel _showWorkPanel;
    SolutionInfo _solutionInfo;

    /**
     * Create the center, main container with the show work and solution loaded
     * for pid.
     * 
     * @param pid
     * @return
     */
    private void setCenterPanelForPid(final String pid) {
        try {
            // create temp user object to identify this student
            UserInfo user = new UserInfo(student.getUid(),activityModel.getTestId());
            user.setRunId(activityModel.getRunId());
            UserInfo.setInstance(user);
            
            // TODO: disable calculator per student's advanced options?
            //       probably not since these should be the options in place when
            //       whiteboard work was done
            user.setDisableCalcAlways(true);
            
            
            
            _showWorkPanel = new ShowWorkPanel(new ShowWorkPanelCallback() {
                @Override
                public void showWorkIsReady() {
                    new RetryAction<CmList<WhiteboardCommand>>() {
                        @Override
                        public void attempt() {
                            CmBusyManager.setBusy(true);
                            GetWhiteboardDataAction action = new GetWhiteboardDataAction(student.getUid(), pid,  UserInfo.getInstance().getRunId());
                            setAction(action);
                            CmShared.getCmService().execute(action, this);
                        }

                        public void oncapture(CmList<WhiteboardCommand> whiteboardCommands) {
                            try {
                                _showWorkPanel.loadWhiteboard(whiteboardCommands);
                            } finally {
                                CmBusyManager.setBusy(false);
                            }
                        }
                    }.register();
                }
                
                @Override
                public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                    return new SaveWhiteboardDataAction(student.getUid(),activityModel.getRunId(), pid, commandType, data);
                }
                
                
                @Override
                public void windowResized() {
                    forceLayout();
                }   
            });
            
            _showWorkPanel.setupForPid(pid);
            
            
            
            
            
            final InmhItemData solItem = new InmhItemData();
            solItem.setType("practice");
            solItem.setFile(pid);
            
            TutorWrapperPanel tutorPanel = new TutorWrapperPanel(true, false, false, true,new TutorCallbackDefault());
            
            
            FlowLayoutContainer tutorFlow = new FlowLayoutContainer();
            tutorFlow.setScrollMode(ScrollMode.AUTO);
            tutorFlow.add(tutorPanel);

            BorderLayoutContainer borderLayoutContainer = new BorderLayoutContainer();
            borderLayoutContainer.addStyleName("whiteboard-container");
            

            BorderLayoutData bld = new BorderLayoutData();
            bld.setSplit(false);
            bld.setFloatable(false);
            bld.setCollapsible(false);
            borderLayoutContainer.setCenterWidget(tutorFlow,bld);

            bld = new BorderLayoutData(.50f);
            bld.setSplit(false);
            bld.setFloatable(false);
            bld.setCollapsible(false);
            bld.setCollapsed(false);
            
            borderLayoutContainer.setEastWidget(_showWorkPanel, bld);
            
            _mainBorderPanel.setCenterWidget(borderLayoutContainer);
            
            _mainBorderPanel.forceLayout();
            
            
            tutorPanel.loadSolution(pid, "Show Work",true, false, solItem.getWidgetJsonArgs(), new CallbackAfterSolutionLoaded() {
                @Override
                public void solutionLoaded(SolutionInfo solutionInfo) {
                    _solutionInfo = solutionInfo;
                    forceLayout();
                }
            });
        } catch (Exception e) {
            Log.error("Error creating Show Work panel for student: " + pid + "," + student.getUid(), e);
        }
    }

    
    private void setCenterPanelForPid2(final String pid) {
        try {
            // create temp user object to identify this student
            UserInfo user = new UserInfo(student.getUid(),activityModel.getTestId());
            user.setRunId(activityModel.getRunId());
            UserInfo.setInstance(user);
            
            // TODO: disable calculator per student's advanced options?
            //       probably not since these should be the options in place when
            //       whiteboard work was done
            user.setDisableCalcAlways(true);
            
            
            
            _showWorkPanel = new ShowWorkPanel(new ShowWorkPanelCallback() {
                @Override
                public void showWorkIsReady() {
                    new RetryAction<CmList<WhiteboardCommand>>() {
                        @Override
                        public void attempt() {
                            CmBusyManager.setBusy(true);
                            GetWhiteboardDataAction action = new GetWhiteboardDataAction(student.getUid(), pid,  UserInfo.getInstance().getRunId());
                            setAction(action);
                            CmShared.getCmService().execute(action, this);
                        }

                        public void oncapture(CmList<WhiteboardCommand> whiteboardCommands) {
                            try {
                                _showWorkPanel.loadWhiteboard(whiteboardCommands);
                            } finally {
                                CmBusyManager.setBusy(false);
                            }
                        }
                    }.register();
                }
                
                @Override
                public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                    return null;
                }
                
                
                @Override
                public void windowResized() {
                    forceLayout();
                }   
            });
            

            
            _showWorkPanel.setupForPid(pid);

            final InmhItemData solItem = new InmhItemData();
            solItem.setType("practice");
            solItem.setFile(pid);
            
            TutorWrapperPanel tutorPanel = new TutorWrapperPanel(true, false, false, true,new TutorCallbackDefault());
            
            
            final BorderLayoutContainer borderLayout = new BorderLayoutContainer();

            BorderLayoutData ld = new BorderLayoutData(400);
            ld.setSplit(true);
            ld.setCollapsible(true);
            ld.setFloatable(true);

            
            
            FlowLayoutContainer flc = new FlowLayoutContainer();
            flc.setScrollMode(ScrollMode.AUTO);
            flc.add(tutorPanel);
            
            borderLayout.setWestWidget(flc, ld);
            
            
            ld = new BorderLayoutData(300);
            borderLayout.setEastWidget(_showWorkPanel,ld);
            
            
            
            _mainBorderPanel.setCenterWidget(_showWorkPanel);
            
            tutorPanel.loadSolution(pid, "Show Work",true, false, solItem.getWidgetJsonArgs(), new CallbackAfterSolutionLoaded() {
                @Override
                public void solutionLoaded(SolutionInfo solutionInfo) {
                    _solutionInfo = solutionInfo;
                    forceLayout();
                }
            });
            
        } catch (Exception e) {
            Log.error("Error creating Show Work panel for student: " + pid + "," + student.getUid(), e);
        }
    }
    
    public interface DataProperties extends PropertyAccess {
        ModelKeyProvider<StudentShowWorkModelPojo> viewTimeKey();
        ValueProvider<StudentShowWorkModelPojo, String> label();
     }
    
    ListView<StudentShowWorkModelPojo, String> _listView;
    private void createDataList(List<StudentShowWorkModelPojo> showWork) {
        
        
        DataProperties dp = GWT.create(DataProperties.class);
        
        final ListStore<StudentShowWorkModelPojo> store = new ListStore<StudentShowWorkModelPojo>(dp.viewTimeKey());
        
        _listView = new ListView<StudentShowWorkModelPojo, String>(store, dp.label());
        _listView.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<StudentShowWorkModelPojo>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<StudentShowWorkModelPojo> event) {
                if(_listView.getSelectionModel().getSelectedItem() != null) {
                    String pid = _listView.getSelectionModel().getSelectedItem().getPid(); 
                    Log.debug("StudentShoworkWindow: " + "Loading solution: " + pid);
                    setCenterPanelForPid(pid);
                }
            }
        });
        
        // _listView.setSimpleTemplate("<div>{view_time}&nbsp;&nbsp;&nbsp;&nbsp;{label}</div>");
        
        store.addAll(showWork);
        
        westContainer.setCenterWidget(_listView);
        
        forceLayout();
    }

    protected void getStudentShowWorkRPC() {

        new RetryAction<CmList<StudentShowWorkModelPojo>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                Log.debug("StudentShowWorkWindow: reading student show work list");
                GetStudentShowWorkAction action = new GetStudentShowWorkAction(student.getUid(), activityModel.getRunId());
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            public void oncapture(CmList<StudentShowWorkModelPojo> list) {
                CmBusyManager.setBusy(false);
                if(list.size() == 0) {
                    CatchupMathTools.showAlert("Student has not entered any answers.");
                    close();
                }
                else {
                    createDataList(list);
                }
                Log.debug("StudentShowWorkWindow: student show work read successfully");
            }

            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                super.onFailure(caught);
            }
        }.register();
    }
}



