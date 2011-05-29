package hotmath.gwt.cm_admin.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CustomProgramHelpBinder extends Composite  {
    private static CustomProgramHelpBinderUiBinder uiBinder = GWT.create(CustomProgramHelpBinderUiBinder.class);
    interface CustomProgramHelpBinderUiBinder extends UiBinder<Widget, CustomProgramHelpBinder> {
    }
    public CustomProgramHelpBinder() {
        initWidget(uiBinder.createAndBindUi(this));
    }
}
