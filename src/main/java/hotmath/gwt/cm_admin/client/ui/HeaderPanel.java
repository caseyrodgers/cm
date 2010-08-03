package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.util.UserInfo;

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
		
		btn = new IconButton("header-panel-logout-btn");
	    btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				/** todo: find how to share this between student and admin
				 * 
				 */
				CmPartner partner = UserInfoBase.getInstance().getPartner();
				if(partner != null) {
					CmLogger.info("Doing custom admin thing: " + partner.onCloseLink);
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
