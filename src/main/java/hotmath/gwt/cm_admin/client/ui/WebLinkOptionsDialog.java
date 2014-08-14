package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.AvailableDevice;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.LinkViewer;
import hotmath.gwt.cm_rpc.client.model.WebLinkType;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

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

    private ComboBox<AvailableDevice> _availableDevice;
    private ComboBox<WebLinkTypeLocal> _linkType;
    private CheckBox _openInExternalWindow;
    private CheckBox _offline;
    private WebLinkModel webLink;
    public WebLinkOptionsDialog(WebLinkModel webLink) {
        super(false);
        setPixelSize(302, 220);
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
        webLink.setAvailableWhen(_availableDevice.getValue().getAvailWhen());
        webLink.setLinkType(_linkType.getCurrentValue().getType());
        webLink.setLinkViewer(_openInExternalWindow.getValue()?LinkViewer.EXTERNAL_WINDOW:LinkViewer.INTERNAL);
        webLink.setOffline(_offline.getValue());
        hide();
    }


    interface ShareLinkProps extends PropertyAccess<String> {
        @Path("share")
        ModelKeyProvider<ShareLink> key();
        LabelProvider<ShareLink> share();
    }


    private void drawGui() {
        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        
        FlowPanel flow = new FlowPanel();
        
        AvailableOn av = webLink.getAvailableWhen();
        int ordinal = av.ordinal();
        _availableDevice = createAvailableCombo();
        _availableDevice.setValue(_availableDevice.getStore().get(ordinal));
        flow.add(new MyFieldLabel(_availableDevice, "Platform(s)",90,160));
        
        _linkType = createTypeCombo();
        int which=webLink.getLinkType() != null?webLink.getLinkType().ordinal():0;
        _linkType.setValue(_linkType.getStore().get(which));
        flow.add(new MyFieldLabel(_linkType, "Type",90,160));
        
        _openInExternalWindow = new CheckBox();
        _openInExternalWindow.setToolTip("Should this link be opened in an external browser window");
        if(webLink.getLinkViewer() == LinkViewer.EXTERNAL_WINDOW) {
            _openInExternalWindow.setValue(true);
        }
        flow.add(new MyFieldLabel(_openInExternalWindow, "New Window", 90, 10));
        
        _offline = new CheckBox();
        _offline.setValue(webLink.isOffline());
        _offline.setToolTip("If checked this web link will not be shown to students");
        flow.add(new MyFieldLabel(_offline, "Offline", 90, 10));
        
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
    
    static public ComboBox<WebLinkTypeLocal> createTypeCombo() {
        WebLinkTypeProps props = GWT.create(WebLinkTypeProps.class);
        ListStore<WebLinkTypeLocal> store = new ListStore<WebLinkTypeLocal>(props.key());
        ComboBox<WebLinkTypeLocal> combo = new ComboBox<WebLinkTypeLocal>(store, props.label());

        for(WebLinkType type: WebLinkType.values()) {
            combo.getStore().add(new WebLinkTypeLocal(type, type.getLabel()));    
        }
       
        combo.setAllowBlank(false);
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.setToolTip("Type of resource");
        return combo;
    }
    
   private ComboBox<AvailableDevice> createAvailableCombo() {
        AvailDeviceProps props = GWT.create(AvailDeviceProps.class);
        ListStore<AvailableDevice> store = new ListStore<AvailableDevice>(props.key());
        ComboBox<AvailableDevice> combo = new ComboBox<AvailableDevice>(store, props.device());
        
        combo.getStore().add(new AvailableDevice("Desktop and iPad",AvailableOn.DESKTOP_AND_MOBILE));
        combo.getStore().add(new AvailableDevice("Desktop Only", AvailableOn.DESKTOP_ONLY));
        combo.getStore().add(new AvailableDevice("iPad Only", AvailableOn.MOBILE_ONLY));
        
        combo.setAllowBlank(false);
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.setToolTip("On what type of devices should this web link be shown?");
        return combo;
    }

    
    interface AvailDeviceProps extends PropertyAccess<String> {

        LabelProvider<AvailableDevice> device();
        @Path("id")
        ModelKeyProvider<AvailableDevice> key();
    }
    
    interface WebLinkTypeProps extends PropertyAccess<String> {
        @Path("value")
        ModelKeyProvider<WebLinkTypeLocal> key();
        LabelProvider<WebLinkTypeLocal> label();
    }
    
    
    static public class WebLinkTypeLocal {
        WebLinkType type;
        String label;
        
        public WebLinkTypeLocal(WebLinkType type, String label) {
            this.type = type;
            this.label = label;
        }

        public WebLinkType getType() {
            return type;
        }

        public void setType(WebLinkType type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
        
        public String getValue() {
        	return getLabel();
        }
    }
    
}
