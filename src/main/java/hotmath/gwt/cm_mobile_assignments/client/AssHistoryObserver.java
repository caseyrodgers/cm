package hotmath.gwt.cm_mobile_assignments.client;

import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;

import com.google.gwt.place.shared.Place;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.googlecode.mgwt.mvp.client.history.HistoryHandler;
import com.googlecode.mgwt.mvp.client.history.HistoryObserver;
import com.googlecode.mgwt.ui.client.MGWT;

public class AssHistoryObserver implements HistoryObserver {

    @Override
    public void onPlaceChange(Place place, HistoryHandler handler) {

    }

    @Override
    public void onHistoryChanged(Place place, HistoryHandler handler) {
    }

    @Override
    public void onAppStarted(Place place, HistoryHandler historyHandler) {
        if (MGWT.getOsDetection().isPhone()) {
            onDeviceNav(place, historyHandler);
        } else {
            // tablet
            onDeviceNav(place, historyHandler);
        }

    }

    private void onDeviceNav(Place place, HistoryHandler historyHandler) {
        if (place instanceof AboutPlace) {
            historyHandler.pushPlace(new AboutPlace());
        }
        else if(place instanceof HomePlace) {
            historyHandler.pushPlace(new HomePlace());
        }
    }

    @Override
    public HandlerRegistration bind(EventBus eventBus, HistoryHandler historyHandler) {
        return null;
    }


}
