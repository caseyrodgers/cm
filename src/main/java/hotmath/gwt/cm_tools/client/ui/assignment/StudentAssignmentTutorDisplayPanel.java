package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.viewer.TutorWrapperPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.user.client.Window;
import com.sencha.gxt.widget.core.client.ContentPanel;

public class StudentAssignmentTutorDisplayPanel extends ContentPanel {
    TutorWrapperPanel tutorPanel = new TutorWrapperPanel();
    public StudentAssignmentTutorDisplayPanel() {
        setWidget(tutorPanel);
    }

    public void loadProblem(final int uid, final int assignKey, final ProblemDto problem) {

        new RetryAction<SolutionInfo>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(uid,assignKey, problem.getPid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(SolutionInfo result) {

                try {
                    
                    setHeadingHtml(problem.getLabel());
                    tutorPanel.addStyleName("tutor_solution_wrapper");
    
                    /** if is EPP, then turn off steps/hints and hide button bar */
                    boolean isEpp = true;
                    tutorPanel.addStyleName("is_epp");
    
                    String variableContext = null;
                    if (result.getContext() != null) {
                        variableContext = result.getContext().getContextJson();
                    }
    
                    String json = result.getContext()!=null?result.getContext().getContextJson():null;
                    initializeTutor(problem.getPid(), problem.getLabel(), json, false, false,result.getHtml(), result.getJs(), isEpp, variableContext);
                    
                    
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
