package hotmath.gwt.cm_search.client;


import hotmath.gwt.cm_search.client.view.SearchView;
import hotmath.gwt.cm_search.client.view.SearchViewImpl;
import hotmath.gwt.cm_search.client.view.TutorView;
import hotmath.gwt.cm_search.client.view.TutorViewImpl;
import hotmath.gwt.cm_search.client.view.TopicView;
import hotmath.gwt.cm_search.client.view.TopicViewImpl;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;

public class ClientFactoryImpl implements ClientFactory {
    private final EventBus eventBus = new SimpleEventBus();
    private final PlaceController placeController = new PlaceController(eventBus);
    private final SearchView searchView = new SearchViewImpl();
    private final TopicView topicView = new TopicViewImpl();
    private final TutorView tutorView = new TutorViewImpl();

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
    
}