package hotmath.gwt.cm_mobile.client.page;

import hotmath.gwt.cm_mobile.client.CatchupMathMobile;
import hotmath.gwt.cm_mobile.client.ControlAction;
import hotmath.gwt.cm_mobile.client.ControlPanel;
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
        ControlPanel cp = CatchupMathMobile.__instance.getControlPanel();
        cp.setControlActions(new ArrayList<ControlAction>());
        // cp.hideControlPanelFloater();
    }
}
