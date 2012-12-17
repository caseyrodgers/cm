package hotmath.gwt.cm_tools.client.ui;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.info.InfoConfig;

public class InfoPopupBox extends ContentPanel {
    
	public static void display(InfoConfig config) {
		Info.display(config);
	}
	
	public static void display(String title, String message) {
	    Info.display(title, message);
	}
}
