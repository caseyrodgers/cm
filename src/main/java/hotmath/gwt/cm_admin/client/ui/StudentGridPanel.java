package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.custom_content.problem.CustomProblemManager;
import hotmath.gwt.cm_admin.client.ui.highlights.HighlightsDataWindow;
import hotmath.gwt.cm_core.client.model.TeacherIdentity;
import hotmath.gwt.cm_rpc.client.model.StringHolder;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.AutoRegisterStudentSetup;
import hotmath.gwt.cm_tools.client.ui.BulkStudentRegistrationWindow;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.DateRangeCallback;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.ExportStudentData;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.MyMenuItem;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.StudentPanelButton;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
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
import java.util.Iterator;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.LoadEvent;
import com.sencha.gxt.data.shared.loader.LoadHandler;
import com.sencha.gxt.data.shared.loader.LoadResultListStoreBinding;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridSelectionModel;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.menu.Item;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.FillToolItem;
import com.sencha.gxt.widget.core.client.toolbar.PagingToolBar;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class StudentGridPanel extends BorderLayoutContainer implements CmAdminDataRefresher, ProcessTracker {
    static public StudentGridPanel instance;
    private ToolBar toolBar;

    Grid<StudentModelI> _grid;
    CmAdminModel _cmAdminMdl;

    final PagingLoader<PagingLoadConfigBean, CmStudentPagingLoadResult<StudentModelI>> _studentLoader;
    final PagingToolBar _pagingToolBar;
    private DateRangePanel dateRangePanel;

    final int MAX_ROWS_PER_PAGE = 50;

    static StudentGridProperties __gridProps = GWT.create(StudentGridProperties.class);

    public StudentGridPanel(CmAdminModel cmAdminMdl) {
        this._cmAdminMdl = cmAdminMdl;

        ColumnModel<StudentModelI> cm = defineColumns();

        dateRangePanel = new DateRangePanel(new DateRangeCallback() {
            public void applyDateRange() {
                loadAndResetStudentLoader();
            }
        });
    
        /**
         * Create proxy to handle the paged RPC calls
         * 
         */
        StudentGridRpcProxy rpcProxy = new StudentGridRpcProxy(cmAdminMdl, dateRangePanel);

        /**
         * Create a loader to do the actual loading
         * 
         */
        _studentLoader = new PagingLoader<PagingLoadConfigBean, CmStudentPagingLoadResult<StudentModelI>>(rpcProxy);

        _studentLoader.setRemoteSort(true);

        //_studentLoader.addLoadHandler(new MyLoadHandler());

        final ListStore<StudentModelI> store = new ListStore<StudentModelI>(__gridProps.id());
        _studentLoader.addLoadHandler(new LoadResultListStoreBinding<PagingLoadConfigBean, StudentModelI, CmStudentPagingLoadResult<StudentModelI>>(store));

        _grid = defineGrid(store, cm);
        _grid.setLoader(_studentLoader);
  
        //_grid.setStyleName("student-grid-panel-grid");

        setNorthWidget(createToolbar(), new BorderLayoutData(30));

        BorderLayoutContainer lc = new BorderLayoutContainer();
        _pagingToolBar = new PagingToolBar(MAX_ROWS_PER_PAGE);
        _pagingToolBar.bind(_studentLoader);
        lc.setSouthWidget(_pagingToolBar, new BorderLayoutData(30));
        
        SimpleContainer sc = new SimpleContainer();
        sc.addStyleName("student-grid-wrapper");
        sc.setWidget(_grid);
        lc.setCenterWidget(sc, new BorderLayoutData(400));

        setCenterWidget(lc);
        setSouthWidget(new FiltersPanel(cmAdminMdl,dateRangePanel), new BorderLayoutData(35));

        final Menu contextMenu = new Menu();

        MenuItem editUser = new MenuItem("Edit Student");
        editUser.addSelectionHandler(new SelectionHandler<Item>() {

            @Override
            public void onSelection(SelectionEvent<Item> event) {
                editStudent();
                contextMenu.hide();
            }
        });
        contextMenu.add(editUser);

        MenuItem teacherMode = new MenuItem("Teacher Mode Login");
        teacherMode.addSelectionHandler(new SelectionHandler<Item>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Item> event) {
                loginAsSelectedUser(true);
                contextMenu.hide();
            }
        });
        contextMenu.add(teacherMode);

        if (CmShared.getQueryParameter("debug") != null) {
            MenuItem studentDetails = new MenuItem("Student Details");
            studentDetails.addSelectionHandler(new SelectionHandler<Item>() {
                public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Item> event) {
                    showStudentDetails(getSelectedStudent());
                }
            });

            contextMenu.add(studentDetails);
            MenuItem loginAsUser = new MenuItem("Login as User");
            loginAsUser.addSelectionHandler(new SelectionHandler<Item>() {
                @Override
                public void onSelection(SelectionEvent<Item> event) {
                    loginAsSelectedUser(false);
                    contextMenu.hide();
                }
            });
            contextMenu.add(loginAsUser);

            MenuItem debugUser = new MenuItem("Debug Info");
            debugUser.addSelectionHandler(new SelectionHandler<Item>() {
                @Override
                public void onSelection(SelectionEvent<Item> event) {
                    showDebugInfo();
                    contextMenu.hide();
                }
            });
            contextMenu.add(debugUser);

            MenuItem removeUser = new MenuItem("Unregister Student");
            removeUser.addSelectionHandler(new SelectionHandler<Item>() {
                @Override
                public void onSelection(SelectionEvent<Item> event) {
                    unregisterStudentsRPC(Arrays.asList((StudentModelI) getSelectedStudent()));
                    contextMenu.hide();
                }
            });
            contextMenu.add(removeUser);

            MenuItem clientTests = new MenuItem("Launch Client Test");
            clientTests.addSelectionHandler(new SelectionHandler<Item>() {
                @Override
                public void onSelection(SelectionEvent<Item> event) {
                    launchClientTest();
                    contextMenu.hide();
                }
            });
            contextMenu.add(clientTests);

            MenuItem resetUser = new MenuItem("Reset User");
            resetUser.addSelectionHandler(new SelectionHandler<Item>() {
                @Override
                public void onSelection(SelectionEvent<Item> event) {
                    CmMessageBox.confirm("Reset User", "Reset user to beginning of current program?", new ConfirmCallback() {

                        @Override
                        public void confirmed(boolean yesNo) {
                            if (yesNo) {
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

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                // DEBUG
//                new RegisterStudent(null, _cmAdminMdl).showWindow();
                //new BulkStudentRegistrationWindow(null, _cmAdminMdl);
                //new AutoRegisterStudentSetup(null, _cmAdminMdl);
//                GroupInfoModel gim = new GroupInfoModel();
//                gim.setAdminId(2);
//                gim.setDescription("TEST");
//                gim.setGroupName("test");
//                new ManageGroupsAssignStudents(_cmAdminMdl, gim, new CmAsyncRequestImplDefault() {
//                    public void requestComplete(String requestData) {}
//                }).setVisible(true);
                
              //  new ManageParallelProgramsWindow(_cmAdminMdl).setVisible(true);                
            }});
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
                if (!result.getDataAsString("status").equals("OK")) {
                    CmMessageBox.showAlert("Error resetting user: " + result);
                } else {
                    InfoPopupBox.display("Reset User", "User '" + uid + "' reset successfully");
                    refreshDataNow(uid);
                }
            }
        }.register();
    }

    public void refreshData() {
        getStudentsRPC(_cmAdminMdl.getUid(), _grid.getStore(), null);
    }

    /**
     * Launch each client into auto-test. At most launch one page.
     * 
     */
    private void launchClientTest() {
        GWT.runAsync(new CmRunAsyncCallback() {

            @Override
            public void onSuccess() {
                StudentModelI student = _grid.getSelectionModel().getSelectedItem();
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
     * @param uid2Select
     *            The uid to select, or null to select current row
     * 
     */
    public void refreshDataNow(Integer uid2Select) {
        getStudentsRPC(this._cmAdminMdl.getUid(), _grid.getStore(), uid2Select);
    }

    public void enableToolBar() {
        for (int i = 0; i < toolBar.getWidgetCount(); i++) {
            if (toolBar.getWidget(i) instanceof Component) {
                ((Component) toolBar.getWidget(i)).enable();
            }
        }
    }

    /**
     * reloads the current page and sets page to 1
     * 
     */
    public void loadAndResetStudentLoader() {
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
        StudentModelI sm = getSelectedStudent();
        if (sm == null)
            return;

        if (sm.getProgram().getIsActiveProgram() == false) {
            if (CmShared.getQueryParameter("debug") == null) {
                CmMessageBox.showAlert("Student is using a Parallel Program, login is not possible at this time.");
            }
            return;
        }

        String mode = teacherMode ? "mode=t" : "debug=true";

        String server = CmShared.getServerForCmStudent();
        String url = server + "/loginService?uid=" + sm.getUid() + "&" + mode;

        String options = "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes";
        Window.open(url, "_blank", options);
    }

    /**
     * Return the selected student model, or null if none selected
     * 
     * @return
     */
    private StudentModelI getSelectedStudent() {
        return _grid.getSelectionModel().getSelectedItem();
    }

    public List<StudentModelI> getStudentsInGrid() {
        return _grid.getStore().getAll();
    }

    private void showDebugInfo() {
        StudentModelI sm = getSelectedStudent();
        if (sm == null)
            return;
        CmMessageBox.showAlert("UID: " + sm.getUid());
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

        TextButton customButton = new StudentPanelButton("Custom", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                
                if(UserInfoBase.getInstance().isMobile()) {
                    new FeatureNotAvailableToMobile();
                    return;
                }
                
                
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        new CustomProgramDialog(_cmAdminMdl);
                    }
                });
            }
        });
        toolbar.add(customButton);

        toolbar.add(new StudentPanelButton("Program Details", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        ProgramDetailsPanel.showPanel(_cmAdminMdl);
                    }
                });
            }
        }));

        toolbar.add(exportStudentsToolItem(_grid));

        toolbar.add(createRefreshButton());
        
        toolbar.add(createEditAssignmentButton());
        
        toolbar.add(createWebLinksButton());

        if(CmShared.getQueryParameter("debug") != null) {
            toolbar.add(createManageCustomProblemsButton());
        }

        toolbar.add(new FillToolItem());

        toolbar.add(displayPrintableReportToolItem(_grid));
        
        return toolbar;
    }

    private TextButton createRegistrationButton() {
        TextButton btn = new StudentPanelButton("Student Registration");
        btn.setToolTip("Register students with Catchup Math");

        Menu menu = new Menu();
        menu.add(defineRegisterItem());
        menu.add(defineUnregisterItem());
        menu.add(defineBulkRegItem());
        menu.add(defineSelfRegItem());
        menu.add(defineManageGroupsItem());
        menu.add(defineParallelProgramsItem());
        btn.setMenu(menu);
        return btn;
    }

    private MyMenuItem defineRegisterItem() {
        return new MyMenuItem("Register One Student", "Create a new single student registration.", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        new RegisterStudent(null, _cmAdminMdl).showWindow();
                    }
                });
            }
        });
    }

    private MyMenuItem defineUnregisterItem() {
        return new MyMenuItem("Unregister Student", "Unregister the selected student.", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                GridSelectionModel<StudentModelI> sel = _grid.getSelectionModel();
                List<StudentModelI> l = sel.getSelection();
                if (l.size() == 0) {
                    CmMessageBox.showAlert("Please select a student.");
                } else {
                    final StudentModelI sm = l.get(0);

                    String s = "Unregister " + sm.getName() + " ?";
                    CmMessageBox.confirm("Unregister Student", s, new ConfirmCallback() {

                        @Override
                        public void confirmed(boolean yesNo) {
                            if (yesNo) {
                                _grid.getStore().remove(sm);
                                List<StudentModelI> list = new ArrayList<StudentModelI>();
                                list.add(sm);
                                unregisterStudentsRPC(list);
                            }
                        }
                    });
                }
                if (_grid.getStore().size() == 0) {
                    // ce.getComponent().disable();
                }
            }
        });
    }

    private MyMenuItem defineSelfRegItem() {
        return new MyMenuItem("Self Registration", "Define a Self Registration group.", new SelectionHandler<MenuItem>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<MenuItem> event) {
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
        return new MyMenuItem("Bulk Registration", "Bulk student registration.", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
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
        return new MyMenuItem("Manage Groups", "Manage group definitions.", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
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
        return new MyMenuItem("Parallel Programs", "Manage Parallel Programs.", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        new ManageParallelProgramsWindow(_cmAdminMdl).setVisible(true);

                    }
                });
            }
        });
    }

    private TextButton createRefreshButton() {
        TextButton btn = new StudentPanelButton("Refresh List");
        btn.setToolTip("Refresh Student List with latest information.");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CmAdminDataReader.getInstance().fireRefreshData();
            }
        });
        return btn;
    }

    private TextButton trendingReportButton() {
        TextButton btn = new StudentPanelButton("Assessment");
        btn.setToolTip("Display lessons being assigned the most.");
        btn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        new TrendingDataWindow(_cmAdminMdl.getUid());
                    }
                });
            }
        });
        return btn;
    }

    private TextButton highlightsButton() {
        TextButton btn = new StudentPanelButton("Highlights");
        btn.setToolTip("Display statistical student highlights");
        btn.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        HighlightsDataWindow.getSharedInstance(_cmAdminMdl.getUid());
                    }
                });
            }
        });
        return btn;
    }

    private Grid<StudentModelI> defineGrid(final ListStore<StudentModelI> store, ColumnModel<StudentModelI> cm) {

        final Grid<StudentModelI> grid = new Grid<StudentModelI>(store, cm) {
            protected void onAfterFirstAttach() {
                super.onAfterFirstAttach();
                
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                      _studentLoader.load();
                    }
                  });

