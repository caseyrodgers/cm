package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemAnnotation;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.TurnInAssignmentAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.assignment.AssignmentProblemListPanel.AssignmentProblemListCallback;
import hotmath.gwt.cm_tools.client.ui.assignment.AssignmentStudentTutorAndShowWorkPanel.AssignmentStudentTutorAndShowWorkPanelCallback;
import hotmath.gwt.cm_tools.client.ui.assignment.event.StudentAssignmentViewerActivatedAction;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.DateField;
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
    
    public StudentAssignmentViewerPanel(int assignKeyToOpen, final CallbackOnComplete callback) {
        __lastInstance = this;
        this.assignKeyToOpen = assignKeyToOpen;

        getHeader().setVisible(false);
        addTool(new GotoNextAnnotationButton());

        TextButton btnReturn = new TextButton("Return To Your Program");
        btnReturn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                callback.isComplete();
            }
        });
        addTool(btnReturn);
        
        _problemListPanel = new AssignmentProblemListPanel(new AssignmentProblemListCallback() {
            @Override
            public void problemSelected(String title, ProblemDto problem) {
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
            public boolean hasUnseenAnnotation(ProblemDto problem) {
                return UserInfo.getInstance().isUnreadAssignmentAnnotation(_studentAssignment.getAssignment().getAssignKey(), problem.getPid());
            }
        });
        
        BorderLayoutData bData = new BorderLayoutData(170);
        bData.setCollapsible(true);
        bData.setSplit(true);
        _main.setNorthWidget(createHeaderPanel(),bData);

        addStyleName("StudentAssignmentViewerPanel");
        setWidget(_main);

        readAssignmentFromServer(assignKeyToOpen, null);
        
        CmRpc.EVENT_BUS.fireEvent(new StudentAssignmentViewerActivatedAction());
    }
    
    private CenterLayoutContainer createEmptyPanel(String msg) {
        CenterLayoutContainer c = new CenterLayoutContainer();
        c.getElement().setAttribute("style", "background-color: gray");
        String html = "<h1 style='font-size: 25px;color: #666'>" + msg + "</h1>";
        c.add(new HTML(html));
        return c;
    }

    private void loadTutorProblemStatement(String title, ProblemDto problem) {
        Log.debug("Load", "loadTutorProblemStatement: " + problem);
        
        // create new each time
        int uid = UserInfoBase.getInstance().getUid();
        int assignKey =_studentAssignment.getAssignment().getAssignKey();
        _assignmentTutorAndShowWorkPanel = new AssignmentStudentTutorAndShowWorkPanel(title, uid, _studentAssignment.getAssignment(),problem, new AssignmentStudentTutorAndShowWorkPanelCallback() {
            
            @Override
            public void tutorWidgetValueUpdated(String value, boolean correct) {
                Log.debug("Widget Update", "StudentAssignmentViewerPanel: " + value);
                _problemListPanel.tutorWidgetValueChanged(value, correct);
                
                /** Update grade textfield with current grade */
                _grade.setValue(GradeBookUtils.getHomeworkGrade(_studentAssignment.getAssigmentStatuses()));
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

        _tutorArea.setCenterWidget(createEmptyPanel("Choose a problem"));
        

        BorderLayoutData bData = new BorderLayoutData(300);
        bData.setSplit(true);
        bData.setCollapsible(true);
        _tutorArea.setWestWidget(_problemListPanel,bData);
        
        return _tutorArea;
    }
    
    DateField _dueDate;
    TextArea _comments;
    TextField _grade;
    FieldLabel _gradeField;
    TextButton _turnInButton, _nextAnnotation;
    DateField _dateTurnedIn;
    
    private IsWidget createHeaderPanel() {
        
        BorderLayoutContainer header = new BorderLayoutContainer();
        VerticalLayoutContainer headerLeft = new VerticalLayoutContainer();
        header.addStyleName("header");
        
        _comments = new TextArea();
        _comments.setReadOnly(true);

        _dueDate = new DateField();
        _dueDate.setReadOnly(true);

        headerLeft.add(new MyFieldLabel(_dueDate,"Due Date",75,100));
        
        _dateTurnedIn = new DateField();
        _dateTurnedIn.setReadOnly(true);
        headerLeft.add(new MyFieldLabel(_dateTurnedIn, "Turned In",75,100));

        
//        _assignmentStatus = new TextField();        
//        _assignmentStatus.setReadOnly(true);
//        headerLeft.add(new MyFieldLabel(_assignmentStatus,"Status",75,75));
        
        
        _grade = new TextField();
        _grade.setReadOnly(true);
        _gradeField = new MyFieldLabel(_grade,"Score",75,75);
        headerLeft.add(_gradeField);
        

        BorderLayoutData bd = new BorderLayoutData(200);
        bd.setCollapsible(true);
        header.setWestWidget(headerLeft,bd);
        header.setCenterWidget(new FieldLabel(_comments,  "Comments"));
        
        HorizontalLayoutContainer buttonBar = new HorizontalLayoutContainer();
        _turnInButton = new TextButton("Turn In This Assignment", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                turnInAssignment();
            }
        });
        _nextAnnotation = new GotoNextAnnotationButton();
        buttonBar.add(_turnInButton);
        buttonBar.add(_nextAnnotation);
        
        header.setSouthWidget(buttonBar, new BorderLayoutData(30));
        return header;
    }
    
    private void turnInAssignment() {
        CmMessageBox.confirm("Turn In Assignment",  "Are you sure you want to turn in this assignment?",new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
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
        });
    }
    public interface StudentAssignmentViewerPanelProperties extends PropertyAccess<String> {
        ModelKeyProvider<Assignment> assignKey();
        LabelProvider<Assignment> assignmentLabel();
      }

    
    StudentAssignment _studentAssignment;
    boolean assignmentAreaDrawn=false;
    private void loadAssignment(StudentAssignment assignment, String pidToLoad) {
        _dueDate.setValue(assignment.getAssignment().getDueDate());
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
        if(CmShared.getQueryParameter("debug") == null && !assignment.getStatus().equals("Closed")) {
            _gradeField.setVisible(false);
        }
        else {
            _gradeField.setVisible(true);
        }
        
        _grade.setValue(GradeBookUtils.getHomeworkGrade(_studentAssignment.getAssigmentStatuses()));
        _comments.setValue(assignment.getAssignment().getComments());
        
        
        if(assignment.isTurnedIn()) {
            _turnInButton.setEnabled(false);
        }
        
        _dateTurnedIn.setValue(assignment.getTurnInDate());
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
            }
        }.register();          
    }
    
}


