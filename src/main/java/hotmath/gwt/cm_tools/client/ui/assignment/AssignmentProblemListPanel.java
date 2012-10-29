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
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

/** Problem listing for a single Assignment
 * 
 * 
 * @author casey
 *
 */
public class AssignmentProblemListPanel extends SimpleContainer {
    
    static AssignmentProblemListPanel __lastInstance;
    
    public interface ProblemListPanelProperties extends PropertyAccess<String> {
        ModelKeyProvider<StudentProblemDto> pid();
        ValueProvider<StudentProblemDto, String> pidLabel();
        ValueProvider<StudentProblemDto, String> status();
      }
    
    
    AssignmentProblemListCallback _callBack;
    Grid<StudentProblemDto> _grid;
    
    public AssignmentProblemListPanel(AssignmentProblemListCallback callback) {
        this._callBack = callback;

        ProblemListPanelProperties props = GWT.create(ProblemListPanelProperties.class);

        List<ColumnConfig<StudentProblemDto, ?>> l = new ArrayList<ColumnConfig<StudentProblemDto, ?>>();

        ColumnConfig<StudentProblemDto, String> labelCol = new ColumnConfig<StudentProblemDto, String>(props.pidLabel(),50, "Problem");
        l.add(labelCol);
        
        ColumnConfig<StudentProblemDto, String> labelStatus = new ColumnConfig<StudentProblemDto, String>(props.status(),100, "Status");
        l.add(labelStatus);
        
        ColumnModel<StudentProblemDto> cm = new ColumnModel<StudentProblemDto>(l);        

        ListStore<StudentProblemDto> store = new ListStore<StudentProblemDto>(props.pid());
                
        _grid = new Grid<StudentProblemDto>(store, cm);
        
        _grid.getView().setAutoExpandColumn(labelCol);
        _grid.getView().setStripeRows(true);
        _grid.getView().setColumnLines(true);
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        

        _grid.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                //loadProblem()
            }
        },DoubleClickEvent.getType());
        
        _grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<StudentProblemDto>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<StudentProblemDto> event) {
                if(event.getSelection().size() > 0) {
                    loadProblemStatement(event.getSelection().get(0));
                }
            }
        });

        add(_grid);
        
        
        __lastInstance = this;
    }
    
    private void loadProblemStatement(StudentProblemDto studentProb) {
        
       
        _callBack.problemSelected(studentProb.getPidLabel(), studentProb.getProblem());
        
        if(studentProb.getStatus().equals("Not Viewed")) {
            studentProb.setStatus("Viewed");
            _grid.getStore().update(studentProb);
        }
    }
    
    /** The current problem's whiteboard has been updated
     *  meaning the user has made an entry.  The problem 
     *  should be marked as pending if is a WHITEBOARD type.
     *  
     *  This only applies if the question does not have an input widget. 
     */
    private void whiteboardUpdated() {
        
        StudentProblemDto prob = _grid.getSelectionModel().getSelectedItem();
        if(prob != null) {
            
            switch(_assignmentProblem.getProblemType()) {
                case WHITEBOARD:
                    
                    if(prob.getStatus().equalsIgnoreCase("Viewed")) {
                        // update the problem type to current type
                        prob.getProblem().setProblemType( _assignmentProblem.getProblemType() );
                        prob.setStatus("Pending");
                        
                        _grid.getStore().update(prob);
                        
                        
                        saveAssignmentProblemStatusToServer(prob);
                        break;
                    }
                    
                case INPUT_WIDGET:
                case MULTI_CHOICE:
                    // whiteboard entry does not change status
                    break;
                    
                    default:
                        CmLogger.debug("AssignmentProblemListPanel: unknown problem type: " + _assignmentProblem.getProblemType());
            }
            
            
            
            
        }
    }

    public void tutorWidgetValueChanged(String value, boolean correct) {
        
        StudentProblemDto prob = _grid.getSelectionModel().getSelectedItem();
        if(prob != null) {
            /** Since a widget value has been set, it is either Correct or Incorrect
             * 
             */
            prob.setStatus(correct?"Correct":"Incorrect");
            _grid.getStore().update(prob);
            
            
            saveAssignmentProblemStatusToServer(prob);
        }
    }
    
    
    private void saveAssignmentProblemStatusToServer(final StudentProblemDto prob) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                String status = prob.getStatus();
                SaveAssignmentProblemStatusAction action = new SaveAssignmentProblemStatusAction(UserInfoBase.getInstance().getUid(),_assignment.getAssignment().getAssignKey(),prob.getPid(),status);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                CmLogger.debug("Assignment problem status saved: " + data);
            }
        }.register();   
    }

    private ProblemType extractProblemType(StudentProblemDto prob) {
        for(StudentProblemDto p: _assignment.getAssigmentStatuses()) {
            if(p.getProblem().getPid().equals(prob.getPid())) {
                return p.getProblem().getProblemType();
            }
        }
        return null;
    }

    AssignmentProblem _assignmentProblem;
    /** Track the currently loaded AssigmentProblem
     * 
     * variable is used when determining if a problem is
     * 'complete'.
     *  
     *  Called via EventBus.
     *  
     * @param assProb
     */
    private void setAssignmentLoaded(AssignmentProblem assProb) {
        _assignmentProblem = assProb;
    }

    StudentAssignment _assignment;
    public void loadAssignment(StudentAssignment assignment) {
        _assignment = assignment;
        _grid.getSelectionModel().setSelection(new ArrayList<StudentProblemDto>());
        try {
            _grid.getStore().clear();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        _grid.getStore().addAll(assignment.getAssigmentStatuses());
        
        // select first, if available
        if(_grid.getStore().size() > 0) {
            _grid.getSelectionModel().select(_grid.getStore().get(0), false);
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
        
//        CmRpc.EVENT_BUS.addHandler(TutorWidgetValueChangedEvent.TYPE, new TutorWidgetValueChangedHandler() {
//            @Override
//            public void widgetValueChanged(String value, boolean correct) {
//                Log.debug("Assignment Tutor widget value changed handled (" + correct + "): " + value);
//                __lastInstance.tutorWidgetValueChanged(value, correct);
//            }
//        });

    }

}
