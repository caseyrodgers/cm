package hotmath.gwt.hm_mobile.client;

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
		setText("About Hotmath Mobile");		
		setGlassEnabled(true);
		setWidget(uiBinder.createAndBindUi(this));
		
		
		setAnimationEnabled(true);
		setAutoHideEnabled(true);
		
		setVisible(true);
	}
	
	public void showCentered() {
		center();
	}
	
	@UiHandler("closeButton")
	protected void onCloseButton(ClickEvent ce) {
		this.hide();
	}
}
