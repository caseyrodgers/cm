package hotmath.gwt.cm_mobile_assignments.client.view;

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

}
