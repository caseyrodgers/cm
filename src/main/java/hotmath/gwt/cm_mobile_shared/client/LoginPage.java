package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import java.util.List;

public class LoginPage implements IPage {
    
    public LoginPage() {
    }

	@Override
	public String getViewTitle() {
		return "Login into Catchup Math";
	}

	@Override
	public String getBackButtonText() {
		return "Back";
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
    
    @Override
    public void isNowActive() {
        /** empty */
    }

    @Override
    public ApplicationType getApplicationType() {
        return ApplicationType.NONE;
    }
    
    @Override
    public String getHeaderBackground() {
        return null;
    }

}
