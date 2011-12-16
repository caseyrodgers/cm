package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.WelcomePanel;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveFeedbackAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.rpc.action.SetBackgroundStyleAction;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.NetTestWindow;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.WindowListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;

public class HelpWindow extends CmWindow {

    ComboBox<BackgroundModel> bgCombo;

    public HelpWindow() {
        setAutoHeight(true);
        setWidth(490);
        setModal(true);
        setResizable(false);
        addStyleName("help-window");
        setHeading("Catchup Math Help Window, version: " +
        		   CatchupMathVersionInfo.getBuildVersion());

        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));

        if (CmMainPanel.__lastInstance != null) {
            CmMainPanel.__lastInstance.expandResourceButtons();
        }

        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                HelpWindow.this.close();
            }
        });
        addButton(closeBtn);
        
        addWindowListener(new WindowListener() {
        	public void windowHide(com.extjs.gxt.ui.client.event.WindowEvent we) {
            	EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED, this));
        	}
        });
        

        Html messageArea = new Html();
        try {
            String html = ContextController.getInstance().getTheContext().getStatusMessage();
            messageArea = new Html(html);
            
        } catch (Exception e) {
            CmLogger.error("Error getting context help", e);
            messageArea.setHtml("Catchup Math makes learning fun!");
        }
        
        
        messageArea.addStyleName("help-window-message-area");

        VerticalPanel vp = new VerticalPanel();
        
        FieldSet fs = new FieldSet();
        fs.setHeading("Using Catchup Math");
        fs.add(messageArea);
        vp.add(fs);
        
        if(CmShared.getQueryParameter("debug") != null) {
            FieldSet fsDebug = new FieldSet();
            fsDebug.setHeading("Debug Info");
            fsDebug.add(new Html(UserInfo.getInstance().getUserStatus()));
            vp.add(fsDebug);
        }

        
        bgCombo = new ComboBox<BackgroundModel>();
        bgCombo.setStore(getBackgroundListStores());
        bgCombo.setEditable(false);
        bgCombo.addStyleName("help-window-bg-combo");
        bgCombo.setEmptyText("-- Select Wallpaper --");
        bgCombo.setTriggerAction(TriggerAction.ALL);
        bgCombo.addSelectionChangedListener(new SelectionChangedListener<BackgroundModel>() {
            public void selectionChanged(final SelectionChangedEvent<BackgroundModel> se) {

                /**
                 * @TODO: use better check
                 * 
                 */
                if (!UserInfo.getInstance().isActiveUser()) {
                    // creating new account, do not allow changing wallpaper
                    CatchupMathTools
                            .showAlert("You will be able to change your wallpaper once you create your own account");
                    return;
                }

                
                new RetryAction<RpcData>() {
                    @Override
                    public void attempt() {
                        CmBusyManager.setBusy(true);
                        CmServiceAsync s = CmShared.getCmService();
                        SetBackgroundStyleAction action = new SetBackgroundStyleAction(UserInfo.getInstance().getUid(), se.getSelectedItem().getBackgroundStyle());
                        setAction(action);
                        s.execute(action,this);
                    }
                    public void oncapture(RpcData result) {
                        CmBusyManager.setBusy(false);
                        try {
                            String newStyle = se.getSelectedItem().getBackgroundStyle();
                            setBackgroundStyle(newStyle);
                        } finally {
                            CatchupMathTools.setBusy(false);
                        }
                    }
                }.register();
            }
        });

        fs = new FieldSet();
        fs.setHeading("Wallpaper");
        Label lab = new Label("Set which image to use for your Catchup Math wallpaper.");
        lab.addStyleName("bg-image-label");
        fs.add(lab);
        fs.add(bgCombo);

        vp.add(fs);

        if (UserInfo.getInstance().isSingleUser() || CmShared.getQueryParameter("debug") != null) {
            fs = new FieldSet();
            fs.setLayout(new FlowLayout());
            fs.setHeading("Configuration");
            fs.addStyleName("help-window-additional-options");
            Button btn = new MyOptionButton("Setup Catchup Math");
            btn.setToolTip("Modify your Catchup Math settings.");
            btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    showStudentConfiguration();
                }
            });
            btn.addStyleName("button");
            fs.add(btn);

            btn = new MyOptionButton("Restart");
            btn.setToolTip("Restart the current program.");
            btn.addStyleName("button");
            btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    MessageBox.confirm("Restart Program",
                            "Are you sure you would like to restart your current program?",
                            new Listener<MessageBoxEvent>() {
                                public void handleEvent(MessageBoxEvent be) {
                                    if (be.getButtonClicked().getText().equals("Yes")) {
                                        CmShared.resetProgram_Gwt(UserInfo.getInstance().getUid());
                                    }
                                }
                            });
                }
            });
            fs.add(btn);

            vp.add(fs);
        }

        fs = new FieldSet();
        fs.setLayout(new FlowLayout());
        fs.addStyleName("help-window-additional-options");
        fs.setHeading("Additional Options");

        SelectionListener<ButtonEvent> selList = new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CatchupMathTools.showAlert("Not available");
            }
        };

        Button btn = new MyOptionButton("Support");
        btn.addStyleName("button");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CatchupMathTools.showAlert("Please email support@hotmath.com for support.");
            }
        });
        fs.add(btn);
        btn = new MyOptionButton("Student History");
        btn.setToolTip("View your history of quizzes, reviews and show work efforts.");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showStudentHistory();
            }
        });
        
        Button btnFeedback = new MyOptionButton("Feedback");
        btnFeedback.addStyleName("button");
        btnFeedback.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showFeedbackPanel_Gwt();
            }
        });
        fs.add(btnFeedback);


        Button btnComputerCheck = new MyOptionButton("Computer Check");
        btnComputerCheck.addStyleName("button");
        btnComputerCheck.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new ComputerCheckWindow();
            }
        });
        fs.add(btnComputerCheck);
        /** Only the owner of the account has access to history */
        if (!UserInfo.getInstance().isActiveUser())
            btn.setEnabled(false);

        /** Do not allow Student History for demo user */
        if (UserInfo.getInstance().isDemoUser())
            btn.setEnabled(false);

        btn.addStyleName("button");
        fs.add(btn);

        vp.add(fs);
        add(vp);
        
        
        if(CmShared.getQueryParameter("debug") != null) {
            LayoutContainer lc = new HorizontalPanel();
            lc.add(new Button("Show CmLogger",new SelectionListener<ButtonEvent>() {
                @Override
                public void componentSelected(ButtonEvent ce) {
                    GWT.runAsync(new CmRunAsyncCallback() {
                        @Override
                        public void onSuccess() {
                            CmLogger.getInstance().enable(true);                        
                        }
                    });
                }
            }));
            lc.add(new Button("Connection Check", new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    GWT.runAsync(new CmRunAsyncCallback() {
                        @Override
                        public void onSuccess() {
                            new NetTestWindow(TestApplication.CM_STUDENT,UserInfo.getInstance().getUid()).runTests();
                        }
                    });
                }
            }));
            add(lc);
        }
    }
    

    /**
     * Remove any previous wallpaper styles, and make
     * sure this is the only one active.
     * 
     * Must check state of systemt to change the appropriate
     * panel. 
     *
     * @TODO: create EventType and listen for change to remove static calls.
     * 
     * NOTE: all wallpaper styles start with 'resource-container-'.
     * The welcome panel starts with 'cm-welcome-panel'
     * 
     */
    private void setBackgroundStyle(String newStyle) {
        
        LayoutContainer panelToChange = null;
        
        if(CmMainPanel.__lastInstance != null) {
            panelToChange = CmMainPanel.__lastInstance._mainContent;
        }
        else {
            panelToChange = WelcomePanel.__instance;
        }
        
        if(panelToChange != null) {
            String styleName = panelToChange.getStyleName();
            if (styleName != null) {
                String names[] = styleName.split(" ");
                for (int i = 0; i < names.length; i++) {
                    if (names[i].startsWith("resource-container-") ||
                            names[i].startsWith("cm-welcome-panel")) {
                            panelToChange.removeStyleName(names[i]);
                        }
                }
            }
            panelToChange.addStyleName(newStyle);
        }
        
        UserInfo.getInstance().setBackgroundStyle(newStyle);
    }

    /**
     * Provide method for single user to configure programs and settings
     * 
     */
    private void showStudentConfiguration() {
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
            new RetryAction<StudentModelI>() {
    
                @Override
                public void attempt() {
                    CmBusyManager.setBusy(true);
                    GetStudentModelAction action = new GetStudentModelAction(UserInfo.getInstance().getUid());
                    setAction(action);
                    CmShared.getCmService().execute(action,this);
                }
                public void oncapture(StudentModelI student) {
                    try {
                        CmAdminModel adminModel = new CmAdminModel();
                        adminModel.setId(student.getAdminUid());
                        new RegisterStudent(student, adminModel).showWindow();
                    }
                    finally {
                        CmBusyManager.setBusy(false);
                    }
                }
            }.register();
            }
        });
    }

    /**
     * Show this student's history. First we must get the student's StudentModel
     * to make sure we have the current information, then call the
     * StudentDetailWindow.
     * 
     * The StudentModel is read before showing details to make sure all summary
     * information is current and not what it was on login.
     */
    private void showStudentHistory() {
        
        GWT.runAsync(new CmRunAsyncCallback() {
            
            @Override
            public void onSuccess() {
                new RetryAction<StudentModelI>() {
                    @Override
                    public void attempt() {
                        CmBusyManager.setBusy(true);
                        GetStudentModelAction action = new GetStudentModelAction(UserInfo.getInstance().getUid());
                        setAction(action);
                        CmShared.getCmService().execute(action, this);
                    }
                    public void oncapture(StudentModelI student) {
                        CmBusyManager.setBusy(false);
                        new StudentDetailsWindow(new StudentModelExt(student));
                    }
                }.register();
            }
        });
    }


    /** Return two dim array showing registered
     *  and available background wallpapers.
     * @return
     */
    public static String[][] getBackgrounds() {

   	 String bgs[][] = {
    		{"Catchup Math","resource-container"},
    		{"Clouds","resource-container-clouds"},
    		{"Forest","resource-container-forest"},
    		{"Meadow","resource-container-sunrise"},
    		{"Mountain Bike","resource-container-bike1"},
    		{"Snowman","resource-container-snowman"},
    		{"Sunfield","resource-container-sunfield"},
    		{"Tulips","resource-container-tulips"},
    		{"Neutral","resource-container-neutral"},
    		{"Redish","resource-container-redish"},
    		{"No background image","resource-container-none"}
    };
    return bgs;
   }

    
    public  ListStore<BackgroundModel> getBackgroundListStores() {
        ListStore<BackgroundModel> backgrounds = new ListStore<BackgroundModel>();
        String bgMap[][] = getBackgrounds();
        for(String s[]: bgMap) {
        	backgrounds.add(new BackgroundModel(s[0],s[1]));
        }
        return backgrounds;
    }

    static public void showFeedbackPanel_Gwt() {
        
        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
        
        MessageBox.prompt("Feedback","Enter Catchup Math feedback.",true,new Listener<MessageBoxEvent> () {
            public void handleEvent(MessageBoxEvent be) {
                final String value = be.getValue();
                if(value == null || value.length() == 0)
                    return;
                
                new RetryAction<RpcData>() {
                    @Override
                    public void attempt() {
                        CmBusyManager.setBusy(true);
                        CmServiceAsync s = CmShared.getCmService();
                        SaveFeedbackAction action = new SaveFeedbackAction(value, "", getFeedbackStateInfo());
                        setAction(action);
                        s.execute(action,this);
                    }
                    public void oncapture(RpcData result) {
                    	CatchupMathTools.showAlert("Feedback saved");
                        CmBusyManager.setBusy(false);
                    }
                }.register();
            }
        });
    }
    

    /** Return string that represents current state of CM
     * 
     */
    static private String getFeedbackStateInfo() {
       String msg = "user_agent=" + CmShared.getBrowserInfo() + ", " + ContextController.getInstance().toString();
       return msg;
    }
}
        

class BackgroundModel extends BaseModelData {

	public BackgroundModel(String text, String style) {
		set("text", text);
		set("bg_style", style);
	}
	
    public String getBackgroundName() {
        return get("text");
    }

    public String getBackgroundStyle() {
        return get("bg_style");
    }
}

class MyOptionButton extends Button {
    
    public MyOptionButton(String name) {
        super(name);
        setWidth(110);        
    }
}


class ComputerCheckWindow extends CmWindow {
    public ComputerCheckWindow() {
        setHeading("Computer Check");
        addCloseButton();
        setModal(true);
        setSize(640,650);
        setLayout(new FitLayout());
        add(new ComputerCheckIFrame());
        
        setVisible(true);
    }
}

class ComputerCheckIFrame extends Frame {
    public ComputerCheckIFrame() {
        super("/system_checker.html?hide=true");
        DOM.setElementProperty(this.getElement(), "frameBorder", "no"); // disable
        DOM.setElementPropertyInt(this.getElement(), "border", 0); // disable
        DOM.setElementPropertyInt(this.getElement(), "frameSpacing", 0); // disable
        DOM.setElementProperty(this.getElement(), "scrolling", "yes"); // disable
    }
}