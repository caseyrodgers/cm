package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.GroupManagerRegisterStudent;
import hotmath.gwt.cm_tools.client.ui.GroupWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetGroupAggregateInfoAction;
import hotmath.gwt.shared.client.rpc.action.GetTemplateForSelfRegGroupAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;
import hotmath.gwt.shared.client.util.RpcData;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class ManageGroupsWindow extends CmWindow {
    

    Grid<GroupInfoModel> _grid;
    ListStore<GroupInfoModel> store = new ListStore<GroupInfoModel>();
    CmAdminModel adminModel;
    int width = 400;
    
    public ManageGroupsWindow(CmAdminModel adminModel) {
        this.adminModel = adminModel;
        setSize(width,300);
        setHeading("Manage Student Groups");    
        
        readRpcData(adminModel.getId());
        
        drawGui();

        getButtonBar().setWidth(width-20);

        addSelfRegGroupLegend();

        addCloseButton();

        setModal(true);
        setResizable(false);
        setVisible(true);
    }

	private void addSelfRegGroupLegend() {
		LayoutContainer lc = new LayoutContainer();
        lc.add(new Html("<img style='background:red;' width='8' height='8' src='/gwt-resources/images/spacer.gif'>&nbsp;Self Registration Group"));
        lc.setStyleAttribute("padding-right", String.valueOf(width-230));
        getButtonBar().add(lc);
	}

	private void addCloseButton() {
		Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        addButton(close);
	}

    private GroupInfoModel getGroupInfo() {
        GroupInfoModel gim =_grid.getSelectionModel().getSelectedItem();
        if (gim == null) {
            CatchupMathTools.showAlert("Please select a group first");
        }
        return gim;
    }

    private void drawGui() {

        setLayout(new BorderLayout());

        _grid = defineGrid(store, defineColumns());
        BorderLayoutData bld = new BorderLayoutData(LayoutRegion.WEST,190);
        add(_grid, bld);

        LayoutContainer lc = new LayoutContainer();
        lc.addStyleName("manage-groups-window-buttons");

        lc.add(new MyButton("New Group Name", "Create a new group name.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
                    public void requestComplete(String requestData) {
                        readRpcData(adminModel.getId());
                    }
                };
                new GroupWindow(callback, adminModel,null,true,null).setVisible(true);              
            }
        }));

        lc.add(new MyButton("Rename Group", "Rename group to a new name.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getAdminId() == 0) {
                        CatchupMathTools.showAlert("This group cannot be renamed.");
                        return;
                    }
                    
                    CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
                        public void requestComplete(String requestData) {
                            updateGroupRPC(gim.getAdminId(),gim.getId(), requestData);
                        }
                    };
                    GroupModel gm = new GroupModel();
                    gm.setId(Integer.toString(gim.getId()));
                    gm.setName(gim.getName());
                    new GroupWindow(callback, adminModel,null,false,gm).setVisible(true);
                }
            }
        }));

        lc.add(new MyButton("Remove Group Name", "Remove selected group name and move assigned students to group 'none'.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getAdminId() == 0) {
                        CatchupMathTools.showAlert("This group cannot be removed.");
                        return;
                    }
                        
                    MessageBox.confirm("Remove group", "Are you sure you want to remove the group '" + gim.getName() + "' (sets existing group students to group 'none')?  ", new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getText().equalsIgnoreCase("yes"))
                                deleteGroup(adminModel.getId(), gim.getId());
                        }
                    });
                }
            }
        }));

        lc.add(new MyButton("Unregister Students", "Unregister all students in selected group.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getCount() == 0) {
                        CatchupMathTools.showAlert("There are no students to unregister.");
                        return;
                    }
                    String msg = "Are you sure you want to unregister the " + getCountString(gim) + " assigned to group '" + gim.getName() + "'?";

                    MessageBox.confirm("Unregister group", msg, new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if (be.getButtonClicked().getText().equalsIgnoreCase("yes"))

                                /** Use the actual adminId for this user, not the group
                                 */
                                unregisterGroup(ManageGroupsWindow.this.adminModel.getId(), gim.getId());
                        }
                    });
                }
            }
        }));

        lc.add(new MyButton("Reassign Program", "Reassign a program to all students in group.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getCount() > 0 || gim.getIsSelfReg()) {

                        if (gim.getIsSelfReg()) {
                        	handleSelfRegGroup(gim);
                        }
                        else {
                            new GroupManagerRegisterStudent(null, ManageGroupsWindow.this.adminModel, gim);
                        }
                    }
                    else {
                        CatchupMathTools.showAlert("There are no students assigned to this group.");
                    }
                }
            }
        }));

        lc.add(new MyButton("Group Settings", "Settings affecting all students in group.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getCount() == 0) {
                        CatchupMathTools.showAlert("There are no students assigned to this group.");
                        return;
                    }

                    new GroupManagerGlobalSettings(adminModel,gim);
                }
            }
        }));        

        add(lc, new BorderLayoutData(LayoutRegion.EAST,200));
    }

    private Grid<GroupInfoModel> defineGrid(final ListStore<GroupInfoModel> store, ColumnModel cm) {
        final Grid<GroupInfoModel> grid = new Grid<GroupInfoModel>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModelExt>>() {
            public void handleEvent(SelectionEvent<StudentModelExt> se) {

                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    CatchupMathTools.showAlert("On click");
                }
            }
        });

        grid.setWidth("200px");
        grid.setHeight("250px");
        return grid;
    }

    /** Return string that deals with singular/plural of student count
     * 
     */
    private String getCountString(GroupInfoModel gim) {
        return gim.getCount() + " " + (gim.getCount() == 1?"student":"students");
    }

    private ColumnModel defineColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig group = new ColumnConfig();
        group.setId(GroupInfoModel.GROUP_NAME);
        group.setHeader("Group");
        group.setWidth(120);
        group.setSortable(true);
        group.setRenderer(new GridCellRenderer<GroupInfoModel>() {
			@Override
			public Object render(GroupInfoModel gim, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<GroupInfoModel> store, Grid<GroupInfoModel> grid) {
                if (gim.getIsSelfReg())
                    return "<span style='color: red'>" + gim.getName() + "</span>";
                else {
                	return gim.getName();
                }
			}
        });
        configs.add(group);

        ColumnConfig usage = new ColumnConfig();
        usage.setId(GroupInfoModel.STUDENT_COUNT);
        usage.setHeader("Count");
        usage.setToolTip("Students in group");
        usage.setWidth(48);
        usage.setSortable(true);
        configs.add(usage);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }

    private void readRpcData(final Integer adminId) {
    	CmBusyManager.setBusy(true);

        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        cmService.execute(new GetGroupAggregateInfoAction(adminId), new CmAsyncCallback<CmList<GroupInfoModel>>() {
            public void onSuccess(CmList<GroupInfoModel> result) {
                store.removeAll();
                store.add(result);
                CmBusyManager.setBusy(false);
            }
            public void onFailure(Throwable caught) {
            	CmBusyManager.setBusy(false);
            	super.onFailure(caught);
            }
        });
    }

    private void deleteGroup(final Integer adminId, final Integer groupId) {

    	CmBusyManager.setBusy(true);

        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");

        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.DELETE, adminId);
        action.setGroupId(groupId);
        cmService.execute(action, new CmAsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
            	CmBusyManager.setBusy(false);
                readRpcData(adminId);
                CmAdminDataReader.getInstance().fireRefreshData();
            }
            public void onFailure(Throwable caught) {
            	CmBusyManager.setBusy(false);
            	super.onFailure(caught);
            }
        });
    }

    private void unregisterGroup(final Integer adminId, final Integer groupId) {
    	CmBusyManager.setBusy(true, false);

        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.UNREGISTER_STUDENTS, adminId);
        action.setGroupId(groupId);
        cmService.execute(action, new CmAsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                readRpcData(adminId);
                CmAdminDataReader.getInstance().fireRefreshData();
                CmBusyManager.setBusy(false);
            }
            public void onFailure(Throwable caught) {
            	CmBusyManager.setBusy(false);
            	super.onFailure(caught);
            }
        });
    }

    protected void updateGroupRPC(final int adminUid, Integer groupId, String groupName) {

    	CmBusyManager.setBusy(true,false);

        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.UPDATE,adminUid);
        action.setGroupId(groupId);
        action.setGroupName(groupName);
        cmService.execute(action, new CmAsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                readRpcData(adminUid);
                CmAdminDataReader.getInstance().fireRefreshData();
            	CmBusyManager.setBusy(false);
            }
            public void onFailure(Throwable caught) {
            	CmBusyManager.setBusy(false);
            	super.onFailure(caught);
            }
       });     
    }

    private void handleSelfRegGroup(final GroupInfoModel gim) {
        CmBusyManager.setBusy(true);

        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
        GetTemplateForSelfRegGroupAction action = new GetTemplateForSelfRegGroupAction(gim.getId());
        s.execute(action, new CmAsyncCallback<StudentModelI>() {
            public void onSuccess(StudentModelI sm) {
                try {
                	new GroupManagerRegisterStudent(sm, ManageGroupsWindow.this.adminModel, gim);
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }
            public void onFailure(Throwable caught) {
                super.onFailure(caught);
                CmBusyManager.setBusy(false);
            }
        });
    }

}

