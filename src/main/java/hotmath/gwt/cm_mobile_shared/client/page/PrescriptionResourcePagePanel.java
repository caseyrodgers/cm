package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.CmMobileResourceViewerFactory;

public class PrescriptionResourcePagePanel extends AbstractPagePanel {
	
	PrescriptionResourcePage page;
	public PrescriptionResourcePagePanel(PrescriptionResourcePage page) {
		this.page = page;
		initWidget(CmMobileResourceViewerFactory.createViewer(page.getItem()).getViewer(page.getItem()));
	}
}
