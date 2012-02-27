package hotmath.gwt.cm_search.client.activity;

import hotmath.gwt.cm_mobile3.client.activity.PrescriptionLessonResourceTutorActivity;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_mobile_shared.client.data.SharedData;
import hotmath.gwt.cm_mobile_shared.client.event.SystemIsBusyEvent;
import hotmath.gwt.cm_mobile_shared.client.util.MessageBox;
import hotmath.gwt.cm_rpc.client.rpc.GetTopicPrescriptionAction;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionResponse;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveSolutionContextAction;
import hotmath.gwt.cm_search.client.ClientFactory;
import hotmath.gwt.cm_search.client.places.SearchPlace;
import hotmath.gwt.cm_search.client.places.TopicPlace;
import hotmath.gwt.cm_search.client.places.TutorPlace;
import hotmath.gwt.cm_search.client.view.TopicView;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class TopicActivity extends AbstractActivity implements TopicView.Presenter {

    private TopicPlace place;
    private ClientFactory clientFactory;

    public TopicActivity(TopicPlace place, ClientFactory clientFactory) {
        this.place = place;
        this.clientFactory = clientFactory;
    }

    @Override
    public void start(AcceptsOneWidget panel, EventBus eventBus) {
        TopicView topicView = clientFactory.getTopicView();
        topicView.setPresenter(this);
        panel.setWidget(topicView);
    }

    @Override
    public void prepareView(final TopicView view) {
        clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(true));
        GetTopicPrescriptionAction action = new GetTopicPrescriptionAction(place.getToken());
        CatchupMathMobileShared.getCmService().execute(action, new AsyncCallback<PrescriptionSessionResponse>() {
            public void onSuccess(PrescriptionSessionResponse result) {
                clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));

                /**
                 * install new data ..
                 * 
                 */
                SharedData.getFlowAction().getPrescriptionResponse().setPrescriptionData(result.getPrescriptionData());
                
                view.setLesson(SharedData.getFlowAction().getPrescriptionResponse().getPrescriptionData().getCurrSession());
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error moving to lesson", caught);
                clientFactory.getEventBus().fireEvent(new SystemIsBusyEvent(false));
                MessageBox.showError("Error moving to next lesson: " + caught.getMessage());
            }
        });

    }

    @Override
    public void loadResource(InmhItemData resourceItem) {
        clientFactory.getPlaceContainer().goTo(determinePlace(resourceItem));
    }
    
    private Place determinePlace(InmhItemData resourceItem) {
        if(resourceItem.getType().equals("practice")) {
            return new TutorPlace(resourceItem.getFile());
        }
        else {
           return new SearchPlace("casey");
        }
    }
}
