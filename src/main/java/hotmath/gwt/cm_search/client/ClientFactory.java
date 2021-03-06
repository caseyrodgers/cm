package hotmath.gwt.cm_search.client;


import hotmath.gwt.cm_search.client.view.ReviewView;
import hotmath.gwt.cm_search.client.view.SearchView;
import hotmath.gwt.cm_search.client.view.TopicView;
import hotmath.gwt.cm_search.client.view.TutorView;
import hotmath.gwt.cm_search.client.view.VideoView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceContainer();
    
    SearchView getSearchView();
    TopicView getTopicView();
    TutorView getTopicResourceTutorView();
    ReviewView getReviewView();
    VideoView getVideoView();
}