package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.util.CmMessageBoxGxt2;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.widget.core.client.button.IconButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;

public class HeaderPanel extends FlowLayoutContainer {

	static public HeaderPanel instance;

	Label headerText;
	//Label schoolLabel;
	HTML  schoolLabel;
	
	public HeaderPanel() {
		instance = this;

		setStyleName("header-panel");
		IconButton btn = new IconButton("header-panel-help-btn");
		btn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
            		new HelpWindow();
			};
		});		
		add(btn);
		
		schoolLabel = new HTML();
		//schoolLabel.setStyleName("header-panel-school-label");
		//setSchoolInfo();
		add(schoolLabel);
		
		headerText = new Label();
		headerText.setStyleName("header-panel-title");
		add(headerText);
		
        IconButton guide = new IconButton("header-panel-guide-btn");
        guide.setToolTip("Find out how to get started with Catchup Math");
        guide.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                new GettingStartedGuideWindow();
            }
        });
        add(guide);
        
        IconButton training = new IconButton("header-panel-training-btn");
        training.setToolTip("Watch our Training Videos");
        training.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
            	showTrainingVideosPage();
            }
        });
        add(training);
        
        // TODO: add button change via CSS when updates page is changed
        IconButton updates = new IconButton("header-panel-updates-btn");
        updates.setToolTip("Learn about recent Catchup Math updates");
        updates.addSelectHandler(new SelectHandler() {
			
			@Override
			public void onSelect(SelectEvent event) {
                new RecentUpdatesWindow();
            }
        });
        add(updates);

        

        EventBus.getInstance().addEventListener(new CmEventListener() {
			
			@Override
			public void handleEvent(CmEvent event) {
				switch(event.getEventType()) {
		    	case EVENT_TYPE_USER_LOGIN:
		    		addLogoutButton();
		    		break;
				}
			}
		});
	}

	private void addLogoutButton() {
		final CmPartner partner = UserInfoBase.getInstance().getPartner();
		String logoClass=null;
		if(partner != null) {
			logoClass = "header-panel-logout-btn_cm-partner-" + partner.key;
		}
		else {
			logoClass = "header-panel-logout-btn";
		}
			
		IconButton btn = new IconButton(logoClass);
		btn.addSelectHandler(new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				
				if(partner != null) {
					try {
						Window.Location.assign(partner.onCloseLink);
					}
					catch(Exception e) {
					    CmMessageBoxGxt2.showAlert("Error returning to our partner page: " + e.getMessage());
					}
				}
				else {
					Window.Location.assign(CmShared.CM_HOME_URL);
				}
			};
		});		
		add(btn);
	}

    private native void showTrainingVideosPage() /*-{
        var tv = window.open('/training-videos');
        tv.focus();
    }-*/;

	/**
	 * Update all info fields and titles in header areas
	 */
	public void setHeaderInfo() {
		setHeaderTitle("Catchup Math Admin");
	}
	
	/* Set the header title */
	public void setHeaderTitle(String title) {
		headerText.setText(title);
	}

}
