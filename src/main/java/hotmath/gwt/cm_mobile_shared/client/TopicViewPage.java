package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import java.util.List;

public class TopicViewPage implements IPage {
    
    String file;
    
    public TopicViewPage(String file) {
        this.file = file;
    }

    public String getTopicFile() {
        return file;
    }
	@Override
	public String getTitle() {
		return "Catchup Math Topic View";
	}

	@Override
	public String getBackButtonText() {
		return "";
	}

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }
}
