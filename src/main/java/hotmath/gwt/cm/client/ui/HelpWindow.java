package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.WelcomePanel;
import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.search.LessonSearchWindow;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyOptionButton;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.resource_viewer.CmResourcePanel;
import hotmath.gwt.cm_tools.client.ui.viewer.CalculatorWindow;
import hotmath.gwt.cm_tools.client.ui.viewer.ResourceViewerImplTutor2;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.StudentHowToFlashWindow;
import hotmath.gwt.shared.client.CatchupMathVersionInfo;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetStudentModelAction;
import hotmath.gwt.shared.client.rpc.action.RunNetTestAction.TestApplication;
import hotmath.gwt.shared.client.rpc.action.SetBackgroundStyleAction;
import hotmath.gwt.shared.client.util.CmLoggerWindow;
import hotmath.gwt.shared.client.util.CmRunAsyncCallback;
import hotmath.gwt.shared.client.util.FeedbackWindow;
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
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldSet;
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
        FlowLayoutContainer flowContainer = new FlowLayoutContainer();
        flowContainer.add(messageArea);
    
        ToolBar toolBar = new ToolBar();
        
        TextButton howTo = new MyOptionButton("Using Catchup Math", "Video: How to use Catchup Math", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                new StudentHowToFlashWindow();
            }
        });
        toolBar.add(howTo);

        TextButton supportBtn = new MyOptionButton("Support", "Get support help", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CmMessageBox.showAlert("Please email support@catchupmath.com for support.");
            }
        });
        toolBar.add(supportBtn);

        TextButton btnFeedback = new MyOptionButton("Feedback", "Provide feedback", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showFeedbackPanel_Gwt();
            }
        });
        toolBar.add(btnFeedback);

        
    

        /** Only the owner of the account has access to history */
        if (!UserInfo.getInstance().isActiveUser())
            supportBtn.setEnabled(false);


        supportBtn.addStyleName("button");
        toolBar.add(supportBtn);
	
        

        if(CmCore.isDebug() == true) {
            
            toolBar.add(new MyOptionButton("Reset Lesson", "Reset the problems for the current lesson.", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    new ResetLessonDialog(UserInfo.getInstance().getUid(), null);
                }
            }));
            toolBar.add(new MyOptionButton("Reset Problem", "Reset the current problem.", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    if(CmMainPanel.getActiveInstance() != null) {
                        CmResourcePanel resource = CmMainPanel.getActiveInstance().getLastResource();
                        if(resource instanceof ResourceViewerImplTutor2) {
                            String pid = ((ResourceViewerImplTutor2)resource).getResourceItem().getFile();
                            new ResetLessonDialog(UserInfo.getInstance().getUid(), pid);
                            return;
                        }
                    }
                    
                    CmMessageBox.showAlert("Load the problem you want to reset.");
                }
            }));
            
            TextButton btnComputerCheck = new MyOptionButton("Computer Check", "Verify your computer can run Catchup Math", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    new ComputerCheckWindow();
                }
            });
            toolBar.add(btnComputerCheck);

            TextButton btnSearch = new MyOptionButton("Lesson Search", "Search for a lesson", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    new LessonSearchWindow();
                }
            });
            toolBar.add(btnSearch);
            
        }
        
        
        flowContainer.add(toolBar);
        
        fs.setWidget(flowContainer);

        flc.add(fs);
        
        if (CmCore.isDebug() == true) {
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


        FieldSet fsAdditional = new FieldSet();
        FlowLayoutContainer additionalFlow = new FlowLayoutContainer();
        fsAdditional.setHeadingText("Additional Options and Configuration");
        ToolBar  addtionalTb = new ToolBar();

        additionalFlow.add(addtionalTb);
        fsAdditional.setWidget(additionalFlow);
        
        flc.add(fsAdditional);

        if (UserInfo.getInstance().isSingleUser() || CmCore.isDebug() == true) {

            TextButton setupButton = new MyOptionButton("Setup Catchup Math","Modify your Catchup Math settings.", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    showStudentConfiguration();
                }
            });
            addtionalTb.add(setupButton);

            TextButton restartButton = new MyOptionButton("Restart", "Restart the current program.",new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    final ConfirmMessageBox mb = new ConfirmMessageBox("Restart Program",
                            "Are you sure you would like to restart your current program?");
                    mb.addDialogHideHandler(new DialogHideHandler() {
                        @Override
                        public void onDialogHide(DialogHideEvent event) {
                            if (event.getHideButton() == PredefinedButton.YES) {
                                CmShared.resetProgram_Gwt(UserInfo.getInstance().getUid());
                            }
                        }
                    });
                    mb.setVisible(true);
                }
            });
            addtionalTb.add(restartButton);
        }
        
        TextButton studentDetailsBtn = new MyOptionButton("Student History", "View your history of quizzes, reviews and show work efforts.", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showStudentHistory();
            }
        });
        /** Do not allow Student History for demo user */
        if (UserInfo.getInstance().isDemoUser()) {
        	studentDetailsBtn.setEnabled(false);
        }
        
        /** There is a conflict between the Quiz panel and Quiz Results .. 
         * they both cannot be active at the same time. 
         */
        if(false && CmMainPanel.getActiveInstance() != null && CmMainPanel.getActiveInstance().isResourceQuiz()) {
            studentDetailsBtn.setEnabled(false);
        }
        
        
        addtionalTb.add(studentDetailsBtn);
        
        TextButton calculator = new TextButton("Calculator", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                CalculatorWindow.getInstance().setVisible(true);
            }
        });
        if(UserInfo.getInstance().isDisableCalcAlways()) {
        	calculator.setEnabled(false);
        }
        addtionalTb.add(calculator);
        

        
        if (CmCore.isDebug() == true) {
        	addtionalTb.add(new TextButton("Connection Check", new SelectHandler() {
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
        	
            addtionalTb.add(new TextButton("Debug Log", new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    GWT.runAsync(new CmRunAsyncCallback() {
                        @Override
                        public void onSuccess() {
                            CmLoggerWindow.getInstance().setVisible(true);
                        }
                    });
                }
            }));

        }

        setWidget(flc);
        setVisible(true);
    }

    public interface BackgroundProperties extends PropertyAccess<String> {
        ModelKeyProvider<BackgroundModel> style();
        LabelProvider<BackgroundModel> type();
    }

    private ComboBox<BackgroundModel> createBackgroundCombo() {

        BackgroundProperties props = GWT.create(BackgroundProperties.class);
        ListStore<BackgroundModel> groupStore = new ListStore<BackgroundModel>(props.style());

        ComboBox<BackgroundModel> combo = new ComboBox<BackgroundModel>(groupStore, props.type());
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
                    CmMessageBox
                            .showAlert("You will be able to change your wallpaper once you create your own account");
                    return;
                }

                new RetryAction<RpcData>() {
                    @Override
                    public void attempt() {
                        CmBusyManager.setBusy(true);
                        CmServiceAsync s = CmRpcCore.getCmService();
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

        if (CmMainPanel.getActiveInstance() != null) {
            panelToChange = CmMainPanel.getActiveInstance()._mainContentWrapper.getResourceWrapper();
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
        GWT.runAsync(new CmRunAsyncCallback() {
            @Override
            public void onSuccess() {
                new RetryAction<StudentModelI>() {

                    @Override
                    public void attempt() {
                        CmBusyManager.setBusy(true);
                        GetStudentModelAction action = new GetStudentModelAction(UserInfo.getInstance().getUid());
                        setAction(action);
                        CmRpcCore.getCmService().execute(action, this);
                    }

                    public void oncapture(StudentModelI student) {
                        try {
                            CmAdminModel adminModel = new CmAdminModel();
                            adminModel.setUid(student.getAdminUid());
                            new RegisterStudent(student, adminModel).showWindow();
                        } finally {
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
                        CmRpcCore.getCmService().execute(action, this);
                    }

                    public void oncapture(StudentModelI student) {
                        CmBusyManager.setBusy(false);
                        new StudentDetailsWindow(new StudentModelExt(student));

                        HelpWindow.this.hide(); // hide to deal with z-order
                                                // issue
                    }
                }.register();
            }
        });
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
        new FeedbackWindow(ContextController.getInstance().toString());
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


class ComputerCheckWindow extends GWindow {
    public ComputerCheckWindow() {
        super(true);
        setHeadingText("Computer Check");
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
