package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_core.client.BackAction;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;

import java.util.List;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;

public class IPageAdapter extends Composite implements IPage {

    public IPageAdapter(IsWidget view) {
        initWidget(view.asWidget());
    }

    @Override
    public String getBackButtonText() {
        return null;
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
    public String getViewTitle() {
        return "View Adapter";
    }
    
    
    @Override
    public void isNowActive() {
        /** empty */
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
