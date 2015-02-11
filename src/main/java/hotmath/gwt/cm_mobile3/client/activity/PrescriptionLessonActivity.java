package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_core.client.event.CmQuizModeActivatedEvent;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.event.MoveToNextSegmentEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowPrescriptionResourceEvent;
import hotmath.gwt.cm_mobile3.client.event.ShowWelcomeViewEvent;
import hotmath.gwt.cm_mobile3.client.view.PrescriptionLessonView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox;
import hotmath.gwt.cm_mobile_shared.client.util.QuestionBox.CallBack;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.SessionTopic;
import hotmath.gwt.cm_rpc.client.rpc.GetPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.SetLessonCompletedAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;

import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PrescriptionLessonActivity implements PrescriptionLessonView.Presenter {

    List<Integer> testQuestionAnswers;

    private EventBus eventBus;
    UserInfo userInfo;
    ClientFactory clientFactory;

    public PrescriptionLessonActivity(ClientFactory clientFactory, EventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        this.userInfo = SharedData.getMobileUser().getBaseLoginResponse().getUserInfo();
    }

    @Override
    public void prepareView(final PrescriptionLessonView view) {
        view.setLesson(SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getCurrSession());
        
        
        
        CmRpcCore.EVENT_BUS.fireEvent(new CmQuizModeActivatedEvent(false));
    }
    
    @Override
    public void showSearch() {
        SearchActivity search = new SearchActivity(clientFactory,clientFactory.getEventBus());
        clientFactory.getSearchView().setPresenter(search);
        clientFactory.getEventBus().fireEvent(new LoadNewPageEvent((IPage)clientFactory.getSearchView() ));                
    }

    @Override
    public void loadResource(InmhItemData resourceItem) {
        eventBus.fireEvent(new ShowPrescriptionResourceEvent(resourceItem, false));
    }

    @Override
    public void moveToNextLesson(final PrescriptionLessonView view) {
        int sessionNumber = findNextSessionNumber();
        
        if(sessionNumber == -1) {
            QuestionBox.askYesNoQuestion("Ready To Move On", "Are you ready for the next quiz?", new CallBack() {
                @Override
                public void onSelectYes() {
                    eventBus.fireEvent(new MoveToNextSegmentEvent());
                }
            });
        }
        
        moveToLesson(view, sessionNumber + 1);
    }
    

    /** return the next available session that is not complete */
    private int findNextSessionNumber() {
        List<SessionTopic> topics = SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics();
        /** search for current position */
        for(int i=userInfo.getSessionNumber()+1,t=topics.size();i<t;i++) {
            SessionTopic topic = topics.get(i);
            if(!topic.isComplete()) {
                return i-1;
            }
        }
        /** search from beginning */
        for(int i=0,t=topics.size();i<t;i++) {
            SessionTopic topic = topics.get(i);
            if(!topic.isComplete()) {
                return i-1;
            }
        }        
        return -1;
    }

    @Override
    public void moveToPreviousLesson(final PrescriptionLessonView view) {
        int sessionNumber = userInfo.getSessionNumber();
        moveToLesson(view, sessionNumber - 1);
    }

    public void moveToLesson(final PrescriptionLessonView view, int sessionNumber) {

        if (sessionNumber < 0) {
            PopupMessageBox.showMessage("No previous lesson");
            return;
        } else if (sessionNumber > SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getSessionTopics().size()) {
            PopupMessageBox.showMessage("No more lessons"); // move to next segment?
            return;
        }

        eventBus.fireEvent(new SystemIsBusyEvent(true));
        int runId = SharedData.getMobileUser().getBaseLoginResponse().getUserInfo().getRunId();
        GetPrescriptionAction action = new GetPrescriptionAction(runId, sessionNumber, true);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<PrescriptionSessionResponse>() {
            public void onSuccess(PrescriptionSessionResponse result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                /**
                 * install new data ..
                 * 
                 */
                PrescriptionSessionResponse res = SharedData.getFlowAction().getPrescriptionResponse();
                res.setPrescriptionData(result.getPrescriptionData());
                userInfo.setSessionNumber(res.getPrescriptionData().getCurrSession().getSessionNumber());
                view.setLesson(res.getPrescriptionData().getCurrSession());
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error moving to lesson", caught);
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                PopupMessageBox.showError("Error moving to next lesson: " + caught.getMessage());                
            }
        });

    }


    @Override
    public void showLessonChooser() {
        /** Bypass adding to history */
        History.newItem("listing|" + System.currentTimeMillis());
    }

    @Override
    public void markLessonAsComplete(final SessionTopic topic) {
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        UserInfo ui = SharedData.getMobileUser().getBaseLoginResponse().getUserInfo();
        String lesson = SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getCurrSession().getTopic();
        int runId = ui.getRunId();
        int sessionNum = ui.getSessionNumber();
        SetLessonCompletedAction action = new SetLessonCompletedAction(lesson, runId, sessionNum);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<RpcData>() {
            @Override
            public void onSuccess(RpcData result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                topic.setComplete(true);
                //MessageBox.showMessage("Lesson marked as complete!");
            }

            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                Log.error("Error marking lesson as complete", caught);
                PopupMessageBox.showError("Error marking lesson as complete: " +  caught.getMessage());
            }
        });
    }

    @Override
    public void goBack() {
        /** force back to Login view */
        eventBus.fireEvent(new ShowWelcomeViewEvent());
    }
}
