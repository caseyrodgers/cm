package hotmath.gwt.cm_mobile_shared.client;

import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import java.util.List;

public class TopicListPage implements IPage {
    
    public TopicListPage() {
    }
    
    @Override
    public void isNowActive() {
        /** empty */
    }

	@Override
	public String getViewTitle() {
		return "Lesson Search";
	}

	@Override
	public String getBackButtonText() {
		return "";
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
    public ApplicationType getApplicationType() {
        return ApplicationType.PROGRAM;        
    }

    @Override
    public String getHeaderBackground() {
        return null;
    }

}
