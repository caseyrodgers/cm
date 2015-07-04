package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.list.ListCustomProgram;
import hotmath.gwt.cm_admin.client.ui.list.ListCustomProgram.CallbackOnDoubleClick;
import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageForCustomProgramWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.IntValueHolder;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction.ActionType;
import hotmath.gwt.shared.client.rpc.action.CustomProgramUsageCountAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * Provides lists of available custom programs and quizzes
 * 
 * @author casey
 * 
 */
public class CustomProgramDialog extends GWindow {

    CmAdminModel adminModel;

    CustomProgramDialog _instance;

    ListView<CustomProgramModel, String> _listViewCp;
    ListView<CustomQuizDef, String> _listViewCq;
    boolean _isDebug;
    boolean _includeArchivedPrograms = false;

    CheckBox _includeArchivedChkBox;

    // MyCheckBoxGroup _includeArchivedChkBoxGrp;

    public CustomProgramDialog(CmAdminModel adminModel) {

        super(false);
        _instance = this;
        this.adminModel = adminModel;
        setStyleName("custom-prescription-dialog");
        setHeadingText("Custom Program Definitions");

        setModal(true);
        setPixelSize(400, 420);

        _isDebug = CmCore.isDebug() == true;

        buildGui();
        getCustomProgramDefinitions();
        setVisible(true);
    }

    ListView<CustomProgramModel, String> _custPrograms;

    BorderLayoutContainer _main = new BorderLayoutContainer();

