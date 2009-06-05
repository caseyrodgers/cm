package hotmath.gwt.cm_admin.client.ui;


import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.core.XTemplate;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class StudentDetailsWindow extends Window {
    
    StudentModel studentModel;
    private HTML html;
    private XTemplate template;
    private ListStore<StudentActivityModel> store;
    
    public StudentDetailsWindow(StudentModel studentModel) {
        this.studentModel = studentModel;
        setSize(550,320);
        setModal(true);
        setResizable(false);
        setHeading("Student Details For: " + studentModel.getName());
        setStyleName("student-details-window");

        defineStudentInfoTemplate();
        
        store = new ListStore<StudentActivityModel>();
        ColumnModel cm = defineColumns();
        
        final Grid<StudentActivityModel> grid = new Grid<StudentActivityModel>(store, cm);
        grid.setStyleName("student-details-panel-grid");
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.setWidth(530);
        grid.setHeight(190);
        
        add(studentInfoPanel());
        add(grid);
                
        Button btnClose = closeButton();
        setButtonAlign(HorizontalAlignment.RIGHT);  
        addButton(btnClose);
        
        getStudentActivityRPC(store, studentModel);

        setVisible(true);
    }
    
	private Button closeButton() {
		Button btn = new Button("Close", new SelectionListener<ButtonEvent>() {  
	        @Override  
	    	public void componentSelected(ButtonEvent ce) {
                close();	        	
	        }  
	    });
	    btn.setIconStyle("icon-delete");
		return btn;
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
    
    private Widget studentInfoPanel() {
        defineStudentInfoTemplate();
        return html;
    }    
    
    private void defineStudentInfoTemplate() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='student-detail-info'>");
        sb.append("<div class='form left'>");
        sb.append("  <div class='fld'><label>Group:</label><div>{group}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Program:</label><div>{program}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Pass %:</label><div> {pass-percent}&nbsp;</div></div>");
        sb.append("</div>");
        sb.append("<div class='form right'>");
        sb.append("  <div class='fld'><label>Passcode:</label><div>{passcode}&nbsp;</div></div>");
        sb.append("  <div class='fld'><label>Tutoring:</label><div>{tutoring-state}&nbsp;</div></div>");
        sb.append("</div>");        
        sb.append("</div>");

        template = XTemplate.create(sb.toString());
        html = new HTML();
    }
    
    
    protected void getStudentActivityRPC(final ListStore <StudentActivityModel> store, StudentModel sm) {
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
        s.getStudentActivity(sm, new AsyncCallback <List<StudentActivityModel>>() {

            public void onSuccess(List<StudentActivityModel> list) {
                store.add(list);
                template.overwrite(html.getElement(), Util.getJsObject(studentModel));
                html.setVisible(true);
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }    
    
}


