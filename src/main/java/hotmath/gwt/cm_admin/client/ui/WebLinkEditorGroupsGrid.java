package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.model.WebLinkModel;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.IsWidget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class WebLinkEditorGroupsGrid extends ContentPanel {

    private WebLinkModel webLinkModel;

    public WebLinkEditorGroupsGrid(WebLinkModel webLinkModel) {
        this.webLinkModel = webLinkModel;
        setHeaderVisible(false);
        setWidget(createGroupsPanel());
    }

    
    Grid4GroupProperties groupProps = GWT.create(Grid4GroupProperties.class);
    Grid<GroupInfoModel> _grid4Groups;
    private IsWidget createGroupsPanel() {
        List<ColumnConfig<GroupInfoModel, ?>> cols = new ArrayList<ColumnConfig<GroupInfoModel, ?>>();
        cols.add(new ColumnConfig<GroupInfoModel, String>(groupProps.groupName(), 200, "Group Name"));
        ColumnModel<GroupInfoModel> cm = new ColumnModel<GroupInfoModel>(cols);
        ListStore<GroupInfoModel> store = new ListStore<GroupInfoModel> (groupProps.key());

        store.addAll(webLinkModel.getLinkGroups());
        _grid4Groups = new Grid<GroupInfoModel>(store, cm);
        _grid4Groups.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        checkForEmptyStore();

        _grid4Groups.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
        
        
        return _grid4Groups;
    }
    
    private void checkForEmptyStore() {
        if(_grid4Groups.getStore().size() == 0) {
            _grid4Groups.getStore().add(new GroupInfoModel(0,0,"All Groups",0,false,false));   
        }
    }

    private void setStore(ListStore<GroupInfoModel> store, List<GroupInfoModel> linkGroups) {
        if(linkGroups.size() == 0) {
            store.add(new GroupInfoModel(0,0,"All Groups (Click Add to change)",0,false,false));   
        }
        else {
            store.addAll(linkGroups);
        }        
    }

    private void readGroupsFromServer() {
        new RetryAction<CmList<GroupInfoModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetActiveGroupsAction action = new GetActiveGroupsAction(webLinkModel.getAdminId());
                setAction(action);
                CmShared.getCmService().execute(action,  this);
            }
            
            @Override
            public void oncapture(CmList<GroupInfoModel> groups) {
                _grid4Groups.getStore().addAll(groups);
                _grid4Groups.getSelectionModel().setSelection(webLinkModel.getLinkGroups());
                CmBusyManager.setBusy(false);
            }
        }.register();
        
        readGroupsFromServer();
    }
    
    
    interface Grid4GroupProperties extends PropertyAccess<String> {
        @Path("id")
        ModelKeyProvider<GroupInfoModel> key();
        ValueProvider<GroupInfoModel, String> groupName();
    }


    public void setGroups(List<GroupInfoModel> linkGroups) {
        _grid4Groups.getStore().clear();
        _grid4Groups.getStore().addAll(linkGroups);
    }

    public void removeSelectedGroup() {
        GroupInfoModel gi = _grid4Groups.getSelectionModel().getSelectedItem();
        if(gi == null) {
            CmMessageBox.showAlert("Select a Group first");
            return;
        }
        _grid4Groups.getStore().remove(gi);
        
        checkForEmptyStore();
    }
}
