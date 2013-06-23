package hotmath.gwt.cm_mobile_shared.client.view;

import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;

import com.google.gwt.user.client.ui.FlowPanel;

public class SubToolBar extends FlowPanel {
    private TouchButton _yourProgram;

    public SubToolBar() {
        super();
        addStyleName("SubToolBar");
    }

    public void showReturnTo(boolean b) {
        if(_yourProgram != null) {
            _yourProgram.setVisible(b);
        }
    }
}