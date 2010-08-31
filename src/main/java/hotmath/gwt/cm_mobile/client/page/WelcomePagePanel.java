package hotmath.gwt.cm_mobile.client.page;


import hotmath.gwt.cm_mobile.client.AbstractPagePanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class WelcomePagePanel extends AbstractPagePanel {
	
	public WelcomePagePanel(WelcomePage page) {
		FlowPanel basePanel = new FlowPanel();
		basePanel.add(new HTML("HELLO WELCOME"));
		initWidget(basePanel);
	}

}
