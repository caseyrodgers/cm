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
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

public class CustomProgramDialog extends CmWindow {

    CmAdminModel adminModel;

    ListView<CustomProgramModel> _listView;
    NewProgramButtonWithTooltip _newProgramButton;
    
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
                editProgram();
            }
        });

        add(_listView, new BorderLayoutData(LayoutRegion.CENTER));

        ToolBar tb = new ToolBar();
        
        _newProgramButton = new NewProgramButtonWithTooltip(this,"Create New", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                addNewCustomProgram();
            }
        }, "Create new custom program");
        tb.add(_newProgramButton);

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

    public void addNewCustomProgram() {
        new CustomProgramDesignerDialog(adminModel, new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                getCustomProgramDefinitions();
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }
        });        
    }
    
    private void editProgram() {
        final CustomProgramModel sel = _listView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }
        
        editProgram(sel);
    }
    
    protected void editProgram(final CustomProgramModel sel) {
        final String oldProgramName = sel.getProgramName();
        new CustomProgramDesignerDialog(adminModel, new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                getCustomProgramDefinitions();
                
                if(!oldProgramName.equals(sel.getProgramName()))
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }
        }, sel);
    }

    private void deleteProgram() {
        final CustomProgramModel sel = _listView.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }
        
        if(sel.getAssignedCount() > 0) {
            CatchupMathTools.showAlert("This program cannot be deleted because it is currently assigned to " + 
                    (sel.getAssignedCount()==1?"1 student":sel.getAssignedCount() + " students") + ".");
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
            public void oncapture(CmList<CustomProgramModel> value) {
                CmBusyManager.setBusy(false);
                
                /** collect templates and non templates in separate lists
                 * 
                 */
                List<CustomProgramModel> templates = new ArrayList<CustomProgramModel>();
                List<CustomProgramModel> nonTemplates = new ArrayList<CustomProgramModel>();
                for(int i=0,t=value.size();i<t;i++) {
                    CustomProgramModel program = value.get(i);
                    if(program.getIsTemplate())
                        templates.add(program);
                    else
                        nonTemplates.add(program);
                }
                _listView.getStore().removeAll();
                _listView.getStore().add(nonTemplates);
                _newProgramButton.setTemplates(templates);
            }
        }.register();
    }

    static class MyButtonWithTooltip extends Button {
        public MyButtonWithTooltip(String name, SelectionListener<ButtonEvent> listener, String toolTip) {
            super(name, listener);
            setToolTip(toolTip);
        }
    }
    
    static class NewProgramButtonWithTooltip extends Button {
        Menu _availTemplates;
        SelectionListener<MenuEvent> templateListener;
        CustomProgramDialog customProgramDialog;
        
        public NewProgramButtonWithTooltip(CustomProgramDialog cpd, String name, final SelectionListener<ButtonEvent> listener, String toolTip) {
            super(name);
            this.customProgramDialog = cpd;
            Menu menu = new Menu();
            menu.add(new MyMenuItem("Blank Program", "Create a new blank custom program", new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent ce) {
                    listener.componentSelected(null);
                }
            }));
            
            templateListener = new SelectionListener<MenuEvent>() {
                @Override
                public void componentSelected(MenuEvent ce) {
                    int index = ce.getIndex();
                    customProgramDialog.editProgram((CustomProgramModel)ce.getItem().getData("program"));
                }
            };
            MenuItem fromTemplate = new MenuItem("From Template");
            _availTemplates = new Menu();
            fromTemplate.setSubMenu(_availTemplates);
            menu.add(fromTemplate);
            
            setMenu(menu);
            setToolTip(toolTip);
        }
        
        public void setTemplates(List<CustomProgramModel> templates) {
            _availTemplates.removeAll();
            for(int i=0,t=templates.size();i<t;i++) {
                CustomProgramModel cp = templates.get(i);
                _availTemplates.add(new MyMenuItem(cp,"Create new custom program based on this template", templateListener));
            }
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
