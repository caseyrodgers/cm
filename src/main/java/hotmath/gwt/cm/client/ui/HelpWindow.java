package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmMainPanel;
import hotmath.gwt.cm_tools.client.ui.ContextController;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.util.UserInfo;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
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
        messageArea.setHtml(ContextController.getInstance().getTheContext().getStatusMessage());
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

        fs = new FieldSet();
        fs.setStyleName("help-window-additional-options");
        fs.setHeading("Additional Options");
        
        
        SelectionListener<ButtonEvent> selList = new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CatchupMathTools.showAlert("Not available");
            }
        };
        
        
        Button btn = new Button("Technical support");
        btn.addStyleName("button");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CatchupMathTools.showAlert("Please email support@hotmath.com for support.");
            }
        });
        btn.setWidth(250);
        fs.add(btn);
        btn = new Button("Student History");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CatchupMathTools.showAlert("Coming soon!");     
            }
        });
        btn.addStyleName("button");
        fs.add(btn);
        vp.add(fs);
        
        add(vp);
    }

    public void onClick(ClickEvent event) {
    }

    private ListStore<BackgroundModel> getBackgrounds() {
        ListStore<BackgroundModel> backgrounds = new ListStore<BackgroundModel>();
        
        BackgroundModel m = new BackgroundModel();
        m.set("text","Snow Field");
        m.set("bg_style", "resource-container");
        backgrounds.add( m );
        
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
        m.set("text","White");
        m.set("bg_style", "resource-container-white");
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



