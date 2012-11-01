package hotmath.gwt.cm_search.client;

import hotmath.gwt.cm_search.client.activity.ReviewActivity;
import hotmath.gwt.cm_search.client.activity.SearchActivity;
import hotmath.gwt.cm_search.client.activity.TopicActivity;
import hotmath.gwt.cm_search.client.activity.TutorActivity;
import hotmath.gwt.cm_search.client.activity.VideoActivity;
import hotmath.gwt.cm_search.client.places.ReviewPlace;
import hotmath.gwt.cm_search.client.places.SearchPlace;
import hotmath.gwt.cm_search.client.places.TopicPlace;
import hotmath.gwt.cm_search.client.places.TutorPlace;
import hotmath.gwt.cm_search.client.places.VideoPlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AppActivityMapper implements ActivityMapper {
    private ClientFactory clientFactory;

    public AppActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
    	
    	/** only one place right now */
    	if(place == null || place instanceof SearchPlace) {
    		return new SearchActivity((SearchPlace)place, clientFactory);
    	}
    	else if (place instanceof TopicPlace) {
            return new TopicActivity((TopicPlace)place, clientFactory);
        }
    	else if(place instanceof TutorPlace) {
    	    return new TutorActivity((TutorPlace)place,clientFactory);
    	}
    	else if(place instanceof ReviewPlace) {
    	    return new ReviewActivity((ReviewPlace)place,clientFactory);
    	}
        else if(place instanceof VideoPlace) {
            return new VideoActivity((VideoPlace)place,clientFactory);
        }

    	return null;
    		
    }
}
