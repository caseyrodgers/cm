package hotmath.gwt.cm_mobile_assignments.client;

import hotmath.gwt.cm_mobile_assignments.client.view.AboutView;
import hotmath.gwt.cm_mobile_assignments.client.view.AssProblemView;
import hotmath.gwt.cm_mobile_assignments.client.view.AssProblemViewImpl;
import hotmath.gwt.cm_mobile_assignments.client.view.AssignmentView;
import hotmath.gwt.cm_mobile_assignments.client.view.AssignmentViewImpl;
import hotmath.gwt.cm_mobile_assignments.client.view.BaseView;
import hotmath.gwt.cm_mobile_assignments.client.view.HomeView;
import hotmath.gwt.cm_mobile_assignments.client.view.HomeViewImpl;
import hotmath.gwt.cm_mobile_assignments.client.view.MainView;
import hotmath.gwt.cm_mobile_assignments.client.view.MainViewImpl;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceController.DefaultDelegate;
import com.google.gwt.place.shared.PlaceController.Delegate;

public class ClientFactoryImplDefault implements ClientFactory {

    EventBus eventBus = new SimpleEventBus();
    private PlaceController placeController;
    private HomeView homeView;
    private AboutView aboutView;
    private MainView mainView ;
    private AssignmentView assignmentView;
    private AssProblemView assProblemView;
    
    @Override
    public EventBus getEventBus() {
        return eventBus;
    }
    
    @Override
    public PlaceController getPlaceController() {
        if(placeController == null) {
            placeController = new PlaceController(getEventBus(), (Delegate) GWT.create(DefaultDelegate.class));
        }
        return placeController;
    }

    @Override
    public HomeView getHomeView() {
        if(homeView == null) {
            homeView = new HomeViewImpl(this);
        }
        return homeView;
    }

    @Override
    public AboutView getAboutView() {
        if(aboutView == null) {
            aboutView = new AboutViewImpl();
        }
        return aboutView;
    }
    

    @Override
    public AssignmentView getAssignmentView() {
        if(assignmentView == null) {
            assignmentView = new AssignmentViewImpl(this);
        }
        return assignmentView;
    }

    
    @Override
    public MainView getMain(BaseView view, String title, boolean needsBackButton) {
        if(mainView == null) {
            mainView = new MainViewImpl(this);
        }
        mainView.setView(view, title, needsBackButton);
        return mainView;
    }

    @Override
    public AssProblemView getAssProblemView() {
        if(assProblemView == null) {
            assProblemView = new AssProblemViewImpl();
        }
        return assProblemView;
    }
}