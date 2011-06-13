package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListener;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.gwt.shared.client.model.UserInfoBase;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

public class HeaderPanel extends LayoutContainer {

	static public HeaderPanel instance;
	
	public HeaderPanel() {
		instance = this;
	}

	Label headerText;
	//Label schoolLabel;
	Html  schoolLabel;
	
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		setStyleName("header-panel");
		IconButton btn = new IconButton("header-panel-help-btn");
		btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
            	if(ce.getEvent().getCtrlKey()) {
					String url = CmShared.getServerForCmStudent() + "/loginService?type=ADMIN&debug=true&uid=" + StudentGridPanel.instance._cmAdminMdl.getId();
					CatchupMathTools.showAlert("Catchup Math Connection URL", url);
				}            	
            	else 
            		new HelpWindow();
			};
		});		
		add(btn);
		
		schoolLabel = new Html();
		//schoolLabel.setStyleName("header-panel-school-label");
		//setSchoolInfo();
		add(schoolLabel);
		
		headerText = new Label();
		headerText.setStyleName("header-panel-title");
		add(headerText);
		
        IconButton guide = new IconButton("header-panel-guide-btn");
        guide.setToolTip("Find out how to get started with Catchup Math");
        guide.addSelectionListener(new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                new GettingStartedGuideWindow();
            }
        });
        add(guide);
        
        IconButton training = new IconButton("header-panel-training-btn");
        training.setToolTip("Watch a 40-minute video about teaching with Catchup Math");
        training.addSelectionListener(new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
                new WebinarWindow();
            }
        });
        add(training);
        
        // TODO: add button change via CSS when updates page is changed
        IconButton updates = new IconButton("header-panel-updates-btn");
        updates.setToolTip("Learn about recent Catchup Math updates");
        updates.addSelectionListener(new SelectionListener<IconButtonEvent>() {
            public void componentSelected(IconButtonEvent ce) {
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
		btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				
				if(partner != null) {
					try {
						Window.Location.assign(partner.onCloseLink);
					}
					catch(Exception e) {
					    CatchupMathTools.showAlert("Error returning to our partner page: " + e.getMessage());
					}
				}
				else {
					Window.Location.assign(CmShared.CM_HOME_URL);
				}
			};
		});		
		add(btn);
		layout();
	}

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
