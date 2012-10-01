package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel.TutorCallback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Window;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.info.Info;

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
        tutorPanel = new TutorWrapperPanel(false,false,false,new TutorCallback() {
            
            @Override
            public void tutorWidgetComplete(String inputValue, boolean correct) {
                Info.display("Change Not Allowed", "Setting a new input value is not allowed");
            }
            
            @Override
            public void onNewProblem(int problemNumber) {
                // empty
            }
            
            @Override
            public void solutionHasBeenViewed(String value) {
                CmLogger.debug("Solution has been viewed");
            }
        });
        setHeadingHtml("Question Display");
        tutorPanel.setVisible(false);
        setWidget(tutorPanel);
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
                    setHeadingHtml(problem.getLabel());
                    
                    String variableContext = null;
                    if (result.getContext() != null) {
                        variableContext = result.getContext().getContextJson();
                    }
                    tutorPanel.externallyLoadedTutor(result, getWidget(), problem.getPid(), null, result.getJs(), result.getHtml(), problem.getLabel(), false, false, variableContext);
                    tutorPanel.setVisible(true);
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
