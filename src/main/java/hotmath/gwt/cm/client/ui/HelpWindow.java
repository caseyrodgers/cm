package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.WelcomePanel;
import hotmath.gwt.cm.client.ui.context.PrescriptionCmGuiDefinition;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveFeedbackAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.CalculatorWindow;
import hotmath.gwt.cm_tools.client.util.IFramedPanelWindow;
import hotmath.gwt.cm_tools.client.util.StudentHowToFlashWindow;
import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.rpc.action.SetBackgroundStyleAction;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.NetTestWindow;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

public class HelpWindow extends GWindow {

    ComboBox<BackgroundModel> bgCombo;

    public HelpWindow() {

        super(false);
        setPixelSize(490, 500);
        
        FlowLayoutContainer flc = new FlowLayoutContainer();

        setModal(true);
        setResizable(false);
        //  addStyleName("help-window");
        setHeadingText("Catchup Math Help Window, version: " + CatchupMathVersionInfo.getBuildVersion());

//        if (CmMainPanel.__lastInstance != null) {
//            CmMainPanel.__lastInstance.removeResource();
//            PrescriptionCmGuiDefinition.showHelpPanel();
//            CmMainPanel.__lastInstance.expandResourceButtons();
//        }

        // EventBus.getInstance().fireEvent(new
        // CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN, this));

        TextButton closeBtn = new TextButton("Close");
        closeBtn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                HelpWindow.this.close();
            }
        });
        addButton(closeBtn);

        HTML messageArea = new HTML();
        try {
            String html = ContextController.getInstance().getTheContext().getStatusMessage();
            messageArea = new HTML(html);

        } catch (Exception e) {
            CmLogger.error("Error getting context help", e);
            messageArea.setHTML("Catchup Math makes learning fun!");
        }

        messageArea.addStyleName("help-window-message-area");


        FieldSet fs = new FieldSet();
        fs.setHeadingText("Using Catchup Math");
        fs.add(messageArea);
        TextButton howTo = new TextButton("Video: How to use Catchup Math");
        howTo.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new StudentHowToFlashWindow();
            }
        });
        fs.add(howTo);

        flc.add(fs);
        
        if (CmShared.getQueryParameter("debug") != null) {
            FieldSet fsDebug = new FieldSet();
            fsDebug.setHeadingText("Debug Info");
            fsDebug.add(new HTML(UserInfo.getInstance().getUserStatus()));
            flc.add(fsDebug);
        }

        bgCombo = createBackgroundCombo();

        fs = new FieldSet();
        fs.setHeadingText("Wallpaper");
        Label lab = new Label("Set which image to use for your Catchup Math wallpaper.");
        lab.addStyleName("bg-image-label");
        fs.add(lab);
        fs.add(bgCombo);

        flc.add(fs);



        if (UserInfo.getInstance().isSingleUser() || CmShared.getQueryParameter("debug") != null) {
            fs = new FieldSet();
            // fs.setLayout(new FlowLayout());
            ToolBar  hlc = new ToolBar();
            fs.setHeadingText("Configuration");
            fs.addStyleName("help-window-additional-options");
            TextButton btn = new MyOptionButton("Setup Catchup Math");
            btn.setToolTip("Modify your Catchup Math settings.");
            btn.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    showStudentConfiguration();
                }
            });
            btn.addStyleName("button");
            hlc.add(btn);

            btn = new MyOptionButton("Restart");
            btn.setToolTip("Restart the current program.");
            btn.addStyleName("button");
            btn.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    final ConfirmMessageBox mb = new ConfirmMessageBox("Restart Program",
                            "Are you sure you would like to restart your current program?");
                    mb.addHideHandler(new HideHandler() {
                        public void onHide(HideEvent event) {
                            if (mb.getHideButton() == mb.getButtonById(PredefinedButton.YES.name())) {
                                CmShared.resetProgram_Gwt(UserInfo.getInstance().getUid());
                            }
                        }
                    });
                }
            });
            hlc.add(btn);
            fs.add(hlc);

            flc.add(fs);
        }


        fs = new FieldSet();
        ToolBar tb = new ToolBar();

        fs.addStyleName("help-window-additional-options");
        fs.setHeadingText("Additional Options");

        
        TextButton calculator = new TextButton("Calculator", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CalculatorWindow.getInstance().setVisible(true);
            }
        });
        tb.add(calculator);
        

        TextButton btn = new MyOptionButton("Support");
        btn.addStyleName("button");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CatchupMathTools.showAlert("Please email support@hotmath.com for support.");
            }
        });
        tb.add(btn);

        btn = new MyOptionButton("Student History");
        btn.setToolTip("View your history of quizzes, reviews and show work efforts.");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showStudentHistory();
            }
        });

        TextButton btnFeedback = new MyOptionButton("Feedback");
        btnFeedback.addStyleName("button");
        btnFeedback.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showFeedbackPanel_Gwt();
            }
        });
        tb.add(btnFeedback);

        TextButton btnComputerCheck = new MyOptionButton("Computer Check");
        btnComputerCheck.addStyleName("button");
        btnComputerCheck.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new ComputerCheckWindow();
            }
        });
        tb.add(btnComputerCheck);

        /** Only the owner of the account has access to history */
        if (!UserInfo.getInstance().isActiveUser())
            btn.setEnabled(false);

        /** Do not allow Student History for demo user */
        if (UserInfo.getInstance().isDemoUser())
            btn.setEnabled(false);

        btn.addStyleName("button");
        tb.add(btn);
        fs.add(tb);

        flc.add(fs);
        
        if (CmShared.getQueryParameter("debug") != null) {
            tb.add(new TextButton("Connection Check", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    GWT.runAsync(new CmRunAsyncCallback() {
                        @Override
                        public void onSuccess() {
                            new NetTestWindow(TestApplication.CM_STUDENT, UserInfo.getInstance().getUid()).runTests();
                        }
                    });
                }
            }));
        }
        
        setWidget(flc);
        setVisible(true);
    }

    public interface BackgroundProperties extends PropertyAccess<String> {
        ModelKeyProvider<BackgroundModel> type();
        LabelProvider<BackgroundModel> style();
    }

    private ComboBox<BackgroundModel> createBackgroundCombo() {

        BackgroundProperties props = GWT.create(BackgroundProperties.class);
        ListStore<BackgroundModel> groupStore = new ListStore<BackgroundModel>(props.type());

        ComboBox<BackgroundModel> combo = new ComboBox<BackgroundModel>(groupStore, props.style());
        combo.setStore(getBackgroundListStores(groupStore));
        combo.setWidth(300);
        combo.addStyleName("help-window-bg-combo");
        combo.setEmptyText("-- Select Wallpaper --");
        combo.setTriggerAction(TriggerAction.ALL);
        combo.addSelectionHandler(new SelectionHandler<BackgroundModel>() {
            public void onSelection(final com.google.gwt.event.logical.shared.SelectionEvent<BackgroundModel> event) {

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
                        SetBackgroundStyleAction action = new SetBackgroundStyleAction(UserInfo.getInstance().getUid(),
                                event.getSelectedItem().getStyle());
                        setAction(action);
                        s.execute(action, this);
                    }

                    public void oncapture(RpcData result) {
                        CmBusyManager.setBusy(false);
                        try {
                            String newStyle = event.getSelectedItem().getStyle();
                            setBackgroundStyle(newStyle);
                        } finally {
                            CatchupMathTools.setBusy(false);
                        }
                    }
                }.register();
            };
        });
        

        return combo;
    }

    /**
     * Remove any previous wallpaper styles, and make sure this is the only one
     * active.
     * 
     * Must check state of systemt to change the appropriate panel.
     * 
     * @TODO: create EventType and listen for change to remove static calls.
     * 
     *        NOTE: all wallpaper styles start with 'resource-container-'. The
     *        welcome panel starts with 'cm-welcome-panel'
     * 
     */
    private void setBackgroundStyle(String newStyle) {

        Widget panelToChange = null;

        if (CmMainPanel.__lastInstance != null) {
            panelToChange = CmMainPanel.__lastInstance._mainContentWrapper.getResourceWrapper();
        } else {
            panelToChange = WelcomePanel.__instance;
        }

        if (panelToChange != null) {
            String styleName = panelToChange.getStyleName();
            if (styleName != null) {
                String names[] = styleName.split(" ");
                for (int i = 0; i < names.length; i++) {
                    if (names[i].startsWith("resource-container-") || names[i].startsWith("cm-welcome-panel")) {
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
        String url = "/cm_admin/launch.jsp?load=student_registration:" + UserInfo.getInstance().getUid();
        new IFramedPanelWindow(url).setVisible(true);
        hide();
//        GWT.runAsync(new CmRunAsyncCallback() {
//            @Override
//            public void onSuccess() {
//                new RetryAction<StudentModelI>() {
//
//                    @Override
//                    public void attempt() {
//                        CmBusyManager.setBusy(true);
//                        GetStudentModelAction action = new GetStudentModelAction(UserInfo.getInstance().getUid());
//                        setAction(action);
//                        CmShared.getCmService().execute(action, this);
//                    }
//
//                    public void oncapture(StudentModelI student) {
//                        try {
//                            CmAdminModel adminModel = new CmAdminModel();
//                            adminModel.setId(student.getAdminUid());
//                            new RegisterStudent(student, adminModel).showWindow();
//                        } finally {
//                            CmBusyManager.setBusy(false);
//                        }
//                    }
//                }.register();
//            }
//        });
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
        String url = "/cm_admin/launch.jsp?load=student_details:" + UserInfo.getInstance().getUid();
        new IFramedPanelWindow(url).setVisible(true);
        hide();
        
//        GWT.runAsync(new CmRunAsyncCallback() {
//
//            @Override
//            public void onSuccess() {
//                new RetryAction<StudentModelI>() {
//                    @Override
//                    public void attempt() {
//                        CmBusyManager.setBusy(true);
//                        GetStudentModelAction action = new GetStudentModelAction(UserInfo.getInstance().getUid());
//                        setAction(action);
//                        CmShared.getCmService().execute(action, this);
//                    }
//
//                    public void oncapture(StudentModelI student) {
//                        CmBusyManager.setBusy(false);
//                        new StudentDetailsWindow(new StudentModelExt(student));
//
//                        HelpWindow.this.hide(); // hide to deal with z-order
//                                                // issue
//                    }
//                }.register();
//            }
//        });
    }

    /**
     * Return two dim array showing registered and available background
     * wallpapers.
     * 
     * @return
     */
    public static String[][] getBackgrounds() {

        String bgs[][] = { { "Catchup Math", "resource-container" }, { "Clouds", "resource-container-clouds" },
                { "Forest", "resource-container-forest" }, { "Meadow", "resource-container-sunrise" },
                { "Mountain Bike", "resource-container-bike1" }, { "Snowman", "resource-container-snowman" },
                { "Sunfield", "resource-container-sunfield" }, { "Tulips", "resource-container-tulips" },
                { "Neutral", "resource-container-neutral" }, { "Redish", "resource-container-redish" },
                { "No background image", "resource-container-none" } };
        return bgs;
    }

    public ListStore<BackgroundModel> getBackgroundListStores(ListStore<BackgroundModel> store) {
        String bgMap[][] = getBackgrounds();
        for (String s[] : bgMap) {
            store.add(new BackgroundModel(s[0], s[1]));
        }
        return store;
    }

    static public void showFeedbackPanel_Gwt() {

        final PromptMessageBox mb = new PromptMessageBox("Feedback", "Enter Catchup Math feedback.");
        mb.addHideHandler(new HideHandler() {
            public void onHide(HideEvent event) {
                if (mb.getHideButton() == mb.getButtonById(PredefinedButton.OK.name())) {

                    final String value = mb.getValue();
                    if (value == null || value.length() == 0)
                        return;

                    new RetryAction<RpcData>() {
                        @Override
                        public void attempt() {
                            CmBusyManager.setBusy(true);
                            CmServiceAsync s = CmShared.getCmService();
                            SaveFeedbackAction action = new SaveFeedbackAction(value, "", getFeedbackStateInfo());
                            setAction(action);
                            s.execute(action, this);
                        }

                        public void oncapture(RpcData result) {
                            Info.display("INfo", "Feedback saved");
                            CmBusyManager.setBusy(false);
                        }
                    }.register();
                }
            }
        });
        mb.setWidth(300);
        mb.show();
    }

    /**
     * Return string that represents current state of CM
     * 
     */
    static private String getFeedbackStateInfo() {
        String msg = "user_agent=" + CmShared.getBrowserInfo() + ", " + ContextController.getInstance().toString();
        return msg;
    }
}

class BackgroundModel  {

    private String type;
    private String style;

    public BackgroundModel(String type, String style) {
        this.type = type;
        this.style = style;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }
    
}

class MyOptionButton extends TextButton {

    public MyOptionButton(String name) {
        super(name);
        setWidth(110);
    }
}

class ComputerCheckWindow extends GWindow {
    public ComputerCheckWindow() {
        super(true);
        setHeadingText("Computer Check");
        addCloseButton();
        setModal(true);
        setPixelSize(640, 650);

        setWidget(new ComputerCheckIFrame());
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