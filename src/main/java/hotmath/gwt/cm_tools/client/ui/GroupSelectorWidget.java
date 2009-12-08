package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class GroupSelectorWidget {
	
	CmAdminModel cmAdminMdl;
	private ListStore <GroupModel> groupStore;
	private ComboBox <GroupModel> groupCombo;
	private boolean includeCreate;
	private ProcessTracker pTracker;
	private String id;


    public static final String NO_FILTERING = "--- No Filtering ---";
    

	public GroupSelectorWidget(final CmAdminModel cmAdminMdl, final ListStore<GroupModel> groupStore, boolean includeCreate,
		ProcessTracker pTracker, String id) {
		this.cmAdminMdl= cmAdminMdl;
        this.groupStore = groupStore;
        this.includeCreate = includeCreate;
        this.pTracker = pTracker;
        this.id = id;
        
        
        getGroupListRPC(cmAdminMdl.getId(), groupStore);
        
        
        CmAdminDataReader.getInstance().addReader(new CmAdminDataRefresher() {
            public void refreshData() {
                getGroupListRPC(cmAdminMdl.getId(), groupStore);
            }
        });
	}
	
	public ComboBox<GroupModel> groupCombo() {
		final ComboBox<GroupModel> combo = new ComboBox<GroupModel>();
		combo.setFieldLabel("Group");
		combo.setValue(groupStore.getAt(0));	
		combo.setForceSelection(false);
		combo.setDisplayField(GroupModel.NAME_KEY);
		combo.setEditable(false);
		combo.setMaxLength(30);
		combo.setAllowBlank(false);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setStore(groupStore);
		combo.setTitle("Select a group");
		combo.setId(this.id);
		combo.setTypeAhead(true);
		combo.setSelectOnFocus(true);
		combo.setEmptyText("-- select a group --");
		combo.setWidth(280);
		
	    combo.addSelectionChangedListener(new SelectionChangedListener<GroupModel>() {
			public void selectionChanged(SelectionChangedEvent<GroupModel> se) {

	        	GroupModel gm = se.getSelectedItem();
	        	if (includeCreate && gm.getName().equals(GroupModel.NEW_GROUP)) {
	        		new GroupWindow(null, cmAdminMdl, combo, true, null);
	        	}
	        }
	    });
		return combo;
	}

	private void getGroupListRPC(Integer uid, final ListStore <GroupModel> store) {

		pTracker.beginStep();
		
		CatchupMathTools.setBusy(true);
		
		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		GetActiveGroupsAction action = new GetActiveGroupsAction(uid);
		s.execute(action, new AsyncCallback <CmList<GroupModel>>() {

			public void onSuccess(CmList<GroupModel> result) {
				// append 'New Group' to end of List
				if (includeCreate) {
					GroupModel gm = new GroupModel();
    				gm.setName(GroupModel.NEW_GROUP);
	    			gm.setId(GroupModel.NEW_GROUP);
		    		result.add(gm);
				}
				
				groupStore.removeAll();
		        GroupModel gm = new GroupModel();
		        gm.setName(NO_FILTERING);
		        gm.setId(NO_FILTERING);
		        groupStore.insert(gm, 0);				
				groupStore.add(result);
				
				pTracker.completeStep();
				pTracker.finish();
				
				CatchupMathTools.setBusy(false);
        	}

			public void onFailure(Throwable caught) {
			    CatchupMathTools.setBusy(false);
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
        	}
        });
		
	}
}
