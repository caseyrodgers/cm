package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm.client.ui.StudentAssignmentButton;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentProblemStatusAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentUserInfo;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.assignment.event.AssignmentProblemLoadedEvent;
import hotmath.gwt.cm_tools.client.ui.assignment.event.AssignmentProblemLoadedHandler;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.event.AssignmentsUpdatedEvent;
import hotmath.gwt.shared.client.event.AssignmentsUpdatedHandler;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/**
 * Problem listing for a single Assignment
 * 
 * 
 * @author casey
 * 
 */
public class AssignmentProblemListPanel extends ContentPanel {

    static AssignmentProblemListPanel __lastInstance;

    AssignmentProblemListCallback _problemListCallback;
    Grid<StudentProblemDto> _studentProblemGrid;
    public AssignmentProblemListPanel(AssignmentProblemListCallback callback) {
        this._problemListCallback = callback;

        setHeadingHtml("Problems in Assignment");
        addTool(createNextProblemButton());
        getButtonBar().add(createAnnotationLedgend());

        ProblemListPanelProperties props = GWT.create(ProblemListPanelProperties.class);

        List<ColumnConfig<StudentProblemDto, ?>> columns = new ArrayList<ColumnConfig<StudentProblemDto, ?>>();

        ColumnConfig<StudentProblemDto, Integer> problemNumberCol = new ColumnConfig<StudentProblemDto, Integer>(props.problemNumberOrdinal(), 25, "");

        columns.add(problemNumberCol);
        ColumnConfig<StudentProblemDto, String> labelCol = new ColumnConfig<StudentProblemDto, String>(props.pidLabel(), 50, "Problem");
        columns.add(labelCol);

        ColumnConfig<StudentProblemDto, String> labelStatus = new ColumnConfig<StudentProblemDto, String>(props.statusForStudent(), 100, "Status");
        columns.add(labelStatus);

        ColumnModel<StudentProblemDto> cm = new ColumnModel<StudentProblemDto>(columns);

        ListStore<StudentProblemDto> store = new ListStore<StudentProblemDto>(props.pid());

        _studentProblemGrid = new Grid<StudentProblemDto>(store, cm);

        _studentProblemGrid.getView().setAutoExpandColumn(labelCol);
        _studentProblemGrid.getView().setStripeRows(true);
        _studentProblemGrid.getView().setColumnLines(true);
        _studentProblemGrid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        _studentProblemGrid.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                // loadProblem()
            }
        }, DoubleClickEvent.getType());

        _studentProblemGrid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<StudentProblemDto>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<StudentProblemDto> event) {
                if (event.getSelection().size() > 0) {
                    loadProblemStatement(event.getSelection().get(0));
                }
            }
        });

        _studentProblemGrid.getView().setViewConfig(new GridViewConfig<StudentProblemDto>() {

            @Override
            public String getRowStyle(StudentProblemDto model, int rowIndex) {
                if (model != null) {
                    if (model.isHasShowWorkAdmin()) {
                        if(_problemListCallback.hasUnseenAnnotation(model)) {
                            return "assign-showwork-admin-unseen";
                        }
                        else {
                            return "assign-showwork-admin";
                        }
                    }
                }
                return null;
            }

            @Override
            public String getColStyle(StudentProblemDto model, ValueProvider<? super StudentProblemDto, ?> valueProvider, int rowIndex, int colIndex) {
                return null;
            }
        });

        setWidget(_studentProblemGrid);

        __lastInstance = this;
    }

    private Widget createAnnotationLedgend() {
        HorizontalPanel lc = new HorizontalPanel();
        lc.add(new HTML("<div style='margin-right: 10px;'><div style='background-color: blue;width: 10px;float: left;'>&nbsp;</div>&nbsp;&nbsp;Notes</div>"));
        lc.add(new HTML("<div><div style='background-color: red;width: 10px;float: left;'>&nbsp;</div>&nbsp;&nbsp;Unread notes</div>"));
        return lc;
    }

    private Widget createNextProblemButton() {
        TextButton b = new TextButton("Next Problem");
        b.setToolTip("Move to the next unanswered problem.");
        b.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                moveToNextIncompleteProblem();
            }
        });
        return b;
    }

    // search from current position to end, then 
    private void moveToNextIncompleteProblem() {
        StudentProblemDto gotoThisOne=null;
        StudentProblemDto selected = _studentProblemGrid.getSelectionModel().getSelectedItem();
        
        gotoThisOne = getGotoThisOne(selected); // search from current position
        if(gotoThisOne == null) {
            gotoThisOne = getGotoThisOne(null); // search from head
        }
        if(gotoThisOne!=null) {
            _studentProblemGrid.getSelectionModel().select(gotoThisOne, false);
        }
        else {
            CmMessageBox.showAlert("Assignment Complete", "There are no unanswered problems.");
        }
    }

    private StudentProblemDto getGotoThisOne(StudentProblemDto selected) {
        StudentProblemDto gotoThisOne=null;

        List<StudentProblemDto> a = _studentProblemGrid.getStore().getAll();
        for (int which=0,count=a.size();which < count;which++) {
            StudentProblemDto p = a.get(which);
            if(selected == null || p == selected) {
                // search from here to end
                for(int i=which+1;i<count;i++) {
                    StudentProblemDto p2 = a.get(i);
                    if(!p2.isComplete()) {
                        gotoThisOne=p2;
                        break;
                    }
                }
                
                if(gotoThisOne!=null) {
                    break;
                }
            }
        }
        
        return gotoThisOne;
    }

    StudentProblemDto _currentProblem;

    private void loadProblemStatement(StudentProblemDto studentProb) {

        if(UserInfo.getInstance().getAssignmentMetaInfo().removeFromUnreadAnnotations(_studentAssignment, studentProb)) {
            _studentProblemGrid.getStore().update(studentProb);
            StudentAssignmentButton.refreshButtonState();
        }
        
        _currentProblem = studentProb;
        String title = studentProb.getProblem().getOrdinalNumber() + ". " + StudentProblemDto.getStudentLabel(studentProb.getPidLabel());
        _problemListCallback.problemSelected(title, studentProb);

        if (studentProb.getStatus().equals("Not Viewed")) {
            studentProb.setStatus("Viewed");
            _studentProblemGrid.getStore().update(studentProb);
        }
    }


    /**
     * The current problem's whiteboard has been updated meaning the user has
     * made an entry. The problem should be marked as pending if is a WHITEBOARD
     * type.
     * 
     * This only applies if the question does not have an input widget.
     */
    public void whiteboardSubmitted() {
        StudentProblemDto prob = _studentProblemGrid.getSelectionModel().getSelectedItem();
        if (prob != null) {
            prob.setStatus("Submitted");
            _studentProblemGrid.getStore().update(prob);
            saveAssignmentProblemStatusToServer(prob);
        }
    }

    public void tutorWidgetValueChanged(String value, boolean correct) {

        StudentProblemDto prob = _studentProblemGrid.getSelectionModel().getSelectedItem();
        if (prob != null) {
            /**
             * Since a widget value has been set, it is either Correct or
             * Incorrect
             * 
             */
            prob.setStatus(correct ? "Correct" : "Incorrect");
            _studentProblemGrid.getStore().update(prob);

            saveAssignmentProblemStatusToServer(prob);
        }
    }

    private void saveAssignmentProblemStatusToServer(final StudentProblemDto prob) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                String status = prob.getStatus();
                SaveAssignmentProblemStatusAction action = new SaveAssignmentProblemStatusAction(UserInfoBase.getInstance().getUid(), _studentAssignment
                        .getAssignment().getAssignKey(), prob.getPid(), status);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                CmLogger.debug("Assignment problem status saved: " + data);
            }
        }.register();
    }

    private ProblemType extractProblemType(StudentProblemDto prob) {
        for (StudentProblemDto p : _studentAssignment.getStudentStatuses().getAssigmentStatuses()) {
            if (p.getProblem().getPid().equals(prob.getPid())) {
                return p.getProblem().getProblemType();
            }
        }
        return null;
    }

    AssignmentProblem _assignmentProblem;

    /**
     * Track the currently loaded AssigmentProblem
     * 
     * variable is used when determining if a problem is 'complete'.
     * 
     * Called via EventBus.
     * 
     * @param assProb
     */
    private void setAssignmentLoaded(AssignmentProblem assProb) {
        _assignmentProblem = assProb;
    }

    StudentAssignment _studentAssignment;

    public void loadAssignment(StudentAssignment assignment, String pidToLoad) {
        _studentAssignment = assignment;
        
        StudentProblemDto selectedItem = _studentProblemGrid.getSelectionModel().getSelectedItem();
        
        // unselect
        _studentProblemGrid.getSelectionModel().setSelection(new ArrayList<StudentProblemDto>());
        try {
            _studentProblemGrid.getStore().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _studentProblemGrid.getStore().addAll(assignment.getStudentStatuses().getAssigmentStatuses());
        if(pidToLoad != null) {
            for(StudentProblemDto sp: _studentProblemGrid.getStore().getAll()) {
                if(sp.getPid().equals(pidToLoad)) {
                    _studentProblemGrid.getSelectionModel().select(sp, false);
                }
            }
        }
        else {
            if(selectedItem != null) {
                _studentProblemGrid.getSelectionModel().select(selectedItem, false);   
            }
            else {
                // select first, if available
                if (_studentProblemGrid.getStore().size() > 0) {
                    _studentProblemGrid.getSelectionModel().select(_studentProblemGrid.getStore().get(0), false);
                }
            }
        }
    }
    
    public void refreshAnnotationMarkings() {
        if(_studentAssignment != null) {
            loadAssignment(_studentAssignment,  null);
            forceLayout();
        }
    }

    public interface AssignmentProblemListCallback {
        void problemSelected(String title, StudentProblemDto studentProb);

        boolean hasUnseenAnnotation(StudentProblemDto problem);

        boolean showStatus();
    }

    static {
        CmRpc.EVENT_BUS.addHandler(AssignmentProblemLoadedEvent.TYPE, new AssignmentProblemLoadedHandler() {
            @Override
            public void assignmentProblemLoaded(AssignmentProblem assProb) {
                __lastInstance.setAssignmentLoaded(assProb);
            }
        });
        
        CmRpc.EVENT_BUS.addHandler(AssignmentsUpdatedEvent.TYPE, new AssignmentsUpdatedHandler() {
            @Override
            public void assignmentsUpdated(AssignmentUserInfo info) {
                __lastInstance.refreshAnnotationMarkings();
            }
        });

        // CmRpc.EVENT_BUS.addHandler(TutorWidgetValueChangedEvent.TYPE, new
        // TutorWidgetValueChangedHandler() {
        // @Override
        // public void widgetValueChanged(String value, boolean correct) {
        // Log.debug("Assignment Tutor widget value changed handled (" + correct
        // + "): " + value);
        // __lastInstance.tutorWidgetValueChanged(value, correct);
        // }
        // });

    }
}


interface ProblemListPanelProperties extends PropertyAccess<String> {
    ModelKeyProvider<StudentProblemDto> pid();
    ValueProvider<StudentProblemDto, String> pidLabel();
    ValueProvider<StudentProblemDto, String> status();
    ValueProvider<StudentProblemDto, String> statusForStudent();
    ValueProvider<StudentProblemDto, Integer> problemNumberOrdinal();
}

