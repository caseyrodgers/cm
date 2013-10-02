package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_core.client.event.ForceSystemSyncCheckEvent;
import hotmath.gwt.cm_core.client.util.DateUtils4Gwt;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.TurnInAssignmentAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.assignment.AssignmentProblemListPanel.AssignmentProblemListCallback;
import hotmath.gwt.cm_tools.client.ui.assignment.AssignmentStudentTutorAndShowWorkPanel.AssignmentStudentTutorAndShowWorkPanelCallback;
import hotmath.gwt.cm_tools.client.ui.assignment.event.StudentAssignmentViewerActivatedAction;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;

/** 
 *
 * Container for editing an Assignment.   
 * 
 * Has a list/grid of problems which when selected show the tutor/whiteboard
 * of the current assignment problem.
 * 
 * Fires AssignmentProblemLoadedEvent when a new problem is loaded.
 * 
 * Has a head showing relevant data for assignment.
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
    
    
    AssignmentStudentTutorAndShowWorkPanel _assignmentTutorAndShowWorkPanel;
    AssignmentProblemListPanel _problemListPanel; 
    
    BorderLayoutContainer _main = new BorderLayoutContainer();
    
    static StudentAssignmentViewerPanel __lastInstance;
    int assignKeyToOpen;
    private CallbackOnComplete callback;
    
    public StudentAssignmentViewerPanel(int assignKeyToOpen, String pid, final CallbackOnComplete callback) {
        __lastInstance = this;
        this.callback = callback;
        this.assignKeyToOpen = assignKeyToOpen;

        getHeader().setVisible(false);
        addTool(new GotoNextAnnotationButton());
        
        _problemListPanel = new AssignmentProblemListPanel(new AssignmentProblemListCallback() {
            @Override
            public void problemSelected(String title, StudentProblemDto problem) {
                loadTutorProblemStatement(title, problem);
            }
            
            @Override
            public boolean showStatus() {
                if(CmShared.getQueryParameter("debug") != null) {
                    return true;
                }
                else {
                    return false;
                }
            }

            @Override
            public boolean hasUnseenAnnotation(StudentProblemDto problem) {
                return UserInfo.getInstance().isUnreadAssignmentAnnotation(_studentAssignment.getAssignment().getAssignKey(), problem.getProblem().getPid());
            }
        });
        
        BorderLayoutData bData = new BorderLayoutData(170);
        bData.setCollapsible(true);
        bData.setSplit(true);
        _main.setNorthWidget(createHeaderPanel(),bData);

        addStyleName("StudentAssignmentViewerPanel");
        setWidget(_main);

        readAssignmentFromServer(assignKeyToOpen, pid);
        
        CmRpcCore.EVENT_BUS.fireEvent(new StudentAssignmentViewerActivatedAction());
    }

    private void loadTutorProblemStatement(String title, StudentProblemDto problem) {
        Log.debug("Load", "loadTutorProblemStatement: " + problem);
        
        // create new each time
        int uid = UserInfoBase.getInstance().getUid();
        int assignKey =_studentAssignment.getAssignment().getAssignKey();
        _assignmentTutorAndShowWorkPanel = new AssignmentStudentTutorAndShowWorkPanel(title, uid, _studentAssignment,problem, new AssignmentStudentTutorAndShowWorkPanelCallback() {
            
            @Override
            public void tutorWidgetValueUpdated(String value, boolean correct) {
                Log.debug("Widget Update", "StudentAssignmentViewerPanel: " + value);
                _problemListPanel.tutorWidgetValueChanged(value, correct);
                
                /** Update grade textfield with current grade */
                _grade.setValue(GradeBookUtils.getHomeworkGrade(_studentAssignment.getStudentStatuses().getAssigmentStatuses()));
            }

            @Override
            public void whiteboardUpdated() {
                _problemListPanel.whiteboardSubmitted();
            }
        });
        _tutorArea.setCenterWidget(_assignmentTutorAndShowWorkPanel);
        _tutorArea.forceLayout();
    }
    
    BorderLayoutContainer _tutorArea;
    private IsWidget createAssignmentDisplayPanel() {
        _tutorArea = new BorderLayoutContainer();

        _tutorArea.setCenterWidget(new DefaultGxtLoadingPanel("Choose a problem"));
        

        BorderLayoutData bData = new BorderLayoutData(300);
        bData.setSplit(true);
        bData.setCollapsible(true);
        _tutorArea.setWestWidget(_problemListPanel,bData);
        
        return _tutorArea;
    }
    
    TextField _dueDate;
    TextArea _comments;
    TextField _grade;
    FieldLabel _gradeField;
    TextButton _turnInButton, _nextAnnotation;
    TextField _dateTurnedIn;
    
    private IsWidget createHeaderPanel() {
        
        BorderLayoutContainer header = new BorderLayoutContainer();
        VerticalLayoutContainer headerLeft = new VerticalLayoutContainer();
        header.addStyleName("header");
        
        _comments = new TextArea();
        _comments.setReadOnly(true);

        _dueDate = new TextField();
        _dueDate.setReadOnly(true);

        headerLeft.add(new MyFieldLabel(_dueDate,"Due Date",75,100));
        
        _dateTurnedIn = new TextField();
        _dateTurnedIn.setReadOnly(true);
        headerLeft.add(new MyFieldLabel(_dateTurnedIn, "Turned In",75,100));

        
//        _assignmentStatus = new TextField();        
//        _assignmentStatus.setReadOnly(true);
//        headerLeft.add(new MyFieldLabel(_assignmentStatus,"Status",75,75));
        
        
        _grade = new TextField();
        _grade.setReadOnly(true);
        _gradeField = new MyFieldLabel(_grade,"Score",75,75);
        _gradeField.setVisible(false);
        headerLeft.add(_gradeField);
        

        BorderLayoutData bd = new BorderLayoutData(200);
        bd.setCollapsible(true);
        header.setWestWidget(headerLeft,bd);
        
        FlowLayoutContainer cP = new FlowLayoutContainer();
        cP.setScrollMode(ScrollMode.AUTO);
        cP.add(new MyFieldLabel(_comments,  "Comments",75,350));
        header.setCenterWidget(cP);
        
        HorizontalLayoutContainer buttonBar = new HorizontalLayoutContainer();
        _turnInButton = new TextButton("Turn In This Assignment", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                turnInAssignment();
            }
        });
        _nextAnnotation = new GotoNextAnnotationButton();


        TextButton btnReturn = new TextButton("Exit Assignments");
        btnReturn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                callback.isComplete();
            }
        });
        


        HorizontalLayoutData hData = new HorizontalLayoutData();
        hData.setMargins(new Margins(10,10,0,0));
        buttonBar.add(btnReturn,hData);
        buttonBar.add(_turnInButton,hData);
        buttonBar.add(_nextAnnotation,hData);
        
        
        BorderLayoutData bData = new BorderLayoutData(30);
        bData.setMargins(new Margins(10,0,0,0));
        header.setSouthWidget(buttonBar, bData);
        
        ContentPanel cpHeader = new ContentPanel();
        cpHeader.setWidget(header);
        
        return cpHeader;
    }
    
    private void turnInAssignment() {
        CmMessageBox.confirm("Turn In Assignment",  "Are you sure you want to turn in this assignment?",new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                
                if(yesNo) {
                    new RetryAction<RpcData>() {
                        @Override
                        public void attempt() {
                            TurnInAssignmentAction action = new TurnInAssignmentAction(UserInfoBase.getInstance().getUid(),_studentAssignment.getAssignment().getAssignKey());
                            setAction(action);
                            CmShared.getCmService().execute(action, this);
                        }
    
                        public void oncapture(RpcData data) {
                            CatchupMathTools.setBusy(false);
                            readAssignmentFromServer(_studentAssignment.getAssignment().getAssignKey(),null);
                        }
                    }.register();
                }
            }
        });
    }
    public interface StudentAssignmentViewerPanelProperties extends PropertyAccess<String> {
        ModelKeyProvider<Assignment> assignKey();
        LabelProvider<Assignment> assignmentLabel();
      }

    
    StudentAssignment _studentAssignment;
    boolean assignmentAreaDrawn=false;
    private void loadAssignment(StudentAssignment assignment, String pidToLoad) {
        _dueDate.setValue(DateUtils4Gwt.getPrettyDateString(assignment.getAssignment().getDueDate(), true));
        _studentAssignment = assignment;
        if(!assignmentAreaDrawn) {
            BorderLayoutData bd = new BorderLayoutData();
            bd.setCollapsible(true);
            bd.setSplit(true);
            _main.setCenterWidget(createAssignmentDisplayPanel(),bd);
            assignmentAreaDrawn = true;
            _main.forceLayout();
        }
        _problemListPanel.loadAssignment(assignment, pidToLoad);
        if(assignment.isGraded()) {
            _gradeField.setVisible(true);
        }
        else {
            _gradeField.setVisible(false);
        }
        
        _grade.setValue(GradeBookUtils.getHomeworkGrade(_studentAssignment.getStudentStatuses().getAssigmentStatuses()));
        _comments.setValue(assignment.getAssignment().getComments());
        
        
        if(assignment.isTurnedIn()) {
            _turnInButton.setEnabled(false);
        }
        
        _dateTurnedIn.setValue(DateUtils4Gwt.getPrettyDateString(assignment.getTurnInDate()));
    }

    public void readAssignmentFromServer(final int assignKey, final String pidToLoad) {
        Log.debug("Load Assignment", "Loading: " + assignKey);
        
        CatchupMathTools.setBusy(true);
        
        new RetryAction<StudentAssignment>() {
            @Override
            public void attempt() {
                GetStudentAssignmentAction action = new GetStudentAssignmentAction(UserInfoBase.getInstance().getUid(),assignKey);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(StudentAssignment assignment) {
                CatchupMathTools.setBusy(false);
                loadAssignment(assignment,pidToLoad);
                
                /** Make sure system is in sync after viewing an assingment
                 * 
                 */
                CmRpcCore.EVENT_BUS.fireEvent(new ForceSystemSyncCheckEvent());
            }
        }.register();          
    }
    
}


