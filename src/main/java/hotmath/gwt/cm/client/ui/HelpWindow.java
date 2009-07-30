package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.ui.context.PrescriptionContext;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.FooterPanel;
import hotmath.gwt.cm_tools.client.ui.RegisterStudent;
import hotmath.gwt.cm_tools.client.ui.StudentDetailsWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.util.UserInfo;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;


public class HelpWindow extends CmWindow {

    public  HelpWindow() {
        setAutoHeight(true);
        setWidth(490);
        setModal(true);
        setResizable(false);
        setStyleName("help-window");
        setHeading("Catchup-Math Help Window");
        
        
        Button closeBtn = new Button("Close");
        closeBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                HelpWindow.this.close();
            }
        });
        addButton(closeBtn);
        
        
        Html messageArea = new Html();
        try {
            messageArea.setHtml(ContextController.getInstance().getTheContext().getStatusMessage());
        }
        catch(Exception e) {
            Log.info("Error getting context help", e);
            messageArea.setHtml("Catchup Math seems to be in an inconsistent state.  Please, try refreshing.");
        }
            messageArea.setStyleName("help-window-message-area");
        
        VerticalPanel vp = new VerticalPanel();
        
        FieldSet fs = new FieldSet();
        fs.setHeading("Using Catchup Math");
        
        fs.add(messageArea);
        vp.add(fs);
        
        ComboBox<BackgroundModel> bgCombo = new ComboBox<BackgroundModel>();
        bgCombo.setStore(getBackgrounds());  
        bgCombo.setEditable(false);
        bgCombo.setStyleName("help-window-bg-combo");
        bgCombo.setEmptyText("-- Select Wallpaper --");
        bgCombo.addSelectionChangedListener(new SelectionChangedListener<BackgroundModel>() {
            public void selectionChanged(final SelectionChangedEvent<BackgroundModel> se) {
                PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
                s.setUserBackground(UserInfo.getInstance().getUid(),se.getSelectedItem().getBackgroundStyle(),new AsyncCallback() {
                    public void onSuccess(Object result) {
                        try {
                            CmMainPanel.__lastInstance._mainContent.setStyleName(se.getSelectedItem().getBackgroundStyle());
                        }
                        finally {
                            CatchupMathTools.setBusy(false);
                        }
                    }
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }
                });
            }
        });
        
        fs = new FieldSet();
        fs.setHeading("Wallpaper");
        Label lab = new Label("Set which image to use for your Catchup Math wallpaper.");
        lab.addStyleName("bg-image-label");
        fs.add(lab);
        fs.add(bgCombo);
        
        vp.add(fs);
        

        if(UserInfo.getInstance().isSingleUser()) {
            fs = new FieldSet();
            fs.setLayout(new FlowLayout());
            fs.setHeading("Configuration");
            fs.setStyleName("help-window-additional-options");
            Button btn = new Button("Setup Catchup Math");
            btn.setWidth(120);
            btn.setToolTip("Modify your Catchup Math settings.");
            btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    showStudentConfiguration();
                }
            });
            btn.addStyleName("button");
            fs.add(btn);
            
            btn = new Button("Restart Program");
            btn.setToolTip("Restart the current program.");
            btn.addStyleName("button");       
            btn.setWidth(120);
            btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
                public void componentSelected(ButtonEvent ce) {
                    MessageBox.confirm("Restart Program", "Are you sure you would like to restart your current program?", new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getText().equals("Yes")) {
                                FooterPanel.resetProgram_Gwt();                                
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
        fs.setStyleName("help-window-additional-options");
        fs.setHeading("Additional Options");
        
        SelectionListener<ButtonEvent> selList = new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CatchupMathTools.showAlert("Not available");
            }
        };
        
        
        Button btn = new Button("Technical Support");
        btn.setWidth(120);
        btn.addStyleName("button");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CatchupMathTools.showAlert("Please email support@hotmath.com for support.");
            }
        });
        fs.add(btn);
        btn = new Button("Student History");
        btn.setToolTip("View your history of quizzes, reviews and show work efforts.");
        btn.setWidth(120);
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                showStudentHistory();
            }
        });
        btn.addStyleName("button");
        fs.add(btn);
        
        vp.add(fs);

        add(vp);
    }
    
    
    /** Provide method for single user to configure programs and settings
     *  
     */
    private void showStudentConfiguration() {
        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getStudentModel(UserInfo.getInstance().getUid(), new AsyncCallback <StudentModel>() {

        public void onSuccess(StudentModel student) {
            
            CmAdminModel adminModel = new CmAdminModel();
            adminModel.setId(student.getAdminUid());
            new RegisterStudent(student, adminModel);
        }

        public void onFailure(Throwable caught) {
            String msg = caught.getMessage();
            CatchupMathTools.showAlert(msg);
        }
        });
    }
    
    /** Show this student's history.  First we must
     *  get the student's StudentModel to make sure 
     *  we have the current information, then call 
     *  the StudentDetailWindow.
     *  
     *  The StudentModel is read before showing details
     *  to make sure all summary information is current
     *  and not what it was on login.
     */
    private void showStudentHistory() {

        PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
        s.getStudentModel(UserInfo.getInstance().getUid(), new AsyncCallback <StudentModel>() {

        public void onSuccess(StudentModel student) {
            new StudentDetailsWindow(student);
        }

        public void onFailure(Throwable caught) {
            String msg = caught.getMessage();
            CatchupMathTools.showAlert(msg);
        }
        });
    }

    public void onClick(ClickEvent event) {
    }

    private ListStore<BackgroundModel> getBackgrounds() {
        ListStore<BackgroundModel> backgrounds = new ListStore<BackgroundModel>();
        
        BackgroundModel m = new BackgroundModel();
        m.set("text","Snow Man");
        m.set("bg_style", "resource-container-snowman");
        backgrounds.add( m );
        
        
        m = new BackgroundModel();
        m.set("text","Catchup Math");
        m.set("bg_style", "resource-container");
        backgrounds.add(m);
        
        
        m = new BackgroundModel();
        m.set("text","Mountain Bike");
        m.set("bg_style", "resource-container-bike1");
        backgrounds.add(m);

        m = new BackgroundModel();
        m.set("text","Tulips");
        m.set("bg_style", "resource-container-tulips");
        backgrounds.add(m);
        
        m = new BackgroundModel();
        m.set("text","Meadow");
        m.set("bg_style", "resource-container-sunrise");
        backgrounds.add(m);
        
        m = new BackgroundModel();
        m.set("text","Forest");
        m.set("bg_style", "resource-container-forest");
        backgrounds.add(m);

        m = new BackgroundModel();
        m.set("text","Clouds");
        m.set("bg_style", "resource-container-clouds");
        backgrounds.add(m);

        m = new BackgroundModel();
        m.set("text","Sunfield");
        m.set("bg_style", "resource-container-sunfield");
        backgrounds.add(m);
        

        m = new BackgroundModel();
        m.set("text","Neutral");
        m.set("bg_style", "resource-container-neutral");
        backgrounds.add(m);
        
        m = new BackgroundModel();
        m.set("text","Redish");
        m.set("bg_style", "resource-container-redish");
        backgrounds.add(m);
        
        m = new BackgroundModel();
        m.set("text","No background image   ");
        m.set("bg_style", "resource-container-none");
        backgrounds.add(m);



        
        return backgrounds;
        
    }
    
    /** Return the current version number
     * 
     * @todo: externalize this parameter 
     * 
     * 
     * @return
     */
    private String getVersion() {
        return "1.2b";
    }
}


class BackgroundModel extends BaseModelData  {
    
    public String getBackgroundName() {
        return get("text");
    }
    
    public String getBackgroundStyle() {
        return get("bg_style");
    }
}



