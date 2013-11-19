package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.WebLinkOptionsDialog.WebLinkTypeLocal;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.model.WebLinkType;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
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

    private ComboBox<PlatformSearch> _platformDevice;
    private ComboBox<WebLinkTypeLocal> _linkType;
    private TextField _textSearch;
    private ToggleButton _applySearch;
    private Callback callback;
    private GroupCombo _groupCombo;
    private int adminId;
    private MyFieldLabel _groupComboLabel;
    private boolean _showGroupCombo;
    
    private void buildUi() {
        setHeadingText("Web Link Filter");
        setPixelSize(300,230);
        _platformDevice  = createPlatformCombo();
        _platformDevice.addSelectionHandler(new SelectionHandler<PlatformSearch>() {
            @Override
            public void onSelection(SelectionEvent<PlatformSearch> event) {
                applyFilter();
            }
        });
        _platformDevice.setValue(_platformDevice.getStore().get(0));
        
        
        _groupCombo = new GroupCombo(adminId, new GroupCombo.Callback() {
            @Override
            public void groupSelected(GroupInfoModel group) {
                if(_applySearch.getValue()) {
                    applyFilter();
                }
            }

            @Override
            public List<WebLinkModel> getWebLinks() {
                return callback.getAllPrivateLinks();
            }
        });

        
        _linkType = WebLinkOptionsDialog.createTypeCombo();
        _linkType.setAllowBlank(false);
        _linkType.getStore().add(new WebLinkTypeLocal(null, "-- Any Type --"));
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
        flow.add(new MyFieldLabel(_platformDevice, "Platform(s)",90,160));
        flow.add(new MyFieldLabel(_linkType, "Type",90,160));
        flow.add(new MyFieldLabel(_textSearch, "Text Search",90,160));
        
        _groupComboLabel = new MyFieldLabel(_groupCombo.asWidget(), "Group",90,160);
        flow.add(_groupComboLabel);
        
        frame.setWidget(flow);
        
        setWidget(frame);
        
        
        _applySearch = new ToggleButton("Apply Filter");
        _applySearch.setToolTip("Toggle the filter on and off");
        _applySearch.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                applyFilter();
            }
        });
        addButton(_applySearch);
        addCloseButton();
    }
    
    private ComboBox<PlatformSearch> createPlatformCombo() {
        PlatformSearchProps props = GWT.create(PlatformSearchProps.class);
        ListStore<PlatformSearch> store = new ListStore<PlatformSearch>(props.key());
        ComboBox<PlatformSearch> combo = new ComboBox<PlatformSearch>(store, props.label());
       
        combo.getStore().add(new PlatformSearch("-- Any Platform --", new AvailableOn[]{AvailableOn.DESKTOP_AND_MOBILE,AvailableOn.DESKTOP_ONLY,AvailableOn.MOBILE_ONLY}));
        combo.getStore().add(new PlatformSearch("Desktop", new AvailableOn[]{AvailableOn.DESKTOP_AND_MOBILE,AvailableOn.DESKTOP_ONLY}));
        combo.getStore().add(new PlatformSearch("Desktop and iPad", new AvailableOn[]{AvailableOn.DESKTOP_AND_MOBILE}));
        combo.getStore().add(new PlatformSearch("iPad", new AvailableOn[]{AvailableOn.DESKTOP_AND_MOBILE, AvailableOn.MOBILE_ONLY}));
        
        combo.setAllowBlank(false);
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.setToolTip("On what type of devices should this web link be shown?");
        return combo;
    }
    
    private void applyFilter() {
        boolean isPressed =_applySearch.getValue();
        if(isPressed) {
            GroupInfoModel group = null;
            if(_showGroupCombo) {
                group = _groupCombo.getSelectedGroup();
            }
            callback.doFilter(group, _platformDevice.getCurrentValue().getAvailable(), _linkType.getCurrentValue().getType(), _textSearch.getText());
        }
        else {
            callback.doFilter(null, null,null,null);
        }
    }
    
    public void stopApplyFilter() {
        _applySearch.setValue(false);
    }
    
    public interface Callback {
        void doFilter(GroupInfoModel groupInfoModel, AvailableOn[] availableOns, WebLinkType webLinkType, String string);

        void filterByGroup(GroupInfoModel group);

        List<WebLinkModel> getAllPrivateLinks();
    }
    
    public static void showSharedInstance(Callback callback, int adminId, boolean showGroupsFilter) {
        WebLinkManagerFilterDialog instance = getSharedInstance();
        instance.setCallback(callback,adminId, showGroupsFilter);
        instance.setVisible(true);
    }

    
    private void setCallback(Callback callback,int adminId, boolean showGroupCombo) {
        this.callback = callback;
        this.adminId = adminId;
        
        _showGroupCombo = showGroupCombo;
        _groupComboLabel.setVisible(showGroupCombo);
    }

    public static WebLinkManagerFilterDialog getSharedInstance() {
        if(__instance == null) {
            __instance = new WebLinkManagerFilterDialog();
        }
        return __instance;
    }
    
    class PlatformSearch {
        String label;
        AvailableOn available[];
        public PlatformSearch(String label, AvailableOn available[]) {
            this.label = label;
            this.available = available;
        }
        public String getLabel() {
            return label;
        }
        public void setLabel(String label) {
            this.label = label;
        }
        public AvailableOn[] getAvailable() {
            return available;
        }
        public void setAvailable(AvailableOn[] available) {
            this.available = available;
        }
       
    }
    
    interface PlatformSearchProps extends PropertyAccess<String> {
        @Path("label")
        ModelKeyProvider<PlatformSearch> key();
        LabelProvider<PlatformSearch> label();
    }
}
