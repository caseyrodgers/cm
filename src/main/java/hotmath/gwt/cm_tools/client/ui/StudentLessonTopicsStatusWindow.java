package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.LessonItemModel;
import hotmath.gwt.cm_tools.client.model.StudentActivityModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Display Student's Lesson Topic (Prescribed Standards) Status
 *
 * @author bob
 * 
 */
public class StudentLessonTopicsStatusWindow extends CmWindow {

    private StudentModel student;
    private Integer runId;
    private String programName;
    private StudentActivityModel activityModel;
    private Grid<LessonItemModel> limGrid;
    private int width = 400;
    private int height = 300; 

    public StudentLessonTopicsStatusWindow(StudentModel student, StudentActivityModel activityModel) {
    	
        setStyleName("student-lesson-topic-status-window");
        this.student = student;
        this.activityModel = activityModel;
        runId = activityModel.getRunId();
        setSize(width, height);
        setResizable(false);
        super.setModal(true);
        
        programName = activityModel.getProgramDescr();

        setLayout(new BorderLayout());
        StringBuffer sb = new StringBuffer();
        sb.append("For ").append(student.getName());
        if(programName != null)
            sb.append(" in program ").append(programName);
        setHeading(sb.toString());
        
        ListStore<LessonItemModel> store = new ListStore<LessonItemModel>();
        ColumnModel cm = defineColumns();
        
        limGrid = new Grid<LessonItemModel>(store, cm);
        limGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        limGrid.getSelectionModel().setFiresEvents(false);
        limGrid.setStripeRows(true);
        limGrid.setWidth(width - 20);
        limGrid.setHeight(height - 70);

        LayoutContainer cp = new LayoutContainer();
        cp.setLayout(new FitLayout());
        cp.add(limGrid);
        add(cp);
        
        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        closeBtn.setIconStyle("icon-delete");

        addButton(closeBtn);
        setVisible(false);
        
        this.getStudentLessonTopicsRPC(store);
    }
    
    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig topicName = new ColumnConfig();
        topicName.setId(LessonItemModel.NAME_KEY);  
        topicName.setHeader("Standards Covered This Section");  
        topicName.setWidth(240);
        topicName.setSortable(true);
        topicName.setMenuDisabled(true);
        configs.add(topicName);
        
        ColumnConfig prescribed = new ColumnConfig();
        prescribed.setId(LessonItemModel.PRESCRIBED_KEY);  
        prescribed.setHeader("Prescribed for Review");  
        prescribed.setWidth(130);
        prescribed.setSortable(false);
        prescribed.setMenuDisabled(true);
        configs.add(prescribed);       

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    protected void getStudentLessonTopicsRPC(final ListStore <LessonItemModel> store) {
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getLessonItemsForTestRun(runId, new AsyncCallback <List<LessonItemModel>>() {

            public void onSuccess(List<LessonItemModel> list) {
            	System.out.println("list size: " + list.size());
                store.add(list);
                setVisible(true);
            }

            public void onFailure(Throwable caught) {
                String msg = caught.getMessage();
                CatchupMathTools.showAlert(msg);
            }
        });
    }    
}
