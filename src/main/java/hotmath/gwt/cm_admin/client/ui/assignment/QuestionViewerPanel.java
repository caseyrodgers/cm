package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.viewer.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Window;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;

/** Display tutor/question XML 
 * 
 * NOTE: There is only one due to references to 
 * DOM ids.
 * 
 * @author casey
 *
 */
public class QuestionViewerPanel extends ContentPanel {
    
    
    private static QuestionViewerPanel __instance;
    public static QuestionViewerPanel getInstance() {
        if(__instance == null) {
            __instance = new QuestionViewerPanel();
        }
        __instance.tutorPanel.setVisible(false);
        return __instance;
    }

    
    private QuestionViewerPanel() {
        tutorPanel = new TutorWrapperPanel();
        setHeadingHtml("Question Display");
        tutorPanel.setVisible(false);
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(tutorPanel);
        setWidget(flow);
    }

    TutorWrapperPanel tutorPanel;

    public void viewQuestion(final ProblemDto problem) {

        new RetryAction<SolutionInfo>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetSolutionAction action = new GetSolutionAction(0, 0, problem.getPid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(SolutionInfo result) {

                try {
                    
                    setHeadingHtml(problem.getName());
                    tutorPanel.addStyleName("tutor_solution_wrapper");
    
                    /** if is EPP, then turn off steps/hints and hide button bar */
                    boolean isEpp = true;
                    tutorPanel.addStyleName("is_epp");
    
                    String variableContext = null;
                    if (result.getContext() != null) {
                        variableContext = result.getContext().getContextJson();
                    }
    
                    String json = result.getContext()!=null?result.getContext().getContextJson():null;
                    initializeTutor(problem.getPid(), problem.getName(), json, false, false,result.getHtml(), result.getJs(), isEpp, variableContext);
                    
                    
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
