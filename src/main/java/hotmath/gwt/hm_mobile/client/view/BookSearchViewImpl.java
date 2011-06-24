package hotmath.gwt.hm_mobile.client.view;

import hotmath.gwt.cm_mobile_shared.client.AbstractPagePanel;
import hotmath.gwt.cm_mobile_shared.client.ControlAction;
import hotmath.gwt.cm_mobile_shared.client.TokenParser;
import hotmath.gwt.cm_mobile_shared.client.page.IPage;
import hotmath.gwt.hm_mobile.client.persist.HmMobilePersistedPropertiesManager;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class BookSearchViewImpl extends AbstractPagePanel implements BookSearchView,IPage  {

	Presenter presenter;
	
	private static BookSearchViewImplUiBinder uiBinder = GWT.create(BookSearchViewImplUiBinder.class);

	interface BookSearchViewImplUiBinder extends UiBinder<Widget, BookSearchViewImpl> {
	}

	public BookSearchViewImpl() {
		initWidget(uiBinder.createAndBindUi(this));
		
		textBox.addKeyPressHandler(new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent keyEvent) {
				char kc = keyEvent.getCharCode();
				if(kc == '\r') {
					doSearch(null);
				}
			}
		});
	}

	@Override
    public void setPresenter(Presenter pres) {
		this.presenter = pres;
		
		if(textBox.getText().length() == 0) {
			textBox.setText(HmMobilePersistedPropertiesManager.getInstance().getSearchTerm());
		}
    }
	

	@UiField
	TextBox textBox;


	@UiField
	Button search;
	
	@UiHandler("search")
	public void doSearch(ClickEvent e) {
		String searchFor = textBox.getText();
		if(searchFor.length() > 0) {
			HmMobilePersistedPropertiesManager.getInstance().setSearchTerm(searchFor);
			HmMobilePersistedPropertiesManager.save();
			
			presenter.doBookSearch(searchFor);
		}
	}

	@Override
	public String getBackButtonText() {
		// TODO Auto-generated method stub
		return "Back";
	}

	@Override
	public List<ControlAction> getControlFloaterActions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TokenParser getBackButtonLocation() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String getTitle() {
		return "Book Search";
	}
}
