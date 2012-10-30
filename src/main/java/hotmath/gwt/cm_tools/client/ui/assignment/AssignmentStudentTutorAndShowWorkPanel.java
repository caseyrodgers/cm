package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_tools.client.ui.assignment.AssignmentTutorPanel.AssignmentTutorPanelCallback;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkPanelCallbackDefault;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

/** Manages the student's assignment view of the tutor and whiteboard
 * 
 * 
 * @author casey
 *
 */
public class AssignmentStudentTutorAndShowWorkPanel extends ContentPanel {

    ShowWorkPanel _showWork;
    AssignmentTutorPanel _tutorPanel;
    AssignmentStudentTutorAndShowWorkPanelCallback _callBack;
    
    interface AssignmentStudentTutorAndShowWorkPanelCallback {
         void tutorWidgetValueUpdated(String value, boolean correct);
    }
    
    public AssignmentStudentTutorAndShowWorkPanel(String title, final int uid, Assignment assignment, final ProblemDto problem, AssignmentStudentTutorAndShowWorkPanelCallback callBack) {
        _callBack = callBack;
        _uid = uid;
        _assignKey = assignment.getAssignKey();
        _pid = problem.getPid();

        
        /** create callback to pass along when tutor widget value changed
         *  
         */
        _tutorPanel = new AssignmentTutorPanel(assignment.isEditable(),new AssignmentTutorPanelCallback() {
            @Override
            public void tutorWidgetValueUpdated(String value, boolean correct) {
                _callBack.tutorWidgetValueUpdated(value, correct);
            }
        });
        
        BorderLayoutContainer container = new BorderLayoutContainer();
        BorderLayoutData bd = new BorderLayoutData(.50);
        bd.setSplit(true);
        bd.setCollapsible(true);
        
        _showWork = new ShowWorkPanel(new ShowWorkPanelCallbackDefault() {
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return createWhiteBoardSaveAction(pid, commandType, data);
            }
            
            @Override
            public void showWorkIsReady() {
                loadAssignmentWhiteboardData(uid, _assignKey, problem.getPid());
            }
        });
        container.setEastWidget(_showWork,  bd);
        
        bd = new BorderLayoutData();
        bd.setSplit(true);
        bd.setCollapsible(true);
        bd.setMargins(new Margins(5, 10, 5, 5));
        container.setCenterWidget(_tutorPanel, bd);
        setWidget(container);
        
        loadTutor(title, uid, _assignKey, problem);
    }

    
    int _uid, _assignKey;
    String _pid;
    private void loadAssignmentWhiteboardData(int uid, int assignKey, String pid) {
        // always use zero for run_id
        GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(uid, pid, assignKey);
        CmTutor.getCmService().execute(action, new AsyncCallback<CmList<WhiteboardCommand>>() {
            public void onSuccess(final CmList<WhiteboardCommand> commands) {
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        _showWork.loadWhiteboard(commands);                    }
                });
            }

            public void onFailure(Throwable caught) {
                Log.error("Error getting whiteboard data: " + caught.toString(), caught);
            };
        });
    }    

    private Action<? extends Response> createWhiteBoardSaveAction(String pid, CommandType comamndType, String commandData) {
        return new SaveAssignmentWhiteboardDataAction(_uid,_assignKey, _pid,comamndType, commandData, true);        
    }

    private void loadTutor(String title, int uid, int assignKey, ProblemDto problem) {
        setHeadingText(title);
        _tutorPanel.loadSolution(uid, assignKey, problem.getPid());

        loadAssignmentWhiteboardData(uid,assignKey,problem.getPid());
    }
}
