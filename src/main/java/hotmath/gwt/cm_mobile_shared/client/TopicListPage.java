package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

public class TopicListPage implements IPage {
    
    public TopicListPage() {
    }

	@Override
	public String getTitle() {
		return "Catchup Math Topic List";
	}

	@Override
	public String getBackButtonText() {
		return "Home";
	}

    @Override
    public void setupControlFloater() {
        ControlPanel cp = CatchupMathMobileShared.__instance.getControlPanel();
    }
}
