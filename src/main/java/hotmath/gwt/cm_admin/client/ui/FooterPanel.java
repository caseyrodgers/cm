package hotmath.gwt.cm_admin.client.ui;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;

public class FooterPanel extends LayoutContainer {
	
	public FooterPanel() {
		setStyleName("footer-panel");
	}

	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);
		
		String html = "<ul class='h-menu'><li>About Us</li><li>Privacy</li><li>Press</li><li>Contact</li></ul>" +
		              "<div>Brought to you by Hotmath.com</div>";
		   
		add(new HTML(html));
	}
}
