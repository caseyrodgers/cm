package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import java.util.List;

public class LoginPage implements IPage {
    
    public LoginPage() {
    }

	@Override
	public String getTitle() {
		return "Login into Catchup Math";
	}

	@Override
	public String getBackButtonText() {
		return "Home";
	}

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }
}
