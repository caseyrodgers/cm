package hotmath.gwt.cm_mobile_assignments.client;

import hotmath.gwt.cm_mobile_assignments.client.activity.AboutActivity;
import hotmath.gwt.cm_mobile_assignments.client.activity.HomeActivity;
import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class AssActivityMapper implements ActivityMapper {
    private ClientFactory clientFactory;

    public AssActivityMapper(ClientFactory clientFactory) {
        super();
        this.clientFactory = clientFactory;
    }

    @Override
    public Activity getActivity(Place place) {
    	/** only one place right now */
    	if(place == null || place instanceof AboutPlace) {
    		return new AboutActivity((AboutPlace)place, clientFactory);
    	}
    	else if(place instanceof HomePlace) {
    	    return new HomeActivity((HomePlace)place, clientFactory);
    	}
    	return null;
    		
    }
}
