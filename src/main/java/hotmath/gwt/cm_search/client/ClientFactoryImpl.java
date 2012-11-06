package hotmath.gwt.cm_search.client;


import hotmath.gwt.cm_search.client.view.ReviewView;
import hotmath.gwt.cm_search.client.view.ReviewViewImpl;
import hotmath.gwt.cm_search.client.view.SearchView;
import hotmath.gwt.cm_search.client.view.SearchViewImpl;
import hotmath.gwt.cm_search.client.view.TopicView;
import hotmath.gwt.cm_search.client.view.TopicViewImpl;
import hotmath.gwt.cm_search.client.view.TutorView;
import hotmath.gwt.cm_search.client.view.TutorViewImpl;
import hotmath.gwt.cm_search.client.view.VideoView;
import hotmath.gwt.cm_search.client.view.VideoViewImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory {
    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private final SearchView searchView = new SearchViewImpl();
    private final TopicView topicView = new TopicViewImpl();
    private final TutorView tutorView = new TutorViewImpl();
    private final ReviewView reviewView = new ReviewViewImpl();
    private final VideoView videoView = new VideoViewImpl();
    
    
    @Override
    public VideoView getVideoView() {
        return videoView;
    }
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    @Override
    public PlaceController  getPlaceContainer() {
        return placeController;
    }
    
    @Override
    public SearchView getSearchView() {
        return searchView;
    }

    @Override
    public TopicView getTopicView() {
        return topicView;
    }

    @Override
    public TutorView getTopicResourceTutorView() {
        return tutorView;
    }

    @Override
    public ReviewView getReviewView() {
        return reviewView;
    }
    
}