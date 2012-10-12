package hotmath.gwt.cm_tools.client.ui.assignment;


import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tools.client.ui.assignment.event.AssignmentProblemLoadedEvent;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tutor.client.CmTutor;
import hotmath.gwt.cm_tutor.client.view.TutorCallbackDefault;
import hotmath.gwt.cm_tutor.client.view.TutorWrapperPanel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;


/** Provide Generic access to a single solution in an Assignment
 * 
 * @author casey
 *
 */
public class AssignmentTutorPanel extends Composite {
    
    static AssignmentTutorPanel __lastInstance;

    TutorWrapperPanel _tutorPanel;

    AssignmentTutorPanelCallback _callBack;
    
    interface AssignmentTutorPanelCallback {
        void tutorWidgetValueUpdated(String value, boolean correct);
    }
    public AssignmentTutorPanel(AssignmentTutorPanelCallback callBack) {
        _callBack = callBack;
        __lastInstance = this;
        _tutorPanel = new TutorWrapperPanel(false, false,false, true, new TutorCallbackDefault() {
            @Override
            public void tutorWidgetComplete(String inputValue, boolean correct) {
                processTutorWidgetComplete(inputValue, correct);
            }
            
            @Override
            public Action<RpcData> getSaveSolutionContextAction(String variablesJson, String pid,
                    int problemNumber) {
                return new SaveAssignmentSolutionContextAction(_assProblem.getUserId(), _assignKey, _assProblem.getInfo().getPid(), 0, variablesJson);
            }
            
        });
        
        _tutorPanel.addStyleName("tutor_solution_wrapper");
        isEpp = true;
        _tutorPanel.addStyleName("is_epp");
        
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.setScrollMode(ScrollMode.AUTO);
        flow.add(_tutorPanel);

        initWidget(flow);
    }
    
    

    SolutionInfo solutionInfo;

    boolean isEpp;
    
    public AssignmentProblem getAssignmentProblem() {
        return _assProblem;
    }

    AssignmentProblem _assProblem;
    public void loadSolution(final int uid, final int assignKey, final String pid) {

        GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(uid,assignKey, pid);
        CmTutor.getCmService().execute(action, new AsyncCallback<AssignmentProblem>() {
            @Override
            public void onSuccess(AssignmentProblem problem) {
                loadSolutionIntoGui(uid, assignKey, problem);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error loading solution: " + pid, caught);
                CmMessageBox.showAlert("Error loading solution", "There was a problem talking to the server");
            }
        });
    }

    int _assignKey;
    int _uid;
    private void loadSolutionIntoGui(int uid, int assignKey, AssignmentProblem problem) {
        try {
            _uid = uid;
            _assProblem = problem;
            _assignKey = assignKey;
            String resourceTitle = "Problem: " + problem.getInfo().getPid();

            String variableContext = null;

             if(problem.getInfo().getContext() != null) {
                 variableContext = problem.getInfo().getContext().getContextJson();
             }

            InmhItemData item = new InmhItemData("solution", problem.getInfo().getPid(), resourceTitle);

            _tutorPanel.externallyLoadedTutor(problem.getInfo(),getWidget(), item.getFile(), item.getWidgetJsonArgs(), problem.getInfo().getJs(),problem.getInfo().getHtml(), resourceTitle, true, false, variableContext);
            
            
            if(problem.getLastUserWidgetValue() != null) {
                _tutorPanel.setTutorWidgetValue(problem.getLastUserWidgetValue());
            }
            
            CmRpc.EVENT_BUS.fireEvent(new AssignmentProblemLoadedEvent(problem));
            
        } catch (Exception e) {
            Log.error("Error loading solution into GUI", e);
        }

    }

    /** Save user's input widget value to server
     * 
     * @param inputValue
     * @param yesNo
     */
    private void processTutorWidgetComplete(String inputValue, boolean yesNo) {
        SaveAssignmentTutorInputWidgetAnswerAction action = new SaveAssignmentTutorInputWidgetAnswerAction(_uid, _assignKey,_assProblem.getInfo().getPid(),inputValue,yesNo);
        CmTutor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                Log.debug("Tutor Widget Answer saved to server.");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error saving tutor widget input value.",caught);
            }
        });
        
        _callBack.tutorWidgetValueUpdated(inputValue,  yesNo);
    }

}

