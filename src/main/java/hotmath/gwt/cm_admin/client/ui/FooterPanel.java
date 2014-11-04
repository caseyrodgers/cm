package hotmath.gwt.cm_admin.client.ui;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;

public class FooterPanel extends SimplePanel {
	
	public FooterPanel() {
		setStyleName("footer-panel");
        String html = "<ul class='h-menu'><li>About Us</li><li>Privacy</li><li>Press</li><li>Contact</li></ul>" +
                "<div>Brought to you by Hotmath.com</div>";
        setWidget(new HTML(html));
	}
}
