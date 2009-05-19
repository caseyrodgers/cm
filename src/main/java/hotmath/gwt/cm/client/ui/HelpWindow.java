package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm.client.util.UserInfo;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class HelpWindow extends Window {

    public  HelpWindow() {
        setHeight(300);
        setWidth(400);
        setModal(true);
        setStyleName("help-window");
        FormPanel fp = new FormPanel();
        setHeading("Catchup-Math Help Window");
        
        ComboBox<BackgroundModel> bgCombo = new ComboBox<BackgroundModel>();
        bgCombo.setStore(getBackgrounds());  
        bgCombo.setEditable(false);
        bgCombo.setWidth(150);
        bgCombo.setEmptyText("-- Select Your Background -- ");
        bgCombo.addSelectionChangedListener(new SelectionChangedListener<BackgroundModel>() {
            public void selectionChanged(final SelectionChangedEvent<BackgroundModel> se) {
                PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
                s.setUserBackground(UserInfo.getInstance().getUid(),se.getSelectedItem().getBackgroundStyle(),new AsyncCallback() {
                    public void onSuccess(Object result) {
                        try {
                            CmMainPanel.__lastInstance._mainContent.setStyleName(se.getSelectedItem().getBackgroundStyle());
                        }
                        finally {
                            CatchupMath.setBusy(false);
                        }
                    }
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }
                });
            }
        });
        
        FieldSet fs = new FieldSet();
        fs.setHeading("Options");
        
        //Html html = new Html(ContextController.getInstance().theContext.getContextHelp());
        //html.setStyleName("help-window-text");
        //add(html);
        
        fs.add(bgCombo);
        Button btn = new Button("Technical support");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
            }
        });
        fs.add(btn);
        btn = new Button("Message to Teacher");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
            }
        });
        fs.add(btn);
        btn = new Button("View Detail History");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
            }
        });
        fs.add(btn);
        btn = new Button("Send Feedback");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
            }
        });
        fs.add(btn);
        
        fp.add(fs);
        add(fp);
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
        m.set("text","Roses");
        m.set("bg_style", "resource-container-roses");
        backgrounds.add(m);
        
        m = new BackgroundModel();
        m.set("text","Sunset");
        m.set("bg_style", "resource-container-sunrise");
        backgrounds.add(m);

        
        return backgrounds;
        
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