//                PagingLoadConfigBean config = new PagingLoadConfigBean();
//                config.setOffset(0);
//
//                /** set rows per page */
//                config.setLimit(MAX_ROWS_PER_PAGE);
//
//                if (getData("offset") != null) {
//                    int offset = (Integer) getData("offset");
//                    int limit = (Integer) getData("limit");
//                    config.setOffset(offset);
//                    config.setLimit(limit);
//                }
//
//                if (getData("sortField") != null) {
//                    List<SortInfo> sortFields = new ArrayList<SortInfo>();
//                    sortFields.add(new SortInfoBean((String) getData("sortField"), SortDir.ASC));
//                    sortFields.add(new SortInfoBean((String) getData("sortDir"), SortDir.ASC));
//                    config.setSortInfo(sortFields);
//                }
//               _studentLoader.load(config);                
            }
        };

        grid.getView().setAutoExpandColumn(cm.findColumnConfig("name"));
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        // grid.getSelectionModel().setFiresEvents(true);

        grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    CmLogger.debug("StudentGrid click handler: Showing StudentDetails");
                    new StudentDetailsWindow(grid.getStore().get(event.getRowIndex()));
                }
            }
        });

        //grid.setWidth("500px");
        //grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);
        return grid;
    }

    private TextButton editStudentToolItem(final Grid<StudentModelI> grid, final CmAdminModel cmAdminMdl) {
        TextButton ti = new StudentPanelButton("Edit Student");
        ti.setToolTip("Edit the profile for the selected student.");

        ti.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                editStudent();
                if (grid.getStore().size() > 0) {
                    // ce.getComponent().enable();
                }
            }

        });
        return ti;
    }

    private void editStudent() {
        GridSelectionModel<StudentModelI> sel = _grid.getSelectionModel();
        List<StudentModelI> l = sel.getSelection();
        if (l.size() == 0) {
            CmMessageBox.showAlert("Please select a student.");
        } else {
            final StudentModelI sm = l.get(0);
            GWT.runAsync(new CmRunAsyncCallback() {
                @Override
                public void onSuccess() {
                    new RegisterStudent(sm, _cmAdminMdl).showWindow();
                }
            });
        }
    }

    private TextButton studentDetailsToolItem(final Grid<StudentModelI> grid) {
        TextButton ti = new StudentPanelButton("Student Detail History");
        ti.setToolTip("View details for the selected student.");

        ti.addSelectHandler(new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                GridSelectionModel<StudentModelI> sel = grid.getSelectionModel();
                final List<StudentModelI> l = sel.getSelection();
                if (l.size() == 0) {
                    CmMessageBox.showAlert("Please select a student.");
                } else {
                    showStudentDetails(l.get(0));
                }
            }
        });
        return ti;
    }

    private void showStudentDetails(final StudentModelI sm) {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                new StudentDetailsWindow(sm);
            }
        });
    }

    private TextButton exportStudentsToolItem(final Grid<StudentModelI> grid) {
        TextButton ti = new StudentPanelButton("Export");
        ti.setToolTip("Export student data to an Excel spreadsheet.");

        ti.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        new ExportStudentData(_cmAdminMdl.getUid());
                    }
                });
            }
        });
        return ti;
    }

    private Widget createEditAssignmentButton() {
        TextButton createEdit = new TextButton("Assignments",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {

                        // new GradeBookDialog(_cmAdminMdl.getId());
                        int groupIdToLoad = 0;
                        StudentModelI selected = _grid.getSelectionModel().getSelectedItem();
                        if (selected != null) {
                            groupIdToLoad = selected.getGroupId();
                        }
                        new AssignmentManagerDialog2(groupIdToLoad, _cmAdminMdl.getUid());
                    }
                });                
            }});
        return createEdit;
    }
    
    private Widget createWebLinksButton() {
        TextButton btn = new TextButton("Web Links",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new WebLinksManager(_cmAdminMdl.getUid());
            }});
        return btn;
    }

    
    private Widget createManageCustomProblemsButton() {
        return new TextButton("Custom Problems",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        CustomProblemManager.showManager();
                    }
                });                
            }});
    }

    private TextButton displayPrintableReportToolItem(final Grid<StudentModelI> grid) {
        TextButton btn = new TextButton("Print");
        // btn.setIconStyle("printer-icon");
        btn.setToolTip("Create a file for printing or sharing");

        Menu menu = new Menu();
        menu.add(defineSummaryItem(grid));
        menu.add(defineReportCardItem(grid));
        
        if(CmShared.getQueryParameter("debug") != null) {
            menu.add(defineAssignmentReportItem(grid));
        }

        btn.setMenu(menu);
        return btn;
    }

    private MyMenuItem defineSummaryItem(final Grid<StudentModelI> grid) {

        return new MyMenuItem("Summary Page(s)", "Display a printable summary report.", new SelectionHandler<MenuItem>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<MenuItem> event) {
                ListStore<StudentModelI> store = grid.getStore();

                final List<Integer> studentUids = new ArrayList<Integer>();
                for (int i = 0; i < store.size(); i++) {
                    studentUids.add(store.get(i).getUid());
                }
                if (studentUids.size() > 0) {
                    GWT.runAsync(new CmRunAsyncCallback() {

                        @Override
                        public void onSuccess() {
                            GeneratePdfAction pdfAction = new GeneratePdfAction(PdfType.STUDENT_SUMMARY, _cmAdminMdl.getUid(), studentUids);
                            pdfAction.setPageAction(StudentGridPanel.instance._pageAction);
                            new PdfWindow(_cmAdminMdl.getUid(), "Catchup Math Student Summary Report", pdfAction);
                        }
                    });
                } else {
                    CmMessageBox.showAlert("No students currently displayed.");
                }
            }
        });
    }

    int currentStudentCount;
    static final int MAX_REPORT_CARD = 50;

    private MyMenuItem defineReportCardItem(final Grid<StudentModelI> grid) {

        return new MyMenuItem("Student Report Cards(s)", "Display printable report cards for up to " + MAX_REPORT_CARD + " students.",
                new SelectionHandler<MenuItem>() {
                    public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<MenuItem> event) {

                        ListStore<StudentModelI> store = grid.getStore();

                        final List<Integer> studentUids = new ArrayList<Integer>();
                        for (int i = 0; i < store.size(); i++) {
                            studentUids.add(store.get(i).getUid());
                        }
                        int studentCount = studentUids.size();
                        if (studentCount > 0 && currentStudentCount <= MAX_REPORT_CARD) {
                            GWT.runAsync(new CmRunAsyncCallback() {

                                @Override
                                public void onSuccess() {
                                    DateRangePanel dateRange = DateRangePanel.getInstance();
                                    Date fromDate, toDate;
                                    if (dateRange.isDefault()) {
                                        fromDate = null;
                                        toDate = null;
                                    } else {
                                        fromDate = dateRange.getFromDate();
                                        toDate = dateRange.getToDate();
                                    }

                                    GeneratePdfAction pdfAction = new GeneratePdfAction(PdfType.REPORT_CARD, _cmAdminMdl.getUid(), studentUids, fromDate, toDate);
                                    new PdfWindow(_cmAdminMdl.getUid(), "Catchup Math Student Report Card", pdfAction);
                                }
                            });
                        } else {
                            if (studentCount < 1)
                                CmMessageBox.showAlert("Report Cards", "No students currently displayed.");
                            else
                                CmMessageBox.showAlert("Report Cards", currentStudentCount
                                        + " students selected, please choose a 'Group' and/or use 'Text Search' to select " + MAX_REPORT_CARD
                                        + " or fewer students.");
                        }
                    }
                });
    }

    private MyMenuItem defineAssignmentReportItem(final Grid<StudentModelI> grid) {

        return new MyMenuItem("Student Assignment Report", "Display printable assignment reports for up to 50 students", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {

                final List<Integer> studentUids = new ArrayList<Integer>();
                ListStore<StudentModelI> store = grid.getStore();

                //studentUids.add(student.getUid());
                for (int i = 0; i < store.size(); i++) {
                    studentUids.add(store.get(i).getUid());
                }
                int studentCount = studentUids.size();
                if (studentCount > 0 && currentStudentCount <= MAX_REPORT_CARD) {

                    GWT.runAsync(new CmRunAsyncCallback() {

                        @Override
                        public void onSuccess() {
                            DateRangePanel dateRange = DateRangePanel.getInstance();
                            Date fromDate = null, toDate = null;
                            if (dateRange != null) {
                                if (dateRange.isDefault() == false) {
                                    fromDate = dateRange.getFromDate();
                                    toDate = dateRange.getToDate();
                                }
                            }

                            GeneratePdfAction pdfAction = new GeneratePdfAction(PdfType.ASSIGNMENT_REPORT, _cmAdminMdl.getUid(), studentUids, fromDate, toDate);
                            new PdfWindow(_cmAdminMdl.getUid(), "Catchup Math Student Assignment Report", pdfAction);
                        }
                    });
                } else {
                    if (studentCount < 1)
                        CmMessageBox.showAlert("Report Cards", "No students currently displayed.");
                    else
                        CmMessageBox.showAlert("Report Cards", currentStudentCount
                                + " students selected, please choose a 'Group' and/or use 'Text Search' to select " + MAX_REPORT_CARD + " or fewer students.");
                }
            }
        });
    }

    private ColumnModel<StudentModelI> defineColumns() {
        List<ColumnConfig<StudentModelI, ?>> cols = new ArrayList<ColumnConfig<StudentModelI, ?>>();

        cols.add(new ColumnConfig<StudentModelI, String>(__gridProps.name(), 140, "Student"));
        // column.setSortable(true);

        if (UserInfoBase.getInstance().getPartner() == null) {
            cols.add(new ColumnConfig<StudentModelI, String>(__gridProps.passcode(), 120, "Password"));
            // pass.setSortable(true);
        }

        cols.add(new ColumnConfig<StudentModelI, String>(__gridProps.group(), 100, "Group"));
        // group.setSortable(true);

        cols.add(new ColumnConfig<StudentModelI, String>(__gridProps.programDescription(), 100, "Program"));
        // prog.setSortable(true);

        cols.add(new ColumnConfig<StudentModelI, String>(__gridProps.status(), 120, "Status"));
        // status.setSortable(true);

        cols.add(defineQuizzesColumn());
        cols.add(defineLastQuizColumn());

        cols.add(new ColumnConfig<StudentModelI, String>(__gridProps.lastLogin(), 70, "Last Login"));
        // lastLogin.setSortable(true);

        return new ColumnModel<StudentModelI>(cols);
    }

    private ColumnConfig<StudentModelI, String> defineLastQuizColumn() {
        ColumnConfig<StudentModelI, String> colConfig = new ColumnConfig<StudentModelI, String>(__gridProps.lastQuiz(), 70, "Last Quiz");
        // lastQuiz.setSortable(true);
        colConfig.setCell(new AbstractCell<String>() {
            @Override
            public void render(Context context, String value, SafeHtmlBuilder sb) {
                StudentModelI sm = _grid.getStore().get(context.getIndex());
                try {
                    if (sm.getProgram().getCustom().isCustomLessons()) {
                        sb.appendEscaped("");
                    } else {
                        String lq = sm.getLastQuiz();
                        lq = lq == null?"":lq;
                        sb.appendEscaped(lq);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return colConfig;
    }

    private ColumnConfig<StudentModelI, String> defineQuizzesColumn() {
        ColumnConfig<StudentModelI, String> passingCount = new ColumnConfig<StudentModelI, String>(__gridProps.quizzes(), 100, "Quizzes");
        passingCount.setSortable(false);
        passingCount.setToolTip(SafeHtmlUtils.fromString("Passed quizzes vs the number taken."));
        // quizzes.setSortable(true);

        passingCount.setCell(new AbstractCell<String>() {
            @Override
            public void render(Context context, String value, SafeHtmlBuilder sb) {
                StudentModelI sm = _grid.getStore().findModelWithKey(context.getKey().toString());
                if (!sm.getProgram().isCustom() && (sm.getPassingCount() > 0 || sm.getNotPassingCount() > 0)) {
                    sb.append(sm.getPassingCount());
                    sb.appendEscaped(" passed out of ");
                    sb.append(sm.getPassingCount() + sm.getNotPassingCount());
                } else {
                    sb.appendEscaped("");
                }

            }
        });
        return passingCount;
    }

    boolean hasBeenInitialized = false;

    public boolean _forceServerRefresh=true;

    protected void getStudentsRPC(Integer uid, final ListStore<StudentModelI> store, final Integer uidToSelect) {
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

    public int getCurrentStudentCount() {
    	return  _studentLoader.getTotalCount();
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

                CmMessageBox.showAlert(sb.toString());

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

    public void setPageAction(GetStudentGridPageAction action) {
        _pageAction = action;
    }

    public CmAdminModel getCmAdminMdl() {
		return _cmAdminMdl;
	}

	static final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");

    class MyLoadHandler implements LoadHandler<PagingLoadConfigBean, CmStudentPagingLoadResult<StudentModelI>> {
        @Override
        public void onLoad(LoadEvent<PagingLoadConfigBean, CmStudentPagingLoadResult<hotmath.gwt.cm_tools.client.model.StudentModelI>> event) {
            Info.display("Student Loaded", "MYLOADHANDLER: STUDENT GRID LOADED: " + event);
        }
    }
    // /**
    // * monitor selected row and reset after reload
    // *
    // * @author casey
    // *
    // */
    // class StudentLoadListener extends LoadListener {
    // StudentModelExt selected;
    //
    // public StudentLoadListener() {
    // }
    //
    // @Override
    // public void loaderBeforeLoad(LoadEvent le) {
    // /** capture selected row */
    // selected = _grid.getSelectionModel().getSelectedItem();
    // }
    //
    // @Override
    // public void loaderLoad(LoadEvent le) {
    // if (selected != null) {
    // final Integer uid = selected.getUid();
    // for (int i = 0, t = _grid.getStore().getModels().size(); i < t; i++) {
    // if (_grid.getStore().getAt(i).getUid() == uid) {
    // /**
    // * Must set in separate thread for this to work due to
    // * GXT bug in setting the selected row on layout() which
    // * overrides this action. This is the only way I could
    // * get the currently selected row re-selected on
    // * refresh.
    // *
    // * @TODO: recheck this condition on next GXT build.
    // */
    // final int visRow = i;
    // new Timer() {
    // public void run() {
    // _grid.getSelectionModel().select(_grid.getStore().getAt(visRow), false);
    // _grid.getView().ensureVisible(visRow, 1, true);
    // }
    // }.schedule(1);
    // break;
    // }
    // }
    // }
    // }
    // }
}
