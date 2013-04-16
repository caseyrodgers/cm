package hotmath.gwt.cm_mobile_assignments.client;

import hotmath.gwt.cm_mobile_assignments.client.view.AboutView;
import hotmath.gwt.cm_mobile_assignments.client.view.BaseView;
import hotmath.gwt.cm_mobile_assignments.client.view.HomeView;
import hotmath.gwt.cm_mobile_assignments.client.view.MainView;

import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;

public interface ClientFactory {
    EventBus getEventBus();
    PlaceController getPlaceController();
    MainView getMain(BaseView view, String title, boolean needsBackButton);
    HomeView getHomeView();
    AboutView getAboutView();
}