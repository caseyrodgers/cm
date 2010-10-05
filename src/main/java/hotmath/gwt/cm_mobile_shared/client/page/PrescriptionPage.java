package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;

import java.util.List;

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

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }
}
