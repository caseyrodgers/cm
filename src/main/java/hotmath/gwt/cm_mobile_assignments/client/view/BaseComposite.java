package hotmath.gwt.cm_mobile_assignments.client.view;

import hotmath.gwt.cm_mobile_assignments.client.place.HomePlace;

import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.Composite;


abstract public class BaseComposite extends Composite implements BaseView {
    
    MainView main;
    
    @Override
    public void setMain(MainView main) {
        this.main = main;
    }
    
    @Override
    public MainView getMain() {
        return this.main;
    }
    
    @Override
    public boolean useScrollPanel() {
        return true;
    }
    
    
    @Override
    public Place getBackPlace() {
        return new HomePlace();
    }

}
