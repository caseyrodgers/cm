package hotmath.gwt.cm_mobile.client.page;

import hotmath.gwt.cm_mobile.client.AbstractPagePanel;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;

public class PrescriptionResourcePagePanel extends AbstractPagePanel {
	
	PrescriptionResourcePage page;
	public PrescriptionResourcePagePanel(PrescriptionResourcePage page) {
		this.page = page;
		
		FlowPanel fp = new FlowPanel();
		fp.add(new HTML("THE RESOURCE"));
		
		initWidget(fp);
	}
}
