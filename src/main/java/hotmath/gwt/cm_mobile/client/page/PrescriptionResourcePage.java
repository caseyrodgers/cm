package hotmath.gwt.cm_mobile.client.page;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;

public class PrescriptionResourcePage implements IPage {

	InmhItemData item;
	
	public PrescriptionResourcePage(InmhItemData item) {
		this.item = item;
	}
	
	@Override
	public String getTitle() {
		return "Prescription Resource";
	}

	@Override
	public String getBackButtonText() {
		return "Prescription";
	}

}
