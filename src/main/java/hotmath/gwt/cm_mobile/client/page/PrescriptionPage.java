package hotmath.gwt.cm_mobile.client.page;

import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;

public class PrescriptionPage implements IPage {

	PrescriptionSessionData sessionData;
	
	public PrescriptionPage(PrescriptionSessionData pdata) {
		sessionData = pdata;
	}
	
	@Override
	public String getTitle() {
		return "Prescription";
	}

	@Override
	public String getBackButtonText() {
		return "<< Quiz";
	}

}
