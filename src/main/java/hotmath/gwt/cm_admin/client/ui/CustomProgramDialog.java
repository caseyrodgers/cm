package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction.ActionType;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class CustomProgramDialog extends CmWindow {

    CmAdminModel adminModel;

    ListView<CustomProgramModel> _listView;

    public CustomProgramDialog(CmAdminModel adminModel) {
        this.adminModel = adminModel;
        setStyleName("custom-prescription-dialog");
        setHeading("Catchup Math Custom Program Definitions");

        setModal(true);
        setSize(350, 280);

        buildGui();
        getCustomProgramDefinitions();
        setVisible(true);
    }

    ListView<CustomProgramModel> _custPrograms;

    private void buildGui() {
        setLayout(new BorderLayout());

        _listView = new ListView<CustomProgramModel>();
        _listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ListStore<CustomProgramModel> store = new ListStore<CustomProgramModel>();
        _listView.setStore(store);
        _listView.setDisplayProperty("programName");

        add(_listView, new BorderLayoutData(LayoutRegion.CENTER));

        ToolBar tb = new ToolBar();
        tb.add(new MyButtonWithTooltip("New", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new CustomProgramDesignerDialog(adminModel, new CmAsyncRequestImplDefault() {
                    @Override
                    public void requestComplete(String requestData) {
                        getCustomProgramDefinitions();
                    }
                });
            }
        }, "Create new custom program"));

        tb.add(new MyButtonWithTooltip("Edit", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                editProgram();
            }
        }, "Edit selected custom program"));

        tb.add(new MyButtonWithTooltip("Delete", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                deleteProgram();
            }
        }, "Delete selected custom program"));
        add(tb, new BorderLayoutData(LayoutRegion.NORTH, 35));
        addCloseButton();
    }

    private void editProgram() {
        CustomProgramModel sel = _listView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }

        new CustomProgramDesignerDialog(adminModel, new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                getCustomProgramDefinitions();
            }
        }, sel);
    }

    private void deleteProgram() {
        final CustomProgramModel sel = _listView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }

        MessageBox.confirm("Delete Custom Program", "Are you sure you want to delete custom program '"
                + sel.getProgramName() + "'?", new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                String btnText = be.getButtonClicked().getText();
                if (btnText.equalsIgnoreCase("yes")) {
                    deleteCustomProgram(sel);
                }
            }
        });
    }

    private void deleteCustomProgram(final CustomProgramModel program) {

        new RetryAction<CmList<CustomProgramModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramDefinitionAction action = new CustomProgramDefinitionAction(ActionType.DELETE, adminModel
                        .getId());
                action.setProgramId(program.getProgramId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomProgramModel> value) {
                CmBusyManager.setBusy(false);
                _listView.getStore().remove(program);
            }
        }.register();
    }

    private void getCustomProgramDefinitions() {
        new RetryAction<CmList<CustomProgramModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramDefinitionAction action = new CustomProgramDefinitionAction(ActionType.GET, adminModel
                        .getId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomProgramModel> value) {
                CmBusyManager.setBusy(false);
                _listView.getStore().removeAll();
                _listView.getStore().add(value);
            }
        }.register();
    }

    static class MyButtonWithTooltip extends Button {
        public MyButtonWithTooltip(String name, SelectionListener<ButtonEvent> listener, String toolTip) {
            super(name, listener);
            setToolTip(toolTip);
        }
    }

}
