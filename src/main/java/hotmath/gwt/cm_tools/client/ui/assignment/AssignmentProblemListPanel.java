package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.event.ShowWorkModifiedHandler;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentProblemStatusAction;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.assignment.event.AssignmentProblemLoadedEvent;
import hotmath.gwt.cm_tools.client.ui.assignment.event.AssignmentProblemLoadedHandler;
import hotmath.gwt.cm_tutor.client.event.ShowWorkModifiedEvent;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
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
import com.sencha.gxt.widget.core.client.info.Info;
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

    public interface ProblemListPanelProperties extends PropertyAccess<String> {
        ModelKeyProvider<StudentProblemDto> pid();

        ValueProvider<StudentProblemDto, String> pidLabel();

        ValueProvider<StudentProblemDto, String> status();

        ValueProvider<StudentProblemDto, Boolean> hasShowWorkAdmin();
    }

    AssignmentProblemListCallback _callBack;
    Grid<StudentProblemDto> _studentProblemGrid;

    public AssignmentProblemListPanel(AssignmentProblemListCallback callback) {
        this._callBack = callback;

        setHeadingHtml("Problems in Assignment");
        addTool(createNextProblemButton());
        
        ProblemListPanelProperties props = GWT.create(ProblemListPanelProperties.class);

        List<ColumnConfig<StudentProblemDto, ?>> l = new ArrayList<ColumnConfig<StudentProblemDto, ?>>();

        ColumnConfig<StudentProblemDto, String> labelCol = new ColumnConfig<StudentProblemDto, String>(
                props.pidLabel(), 50, "Problem");
        l.add(labelCol);

        ColumnConfig<StudentProblemDto, String> labelStatus = new ColumnConfig<StudentProblemDto, String>(
                props.status(), 100, "Status");
        l.add(labelStatus);

        ColumnModel<StudentProblemDto> cm = new ColumnModel<StudentProblemDto>(l);

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

        _studentProblemGrid.getSelectionModel().addSelectionChangedHandler(
                new SelectionChangedHandler<StudentProblemDto>() {
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
                        return "assign-showwork-admin";
                    }
                }
                return null;
            }

            @Override
            public String getColStyle(StudentProblemDto model,
                    ValueProvider<? super StudentProblemDto, ?> valueProvider, int rowIndex, int colIndex) {
                return null;
            }
        });
        
        setWidget(_studentProblemGrid);

        __lastInstance = this;
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
    
    private void moveToNextIncompleteProblem() {
        StudentProblemDto selected = _studentProblemGrid.getSelectionModel().getSelectedItem();
        for(StudentProblemDto s: _studentProblemGrid.getStore().getAll()) {
            if(!s.isComplete() && selected != s) {
                _studentProblemGrid.getSelectionModel().select(s, false);
                return;
            }
        }
        
        Info.display("Assignment Complete", "There are no unanswered problems.");
    }

    private void loadProblemStatement(StudentProblemDto studentProb) {

        _callBack.problemSelected(studentProb.getPidLabel(), studentProb.getProblem());

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
    private void whiteboardUpdated() {

        StudentProblemDto prob = _studentProblemGrid.getSelectionModel().getSelectedItem();
        if (prob != null) {

            switch (_assignmentProblem.getProblemType()) {
            case WHITEBOARD:

                if (prob.getStatus().equalsIgnoreCase("Viewed")) {
                    // update the problem type to current type
                    prob.getProblem().setProblemType(_assignmentProblem.getProblemType());
                    prob.setStatus("Pending");

                    _studentProblemGrid.getStore().update(prob);

                    saveAssignmentProblemStatusToServer(prob);
                    break;
                }

            case INPUT_WIDGET:
            case MULTI_CHOICE:
                // whiteboard entry does not change status
                break;

            default:
                CmLogger.debug("AssignmentProblemListPanel: unknown problem type: "
                        + _assignmentProblem.getProblemType());
            }

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
                SaveAssignmentProblemStatusAction action = new SaveAssignmentProblemStatusAction(UserInfoBase
                        .getInstance().getUid(), _assignment.getAssignment().getAssignKey(), prob.getPid(), status);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                CmLogger.debug("Assignment problem status saved: " + data);
            }
        }.register();
    }

    private ProblemType extractProblemType(StudentProblemDto prob) {
        for (StudentProblemDto p : _assignment.getAssigmentStatuses()) {
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

    StudentAssignment _assignment;

    public void loadAssignment(StudentAssignment assignment) {
        _assignment = assignment;
        _studentProblemGrid.getSelectionModel().setSelection(new ArrayList<StudentProblemDto>());
        try {
            _studentProblemGrid.getStore().clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
        _studentProblemGrid.getStore().addAll(assignment.getAssigmentStatuses());

        // select first, if available
        if (_studentProblemGrid.getStore().size() > 0) {
            _studentProblemGrid.getSelectionModel().select(_studentProblemGrid.getStore().get(0), false);
        }
    }

    public interface AssignmentProblemListCallback {
        void problemSelected(String title, ProblemDto problem);
    }

    static {
        CmRpc.EVENT_BUS.addHandler(ShowWorkModifiedEvent.TYPE, new ShowWorkModifiedHandler() {
            @Override
            public void whiteboardUpdated(ShowWorkPanel showWorkPanel) {
                Log.debug("Whiteboard updated, update the associated problem");
                __lastInstance.whiteboardUpdated();
            }
        });

        CmRpc.EVENT_BUS.addHandler(AssignmentProblemLoadedEvent.TYPE, new AssignmentProblemLoadedHandler() {
            @Override
            public void assignmentProblemLoaded(AssignmentProblem assProb) {
                __lastInstance.setAssignmentLoaded(assProb);
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
