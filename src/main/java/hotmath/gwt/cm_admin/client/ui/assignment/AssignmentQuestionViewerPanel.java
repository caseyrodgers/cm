package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentProblemStatusAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentWhiteboardData;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanelCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/** Display tutor/question XML 
 * 
 * NOTE: There is only one due to references to 
 * DOM ids.
 * 
 * @author casey
 *
 */
public class AssignmentQuestionViewerPanel extends ContentPanel {

    
    TabPanel _tabPanel;
    TutorWrapperPanel _tutorPanel;
    ShowWorkPanel2 _showWork;
    SimpleContainer _showWorkWrapper;

    public AssignmentQuestionViewerPanel() {
        _tutorPanel = new TutorWrapperPanel(false, false,false,false,new TutorCallbackDefault(){
            @Override
            public void tutorWidgetCompleteDenied(String inputValue, boolean correct) {
                CmMessageBox.showAlert("Not Allowed", "To provide annotations, write on the whiteboard.");
            }
        });
        _tutorPanel.setReadOnly(true);
        
        
        setHeadingHtml("Problem Statement");
        _tutorPanel.setVisible(false);
        
        _tabPanel = new TabPanel();
        
        final FlowLayoutContainer flowPanel = new FlowLayoutContainer();
        flowPanel.add(_tutorPanel);
        flowPanel.setScrollMode(ScrollMode.AUTO);
        
        _tabPanel.add(flowPanel,new TabItemConfig("Problem"));

        _showWorkWrapper = new SimpleContainer();
        _showWorkWrapper.setWidget(new Label("Show Work loading ..."));
        _tabPanel.add(_showWorkWrapper, new TabItemConfig("Show Work"));
        
        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                if(_tabPanel.getActiveWidget() != flowPanel) {
                    setupShowWorkIfRequired();
                }
                
                
                /** Make sure the tutor/whiteboard are sized correctly */
                CmRpcCore.EVENT_BUS.fireEvent(new  WindowHasBeenResizedEvent());
            }
        });
        
        setWidget(_tabPanel);
    }
    
    
    private void saveAssignmentProblemStatusToServer(final StudentProblemDto prob) {
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                String status = prob.getStatus();
                int thisUserUid=_assignmentProblem.getUserId();
                SaveAssignmentProblemStatusAction action = new SaveAssignmentProblemStatusAction(thisUserUid,_assignmentProblem.getAssignKey(),prob.getPid(),status);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                CmLogger.debug("Assignment problem status saved: " + data);
            }
        }.register();   
    }

    /** Lazy initialize show work, call load async
     * after whiteboard has been fully loaded. 
     */
    private void setupShowWorkIfRequired() {
        
        if(_showWork == null) {
            _showWork = new ShowWorkPanel2(new ShowWorkPanelCallbackDefault(){
                @Override
                public void showWorkIsReady() {
                    loadShowWork();
                    
                    _showWork.setAsTeacherMode(true);
                    forceLayout();
                }
                
                @Override
                public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType,
                        String data) {
                        return new SaveAssignmentWhiteboardDataAction(_assignmentProblem.getUserId(),_assignmentProblem.getAssignKey(), _assignmentProblem.getInfo().getPid(),commandType, data, true);        
                }
            });
            
            _showWorkWrapper.setWidget(_showWork);
            forceLayout();
        }
        else {
            _showWork.loadWhiteboard(new CmArrayList<WhiteboardCommand>());
            loadShowWork();
        }
    }
    
    private void loadShowWork() {
        Log.info("Loading Show Work");

        CmBusyManager.setBusy(true);
        
        // always use zero for run_id
        GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(_assignmentProblem.getUserId(), _assignmentProblem.getInfo().getPid(), _assignmentProblem.getAssignKey());
        CmTutor.getCmService().execute(action, new AsyncCallback<AssignmentWhiteboardData>() {
            public void onSuccess(AssignmentWhiteboardData data) {
                CmBusyManager.setBusy(false);
                _showWork.loadWhiteboard(data.getCommands());
            }

            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                Log.error("Error getting whiteboard data: " + caught.toString(), caught);
            };
        });
    }
    
    
    AssignmentProblem _assignmentProblem;
    public void viewQuestion(final StudentAssignment studentAssignment, final StudentProblemDto problem) {
        
        _tabPanel.setActiveWidget(_tabPanel.getWidget(0));

        new RetryAction<AssignmentProblem>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(studentAssignment.getUid(),studentAssignment.getAssignment().getAssignKey(),problem.getPid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(AssignmentProblem assignmentProblem) {
                _assignmentProblem = assignmentProblem;
                SolutionInfo solInfo = assignmentProblem.getInfo();
                try {
                    setHeadingHtml(problem.getProblem().getLabel());
                    
                    String variableContext = null;
                    if (solInfo.getContext() != null) {
                        variableContext = solInfo.getContext().getContextJson();
                    }
                    _tutorPanel.externallyLoadedTutor(solInfo, getWidget(), problem.getPid(), null, solInfo.getJs(), solInfo.getHtml(), problem.getProblem().getLabel(), false, false, variableContext);
                    _tutorPanel.setVisible(true);
                    
                    if(assignmentProblem.getLastUserWidgetValue() != null) {
                        _tutorPanel.setTutorWidgetValue(assignmentProblem.getLastUserWidgetValue());
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                finally {
                    CmBusyManager.setBusy(false);
                }

            }
        }.register();
    }
}
