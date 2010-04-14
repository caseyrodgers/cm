package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction.ActionType;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
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
        _listView.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                editProgram(false);
            }
        });

        add(_listView, new BorderLayoutData(LayoutRegion.CENTER));

        ToolBar tb = new ToolBar();
        
        tb.add(new MyButtonWithTooltip("New", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                addNewCustomProgram();
            }
        }, "Create a new blank custom program."));
        
        tb.add(new MyButtonWithTooltip("Copy", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                editProgram(true);
            }
        }, "Create new custom program by copying an existing one."));

        tb.add(new MyButtonWithTooltip("Edit", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                editProgram(false);
            }
        }, "Edit the selected custom program."));

        tb.add(new MyButtonWithTooltip("Delete", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                deleteProgram();
            }
        }, "Delete selected custom program"));
        tb.add(new MyButtonWithTooltip("Info", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                infoForProgram();
            }
        }, "Get information about selected custom program."));

        add(tb, new BorderLayoutData(LayoutRegion.NORTH, 35));
        addCloseButton();
    }

    public void addNewCustomProgram() {
        new CustomProgramDesignerDialog(adminModel, new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                getCustomProgramDefinitions();
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }
        });        
    }
    
    private void infoForProgram() {
        final CustomProgramModel sel = _listView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }
        
        new CustomProgramInfoSubDialog(sel).setVisible(true);
    }
    private void editProgram(boolean asCopy) {
        final CustomProgramModel sel = _listView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }
        editProgram(sel,asCopy);
    }
    
    protected void editProgram(final CustomProgramModel sel,boolean asCopy) {
        final String oldProgramName = sel.getProgramName();
        new CustomProgramDesignerDialog(adminModel, new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                getCustomProgramDefinitions();
                
                if(!oldProgramName.equals(sel.getProgramName()))
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }
        }, sel,asCopy);
    }

    private void deleteProgram() {
        final CustomProgramModel sel = _listView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }
        
        if(sel.getAssignedCount() > 0) {
            CatchupMathTools.showAlert("This program cannot be deleted because it is currently in use.");
            return;
        }
        
        if(sel.getIsTemplate()) {
            CatchupMathTools.showAlert("This program is a system template and cannot be deleted.");
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
            public void oncapture(CmList<CustomProgramModel> programs) {
                CmBusyManager.setBusy(false);
                
                /** collect templates and non templates in separate lists
                 * 
                 */
                List<CustomProgramModel> templates = new ArrayList<CustomProgramModel>();
                List<CustomProgramModel> nonTemplates = new ArrayList<CustomProgramModel>();
                for(int i=0,t=programs.size();i<t;i++) {
                    CustomProgramModel program = programs.get(i);
                    if(program.getIsTemplate())
                        templates.add(program);
                    else
                        nonTemplates.add(program);
                }
                _listView.getStore().removeAll();
                _listView.getStore().add(programs);
            }
        }.register();
    }

    static class MyButtonWithTooltip extends Button {
        public MyButtonWithTooltip(String name, SelectionListener<ButtonEvent> listener, String toolTip) {
            super(name, listener);
            setToolTip(toolTip);
        }
    }
    
    static class MyMenuItem extends MenuItem {
        public MyMenuItem(CustomProgramModel program,String tip, SelectionListener<MenuEvent> listener) {
            this(program.getProgramName(),tip,listener);
            setData("program", program);
        }
        public MyMenuItem(String text,String tip, SelectionListener<MenuEvent> listener) {
            super(text,listener);
            setToolTip(tip);
        }        
    }
}
