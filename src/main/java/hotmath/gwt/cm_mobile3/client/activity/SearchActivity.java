package hotmath.gwt.cm_mobile3.client.activity;

import hotmath.gwt.cm_core.client.model.TopicSearchResults;
import hotmath.gwt.cm_mobile3.client.ClientFactory;
import hotmath.gwt.cm_mobile3.client.view.SearchView;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.LoadNewPageEvent;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.PopupMessageBox;
import hotmath.gwt.cm_rpc.client.model.TopicMatch;
import hotmath.gwt.cm_rpc.client.rpc.CmProgramFlowAction;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction;
import hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchApp;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class SearchActivity implements SearchView.Presenter {
    
    private EventBus eventBus;
    ClientFactory clientFactory;
    public SearchActivity(ClientFactory clientFactory, EventBus eventBus) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
    }

    @Override
    public void doSearch(String search, final CallBack callBack) {
        
        if(search == null || search.length() < 2) {
            PopupMessageBox.showMessage("Enter a search phrase of at least two letters.");
            return;
        }
        
        Log.info("Doing search: " + search);
        
        eventBus.fireEvent(new SystemIsBusyEvent(true));
        
        int uid = SharedData.getMobileUser() != null?SharedData.getMobileUser().getUserId():-1;
        SearchTopicAction action = new SearchTopicAction(search,SearchApp.CM_MOBILE,uid);
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<TopicSearchResults>() {
            @Override
            public void onSuccess(TopicSearchResults result) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                callBack.searchResults(result.getTopics());
            }
            
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new SystemIsBusyEvent(false));
                Window.alert("Error: " + caught);
            }
        });
    }

    static public interface CallBack {
        void searchResults(CmList<TopicMatch> results);
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
        
        // int sessionNumber = response.getPrescriptionData().getCurrSession().getSessionNumber();
        // SharedData.getMobileUser().getBaseLoginResponse().getUserInfo().setSessionNumber(sessionNumber);
        
        PrescriptionSessionData session = response.getPrescriptionData().getCurrSession(); // SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getCurrSession();
        SearchLessonActivity presenter = new SearchLessonActivity(session);
        clientFactory.getSearchLessonView().setPresenter(presenter);
        this.eventBus.fireEvent(new LoadNewPageEvent(clientFactory.getSearchLessonView()));
    }
}