    private void buildGui() {

        setWidget(_main);

        _listViewCp = new ListCustomProgram(new CallbackOnDoubleClick() {
            @Override
            public void doubleClicked(CustomProgramModel modelClicked) {
                editCustomProgram(false);
            }
        });


        if (CmCore.isDebug() == true) {
            Menu contextMenu = new Menu();
            contextMenu.add(new MenuItem("Export", new SelectionHandler<MenuItem>() {
                @Override
                public void onSelection(SelectionEvent<MenuItem> event) {
                    doExportSelectedCustomProgram();
                }
            }));
            _listViewCp.setContextMenu(contextMenu);
        }

        // tabCustomProgram.add(_listViewCp);
        _main.setCenterWidget(_listViewCp);
        
        // _includeArchivedChkBox.setValue(_includeArchivedPrograms);

        ToolBar tb = new ToolBar();

        tb.add(new MyButtonWithTooltip("New", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                addNewCustom();
            }
        }, "Create a new blank custom item."));

        tb.add(new MyButtonWithTooltip("Copy", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {     
                    editCustomProgram(true);
            }
        }, "Create new custom item by copying an existing one."));

        tb.add(new MyButtonWithTooltip("Edit", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                editCustom();
            }
        }, "Edit the selected custom item."));

        tb.add(new MyButtonWithTooltip("Delete/Archive", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                deleteProgram();
            }
        }, "Delete or archive selected custom item"));

        tb.add(new MyButtonWithTooltip("Info", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                infoForProgram();
            }
        }, "Get information about selected custom item."));

        tb.add(new MyButtonWithTooltip("CCSS", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                displayCCSSForProgram();
            }
        }, "Display CCSS coverage information for selected custom item."));

        tb.add(new MyButtonWithTooltip("Help", new SelectHandler() {

            @Override
            public void onSelect(SelectEvent event) {
                new CustomProgramHelpWindow();
            }
        }, "Get information about selected custom item."));

        _main.setNorthWidget(tb, new BorderLayoutData(35));
        addCloseButton();

        _includeArchivedChkBox = new CheckBox();
        _includeArchivedChkBox.setId("include-archived");
        _includeArchivedChkBox.setToolTip("include archived items in list");
        _includeArchivedChkBox.setStyleName("custom-quiz-check-box");
        _includeArchivedChkBox.setBoxLabel("Include archived custom items");
        _includeArchivedChkBox.setValue(_includeArchivedPrograms);
        // _includeArchivedChkBox.setFireChangeEventOnSetValue(true);
        // _includeArchivedChkBox.setLabelSeparator("");

        _includeArchivedChkBox.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                archivedButtonChecked();
            }
        }, ClickEvent.getType());

        _main.setSouthWidget(_includeArchivedChkBox, new BorderLayoutData(30));
    }

    protected void archivedButtonChecked() {
        getCustomProgramDefinitions();
    }

    protected void doExportSelectedCustomProgram() {
        final CustomProgramModel sel1 = _listViewCp.getSelectionModel().getSelectedItem();
        if (sel1 == null) {
            CmMessageBox.showAlert("No selected custom program");
            return;
        }
        String value = Window.prompt("Enter admin id to copy selected program into", "0");
        if (value == null) {
            return;
        }
        final int uidToCopyTo = Integer.parseInt(value);

        new RetryAction<CmList<CustomLessonModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramAction action = new CustomProgramAction(CustomProgramAction.ActionType.COPY);
                action.setDestAdminId(uidToCopyTo);
                action.setProgramId(sel1.getProgramId());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomLessonModel> result) {
                CmBusyManager.setBusy(false);

                CmMessageBox.showAlert("Custom program was copied");
            }
        }.register();

    }

    private void addNewCustom() {
        addNewCustomProgram();
    }

    private void editCustom() {
        editCustomProgram(false);
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
        final CustomProgramModel sel = _listViewCp.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CmMessageBox.showAlert("Select a custom program first");
            return;
        }

        new CustomProgramInfoSubDialog(sel).setVisible(true);
    }

    private void displayCCSSForProgram() {
        final CustomProgramModel sel = _listViewCp.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CmMessageBox.showAlert("Select a custom program");
            return;
        }
        this.hide();
        new CCSSCoverageForCustomProgramWindow(sel, new CallbackOnComplete() {
            @Override
            public void isComplete() {
                _instance.setVisible(true);
            }
        });
    }

    private void editCustomProgram(boolean asCopy) {
        final CustomProgramModel sel = _listViewCp.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CmMessageBox.showAlert("Select a custom program first");
            return;
        }
        editProgram(sel, asCopy);
    }

    protected void editProgram(final CustomProgramModel sel, boolean asCopy) {
        final String oldProgramName = sel.getProgramName();
        new CustomProgramDesignerDialog(adminModel, new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                getCustomProgramDefinitions();

                if (!oldProgramName.equals(sel.getProgramName()))
                    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_REFRESH_STUDENT_DATA));
            }
        }, sel, asCopy);
    }

    private void deleteProgram() {
        deleteCustomProgram();
    }



    private void deleteCustomProgram() {
        final CustomProgramModel sel = _listViewCp.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CmMessageBox.showAlert("Select a custom program first");
            return;
        }

        if (sel.getIsArchived()) {
            CmMessageBox.showAlert("This custom program has been archived and cannot be deleted.");
            return;
        }

        if (!_isDebug && sel.getIsTemplate()) {
            CmMessageBox.showAlert("This program is built-in and cannot be deleted.");
            return;
        }

        if (sel.getAssignedCount() < 1) {

            new RetryAction<IntValueHolder>() {
                @Override
                public void attempt() {
                    CmBusyManager.setBusy(true, false);
                    CustomProgramUsageCountAction action = new CustomProgramUsageCountAction(sel.getProgramId());
                    setAction(action);
                    CmRpcCore.getCmService().execute(action, this);
                }

                @Override
                public void oncapture(IntValueHolder count) {
                    CmBusyManager.setBusy(false);

                    if (count.getValue() > 0) {
                        archiveCustomProgram(sel);
                    } else {
                        deleteCustomProgram(sel);
                    }
                }
            }.register();
        } else {
            archiveCustomProgram(sel);
        }
    }

    private void deleteCustomProgram(final CustomProgramModel program) {
        CmMessageBox.confirm("Delete Custom Program",
                "Are you sure you want to delete custom program '" + program.getProgramName() + "'?",
                new ConfirmCallback() {

                    @Override
                    public void confirmed(boolean yesNo) {
                        if (yesNo) {
                            deleteCustomProgramDo(program);
                        }
                    }
                });
    }

    private void deleteCustomProgramDo(final CustomProgramModel program) {

        new RetryAction<CmList<CustomProgramModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramDefinitionAction action = new CustomProgramDefinitionAction(ActionType.DELETE,
                        adminModel.getUid());
                action.setProgramId(program.getProgramId());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomProgramModel> value) {
                CmBusyManager.setBusy(false);
                _listViewCp.getStore().remove(program);
            }
        }.register();
    }

    private void archiveCustomProgram(final CustomProgramModel program) {
        CmMessageBox.confirm("Archive Custom Program",
                "Are you sure you want to archive custom program '" + program.getProgramName() + "'?",
                new ConfirmCallback() {

                    @Override
                    public void confirmed(boolean yesNo) {
                        if (yesNo) {
                            archiveCustomProgramDo(program);
                        }
                    }
                });
    }

    private void archiveCustomProgramDo(final CustomProgramModel program) {

        new RetryAction<CmList<CustomProgramModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramDefinitionAction action = new CustomProgramDefinitionAction(ActionType.ARCHIVE,
                        adminModel.getUid());
                action.setProgramId(program.getProgramId());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomProgramModel> value) {
                CmBusyManager.setBusy(false);
                // _listViewCp.getStore().remove(program);
                int index = _listViewCp.getStore().indexOf(program);
                _listViewCp.getStore().remove(program);
                // _listViewCp.getStore().addAll(index, value);
            }
        }.register();
    }

  
    private void getCustomProgramDefinitions() {
        new RetryAction<CmList<CustomProgramModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramDefinitionAction action = new CustomProgramDefinitionAction(ActionType.GET,
                        adminModel.getUid());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomProgramModel> programs) {
                CmBusyManager.setBusy(false);

                /**
                 * collect templates and non templates in separate lists
                 * 
                 */
                List<CustomProgramModel> templates = new ArrayList<CustomProgramModel>();
                List<CustomProgramModel> nonTemplates = new ArrayList<CustomProgramModel>();
                List<CustomProgramModel> selected = new ArrayList<CustomProgramModel>();
                for (int i = 0, t = programs.size(); i < t; i++) {
                    CustomProgramModel program = programs.get(i);
                    if (program.getIsArchived() == true && _includeArchivedChkBox.getValue() == false) {
                        continue;
                    }
                    if (program.getIsTemplate())
                        templates.add(program);
                    else
                        nonTemplates.add(program);
                    selected.add(program);
                }
                _listViewCp.getStore().clear();
                _listViewCp.getStore().addAll(selected);
            }
        }.register();
    }

    static class MyButtonWithTooltip extends TextButton {
        public MyButtonWithTooltip(String name, SelectHandler listener, String toolTip) {
            super(name, listener);
            setToolTip(toolTip);
        }
    }

    static class MyMenuItem extends MenuItem {
        public MyMenuItem(CustomProgramModel program, String tip, SelectionHandler<MenuItem> listener) {
            this(program.getProgramName(), tip, listener);
            setData("program", program);
        }

        public MyMenuItem(String text, String tip, SelectionHandler<MenuItem> listener) {
            super(text, listener);
            setToolTip(tip);
        }
    }

    static public void startTest() {
        new GwtTester(new TestWidget() {
            @Override
            public void runTest() {
                CmAdminModel cam = new CmAdminModel();
                cam.setUid(2);
                new CustomProgramDialog(cam);
            }
        });
    }
}
