package hotmath.gwt.cm_search.client.activity;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_rpc.client.rpc.SolutionResponse;
import hotmath.gwt.cm_search.client.ClientFactory;
import hotmath.gwt.cm_search.client.places.SearchPlace;
import hotmath.gwt.cm_search.client.places.TutorPlace;
import hotmath.gwt.cm_search.client.view.TopicView;
import hotmath.gwt.cm_search.client.view.TutorView;
import hotmath.gwt.cm_search.client.view.TutorViewImpl;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class TutorActivity extends AbstractActivity implements TutorView.Presenter {

    private TutorPlace place;
    private ClientFactory clientFactory;

    public TutorActivity(TutorPlace place, ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
        
        setupExternalJsHooks(this);
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        TutorView view = clientFactory.getTopicResourceTutorView();
        view.setPresenter(this);
        panel.setWidget(view);
        
        loadTutor(place.getToken(),view);
    }

    @Override
    public void prepareView(TopicView view) {
    }
    
    
    
    private void loadTutor(String pid, final TutorView view) {
        clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(true));
        GetSolutionAction action = new GetSolutionAction(pid);
        CatchupMathMobileShared.getCmService().execute(action,new AsyncCallback<SolutionResponse>() {
            public void onSuccess(SolutionResponse solutionResponse) {
                clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
                
                view.loadSolution(solutionResponse);
            }
            
            @Override
            public void onFailure(Throwable ex) {
                clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
                ex.printStackTrace();
                Window.alert(ex.getMessage());
                Log.error("Error getting tutor", ex);
            }
        });        
    }

    @Override
    public void markSolutionAsComplete() {
    }

    @Override
    public void showWhiteboard(String title) {
        Window.alert("Show Whiteboard");
    }
    
    
    public void gwt_solutionHasBeenInitialized(String variablesJson) {
        SaveSolutionContextAction action = new SaveSolutionContextAction(SharedData.getUserInfo().getUid(),SharedData.getUserInfo().getRunId(),place.getToken(),TutorViewImpl.__probNum, variablesJson);
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
    
    
    native public void setupExternalJsHooks(TutorActivity instance) /*-{
        $wnd.gwt_solutionHasBeenInitialized = function() {
            //var solutionVariablesJson = $wnd.getTutorVariableContextJson($wnd.TutorManager.tutorData._variables);
            //instance.@hotmath.gwt.cm_search.client.activity.TutorActivity::gwt_solutionHasBeenInitialized(Ljava/lang/String;)(solutionVariablesJson);        
        }
    }-*/;

    @Override
    public void goBack() {
        clientFactory.getPlaceContainer().goTo(new SearchPlace(""));
    }
    
    
}
