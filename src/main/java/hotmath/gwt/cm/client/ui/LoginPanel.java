package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class LoginPanel extends LayoutContainer {
	
    TextField userName;
	TextField passcode;
	
	public LoginPanel() {
		
		setStyleName("login-panel");
		
		Image img = new Image("/gwt-resources/images/prescription_resource_background-1600x1050.jpg");
		img.setStyleName("login-background-img");
		img.setHeight("100%");
		img.setWidth("100%");
		
		add(img);

		LayoutContainer cp = new LayoutContainer();
		cp.setStyleName("login-panel-container");
		
        
		LayoutContainer form = new LayoutContainer();
		form.addStyleName("login-panel-form");

		Label title = new Label("Login to Catchup Math");
		title.addStyleName("login-panel-form-title");
		form.add(title);
		HorizontalPanel hp = new HorizontalPanel();
        hp.setStyleName("row");
        Label lab = new Label("User Name: ");
        lab.addStyleName("field");
        hp.add(lab);
        userName = new TextField();
        userName.addStyleName("value");
        userName.addKeyListener(new KeyListener() {
            public void componentKeyUp(ComponentEvent event) {
                if(event.getKeyCode() == 13) {
                    doLogin();
                }
            }
        });
        hp.add(userName);
        form.add(hp);
        		
		
		hp = new HorizontalPanel();
		hp.setStyleName("row");
		lab = new Label("Password: ");
		lab.addStyleName("field");
		hp.add(lab);
		passcode = new TextField();
		passcode.addStyleName("value");
		passcode.addKeyListener(new KeyListener() {
			public void componentKeyUp(ComponentEvent event) {
				if(event.getKeyCode() == 13) {
					doLogin();
				}
			}
		});
		hp.add(passcode);

		Button loginButton = new Button("Login");
		loginButton.setStyleName("login-action");
		loginButton.setIconStyle("login-action-background");
		loginButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				doLogin();
			}
		});
		hp.add(loginButton);
		
        form.add(hp);

		Anchor anchor = new Anchor("Forgot your passcode?");
		anchor.setStyleName("login-panel-form-forgot");
		anchor.addClickHandler(new ClickHandler() {
		    public void onClick(ClickEvent arg0) {
				CatchupMath.showAlert("Contact Hotmath support ... ");
			}
		});
		form.add(anchor);
		
		cp.add(form);
		
		//Image img = new Image("images/login_bkg.png");
		//img.setStyleName("background-img");
		
		add(cp);
	}
	
	public void resetContext() {
		History.newItem("login");
	}
	
	public String getContextTitle() {
		return "Login to Catcup Math";
	}	


	public String getContextHelp() {
		return "Log into Catchup Math";
	}


	/** Need to populate custom data structures
	 *  to define this user's current position
	 *  in their 
	 */
	private void doLogin() {
		String p=(String)passcode.getValue();
		if(p == null || p.length() == 0) {
			// do nothing
		}
		else if(!p.equalsIgnoreCase("test")){
			CatchupMath.showAlert("Passcode does not exist.  Hint: try 'test'");
		}
		else {
			CatchupMath.getThisInstance().showQuizPanel();
		}
	}

	public NextPanelInfo getNextPanelInfo() {
		return new NextPanelInfoImplDefault() {
			public void doNext() {
				CatchupMath.getThisInstance().showQuizPanel();
			}
			public Widget getNextPanelWidget() {
				return new HTML("Login to Catchup Math.");
			}
		};
	}
}
