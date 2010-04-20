package hotmath.gwt.cm_mobile.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class LoginForm extends Composite {
    
    public LoginForm() {
        initWidget(uiBinder.createAndBindUi(this));
    }
    
    interface MyUiBinder extends UiBinder<HorizontalPanel, LoginForm> {}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
}