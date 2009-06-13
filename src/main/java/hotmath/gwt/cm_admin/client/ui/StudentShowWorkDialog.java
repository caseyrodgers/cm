package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudentShowWorkModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.DataList;
import com.extjs.gxt.ui.client.widget.DataListItem;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.user.client.rpc.AsyncCallback;

/** Display interface to allow user to show work
 * 
 *  Allow for general restriction of problems
 *  within a single student
 *  
 *  
 * @author casey
 *
 */
public class StudentShowWorkDialog extends Window {
    
    StudentModel student;
    public StudentShowWorkDialog(StudentModel student) {
        this.student = student;
        createGui();
    }
    
    private void createGui() {
        setSize(100,300);
        setResizable(true);
        setLayout(new BorderLayout());
        LayoutContainer head  = new LayoutContainer();
        head.add(new Html("<div style='margin: 10px;'>Click on a problem below to view the associated work</div>"));
        add(head, new BorderLayoutData(LayoutRegion.NORTH, 50));
        setHeading("Show Work for " + student.getName());
        setVisible(true);
        getStudentShowWorkRPC();
    }
    
    
    private void createDataList(List<StudentShowWorkModel> showWork) {
        final DataList list2 = new DataList();  
        list2.setFlatStyle(true);  
        
        
        
        Listener<ComponentEvent> l = new Listener<ComponentEvent>() {  
            public void handleEvent(ComponentEvent ce) {  
              DataList l = (DataList) ce.getComponent();  
              int count = l.getSelectedItems().size();  
              
              String pid = l.getSelectedItem().getData("pid");
              Info.display("Loading Solution", "Loading solution: " + pid);
              
              new StudentShowWorkDisplayWindow(student, pid);
            }  
          };  
          
        list2.addListener(Events.SelectionChange, l);
        for (StudentShowWorkModel work : showWork) {  
          DataListItem item = new DataListItem();  
          item.setText(work.getLabel() + " " + work.getViewTime());
          item.setData("pid",work.getPid());
          list2.add(item);  
        }  
        
        add(list2, new BorderLayoutData(LayoutRegion.CENTER));
        layout();
    }
    
    
    Grid<StudentShowWorkModel> grid;
    private ListStore<StudentShowWorkModel> store;
    private Grid createProblemsWithShowWork() {

        
        store = new ListStore<StudentShowWorkModel>();
        ColumnModel cm = defineColumns();
        
        grid = new Grid<StudentShowWorkModel>(store, cm);
        grid.setStyleName("student-show-work-grid");
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.setWidth(530);
        grid.setHeight(190);

        
        getStudentShowWorkRPC();
        
        return grid;
    }
    
    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig label = new ColumnConfig();
        label.setId(StudentShowWorkModel.LABEL_KEY);  
        label.setHeader("Problem");  
        label.setWidth(75);
        label.setSortable(true);
        configs.add(label);
        
        ColumnConfig lastWork = new ColumnConfig();
        lastWork.setId(StudentShowWorkModel.VIEW_TIME_KEY);  
        lastWork.setHeader("Last Worked On");  
        lastWork.setWidth(105);
        lastWork.setSortable(true);
        configs.add(lastWork);
        
        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
    
    protected void getStudentShowWorkRPC() {
        RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
        s.getStudentShowWork(student.getUid(), new AsyncCallback <List<StudentShowWorkModel>>() {

            public void onSuccess(List<StudentShowWorkModel> list) {
                createDataList(list);
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathAdmin.showAlert(msg);
            }
        });
    }        
}
