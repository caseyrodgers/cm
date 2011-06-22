package hotmath.gwt.hm_mobile.client.view;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class MainMobileViewImpl extends Composite implements MainMobileView {

	private static MainMobileViewImplUiBinder uiBinder = GWT.create(MainMobileViewImplUiBinder.class);

	interface MainMobileViewImplUiBinder extends UiBinder<Widget, MainMobileViewImpl> {
	}

	@UiField
	Button navigateTo, searchTo;
	
		
	Presenter presenter;

	public MainMobileViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@Override
    public void setWidget(IsWidget widget) {
	
    }

	@Override
    public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
    }


	@UiHandler("navigateTo")
	public void doNavigate(ClickEvent e) {
		presenter.navigateToBook();
	}
	
	@UiHandler("searchTo")
	public void doSearch(ClickEvent e) {
		presenter.searchForBook();
	}
	
	@Override
    public void setupView() {
    }
}
