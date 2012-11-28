package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.history.CmHistoryQueue;
import hotmath.gwt.cm.client.ui.context.ContextChangeMessage;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.InfoPopupBox;
import hotmath.gwt.cm_tools.client.ui.context.CmContext;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.CmEventListenerImplDefault;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.util.CmInfoConfig;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;

import com.extjs.gxt.ui.client.event.IconButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.IconButton;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Label;

public class HeaderPanel extends LayoutContainer {

	static public HeaderPanel __instance;

	public HeaderPanel() {
		__instance = this;
	}

	Label _headerText;
	Html _helloInfo = new Html();
	private IconButton helpButton;
	Anchor _assignmentsAnchor;
	protected void onRender(Element parent, int index) {
		super.onRender(parent, index);

		setStyleName("header-panel");
		
		_assignmentsAnchor = new Anchor("Assignments");
		_assignmentsAnchor.addStyleName("assigments_anchor");
		_assignmentsAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                
//                new StudentAssignmentViewerWindow(new CallbackOnComplete() {
//                    @Override
//                    public void isComplete() {
//                    }
//                });
        
                if(!UserInfo.getInstance().isDemoUser()) {
                    CatchupMath.getThisInstance().showAssignments_gwt();
                }
                else {
                    CatchupMathTools.showAlert("Assignments are not available for demo accounts");
                }
            }
            
            
        });
		
		
		if(CmShared.getQueryParameter("debug") != null) {
		    add(_assignmentsAnchor);
		}
		
		_helloInfo.setStyleName("hello-info");
		add(_helloInfo);
		
		helpButton = new IconButton("header-panel-help-btn");
		helpButton.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				
				if(ce.getEvent().getCtrlKey()) {
					String url = CmShared.getServerForCmStudent() + "/loginService?debug=true&uid=" + UserInfo.getInstance().getUid();
					CatchupMathTools.showAlert("Catchup Math Connection URL", url);
				}
				else {
					GWT.runAsync(new CmRunAsyncCallback() {
						@Override
						public void onSuccess() {
							new HelpWindow();
						}
					});
				}
			};
		});		
		add(helpButton);

		_headerText = new Label();
		_headerText.addStyleName("header-panel-title");
		//add(_headerText);
		
		
		EventBus.getInstance().addEventListener(new CmEventListenerImplDefault() {
		    public void handleEvent(final CmEvent event) {
		    	switch(event.getEventType()) {
			    	case EVENT_TYPE_USERCHANGED:
			            setLoginInfo();
			            break;
			            
			    	case EVENT_TYPE_CONTEXTCHANGED:
			            CmContext context = (CmContext)event.getEventData();
	  		            boolean tr = CmMainPanel.__lastInstance == null;
	  		            if(CmMainPanel.__lastInstance != null) {
	  		                /** note we set a default heading, no matter what the test type */
			                CmMainPanel.__lastInstance._westPanelWrapper.setHeadingText(context.getContextSubTitle());
	  		            }
	  		            break;
			    	case EVENT_TYPE_TOPIC_CHANGED:
					        /** Only show modal popup if not in auto test mode 
					         * 
					         */
					        if(CmShared.getQueryParameter("debug") != null || UserInfo.getInstance().isAutoTestMode() || CmHistoryQueue.getInstance().isInitializingToNonStandard())
					            InfoPopupBox.display(new CmInfoConfig("Current Topic", "Current topic is: " + event.getEventData()));
					        else {
					            new ContextChangeMessage((String)event.getEventData());
					        }
			    	break;
			    	
			    	case EVENT_TYPE_USER_LOGIN:
			    		/** done after login to allow parter info to be set first */
			    		addLogoutButton();
			    		break;
			    		
			    		
			    	case EVENT_TYPE_LOGOUT:
			    	    setLogout();
			    	    break;
			    	    
			    	    
			    	case EVENT_TYPE_ASSIGNMENTS_UPDATED:
			    	    updateAssignmentMessage((Boolean)event.getEventData());
			    	    break;
		    	}
			    		
		    }
	    });
	}
	
	
	private void updateAssignmentMessage(boolean incompleteAssignments) {
	    if(incompleteAssignments) {
	        _assignmentsAnchor.setText("You have Assignments");
	    }
	    else {
	        _assignmentsAnchor.setText("You do not have Assignments");
	    }
	}
	
	/** TODO: how to share this between student and admin 
	 * 
	 */
	IconButton logoutButton;
	private void addLogoutButton() {
		final CmPartner partner = UserInfoBase.getInstance().getPartner();
		String logoClass=null;
		if(partner != null) {
			logoClass = "header-panel-logout-btn_cm-partner-" + partner.key;
		}
		else {
			logoClass = "header-panel-logout-btn";
		}
			
		logoutButton = new IconButton(logoClass);
		logoutButton.addSelectionListener(new SelectionListener<IconButtonEvent>() {
			public void componentSelected(IconButtonEvent ce) {
				
				if(partner != null) {
					CmLogger.info("Doing custom thing: " + partner.onCloseLink);
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
		add(logoutButton);
	}
	private void setLogout() {
	    if(logoutButton != null) {
	        remove(logoutButton);
	        logoutButton = null;
	    }
	    
	    if(helpButton != null) {
	        remove(helpButton);
	        helpButton = null;
	    }
	    
	    layout();
	}
	
	public void setLoginInfo() {
        UserInfo user = UserInfo.getInstance();
        int viewCount = UserInfo.getInstance().getViewCount();
        if(user != null) {
            String nameCap = user.getUserName();
            if(nameCap == null)
                return;
            
            /** Check for demo user and normalize the display name
             * 
             */
            if(nameCap.startsWith("Student: "))
                nameCap = "Student";
            
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

}
