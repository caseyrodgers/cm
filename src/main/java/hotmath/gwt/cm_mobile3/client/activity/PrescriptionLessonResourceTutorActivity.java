package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.event.ShowWorkViewEvent;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonResourceTutorView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SetInmhItemAsViewedAction;
import hotmath.gwt.cm_rpc.client.rpc.SetLessonCompletedAction;
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


    @Override
    public void showWhiteboard() {
        final String pid = resourceItem.getFile();
        eventBus.fireEvent(new ShowWorkViewEvent(pid));
    }


    @Override
    public void markSolutionAsComplete() {
        UserInfo ui = CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo();
        String pid=resourceItem.getFile();
        int runId = ui.getRunId();
        int sessionNum = ui.getSessionNumber();
        SetInmhItemAsViewedAction action = new SetInmhItemAsViewedAction(runId,resourceItem.getType(),pid,sessionNum);
        CatchupMathMobileShared.getCmService().execute(action,  new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                resourceItem.setViewed(true);
                
                
                CatchupMathMobile3.__clientFactory.getPrescriptionLessonView().refreshRppIndicators();
                
                MessageBox.showMessage("Practice problem marked as complete");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error marking solution as complete", caught);
                Window.alert(caught.getMessage());
            }
        });
    }


    @Override
    public void markLessonAsComplete() {
        UserInfo ui = CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo();
        String lesson=resourceItem.getFile();
        int runId = ui.getRunId();
        int sessionNum = ui.getSessionNumber();
        SetLessonCompletedAction action = new SetLessonCompletedAction(lesson,runId,sessionNum);
        CatchupMathMobileShared.getCmService().execute(action,  new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                resourceItem.setViewed(true);
                MessageBox.showMessage("Lesson marked as complete!");
            }
            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error marking lesson as complete", caught);
                Window.alert(caught.getMessage());
            }
        });
    }
}