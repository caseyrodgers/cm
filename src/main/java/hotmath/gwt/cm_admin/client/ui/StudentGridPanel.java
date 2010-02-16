package hotmath.gwt.cm_admin.client.ui;

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
import hotmath.gwt.cm_tools.client.ui.GroupSelectorWidget;
import hotmath.gwt.cm_tools.client.ui.PdfWindow;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CmStudentPagingLoadResult;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentGridPageAction;
import hotmath.gwt.shared.client.rpc.action.UnregisterStudentsAction;
import hotmath.gwt.shared.client.rpc.action.GeneratePdfAction.PdfType;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
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
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
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
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
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
    private boolean _forceServerRefresh=true;

    final PagingLoader<PagingLoadResult<StudentModelExt>> _studentLoader;
    final PagingToolBar _pagingToolBar;

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
         * Create a load to do the actual loading
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
                config.setLimit(50);

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
        _pagingToolBar = new PagingToolBar(50);
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

            MenuItem removeUser = new MenuItem("Unregister Student");
            removeUser.addSelectionListener(new SelectionListener<MenuEvent>() {
                public void componentSelected(MenuEvent ce) {
                    unregisterStudentsRPC(Arrays.asList((StudentModelI) getSelectedStudent()));
                    contextMenu.hide();
                }
            });
            contextMenu.add(removeUser);

        }

        _grid.setContextMenu(contextMenu);

        instance = this;

        CmAdminDataReader.getInstance().addReader(this);

        EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
            public void handleEvent(CmEvent event) {
                if (event.getEventType() == EventType.EVENT_TYPE_REFRESH_STUDENT_DATA) {
                    CmAdminDataReader.getInstance().fireRefreshData();
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
        fp.setWidth(300);
        fp.setLabelWidth(80);
        fp.add(new QuickSearchPanel());
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

    private Button manageGroupButton(final Grid<StudentModelExt> grid) {
        final Button btn = new StudenPanelButton("Manage Groups");
        btn.setToolTip("Manage the group definitions.");

        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                GWT.runAsync(new CmRunAsyncCallback() {
                    @Override
                    public void onSuccess() {
                        new ManageGroupsWindow(_cmAdminMdl).setVisible(true);
                    }
                });
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
        StudentModelExt sm = getSelectedStudent();
        if (sm == null)
            return;
        String server = CmShared.getServerForCmStudent();
        String url = server + "/cm_student/CatchupMath.html?uid=" + sm.getUid() + "&debug=true";
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
        toolbar.addStyleName("student-grid-panel-toolbar");
        toolbar.add(createRefreshButton());
        toolbar.add(createRegistrationButton());
        toolbar.add(editStudentToolItem(_grid, _cmAdminMdl));
        toolbar.add(studentDetailsToolItem(_grid));
        toolbar.add(manageGroupButton(_grid));
        
        toolbar.add(trendingReportButton());

        toolbar.add(new FillToolItem());
        toolbar.add(displayPrintableReportToolItem(_grid));

        return toolbar;
    }

    private Button createRegistrationButton() {
        Button btn = new StudenPanelButton("Student Registration");
        btn.setToolTip("Register students with Catchup Math");
        Menu menu = new Menu();

        menu.add(defineRegisterItem());

        menu.add(defineUnregisterItem());
        //btn.setMenu(menu);
        
        menu.add(defineBulkRegItem());
        
        menu.add(defineSelfRegItem());
        
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

    static class MyMenuItem extends MenuItem {
        public MyMenuItem(String test, String tip, SelectionListener<MenuEvent> listener) {
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

    private Button trendingReportButton() {
        Button btn = new StudenPanelButton("Assessment");
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
        Button ti = new StudenPanelButton("Student Detail History");
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

    private Button displayPrintableReportToolItem(final Grid<StudentModelExt> grid) {
        Button ti = new Button();
        ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable student summary report");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ListStore<StudentModelExt> store = grid.getStore();

                final List<Integer> studentUids = new ArrayList<Integer>();
                for (int i = 0; i < store.getCount(); i++) {
                    studentUids.add(store.getAt(i).getUid());
                }
                GWT.runAsync(new CmRunAsyncCallback() {

                    @Override
                    public void onSuccess() {
                    	GeneratePdfAction pdfAction = new GeneratePdfAction(PdfType.STUDENT_SUMMARY, _cmAdminMdl.getId(), studentUids);
                    	pdfAction.setFilterMap(instance._pageAction.getFilterMap());
                        new PdfWindow(_cmAdminMdl.getId(), "Catchup Math Student Summary Report", pdfAction);
                    }
                });
            }
        });
        return ti;
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Student");
        column.setWidth(140);
        column.setSortable(true);
        configs.add(column);

        ColumnConfig pass = new ColumnConfig();
        pass.setId("passcode");
        pass.setHeader("Password");
        pass.setWidth(120);
        pass.setSortable(true);
        configs.add(pass);

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
        
        ColumnConfig lastQuiz = new ColumnConfig();
        lastQuiz.setId(StudentModelExt.LAST_QUIZ_KEY);
        lastQuiz.setHeader("Last Quiz");
        lastQuiz.setWidth(70);
        lastQuiz.setSortable(true);
        configs.add(lastQuiz);

        ColumnConfig lastLogin = new ColumnConfig();
        lastLogin.setId(StudentModelExt.LAST_LOGIN_KEY);
        lastLogin.setHeader("Last Login");
        lastLogin.setWidth(70);
        lastLogin.setSortable(true);
        configs.add(lastLogin);

        ColumnConfig tutoring = new ColumnConfig();
        tutoring.setId(StudentModelExt.TUTORING_USE_KEY);
        tutoring.setHeader("Tutoring");
        tutoring.setToolTip("Number of tutor requests");
        tutoring.setWidth(55);
        tutoring.setSortable(true);
        configs.add(tutoring);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

	private ColumnConfig defineQuizzesColumn() {
		ColumnConfig quizzes = new ColumnConfig();
        quizzes.setId(StudentModelExt.PASSING_COUNT_KEY);
        quizzes.setHeader("Quizzes");
        quizzes.setWidth(100);
        quizzes.setSortable(true);
        quizzes.setRenderer(new GridCellRenderer<StudentModelExt>() {
			@Override
			public Object render(StudentModelExt sm, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<StudentModelExt> store, Grid<StudentModelExt> grid) {
                if (sm.getPassingCount() > 0 || sm.getNotPassingCount() > 0) {
                	StringBuffer sb = new StringBuffer();
                	sb.append(sm.getPassingCount()).append(" passed out of ");
                	sb.append(sm.getPassingCount() + sm.getNotPassingCount());
                    return sb.toString();
                }
                else {
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
                CmShared.getCmService().execute(new UnregisterStudentsAction(smList), this);
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
        }.attempt();
        
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
    
    /**
     * Create proxy to handle the paged student grid RPC calls
     * 
     */
    class StudentGridRpcProxy extends RpcProxy<CmStudentPagingLoadResult<StudentModelExt>> {
        
        @Override
        public void load(final Object loadConfig, final AsyncCallback<CmStudentPagingLoadResult<StudentModelExt>> callback) {
            new RetryAction<CmStudentPagingLoadResult<StudentModelExt>>() {
                @Override
                public void attempt() {
                    _pageAction = new GetStudentGridPageAction(_cmAdminMdl.getId(), (PagingLoadConfig) loadConfig);
                    
                    /**
                     * setup request for special handling
                     * 
                     * use module vars to hold request options
                     */
                    _pageAction.setForceRefresh(_forceServerRefresh);
                    if (_groupFilterId != null) {
                    	_pageAction.setGroupFilter(_groupFilterId.toString());
                    	_pageAction.addFilter(GetStudentGridPageAction.FilterType.GROUP, _groupFilterId.toString());
                    }
                    else
                    	_pageAction.setGroupFilter(null);

                    _pageAction.setQuickSearch(_quickSearch);
                    if (_quickSearch != null && _quickSearch.trim().length() > 0) {
                    	_pageAction.addFilter(GetStudentGridPageAction.FilterType.QUICKTEXT, _quickSearch.trim());
                    }

                    CmShared.getCmService().execute(_pageAction, this);
                }
                
                @Override
                public void oncapture(CmStudentPagingLoadResult<StudentModelExt> value) {
                    /** always reset request options */
                    _forceServerRefresh = false;
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_STUDENT_GRID_FILTERED, _pageAction));

                    /** callback the proxy listener
                     * 
                     */
                    callback.onSuccess(value);
                }
            }.attempt();
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
            quickFilter = new TextField<String>();
            quickFilter.setEmptyText("--- Text Search ---");
            quickFilter.setToolTip("Enter text to match any field");
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
            submit.setToolTip("Apply text search");
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
            Log.debug("GroupFilter: setting quick search: " + quickFilter.getValue());
            boolean shouldRefresh = true;
            if (_quickSearch != null && _quickSearch.equals(quickFilter.getValue()))
                shouldRefresh = false;

            if (shouldRefresh) {
                _quickSearch = quickFilter.getValue();
                loadAndResetStudentLoader();
            }
        }
    }

    /** monitor selected row and reset after reload
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
}

/**
 * Provides standard button sizing
 * 
 * @author casey
 * 
 */
class StudenPanelButton extends Button {
    public StudenPanelButton(String name) {
        super(name);
        addStyleName("student-grid-panel-button");
        setWidth(115);
    }
}



