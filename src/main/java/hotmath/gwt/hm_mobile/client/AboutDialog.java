package hotmath.gwt.hm_mobile.client;


import hotmath.gwt.hm_mobile.client.model.HmMobileLoginInfo;
import hotmath.gwt.hm_mobile.client.persist.HmMobilePersistedPropertiesManager;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;

public class AboutDialog extends DialogBox  {
	
    private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

    interface AboutDialogUiBinder extends UiBinder<Widget, AboutDialog> {
    }

	
    @UiField 
    Element welcome;
    
	public AboutDialog() {
		super(true);
		setSize("400px", "250px");
		setText("Math Homework Help");		
		setGlassEnabled(true);
		
		setWidget(uiBinder.createAndBindUi(this));
		
		HmMobileLoginInfo li = HmMobile.__instance.getLoginInfo();
		String userName = li!=null?li.getUser():null;
		if(userName != null) {
		    String expired = null;
		    try {
		        expired = HmMobilePersistedPropertiesManager._expiredDateFormat.format(li.getDateExpired());
		    }
		    catch(Exception e) {
		        Log.error("Could not format date expired", e);
		    }
		    welcome.setInnerHTML("Welcome " + userName + "<br/>Your account expires: " + expired);
		}
		else {
	          welcome.setInnerHTML("You are not logged in");
		}
		
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
