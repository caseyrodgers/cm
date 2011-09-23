package hotmath.gwt.cm_tools.client.ui.viewer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class TutorWrapperPanel extends Composite {

    interface MyUiBinder extends UiBinder<Widget, TutorWrapperPanel> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    public TutorWrapperPanel() {
        initWidget(uiBinder.createAndBindUi(this));
    }
}
