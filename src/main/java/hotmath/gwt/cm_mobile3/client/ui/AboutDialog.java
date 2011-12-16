package hotmath.gwt.cm_mobile3.client.ui;

import hotmath.gwt.cm_mobile3.client.data.SharedData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class AboutDialog extends DialogBox  {
	
    private static AboutDialogUiBinder uiBinder = GWT.create(AboutDialogUiBinder.class);

    interface AboutDialogUiBinder extends UiBinder<Widget, AboutDialog> {
    }

	
	public AboutDialog() {
		super(true);
		setSize("300px", "250px");
		setText("Info");		
		setGlassEnabled(true);
		setAnimationEnabled(true);
		setAutoHideEnabled(true);

        TabPanel tp = new TabPanel();
        tp.add(uiBinder.createAndBindUi(this), "Info");
        tp.add(new FeedbackPanel(), "Feedback");
        tp.selectTab(0);
        
        setWidget(tp);      
		
		String loggedIn="Not logged in";
		if(SharedData.getUserInfo() != null) {
		    loggedIn = SharedData.getUserInfo().getUserName();
		}
		loggedInAs.setInnerHTML(loggedIn);
        
        setVisible(true);
	}
	
	public void showCentered() {
		center();
	}
	
	@UiHandler("closeButton")
	protected void onCloseButton(ClickEvent ce) {
		this.hide();
	}
	
	@UiField
	Element loggedInAs;
}


class FeedbackPanel extends FlowPanel {
    
    public FeedbackPanel() {
        setWidth("100%");
        setHeight("200px");
        
        add(new Label("Feedback!"));
    }
    
}
