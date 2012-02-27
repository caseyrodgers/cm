package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.view.SearchView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.CmMobileUser;
import hotmath.gwt.cm_mobile_shared.client.rpc.Topic;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.UserLoginResponse;
import hotmath.gwt.cm_rpc.client.rpc.CmDestination;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmPlace;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class SearchActivity implements SearchView.Presenter {
    
    private com.google.gwt.event.shared.EventBus eventBus;
    ClientFactory clientFactory;
    public SearchActivity(ClientFactory clientFactory, com.google.gwt.event.shared.EventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }

    @Override
    public void doSearch(String search, final CallBack callBack) {
        
        if(search == null || search.length() < 3) {
            MessageBox.showError("Search string must be at least three letters.");
            return;
        }
        
        Log.info("Doing search: " + search);
        
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        
        SearchTopicAction action = new SearchTopicAction(search);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<CmList<Topic>>() {
            @Override
            public void onSuccess(CmList<Topic> result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                callBack.searchResults(result);
            }
            
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                Window.alert("Error: " + caught);
            }
        });
    }

    static public interface CallBack {
        void searchResults(CmList<Topic> results);
    }

    @Override
    public void loadTopic(String topicFile) {
        Log.info("Loading topic: " + topicFile);
        
        GetTopicPrescriptionAction action = new GetTopicPrescriptionAction(topicFile);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<PrescriptionSessionResponse>() {
            @Override
            public void onSuccess(PrescriptionSessionResponse result) {
                loadLesson(result);
            }
            
            @Override
            public void onFailure(Throwable caught) {
                Window.alert("Error: " + caught);
            }
        });
        
    }
    
    
    private void loadLesson(PrescriptionSessionResponse response) {

        /**
         * install new data ..
         * 
         */
        if(SharedData.getFlowAction() == null) {
            SharedData.setFlowAction(new CmProgramFlowAction(response));
        }
        else {
            SharedData.getFlowAction().getPrescriptionResponse().setPrescriptionData(response.getPrescriptionData());
        }

        if(CatchupMathMobileShared.__instance.user == null) {
            CmMobileUser user = new CmMobileUser(0,0,1,0,response.getRunId());
            user.setBaseLoginResponse(new UserLoginResponse(new UserInfo(0, 0),new CmDestination(CmPlace.PRESCRIPTION)));
            CatchupMathMobileShared.__instance.user = user;
        }
        CatchupMathMobileShared.getUser().getBaseLoginResponse().getUserInfo().setSessionNumber(SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getCurrSession().getSessionNumber());
        clientFactory.getPrescriptionLessonView().setLesson(SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getCurrSession());
        
        new PrescriptionLessonActivity(clientFactory,eventBus).moveToLesson(clientFactory.getPrescriptionLessonView(), 0);
        this.eventBus.fireEvent(new LoadNewPageEvent(clientFactory.getPrescriptionLessonView()));
    }
}


