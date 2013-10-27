package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class GroupCombo implements IsWidget {
    
    ComboBox<GroupInfoModel> _combo;
    GroupComboProperties props = GWT.create(GroupComboProperties.class);
    private int adminId;
    
    interface Callback {
        void groupSelected(GroupInfoModel group);
    }
    
    public GroupCombo(int adminId, final Callback callback) {
        this.adminId = adminId;
        
        ListStore<GroupInfoModel> store = new ListStore<GroupInfoModel>(props.key());
        _combo = new ComboBox<GroupInfoModel>(store, props.groupName());
        _combo.setEmptyText("Filter by Group");
        _combo.addSelectionHandler(new SelectionHandler<GroupInfoModel>() {
            @Override
            public void onSelection(SelectionEvent<GroupInfoModel> event) {
                callback.groupSelected(event.getSelectedItem());
            }
        });
        _combo.setTriggerAction(TriggerAction.ALL);
        
        getGroupsFromServer();
    }

    private void getGroupsFromServer() {
        new RetryAction <CmList<GroupInfoModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true, false);
                CmServiceAsync s = CmShared.getCmService();
                GetActiveGroupsAction action = new GetActiveGroupsAction(adminId);
                setAction(action);
                s.execute(action,this);
            }
            public void oncapture(CmList<GroupInfoModel> result) {
                CmBusyManager.setBusy(false);
                _combo.getStore().clear();
                _combo.getStore().add(new GroupInfoModel(0,0,"-- No Group Filter --",0,true,false));
                _combo.getStore().addAll(result);
            }
        }.register();    
    }

    @Override
    public Widget asWidget() {
        return _combo;
    }
    
    interface GroupComboProperties extends PropertyAccess<String> {
        @Path("groupId")
        public ModelKeyProvider<GroupInfoModel> key();
        
        public LabelProvider<GroupInfoModel> groupName();
    }
}