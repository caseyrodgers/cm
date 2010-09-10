package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.ControlPanel;
import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionData;

import java.util.ArrayList;

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
    public void setupControlFloater() {
        ControlPanel cp = CatchupMathMobileShared.__instance.getControlPanel();
        cp.setControlActions(new ArrayList<ControlAction>());
        // cp.hideControlPanelFloater();
    }
}
