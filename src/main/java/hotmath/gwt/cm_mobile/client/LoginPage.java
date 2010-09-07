package hotmath.gwt.cm_mobile.client;

import hotmath.gwt.cm_mobile.client.page.IPage;

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
        ControlPanel cp = CatchupMathMobile.__instance.getControlPanel();
        cp.hideControlPanelFloater();
    }
}
