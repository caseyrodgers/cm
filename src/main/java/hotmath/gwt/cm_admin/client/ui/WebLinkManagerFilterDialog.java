package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.WebLinkOptionsDialog.WebLinkTypeLocal;
import hotmath.gwt.cm_rpc.client.model.AvailableDevice;
import hotmath.gwt.cm_rpc.client.model.WebLinkType;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

public class WebLinkManagerFilterDialog extends GWindow {
    
    static private WebLinkManagerFilterDialog __instance;
    public WebLinkManagerFilterDialog() {
        super(false);
        buildUi();
    }

    private ComboBox<AvailableDevice> _availableDevice;
    private ComboBox<WebLinkTypeLocal> _linkType;
    private TextField _textSearch;
    private ToggleButton _applySearch;
    private Callback callback;
    private void buildUi() {
        setHeadingText("Web Link Filter");
        setPixelSize(300,200);
        _availableDevice = WebLinkOptionsDialog.createAvailableCombo();
        _availableDevice.setEmptyText("-- All Platforms --");
        _availableDevice.getStore().add(new AvailableDevice("-- All Platforms --", null));
        _availableDevice.addSelectionHandler(new SelectionHandler<AvailableDevice>() {
            @Override
            public void onSelection(SelectionEvent<AvailableDevice> event) {
                applyFilter();
            }
        });
        _availableDevice.setValue(_availableDevice.getStore().get(_availableDevice.getStore().size()-1));
        
        
        
        _linkType = WebLinkOptionsDialog.createTypeCombo();
        _linkType.setAllowBlank(false);
        _linkType.getStore().add(new WebLinkTypeLocal(null, "-- All Types --"));
        _linkType.setValue(_linkType.getStore().get(_linkType.getStore().size()-1));
        _linkType.addSelectionHandler(new SelectionHandler<WebLinkTypeLocal>() {
            @Override
            public void onSelection(SelectionEvent<WebLinkTypeLocal> event) {
                applyFilter();
            }
        });
        
        _textSearch = new TextField();
        _textSearch.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                applyFilter();
            }
        });

        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        FlowPanel flow = new FlowPanel();
        flow.add(new MyFieldLabel(_availableDevice, "Platform(s)",90,160));
        flow.add(new MyFieldLabel(_linkType, "Type",90,160));
        flow.add(new MyFieldLabel(_textSearch, "Text Search",90,160));
        
        frame.setWidget(flow);
        
        setWidget(frame);
        
        
        _applySearch = new ToggleButton("Apply Filter");
        _applySearch.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                applyFilter();
            }
        });
        addButton(_applySearch);
        addCloseButton();
    }
    
    private void applyFilter() {
        boolean isPressed =_applySearch.getValue();
        if(isPressed) {
            callback.doFilter(_availableDevice.getCurrentValue(), _linkType.getCurrentValue().getType(), _textSearch.getText());
        }
        else {
            callback.doFilter(null,null,null);
        }
    }
    
    public void stopApplyFilter() {
        _applySearch.setValue(false);
    }
    
    public interface Callback {
        void doFilter(AvailableDevice device, WebLinkType webLinkType, String string);
    }
    
    public static void showSharedInstance(Callback callback) {
        WebLinkManagerFilterDialog instance = getSharedInstance();
        instance.setCallback(callback);
        instance.setVisible(true);
    }

    private void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static WebLinkManagerFilterDialog getSharedInstance() {
        if(__instance == null) {
            __instance = new WebLinkManagerFilterDialog();
        }
        return __instance;
    }
}
