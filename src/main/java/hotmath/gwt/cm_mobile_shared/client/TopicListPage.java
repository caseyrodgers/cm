package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import java.util.List;

public class TopicListPage implements IPage {
    
    public TopicListPage() {
    }

	@Override
	public String getTitle() {
		return "Lesson List";
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
