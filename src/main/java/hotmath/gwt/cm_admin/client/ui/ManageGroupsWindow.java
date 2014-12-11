package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.model.GroupInfoModel;
import hotmath.gwt.cm_rpc.client.model.StudentModelI;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminDataReader;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.GroupManagerRegisterStudent;
import hotmath.gwt.cm_tools.client.ui.GroupWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.MyFieldSet;
import hotmath.gwt.cm_tools.client.ui.PassPercent;
import hotmath.gwt.cm_tools.client.ui.PassPercentCombo;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.shared.client.rpc.action.GetGroupAggregateInfoAction;
import hotmath.gwt.shared.client.rpc.action.GetTemplateForSelfRegGroupAction;
import hotmath.gwt.shared.client.rpc.action.GroupManagerAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.RowDoubleClickEvent.RowDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class ManageGroupsWindow extends GWindow {

    boolean cancelled;
    Grid<GroupInfoModel> _grid;
    CmAdminModel adminModel;
    int width = 400;


    interface GroupProperties extends PropertyAccess<String> {
        ModelKeyProvider<GroupInfoModel> id();
        ValueProvider<GroupInfoModel, String> groupName();
        ValueProvider<GroupInfoModel, Integer> studentCount();
    }
    static GroupProperties __propsGroupInfo = GWT.create(GroupProperties.class);

    public ManageGroupsWindow(CmAdminModel adminModel) {

        super(false);

        this.adminModel = adminModel;
        setPixelSize(width, 350);
        setHeadingText("Manage Student Groups");

        readRpcData(adminModel.getUid(), true);

        if (!cancelled) {
            drawGui();

            getButtonBar().setWidth(width - 20);

            addSelfRegGroupLegend();

            addCloseButton();

            setModal(true);
            setResizable(true);
            setMaximizable(true);
            setVisible(true);
        }
    }

    private void addSelfRegGroupLegend() {
        SimpleContainer simp = new SimpleContainer();
        simp.setWidget(new HTML("<span style='margin-right: 30px;'><img style='background:red;' width='8' height='8' src='/gwt-resources/images/spacer.gif'>&nbsp;Self Registration Group</span>"));
        getButtonBar().add(simp);
    }

    private GroupInfoModel getGroupInfo() {
        GroupInfoModel gim = _grid.getSelectionModel().getSelectedItem();
        if (gim == null) {
            CmMessageBox.showAlert("Please select a group first");
        }
        return gim;
    }

    private void drawGui() {

        ListStore<GroupInfoModel> store = new ListStore<GroupInfoModel>(__propsGroupInfo.id());

        BorderLayoutContainer borderLayout = new BorderLayoutContainer();
        setWidget(borderLayout);

        _grid = defineGrid(store, defineColumns());
        borderLayout.setCenterWidget(_grid);

        VerticalLayoutContainer vertButtons = new VerticalLayoutContainer();
        vertButtons.addStyleName("manage-groups-window-buttons");

        vertButtons.add(new MyButton("New Group Name", "Create a new group name.", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
                    public void requestComplete(String requestData) {
                        readRpcData(adminModel.getUid(), false);
                    }
                };
                new GroupWindow(callback, adminModel, null, true, null).setVisible(true);
            }
        }));

        vertButtons.add(new MyButton("Rename Group", "Rename group to a new name.", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getAdminId() == 0) {
                        CmMessageBox.showAlert("This group cannot be renamed.");
                        return;
                    }

                    CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
                        public void requestComplete(String requestData) {
                            updateGroupRPC(gim.getAdminId(), gim.getId(), requestData);
                        }
                    };
                    GroupInfoModel gm = new GroupInfoModel();
                    gm.setId(gim.getId());
                    gm.setGroupName(gim.getGroupName());
                    new GroupWindow(callback, adminModel, null, false, gm).setVisible(true);
                }
            }
        }));

        vertButtons.add(new MyButton("Remove Group Name", "Remove selected group name and move assigned students to group 'none'.", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getAdminId() == 0 || gim.isSystemSelfReg()) {
                        CmMessageBox.showAlert("This group cannot be removed.");
                        return;
                    }

                    CmMessageBox.confirm("Remove group", "Are you sure you want to remove the group '" + gim.getGroupName()
                            + "' (sets existing group students to group 'none')?  ", new ConfirmCallback() {

                        @Override
                        public void confirmed(boolean yesNo) {
                            if (yesNo)
                                deleteGroup(adminModel.getUid(), gim.getId());
                        }
                    });
                }
            }
        }));

        vertButtons.add(new MyButton("Unregister Students", "Unregister all students in selected group.", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getStudentCount() == 0) {
                        CmMessageBox.showAlert("There are no students to unregister.");
                        return;
                    }
                    String msg = "Are you sure you want to unregister the " + getCountString(gim) + " assigned to group '" + gim.getGroupName() + "'?";

                    CmMessageBox.confirm("Unregister group", msg, new ConfirmCallback() {

                        @Override
                        public void confirmed(boolean yesNo) {
                            if (yesNo)
                                /**
                                 * Use the actual adminId for this user, not the
                                 * group
                                 */
                                unregisterGroup(ManageGroupsWindow.this.adminModel.getUid(), gim.getId());
                        }
                    });
                }
            }
        }));

        vertButtons.add(new MyButton("Reassign Program", "Reassign a program to all students in group.", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getStudentCount() > 0 || gim.isSelfReg()) {

                        if (gim.isSelfReg()) {
                            handleSelfRegGroup(gim);
                        } else {
                            new GroupManagerRegisterStudent(null, ManageGroupsWindow.this.adminModel, gim);
                        }
                    } else {
                        CmMessageBox.showAlert("There are no students assigned to this group.");
                    }
                }
            }
        }));

        vertButtons.add(new MyButton("Group Settings", "Settings affecting all students in group.", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {
                    if (gim.getStudentCount() == 0) {
                        CmMessageBox.showAlert("There are no students assigned to this group.");
                        return;
                    }
                    new GroupManagerGlobalSettings(adminModel, gim);
                }
            }
        }));

        vertButtons.add(new MyButton("Assign Students", "Add or remove students from selected group.", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {

                    // TODO: adminId == 0 is used to indicate a "global/default"
                    // group - should use an attribute instead
                    if (gim.getAdminId() == 0)
                        gim.setAdminId(adminModel.getUid());

                    new ManageGroupsAssignStudents(adminModel, gim, new CmAsyncRequestImplDefault() {
                        @Override
                        public void requestComplete(String requestData) {
                            readRpcData(adminModel.getUid(), false);
                            CmAdminDataReader.getInstance().fireRefreshData();
                        }
                    });
                }
            }
        }));

        // deactivating for now...
        if (false)
        vertButtons.add(new MyButton("CCSS Coverage", "View CCSS Coverage for selected group.", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                final GroupInfoModel gim = getGroupInfo();
                if (gim != null) {

                    if (gim.getAdminId() == 0)
                        gim.setAdminId(adminModel.getUid());

                    GroupDto groupDto = new GroupDto(gim.getId(), gim.getGroupName(), gim.getDescription(), gim.getAdminId());
                    new CCSSCoverageWindow(null, groupDto);
                }
            }
        }));

        borderLayout.setEastWidget(vertButtons, new BorderLayoutData(200));
    }

    private Grid<GroupInfoModel> defineGrid(final ListStore<GroupInfoModel> store, ColumnModel<GroupInfoModel> cm) {
        final Grid<GroupInfoModel> grid = new Grid<GroupInfoModel>(store, cm);
        grid.setBorders(true);
        grid.getView().setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // grid.getSelectionModel().setFiresEvents(true);
        grid.addRowDoubleClickHandler(new RowDoubleClickHandler() {
            @Override
            public void onRowDoubleClick(RowDoubleClickEvent event) {
                if (grid.getSelectionModel().getSelectedItems().size() > 0) {
                    // do nothing on double click
                }
            }
        });

        //grid.setWidth("200px");
        //grid.setHeight("250px");
        return grid;
    }

    /**
     * Return string that deals with singular/plural of student count
     * 
     */
    private String getCountString(GroupInfoModel gim) {
        return gim.getStudentCount() + " " + (gim.getStudentCount() == 1 ? "student" : "students");
    }

    private ColumnModel<GroupInfoModel> defineColumns() {
        List<ColumnConfig<GroupInfoModel, ?>> cols = new ArrayList<ColumnConfig<GroupInfoModel, ?>>();
        cols.add(new ColumnConfig<GroupInfoModel, String>(__propsGroupInfo.groupName(), 120, "Group"));
        cols.get(cols.size()-1).setMenuDisabled(true);
        ColumnConfig<GroupInfoModel, String> group = (ColumnConfig<GroupInfoModel, String>)cols.get(cols.size()-1);
        group.setCell(new AbstractCell<String>() {
            @Override
            public void render(Context context, String groupName, SafeHtmlBuilder sb) {
                GroupInfoModel gim = _grid.getStore().get(context.getIndex());
                if (gim.isSelfReg())
                    sb.appendHtmlConstant("<span style='color: red'>" + groupName + "</span>" );
                else {
                    sb.appendHtmlConstant(groupName);
                }
            }
        });

        cols.add(new ColumnConfig<GroupInfoModel, Integer>(__propsGroupInfo.studentCount(), 48, "Count"));
        cols.get(cols.size()-1).setMenuDisabled(true);
        cols.get(cols.size()-1).setToolTip(SafeHtmlUtils.fromString("Students in group"));
        //usage.setSortable(true);

        ColumnModel<GroupInfoModel> cm = new ColumnModel<GroupInfoModel>(cols);
        return cm;
    }

    private void readRpcData(final Integer adminId, final boolean closeOnCancel) {

        final GWindow cmw = this;

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
                _grid.getStore().clear();
                _grid.getStore().addAll(result);
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

            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);

                if (caught.getMessage().indexOf("you entered") > 0) {
                    CmMessageBox.showAlert("Problem renaming group", caught.getMessage());
                    RetryActionManager.getInstance().requestComplete(this);
                    return;
                }
                super.onFailure(caught);
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
    
    
    
    
    static public void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                CmAdminModel admin = new CmAdminModel();
                admin.setUid(2);
                new ManageGroupsWindow(admin);
            }
        });
    }


}

