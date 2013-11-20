package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.WebLinkOptionsDialog.WebLinkTypeLocal;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.AvailableOn;
import hotmath.gwt.cm_rpc.client.model.WebLinkType;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;

import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;

public class WebLinkManagerFilterPanel extends Composite {

    static private WebLinkManagerFilterPanel __instance;

    private ComboBox<PlatformSearch> _platformDevice;
    private ComboBox<WebLinkTypeLocal> _linkType;
    private TextField _textSearch;
    private Callback callback;
    private GroupCombo _groupCombo;
    private int adminId;
    private MyFieldLabel _groupComboLabel;
    private boolean _showGroupCombo;

    public WebLinkManagerFilterPanel(Callback callback, int adminId) {
        this.callback = callback;
        this.adminId = adminId;
        buildUi();
    }

    private void buildUi() {
        _platformDevice = createPlatformCombo();
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
                applyFilter();
            }

            @Override
            public List<WebLinkModel> getWebLinks() {
                return callback.getAllPrivateLinks();
            }
        });

        _linkType = WebLinkOptionsDialog.createTypeCombo();
        _linkType.setAllowBlank(false);
        _linkType.getStore().add(new WebLinkTypeLocal(null, "-- Any Type --"));
        _linkType.setValue(_linkType.getStore().get(_linkType.getStore().size() - 1));
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
        frame.setHeadingHtml("Filter Web Links");
        // frame.setHeaderVisible(false);
        frame.setBodyBorder(false);
        _groupComboLabel = new MyFieldLabel(_groupCombo.asWidget(), "Group", 80, 160);

        FlowPanel flow1 = new FlowPanel();
        flow1.add(new MyFieldLabel(_platformDevice, "Platform(s)", 80, 160));
        flow1.add(new MyFieldLabel(_linkType, "Type", 80, 160));

        FlowPanel flow2 = new FlowPanel();
        flow2.add(new MyFieldLabel(_textSearch, "Text Search", 80, 160));
        flow2.add(_groupComboLabel);

        HorizontalPanel hPan = new HorizontalPanel();
        hPan.add(flow1);
        hPan.add(flow2);

        FlowPanel flow = new FlowPanel();
        flow.add(new HTML("<div style='color: #666;margin-top: 5px;margin-bottom: 5px;font-weight: bold'>Filter</div>"));
        flow.add(hPan);
        frame.setWidget(hPan);

        initWidget(frame);
    }

    private ComboBox<PlatformSearch> createPlatformCombo() {
        PlatformSearchProps props = GWT.create(PlatformSearchProps.class);
        ListStore<PlatformSearch> store = new ListStore<PlatformSearch>(props.key());
        ComboBox<PlatformSearch> combo = new ComboBox<PlatformSearch>(store, props.label());

        combo.getStore().add(
                new PlatformSearch("-- Any Platform --",
                        new AvailableOn[] { AvailableOn.DESKTOP_AND_MOBILE, AvailableOn.DESKTOP_ONLY, AvailableOn.MOBILE_ONLY }));
        combo.getStore().add(new PlatformSearch("Desktop", new AvailableOn[] { AvailableOn.DESKTOP_AND_MOBILE, AvailableOn.DESKTOP_ONLY }));
        combo.getStore().add(new PlatformSearch("Desktop and iPad", new AvailableOn[] { AvailableOn.DESKTOP_AND_MOBILE }));
        combo.getStore().add(new PlatformSearch("iPad", new AvailableOn[] { AvailableOn.DESKTOP_AND_MOBILE, AvailableOn.MOBILE_ONLY }));

        combo.setAllowBlank(false);
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);

        combo.setToolTip("On what type of devices should this web link be shown?");
        return combo;
    }

    public void applyFilter() {
        GroupInfoModel group = null;
        if (_groupComboLabel.isEnabled()) {
            group = _groupCombo.getSelectedGroup();
        }
        callback.doFilter(group, _platformDevice.getCurrentValue().getAvailable(), _linkType.getCurrentValue().getType(), _textSearch.getText());
    }


    public interface Callback {
        void doFilter(GroupInfoModel groupInfoModel, AvailableOn[] availableOns, WebLinkType webLinkType, String string);

        void filterByGroup(GroupInfoModel group);

        List<WebLinkModel> getAllPrivateLinks();
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

    public void enableGroupCombo(boolean b) {
        _groupComboLabel.setEnabled(b);
    }
}
