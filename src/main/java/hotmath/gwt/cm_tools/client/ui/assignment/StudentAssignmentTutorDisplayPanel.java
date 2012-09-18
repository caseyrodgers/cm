package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_mobile_shared.client.view.ShowWorkViewImpl;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.viewer.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Window;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;

/** Shown to students when displaying a single tutor
 * 
 * @author casey
 *
 */
public class StudentAssignmentTutorDisplayPanel extends ContentPanel {
    TutorWrapperPanel tutorPanel = new TutorWrapperPanel();
    
    ShowWorkViewImpl whiteboard;
    public StudentAssignmentTutorDisplayPanel() {
        BorderLayoutContainer container = new BorderLayoutContainer();
        BorderLayoutData bd = new BorderLayoutData(.50);
        bd.setSplit(true);
        
        whiteboard = createWhiteBoard();
        container.setEastWidget(whiteboard,  bd);
        container.setCenterWidget(tutorPanel);
        setWidget(container);
    }
    
    private ShowWorkViewImpl createWhiteBoard() {
        
        
        ShowWorkViewImpl viewImpl = new ShowWorkViewImpl();
//        
//        ShowWorkPanel showWorkPanel = new ShowWorkPanel(new CmAsyncRequestImplDefault() {
//            @Override
//            public void requestComplete(String requestData) {
//                //
//            }
//        });
        
        return viewImpl;
    }
    
    AssignmentProblem _assignmentProblem;

    public void loadProblem(final int uid, final int assignKey, final ProblemDto problem) {

        new RetryAction<AssignmentProblem>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(uid,assignKey, problem.getPid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(AssignmentProblem result) {
                
                
                _assignmentProblem = result;

                CmLogger.debug("Assignment problem type: " + result.getProblemType());
                
                try {
                    
                    SolutionInfo solInfo = result.getInfo();
                    setHeadingHtml(problem.getLabel());
                    tutorPanel.addStyleName("tutor_solution_wrapper");
    
                    /** if is EPP, then turn off steps/hints and hide button bar */
                    boolean isEpp = true;
                    tutorPanel.addStyleName("is_epp");
    
                    String variableContext = null;
                    if (solInfo.getContext() != null) {
                        variableContext = solInfo.getContext().getContextJson();
                    }
    
                    String json = solInfo.getContext()!=null?solInfo.getContext().getContextJson():null;
                    initializeTutor(problem.getPid(), problem.getLabel(), json, false, false,solInfo.getHtml(), solInfo.getJs(), isEpp, variableContext);
                    
                    
                    tutorPanel.setVisible(true);
                }
                catch(Exception e) {
                    e.printStackTrace();
                    Window.alert(e.getMessage());
                }
                
                CmBusyManager.setBusy(false);

            }
        }.register();        
        
    }
    
    static private native void initializeTutor(String pid, String title, String jsonConfig, boolean hasShowWork, boolean shouldExpandSolution,String solutionHtml,String solutionData,boolean isEpp, String contextVarsJson) /*-{
        $wnd.doLoad_Gwt(pid, title,jsonConfig, hasShowWork,shouldExpandSolution,solutionHtml,solutionData,isEpp,contextVarsJson);
    }-*/;
}
