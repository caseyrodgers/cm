package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewer;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
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

    StudentModel student;
    Integer runId;
    String programName;
    StudentActivityModel activityModel;

    public StudentShowWorkWindow(StudentModel student) {
        this(student, null);
    }
    
    public StudentShowWorkWindow(StudentModel student, StudentActivityModel activityModel) {
        setStyleName("student-show-work-window");
        this.student = student;
        this.activityModel = activityModel;
        setSize(770, 600);
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

        setVisible(true);
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
        String html = "<h1 style='color: blue;width: 200;'>No problem to show</h1>";
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
        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new BorderLayout());
        try {
            // create temp user object to identify this student
            UserInfo user = new UserInfo(student.getUid(), 0);
            UserInfo.setInstance(user);

            
            ShowWorkPanel workPanel = new ShowWorkPanel();
            BorderLayoutData ld = new BorderLayoutData(LayoutRegion.NORTH, 360);
            ld.setSplit(false);            
            lc.add(workPanel, ld);
            
            workPanel.setupForPid(pid);

            InmhItemData solItem = new InmhItemData();
            solItem.setType("practice");
            solItem.setFile(pid);
            ResourceViewer viewer = ResourceViewerFactory.create(solItem.getType());

            lc.add(viewer.getResourcePanel(solItem), new BorderLayoutData(LayoutRegion.CENTER));
            centerContainer.removeAll();
            centerContainer.setLayout(new FitLayout());
            centerContainer.add(lc);
            
        } catch (Exception e) {
            Log.error("Error creating Show Work panel for student: " + pid + "," + student.getUid(), e);
        }
        return lc;
    }

    private void createDataList(List<StudentShowWorkModel> showWork) {
        final ListView<StudentShowWorkModel> lv = new ListView<StudentShowWorkModel>();
        lv.setSimpleTemplate("<div>{view_time}&nbsp;&nbsp;&nbsp;&nbsp;{label}</div>");
        
        Listener<ComponentEvent> l = new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent ce) {
                String pid = lv.getSelectionModel().getSelectedItem().getPid(); 

                Log.debug("StudentShoworkWindow: " + "Loading solution: " + pid);

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

        

        westContainer.add(lv, new BorderLayoutData(LayoutRegion.CENTER));
        westContainer.layout();
    }

    protected void getStudentShowWorkRPC() {

        Log.debug("StudentShowWorkWindow: reading student show work list");
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
        s.getStudentShowWork(student.getUid(), runId,new AsyncCallback<List<StudentShowWorkModel>>() {

            public void onSuccess(List<StudentShowWorkModel> list) {
                createDataList(list);

                Log.debug("StudentShowWorkWindow: student show work read successfully");
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }
}
