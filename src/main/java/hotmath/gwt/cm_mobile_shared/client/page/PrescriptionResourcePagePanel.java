package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.CmMobileResourceViewerFactory;

import com.google.gwt.user.client.ui.Widget;

public class PrescriptionResourcePagePanel extends AbstractPagePanel {
	
	PrescriptionResourcePage page;
	public PrescriptionResourcePagePanel(PrescriptionResourcePage page) {
		this.page = page;
		Widget viewer = CmMobileResourceViewerFactory.createViewer(page.getItem()).getViewer(page.getItem());
		initWidget(viewer);
	}
}
