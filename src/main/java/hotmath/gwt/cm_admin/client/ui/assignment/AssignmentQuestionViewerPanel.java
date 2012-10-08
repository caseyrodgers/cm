package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentWhiteboardDataAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel.ShowWorkProxyDefault;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
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
    ShowWorkPanel _showWork;
    SimpleContainer _showWorkWrapper;

    public AssignmentQuestionViewerPanel() {
        _tutorPanel = new TutorWrapperPanel(false,false,false,false,new TutorCallbackDefault());
        setHeadingHtml("Question Display");
        _tutorPanel.setVisible(false);
        
        _tabPanel = new TabPanel();
        
        final FlowLayoutContainer flowPanel = new FlowLayoutContainer();
        flowPanel.add(_tutorPanel);
        flowPanel.setScrollMode(ScrollMode.AUTOY);
        _tabPanel.add(flowPanel,new TabItemConfig("Question"));

        _showWorkWrapper = new SimpleContainer();
        _showWorkWrapper.setWidget(new Label("Show Work loading ..."));
        _tabPanel.add(_showWorkWrapper, new TabItemConfig("Show Work"));
        
        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
            @Override
            public void onSelection(SelectionEvent<Widget> event) {
                if(_tabPanel.getActiveWidget() != flowPanel) {
                    setupShowWorkIfRequired();
                }
            }
        });
        
        setWidget(_tabPanel);
    }

    /** Lazy initialize show work, call load async
     * after whiteboard has been fully loaded. 
     */
    private void setupShowWorkIfRequired() {
        if(_showWork == null) {
            _showWork = new ShowWorkPanel(new ShowWorkProxyDefault(){
                @Override
                public void showWorkIsReady() {
                    loadShowWork();
                    
                    _showWork.setAsTeacherMode(true);
                    forceLayout();
                }
                
                @Override
                public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType,
                        String data) {
                        return new SaveAssignmentWhiteboardDataAction(_assignmentProblem.getUserId(),_assignmentProblem.getAssignKey(), _assignmentProblem.getInfo().getPid(),commandType, data);        
                }
            });
            
            _showWorkWrapper.setWidget(_showWork);
            forceLayout();
        }
        else {
            loadShowWork();
        }
    }
    
    private void loadShowWork() {
        Log.info("Loading Show Work");

        // always use zero for run_id
        GetAssignmentWhiteboardDataAction action = new GetAssignmentWhiteboardDataAction(_assignmentProblem.getUserId(), _assignmentProblem.getInfo().getPid(), _assignmentProblem.getAssignKey());
        CmTutor.getCmService().execute(action, new AsyncCallback<CmList<WhiteboardCommand>>() {
            public void onSuccess(CmList<WhiteboardCommand> commands) {
                _showWork.loadWhiteboard(commands);
            }

            public void onFailure(Throwable caught) {
                Log.error("Error getting whiteboard data: " + caught.toString(), caught);
            };
        });
    }
    
    
    AssignmentProblem _assignmentProblem;
    public void viewQuestion(final StudentAssignment studentAssignment, final ProblemDto problem) {
        
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
                    setHeadingHtml(problem.getLabel());
                    
                    String variableContext = null;
                    if (solInfo.getContext() != null) {
                        variableContext = solInfo.getContext().getContextJson();
                    }
                    _tutorPanel.externallyLoadedTutor(solInfo, getWidget(), problem.getPid(), null, solInfo.getJs(), solInfo.getHtml(), problem.getLabel(), false, false, variableContext);
                    _tutorPanel.setVisible(true);
                    
                    if(assignmentProblem.getLastUserWidgetValue() != null) {
                        _tutorPanel.setTutorWidgetValue(assignmentProblem.getLastUserWidgetValue());
                    }
                    
                    //setupShowWorkIfRequired();                    
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
