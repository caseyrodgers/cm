package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class WebLinkEditorGroupSelectDialog extends GWindow {
    
    CheckBox allGroups;
    private WebLinkModel webLink;
    public WebLinkEditorGroupSelectDialog(WebLinkModel link) {
        super(false);    
        this.webLink = link;
        setPixelSize(250,  400);
        setHeadingText("Web Link Group Availability");
        
        BorderLayoutContainer main = new BorderLayoutContainer();
       
        allGroups = new CheckBox();
        allGroups.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> event) {
                setupEnable();
            }
        });
        FramedPanel frame = new FramedPanel();
        frame.setHeaderVisible(false);
        
        FlowLayoutContainer flow = new FlowLayoutContainer();
        flow.add(new MyFieldLabel(allGroups, "All Groups", 75, 10));
        allGroups.setToolTip("If checked then all groups will utilize web link");
        
        allGroups.setValue(webLink.isAllGroups());
        
        frame.setWidget(flow);
        main.setNorthWidget(frame, new BorderLayoutData(50));
        main.setCenterWidget(createGroupsPanel());
        setWidget(main);
        
        
        addButton(new TextButton("Apply", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                saveSelectedGroups();
            }
        }));
        
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        readGroupsFromServer();
        setupEnable();
        setVisible(true);
    }
    

    protected void saveSelectedGroups() {
        webLink.getLinkGroups().clear();
        webLink.setAllGroups(allGroups.getValue());
        List<GroupInfoModel> sel = _grid4Groups.getSelectionModel().getSelectedItems();
        if(!webLink.isAllGroups() && sel.size() == 0) {
            CmMessageBox.showAlert("No groups selected.  Please either select All Groups or individual groups.");
            return;
        }
        webLink.getLinkGroups().addAll(sel);
        hide();
    }


    private void readGroupsFromServer() {
        new RetryAction<CmList<GroupInfoModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetActiveGroupsAction action = new GetActiveGroupsAction(webLink.getAdminId());
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            
            @Override
            public void oncapture(CmList<GroupInfoModel> groups) {
                _grid4Groups.getStore().addAll(groups);
                _grid4Groups.getSelectionModel().setSelection(webLink.getLinkGroups());
                CmBusyManager.setBusy(false);
            }
        }.register();
    }


    Grid4GroupProperties groupProps = GWT.create(Grid4GroupProperties.class);
    Grid<GroupInfoModel> _grid4Groups;
    private IsWidget createGroupsPanel() {
        List<ColumnConfig<GroupInfoModel, ?>> cols = new ArrayList<ColumnConfig<GroupInfoModel, ?>>();
        cols.add(new ColumnConfig<GroupInfoModel, String>(groupProps.groupName(), 200, "Group Name"));
        ColumnModel<GroupInfoModel> cm = new ColumnModel<GroupInfoModel>(cols);
        ListStore<GroupInfoModel> store = new ListStore<GroupInfoModel> (groupProps.key());
        _grid4Groups = new Grid<GroupInfoModel>(store, cm);
        _grid4Groups.setEnabled(false);
        _grid4Groups.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        return _grid4Groups;
    }

    private void setupEnable() {
        if(allGroups.getValue()) {
            _grid4Groups.setEnabled(false);
        }
        else {
            _grid4Groups.setEnabled(true);
        }
    }
    
    interface Grid4GroupProperties extends PropertyAccess<String> {
        @Path("id")
        ModelKeyProvider<GroupInfoModel> key();
        ValueProvider<GroupInfoModel, String> groupName();
    }

}
