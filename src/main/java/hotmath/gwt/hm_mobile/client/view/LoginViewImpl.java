package hotmath.gwt.hm_mobile.client.view;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.cm_mobile_shared.client.ui.TouchButton;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;

public class LoginViewImpl extends AbstractPagePanel implements LoginView, IPage {

	interface MyUiBinder extends UiBinder<Widget, LoginViewImpl> {
	}
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	Presenter presenter;

    private CallbackOnComplete callback;

	public LoginViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}
    @Override
    public void resetView() {
    }

    @Override
    public String getViewTitle() {
        return "Hotmath Mobile";
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
    public void setPresenter(Presenter presenter, CallbackOnComplete callback) {
        this.presenter = presenter;
        this.callback = callback;
        
        // form is ready
        this.callback.isComplete();
    }
    
    @UiHandler("demoButton")
    protected void handleDemoButton(ClickEvent ce) {
        presenter.setupDemoMode();
    }

    
    @UiField
    TouchButton demoButton;
    
}
