package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetActiveGroupsAction;

import java.util.Comparator;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.Store.StoreSortInfo;
import com.sencha.gxt.widget.core.client.form.ComboBox;


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
	LabelProvider<GroupInfoModel> groupLabel;

    public static final String NO_FILTERING = "--- No Filtering ---";


	public GroupSelectorWidget(final CmAdminModel cmAdminMdl, final ListStore<GroupInfoModel> groupStore,
		boolean inRegistrationMode, ProcessTracker pTracker, String id, Boolean loadRpc, LabelProvider<GroupInfoModel> groupLabel) {

		this.cmAdminMdl= cmAdminMdl;
        this.groupStore = groupStore;
        this.inRegistrationMode = inRegistrationMode;
        this.pTracker = pTracker;
        this.id = id;
        this.groupLabel = groupLabel;

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
		final ComboBox<GroupInfoModel> combo = new ComboBox<GroupInfoModel>(groupStore, groupLabel);
		// combo.setFieldLabel("Group");
		combo.setForceSelection(false);
		combo.setEditable(false);
		// combo.setMaxLength(30);
		combo.setAllowBlank(false);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setStore(groupStore);
		combo.setTitle("Select a group");
		combo.setId(this.id);
		combo.setTypeAhead(true);
		combo.setSelectOnFocus(true);
		combo.setEmptyText("-- select a group --");
		combo.setWidth(280);
        
		combo.getStore().addSortInfo(new StoreSortInfo<GroupInfoModel>(new Comparator<GroupInfoModel>() {
            @Override
            public int compare(GroupInfoModel o1, GroupInfoModel o2) {
                return o1.getGroupName().compareTo(o1.getGroupName());
            }
        }, SortDir.ASC));

	    combo.addSelectionHandler(new SelectionHandler<GroupInfoModel>() {
            
            @Override
            public void onSelection(SelectionEvent<GroupInfoModel> event) {
			    GroupInfoModel gm = event.getSelectedItem();
	        	if (gm != null && inRegistrationMode && gm.getGroupName().equals(GroupInfoModel.NEW_GROUP)) {
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

                groupStore.clear();

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
                    groupStore.add(0, gm);
                }

                groupStore.addAll(result);
                
                pTracker.completeStep();
                pTracker.finish();

                CmBusyManager.setBusy(false);
            }
        }.register();
	}

	@Override
	public void refreshData() {
	     getGroupListRPC(cmAdminMdl.getUid(), groupStore);
	}

	/** Must remove the reader from the main data refresher once no longer needed.
	 * 
	 */
	public void release() {
		CmAdminDataReader.getInstance().removeReader(this);
	}
}
