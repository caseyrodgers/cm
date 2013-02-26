package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
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
        tutorPanel = new TutorWrapperPanel(false,false,false,false,new TutorCallbackDefault() {
            @Override
            public void tutorWidgetCompleteDenied(String inputValue, boolean correct) {
                Info.display("Tutor Readonly", "This solution is read only.  You can add comments and corrections to the whiteboard.");
            }
        });
        setHeadingHtml("Problem Statement");
        tutorPanel.setVisible(true);
        setWidget(createDefaultContainer());
    }

    TutorWrapperPanel tutorPanel;
    
    public void viewQuestion(final ProblemDto problem) {
        viewQuestion(problem, false);
    }

    FlowLayoutContainer flowWrapper;
    public void viewQuestion(final ProblemDto problem, final boolean isReadOnly) {
        
        if(flowWrapper == null) {
            flowWrapper = new FlowLayoutContainer();
            flowWrapper.setScrollMode(ScrollMode.AUTOY);
            flowWrapper.add(tutorPanel);
            setWidget(flowWrapper);
            forceLayout();
        }
        
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
                    
                    forceLayout();
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
    
    private Widget createDefaultContainer() {
        CenterLayoutContainer cc = new CenterLayoutContainer();
        cc.add(new HTML("<h1>No problem selected</h1>"));
        return cc;
    }


    public void removeQuestion() {
        flowWrapper = null;
        setHeadingHtml("Problem Statement");
        setWidget(createDefaultContainer());
        forceLayout();
    }
}
