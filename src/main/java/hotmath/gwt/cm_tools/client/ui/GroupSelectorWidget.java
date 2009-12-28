package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
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

/** Provide central control of the group selection box
 * 
 * NOTE: important!  We register with CmAdminDataReader on construction, so
 * we must unregister this listener when closed.  Otherwise, this listener
 * will get called each time the data is refreshed.
 * 
 * @author casey
 *
 */
public class GroupSelectorWidget implements CmAdminDataRefresher {
	
	CmAdminModel cmAdminMdl;
	private ListStore <GroupModel> groupStore;
	private boolean inRegistrationMode;
	private ProcessTracker pTracker;
	private String id;

    public static final String NO_FILTERING = "--- No Filtering ---";
    

	public GroupSelectorWidget(final CmAdminModel cmAdminMdl, final ListStore<GroupModel> groupStore,
		boolean inRegistrationMode, ProcessTracker pTracker, String id, Boolean loadRpc) {
		this.cmAdminMdl= cmAdminMdl;
        this.groupStore = groupStore;
        this.inRegistrationMode = inRegistrationMode;
        this.pTracker = pTracker;
        this.id = id;

        /** Only refresh on startup if requested.  This is to prevent loading twice on 
         *  startup of StudentGridPanel ... once in constructor and once when
         *  CmAdminDataReader is fired.
         * 
         */
        if(loadRpc)
        	refreshData();

        CmAdminDataReader.getInstance().addReader(this);
	}

	public ComboBox<GroupModel> groupCombo() {
		final ComboBox<GroupModel> combo = new ComboBox<GroupModel>();
		combo.setFieldLabel("Group");
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
	        	if (inRegistrationMode && gm.getName().equals(GroupModel.NEW_GROUP)) {
	        		new GroupWindow(null, cmAdminMdl, combo, true, null);
	        	}
	        }
	    });
		return combo;
	}

	private void getGroupListRPC(Integer uid, final ListStore <GroupModel> store) {

		pTracker.beginStep();

		CmBusyManager.setBusy(true, false);

		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		GetActiveGroupsAction action = new GetActiveGroupsAction(uid);
		s.execute(action, new AsyncCallback <CmList<GroupModel>>() {

			public void onSuccess(CmList<GroupModel> result) {

				groupStore.removeAll();

				// append 'New Group' to end of List if in Reg mode
				if (inRegistrationMode) {
					GroupModel gm = new GroupModel();
    				gm.setName(GroupModel.NEW_GROUP);
	    			gm.setId(GroupModel.NEW_GROUP);
		    		result.add(gm);
				}
				// only include NO_FILTERING if NOT in Reg mode
				else {
			        GroupModel gm = new GroupModel();
			        gm.setName(NO_FILTERING);
			        gm.setId(NO_FILTERING);
			        groupStore.insert(gm, 0);
				}

				groupStore.add(result);
				
				pTracker.completeStep();
				pTracker.finish();

				CmBusyManager.setBusy(false);
        	}

			public void onFailure(Throwable caught) {
				CmBusyManager.setBusy(false);
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
        	}
        });

	}

	@Override
	public void refreshData() {
	     getGroupListRPC(cmAdminMdl.getId(), groupStore);
	}

	/** Must remove the reader from the main data refresher once no longer needed.
	 * 
	 */
	public void release() {
		CmAdminDataReader.getInstance().removeReader(this);
	}
}
