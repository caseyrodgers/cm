package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile3.client.CatchupMathMobile3;
import hotmath.gwt.cm_mobile3.client.event.ShowLoginViewEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class AboutDialog extends DialogBox  {
	
    private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

    interface AboutDialogUiBinder extends UiBinder<Widget, AboutDialog> {
    }

	
	public AboutDialog() {
		super(true);
		setSize("300px", "250px");
		setText("Catchup Math Mobile Information");		
		setGlassEnabled(true);
		setWidget(uiBinder.createAndBindUi(this));
		
		
		setAnimationEnabled(true);
		setAutoHideEnabled(true);
		
		setVisible(true);
	}
	
	public void showCentered() {
		center();
	}
	
	@UiHandler("logoutButton")
	protected void onLogoutButton(ClickEvent ce) {
	    CatchupMathMobile3.__clientFactory.getEventBus().fireEvent(new ShowLoginViewEvent());
	}
	
	@UiHandler("closeButton")
	protected void onCloseButton(ClickEvent ce) {
		this.hide();
	}
}
