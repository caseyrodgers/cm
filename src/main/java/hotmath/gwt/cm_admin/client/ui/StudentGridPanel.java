package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.CmAdminModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;


//import hotmath.testset.ha.HaUser;

public class StudentGridPanel extends LayoutContainer {

	static public StudentGridPanel instance;

	private StudentDetailsPanel detailsPanel;
	private ToolBar toolBar;
	
	// TODO: undo button?
	//private Button undoButton;
	
	public StudentGridPanel(CmAdminModel cmAdminMdl) {
		
	    setLayout(new FlowLayout(10));
	    
	    final ListStore<StudentModel> store = new ListStore<StudentModel>();
	    getStudentsRPC(cmAdminMdl.getId(), store);
	    
	    ColumnModel cm = defineColumns();

	    Grid<StudentModel> grid = defineGrid(store, cm);
	    
	    ToolBar toolBar = buildToolBar(grid, cmAdminMdl);
	    
	    //TODO: using a HorizontalPanel does not produce centering...
	    //HorizontalPanel hp = new HorizontalPanel();
	    //hp.setHeight(700);
	    //hp.setWidth(500);
	    //hp.setHorizontalAlign(HorizontalAlignment.CENTER);
	    
	    ContentPanel cp = new ContentPanel();
	    getAccountInfoRPC(cmAdminMdl.getId(), cp);
	    //hp.add(mainContentPanel);
	    
	    //cp.setFrame(false);
	    //cp.setBorders(false);
	    cp.setAutoWidth(true);
	    //cp.setSize(700, 500);
	    cp.setHeight(500);
	    cp.setLayout(new FitLayout());
	    cp.setStyleName("student-grid");
	    
	    cp.setTopComponent(toolBar);
/*
        // TODO: support undo?
	    Button btn = undoButton(grid);
	    cp.addButton(btn);
 */
	    cp.add(grid);

	    add(cp);
		instance = this;
	}

	private ToolBar buildToolBar(final Grid<StudentModel> grid, CmAdminModel cmAdminMdl) {
		toolBar = new ToolBar();

		Button ti = studentDetailsToolItem(grid);
	    toolBar.add(ti);
        toolBar.add(new SeparatorToolItem());
        
        ti = editStudentToolItem(grid, cmAdminMdl);
        toolBar.add(ti);
        toolBar.add(new SeparatorToolItem());
	    
	    ti = registerStudentToolItem(grid, cmAdminMdl);
	    toolBar.add(ti);
        toolBar.add(new SeparatorToolItem());
	    
        toolBar.add(new FillToolItem());
        toolBar.add(new SeparatorToolItem());
        ti = unregisterStudentToolItem(grid);
	    toolBar.add(ti);
		return toolBar;
	}

	private Grid<StudentModel> defineGrid(final ListStore<StudentModel> store,
			ColumnModel cm) {
		final Grid<StudentModel> grid = new Grid<StudentModel>(store, cm);  
	    grid.setAutoExpandColumn("name");  
	    grid.setBorders(true);
	    grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    grid.getSelectionModel().setFiresEvents(true);	    
	    grid.getSelectionModel().addListener(Events.RowDoubleClick,  
            new Listener<SelectionEvent <StudentModel>>() {
                public void handleEvent(SelectionEvent <StudentModel> se) {
            		detailsPanel = new StudentDetailsPanel();
                	detailsPanel.show();
                	detailsPanel.focus();
                    if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    	detailsPanel.setStudentModel(se.getModel());  
                    } else {
                        //detailsPanel.collapse();
                    }
                }
            });
		return grid;
	}

	private Button registerStudentToolItem(
			final Grid<StudentModel> grid, final CmAdminModel cmAdminMdl) {
		Button ti = new Button("Register Student");
		ti.setStyleAttribute("font-weight", "bold");
		ti.setStyleAttribute("font-size", "12px");
		ti.setTitle("Register a new student");

	    ti.addSelectionListener(new SelectionListener<ButtonEvent>() {  
	        public void componentSelected(ButtonEvent ce) {
	        	new RegisterStudent(grid, null, cmAdminMdl);
	          }  
	      
	        });
		return ti;
	}
	
	private Button editStudentToolItem(
			final Grid<StudentModel> grid, final CmAdminModel cmAdminMdl) {
		Button ti = new Button("Edit");
		ti.setStyleAttribute("font-weight", "bold");
		ti.setStyleAttribute("font-size", "12px");
		ti.setTitle("Edit the profile for the selected student.");

	    ti.addSelectionListener(new SelectionListener<ButtonEvent>() {  
	        @Override  
	        public void componentSelected(ButtonEvent ce) {
	        	GridSelectionModel<StudentModel> sel = grid.getSelectionModel();
	        	List <StudentModel> l = sel.getSelection();
	        	if (l.size() == 0) {
	        		CatchupMathAdmin.showAlert("Please select a student.");
	        	}
	        	else {
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
	
	private Button studentDetailsToolItem(
			final Grid<StudentModel> grid) {
		Button ti = new Button("Student Details");
		ti.setStyleAttribute("font-weight", "bold");
		ti.setStyleAttribute("font-size", "12px");
		ti.setTitle("View details for the selected student.");

	    ti.addSelectionListener(new SelectionListener<ButtonEvent>() {  
	        public void componentSelected(ButtonEvent ce) {
	        	GridSelectionModel<StudentModel> sel = grid.getSelectionModel();
	        	List <StudentModel> l = sel.getSelection();
	        	if (l.size() == 0) {
	        		CatchupMathAdmin.showAlert("Please select a student.");
	        	}
	        	else {
            		detailsPanel = new StudentDetailsPanel();
                    detailsPanel.setStudentModel(l.get(0));
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
	
	private Button unregisterStudentToolItem(
			final Grid<StudentModel> grid) {
		Button ti = new Button("Unregister Student");
		ti.setStyleAttribute("font-weight", "bold");
		ti.setStyleAttribute("font-size", "12px");
		ti.setTitle("Unregister the selected student");

	    ti.addSelectionListener(new SelectionListener<ButtonEvent>() {  
	        @Override  
	        public void componentSelected(ButtonEvent ce) {
	        	GridSelectionModel<StudentModel> sel = grid.getSelectionModel();
	        	List <StudentModel> l = sel.getSelection();
	        	if (l.size() == 0) {
	        		CatchupMathAdmin.showAlert("Please select a student.");
	        	}
	        	else {
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
	    column.setWidth(70);
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

	protected void getStudentsRPC(Integer uid, final ListStore <StudentModel> store) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		
		s.getSummariesForActiveStudents(uid, new AsyncCallback <List<StudentModel>>() {

			public void onSuccess(List<StudentModel> result) {
				store.add(result);
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	}
	
	protected void getAccountInfoRPC(Integer uid, final ContentPanel cp) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		
		s.getAccountInfoForAdminUid(uid, new AsyncCallback <AccountInfoModel> () {
			
			public void onSuccess(AccountInfoModel ai) {
			    StringBuilder sb = new StringBuilder();
			    sb.append("Manage ").append(ai.getSchoolName()).append(" Students");
				cp.setHeading(sb.toString());
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	}

	protected void deactivateUserRPC(StudentModel sm) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		
		s.deactivateUser(sm, new AsyncCallback <StudentModel> () {
			
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