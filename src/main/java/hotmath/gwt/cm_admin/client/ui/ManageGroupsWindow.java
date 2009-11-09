package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminDataRefresher;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.GroupManagerRegisterStudent;
import hotmath.gwt.cm_tools.client.ui.GroupWindow;
import hotmath.gwt.cm_tools.client.ui.PassPercent;
import hotmath.gwt.cm_tools.client.ui.PassPercentCombo;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetGroupAggregateInfoAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;
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
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ManageGroupsWindow extends CmWindow {
    

    Grid<GroupInfoModel> _grid;
    ListStore<GroupInfoModel> store = new ListStore<GroupInfoModel>();
    CmAdminModel adminModel;
    
    public ManageGroupsWindow(CmAdminModel adminModel) {
        this.adminModel = adminModel;
        setSize(400,300);
        setHeading("Manage Student Groups");    
        
        readRpcData(adminModel.getId());
        
        drawGui();
        
        Button close = new Button("Close");
        close.addSelectionListener(new SelectionListener<ButtonEvent>() {
            
            @Override
            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        
        
        addButton(close);
        setModal(true);
        setResizable(false);
        setVisible(true);
    }
    
    
    private GroupInfoModel getGroupInfo() {
        GroupInfoModel gim =_grid.getSelectionModel().getSelectedItem();
        if(gim == null) {
            CatchupMathTools.showAlert("Please select a group first");
        }
        return gim;
    }
    
    
    private void drawGui() {
        
        setLayout(new BorderLayout());
        
        _grid = defineGrid(store, defineColumns());
        add(_grid, new BorderLayoutData(LayoutRegion.WEST,200));
        
        LayoutContainer lc = new LayoutContainer();
        lc.setStyleName("manage-groups-window-buttons");

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
                if(gim != null) {
                    if(gim.getAdminId() == 0) {
                        CatchupMathTools.showAlert("This group name cannot be renamed.");
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
                if(gim != null) {
                    if(gim.getName().equals("none") || gim.getName().equals("All Students")){
                        CatchupMathTools.showAlert("This group cannot be removed.");
                        return;
                    }
                        
                    MessageBox.confirm("Remove group", "Are you sure you want to remove the group '" + gim.getName() + "' (sets existing group students to group 'none')?  ", new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if(be.getButtonClicked().getText().equalsIgnoreCase("yes"))
                                deleteGroup(adminModel.getId(), gim.getId());
                        }
                    });
                }
            }
        }));
        

        lc.add(new MyButton("Unregister Students", "Unregister all students in selected group.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if(gim != null) {
                    if(gim.getCount() == 0) {
                        CatchupMathTools.showAlert("There are no students to unregister.");
                        return;
                    }
                    String msg = "Are you sure you want to unregister the " + getCountString(gim) + " assigned to group '" + gim.getName() + "'?";
                    
                    MessageBox.confirm("Unregister group", msg, new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if(be.getButtonClicked().getText().equalsIgnoreCase("yes"))
                                
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
                if(gim != null) {
                    if(gim.getCount() == 0) {
                        CatchupMathTools.showAlert("There are no students assigned to this group.");
                        return;
                    }
                    new GroupManagerRegisterStudent(null, ManageGroupsWindow.this.adminModel,gim);
                }
            }
        }));
        
        
        lc.add(new MyButton("Group Settings", "Settings affecting all students in group.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if(gim != null) {
                    if(gim.getCount() == 0) {
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
        grid.getSelectionModel().addListener(Events.RowDoubleClick, new Listener<SelectionEvent<StudentModel>>() {
            public void handleEvent(SelectionEvent<StudentModel> se) {

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
        group.setId("group_name");
        group.setHeader("Group");
        group.setWidth(145);
        group.setSortable(true);
        configs.add(group);
        
        ColumnConfig usage = new ColumnConfig();
        usage.setId("student_count");
        usage.setHeader("Count");
        usage.setToolTip("Students in group");
        usage.setWidth(50);
        usage.setSortable(true);
        configs.add(usage);

        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
    
    
    private void readRpcData(final Integer adminId) {
        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        cmService.execute(new GetGroupAggregateInfoAction(adminId), new AsyncCallback<CmList<GroupInfoModel>>() {
            public void onSuccess(CmList<GroupInfoModel> result) {
                store.removeAll();
                store.add(result);
            }
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });
    }

    
    private void deleteGroup(final Integer adminId, final Integer groupId) {
        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        
        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.DELETE,adminId);
        action.setGroupId(groupId);
        cmService.execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                readRpcData(adminId);
                CmAdminDataReader.getInstance().fireRefreshData();
            }
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });
    }

    
    private void unregisterGroup(final Integer adminId, final Integer groupId) {
        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.UNREGISTER_STUDENTS,adminId);
        action.setGroupId(groupId);
        cmService.execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                readRpcData(adminId);
                CmAdminDataReader.getInstance().fireRefreshData();                
            }
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });     
    }
    
    

    protected void updateGroupRPC(final int adminUid, Integer groupId, String groupName) {
        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.UPDATE,adminUid);
        action.setGroupId(groupId);
        action.setGroupName(groupName);
        cmService.execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                readRpcData(adminUid);
                CmAdminDataReader.getInstance().fireRefreshData();              
            }
            public void onFailure(Throwable caught) {
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });     
    }

}

