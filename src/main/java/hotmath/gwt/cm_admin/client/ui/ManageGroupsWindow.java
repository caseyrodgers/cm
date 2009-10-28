package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.GroupManagerRegisterStudent;
import hotmath.gwt.cm_tools.client.ui.GroupWindow;
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
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
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

        lc.add(new MyButton("New Group", "Create a new group",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
                    public void requestComplete(String requestData) {
                        readRpcData(adminModel.getId());
                    }
                };
                new GroupWindow(callback, adminModel,null,true,null).setVisible(true);              
            }
        }));
        
        lc.add(new MyButton("Edit Group", "Edit existing group name",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                
                final GroupInfoModel gim = getGroupInfo();
                if(gim != null) {
                    if(gim.getAdminId() == 0) {
                        CatchupMathTools.showAlert("This group is not editable");
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

        
        lc.add(new MyButton("Remove Group", "Remove selected group",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if(gim != null) {
                    if(gim.getName().equals("none")){
                        CatchupMathTools.showAlert("This group cannot be removed");
                        return;
                    }
                        
                    MessageBox.confirm("Delete group", "Are you sure you want to delete the group '" + gim.getName() + "'?", new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {
                            if(be.getButtonClicked().getText().equalsIgnoreCase("yes"))
                                deleteGroup(gim.getAdminId(), gim.getId());
                        }
                    });
                }
            }
        }));
        

        lc.add(new MyButton("Unregister Students", "Unregister all students in selected group",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if(gim != null) {
                    MessageBox.confirm("Unregister group", "Are you sure you want to unregister the " + gim.getCount() + " students assigned to group '" + gim.getName() + "'?", new Listener<MessageBoxEvent>() {
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
        
        
        lc.add(new MyButton("Assign Program", "Assign a program to all students in group.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if(gim != null) {
                    new GroupManagerRegisterStudent(null, ManageGroupsWindow.this.adminModel,gim);
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
                
                CatchupMathTools.showAlert("Students were successfully unregistered");
                
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
                EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_REFRESH_STUDENT_DATA));                
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
        setWidth(150);
        setStyleName("manage-groups-window-buttons-button");
        setToolTip(tooltip);
        addSelectionListener(listener);
    }
};



