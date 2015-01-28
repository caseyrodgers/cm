package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_core.client.model.GroupCopyModel;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.rpc.ImportAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetGroupAssignmentsAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.info.Info;

public class AssignmentCopyDialog extends GWindow{
    
    static AssignmentCopyDialog __instance;
    public static AssignmentCopyDialog getInstance() {
        if(__instance == null) {
            __instance = new AssignmentCopyDialog();
        }
        return __instance;
    }

    Grid<GroupCopyModel> _grid;

    GridProperties _props = GWT.create(GridProperties.class);
    GroupDto currentGroup;

    private AssignmentCopyDialog() {
        super(false);
        
        setHeadingText("Import Assignments");   
        
        final ListStore<GroupCopyModel> store = new ListStore<GroupCopyModel>(_props.id());

        _grid = defineGrid(store, defineColumns());
        
        setWidget(_grid);
        
        addButton(createImportButton());
        addCloseButton();
        
        getServerData();
        
        setVisible(true);
    }
    
    private AssignmentCopyDialog(GroupDto group) {
        this();
        this.currentGroup = group;
    }

    private Widget createImportButton() {
        TextButton b = new TextButton("Import", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                final ConfirmMessageBox cm = new ConfirmMessageBox("Import Assignment", "Are you sure you want to import this assignment?");
                cm.addDialogHideHandler(new DialogHideHandler() {
                    @Override
                    public void onDialogHide(DialogHideEvent event) {
                        if(event.getHideButton() == PredefinedButton.YES) {
                            importSelectedAssignment();
                        }
                    }
                });
                cm.setWidth(300);
                cm.show();                
            }
        });
        return b;
   }

    protected void importSelectedAssignment() {
        final GroupCopyModel groupModel = _grid.getSelectionModel().getSelectedItem();
        if(groupModel == null) {
            CmMessageBox.showAlert("Select an Assignment to import");
            return;
        }
        
        
        CmBusyManager.setBusy(true);
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                ImportAssignmentAction action = new  ImportAssignmentAction(UserInfoBase.getInstance().getUid(),currentGroup.getGroupId(),groupModel.getAssignmentId());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }
            
            public void oncapture(RpcData result) {
                CmBusyManager.setBusy(false);
                Info.display("Information", "Assignent was imported");
                hide();
                CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.ASSIGNMENTS));
            }
        }.register();
    }

    private void getServerData() {
        _grid.setLoadMask(true);
        new RetryAction<CmList<GroupCopyModel>>() {
            @Override
            public void attempt() {
                GetGroupAssignmentsAction action = new  GetGroupAssignmentsAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }
            
            public void oncapture(CmList<GroupCopyModel> value) {
                _grid.setLoadMask(false);
                _grid.getStore().addAll(value);
            }
        }.register();
    }

    private Grid<GroupCopyModel> defineGrid(final ListStore<GroupCopyModel> store, ColumnModel<GroupCopyModel> cm) {
        final Grid<GroupCopyModel> grid = new Grid<GroupCopyModel>(store, cm);
        grid.getView().setStripeRows(true);
        grid.getView().setColumnLines(true);        
        grid.setLoadMask(true);
        
        grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                importSelectedAssignment();
            }
        });
        
        return grid;
    }

    private ColumnModel<GroupCopyModel> defineColumns() {
        List<ColumnConfig<GroupCopyModel, ?>> cols = new ArrayList<ColumnConfig<GroupCopyModel, ?>>();
        cols.add(new ColumnConfig<GroupCopyModel, String>(_props.group(), 140, "Group"));
        cols.add(new ColumnConfig<GroupCopyModel, String>(_props.assignment(), 300, "Assignment"));
        return new ColumnModel<GroupCopyModel>(cols);
    }

    public void showAndSetCurrentGroup(GroupDto currentGroup) {
        this.currentGroup = currentGroup;
        setVisible(true);
    }

}

interface GridProperties extends PropertyAccess<Integer> {
    @Path("assignmentId")
    ModelKeyProvider<GroupCopyModel> id();
    ValueProvider<GroupCopyModel, String> group();
    ValueProvider<GroupCopyModel, String> assignment();
}
