package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.CatchupMathMobileShared;


public class WelcomePage implements IPage {

	@Override
	public String getTitle() {
		return "Welcome Page";
	}

	@Override
	public String getBackButtonText() {
		return "Back to Welcome Page";
	}

    @Override
    public void setupControlFloater() {
        CatchupMathMobileShared.__instance.getControlPanel().hideControlPanelFloater();
    }
}
