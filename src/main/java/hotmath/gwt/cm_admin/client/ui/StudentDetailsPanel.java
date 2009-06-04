package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

//import hotmath.testset.ha.HaUser;

public class StudentDetailsPanel extends LayoutContainer {

	static public StudentDetailsPanel instance;
	
	private StudentModel studentModel;
	
	private ContentPanel outerPanel;
	
	private Window fw;
	
	private HTML html;
	private XTemplate template;
	private ListStore<StudentActivityModel> store;

	public StudentDetailsPanel() {
		
	    setLayout(new FlowLayout(10));

	    fw = new Window();
		fw.setWidth(700);
		fw.setMinWidth(680);
		fw.setHeight(260);
		fw.setLayout(new FitLayout());
		fw.setResizable(true);
		fw.setDraggable(true);
		fw.setModal(true);
		
		outerPanel = new ContentPanel();
	    outerPanel.setHeading("Student Details");  
	    outerPanel.setFrame(true);  
	    outerPanel.setSize(570, 250);
	    outerPanel.setLayout(new RowLayout(Orientation.VERTICAL));
	    outerPanel.setStylePrimaryName("student-details");
	    
	    defineStudentInfoTemplate();
	    
	    store = new ListStore<StudentActivityModel>();
	    
	    ContentPanel icp = new ContentPanel();
	    icp.setStylePrimaryName("student-grid");
	    icp.setSize(540, 160);
	    icp.setLayout(new FitLayout());
	    icp.setHeaderVisible(false);
		
	    ColumnModel cm = defineColumns();
	    
	    final Grid<StudentActivityModel> grid = new Grid<StudentActivityModel>(store, cm);  
	    grid.setBorders(true);
	    grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
	    grid.getSelectionModel().setFiresEvents(true);
	    grid.setHeight(150);
	    
	    icp.add(grid);
		
	    Button btn = showWorkButton(grid);
	    btn.setStyleName("grid-selection-btn");

	    outerPanel.add(icp);	    
	    outerPanel.add(btn);
	    
	    btn = closeButton();
	    //outerPanel.add(btn);
	    
	    fw.add(outerPanel);
	    fw.show();

	    instance = this;
	}

	private HorizontalPanel studentInfoPanel() {
		HorizontalPanel hp = new HorizontalPanel();
		hp.setSpacing(7);
		hp.setHeight(30);
		hp.setWidth(480);
		hp.setBorders(false);
		
		defineStudentInfoTemplate();
		
		hp.add(html);
		return hp;
	}

	private void defineStudentInfoTemplate() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<div class=text style='line-height: 13px; font-size: 11px'>");
		sb.append("<b>Group:</b> {group} &nbsp;|&nbsp;");
		sb.append("<b>Program:</b> {program} &nbsp;|&nbsp;");
		sb.append("<b>Pass % goal for quizzes:</b> {pass-percent} &nbsp;|&nbsp;");
		sb.append("<b>Passcode:</b> {passcode} &nbsp;|&nbsp;");
		sb.append("<b>Tutoring:</b> {tutoring-state}<br>");
		sb.append("</div>");  

		template = XTemplate.create(sb.toString());
		html = new HTML();
	}
	
	private ColumnModel defineColumns() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

	    ColumnConfig date = new ColumnConfig();
	    date.setId(StudentActivityModel.USE_DATE_KEY);  
	    date.setHeader("Date");  
	    date.setWidth(75);
	    date.setSortable(true);
	    configs.add(date);
	    
	    ColumnConfig program = new ColumnConfig();
	    program.setId(StudentActivityModel.PROGRAM_KEY);  
	    program.setHeader("Program");  
	    program.setWidth(105);
	    program.setSortable(true);
	    configs.add(program);	    
	    
	    ColumnConfig activity = new ColumnConfig();  
	    activity.setId(StudentActivityModel.ACTIVITY_KEY);  
	    activity.setHeader("Activity-Section");  
	    activity.setWidth(125);
	    activity.setSortable(true);
	    configs.add(activity);
	    
	    ColumnConfig result = new ColumnConfig();  
	    result.setId(StudentActivityModel.RESULT_KEY);  
	    result.setHeader("Result");  
	    result.setWidth(205);
	    result.setSortable(true);
	    configs.add( result);
/*
	    ColumnConfig start = new ColumnConfig();
	    start.setId(StudentActivityModel.START_KEY);  
	    start.setHeader("Start");  
	    start.setWidth(70);
	    start.setSortable(false);
	    configs.add(start);
	    
	    ColumnConfig finish = new ColumnConfig();  
	    finish.setId(StudentActivityModel.STOP_KEY);  
	    finish.setHeader("Stop");  
	    finish.setWidth(70);
	    finish.setSortable(false);
	    configs.add(finish);
*/
	    ColumnModel cm = new ColumnModel(configs);
		return cm;
	}

	private Button showWorkButton(final Grid<StudentActivityModel> grid) {
		Button btn = new Button("Show Work for Activity", new SelectionListener<ButtonEvent>() {  
	        @Override  
	    	public void componentSelected(ButtonEvent ce) {
	        	GridSelectionModel<StudentActivityModel> sel = grid.getSelectionModel();
	        	List <StudentActivityModel> l = sel.getSelection();
	        	if (l.size() == 0) {
	        		CatchupMathAdmin.getInstance().showAlert("Please select an activity.");
	        	}
	        	else {
	        	    StudentActivityModel sm = l.get(0);
	        	    
	        	    //TODO show work: 1 to N problems, each with 'Show Work' flash r/o

	        	}
	        }  
	    });
	    btn.setIconStyle("icon-delete");
		return btn;
	}

	private Button closeButton() {
		Button btn = new Button("Close", new SelectionListener<ButtonEvent>() {  
	        @Override  
	    	public void componentSelected(ButtonEvent ce) {
                fw.close();	        	
	        }  
	    });
	    btn.setIconStyle("icon-delete");
		return btn;
	}
	protected void getStudentActivityRPC(final ListStore <StudentActivityModel> store, StudentModel sm) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		s.getStudentActivity(sm, new AsyncCallback <List<StudentActivityModel>>() {

			public void onSuccess(List<StudentActivityModel> list) {
				store.add(list);
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	}

	public StudentModel getStudentModel() {
		return studentModel;
	}

	
	public void setStudentModel(StudentModel studentModel) {
		//this.show();
		this.studentModel = studentModel;
		template.overwrite(html.getElement(), Util.getJsObject(studentModel));
		
		outerPanel.setHeading(html.toString());

	    for (StudentActivityModel sam : store.getModels()) {
            store.remove(sam);
	    }
	    
	    fw.setHeading("Student Details for " + studentModel.getName());
	    getStudentActivityRPC(store, studentModel);
	    	    
	}
}