package hotmath.gwt.hm_mobile.client;

import hotmath.gwt.hm_mobile.client.activity.BookListActivity;
import hotmath.gwt.hm_mobile.client.activity.CategoryListActivity;
import hotmath.gwt.hm_mobile.client.activity.HomeActivity;
import hotmath.gwt.hm_mobile.client.place.BookListPlace;
import hotmath.gwt.hm_mobile.client.place.CategoryListPlace;
import hotmath.gwt.hm_mobile.client.place.HomePlace;

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
    	if(place == null || place instanceof HomePlace) {
    		return new HomeActivity((HomePlace)place, clientFactory);
    	}
    	else if(place instanceof BookListPlace) {
    		return new BookListActivity((BookListPlace)place, clientFactory);
    	}
    	else if(place instanceof CategoryListPlace) {
    		return new CategoryListActivity((CategoryListPlace)place, clientFactory);
    	}
    	else  {
    		//return new StartActivity(null, clientFactory);
    	}
    	
    	return null;
    		
    }
}
