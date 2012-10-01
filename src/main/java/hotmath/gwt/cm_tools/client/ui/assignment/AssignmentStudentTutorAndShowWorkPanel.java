package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.AssignmentTutorPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkProxy;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

/** Shown to students when displaying a single tutor
 * 
 * @author casey
 *
 */
public class AssignmentStudentTutorAndShowWorkPanel extends ContentPanel {

    ShowWorkPanel _showWork;
    AssignmentTutorPanel _tutorPanel = new AssignmentTutorPanel();
    
    public AssignmentStudentTutorAndShowWorkPanel() {
        BorderLayoutContainer container = new BorderLayoutContainer();
        BorderLayoutData bd = new BorderLayoutData(.50);
        bd.setSplit(true);
        bd.setCollapsible(true);
        
        _showWork = createWhiteBoard();
        container.setEastWidget(_showWork,  bd);
        
        bd = new BorderLayoutData();
        bd.setSplit(true);
        bd.setCollapsible(true);
        bd.setMargins(new Margins(5, 10, 5, 5));
        container.setCenterWidget(_tutorPanel, bd);
        setWidget(container);
    }

    
    private ShowWorkPanel createWhiteBoard() {
        return new ShowWorkPanel(new ShowWorkProxy() {
            
            @Override
            public void showWorkIsReady() {
                //Window.alert("Show work is ready, loading data ...");
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                return createWhiteBoardSaveAction(pid, commandType, data);
            }
            
            @Override
            public void windowResized() {
            }
        });
    }
    
    
    int _uid, _assignKey;
    String _pid;
    public void loadAssignmentWhiteboardData(int uid, int assignKey, String pid) {
        _uid = uid;
        _assignKey = assignKey;
        _pid = pid;
        
        // always use zero for run_id
        GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(uid, pid, assignKey);
        CmTutor.getCmService().execute(action, new AsyncCallback<CmList<WhiteboardCommand>>() {
            public void onSuccess(CmList<WhiteboardCommand> commands) {
                _showWork.loadWhiteboard(commands);
            }

            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                System.out.println("Error getting whiteboard data: " + caught.toString());
            };
        });
    }    

    private Action<? extends Response> createWhiteBoardSaveAction(String pid, CommandType comamndType, String commandData) {
        return new SaveAssignmentWhiteboardDataAction(UserInfoBase.getInstance().getUid(),_assignKey, _pid,comamndType, commandData);        
    }

    public void loadProblem(String title, int uid, int assignKey, ProblemDto problem) {
        setHeadingText(title);
        _tutorPanel.loadSolution(uid, assignKey, problem.getPid());

        loadAssignmentWhiteboardData(uid,assignKey,problem.getPid());
    }
}
