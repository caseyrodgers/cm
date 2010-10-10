package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;

import java.util.List;



public class TestPage implements IPage {

    @Override
    public String getTitle() {
        return "Test Title";
    }

    @Override
    public String getBackButtonText() {
        return "Test";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }
    
}