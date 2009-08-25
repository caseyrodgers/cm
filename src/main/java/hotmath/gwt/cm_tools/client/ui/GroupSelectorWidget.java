package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;


import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;

public class GroupSelectorWidget {
	
	CmAdminModel cmAdminMdl;
	private ListStore <GroupModel> groupStore;
	private ComboBox <GroupModel> groupCombo;
	private boolean includeCreate;
	private ProcessTracker pTracker;
	private String id;

	public GroupSelectorWidget(CmAdminModel cmAdminMdl, ListStore<GroupModel> groupStore, boolean includeCreate,
		ProcessTracker pTracker, String id) {
		this.cmAdminMdl= cmAdminMdl;
        this.groupStore = groupStore;
        this.includeCreate = includeCreate;
        this.pTracker = pTracker;
        this.id = id;
        getGroupListRPC(cmAdminMdl.getId(), groupStore);
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
	        		new GroupWindow(cmAdminMdl, combo, true);
	        	}
	        }
	    });
		return combo;
	}

	private void getGroupListRPC(Integer uid, final ListStore <GroupModel> store) {

		pTracker.beginStep();
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getActiveGroups(uid, new AsyncCallback <List<GroupModel>>() {

			public void onSuccess(List<GroupModel> result) {
				// append 'New Group' to end of List
				if (includeCreate) {
					GroupModel gm = new GroupModel();
    				gm.setName(GroupModel.NEW_GROUP);
	    			gm.setId(GroupModel.NEW_GROUP);
		    		result.add(gm);
				}
				
				groupStore.add(result);
				
				pTracker.completeStep();
				pTracker.finish();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
        	}
        });
		
	}
}
