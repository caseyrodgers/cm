package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsForUserAction;
import hotmath.gwt.cm_rpc.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_tools.client.ui.assignment.ProblemListPanel.Callback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;

/** 
 * 
 * 
  Student can click on any non-closed row to work on the assignment,
  and sees a screen with all problem numbers along with current status: (
  Not answered, Correct, Incorrect, or in the case of wb-problems: Answered or Not answered)
  
  Student can click on any problem number and see the problem 
  (along with any previously entered answer) as well as buttons to see the related Video,
   Lesson, Activity and (eventually) Search.

 * @author casey
 *
 */
public class StudentAssignmentViewerPanel extends ContentPanel {
    
    
    StudentAssignmentTutorDisplayPanel tutorPanel = new StudentAssignmentTutorDisplayPanel();
    ProblemListPanel problemListPanel; 
    
    BorderLayoutContainer _main = new BorderLayoutContainer();
    
    TextField _assignmentStatus = new TextField();
    public StudentAssignmentViewerPanel() {
        
        
        getHeader().setVisible(false);
        
        problemListPanel = new ProblemListPanel(new Callback() {
            @Override
            public void problemSelected(ProblemDto problem) {
                loadTutorProblemStatement(problem);
            }
        });
        
        BorderLayoutData bData = new BorderLayoutData(100);
        bData.setSplit(true);
        bData.setCollapsible(true);
        _main.setNorthWidget(createHeaderPanel(),bData);
        _main.setCenterWidget(createTutorDisplayPanel());
        
        addStyleName("StudentAssignmentViewerPanel");
        setWidget(_main);
        
        
        
        readAssignmentNamesFromServer();
    }
    
    
    private void loadTutorProblemStatement(ProblemDto problem) {
        Info.display("Load", "Load problem: " + problem);
        tutorPanel.loadProblem(UserInfoBase.getInstance().getUid(), _studentAssignment.getAssignment().getAssignKey(),problem);
    }

    public void readAssignmentNamesFromServer() {
        new RetryAction<CmList<Assignment>>() {
            @Override
            public void attempt() {
                GetAssignmentsForUserAction action = new GetAssignmentsForUserAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<Assignment> assignments) {
                _assignmentCombo.getStore().addAll(assignments);
            }
        }.register();          
    }
   
    
    private IsWidget createTutorDisplayPanel() {
        
        BorderLayoutContainer tutorArea = new BorderLayoutContainer();
        tutorArea.setCenterWidget(tutorPanel);
        BorderLayoutData bData = new BorderLayoutData(300);
        bData.setSplit(true);
        bData.setCollapsible(true);
        tutorArea.setWestWidget(problemListPanel,bData);
        
        return tutorArea;
    }
    
    ComboBox<Assignment> _assignmentCombo;
    
    private IsWidget createHeaderPanel() {
        FlowLayoutContainer header = new FlowLayoutContainer();
        header.setStyleName("header");

         _assignmentCombo = createAssignmentCombo();
         
         _assignmentStatus.setReadOnly(true);
         
        header.setLayoutData(new MarginData(10));
        header.add(new MyFieldLabel(_assignmentCombo,"Assignment Name"));
        header.add(new MyFieldLabel(_assignmentStatus,"Assignment Status"));
        
        return header;
    }
    
    public interface StudentAssignmentViewerPanelProperties extends PropertyAccess<String> {
        ModelKeyProvider<Assignment> assignKey();
        LabelProvider<Assignment> assignmentName();
      }
    
    private ComboBox<Assignment> createAssignmentCombo() {
        
        StudentAssignmentViewerPanelProperties props = GWT.create(StudentAssignmentViewerPanelProperties.class);
        ListStore<Assignment> assStore = new ListStore<Assignment>(props.assignKey());
   
        ComboBox<Assignment> combo = new ComboBox<Assignment>(assStore, props.assignmentName());
        combo.setToolTip("Select an Assignment to work with.");
        combo.setWidth(400);
        combo.setTypeAhead(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setForceSelection(true);
        
        combo.addSelectionHandler(new SelectionHandler<Assignment>() {
            
            @Override
            public void onSelection(SelectionEvent<Assignment> event) {
                readAssignmentFromServer(event.getSelectedItem());
            }
        });
        combo.setExpanded(true);
        combo.setAllowBlank(false);
        combo.setEmptyText("--Select Assignment--");
        combo.setForceSelection(true);
        
        return combo;
    }

    
    StudentAssignment _studentAssignment;
    private void loadAssignment(StudentAssignment assignment) {
        
        
        _studentAssignment = assignment;
        problemListPanel.loadAssignment(assignment);
        
        _assignmentStatus.setValue(assignment.getStatus());
    }

    private void readAssignmentFromServer(final Assignment assignment) {
        Info.display("Load Assignment", "Loading: " + assignment);
        
        new RetryAction<StudentAssignment>() {
            @Override
            public void attempt() {
                GetStudentAssignmentAction action = new GetStudentAssignmentAction(UserInfoBase.getInstance().getUid(),assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(StudentAssignment assignment) {
                loadAssignment(assignment);
            }
        }.register();          
    }
    
}


class MyFieldLabel extends FieldLabel {
    public MyFieldLabel(Widget widget, String string) {
        super(widget,string);
        
        setLabelWidth(150);
    }
    
}