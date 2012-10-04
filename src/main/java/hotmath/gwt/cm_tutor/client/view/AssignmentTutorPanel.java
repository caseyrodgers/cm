package hotmath.gwt.cm_tutor.client.view;


import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentProblem;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentTutorInputWidgetAnswerAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_tutor.client.CmTutor;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;


/** Provide Generic access to a single solution in an Assignment
 * 
 * @author casey
 *
 */
public class AssignmentTutorPanel extends Composite {
    
    static AssignmentTutorPanel __lastInstance;

    TutorWrapperPanel _tutorPanel;

    public AssignmentTutorPanel() {
        __lastInstance = this;
        _tutorPanel = new TutorWrapperPanel(false, false,false, true, new TutorCallbackDefault() {
            @Override
            public void tutorWidgetComplete(String inputValue, boolean correct) {
                processTutorWidgetComplete(inputValue, correct);
            }
        });
        
        _tutorPanel.addStyleName("tutor_solution_wrapper");
        isEpp = true;
        _tutorPanel.addStyleName("is_epp");

        initWidget(_tutorPanel);
    }
    
    

    SolutionInfo solutionInfo;

    boolean isEpp;
    
    public AssignmentProblem getAssignmentProblem() {
        return _assProblem;
    }

    AssignmentProblem _assProblem;
    public void loadSolution(final int uid, final int assignKey, String pid) {

        GetAssignmentSolutionAction action = new GetAssignmentSolutionAction(uid,assignKey, pid);
        CmTutor.getCmService().execute(action, new AsyncCallback<AssignmentProblem>() {
            @Override
            public void onSuccess(AssignmentProblem problem) {
                loadSolutionIntoGui(uid, assignKey, problem);
            }

            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error: " + caught);
                caught.printStackTrace();
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
            
            _tutorPanel.setTutorWidgetValue(problem.getLastUserWidgetValue());
            
            // CmRpc.EVENT_BUS.fireEvent(new AssignmentProblemLoadedEvent(problem));
            
        } catch (Exception e) {
            e.printStackTrace();
            Window.alert(e.getMessage());
        }

    }

    private void processTutorWidgetComplete(String inputValue, boolean yesNo) {
        SaveAssignmentTutorInputWidgetAnswerAction action = new SaveAssignmentTutorInputWidgetAnswerAction(_uid, _assignKey,_assProblem.getInfo().getPid(),inputValue,yesNo);
        CmTutor.getCmService().execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                Log.debug("Tutor Widget Answer saved");
            }
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
                Log.debug("Error saving tutor widget input value",caught);
            }
        });
    }

}

