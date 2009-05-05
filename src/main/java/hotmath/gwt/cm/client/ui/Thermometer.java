package hotmath.gwt.cm.client.ui;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

/** Presents a simple thermometer object that shows
 *  current position.
 *  
 * @author Casey
 *
 */
public class Thermometer extends SimplePanel {
	
	Image _thermometer;
	
	int DEF_PERCENT=0;
	public Thermometer() {
		setStyleName("percent-complete-wrapper");
		_thermometer = new Image("/gwt-resources/images/therm_complete.png");
		_thermometer.setStyleName("percent-complete");
		_thermometer.setTitle("Your current completion percentage.");
        setPerecent(DEF_PERCENT);
		add(_thermometer);
		
		
		setTitle("Your message here");
	}
	
	
	/** Show percentage using integer
	 * 
	 * @param percent
	 */
	public void setPerecent(int percent) {
		_thermometer.setWidth(percent + "%");
		_thermometer.setTitle("Completion percentage: " + percent + "%");
	}
}
