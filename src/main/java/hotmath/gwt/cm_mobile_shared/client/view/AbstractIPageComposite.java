package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;

abstract public class AbstractIPageComposite extends Composite implements IPage {


    @Override
    abstract public ApplicationType getApplicationType();


    @Override
    abstract public String getViewTitle();

    @Override
    public String getBackButtonText() {
        return "Back";
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
    public void isNowActive() {
    }
    
    @Override
    public String getHeaderBackground() {
        return null;
    }

}
