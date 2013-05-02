package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.page.IPage.BackAction;

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
	public String getViewTitle() {
		return "Lesson";
	}

	@Override
	public String getBackButtonText() {
		return "Search";
	}

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }
    
    @Override
    public TokenParser getBackButtonLocation() {
        return new TokenParser();
    }
    
    
    @Override
    public BackAction getBackAction() {
    	return null;
    }    
}
