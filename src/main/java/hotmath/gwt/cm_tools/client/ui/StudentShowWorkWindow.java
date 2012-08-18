package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentShowWorkAction;

import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

/**
 * Display interface to allow user to show work
 * 
 * Allow for general restriction of problems within a single student
 * 
 * 
 * @author casey
 * 
 */
public class StudentShowWorkWindow extends CmWindow {

    StudentModelExt student;
    Integer runId;
    String programName;
    StudentActivityModel activityModel;

    public StudentShowWorkWindow(StudentModelExt student) {
        this(student, null);
    }
    
    public StudentShowWorkWindow(StudentModelExt student, StudentActivityModel activityModel) {
        setStyleName("student-show-work-window");
        this.student = student;
        this.activityModel = activityModel;
        setSize(760, 600);
        setResizable(false);

        setLayout(new BorderLayout());
        String title = "Show Work for " + student.getName();
        if(programName != null)
            title += " in program " + programName;
        setHeading(title);

        add(createWestPanel(), new BorderLayoutData(LayoutRegion.WEST, 200));
        add(createCenterPanel(), new BorderLayoutData(LayoutRegion.CENTER));

        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });

        addButton(closeBtn);

        setVisible(false);
    }

    LayoutContainer westContainer;

    /**
     * Create the west component to provide list of solutions with show work
     * 
     * @return
     */
    private Widget createWestPanel() {
        westContainer = new LayoutContainer();
        westContainer.setLayout(new BorderLayout());
        LayoutContainer head = new LayoutContainer();
        head.add(new Html("<div style='margin: 10px;'>Click on a date/time below to view the student show-work effort.</div>"));
        westContainer.add(head, new BorderLayoutData(LayoutRegion.NORTH, 50));

        getStudentShowWorkRPC();

        return westContainer;
    }

    LayoutContainer centerContainer = new LayoutContainer();

    private Widget createCenterPanel() {
        centerContainer.setLayout(new CenterLayout());
        String html = "<h1 style='color: blue;width: 200;'>Select an item from the list at left.</h1>";
        centerContainer.add(new Html(html));
        return centerContainer;
    }
    
    

    /**
     * Create the center, main container with the show work and solution loaded
     * for pid.
     * 
     * @param pid
     * @return
     */
    private Widget createCenterPanelForPid(String pid) {
        final LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new BorderLayout());
        try {
            // create temp user object to identify this student
            UserInfo user = new UserInfo(student.getUid(),activityModel.getTestId());
            user.setRunId(activityModel.getRunId());
            UserInfo.setInstance(user);
            
            // TODO: disable calculator per student's advanced options?
            //       probably not since these should be the options in place when
            //       whiteboard work was done
            user.setDisableCalcAlways(true);
            
            ShowWorkPanel workPanel = new ShowWorkPanel(null);
            BorderLayoutData ld = new BorderLayoutData(LayoutRegion.NORTH, 290);
            ld.setSplit(false);            
            lc.add(workPanel, ld);
            
            workPanel.setupForPid(pid);

            final InmhItemData solItem = new InmhItemData();
            solItem.setType("practice");
            solItem.setFile(pid);
            
            ResourceViewerFactory.ResourceViewerFactory_Client client = new ResourceViewerFactory.ResourceViewerFactory_Client() {
            	@Override
            	public void onSuccess(ResourceViewerFactory instance) {
            	    
            		try {
	                    CmResourcePanel viewer = instance.create(solItem);
	
	                    lc.add(viewer.getResourcePanel(), new BorderLayoutData(LayoutRegion.CENTER));
	                    centerContainer.removeAll();
	                    centerContainer.setLayout(new FitLayout());
	                    centerContainer.add(lc);
	                    
	                    layout();
            		}
            		catch(Exception e) {
            			CatchupMathTools.showAlert("Error loading resource: " + e.getLocalizedMessage());
            		}
            	}
            	
            	@Override
            	public void onUnavailable() {
            	    Window.alert("Resource unavailable");
            	}
            };
            ResourceViewerFactory.createAsync(client);
            
        } catch (Exception e) {
            CmLogger.error("Error creating Show Work panel for student: " + pid + "," + student.getUid(), e);
        }
        return lc;
    }

    private void createDataList(List<StudentShowWorkModel> showWork) {
        final ListView<StudentShowWorkModel> lv = new ListView<StudentShowWorkModel>();
        lv.setSimpleTemplate("<div>{view_time}&nbsp;&nbsp;&nbsp;&nbsp;{label}</div>");
        
        Listener<ComponentEvent> l = new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent ce) {
                String pid = lv.getSelectionModel().getSelectedItem().getPid(); 

                CmLogger.debug("StudentShoworkWindow: " + "Loading solution: " + pid);

                createCenterPanelForPid(pid);
                layout();
            }
        };

        ListStore<StudentShowWorkModel> store =new ListStore<StudentShowWorkModel>(); 
        lv.addListener(Events.OnClick, l);
        for (int i=0,t=showWork.size();i<t;i++) {
            StudentShowWorkModel sw = showWork.get(i);
            store.add(sw);
        }
        lv.setStore(store);


        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new FitLayout());
        add(lv);
        
        westContainer.add(lv, new BorderLayoutData(LayoutRegion.CENTER));
        westContainer.layout();
        
        setVisible(true);
    }

    protected void getStudentShowWorkRPC() {

        new RetryAction<CmList<StudentShowWorkModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CmLogger.debug("StudentShowWorkWindow: reading student show work list");
                GetStudentShowWorkAction action = new GetStudentShowWorkAction(student.getUid(), activityModel.getRunId());
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            public void oncapture(CmList<StudentShowWorkModel> list) {
                CmBusyManager.setBusy(false);
                if(list.size() == 0) {
                    CatchupMathTools.showAlert("Student has not entered any answers.");
                    close();
                }
                else {
                    createDataList(list);
                }
                CmLogger.debug("StudentShowWorkWindow: student show work read successfully");
            }

            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                super.onFailure(caught);
            }
        }.register();
    }
}



