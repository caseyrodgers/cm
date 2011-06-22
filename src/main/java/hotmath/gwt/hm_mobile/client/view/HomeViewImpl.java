package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.hm_mobile.client.HmMobile;
import hotmath.gwt.hm_mobile.client.event.ShowCategoryListEvent;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;

public class HomeViewImpl extends AbstractPagePanel implements HomeView,IPage {

	private static HomeViewImplUiBinder uiBinder = GWT.create(HomeViewImplUiBinder.class);

	interface HomeViewImplUiBinder extends UiBinder<Widget, HomeViewImpl> {
	}

	Presenter presenter;
	
	@UiField
	Button searchTo,navigateTo;
	
	public HomeViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setText(String text) {
	}

	public String getText() {
		return "";
	}
	
	public void doShowBooks(ClickEvent clickHandler) {
		//HmMobile.__clientFactory.getEventBus().fireEvent(new ShowCategoryListEvent());
	}
	
	
	@Override
	public void showHome() {
	    System.out.println("Showing Home View");	
	}

	@Override
    public void setPresenter(Presenter presenter) {
		this.presenter=presenter;
    }

    @Override
    public String getBackButtonText() {
        return "You are home!";
    }

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;
    }

    @Override
    public TokenParser getBackButtonLocation() {
        return null;
    }
    
    @UiHandler("navigateTo")
    public void doNavigate(ClickEvent ce) {
        HmMobile.__clientFactory.getEventBus().fireEvent(new ShowCategoryListEvent());
    }
}
