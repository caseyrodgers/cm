package hotmath.gwt.assignment_print.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class HeaderPanel extends Composite {
	
	public HeaderPanel() {
		setStyleName("header_panel");
		
		
		FlowPanel flow = new FlowPanel();
		flow.add(new HTML("<h1>Catchup Math Assignment Print</h1>"));
	}
}
