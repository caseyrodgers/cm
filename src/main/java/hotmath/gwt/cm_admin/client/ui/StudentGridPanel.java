package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.ui.DateRangePickerDialog.FilterOptions;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.AutoRegisterStudentSetup;
import hotmath.gwt.cm_tools.client.ui.BulkStudentRegistrationWindow;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.ExportStudentData;
import hotmath.gwt.cm_tools.client.ui.GroupSelectorWidget;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.RegisterStudentProto;
import hotmath.gwt.cm_tools.client.ui.RegisterStudentProto2;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageExtendedAction;
import hotmath.gwt.shared.client.rpc.action.ResetUserAction;
import hotmath.gwt.shared.client.rpc.action.UnregisterStudentsAction;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StudentGridPanel extends LayoutContainer implements CmAdminDataRefresher, ProcessTracker {
    static public StudentGridPanel instance;

    private ToolBar toolBar;

    Grid<StudentModelExt> _grid;
    CmAdminModel _cmAdminMdl;

    private ListStore<GroupInfoModel> groupStore;
    private ComboBox<GroupInfoModel> groupCombo;

    private Integer _groupFilterId;
    private String _quickSearch;
    private boolean _forceServerRefresh = true;

    final PagingLoader<PagingLoadResult<StudentModelExt>> _studentLoader;
    final PagingToolBar _pagingToolBar;

    final int MAX_ROWS_PER_PAGE = 50;

    public StudentGridPanel(CmAdminModel cmAdminMdl) {
        this._cmAdminMdl = cmAdminMdl;

        setLayout(new BorderLayout());

        ColumnModel cm = defineColumns();

        /**
         * Create proxy to handle the paged RPC calls
         * 
         */
        StudentGridRpcProxy rpcProxy = new StudentGridRpcProxy();

        /**
         * Create a loader to do the actual loading
         * 
         */
        // loader
        _studentLoader = new BasePagingLoader<PagingLoadResult<StudentModelExt>>(rpcProxy);
        _studentLoader.setRemoteSort(true);

        _studentLoader.addLoadListener(new StudentLoadListener());

        final ListStore<StudentModelExt> store = new ListStore<StudentModelExt>(_studentLoader);

        _grid = defineGrid(store, cm);
        _grid.addListener(Events.Attach, new Listener<GridEvent<StudentModelExt>>() {
            public void handleEvent(GridEvent<StudentModelExt> be) {

                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);

                /** set rows per page */
                config.setLimit(MAX_ROWS_PER_PAGE);

                Map<String, Object> state = getState();
                if (state.containsKey("offset")) {
                    int offset = (Integer) state.get("offset");
                    int limit = (Integer) state.get("limit");
                    config.setOffset(offset);
                    config.setLimit(limit);
                }

                if (state.containsKey("sortField")) {
                    config.setSortField((String) state.get("sortField"));
                    config.setSortDir(SortDir.valueOf((String) state.get("sortDir")));
                }
                _studentLoader.load(config);
            }
        });

        _grid.setStyleName("student-grid-panel-grid");

        add(createToolbar(), new BorderLayoutData(LayoutRegion.NORTH, 30));

        LayoutContainer lc = new LayoutContainer(new BorderLayout());
        _pagingToolBar = new PagingToolBar(MAX_ROWS_PER_PAGE);
        _pagingToolBar.bind(_studentLoader);
        lc.add(_pagingToolBar, new BorderLayoutData(LayoutRegion.SOUTH, 30));
        lc.add(_grid, new BorderLayoutData(LayoutRegion.CENTER, 400));

        add(lc, new BorderLayoutData(LayoutRegion.CENTER));

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
        
        MenuItem teacherMode = new MenuItem("Teacher Mode Login");
        teacherMode.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                loginAsSelectedUser(true);
                contextMenu.hide();
            }
        });
        contextMenu.add(teacherMode);

        if (CmShared.getQueryParameter("debug") != null) {
            MenuItem studentDetails = new MenuItem("Student Details");
            studentDetails.addSelectionListener(new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent ce) {
                    showStudentDetails(getSelectedStudent());
                }
            });

            contextMenu.add(studentDetails);
            MenuItem loginAsUser = new MenuItem("Login as User");
            loginAsUser.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    loginAsSelectedUser(false);
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

            MenuItem removeUser = new MenuItem("Unregister Student");
            removeUser.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    unregisterStudentsRPC(Arrays.asList((StudentModelI) getSelectedStudent()));
                    contextMenu.hide();
                }
            });
            contextMenu.add(removeUser);

            MenuItem clientTests = new MenuItem("Launch Client Test");
            clientTests.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    launchClientTest();
                    contextMenu.hide();
                }
            });
            contextMenu.add(clientTests);
            
            MenuItem resetUser = new MenuItem("Reset User");
            resetUser.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    MessageBox.confirm("Reset User", "Reset user to beginning of current program?",new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if(be.getButtonClicked().getText().equals("Yes")) {
                                int uid = _grid.getSelectionModel().getSelectedItem().getUid();
                                resetProgramForUser(uid);
                            }
                        }
                    });
                }
            });
            contextMenu.add(resetUser);
        }

        _grid.setContextMenu(contextMenu);

        instance = this;

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_REFRESH_STUDENT_DATA) {
                    CmAdminDataReader.getInstance().fireRefreshData();
                }
            }
        });
        CmAdminDataReader.getInstance().addReader(this);

        if (CmShared.getQueryParameter("show_quiz") != null) {
            new CustomProgramAddQuizDialog(null, null, false);
        }
    }
    
    private void resetProgramForUser(final int uid) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                ResetUserAction action = new ResetUserAction(uid, 0);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData result) {
                CmBusyManager.setBusy(false);
                if(!result.getDataAsString("status").equals("OK")) {
                    CatchupMathTools.showAlert("Error resetting user: " + result);
                }
                else {
                    InfoPopupBox.display("Reset User", "User '" + uid + "' reset successfully");
                   refreshDataNow(uid);
                }
            }
        }.register();
    }

    public void refreshData() {
        getStudentsRPC(_cmAdminMdl.getId(), _grid.getStore(), null);
    }

    /**
     * Launch each client into auto-test. At most launch one page.
     * 
     */
    private void launchClientTest() {
        GWT.runAsync(new CmRunAsyncCallback() {

            @Override
            public void onSuccess() {
                StudentModelExt student = _grid.getSelectionModel().getSelectedItem();
                if (student == null)
                    return;
                if (Window.confirm("This will launch the selected client in auto-test model.  Are you sure?")) {
                    String url = "/loginService?debug=true&type=auto_test&test_rpp_only=true&uid=" + student.getUid();
                    Window.open(url, "_blank", "height=480,width=640,status=yes,scrollbars=1");
                }
            }
        });
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

        groupStore = new ListStore<GroupInfoModel>();
        GroupSelectorWidget gsw = new GroupSelectorWidget(_cmAdminMdl, groupStore, false, this, "group-filter", false);
        groupCombo = gsw.groupCombo();
        groupCombo.setAllowBlank(true);

        groupCombo.addSelectionChangedListener(new SelectionChangedListener<GroupInfoModel>() {
            public void selectionChanged(SelectionChangedEvent<GroupInfoModel> se) {

                // filter grid based on current selection
                GroupInfoModel gm = se.getSelectedItem();
                _groupFilterId = gm.getId();

                loadAndResetStudentLoader();
            }
        });

        LayoutContainer lc = new HorizontalPanel();

        class MyFormPanel extends FormPanel {
            MyFormPanel() {
                setHeaderVisible(false);
                setLabelWidth(40);
                setBorders(false);
                setFrame(false);
                setFooter(false);
                setBodyBorder(false);
                setBorders(false);
                setWidth(300);
            }
        }
        ;

        FormPanel fp = new MyFormPanel();
        fp.add(groupCombo);
        lc.add(fp);

        fp = new MyFormPanel();
        fp.setWidth(275);
        fp.setLabelWidth(80);
        fp.add(new QuickSearchPanel());
        lc.add(fp);

        // Date Range Panel
        fp = new MyFormPanel();
        fp.setWidth(300);
        fp.setLabelWidth(80);
        fp.add(new DateRangePanel());
        lc.add(fp);

        return lc;
    }

    /**
     * reloads the current page and sets page to 1
     * 
     */
    private void loadAndResetStudentLoader() {
        if (_pagingToolBar.getActivePage() == 1) {
            _studentLoader.load();
        } else {
            _pagingToolBar.setActivePage(1);
        }
    }

    /**
     * Log in as selected user (student)
     * 
     * 
     */
    private void loginAsSelectedUser(boolean teacherMode) {
        StudentModelExt sm = getSelectedStudent();
        if (sm == null)
            return;
        
        if (sm.getProgram().getIsActiveProgram() == false) {
            if(CmShared.getQueryParameter("debug") == null) {
                CatchupMathTools.showAlert("Student is using a Parallel Program, login is not possible at this time.");
            }
        	return;
        }
        
        String mode=teacherMode?"mode=t":"debug=true";
        
        String server = CmShared.getServerForCmStudent();
        String url = server + "/loginService?uid=" + sm.getUid() + "&" + mode;
        Window.open(url, "_blank", "location=1,menubar=1,resizable=1,scrollbars=yes");
    }

    /**
     * Return the selected student model, or null if none selected
     * 
     * @return
     */
    private StudentModelExt getSelectedStudent() {
        StudentModelExt sm = _grid.getSelectionModel().getSelectedItem();
        return sm;
    }

    private void showDebugInfo() {
        StudentModelExt sm = getSelectedStudent();
        if (sm == null)
            return;

        CatchupMathTools.showAlert("UID: " + sm.getUid());
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();
        toolbar.setSpacing(5);
        toolbar.addStyleName("student-grid-panel-toolbar");
        toolbar.add(createRegistrationButton());
        toolbar.add(editStudentToolItem(_grid, _cmAdminMdl));
        toolbar.add(studentDetailsToolItem(_grid));
        toolbar.add(trendingReportButton());

        toolbar.add(highlightsButton());

        Button customButton = new StudentPanelButton("Custom", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        new CustomProgramDialog(_cmAdminMdl);
                    }
                });
            }
        });
        toolbar.add(customButton);

        toolbar.add(new StudentPanelButton("Program Details", new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        ProgramDetailsPanel.showPanel(_cmAdminMdl);
                    }
                });
            }
        }));

        toolbar.add(exportStudentsToolItem(_grid));

        if (CmShared.getQueryParameter("debug") != null) {
            toolbar.add(createRefreshButton());
        }

        toolbar.add(new FillToolItem());

        toolbar.add(displayPrintableReportToolItem(_grid));

        return toolbar;
    }

    private Button createRegistrationButton() {
        Button btn = new StudentPanelButton("Student Registration");
        btn.setToolTip("Register students with Catchup Math");
        Menu menu = new Menu();

        menu.add(defineRegisterItem());
        
        if (CmShared.getQueryParameter("debug") != null) {
            menu.add(defineRegisterProtoItem());
        }

        menu.add(defineUnregisterItem());

        menu.add(defineBulkRegItem());

        menu.add(defineSelfRegItem());

        menu.add(defineManageGroupsItem());

        menu.add(defineParallelProgramsItem());
 
        btn.setMenu(menu);

        return btn;
    }

    private MyMenuItem defineRegisterItem() {
        return new MyMenuItem("Register One Student", "Create a new single student registration.",
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        GWT.runAsync(new CmRunAsyncCallback() {

                            @Override
                            public void onSuccess() {
                                new RegisterStudent(null, _cmAdminMdl).showWindow();
                            }
                        });
                    }
                });
    }

    private MyMenuItem defineRegisterProtoItem() {
        return new MyMenuItem("Register Prototype", "Create a new single student registration.",
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        GWT.runAsync(new CmRunAsyncCallback() {

                            @Override
                            public void onSuccess() {
                                new RegisterStudentProto(null, _cmAdminMdl).showWindow();
                            }
                        });
                    }
                });
    }

    private MyMenuItem defineUnregisterItem() {
        return new MyMenuItem("Unregister Student", "Unregister the selected student.",
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        GridSelectionModel<StudentModelExt> sel = _grid.getSelectionModel();
                        List<StudentModelExt> l = sel.getSelection();
                        if (l.size() == 0) {
                            CatchupMathTools.showAlert("Please select a student.");
                        } else {
                            final StudentModelExt sm = l.get(0);

                            String s = "Unregister " + sm.getName() + " ?";
                            MessageBox.confirm("Unregister Student", s, new Listener<MessageBoxEvent>() {
                                public void handleEvent(MessageBoxEvent be) {
                                    String btnText = be.getButtonClicked().getText();
                                    if (btnText.equalsIgnoreCase("yes")) {
                                        _grid.getStore().remove(sm);
                                        List<StudentModelI> list = new ArrayList<StudentModelI>();
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
                });
    }

    private MyMenuItem defineSelfRegItem() {
        return new MyMenuItem("Self Registration", "Define a Self Registration group.",
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        GWT.runAsync(new CmRunAsyncCallback() {
                            @Override
                            public void onSuccess() {
                                new AutoRegisterStudentSetup(null, _cmAdminMdl);
                            }
                        });
                    }
                });
    }

    private MyMenuItem defineBulkRegItem() {
        return new MyMenuItem("Bulk Registration", "Bulk student registration.", new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                GWT.runAsync(new CmRunAsyncCallback() {

                    @Override
                    public void onSuccess() {
                        new BulkStudentRegistrationWindow(null, _cmAdminMdl);
                    }
                });
            }
        });
    }

    private MyMenuItem defineManageGroupsItem() {
        return new MyMenuItem("Manage Groups", "Manage group definitions.",
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        GWT.runAsync(new CmRunAsyncCallback() {
                            @Override
                            public void onSuccess() {
                                new ManageGroupsWindow(_cmAdminMdl).setVisible(true);
                            }
                        });
                    }
                });
    }

    private MyMenuItem defineParallelProgramsItem() {
        return new MyMenuItem("Parallel Programs", "Manage Parallel Programs.",
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        GWT.runAsync(new CmRunAsyncCallback() {
                            @Override
                            public void onSuccess() {
                                new ManageParallelProgramsWindow(_cmAdminMdl).setVisible(true);

                            }
                        });
                    }
                });
    }

    static class MyMenuItem extends MenuItem {
        public MyMenuItem(String test, String tip, SelectionListener<MenuEvent> listener) {
            super(test, listener);
            setToolTip(tip);
        }
    }

    private Button createRefreshButton() {
        Button btn = new StudentPanelButton("Refresh List");
        btn.setToolTip("Refresh Student List with latest information.");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CmAdminDataReader.getInstance().fireRefreshData();
            }
        });
        return btn;
    }

    private Button trendingReportButton() {
        Button btn = new StudentPanelButton("Assessment");
        btn.setToolTip("Display lessons being assigned the most.");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        new TrendingDataWindow(_cmAdminMdl.getId());
                    }
                });
            }
        });
        return btn;
    }

    private Button highlightsButton() {
        Button btn = new StudentPanelButton("Highlights");
        btn.setToolTip("Display statistical student highlights");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        HighlightsDataWindow.getSharedInstance(_cmAdminMdl.getId());
                    }
                });
            }
        });
        return btn;
    }

    private Grid<StudentModelExt> defineGrid(final ListStore<StudentModelExt> store, ColumnModel cm) {
        final Grid<StudentModelExt> grid = new Grid<StudentModelExt>(store, cm);
        grid.setAutoExpandColumn("name");
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModelExt>>() {
            public void handleEvent(final SelectionEvent<StudentModelExt> se) {

                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    GWT.runAsync(new CmRunAsyncCallback() {
                        @Override
                        public void onSuccess() {
                            new StudentDetailsWindow(se.getModel());
                        }
                    });
                }
            }
        });

        grid.setWidth("500px");
        grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);
        return grid;
    }

    private Button editStudentToolItem(final Grid<StudentModelExt> grid, final CmAdminModel cmAdminMdl) {
        Button ti = new StudentPanelButton("Edit Student");
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
        GridSelectionModel<StudentModelExt> sel = _grid.getSelectionModel();
        List<StudentModelExt> l = sel.getSelection();
        if (l.size() == 0) {
            CatchupMathTools.showAlert("Please select a student.");
        } else {
            final StudentModelExt sm = l.get(0);
            GWT.runAsync(new CmRunAsyncCallback() {
                @Override
                public void onSuccess() {
                    new RegisterStudent(sm, _cmAdminMdl).showWindow();
                }
            });
        }
    }

    private Button studentDetailsToolItem(final Grid<StudentModelExt> grid) {
        Button ti = new StudentPanelButton("Student Detail History");
        ti.setToolTip("View details for the selected student.");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                GridSelectionModel<StudentModelExt> sel = grid.getSelectionModel();
                final List<StudentModelExt> l = sel.getSelection();
                if (l.size() == 0) {
                    CatchupMathTools.showAlert("Please select a student.");
                } else {
                    showStudentDetails(l.get(0));
                }
            }
        });
        return ti;
    }

    private void showStudentDetails(final StudentModelExt sm) {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                new StudentDetailsWindow(sm);
            }
        });
    }

    private Button exportStudentsToolItem(final Grid<StudentModelExt> grid) {
    	Button ti = new StudentPanelButton("Export");
    	ti.setToolTip("Export student data to an Excel spreadsheet.");

    	ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
    		public void componentSelected(ButtonEvent ce) {
    			GWT.runAsync(new CmRunAsyncCallback() {
    				@Override
    				public void onSuccess() {
    					new ExportStudentData(_cmAdminMdl.getId());
    				}
    			});
    		}
    	});
    	return ti;
    }

    private Button displayPrintableReportToolItem(final Grid<StudentModelExt> grid) {
        Button btn = new Button();
        btn.setIconStyle("printer-icon");
        btn.setToolTip("Create a file for printing or sharing");

        Menu menu = new Menu();

        menu.add(defineSummaryItem(grid));

        menu.add(defineReportCardItem(grid));

        btn.setMenu(menu);

        return btn;
    }

    private MyMenuItem defineSummaryItem(final Grid<StudentModelExt> grid) {

        return new MyMenuItem("Summary Page(s)", "Display a printable summary report.",
                new SelectionListener<MenuEvent>() {
                    @Override
                    public void componentSelected(MenuEvent ce) {
                        ListStore<StudentModelExt> store = grid.getStore();

                        final List<Integer> studentUids = new ArrayList<Integer>();
                        for (int i = 0; i < store.getCount(); i++) {
                            studentUids.add(store.getAt(i).getUid());
                        }
                        if (studentUids.size() > 0) {
                            GWT.runAsync(new CmRunAsyncCallback() {

                                @Override
                                public void onSuccess() {
                                    GeneratePdfAction pdfAction = new GeneratePdfAction(PdfType.STUDENT_SUMMARY,
                                            _cmAdminMdl.getId(), studentUids);
                                    pdfAction.setPageAction(StudentGridPanel.instance._pageAction);
                                    new PdfWindow(_cmAdminMdl.getId(), "Catchup Math Student Summary Report", pdfAction);
                                }
                            });
                        } else {
                            CatchupMathTools.showAlert("No students currently displayed.");
                        }
                    }
                });
    }

    int currentStudentCount;
    static final int MAX_REPORT_CARD = 50;

    private MyMenuItem defineReportCardItem(final Grid<StudentModelExt> grid) {

        return new MyMenuItem("Student Report Cards(s)", "Display printable report cards for up to " + MAX_REPORT_CARD
                + " students.", new SelectionListener<MenuEvent>() {
            @Override
            public void componentSelected(MenuEvent ce) {
                ListStore<StudentModelExt> store = grid.getStore();

                final List<Integer> studentUids = new ArrayList<Integer>();
                for (int i = 0; i < store.getCount(); i++) {
                    studentUids.add(store.getAt(i).getUid());
                }
                int studentCount = studentUids.size();
                if (studentCount > 0 && currentStudentCount <= MAX_REPORT_CARD) {
                    GWT.runAsync(new CmRunAsyncCallback() {

                        @Override
                        public void onSuccess() {
                            GeneratePdfAction pdfAction = new GeneratePdfAction(PdfType.REPORT_CARD, _cmAdminMdl
                                    .getId(), studentUids);
                            new PdfWindow(_cmAdminMdl.getId(), "Catchup Math Student Report Card", pdfAction);
                        }
                    });
                } else {
                    if (studentCount < 1)
                        CatchupMathTools.showAlert("Report Cards", "No students currently displayed.");
                    else
                        CatchupMathTools.showAlert("Report Cards", currentStudentCount
                                + " students selected, please choose a 'Group' and/or use 'Text Search' to select "
                                + MAX_REPORT_CARD + " or fewer students.");
                }
            }
        });
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Student");
        column.setWidth(140);
        column.setSortable(true);
        configs.add(column);

        if (UserInfoBase.getInstance().getPartner() == null) {
            ColumnConfig pass = new ColumnConfig();
            pass.setId("passcode");
            pass.setHeader("Password");
            pass.setWidth(120);
            pass.setSortable(true);
            configs.add(pass);
        }

        ColumnConfig group = new ColumnConfig();
        group.setId(StudentModelExt.GROUP_KEY);
        group.setHeader("Group");
        group.setWidth(100);
        group.setSortable(true);
        configs.add(group);

        ColumnConfig prog = new ColumnConfig();
        prog.setId(StudentModelExt.PROGRAM_DESCR_KEY);
        prog.setHeader("Program");
        prog.setWidth(100);
        prog.setSortable(true);
        configs.add(prog);

        ColumnConfig status = new ColumnConfig();
        status.setId(StudentModelExt.STATUS_KEY);
        status.setHeader("Status");
        status.setWidth(120);
        status.setSortable(true);
        configs.add(status);

        ColumnConfig quizzes = defineQuizzesColumn();
        configs.add(quizzes);

        ColumnConfig lastQuiz = defineLastQuizColumn();
        configs.add(lastQuiz);

        ColumnConfig lastLogin = new ColumnConfig();
        lastLogin.setId(StudentModelExt.LAST_LOGIN_KEY);
        lastLogin.setHeader("Last Login");
        lastLogin.setWidth(70);
        lastLogin.setSortable(true);
        configs.add(lastLogin);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    private ColumnConfig defineLastQuizColumn() {
        ColumnConfig lastQuiz = new ColumnConfig();
        lastQuiz.setId(StudentModelExt.LAST_QUIZ_KEY);
        lastQuiz.setHeader("Last Quiz");
        lastQuiz.setWidth(70);
        lastQuiz.setSortable(true);
        lastQuiz.setRenderer(new GridCellRenderer<StudentModelExt>() {
            @Override
            public Object render(StudentModelExt sm, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<StudentModelExt> store, Grid<StudentModelExt> grid) {
                if (sm.getProgram().getCustom().isCustomLessons()) {
                    return "";
                } else {
                    return sm.getLastQuiz();
                }
            }
        });
        return lastQuiz;
    }

    private ColumnConfig defineQuizzesColumn() {
        ColumnConfig quizzes = new ColumnConfig();
        quizzes.setId(StudentModelExt.PASSING_COUNT_KEY);
        quizzes.setHeader("Quizzes");
        quizzes.setWidth(100);
        quizzes.setSortable(true);
        quizzes.setToolTip("Passed quizzes vs the number taken.");
        quizzes.setRenderer(new GridCellRenderer<StudentModelExt>() {
            @Override
            public Object render(StudentModelExt sm, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<StudentModelExt> store, Grid<StudentModelExt> grid) {
                if (!sm.getProgram().isCustom()
                        && (sm.getPassingCount() > 0 || sm.getNotPassingCount() > 0)) {
                    StringBuffer sb = new StringBuffer();
                    sb.append(sm.getPassingCount()).append(" passed out of ");
                    sb.append(sm.getPassingCount() + sm.getNotPassingCount());
                    return sb.toString();
                } else {
                    return "";
                }
            }
        });
        return quizzes;
    }

    boolean hasBeenInitialized = false;

    protected void getStudentsRPC(Integer uid, final ListStore<StudentModelExt> store, final Integer uidToSelect) {
        /**
         * do not do this the first time, because the paging loader will
         * initialize the data set, we will use this call to refresh its view.
         */
        if (hasBeenInitialized) {
            _forceServerRefresh = true;
            _studentLoader.load();
        } else {
            hasBeenInitialized = true;
        }
    }

    /**
     * Unregister named students
     * 
     * @TODO: rewrite using appropriate result object instead of JSON.
     * 
     * @param smList
     */
    protected void unregisterStudentsRPC(final List<StudentModelI> smList) {

        new RetryAction<StringHolder>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                UnregisterStudentsAction action = new UnregisterStudentsAction(smList);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(StringHolder result) {
                CmBusyManager.setBusy(false);

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

                CmAdminDataReader.getInstance().fireRefreshData();
            }
        }.register();
    }

    /*
     * what page is the grid currently viewing?
     */
    private int getCurrentPage() {
        int offset = _pageAction.getLoadConfig().getOffset();
        return offset == 0 ? 0 : offset / MAX_ROWS_PER_PAGE;
    }

    /**
     * Read any extended data for the current page
     * 
     */
    GetStudentGridPageExtendedAction _lastRequest;

    private void readExtendedDataForPage(final List<Integer> studentUids) {
        new RetryAction<CmStudentPagingLoadResult<StudentModelExt>>() {
            int _requestPage;

            @Override
            public void attempt() {
                GetStudentGridPageExtendedAction action = new GetStudentGridPageExtendedAction(
                        StudentGridPanel.this._cmAdminMdl.getId(), studentUids);

                /**
                 * save the page used to make request
                 * 
                 */
                _requestPage = getCurrentPage();

                setAction(action);
                _lastRequest = action;
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmStudentPagingLoadResult<StudentModelExt> value) {

                /**
                 * Check to make sure the current page (_grid) is on the
                 * associated with this request.
                 */
                int pageNow = getCurrentPage();
                if (pageNow != _requestPage) {
                    CmLogger.debug("Extended data out of sync (" + pageNow + " != " + _requestPage);
                    return; // ?

                }
                for (int i = 0, t = value.getData().size(); i < t; i++) {
                    try {
                        StudentModelExt smEx = value.getData().get(i);
                        StudentModelExt smLive = _grid.getStore().getModels().get(i);

                        smLive.setPassingCount(smEx.getPassingCount());
                        smLive.setLastQuiz(smEx.getLastQuiz());
                        smLive.setLastLogin(smEx.getLastLogin());
                        smLive.setNotPassingCount(smEx.getNotPassingCount());
                        smLive.setTutoringUse(smEx.getTutoringUse());
                    } catch (Exception ex) {
                        CmLogger.debug("Extended data could not be set for row '" + i + ": " + ex.getMessage());
                    }
                }
                /**
                 * Force refresh of display TODO: is there a better way than
                 * refreshing entire display?
                 */
                _grid.reconfigure(_grid.getStore(), _grid.getColumnModel());
            }
        }.register();
    }

    @Override
    public void beginStep() {
        // empty impl
    }

    @Override
    public void completeStep() {
        // empty impl
    }

    @Override
    public void finish() {
        // empty impl
    }

    GetStudentGridPageAction _pageAction = null;

    public GetStudentGridPageAction getPageAction() {
    	return _pageAction;
    }

    /**
     * Create proxy to handle the paged student grid RPC calls
     * 
     */
    class StudentGridRpcProxy extends RpcProxy<CmStudentPagingLoadResult<StudentModelExt>> {

        @Override
        public void load(final Object loadConfig,
                final AsyncCallback<CmStudentPagingLoadResult<StudentModelExt>> callback) {

            new RetryAction<CmStudentPagingLoadResult<StudentModelExt>>() {
                @Override
                public void attempt() {
                    CmBusyManager.setBusy(true);
                    _pageAction = new GetStudentGridPageAction(_cmAdminMdl.getId(), (PagingLoadConfig) loadConfig);
                    setAction(_pageAction);

                    /**
                     * setup request for special handling
                     * 
                     * use module vars to hold request options
                     */
                    _pageAction.setForceRefresh(_forceServerRefresh);
                    if (_groupFilterId != null) {
                        _pageAction.setGroupFilter(_groupFilterId.toString());
                        _pageAction.addFilter(GetStudentGridPageAction.FilterType.GROUP, _groupFilterId.toString());
                    } else
                        _pageAction.setGroupFilter(null);

                    _pageAction.setQuickSearch(_quickSearch);
                    if (_quickSearch != null && _quickSearch.trim().length() > 0) {
                        _pageAction.addFilter(GetStudentGridPageAction.FilterType.QUICKTEXT, _quickSearch.trim());
                    }

                    String dateRange = null;
                    String value = dateRangeFilter.getValue();  /** will be null until initialized */
                    if (value != null && value.trim().length() > 0 && fromDate != null && toDate != null) {
                    	dateRange = dateFormat.format(fromDate) + " - " + dateFormat.format(toDate);
                        _pageAction.addFilter(GetStudentGridPageAction.FilterType.DATE_RANGE, dateRange);
                        _pageAction.addFilter(GetStudentGridPageAction.FilterType.OPTIONS, (_filterOptions!=null?_filterOptions.toParsableString():""));
                    }
                    _pageAction.setDateRange(dateRange);

                    CmShared.getCmService().execute(_pageAction, this);

                    /** always turn off */
                    _forceServerRefresh = false;
                    _pageAction.setForceRefresh(_forceServerRefresh);
                }

                @Override
                public void oncapture(CmStudentPagingLoadResult<StudentModelExt> students) {
                    /** always reset request options */
                    _forceServerRefresh = false;
                    /**
                     * callback the proxy listener
                     * 
                     */
                    currentStudentCount = students.getTotalLength();
                    
                    EventBus.getInstance().fireEvent(
                            new CmEvent(EventType.EVENT_TYPE_STUDENT_GRID_FILTERED, _pageAction));

                    callback.onSuccess(students);
                    CmBusyManager.setBusy(false);
                }
            }.attempt();

            new RetryAction<CmStudentPagingLoadResult<StudentModelExt>>() {
                @Override
                public void attempt() {
                }

                @Override
                public void oncapture(CmStudentPagingLoadResult<StudentModelExt> students) {
                }
            }.register();
        }
    }

    /**
     * Create panel to show a quick search text field and clear button
     * 
     * @author casey
     * 
     */
    class QuickSearchPanel extends HorizontalPanel {
        TextField<String> quickFilter;

        public QuickSearchPanel() {

            String qfTip = "Apply text filter to first four columns.";
            quickFilter = new TextField<String>();
            quickFilter.setEmptyText("--- Text Search ---");
            quickFilter.setToolTip(qfTip);
            quickFilter.setFieldLabel("Text Search");
            quickFilter.addListener(Events.KeyUp, new Listener<FieldEvent>() {
                public void handleEvent(FieldEvent be) {
                    if (be.getKeyCode() == 13) {
                        applyQuickSearch();
                    }
                }
            });
            add(quickFilter);

            Button submit = new Button("submit", new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    applyQuickSearch();
                }
            });
            submit.setToolTip(qfTip);
            add(submit);
            Button clear = new Button("clear", new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    quickFilter.setValue("");
                    boolean shouldRefresh = (_quickSearch != null && _quickSearch.length() > 0);
                    _quickSearch = null;
                    if (shouldRefresh)
                        loadAndResetStudentLoader();
                }
            });
            clear.setToolTip("Clear text");
            add(clear);
        }

        private void applyQuickSearch() {
            CmLogger.debug("GroupFilter: setting quick search: " + quickFilter.getValue());
            boolean shouldRefresh = true;
            if (_quickSearch != null && _quickSearch.equals(quickFilter.getValue()))
                shouldRefresh = false;

            if (shouldRefresh) {
                _quickSearch = quickFilter.getValue();
                loadAndResetStudentLoader();
            }
        }
    }

    /**
     * monitor selected row and reset after reload
     * 
     * @author casey
     * 
     */
    class StudentLoadListener extends LoadListener {
        StudentModelExt selected;

        public StudentLoadListener() {
        }

        @Override
        public void loaderBeforeLoad(LoadEvent le) {
            /** capture selected row */
            selected = _grid.getSelectionModel().getSelectedItem();
        }

        @Override
        public void loaderLoad(LoadEvent le) {
            if (selected != null) {
                final Integer uid = selected.getUid();
                for (int i = 0, t = _grid.getStore().getModels().size(); i < t; i++) {
                    if (_grid.getStore().getAt(i).getUid() == uid) {
                        /**
                         * Must set in separate thread for this to work due to
                         * GXT bug in setting the selected row on layout() which
                         * overrides this action. This is the only way I could
                         * get the currently selected row re-selected on
                         * refresh.
                         * 
                         * @TODO: recheck this condition on next GXT build.
                         */
                        final int visRow = i;
                        new Timer() {
                            public void run() {
                                _grid.getSelectionModel().select(_grid.getStore().getAt(visRow), false);
                                _grid.getView().ensureVisible(visRow, 1, true);
                            }
                        }.schedule(1);
                        break;
                    }
                }
            }
        }
    }

    /**
     * Set and clear date range
     * 
     * @author bob
     * 
     */
    static Date fromDate, toDate;
    static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
    static TextField<String> dateRangeFilter;
    static FilterOptions _filterOptions;
    
    public Date getFromDate() {
    	if (fromDate == null)
            fromDate = CatchupMathAdmin.getInstance().getAccountInfoPanel().getModel().getAccountCreateDate();
        return fromDate;
    }
    
    public Date getToDate() {
    	if (toDate == null) {
            toDate = new Date();
            addDaysToDate(toDate, 1);
    	}
    	return toDate;
    }

    @SuppressWarnings("deprecation") // GWT requires Date
    public void addDaysToDate(Date date, int days) {
        date.setDate(date.getDate() + days);
    }

    public String formatDateRange() {
        return dateFormat.format(getFromDate()) + " - " + dateFormat.format(getToDate());
    }

    class DateRangePanel extends HorizontalPanel {
        
        Button dateRangeButton;
        Button clearButton;
       
        DateRangePanel() {
        	init();
        }

    	void init() {
    		
            dateRangeFilter = new TextField<String>();
            dateRangeFilter.setEmptyText(" Use \"set\" for Date Range");
            dateRangeFilter.setFieldLabel("Date Range");
            dateRangeFilter.setWidth("160px");
            dateRangeFilter.setReadOnly(true);
            dateRangeFilter.setToolTip("No date range filter applied");
            dateRangeFilter.addListener(Events.OnMouseUp, new Listener<BaseEvent>() {
				@Override
				public void handleEvent(BaseEvent be) {
                	getFromDate();
                    showDatePicker();
                }
            });

            getToDate();

            add(dateRangeFilter);
            
            dateRangeButton = new Button("set", new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                	if (fromDate == null)
                        fromDate = CatchupMathAdmin.getInstance().getAccountInfoPanel().getModel().getAccountCreateDate();
                    showDatePicker();
                }
            });
            dateRangeButton.setToolTip("Set Date range filter");
            add(dateRangeButton);

            clearButton = new Button("clear", new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                	clearDateRange();
                }
            });
            clearButton.setToolTip("Set date range to maximum");
            add(clearButton);

    	}

        private void showDatePicker() {
            DateRangePickerDialog.showSharedInstance(fromDate, toDate, new DateRangePickerDialog.Callback() {
                @Override
                public void datePicked(Date from, Date to, FilterOptions filterOptions) {
                    fromDate = (from != null) ? from : fromDate;
                    toDate = (to != null) ? to : toDate;

                    dateRangeFilter.setValue(formatDateRange(from, to));
                    dateRangeFilter.setToolTip("Date range filter applied to Student activity");
                    _filterOptions = filterOptions;
                    
                    applyDateRange();
                }
            });
        }

        private void clearDateRange() {

            dateRangeFilter.clear();
            dateRangeFilter.setToolTip("No date range filter applied");

            fromDate = CatchupMathAdmin.getInstance().getAccountInfoPanel().getModel().getAccountCreateDate();
            toDate = new Date();
            addDaysToDate(toDate, 1);

            _filterOptions = null;

            applyDateRange();
        }

        private void applyDateRange() {
            loadAndResetStudentLoader();
        }

        private String formatDateRange(Date from, Date to) {
        	if (from != null && to != null)
                return dateFormat.format(from) + " - " + dateFormat.format(to);
        	else
        		return " ";
        }
    }

}

/**
 * Provides standard button sizing
 * 
 * @author casey
 * 
 */
class StudentPanelButton extends Button {
    public StudentPanelButton(String name) {
        this(name, null);
    }

    public StudentPanelButton(String name, SelectionListener<ButtonEvent> listener) {
        super(name, listener);
        addStyleName("student-grid-panel-button");
        // setWidth(115);
    }
}
