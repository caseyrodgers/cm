package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.GroupManagerRegisterStudent;
import hotmath.gwt.cm_tools.client.ui.GroupWindow;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetGroupAggregateInfoAction;
import hotmath.gwt.shared.client.rpc.action.GetTemplateForSelfRegGroupAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;

import java.util.ArrayList;
import java.util.List;

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
    
    boolean cancelled;
    Grid<GroupInfoModel> _grid;
    ListStore<GroupInfoModel> store = new ListStore<GroupInfoModel>();
    CmAdminModel adminModel;
    int width = 400;
    
    public ManageGroupsWindow(CmAdminModel adminModel) {
        this.adminModel = adminModel;
        setSize(width,300);
        setHeading("Manage Student Groups");    
        
        readRpcData(adminModel.getId(), true);
        
        if (! cancelled) {
            drawGui();

            getButtonBar().setWidth(width-20);

            addSelfRegGroupLegend();

            addCloseButton();

            setModal(true);
            setResizable(false);
            setVisible(true);
        }
    }

	private void addSelfRegGroupLegend() {
		LayoutContainer lc = new LayoutContainer();
        lc.add(new Html("<img style='background:red;' width='8' height='8' src='/gwt-resources/images/spacer.gif'>&nbsp;Self Registration Group"));
        lc.setStyleAttribute("padding-right", String.valueOf(width-230));
        getButtonBar().add(lc);
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
                        readRpcData(adminModel.getId(), false);
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
                    GroupInfoModel gm = new GroupInfoModel();
                    gm.setId(gim.getId());
                    gm.setGroupName(gim.getName());
                    new GroupWindow(callback, adminModel, null, false, gm).setVisible(true);
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
        
        lc.add(new MyButton("Assign Students", "Add or remove students from selected group.",new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    new ManageGroupsAssignStudents(adminModel, gim,new CmAsyncRequestImplDefault() {
						@Override
						public void requestComplete(String requestData) {
							readRpcData(adminModel.getId(), false);
							CmAdminDataReader.getInstance().fireRefreshData();							
						}
					});
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

    private void readRpcData(final Integer adminId, final boolean closeOnCancel) {
    	
    	final CmWindow cmw = this;
    	
        new RetryAction<CmList<GroupInfoModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetGroupAggregateInfoAction action = new GetGroupAggregateInfoAction(adminId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<GroupInfoModel> result) {
                store.removeAll();
                store.add(result);
                CmBusyManager.setBusy(false);
            }

            @Override
            public void onCancel() {
            	if (closeOnCancel)
            		cmw.close();
            }
        }.register();
    }

    private void deleteGroup(final Integer adminId, final Integer groupId) {
    	groupActionRPC(adminId, groupId, null, GroupManagerAction.ActionType.DELETE);
    }

    private void unregisterGroup(final Integer adminId, final Integer groupId) {
    	groupActionRPC(adminId, groupId, null, GroupManagerAction.ActionType.UNREGISTER_STUDENTS);
    }

    protected void updateGroupRPC(final int adminUid, Integer groupId, String groupName) {
    	groupActionRPC(adminUid, groupId, groupName, GroupManagerAction.ActionType.UPDATE);
    }

    private void groupActionRPC(final Integer adminId, final Integer groupId, final String groupName, final GroupManagerAction.ActionType actionType) {

    	new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GroupManagerAction action = new GroupManagerAction(actionType, adminId);
                setAction(action);
                action.setGroupId(groupId);
                action.setGroupName(groupName);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData result) {
                readRpcData(adminId, false);
                CmAdminDataReader.getInstance().fireRefreshData();
            	CmBusyManager.setBusy(false);
            }
        }.register();

    }

    private void handleSelfRegGroup(final GroupInfoModel gim) {
        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetTemplateForSelfRegGroupAction action = new GetTemplateForSelfRegGroupAction(gim.getId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(StudentModelI sm) {
                try {
                	new GroupManagerRegisterStudent(sm, ManageGroupsWindow.this.adminModel, gim);
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }
        }.register();
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
    CheckBox tutoringAllowed;
    CheckBox limitGames;
    CheckBox stopAtProgramEnd;
    
    CmAdminModel cm;
    public GroupManagerGlobalSettings(CmAdminModel cm, GroupInfoModel gim) {
        this.cm = cm;
        this.gim = gim;
        setHeading("Group Settings for '" + gim.getName() + "'" );
        setSize(400,290);
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
        fl.setLabelWidth(175);
        fs.setLayout(fl);
        fs.setHeading("Group Settings");
        showWorkRequired = new CheckBox();
        showWorkRequired.setFieldLabel("Require Show Work");
        showWorkRequired.setBoxLabel("");
        fs.add(showWorkRequired);
        
        limitGames = new CheckBox();
        limitGames.setFieldLabel("Limit Games to One Per Lesson");
        limitGames.setBoxLabel("");
        fs.add(limitGames);

        stopAtProgramEnd = new CheckBox();
        stopAtProgramEnd.setFieldLabel("Stop at End of Program");
        stopAtProgramEnd.setBoxLabel("");
        fs.add(stopAtProgramEnd);

        tutoringAllowed = new CheckBox();
        tutoringAllowed. setFieldLabel("Tutoring Enabled");
        tutoringAllowed.setBoxLabel("");
        fs.add(tutoringAllowed);

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
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.GROUP_PROPERTY_SET,cm.getId());
                setAction(action);
                action.setGroupId(gim.getId());
                action.setShowWorkRequired(showWorkRequired.getValue());
                action.setDisallowTutoring( !tutoringAllowed.getValue());
                action.setStopAtProgramEnd(stopAtProgramEnd.getValue());
                action.setLimitGames(limitGames.getValue());                
                action.setPassPercent(null);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData result) {
                CmAdminDataReader.getInstance().fireRefreshData();
                close();
            	CmBusyManager.setBusy(false);
            }
        }.register();
    }
}
