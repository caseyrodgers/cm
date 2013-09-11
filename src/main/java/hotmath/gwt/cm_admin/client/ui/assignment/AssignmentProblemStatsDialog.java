package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_assignments.client.model.AssignmentRealTimeStatsUsers;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetAssignmentRealTimeStatsUsersAction;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;


public class AssignmentProblemStatsDialog extends GWindow {
    
    private int assignKey;
    private String pid;
    TabPanel tabPanel = new TabPanel();
    BorderLayoutContainer borderContainer = new BorderLayoutContainer();
    Label userName = new Label();

    private MyGrid gridCorrect, gridIncorrect, gridSubmitted, gridUnanswered;
    
    public AssignmentProblemStatsDialog(int assignKey, String pid, String label) {
        super(true);
        setResizable(false);
        this.assignKey = assignKey;
        this.pid = pid;
        setPixelSize(340,  400);
        setHeadingHtml("Assignment Problem Status");
        userName.setText(label);
        
        
        tabPanel.setCloseContextMenu(false);
        
        StudentGridLocalProperties props = GWT.create(StudentGridLocalProperties.class);
        
        List<ColumnConfig<StudentModel, ?>> cols = new ArrayList<ColumnConfig<StudentModel, ?>>();
        cols.add(new ColumnConfig<StudentModel, String>(props.name(), 250, "Student Name"));
        ColumnModel<StudentModel> probColModel = new ColumnModel<StudentModel>(cols);
        
        gridCorrect = new MyGrid(new ListStore<StudentModel>(props.id()), probColModel);
        tabPanel.add(gridCorrect,  new TabItemConfig("Correct", false));
        gridIncorrect = new MyGrid(new ListStore<StudentModel>(props.id()), probColModel);
        tabPanel.add(gridIncorrect,  new TabItemConfig("Incorrect", false));        
        gridSubmitted = new MyGrid(new ListStore<StudentModel>(props.id()), probColModel);
        tabPanel.add(gridSubmitted,  new TabItemConfig("Submitted", false));        
        gridUnanswered = new MyGrid(new ListStore<StudentModel>(props.id()), probColModel);
        tabPanel.add(gridUnanswered,  new TabItemConfig("Unanswered", false));        
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        
        String html = "<div style='margin: 10px;'><b>Problem:</b> <span>" + label + "</span></div>";
        flow.add(new HTML(html));
        
        borderContainer.setNorthWidget(flow, new BorderLayoutData(50));
        borderContainer.setCenterWidget(tabPanel);
        
        setWidget(borderContainer);
        
        readDataFromServer();
        
        setVisible(true);
    }

    protected void loadSelectedStudentGrading() {
        
        final StudentModel sm = gridUnanswered.getSelectionModel().getSelectedItem();
        if(sm == null) {
            return;
        }
        
        new RetryAction<StudentAssignment>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CmServiceAsync s = CmShared.getCmService();
                GetStudentAssignmentAction action = new GetStudentAssignmentAction(sm.getUid(), assignKey);
                setAction(action);
                CmLogger.info("AccountInfoPanel: reading admin info RPC");
                s.execute(action,this);
            }

            public void oncapture(StudentAssignment ai) {
                new GradeBookDialog(ai,  new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        Log.info("Grading of user complete");
                    }
                });
                
                forceLayout();
                CmBusyManager.setBusy(false);
            }
        }.register();            
        
    }

    private void readDataFromServer() {
        CmBusyManager.showLoading(true);
        new RetryAction<AssignmentRealTimeStatsUsers>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CmServiceAsync s = CmShared.getCmService();
                GetAssignmentRealTimeStatsUsersAction action = new GetAssignmentRealTimeStatsUsersAction(assignKey, pid);
                setAction(action);
                CmLogger.info("AccountInfoPanel: reading admin info RPC");
                s.execute(action,this);
            }

            public void oncapture(AssignmentRealTimeStatsUsers ai) {
                updateUi(ai);
                CmBusyManager.setBusy(false);
            }
        }.register();            
    }
    
    
    protected void updateUi(AssignmentRealTimeStatsUsers ai) {
        gridCorrect.getStore().addAll(ai.getCorrect());
        gridIncorrect.getStore().addAll(ai.getIncorrect());
        gridSubmitted.getStore().addAll(ai.getSubmitted());
        gridUnanswered.getStore().addAll(ai.getUnanswered());
    }


    public interface StudentGridLocalProperties extends PropertyAccess<String> {
        @Path("uid")
        ModelKeyProvider<StudentModel> id();

        ValueProvider<StudentModel, String> name();
    }    

}


class MyGrid extends Grid<StudentModel> {

    public MyGrid(ListStore<StudentModel> listStore, ColumnModel<StudentModel> probColModel) {
        super(listStore, probColModel);
    }
    
}