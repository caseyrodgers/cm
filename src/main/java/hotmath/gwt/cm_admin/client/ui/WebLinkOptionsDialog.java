package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.PublicAvailable;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class WebLinkOptionsDialog extends GWindow {
    
    private CheckBox _shareLink;
    private ComboBox<AvailableDevice> _availableDevice;
    private WebLinkModel webLink;
    public WebLinkOptionsDialog(WebLinkModel webLink) {
        super(false);
        setPixelSize(302, 176);
        this.webLink = webLink;
        setHeadingText("Web Link Options");
        drawGui();
        
        addButton(new TextButton("Apply", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                saveOption();
            }
        }));
        
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        setVisible(true);
    }
    

    protected void saveOption() {
        // apply changes to passed in webLink
        
        if(_shareLink.isEnabled() && _shareLink.getValue() == false) {
            webLink.setPublicAvailability(PublicAvailable.PRIVATE);
        }
        else {
            PublicAvailable val = webLink.getPublicAvailability();
            if(webLink.getPublicAvailability() == PublicAvailable.PRIVATE) {
                webLink.setPublicAvailability(PublicAvailable.PUBLIC_SUGGESTED);
            }
            else {
                // no change
            }
        }
        
        webLink.setAvailableWhen(_availableDevice.getValue().getAvailWhen());
        hide();
    }


    interface ShareLinkProps extends PropertyAccess<String> {
        @Path("share")
        ModelKeyProvider<ShareLink> key();
        LabelProvider<ShareLink> share();
    }
    
    private ComboBox<ShareLink> createShareLinkCombo() {
        ShareLinkProps shareLinkProps = GWT.create(ShareLinkProps.class);
        ListStore<ShareLink> store = new ListStore<ShareLink>(shareLinkProps.key());
        store.add(new ShareLink("Our school only"));
        store.add(new ShareLink("All Catchup Math schools"));
        ComboBox<ShareLink> cb = new ComboBox<ShareLink>(store, shareLinkProps.share());
        cb.setAllowBlank(false);
        cb.setForceSelection(true);
        cb.setTriggerAction(TriggerAction.ALL);
        cb.setToolTip("Should this link be shared with other schools?");
        return cb;
    }

    private void drawGui() {
        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        
        FlowPanel flow = new FlowPanel();

        _shareLink = new CheckBox();
        _shareLink.setToolTip("Submit for sharing with other schools");
        if(webLink.getPublicAvailability() == PublicAvailable.PRIVATE) {
            _shareLink.setEnabled(true);
        }
        else {
            _shareLink.setEnabled(false);
            _shareLink.setValue(true);
            if(webLink.getPublicAvailability() == PublicAvailable.PUBLIC_SUGGESTED) {
                _shareLink.setToolTip("This web link has already been submitted for sharing");
            }
            else {
                _shareLink.setToolTip("This web link has been approved for sharing");
            }
        }
        
        flow.add(new MyFieldLabel(_shareLink, "Sharing",90,10));
        
        
        AvailableOn av = webLink.getAvailableWhen();
        int ordinal = av.ordinal();
        _availableDevice = createAvailableCombo();
        _availableDevice.setValue(_availableDevice.getStore().get(ordinal));
        flow.add(new MyFieldLabel(_availableDevice, "Platform(s)",90,160));
        
        frame.setWidget(flow);
        setWidget(frame);
    }

    class ShareLink {
        String share;
        public ShareLink(String share) {
            this.share = share;
        }
        
        public String getShare() {
            return this.share;
        }
    }
    
    private ComboBox<AvailableDevice> createAvailableCombo() {
        AvailDeviceProps props = GWT.create(AvailDeviceProps.class);
        ListStore<AvailableDevice> store = new ListStore<AvailableDevice>(props.key());
        ComboBox<AvailableDevice> combo = new ComboBox<AvailableDevice>(store, props.device());
        
        combo.getStore().add(new AvailableDevice("Desktop and Mobile",AvailableOn.DESKTOP_AND_MOBILE));
        combo.getStore().add(new AvailableDevice("Desktop Only", AvailableOn.DESKTOP_ONLY));
        combo.getStore().add(new AvailableDevice("Mobile Only", AvailableOn.MOBILE_ONLY));
        
        combo.setAllowBlank(false);
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.setToolTip("On what type of devices should this web link be shown?");
        return combo;
    }
    
    class AvailableDevice {
        String device;
        private int id;
        private AvailableOn availableWhen;
        
        public AvailableDevice(String label, AvailableOn available) {
            this.availableWhen = available;
            device = label;
            id = available.ordinal();
        }
        
        public AvailableOn getAvailWhen() {
            // TODO Auto-generated method stub
            return availableWhen;
        }

        public String getDevice() {
            return device;
        }
        
        public int getId() {
            return this.id;
        }
    }
    
    interface AvailDeviceProps extends PropertyAccess<String> {

        LabelProvider<AvailableDevice> device();
        @Path("id")
        ModelKeyProvider<AvailableDevice> key();
    }
    
    
    
}
