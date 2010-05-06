package hotmath.gwt.cm_mobile.client;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

public class Thermometer extends SimplePanel {
    
    Image _thermometer;
    
    int DEF_PERCENT=0;
    public Thermometer() {
        setStyleName("percent-complete-wrapper");
        _thermometer = new Image("/gwt-resources/images/therm_complete.png");
        _thermometer.setStyleName("percent-complete");
        setPerecent(DEF_PERCENT);
        add(_thermometer);
    }
    
    
    /** Show percentage using integer
     * 
     * @param percent
     */
    public void setPerecent(int percent) {
        _thermometer.setWidth(percent + "%");
        _thermometer.setTitle(percent + "%");
    }
}