/**
 * Provide standard button sizes and configuration
 * 
 * 
 */
class MyButton extends TextButton {
    public MyButton(String name, String tooltip, SelectHandler handler) {
        super(name);
        addStyleName("manage-groups-window-buttons-button");
        setToolTip(tooltip);
        if (handler != null)
            addSelectHandler(handler);
        setWidth("150px");
    }
};

/**
 * Display window showing potential group wide settings.
 * 
 * @author casey
 * 
 */
class GroupManagerGlobalSettings extends GWindow {

    GroupInfoModel gim;
    CheckBox showWorkRequired;
    CheckBox limitGames;
    CheckBox stopAtProgramEnd;
    CheckBox disableCalcAlways;
    CheckBox disableCalcQuizzes;
    CheckBox disableSearch;
    CheckBox isNoPublicWebLinks;
    ComboBox <PassPercent> passCombo;

    CmAdminModel cm;

    public GroupManagerGlobalSettings(CmAdminModel cm, GroupInfoModel gim) {
        super(false);
        
        this.cm = cm;
        this.gim = gim;
        setHeadingText("Group Settings for '" + gim.getGroupName() + "'");
        setPixelSize(440, 450);
        drawGui();
        setModal(true);
        setResizable(false);
        setVisible(true);
    }

    private void drawGui() {
        FramedPanel framedPanel = new FramedPanel();
        framedPanel.getHeader().setVisible(false);
        
        VerticalLayoutContainer vertMain = new VerticalLayoutContainer();
        
        MyFieldSet fs = new MyFieldSet("Group Settings", 403);
        
        disableSearch = new CheckBox();
        disableSearch.setBoxLabel("");
        fs.addThing(new MyFieldLabel(disableSearch,"Disable lesson search", 290));
        

        showWorkRequired = new CheckBox();
        showWorkRequired.setBoxLabel("");
        showWorkRequired.setToolTip("If checked, the whiteboard will be shown with each problem.");
        fs.addThing(new MyFieldLabel(showWorkRequired, "Require Show Work", 290));

        limitGames = new CheckBox();
        limitGames.setToolTip("If checked, then no Games can be played.");
        limitGames.setBoxLabel("");
        fs.addThing(new MyFieldLabel(limitGames, "Disallow Games", 290));

        stopAtProgramEnd = new CheckBox();
        stopAtProgramEnd.setBoxLabel("");
        stopAtProgramEnd.setToolTip("If checked, the student's program will stop when complete.");
        fs.addThing(new MyFieldLabel(stopAtProgramEnd, "Stop at End of Program", 290));

        disableCalcAlways = new CheckBox();
        disableCalcAlways.setBoxLabel("");
        fs.addThing(new MyFieldLabel(disableCalcAlways, "Disable whiteboard calculator always", 290));

        disableCalcQuizzes = new CheckBox();
        disableCalcQuizzes.setBoxLabel("");
        fs.addThing(new MyFieldLabel(disableCalcQuizzes,"Disable whiteboard calculator for quizzes", 290));
        

        isNoPublicWebLinks = new CheckBox();
        isNoPublicWebLinks.setBoxLabel("");
        fs.addThing(new MyFieldLabel(isNoPublicWebLinks,"Disallow All School's Web Links", 290));

        
        passCombo = new PassPercentCombo(false);
        passCombo.enable();
        passCombo.setEmptyText("---");
        passCombo.setWidth(55);
        passCombo.setAllowBlank(true);
		fs.addThing(new MyFieldLabel(passCombo,  "Pass Percent", 290));
        
        vertMain.add(fs);
        
        HTML html = new HTML("<p style='font-style: italcs;'>Changes apply to all students in the group '" + gim.getGroupName() + "'.</p>");
        MyFieldSet fsInfo = new MyFieldSet("Information");
        fsInfo.addThing(html);

        vertMain.add(fsInfo);
        
        framedPanel.setWidget(vertMain);
        setWidget(framedPanel);

        TextButton apply = new TextButton("Apply Changes");
        apply.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                applyChanges();
            }
        });
        addButton(apply);
        
        TextButton close = new TextButton("Close");
        close.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
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
                GroupManagerAction action = new GroupManagerAction(GroupManagerAction.ActionType.GROUP_PROPERTY_SET, cm.getUid());
                setAction(action);
                action.setGroupId(gim.getId());
                action.setShowWorkRequired(showWorkRequired.getValue());
                action.setDisallowTutoring(false);
                action.setStopAtProgramEnd(stopAtProgramEnd.getValue());
                action.setLimitGames(limitGames.getValue());
                action.setDisableCalcAlways(disableCalcAlways.getValue());
                action.setDisableCalcQuizzes(disableCalcQuizzes.getValue());
                action.setNoPublicWebLinks(isNoPublicWebLinks.getValue());
                action.setDisableSearch(disableSearch.getValue());
                PassPercent passPercent = passCombo.getValue();
                if (passPercent != null) {
                	String percentStr = passPercent.getPercent();
                	int percent = Integer.parseInt(percentStr.substring(0, percentStr.length() - 1));
                    action.setPassPercent(percent);
                }
                else {
                    action.setPassPercent(null);
                }
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
