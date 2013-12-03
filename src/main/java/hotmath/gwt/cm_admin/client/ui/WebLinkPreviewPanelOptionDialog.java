package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc.client.model.WebLinkModel.LinkViewer;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction;
import hotmath.gwt.cm_rpc.client.rpc.DoWebLinksCrudOperationAction.CrudOperation;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;



public class WebLinkPreviewPanelOptionDialog extends GWindow {
    
    private WebLinkModel webLink;
    
    ComboBox<OpenType> _combo;

    public WebLinkPreviewPanelOptionDialog(WebLinkModel webLink) {
        super(false);
        setPixelSize(400,  200);
        this.webLink = webLink;
        setModal(true);
        setResizable(false);
        setMaximizable(false);
        setClosable(true);
        buildUi();
        setVisible(true);
    }

    OpenTypeProps props = GWT.create(OpenTypeProps.class);
    private void buildUi() {
        ListStore<OpenType> store = new ListStore<OpenType>(props.key());
        store.add(new OpenType("Yes, Use this method", LinkViewer.EXTERNAL_WINDOW));
        store.add(new OpenType("No, I will edit the link",LinkViewer.INTERNAL));
        _combo = new ComboBox<OpenType>(store,props.type());
        _combo.setValue(store.get(0));
        _combo.setWidth(300);
        _combo.setAllowBlank(false);
        _combo.setTriggerAction(TriggerAction.ALL);
        FramedPanel frame = new FramedPanel();
        
        FlowPanel flow = new FlowPanel();
        flow.add(new HTML("<div style='margin-bottom: 15px;font-size: 1.2em;'>Did the web link look good in a new browser window?</a>"));
        flow.add(_combo);
        frame.setWidget(flow);
        
        frame.setHeaderVisible(false);
        setWidget(frame);
        
        _combo.setExpanded(true);
        
        
        
        addButton(new TextButton("OK", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                doSave();
            }
        }));
    }
    
    private void doSave() {
        OpenType openType = _combo.getCurrentValue();
        
        LinkViewer linkViewer = openType.getLinkViewer();
        webLink.setLinkViewer(linkViewer);
        
        if(webLink.getLinkId() > 0) {
            new RetryAction<RpcData>() {
                @Override
                public void attempt() {
                    CmBusyManager.setBusy(true);
                    CrudOperation actionToDo = CrudOperation.ADD;
                    DoWebLinksCrudOperationAction action = new DoWebLinksCrudOperationAction(webLink.getAdminId(), actionToDo, webLink);
                    setAction(action);
                    CmShared.getCmService().execute(action, this);
                }
    
                @Override
                public void oncapture(RpcData data) {
                    CmBusyManager.setBusy(false);
                }
                
            }.attempt();
        }
        hide();
    }

    class OpenType {
        String type;
        LinkViewer linkViewer;
        public OpenType(String type, LinkViewer linkViewer) {
            this.type = type;
            this.linkViewer = linkViewer;
        }
        public LinkViewer getLinkViewer() {
            return linkViewer;
        }
        public void setLinkViewer(LinkViewer linkViewer) {
            this.linkViewer = linkViewer;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return type;
        }
    }
    
    interface OpenTypeProps extends PropertyAccess<String> {
        public LabelProvider<OpenType> type();

        @Path("type")
        public ModelKeyProvider<OpenType> key();
    }
}
