package hotmath.gwt.cm_mobile2.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.IPage.BackAction;
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
    
    
    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }
    
    @Override
    public BackAction getBackAction() {
    	return null;
    }    
    
}
