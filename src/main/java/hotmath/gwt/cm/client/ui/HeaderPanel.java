package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.InfoConfig;
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
		
		
		EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
		    public void handleEvent(CmEvent event) {
		        if(event.getEventName().equals(EventBus.EVENT_TYPE_USERCHANGED)) {
		            setLoginInfo();
		        }
		        else if(event.getEventName().equals(EventBus.EVENT_TYPE_CONTEXTCHANGED)) {
		            CmContext context = (CmContext)event.getEventData();
  		            HeaderPanel.__instance.setHeaderTitle();
		            CmMainPanel.__lastInstance._westPanel.setHeading(context.getContextSubTitle());
		        }
		        else if(event.getEventName().equals(EventBus.EVENT_TYPE_TOPIC_CHANGED)) {
		            /**
		            if(CmShared.getQueryParameter("debug") != null) {
    		            String title = "The lesson is '" + event.getEventData() + "'";
    		            String message = "Use any menu items you wish, before or after trying the Required Practice Problems.";
    		            InfoConfig infoConfig = new MyInfoConfig(title,message);
                        InfoPopupBox.display(infoConfig);
		            }
		            */
		        }
		    }
		});
	}
	
	
	public void setLoginInfo() {
        UserInfo user = UserInfo.getInstance();
        int viewCount = UserInfo.getInstance().getViewCount();
        if(user != null) {
            String nameCap = user.getUserName();
            nameCap = nameCap.substring(0,1).toUpperCase() + nameCap.substring(1);
            String s = "Hello, <b>" +  nameCap + "</b>";
            if(viewCount > 1)
                s += ". You have worked on " + viewCount + " problems.";
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
	public void setHeaderTitle() {
	   // CatchupMathTools.showAlert("Set Header info: " + UserInfo.getInstance().getSubTitle());
	    String subTitle = UserInfo.getInstance().getSubTitle();
	   _headerText.setText(subTitle != null?"Chapter " + subTitle:"");
	}

}


class MyInfoConfig extends InfoConfig {
    public MyInfoConfig(String t, String m) {
        super(t, m);
        display = 5000;
        width = 300;
    }
}