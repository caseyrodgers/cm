package hotmath.gwt.cm_mobile_shared.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HmMobileWindow extends DialogBox {
	
	private HmMobileWindow() {
		super(true);
		setSize("280px", "100px");
		setGlassEnabled(true);
		setWidget(createUi());
		
		setAnimationEnabled(true);
		setAutoHideEnabled(true);
	}
	
	HTML _messageInfo = new HTML();
	private Widget createUi() {
		return _messageInfo;
	}
	
	public void showCentered() {
		center();
	}
	
	@UiHandler("closeButton")
	protected void onCloseButton(ClickEvent ce) {
		this.hide();
	}
	
	
	static HmMobileWindow __instance = new HmMobileWindow();
	static public void alert(String title, String message) {
		__instance.setText(title);
		__instance._messageInfo.setHTML(message);
		__instance.showCentered();
	}
}
