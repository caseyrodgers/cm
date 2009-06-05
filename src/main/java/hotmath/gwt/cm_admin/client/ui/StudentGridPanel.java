package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.CmAdminModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;

import java.util.ArrayList;
import java.util.List;

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
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

//import hotmath.testset.ha.HaUser;

public class StudentGridPanel extends LayoutContainer {

    static public StudentGridPanel instance;

    private StudentDetailsPanel detailsPanel;
    private ToolBar toolBar;

    // TODO: undo button?
    // private Button undoButton;

    FieldSet _gridContainer;
    Grid<StudentModel> _grid;
    CmAdminModel _cmAdminMdl;

    public StudentGridPanel(CmAdminModel cmAdminMdl) {
        this._cmAdminMdl = cmAdminMdl;
        final ListStore<StudentModel> store = new ListStore<StudentModel>();
        
        
        final int uid = cmAdminMdl.getId();
        
        getStudentsRPC(uid, store,null);
        
        // refresh the data every 10 seconds
        Timer t = new Timer() {
            public void run() {
                getStudentsRPC(uid, store,null);
            }
          };
        t.scheduleRepeating(1000 * 60);   

        ColumnModel cm = defineColumns();

        _grid = defineGrid(store, cm);
        _grid.setStyleName("student-grid-panel-grid");

        getAccountInfoRPC(cmAdminMdl.getId());
        // refresh the data every 10 seconds
        Timer t2 = new Timer() {
            public void run() {
                getAccountInfoRPC(uid);
            }
          };
        t2.scheduleRepeating(1000 * 30);        

        _gridContainer = new FieldSet();
        _gridContainer.setStyleName("student-grid-panel-grid-container");
        _gridContainer.add(createToolbar());
        _gridContainer.add(_grid);

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
        _grid.setContextMenu(contextMenu);

        instance = this;
    }
    
    /** Force a refresh */
    public void refreshDataNow(Integer uid) {
        getStudentsRPC(this._cmAdminMdl.getId(), _grid.getStore(), uid);
        getAccountInfoRPC(this._cmAdminMdl.getId());
    }

    /**
     * Double clicked on row
     * 
     */
    private void loginAsSelectedUser() {
        StudentModel sm = _grid.getSelectionModel().getSelectedItem();
        if (sm == null)
            return;

        String url = "http://hotmath.kattare.com/cm_student/CatchupMath.html?uid=" + sm.getUid();
        Window.open(url, "_blank", "location=1,menubar=1,resizable=1");
    }

    /**
     * Call when container is resized, allows dynamically resizing children to
     * fit entire size. Mainly used for the grid. It might be set to
     * position:absolute for this to work
     * 
     */
    int MAX_HEIGHT = 400;

    public void resizeChildren() {
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

        TableData tData = new TableData("150px", "30px");
        tData.setMargin(20);

        Button ti = registerStudentToolItem(_grid, _cmAdminMdl);
        toolbar.add(ti, tData);

        ti = editStudentToolItem(_grid, _cmAdminMdl);
        toolbar.add(ti, tData);

        ti = studentDetailsToolItem(_grid);
        toolbar.add(ti, tData);

        ti = unregisterStudentToolItem(_grid);
        toolbar.add(ti, tData);

        return toolbar;
    }

    private ToolBar buildToolbar() {
        toolBar = new ToolBar();
        toolBar.setStyleName("student-grid-panel-toolbar");

        Button ti = studentDetailsToolItem(_grid);
        toolBar.add(ti);
        toolBar.add(new SeparatorToolItem());

        ti = editStudentToolItem(_grid, _cmAdminMdl);
        toolBar.add(ti);
        toolBar.add(new SeparatorToolItem());

        ti = registerStudentToolItem(_grid, _cmAdminMdl);
        toolBar.add(ti);
        toolBar.add(new SeparatorToolItem());

        toolBar.add(new FillToolItem());
        toolBar.add(new SeparatorToolItem());
        ti = unregisterStudentToolItem(_grid);
        toolBar.add(ti);
        return toolBar;
    }

    private Grid<StudentModel> defineGrid(final ListStore<StudentModel> store, ColumnModel cm) {
        final Grid<StudentModel> grid = new Grid<StudentModel>(store, cm);
        grid.setAutoExpandColumn("name");
        grid.setBorders(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModel>>() {
            public void handleEvent(SelectionEvent<StudentModel> se) {

                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    new StudentDetailsWindow(se.getModel());
                }
            }
        });
        return grid;
    }

    private Button registerStudentToolItem(final Grid<StudentModel> grid, final CmAdminModel cmAdminMdl) {
        Button ti = new StudenPanelButton("Register Student");
        ti.setToolTip("Register a new student");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new RegisterStudent(grid, null, cmAdminMdl);
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
                    new RegisterStudent(grid, sm, cmAdminMdl);
                }
                if (grid.getStore().getCount() > 0) {
                    ce.getComponent().enable();
                }
            }

        });
        return ti;
    }

    private Button studentDetailsToolItem(final Grid<StudentModel> grid) {
        Button ti = new StudenPanelButton("Student Details");
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
                    
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }

    protected void getAccountInfoRPC(Integer uid) {
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");

        s.getAccountInfoForAdminUid(uid, new AsyncCallback<AccountInfoModel>() {

            public void onSuccess(AccountInfoModel ai) {
                StringBuilder sb = new StringBuilder();
                sb.append("Manage ").append(ai.getSchoolName()).append(" Students");
                _gridContainer.setHeading(sb.toString());
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }

    protected void deactivateUserRPC(StudentModel sm) {
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");

        s.deactivateUser(sm, new AsyncCallback<StudentModel>() {

            public void onSuccess(StudentModel ai) {
                // empty
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }

    public StudentDetailsPanel getDetailsPanel() {
        return detailsPanel;
    }

    public void setDetailsPanel(StudentDetailsPanel detailsPanel) {
        this.detailsPanel = detailsPanel;
    }
}

class StudenPanelButton extends Button {
    public StudenPanelButton(String name) {
        super(name);
        addStyleName("student-grid-panel-button");
    }
}
