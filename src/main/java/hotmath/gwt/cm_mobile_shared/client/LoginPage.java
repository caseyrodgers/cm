package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

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
    public void setupControlFloater() {
        ControlPanel cp = CatchupMathMobileShared.__instance.getControlPanel();
        cp.hideControlPanelFloater();
    }
}
