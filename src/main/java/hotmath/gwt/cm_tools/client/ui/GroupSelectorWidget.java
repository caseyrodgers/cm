package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;

import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;

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
	private ListStore <GroupInfoModel> groupStore;
	private boolean inRegistrationMode;
	private ProcessTracker pTracker;
	private String id;

    public static final String NO_FILTERING = "--- No Filtering ---";
    

	public GroupSelectorWidget(final CmAdminModel cmAdminMdl, final ListStore<GroupInfoModel> groupStore,
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

	public ComboBox<GroupInfoModel> groupCombo() {
		final ComboBox<GroupInfoModel> combo = new ComboBox<GroupInfoModel>();
		combo.setFieldLabel("Group");
		combo.setForceSelection(false);
		combo.setDisplayField(GroupInfoModel.GROUP_NAME);
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

	    combo.addSelectionChangedListener(new SelectionChangedListener<GroupInfoModel>() {
			public void selectionChanged(SelectionChangedEvent<GroupInfoModel> se) {
			    GroupInfoModel gm = se.getSelectedItem();
	        	if (gm != null && inRegistrationMode && gm.getName().equals(GroupInfoModel.NEW_GROUP)) {
	        		new GroupWindow(null, cmAdminMdl, combo, true, null);
	        	}
	        }
	    });
		return combo;
	}

	private void getGroupListRPC(final Integer uid, final ListStore <GroupInfoModel> store) {

		pTracker.beginStep();
		
		new RetryAction <CmList<GroupInfoModel>>() {
		    @Override
		    public void attempt() {
		        CmBusyManager.setBusy(true, false);
		        CmServiceAsync s = CmShared.getCmService();
		        GetActiveGroupsAction action = new GetActiveGroupsAction(uid);
		        setAction(action);
		        s.execute(action,this);
		    }
            public void oncapture(CmList<GroupInfoModel> result) {

                groupStore.removeAll();

                // append 'New Group' to end of List if in Reg mode
                if (inRegistrationMode) {
                    GroupInfoModel gm = new GroupInfoModel();
                    gm.setGroupName(GroupInfoModel.NEW_GROUP);
                    gm.setId(GroupInfoModel.CREATE_GROUP);
                    result.add(gm);
                }
                // only include NO_FILTERING if NOT in Reg mode
                else {
                    GroupInfoModel gm = new GroupInfoModel();
                    gm.setGroupName(NO_FILTERING);
                    gm.setId(GroupInfoModel.NO_FILTERING);
                    groupStore.insert(gm, 0);
                }

                groupStore.add(result);
                
                pTracker.completeStep();
                pTracker.finish();

                CmBusyManager.setBusy(false);
            }
        }.register();
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
