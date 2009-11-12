package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.AutoRegisterStudentSetup;
import hotmath.gwt.cm_tools.client.ui.BulkStudentRegistrationWindow;
import hotmath.gwt.cm_tools.client.ui.GroupSelectorWidget;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.UnregisterStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreFilter;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;

public class StudentGridPanel extends LayoutContainer implements CmAdminDataRefresher, ProcessTracker {

    static public StudentGridPanel instance;

    private ToolBar toolBar;

    Grid<StudentModel> _grid;
    CmAdminModel _cmAdminMdl;
    ListStore<StudentModel> smStore;

    private ListStore<GroupModel> groupStore;
    private ComboBox<GroupModel> groupCombo;

    public StudentGridPanel(CmAdminModel cmAdminMdl) {
        this._cmAdminMdl = cmAdminMdl;
        final ListStore<StudentModel> store = new ListStore<StudentModel>();

        setLayout(new BorderLayout());

        ColumnModel cm = defineColumns();
        _grid = defineGrid(store, cm);
        _grid.setStyleName("student-grid-panel-grid");

        add(createToolbar(), new BorderLayoutData(LayoutRegion.NORTH, 30));
        add(_grid, new BorderLayoutData(LayoutRegion.CENTER, 400));

        BorderLayoutData borderLayout = new BorderLayoutData(LayoutRegion.SOUTH, 35);
        add(createGroupFilter(), borderLayout);

        final Menu contextMenu = new Menu();

        MenuItem editUser = new MenuItem("Edit Student");
        editUser.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                editStudent();
                contextMenu.hide();
            }
        });
        contextMenu.add(editUser);

        if (CmShared.getQueryParameter("debug") != null) {
            MenuItem loginAsUser = new MenuItem("Login as User");
            loginAsUser.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    loginAsSelectedUser();
                    contextMenu.hide();
                }
            });
            contextMenu.add(loginAsUser);

            MenuItem debugUser = new MenuItem("Debug Info");
            debugUser.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    showDebugInfo();
                    contextMenu.hide();
                }
            });
            contextMenu.add(debugUser);
        }

        _grid.setContextMenu(contextMenu);

        instance = this;

        CmAdminDataReader.getInstance().addReader(this);

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if (event.getEventName().equals(EventBus.EVENT_TYPE_REFRESH_STUDENT_DATA)) {
                    refreshDataNow(null);
                }
            }
        });
    }

    public void refreshData() {
        getStudentsRPC(_cmAdminMdl.getId(), _grid.getStore(), null);
    }

    /**
     * Force a refresh
     * 
     * @TODO: Combine these into one request
     * 
     * @param uid
     *            The uid to select, or null to select current row
     * 
     */
    public void refreshDataNow(Integer uid2Select) {
        getStudentsRPC(this._cmAdminMdl.getId(), _grid.getStore(), uid2Select);
    }

    public void enableToolBar() {
        for (Component ti : toolBar.getItems()) {
            ti.enable();
        }
    }

    private Component createGroupFilter() {

        groupStore = new ListStore<GroupModel>();
        GroupSelectorWidget gsw = new GroupSelectorWidget(_cmAdminMdl, groupStore, false, this, "group-filter");
        groupCombo = gsw.groupCombo();
        groupCombo.setAllowBlank(true);

        groupCombo.addSelectionChangedListener(new SelectionChangedListener<GroupModel>() {
            public void selectionChanged(SelectionChangedEvent<GroupModel> se) {

                // filter grid based on current selection
                GroupModel gm = se.getSelectedItem();
                String groupName = gm.getName();

                StudentModelGroupFilter smgf = new StudentModelGroupFilter();
                _grid.getStore().addFilter(smgf);
                _grid.getStore().applyFilters(groupName);
            }
        });

        
        FormPanel fp = new FormPanel();
        fp.setHeaderVisible(false); 
        fp.setLabelWidth(40);
        fp.setBorders(false);
        fp.setFrame(false);
        fp.setFooter(false);
        fp.setBodyBorder(false);
        
        fp.add(groupCombo);
        return fp;
    }

    private Button manageGroupButton(final Grid<StudentModel> grid) {
        final Button btn = new StudenPanelButton("Manage Groups");
        btn.setToolTip("Manage the group definitions.");

        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new ManageGroupsWindow(_cmAdminMdl).setVisible(true);
            }
        });
        return btn;
    }

    /**
     * Log in as selected user (student)
     * 
     * This is this is not the same thing as CM_HOME_URL for example, on the
     * test server:
     * 
     * from hotmath.kattare.com:8081 (for admin) to hotmath.kattare.com (for
     * student) and logout of student (at hotmath.kattare.com) and back to
     * hotmath.kattare.com:8081
     * 
     * for live server, it is:
     * 
     * catchupmath.com (admin) -> hotmath.com (student) and logout of student
     * must go back to catchupmath.comd
     * 
     * I'm for generalizing this, we must deal with all cases.
     * 
     */
    private void loginAsSelectedUser() {
        StudentModel sm = _grid.getSelectionModel().getSelectedItem();
        if (sm == null)
            return;

        String server = CmShared.getQueryParameter("host");
        if (server == null || server.length() == 0)
            server = "hotmath.com";

        String url = "http://" + server + "/cm_student/CatchupMath.html?uid=" + sm.getUid() + "&debug=true";
        Window.open(url, "_blank", "location=1,menubar=1,resizable=1");
    }

    private void showDebugInfo() {
        StudentModel sm = _grid.getSelectionModel().getSelectedItem();
        if (sm == null)
            return;

        CatchupMathTools.showAlert("UID: " + sm.getUid());
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();
        toolbar.addStyleName("student-grid-panel-toolbar");
        toolbar.add(createRefreshButton());
        toolbar.add(createRegistrationButton());
        toolbar.add(editStudentToolItem(_grid, _cmAdminMdl));
        toolbar.add(studentDetailsToolItem(_grid));
        toolbar.add(manageGroupButton(_grid));
        toolbar.add(new FillToolItem());
        toolbar.add(displayPrintableReportToolItem(_grid));

        return toolbar;
    }
    
    private Button createRegistrationButton() {
        Button btn = new StudenPanelButton("Student Registration");
        btn.setToolTip("Register students with Catchup Math");
        Menu menu = new Menu();
        menu.add(new MyMenuItem("New Student", "Create a new single student registration.",new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                new RegisterStudent(null, _cmAdminMdl).showWindow();
            }
        }));
        menu.add(new MyMenuItem("Unregister Student", "Unregister the selected student.",new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                GridSelectionModel<StudentModel> sel = _grid.getSelectionModel();
                List<StudentModel> l = sel.getSelection();
                if (l.size() == 0) {
                    CatchupMathTools.showAlert("Please select a student.");
                } else {
                    final StudentModel sm = l.get(0);

                    String s = "Unregister " + sm.getName() + " ?";
                    MessageBox.confirm("Unregister Student", s, new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            String btnText = be.getButtonClicked().getText();
                            if (btnText.equalsIgnoreCase("yes")) {
                                _grid.getStore().remove(sm);
                                List<StudentModel> list = new ArrayList<StudentModel>();
                                list.add(sm);
                                unregisterStudentsRPC(list);
                            }
                        }
                    });
                }
                if (_grid.getStore().getCount() == 0) {
                    // ce.getComponent().disable();
                }                
            }
        }));
        btn.setMenu(menu);
        menu.add(new MyMenuItem("Bulk Registration", "Bulk student registration.",new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                new BulkStudentRegistrationWindow(null, _cmAdminMdl);
            }
        }));
        menu.add(new MyMenuItem("Self Registration", "Define a Self Registration group.", new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                new AutoRegisterStudentSetup(null, _cmAdminMdl);
            }
        }));
        btn.setMenu(menu);
        
        return btn;
    }
    
    static class MyMenuItem extends MenuItem {
        public MyMenuItem(String test, String tip, SelectionListener listener) {
            super(test, listener);
            setToolTip(tip);
        }
    }
    
    
    private Button createRefreshButton() {
        Button btn = new StudenPanelButton("Refresh List");
        btn.setToolTip("Refresh Student List with latest information.");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CmAdminDataReader.getInstance().fireRefreshData();
            }
        });
        return btn;
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

    private Button editStudentToolItem(final Grid<StudentModel> grid, final CmAdminModel cmAdminMdl) {
        Button ti = new StudenPanelButton("Edit Student");
        ti.setToolTip("Edit the profile for the selected student.");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                editStudent();
                if (grid.getStore().getCount() > 0) {
                    // ce.getComponent().enable();
                }
            }

        });
        return ti;
    }

    private void editStudent() {
        GridSelectionModel<StudentModel> sel = _grid.getSelectionModel();
        List<StudentModel> l = sel.getSelection();
        if (l.size() == 0) {
            CatchupMathTools.showAlert("Please select a student.");
        } else {
            StudentModel sm = l.get(0);
            new RegisterStudent(sm, _cmAdminMdl).showWindow();
        }

    }

    private Button studentDetailsToolItem(final Grid<StudentModel> grid) {
        Button ti = new StudenPanelButton("Student Detail History");
        ti.setToolTip("View details for the selected student.");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                GridSelectionModel<StudentModel> sel = grid.getSelectionModel();
                List<StudentModel> l = sel.getSelection();
                if (l.size() == 0) {
                    CatchupMathTools.showAlert("Please select a student.");
                } else {
                    new StudentDetailsWindow(l.get(0));
                }
            }

        });
        return ti;
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
                    CatchupMathTools.showAlert("Please select a student.");
                } else {
                    final StudentModel sm = l.get(0);

                    String s = "Unregister " + sm.getName() + " ?";
                    MessageBox.confirm("Unregister Student", s, new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            String btnText = be.getButtonClicked().getText();
                            if (btnText.equalsIgnoreCase("yes")) {
                                grid.getStore().remove(sm);
                                List<StudentModel> list = new ArrayList<StudentModel>();
                                list.add(sm);
                                unregisterStudentsRPC(list);
                            }
                        }
                    });
                }
                if (grid.getStore().getCount() == 0) {
                    // ce.getComponent().disable();
                }
            }

        });
        return ti;
    }

    private Button displayPrintableReportToolItem(final Grid<StudentModel> grid) {
        Button ti = new Button();
        ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable student summary report");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ListStore<StudentModel> store = grid.getStore();

                List<Integer> studentUids = new ArrayList<Integer>();
                for (int i = 0; i < store.getCount(); i++) {
                    studentUids.add(store.getAt(i).getUid());

                }
                new PdfWindow(_cmAdminMdl.getId(), "Catchup Math Student Summary Report", new GeneratePdfAction(
                        PdfType.STUDENT_SUMMARY, _cmAdminMdl.getId(), studentUids));

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

    protected void getStudentsRPC(Integer uid, final ListStore<StudentModel> store, final Integer uidToSelect) {

        Log.info("StudentGridPanel: reading students RPC");

        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");

        s.getSummariesForActiveStudents(uid, new AsyncCallback<List<StudentModel>>() {

            public void onSuccess(List<StudentModel> result) {

                /** save the current selection, if any */
                int selectedUid = 0;
                if (uidToSelect != null) {
                    selectedUid = uidToSelect;
                } else {
                    // used current selection
                    StudentModel sm = _grid.getSelectionModel().getSelectedItem();
                    selectedUid = (sm != null) ? sm.getUid() : 0;
                }

                /**
                 * remove all existing records, and add new set
                 * 
                 * NOTE: changed this to add one by one due to bug with
                 * add(list) when filter applied
                 * 
                 * */
                store.removeAll();
                if (store.getFilters() != null) {
                    /**
                     * if filter applied, then must do each one to avoid GXT bug
                     * of throwning out of bounds
                     * 
                     * @TODO: recheck this condition on next GXT build
                     */
                    for (StudentModel s : result) {
                        store.add(s);
                    }
                } else {
                    /** if not filter, this is much faster */
                    store.add(result);
                }

                /** Reselect selected row */
                if (selectedUid > 0) {
                    for (int i = 0; i < store.getCount(); i++) {
                        if (store.getAt(i).getUid() == selectedUid) {
                            _grid.getSelectionModel().select(store.getAt(i), false);

                            /**
                             * Must set in separate thread for this to work due
                             * to GXT bug in setting the selected row on
                             * layout() which overrides this action. This is the
                             * only way I could get the currently selected row
                             * re-selected on refresh.
                             * 
                             * @TODO: recheck this condition on next GXT build.
                             */
                            final int visRow = i;
                            new Timer() {
                                public void run() {
                                    _grid.getView().ensureVisible(visRow, 0, true);
                                }
                            }.schedule(1);

                            break;
                        }
                    }
                }

                Log.info("StudentGridPanel: students RPC successfully read");
            }

            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });
    }

    protected void unregisterStudentsRPC(List<StudentModel> smList) {
        CmServiceAsync cms = (CmServiceAsync) Registry.get("cmService");

        cms.execute(new UnregisterStudentsAction(smList), new AsyncCallback<StringHolder>() {
            public void onSuccess(final StringHolder result) {
                String response = result.getResponse();
                StringBuffer sb = new StringBuffer();

                JSONValue rspValue = JSONParser.parse(response);
                JSONObject rspObj = rspValue.isObject();

                String value = rspObj.get("deactivateCount").isString().stringValue();
                int deactivateCount = Integer.valueOf(value);
                value = rspObj.get("deactivateErrorCount").isString().stringValue();
                int deactivateErrorCount = Integer.valueOf(value);
                value = rspObj.get("removeCount").isString().stringValue();
                int removeCount = Integer.valueOf(value);
                value = rspObj.get("removeErrorCount").isString().stringValue();
                int removeErrorCount = Integer.valueOf(value);

                int unregisterCount = deactivateCount + removeCount;
                int unregisterErrorCount = deactivateErrorCount + removeErrorCount;

                if (unregisterCount > 0) {
                    sb.append("Unregistered ").append(unregisterCount);
                    sb.append((unregisterCount > 1) ? " students." : " student.");
                    if (unregisterErrorCount > 0)
                        sb.append(" <br/>");
                }
                if (unregisterErrorCount > 0) {
                    sb.append("Unregister failed for ").append(unregisterErrorCount);
                    sb.append((unregisterErrorCount > 1) ? " students." : " student.");
                }

                CatchupMathTools.showAlert(sb.toString());
                AccountInfoPanel aip = CatchupMathAdmin.getInstance().getAccountInfoPanel();
                aip.refreshData();

            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }

    // @Override
    public void beginStep() {
        // empty impl
    }

    // @Override
    public void completeStep() {
        // empty impl
    }

    // @Override
    public void finish() {
        // empty impl
    }
}

class StudenPanelButton extends Button {
    public StudenPanelButton(String name) {
        super(name);
        addStyleName("student-grid-panel-button");
        setWidth(115);
    }
}

class StudentModelGroupFilter implements StoreFilter<StudentModel> {

    // @Override
    public boolean select(Store<StudentModel> store, StudentModel parent, StudentModel item, String property) {
        if (GroupSelectorWidget.NO_FILTERING.equals(property))
            return true;
        return (property.equals(item.getGroup()));
    }
}
