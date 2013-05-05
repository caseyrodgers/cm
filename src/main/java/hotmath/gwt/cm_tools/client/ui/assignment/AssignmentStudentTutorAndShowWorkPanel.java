package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.ui.assignment.AssignmentTutorPanel.AssignmentTutorPanelCallback;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallbackDefault;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

/** Manages the student's assignment view of
 *  a single tutor and its associated whiteboard
 * 
 * 
 * @author casey
 *
 */
public class AssignmentStudentTutorAndShowWorkPanel extends ContentPanel {

    ShowWorkPanel _showWork;
    AssignmentTutorPanel _tutorPanel;
    AssignmentStudentTutorAndShowWorkPanelCallback _callBack;
    StudentProblemDto _problem;
    
    interface AssignmentStudentTutorAndShowWorkPanelCallback {
         void tutorWidgetValueUpdated(String value, boolean correct);

        void whiteboardUpdated();
    }
    
    BorderLayoutContainer _container;
    private ToggleButton _showWhiteboardButton;
    public AssignmentStudentTutorAndShowWorkPanel(String title, final int uid, StudentAssignment assignment, final StudentProblemDto problem, AssignmentStudentTutorAndShowWorkPanelCallback callBack) {
        _callBack = callBack;
        _uid = uid;
        _assignKey = assignment.getAssignment().getAssignKey();
        _problem = problem;

        
        /** create callback to pass along when tutor widget value changed
         *  
         */
        boolean iEditable = assignment.isEditable();
        /** Multi Choice problems do not have steps so
         *  we cannot show the buttonbar.
         * 
         */
        _tutorPanel = new AssignmentTutorPanel(iEditable,problem.getProblem(),assignment.isGraded(), new AssignmentTutorPanelCallback() {
            @Override
            public void tutorWidgetValueUpdated(String value, boolean correct) {
                _callBack.tutorWidgetValueUpdated(value, correct);
            }

            @Override
            public void whiteboardSubmitted() {
                _callBack.whiteboardUpdated();
            }
        });
        
        _container = new BorderLayoutContainer();
        
        BorderLayoutData bd = new BorderLayoutData();
        bd.setSplit(true);
        bd.setCollapsible(true);
        bd.setMargins(new Margins(5, 10, 5, 5));
        _container.setCenterWidget(_tutorPanel, bd);
        
        _showWhiteboardButton = createShowWhiteboardButton();
        addTool(_showWhiteboardButton);
        addTool(createShowLessonButton());
        
        if(_whiteboardShown || problem.getProblem().getProblemType() == ProblemType.WHITEBOARD) {
            _showWhiteboardButton.setValue(true);
            showWhiteboard();
        }
        
        setWidget(_container);
        
        loadTutor(title, uid, _assignKey, problem);
    }

    static boolean _whiteboardShown;
    ContentPanel _showWorkWrapper;
    private void showWhiteboard() {
        
        _showWork = new ShowWorkPanel(new ShowWorkPanelCallbackDefault() {
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return createWhiteBoardSaveAction(pid, commandType, data);
            }
            
            @Override
            public void showWorkIsReady() {
                loadAssignmentWhiteboardData(_uid, _assignKey, _problem.getPid());
            }
        });

        BorderLayoutData bd = new BorderLayoutData(.50);
        bd.setSplit(true);
        bd.setCollapsible(true);

        
        _showWorkWrapper = new ContentPanel();
        _showWorkWrapper.setWidget(_showWork);
        _container.setEastWidget(_showWorkWrapper,  bd);
        
        _container.forceLayout();
        
        _whiteboardShown=true;
    }
    
    private Widget createShowLessonButton() {
        return new ProblemResourcesButton(_problem.getProblem());
    }
    
    private ToggleButton createShowWhiteboardButton() {
        final ToggleButton showLesson = new ToggleButton("Show/Hide Whiteboard");
        showLesson.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(showLesson.getValue()) {
                    showWhiteboard();
                    loadAssignmentWhiteboardData(_uid,_assignKey,_problem.getPid());
                }
                else {
                    _container.remove(_showWorkWrapper);
                    forceLayout();
                    
                    _whiteboardShown=false;
                }
            }
        });
        return showLesson;
    }
    
    
    int _uid, _assignKey;
    private void loadAssignmentWhiteboardData(int uid, int assignKey, String pid) {
        // always use zero for run_id
        GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(uid, pid, assignKey);
        CmTutor.getCmService().execute(action, new AsyncCallback<AssignmentWhiteboardData>() {
            public void onSuccess(final AssignmentWhiteboardData data) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        if(_showWork != null) {
                            _showWork.loadWhiteboard(data.getCommands());                    }
                        }
                });
            }

            public void onFailure(Throwable caught) {
                Log.error("Error getting whiteboard data: " + caught.toString(), caught);
            };
        });
    }    

    private Action<? extends Response> createWhiteBoardSaveAction(String pid, CommandType comamndType, String commandData) {
        return new SaveAssignmentWhiteboardDataAction(_uid,_assignKey, _problem.getPid(),comamndType, commandData, false);        
    }

    private void loadTutor(String title, int uid, int assignKey, StudentProblemDto problem) {
        setHeadingText(title);
        _tutorPanel.loadSolution(uid, assignKey, problem);

        loadAssignmentWhiteboardData(uid,assignKey,problem.getPid());
    }
}
