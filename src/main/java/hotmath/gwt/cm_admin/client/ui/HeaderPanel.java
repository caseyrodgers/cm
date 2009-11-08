package hotmath.gwt.cm_admin.client.ui;

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
			   new HelpWindow();
			};
		});		
		add(btn);
		
		btn = new IconButton("header-panel-logout-btn");
	    btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				//CmShared.logout();
				Window.Location.assign("/");
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
