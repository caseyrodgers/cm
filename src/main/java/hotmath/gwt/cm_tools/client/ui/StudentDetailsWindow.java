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
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Util;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;

public class StudentDetailsWindow extends Window {
    
    StudentModel studentModel;
    private HTML html;
    private XTemplate template;
    private ListStore<StudentActivityModel> store;
    
    public StudentDetailsWindow(StudentModel studentModel) {
        this.studentModel = studentModel;
        setSize(550,300);
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
        grid.setWidth(550);
        grid.setHeight(200);
        
        setTopComponent(studentInfoPanel());
        add(grid);
                
        Button btnClose = new Button("Close");
        btnClose.addSelectionListener(new SelectionListener() {
            public void componentSelected(ComponentEvent ce) {
                close();
            }
        });
        setButtonAlign(HorizontalAlignment.RIGHT);  
        addButton(btnClose);
        
        
        getStudentActivityRPC(store, studentModel);
        
        setVisible(true);
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
    
    private HorizontalPanel studentInfoPanel() {
        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(7);
        hp.setHeight(100);
        hp.setBorders(false);
        
        defineStudentInfoTemplate();
        
        hp.add(html);
        return hp;
    }    
    
    private void defineStudentInfoTemplate() {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class='student-detail-info'>");
        sb.append("<div class='form left'>");
        sb.append("  <div class='fld'><label>Group:</label><div>{group}</div></div>");
        sb.append("  <div class='fld'><label>Program:</label><div>{program}</div></div>");
        sb.append("  <div class='fld'><label>Pass %:</label><div> {pass-percent}</div></div>");
        sb.append("  <div class='fld'><label>Expires:</label><div> {expiration-date}</div></div>");
        sb.append("  <div class='fld'><label>Live Tutoring:</label><div>{has-tutoring}</div></div>");
        sb.append("</div>");
        sb.append("<div class='form right'>");
        sb.append("  <div class='fld'><label>Passcode:</label><div>{passcode}</div></div>");
        sb.append("  <div class='fld'><label>Tutoring:</label><div>{tutoring-state}</div></div>");
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


