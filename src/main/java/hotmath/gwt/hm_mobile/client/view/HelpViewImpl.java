package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;

public class HelpViewImpl extends AbstractPagePanel implements HelpView, IPage {

	private static HelpViewImplUiBinder uiBinder = GWT.create(HelpViewImplUiBinder.class);

	interface HelpViewImplUiBinder extends UiBinder<Widget, HelpViewImpl> {
	}

	Presenter presenter;

	public HelpViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setText(String text) {
	}

	public String getText() {
		return "";
	}

	@Override
    public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
    }

    @Override
    public String getBackButtonText() {
        return "<<";
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
    public String getTitle() {
        return "Help!";
    }

	@Override
	public void showHelp() {
		/** empty for now */
	}
}