class GotoNextAnnotationButton extends TextButton implements SelectHandler {
    int next;
    public GotoNextAnnotationButton() {
        super("Goto New Teacher Annotation");
        addSelectHandler(this);
        setEnabled(false);
        startChecking();
    }
    
    private void checkIt() {
        if(UserInfo.getInstance().getAssignmentMetaInfo() != null && UserInfo.getInstance().getAssignmentMetaInfo().getUnreadAnnotations().size() > 0) {
            setEnabled(true);
        }
        else {
            setEnabled(false);
        }
    }
    private void startChecking() {
        new Timer() {
            @Override
            public void run() {
                checkIt();
            }
        }.scheduleRepeating(10000);
        checkIt();
    }

    @Override
    public void onSelect(SelectEvent event) {
        if(UserInfo.getInstance().getAssignmentMetaInfo() != null) {
            List<ProblemAnnotation> pids = UserInfo.getInstance().getAssignmentMetaInfo().getUnreadAnnotations();
            if(pids.size() > 0) {
                if(next > pids.size()-1) {
                    next = 0;
                }
                
                ProblemAnnotation annotation = pids.get(next);
                StudentAssignmentViewerPanel.__lastInstance.readAssignmentFromServer(annotation.getAssignKey(), annotation.getPid());
                
                next++;
            }
            else {
                CmMessageBox.showAlert("There are no teacher annotations available.");
            }
        }
        
    }
    
}