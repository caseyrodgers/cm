package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.StudentShowWorkWindow;
import hotmath.gwt.shared.client.CmShared;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StudentGridPanel extends LayoutContainer implements CmAdminDataRefresher {

    static public StudentGridPanel instance;

    private ToolBar toolBar;

    // TODO: undo button?
    // private Button undoButton;

    FieldSet _gridContainer;
    Grid<StudentModel> _grid;
    CmAdminModel _cmAdminMdl;

    public StudentGridPanel(CmAdminModel cmAdminMdl) {
        this._cmAdminMdl = cmAdminMdl;
        final ListStore<StudentModel> store = new ListStore<StudentModel>();

        ColumnModel cm = defineColumns();
        _grid = defineGrid(store, cm);
        _grid.setStyleName("student-grid-panel-grid");

        _gridContainer = new FieldSet();
        _gridContainer.setStyleName("student-grid-panel-grid-container");
        _gridContainer.add(createToolbar());
        
        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new FitLayout());
        lc.add(_grid);
        _gridContainer.add(lc);

        add(_gridContainer);
        final Menu contextMenu = new Menu();
        Button loginAsUser = new Button("Login as User");
        loginAsUser.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                loginAsSelectedUser();
                contextMenu.hide();
            }
        });
        contextMenu.add(loginAsUser);
        
        
        Button showWork = new Button("Show Work");
        showWork.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showWorkDialog();
                contextMenu.hide();
            }
        });
        contextMenu.add(showWork);

        
        if(CmShared.getQueryParameter("debug") != null) {
            Button debugUser = new Button("Debug Info");
            debugUser.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    showDebugInfo();
                    contextMenu.hide();
                }
            });
            contextMenu.add(debugUser);
        }
        
        _grid.setContextMenu(contextMenu);

        instance = this;
        
        CmAdminDataReader.getInstance().addReader(this);
    }
    
    public void refreshData() {
        getStudentsRPC(_cmAdminMdl.getId(), _grid.getStore(), null);
    }

    /** Force a refresh
     * 
     * @TODO: Combine these into one request
     * 
     * @param uid  The uid to select, or null to select current row
     * 
     */
    public void refreshDataNow(Integer uid) {
        getStudentsRPC(this._cmAdminMdl.getId(), _grid.getStore(), uid);
    }

    
    /**
     * Log in as this user
     * 
     */
    private void loginAsSelectedUser() {
        StudentModel sm = _grid.getSelectionModel().getSelectedItem();
        if (sm == null)
            return;

        String url = "http://hotmath.kattare.com/cm_student/CatchupMath.html?debug=true&uid=" + sm.getUid();
        Window.open(url, "_blank", "location=1,menubar=1,resizable=1");
    }

    private void showWorkDialog() {
        StudentModel sm = _grid.getSelectionModel().getSelectedItem();
        if (sm == null)
            return;
        
        new StudentShowWorkWindow(sm);
    }
    
    private void showDebugInfo() {
        StudentModel sm = _grid.getSelectionModel().getSelectedItem();
        if (sm == null)
            return;
        
        CatchupMathAdmin.showAlert("UID: " + sm.getUid());
    }
    
    /**
     * Call when container is resized, allows dynamically resizing children to
     * fit entire size. Mainly used for the grid. It might be set to
     * position:absolute for this to work
     * 
     */
    int MAX_HEIGHT = 400;

    public void resizeChildren() {
        if(true)
            return;
        
        int HEADER_SIZE = 40;
        try {

            // no children
            if (getItemCount() == 0)
                return;

            layout(); // make sure form is ready

            for (int i = 0; i < getItemCount(); i++) {
                El el = getItem(i).el();

                // defer the setting of the header and footer
                // util after we have determined the size
                el.center(_gridContainer.getParent().getElement());
            }

            int cheight = Window.getClientHeight();
            _grid.setHeight((int) (cheight * .40));
            _gridContainer.setHeight(_grid.getHeight(false) + 100);
            layout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HorizontalPanel createToolbar() {
        HorizontalPanel toolbar = new HorizontalPanel();
        toolbar.setHorizontalAlign(HorizontalAlignment.CENTER);
        toolbar.setStyleName("student-grid-panel-toolbar");

        TableData tData = new TableData("140px", "35px");
        tData.setMargin(20);

        Button ti = registerStudentToolItem(_grid, _cmAdminMdl);
        toolbar.add(ti, tData);

        ti = editStudentToolItem(_grid, _cmAdminMdl);
        toolbar.add(ti, tData);

        ti = studentDetailsToolItem(_grid);
        toolbar.add(ti, tData);
        
        ti = showWorkToolItem(_grid,_cmAdminMdl);
        toolbar.add(ti, tData);


        ti = unregisterStudentToolItem(_grid);
        toolbar.add(ti, tData);

        return toolbar;
    }

    private Grid<StudentModel> defineGrid(final ListStore<StudentModel> store, ColumnModel cm) {
        final Grid<StudentModel> grid = new Grid<StudentModel>(store, cm);
        grid.setAutoExpandColumn("name");
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModel>>() {
            public void handleEvent(SelectionEvent<StudentModel> se) {

                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    new StudentDetailsWindow(se.getModel());
                }
            }
        });
        
        grid.setWidth("500px");
        grid.setHeight("300px");
        return grid;
    }

    private Button registerStudentToolItem(final Grid<StudentModel> grid, final CmAdminModel cmAdminMdl) {
        Button ti = new StudenPanelButton("Register Student");
        ti.setId("register-student-btn");
        ti.setToolTip("Register a new student");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new RegisterStudent(null, cmAdminMdl);
            }

        });
        return ti;
    }

    private Button editStudentToolItem(final Grid<StudentModel> grid, final CmAdminModel cmAdminMdl) {
        Button ti = new StudenPanelButton("Edit Student");
        ti.setToolTip("Edit the profile for the selected student.");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                GridSelectionModel<StudentModel> sel = grid.getSelectionModel();
                List<StudentModel> l = sel.getSelection();
                if (l.size() == 0) {
                    CatchupMathAdmin.showAlert("Please select a student.");
                } else {
                    StudentModel sm = l.get(0);
                    new RegisterStudent(sm, cmAdminMdl);
                }
                if (grid.getStore().getCount() > 0) {
                    ce.getComponent().enable();
                }
            }

        });
        return ti;
    }
    
    private Button showWorkToolItem(final Grid<StudentModel> grid, final CmAdminModel cmAdminMdl) {
        Button ti = new StudenPanelButton("Show Work");
        ti.setToolTip("Show all student's show work effort.");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                GridSelectionModel<StudentModel> sel = grid.getSelectionModel();
                List<StudentModel> l = sel.getSelection();
                if (l.size() == 0) {
                    CatchupMathAdmin.showAlert("Please select a student.");
                } else {
                    StudentModel sm = l.get(0);
                    new StudentShowWorkWindow(sm);
                }
                if (grid.getStore().getCount() > 0) {
                    ce.getComponent().enable();
                }
            }

        });
        return ti;
    }
    private Button studentDetailsToolItem(final Grid<StudentModel> grid) {
        Button ti = new StudenPanelButton("Student Detail History");
        ti.setToolTip("View details for the selected student.");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                GridSelectionModel<StudentModel> sel = grid.getSelectionModel();
                List<StudentModel> l = sel.getSelection();
                if (l.size() == 0) {
                    CatchupMathAdmin.showAlert("Please select a student.");
                } else {
                    new StudentDetailsWindow(l.get(0));
                }
            }

        });
        return ti;
    }

    public void enableToolBar() {
        for (Component ti : toolBar.getItems()) {
            ti.enable();
        }
    }

    private Button unregisterStudentToolItem(final Grid<StudentModel> grid) {
        Button ti = new StudenPanelButton("Unregister Student");
        ti.setToolTip("Unregister the selected student");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                GridSelectionModel<StudentModel> sel = grid.getSelectionModel();
                List<StudentModel> l = sel.getSelection();
                if (l.size() == 0) {
                    CatchupMathAdmin.showAlert("Please select a student.");
                } else {
                    final StudentModel sm = l.get(0);

                    String s = "Unregister " + sm.getName() + " ?";
                    MessageBox.confirm("Unregister Student?", s, new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            String btnText = be.getButtonClicked().getText();
                            if (btnText.equalsIgnoreCase("yes")) {
                                grid.getStore().remove(sm);
                                deactivateUserRPC(sm);
                            }
                        }
                    });
                }
                if (grid.getStore().getCount() == 0) {
                    ce.getComponent().disable();
                }
            }

        });
        return ti;
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Student");
        column.setWidth(120);
        column.setSortable(true);
        configs.add(column);

        ColumnConfig group = new ColumnConfig();
        group.setId(StudentModel.GROUP_KEY);
        group.setHeader("Group");
        group.setWidth(100);
        group.setSortable(true);
        configs.add(group);

        ColumnConfig prog = new ColumnConfig();
        prog.setId("program");
        prog.setHeader("Program");
        prog.setWidth(100);
        prog.setSortable(true);
        configs.add(prog);

        ColumnConfig status = new ColumnConfig();
        status.setId("status");
        status.setHeader("Status");
        status.setWidth(150);
        status.setSortable(true);
        configs.add(status);

        ColumnConfig lastQuiz = new ColumnConfig();
        lastQuiz.setId("last-quiz");
        lastQuiz.setHeader("Last Quiz");
        lastQuiz.setWidth(70);
        lastQuiz.setSortable(true);
        configs.add(lastQuiz);

        ColumnConfig lastLogin = new ColumnConfig();
        lastLogin.setId("last-login");
        lastLogin.setHeader("Last Login");
        lastLogin.setWidth(70);
        lastLogin.setSortable(true);
        configs.add(lastLogin);

        ColumnConfig usage = new ColumnConfig();
        usage.setId("total-usage");
        usage.setHeader("Usage");
        usage.setWidth(70);
        usage.setSortable(true);
        configs.add(usage);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    protected void getStudentsRPC(Integer uid, final ListStore<StudentModel> store,  final Integer uidToSelect) {
        
        Log.info("StudentGridPanel: reading students RPC");
        
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");

        s.getSummariesForActiveStudents(uid, new AsyncCallback<List<StudentModel>>() {

            public void onSuccess(List<StudentModel> result) {
                
                // try to reselect current selection
                int selectedUid=0;
                if(uidToSelect != null) {
                    selectedUid = uidToSelect;
                }
                else {
                    // used current selection
                    StudentModel sm = _grid.getSelectionModel().getSelectedItem();
                    selectedUid = (sm != null)?sm.getUid():0;
                }
                
                store.removeAll();
                store.add(result);
                
                if(selectedUid > 0) {
                    for(int i=0;i<store.getCount();i++) {
                        if(store.getAt(i).getUid() == selectedUid) {
                            _grid.getSelectionModel().select(store.getAt(i),false);    
                            break;
                        }
                    }
                }
                    
                Log.info("StudentGridPanel: students RPC successfully read");
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                caught.printStackTrace();   // quite
                // CatchupMathAdmin.showAlert(msg);
            }
        });
    }

    protected void deactivateUserRPC(StudentModel sm) {
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");

        s.deactivateUser(sm, new AsyncCallback<StudentModel>() {

            public void onSuccess(StudentModel ai) {
                CmAdminDataReader.getInstance().fireRefreshData();
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }
    
    protected void removeUserRPC(StudentModel sm) {
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");

        s.removeUser(sm, new AsyncCallback<StudentModel>() {

            public void onSuccess(StudentModel ai) {
                CmAdminDataReader.getInstance().fireRefreshData();
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }

}

class StudenPanelButton extends Button {
    public StudenPanelButton(String name) {
        super(name);
        addStyleName("student-grid-panel-button");
    }
}
