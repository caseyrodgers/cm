package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewer;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerFactory;
import hotmath.gwt.cm_tools.client.ui.viewer.ShowWorkPanel;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.DataList;
import com.extjs.gxt.ui.client.widget.DataListItem;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
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
public class StudentShowWorkWindow extends Window {

    StudentModel student;

    public StudentShowWorkWindow(StudentModel student) {
        this.student = student;
        setSize(770, 530);
        setResizable(true);

        setLayout(new BorderLayout());
        setHeading("Show Work for " + student.getName());

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
        head.add(new Html("<div style='margin: 10px;'>Click on a problem below to view the associated work</div>"));
        westContainer.add(head, new BorderLayoutData(LayoutRegion.NORTH, 50));

        getStudentShowWorkRPC();

        return westContainer;
    }

    LayoutContainer centerContainer = new LayoutContainer();

    private Widget createCenterPanel() {
        centerContainer.setLayout(new FitLayout());
        String html = "<h1 style='width: 200;margin: auto;'>No problem to show</h1>";
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

        BorderLayoutData ld = new BorderLayoutData(LayoutRegion.NORTH, 300);
        ld.setSplit(true);

        try {

            // create temp user object to identify this student
            UserInfo user = new UserInfo(student.getUid(), 0);
            UserInfo.setInstance(user);

            centerContainer.removeAll();
            centerContainer.setLayout(new BorderLayout());

            ShowWorkPanel workPanel = new ShowWorkPanel();
            centerContainer.add(workPanel, ld);

            workPanel.setupForPid(pid);

            InmhItemData solItem = new InmhItemData();
            solItem.setType("practice");
            solItem.setFile(pid);
            ResourceViewer viewer = ResourceViewerFactory.create(solItem.getType());

            centerContainer.add(viewer.getResourcePanel(solItem), new BorderLayoutData(LayoutRegion.CENTER));
            
            centerContainer.layout();
        } catch (Exception e) {
            Log.error("Error creating Show Work panel for student: " + pid + "," + student.getUid(), e);
        }
        setVisible(true);

        return lc;

    }

    private void createDataList(List<StudentShowWorkModel> showWork) {
        final DataList list2 = new DataList();
        list2.setFlatStyle(true);

        Listener<ComponentEvent> l = new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent ce) {
                DataList l = (DataList) ce.getComponent();
                String pid = l.getSelectedItem().getData("pid");

                Log.info("StudentShoworkWindow: " + "Loading solution: " + pid);

                createCenterPanelForPid(pid);
            }
        };

        list2.addListener(Events.OnClick, l);
        for (StudentShowWorkModel work : showWork) {
            DataListItem item = new DataListItem();
            item.setText(work.getLabel() + " " + work.getViewTime());
            item.setData("pid", work.getPid());
            list2.add(item);
        }

        westContainer.add(list2, new BorderLayoutData(LayoutRegion.CENTER));
        westContainer.layout();
    }

    protected void getStudentShowWorkRPC() {

        Log.info("StudentShowWorkWindow: reading student show work list");
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
        s.getStudentShowWork(student.getUid(), new AsyncCallback<List<StudentShowWorkModel>>() {

            public void onSuccess(List<StudentShowWorkModel> list) {
                createDataList(list);

                Log.info("StudentShowWorkWindow: student show work read successfully");
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }
}
