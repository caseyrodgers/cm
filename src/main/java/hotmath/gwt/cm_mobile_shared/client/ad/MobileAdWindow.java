package hotmath.gwt.cm_mobile_shared.client.ad;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class MobileAdWindow extends PopupPanel {
	public MobileAdWindow() {
		super(true, true);
		
		
		setTitle("Hotmath Mobile Ad Framework");
		
		SimplePanel main = new SimplePanel();
		main.setWidget(new HTML("<div class='the_ad'><img src='/assets/ads/CSG_TWW_MKT_20080513_01 _photo_300x250.jpg'/></div>"));
		
		setWidget(main);
		center();
	}
}
