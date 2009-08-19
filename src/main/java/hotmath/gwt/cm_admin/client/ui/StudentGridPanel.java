package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StringHolder;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.ui.AutoRegisterStudentSetup;
import hotmath.gwt.cm_tools.client.ui.BulkStudentRegistrationWindow;
import hotmath.gwt.cm_tools.client.ui.GroupSelectorWidget;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.StudentShowWorkWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.GetReportDefAction;

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
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class StudentGridPanel extends LayoutContainer implements CmAdminDataRefresher, ProcessTracker {

    static public StudentGridPanel instance;

    private ToolBar toolBar;

    // TODO: undo button?
    // private Button undoButton;

    FieldSet _gridContainer;
    Grid<StudentModel> _grid;
    CmAdminModel _cmAdminMdl;
    ListStore<StudentModel> smStore;
    
	private ListStore <GroupModel> groupStore;
	private ComboBox <GroupModel> groupCombo;

    public StudentGridPanel(CmAdminModel cmAdminMdl) {
        this._cmAdminMdl = cmAdminMdl;
        final ListStore<StudentModel> store = new ListStore<StudentModel>();

        ColumnModel cm = defineColumns();
        _grid = defineGrid(store, cm);
        _grid.setStyleName("student-grid-panel-grid");

        _gridContainer = new FieldSet();
        _gridContainer.setStyleName("student-grid-panel-grid-container");

        LayoutContainer lc = new LayoutContainer();
        lc.setLayout(new BorderLayout());
        lc.add(createToolbar(),new BorderLayoutData(LayoutRegion.NORTH,30));

        lc.add(_grid,new BorderLayoutData(LayoutRegion.CENTER));
        
        BorderLayoutData bdl = new BorderLayoutData(LayoutRegion.SOUTH, 60);
        bdl.setMargins(new Margins(10, 5, 0, 5));
        lc.add(createGroupFilter(), bdl);
        _gridContainer.add(lc);
        
        add(_gridContainer);
        
        final Menu contextMenu = new Menu();
        
        MenuItem showWork = new MenuItem("Show Work");
        showWork.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                showWorkDialog();
                contextMenu.hide();
            }
        });
        contextMenu.add(showWork);

        MenuItem editUser = new MenuItem("Edit Student");
        editUser.addSelectionListener(new SelectionListener<MenuEvent>() {
            public void componentSelected(MenuEvent ce) {
                editStudent();
                contextMenu.hide();
            }
        });
        contextMenu.add(editUser);

        if(CmShared.getQueryParameter("debug") != null) {
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
                if(event.getEventName().equals(EventBus.EVENT_TYPE_REFRESH_STUDENT_DATA)) {
                    refreshDataNow(null);
                }
            }
        });
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
    public void refreshDataNow(Integer uid2Select) {
        getStudentsRPC(this._cmAdminMdl.getId(), _grid.getStore(), uid2Select);
    }

    public void enableToolBar() {
        for (Component ti : toolBar.getItems()) {
            ti.enable();
        }
    }

    private FieldSet createGroupFilter() {
    	
		FieldSet fs = new FieldSet();
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(50);
        fl.setDefaultWidth(250);
        fs.setLayout(fl);
        fs.setStyleAttribute("padding-top", "10px");
        fs.setHeading("Filter");
        
        groupStore = new ListStore <GroupModel> ();
		GroupSelectorWidget gsw = new GroupSelectorWidget(_cmAdminMdl, groupStore, false, this);
		groupCombo = gsw.groupCombo();
		
		GroupModel gm = new GroupModel();
		gm.setName("--- reset ---");
		gm.setId("--- reset---");
		groupStore.insert(gm, 0);
		
		groupCombo.addSelectionChangedListener(new SelectionChangedListener<GroupModel>() {
			public void selectionChanged(SelectionChangedEvent<GroupModel> se) {

				// filter grid based on current selection
	        	GroupModel gm = se.getSelectedItem();
	        	
	        	StudentModelGroupFilter smgf = new StudentModelGroupFilter();
	        	
	        	_grid.getStore().addFilter(smgf);
	        	_grid.getStore().applyFilters(gm.getName());
	        }
	    });
		
		fs.add(groupCombo);
		
		return fs;
    }

    /**
     * Log in as selected user (student)
     * 
     */
    private void loginAsSelectedUser() {
        StudentModel sm = _grid.getSelectionModel().getSelectedItem();
        if (sm == null)
            return;

        String server = CmShared.getQueryParameter("host");
        if(server == null || server.length() == 0)
            server = "hotmath.com";
        
        String url = "http://" + server +"/cm_student/CatchupMath.html?uid=" + sm.getUid() + "&debug=true";
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
        
        CatchupMathTools.showAlert("UID: " + sm.getUid());
    }

    private ToolBar createToolbar() {
        ToolBar toolbar = new ToolBar();

        // toolbar.setHorizontalAlign(HorizontalAlignment.CENTER);
        toolbar.setStyleName("student-grid-panel-toolbar");

        Button ti = registerStudentToolItem(_grid, _cmAdminMdl);
        toolbar.add(ti);

        ti = editStudentToolItem(_grid, _cmAdminMdl);
        toolbar.add(ti);

        ti = studentDetailsToolItem(_grid);
        toolbar.add(ti);

        ti = showWorkToolItem(_grid,_cmAdminMdl);
        toolbar.add(ti);

        ti = unregisterStudentToolItem(_grid);
        toolbar.add(ti);

        toolbar.add(new FillToolItem());

        Button btn = new Button("Bulk Registration");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new BulkStudentRegistrationWindow(null, _cmAdminMdl);
            } 
        });
        toolbar.add(btn);

        btn = new Button("Self Registration Setup");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                new AutoRegisterStudentSetup(null, _cmAdminMdl);
            } 
        });
        toolbar.add(btn);

        ti = displayPrintableReportToolItem(_grid);
        toolbar.add(ti);

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
                editStudent();
                if (grid.getStore().getCount() > 0) {
                    //ce.getComponent().enable();
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
            new RegisterStudent(sm, _cmAdminMdl);
        }
      
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
                    CatchupMathTools.showAlert("Please select a student.");
                } else {
                    StudentModel sm = l.get(0);
                    new StudentShowWorkWindow(sm);
                }
                if (grid.getStore().getCount() > 0) {
                    //ce.getComponent().enable();
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

    private Button displayPrintableReportToolItem(final Grid<StudentModel> grid) {
        Button ti = new Button();
        ti.setIconStyle("printer-icon");
        ti.setToolTip("Display a printable student summary report");

        ti.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ListStore<StudentModel> store = grid.getStore();
                displayPrintableReportRPC(store);
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
                
                /** Reselect selected row */
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
                caught.printStackTrace();   // quiet
                // CatchupMathTools.showAlert(msg);
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
                CatchupMathTools.showAlert(msg);
            }
        });
    }

    protected void displayPrintableReportRPC(ListStore<StudentModel> store) {
        
        List<Integer> studentUids = new ArrayList<Integer>(store.getCount());
        for (int i=0; i < store.getCount(); i++) {
        	StudentModel sm = store.getAt(i);
            studentUids.add(sm.getUid()); 	
        }

        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        s.execute(new GetReportDefAction(studentUids), new AsyncCallback<StringHolder>() {

            public void onSuccess(StringHolder reportId) {
            	String url = "/cm_admin/genPDF?id=" + reportId.getResponse() + "&aid=" + _cmAdminMdl.getId() + "&type=studentSummary";
                Window.open(url, "_blank", "location=0,menubar=0,resizable=1");
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }

	//@Override
	public void beginStep() {
		// empty impl
	}

	//@Override
	public void completeStep() {
		// empty impl
	}

	//@Override
	public void finish() {
		// empty impl
	}
}

class StudenPanelButton extends Button {
    public StudenPanelButton(String name) {
        super(name);
        addStyleName("student-grid-panel-button");
    }
}

class StudentModelGroupFilter implements StoreFilter <StudentModel> {

	//@Override
	public boolean select(Store<StudentModel> store, StudentModel parent,
			StudentModel item, String property) {
		System.out.println("+++ item.getGroup(): " + item.getGroup() + ", property: " + property);
		if ("--- reset ---".equals(property)) return true;
		return (property.equals(item.getGroup()));
	}

}