/** Provide custom button sizes and configuration
 * 
 * @author casey
 *
 */
class MyButton extends Button {
    public MyButton(String name, String tooltip,SelectionListener<ButtonEvent> listener) {
        super(name);
        addStyleName("manage-groups-window-buttons-button");
        setToolTip(tooltip);
        addSelectionListener(listener);
        setWidth("115px");
    }
};



class GroupManagerGlobalSettings extends CmWindow {
    
    GroupInfoModel gim;
    CheckBox showWorkRequired, disallowTutoring;
    PassPercentCombo passPercent;
    CmAdminModel cm;
    public GroupManagerGlobalSettings(CmAdminModel cm, GroupInfoModel gim) {
        this.cm = cm;
        this.gim = gim;
        setHeading("Group Settings for '" + gim.getName() + "'" );
        setSize(400,250);
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
        
        disallowTutoring = new CheckBox();
        disallowTutoring.setValue(true);
        disallowTutoring.setBoxLabel("");
        disallowTutoring.setFieldLabel("Disallow Tutoring");
        fs.add(disallowTutoring);
        
        passPercent = new PassPercentCombo();
        passPercent.enable();
        fs.add(passPercent);
        
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
        
        CatchupMathTools.setBusy(true);
        CmServiceAsync cmService = (CmServiceAsync)Registry.get("cmService");
        
        GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.GROUP_PROPERTY_SET,cm.getId());
        action.setGroupId(gim.getId());
        action.setDisallowTutoring(disallowTutoring.getValue());
        action.setShowWorkRequired(showWorkRequired.getValue());
        action.setPassPercent(passPercent.getPassPercent());
        cmService.execute(action, new AsyncCallback<RpcData>() {
            public void onSuccess(RpcData result) {
                CmAdminDataReader.getInstance().fireRefreshData();
                close();
                
                CatchupMathTools.setBusy(false);
            }
            public void onFailure(Throwable caught) {
                CatchupMathTools.setBusy(false);
                CatchupMathTools.showAlert(caught.getMessage());
            }
        });
    }
    
    
    private ComboBox<PassPercent> passPercentCombo(ListStore<PassPercent> store) {
        ComboBox<PassPercent> combo = new ComboBox<PassPercent>();
        combo.setValue(store.getAt(2));
        combo.setFieldLabel("Pass Percent");
        combo.setForceSelection(false);
        combo.setDisplayField("pass-percent");
        combo.setEditable(false);
        combo.setMaxLength(30);
        combo.setAllowBlank(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setStore(store);
        combo.setTitle("Select a percentage");
        combo.setId("pass-combo");
        combo.setTypeAhead(true);
        combo.setSelectOnFocus(true);
        combo.setEmptyText("-- select a value --");
        combo.disable();
        combo.setWidth(280);
        return combo;
    }
    
}