/** Provide standard button sizes and configuration
 * 
 * @author casey
 *
 */
class MyButton extends Button {
    public MyButton(String name, String tooltip,SelectionListener<ButtonEvent> listener) {
        super(name);
        addStyleName("manage-groups-window-buttons-button");
        setToolTip(tooltip);
        if (listener != null)
            addSelectionListener(listener);
        setWidth("150px");
    }
};


/** Display window showing potential group
 *  wide settings.
 *  
 * @author casey
 *
 */
class GroupManagerGlobalSettings extends CmWindow {
    
    GroupInfoModel gim;
    CheckBox showWorkRequired;
    CmAdminModel cm;
    public GroupManagerGlobalSettings(CmAdminModel cm, GroupInfoModel gim) {
        this.cm = cm;
        this.gim = gim;
        setHeading("Group Settings for '" + gim.getName() + "'" );
        setSize(400,230);
        drawGui();
        setModal(true);
        setResizable(false);
        setVisible(true);
    }

    private void drawGui() {
        setLayout(new FitLayout());
        FormPanel form = new FormPanel();
        form.setFrame(false);
        form.getHeader().setVisible(false);
        FieldSet fs = new FieldSet();
        FormLayout fl = new FormLayout();
        fl.setLabelWidth(130);
        fs.setLayout(fl);
        fs.setHeading("Group Settings");
        showWorkRequired = new CheckBox();
        showWorkRequired.setFieldLabel("Show Work Required");
        showWorkRequired.setBoxLabel("");
        fs.add(showWorkRequired);

        form.add(fs);

        Html html = new Html("<p style='font-style: italcs;'>Changes apply to all students in the group '" + gim.getName() + "'.</p>");

        FieldSet fsInfo = new FieldSet();
        fsInfo.setHeading("");
        fsInfo.add(html);
        form.add(fsInfo);

        add(form);

        Button apply = new Button("Apply Changes");
        apply.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                applyChanges();
            }
        });
        addButton(apply);
        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });

        addButton(close);
    }

    private void applyChanges() {

    	CmBusyManager.setBusy(true,false);

        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.GROUP_PROPERTY_SET,cm.getId());
        action.setGroupId(gim.getId());
        action.setDisallowTutoring(true);
        action.setShowWorkRequired(showWorkRequired.getValue());
        action.setPassPercent(70);
        cmService.execute(action, new CmAsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                CmAdminDataReader.getInstance().fireRefreshData();
                close();

                CmBusyManager.setBusy(false);
            }
            public void onFailure(Throwable caught) {
            	CmBusyManager.setBusy(false);
            	super.onFailure(caught);
            }
        });
    }
}
