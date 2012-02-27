package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.event.ShowWorkViewEvent;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SetInmhItemAsViewedAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PrescriptionLessonResourceTutorActivity  implements PrescriptionLessonResourceTutorView.Presenter {
	InmhItemData resourceItem;
	com.google.gwt.event.shared.EventBus eventBus;
	
	public PrescriptionLessonResourceTutorActivity(com.google.gwt.event.shared.EventBus eventBus, InmhItemData resourceItem) {
	    this.eventBus = eventBus;
		this.resourceItem = resourceItem;
		
		
		setupExternalJsHooks(this);
	}


    @Override
    public void setupView(final PrescriptionLessonResourceTutorView view) {
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        GetSolutionAction action = new GetSolutionAction(resourceItem.getFile());
        CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<SolutionResponse>() {
            public void onSuccess(SolutionResponse solutionResponse) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                
                view.loadSolution(solutionResponse);
            }
            
            @Override
            public void onFailure(Throwable ex) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                ex.printStackTrace();
                Window.alert(ex.getMessage());
                Log.error("Error getting tutor", ex);
            }
        });
    }
    
    native public int getSolutionStepUnit() /*-{
        return $wnd.TutorManager.getCurrentStepNumber();
    }-*/;
    
    native public int setSolutionStepUnit(int x) /*-{
        $wnd.gotoStepUnit(x);
    }-*/; 

    private void saveTutorVariableContext_aux(String varsAsJson) {
        Window.alert("Got the JSON: " + varsAsJson);
    }
    
    public void gwt_solutionHasBeenInitialized(String variablesJson) {
        SaveSolutionContextAction action = new SaveSolutionContextAction(SharedData.getUserInfo().getUid(),SharedData.getUserInfo().getRunId(),resourceItem.getFile(), variablesJson);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                Log.info("Context saved");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error saving solution context", caught);
            }
        });
    }
    
    
    native public void setupExternalJsHooks(PrescriptionLessonResourceTutorActivity instance) /*-{
        $wnd.gwt_solutionHasBeenInitialized = function() {
            //var solutionVariablesJson = $wnd.getTutorVariableContextJson($wnd.TutorManager.tutorData._variables);
            //instance.@hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceTutorActivity::gwt_solutionHasBeenInitialized(Ljava/lang/String;)(solutionVariablesJson);        
        }
    }-*/;

    @Override
    public void showWhiteboard(String title) {
        String pid = resourceItem.getFile();
        eventBus.fireEvent(new ShowWorkViewEvent(pid, title));
    }


    @Override
    public void markSolutionAsComplete() {
        UserInfo ui = CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo();
        String pid=resourceItem.getFile();
        int runId = ui.getRunId();
        int sessionNum = ui.getSessionNumber();
        Action<RpcData> action = new SetInmhItemAsViewedAction(runId,resourceItem.getType(),pid,sessionNum);
        CatchupMathMobileShared.getCmService().execute(action,  new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                resourceItem.setViewed(true);
                
                
                CatchupMathMobile3.__clientFactory.getPrescriptionLessonView().refreshRppIndicators();
                
               // MessageBox.showMessage("Practice problem marked as complete");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error marking solution as complete", caught);
                Window.alert(caught.getMessage());
            }
        });
    }

    @Override
    public InmhItemData getItemData() {
        return resourceItem;
    }

}