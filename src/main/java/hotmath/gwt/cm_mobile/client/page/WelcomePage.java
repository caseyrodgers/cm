package hotmath.gwt.cm_mobile.client.page;

import hotmath.gwt.cm_mobile.client.CatchupMathMobile;

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
        CatchupMathMobile.__instance.getControlPanel().hideControlPanelFloater();
    }
}
