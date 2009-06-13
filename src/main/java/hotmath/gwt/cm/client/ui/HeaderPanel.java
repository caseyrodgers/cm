package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.util.UserInfo;
import hotmath.gwt.shared.client.CmShared;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;

public class HeaderPanel extends LayoutContainer {

	static public HeaderPanel __instance;

	public HeaderPanel() {
		__instance = this;
	}

	Label _headerText;
	Html _helloInfo = new Html();
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		setStyleName("header-panel");
		
		
		_helloInfo.setStyleName("hello-info");
		add(_helloInfo);
		
		IconButton btn = new IconButton("header-panel-help-btn-icon");
		btn.setStyleName("header-panel-help-btn");
		btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				new HelpWindow().setVisible(true);
			};
		});		
		add(btn);
		
		btn = new IconButton("header-panel-logout-btn-icon");
		btn.setStyleName("header-panel-logout-btn");
		btn.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				Window.Location.assign(CmShared.CM_HOME_URL);
			};
		});		
		add(btn);
		
		
		_headerText = new Label();
		_headerText.setStyleName("header-panel-title");
		add(_headerText);
	}
	
	
	public void setLoginInfo() {
        UserInfo user = UserInfo.getInstance();
        int viewCount = UserInfo.getInstance().getViewCount();
        if(user != null) {
            String nameCap = user.getUserName();
            nameCap = nameCap.substring(0,1).toUpperCase() + nameCap.substring(1);
            String s = "Hello, <b>" +  nameCap + "</b>";
            if(viewCount > 1)
                s += ". You have viewed " + viewCount + " learning items.";
            _helloInfo.setHtml(s);
            layout();
        }
	}

	/**
	 * Update all info fields and titles in header areas
	 */
	public void setHeaderInfo() {
		CatchupMathTools.showAlert("Set Header info");	
	}
	
	/* Set the header title for this context */
	public void setHeaderTitle(String title) {
	   // CatchupMathTools.showAlert("Set Header info: " + title);  
	   //	_headerText.setText(title);
	}

}
