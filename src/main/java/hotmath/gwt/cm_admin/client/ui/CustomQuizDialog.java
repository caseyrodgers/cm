package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.CustomProgramAddQuizDialog.Callback;
import hotmath.gwt.cm_admin.client.ui.list.ListCustomQuiz;
import hotmath.gwt.cm_core.client.CmCore;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_core.client.util.CmBusyManager;
import hotmath.gwt.cm_core.client.util.GwtTester;
import hotmath.gwt.cm_core.client.util.GwtTester.TestWidget;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageForCustomQuizWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.IntValueHolder;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.ArchiveCustomQuizAction;
import hotmath.gwt.shared.client.rpc.action.CustomQuizUsageCountAction;
import hotmath.gwt.shared.client.rpc.action.DeleteCustomQuizAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.TabItemConfig;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.menu.MenuItem;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

/**
 * Provides lists of available custom programs and quizzes
 * 
 * @author casey
 * 
 */
public class CustomQuizDialog extends GWindow {

    CmAdminModel adminModel;

    CustomQuizDialog _instance;

    ListView<CustomQuizDef, String> _listViewCq;
    boolean _isDebug;

    boolean _includeArchivedQuizzes = false;

    CheckBox _includeArchivedChkBox;

    // MyCheckBoxGroup _includeArchivedChkBoxGrp;

    public CustomQuizDialog(CmAdminModel adminModel) {

        super(false);
        _instance = this;
        this.adminModel = adminModel;
        setStyleName("custom-prescription-dialog");
        setHeadingText("Custom Quiz Definitions");

        setModal(true);
        setPixelSize(400, 420);

        _isDebug = CmCore.isDebug() == true;

        buildGui();
        loadCustomQuizDefinitions();
        setVisible(true);
    }

    ListView<CustomProgramModel, String> _custPrograms;

    BorderLayoutContainer _main = new BorderLayoutContainer();

    private void buildGui() {

        setWidget(_main);

        _listViewCq = new ListCustomQuiz(new ListCustomQuiz.CallbackOnDoubleClick() {
            @Override
            public void doubleClicked(CustomQuizDef modelClicked) {
                editCustomQuiz(false);
            }
        });
        //
        _listViewCq.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        // _listViewCq.aaddListener(Events.DoubleClick, new
        // Listener<BaseEvent>() {
        // public void handleEvent(BaseEvent be) {
        // editCustomQuiz(false);
        // }
        // });

        TabItemConfig tabCustomQuizzes = new TabItemConfig("Custom Quizzes", false);

        tabCustomQuizzes.setEnabled(true);

        loadCustomQuizDefinitions();
        //ncludeArchivedChkBox.setValue(_includeArchivedQuizzes);

        _main.setCenterWidget(_listViewCq);

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
                editCustomQuiz(true);

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

                infoForQuiz();

            }
        }, "Get information about selected custom item."));

        tb.add(new MyButtonWithTooltip("CCSS", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

                displayCCSSForQuiz();

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
        _includeArchivedChkBox.setValue(_includeArchivedQuizzes);
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
        _includeArchivedQuizzes = !_includeArchivedChkBox.getValue();
        loadCustomQuizDefinitions();

    }

    private void addNewCustom() {

        addNewCustomQuiz();

    }

    private void editCustom() {

        editCustomQuiz(false);

    }

    private void editCustomQuiz(boolean asCopy) {
        CustomQuizDef sel1 = null;
        if (asCopy) {
            sel1 = _listViewCq.getSelectionModel().getSelectedItem();
            if (sel1 == null) {
                CmMessageBox.showAlert("Select a custom quiz first");
                return;
            }
        }

        CustomQuizDef quiz = _listViewCq.getSelectionModel().getSelectedItem();
        CustomQuizDef def = new CustomQuizDef(quiz.getQuizId(), quiz.getQuizName(), adminModel.getUid(),
                quiz.isAnswersViewable(), quiz.isInUse(), quiz.isArchived(), quiz.getArchiveDate());
        new CustomProgramAddQuizDialog(adminModel.getUid(), new Callback() {
            @Override
            public void quizCreated() {
                loadCustomQuizDefinitions();
            }
        }, def, asCopy);
    }

    public void addNewCustomQuiz() {
        new CustomProgramAddQuizDialog(adminModel.getUid(), new Callback() {
            @Override
            public void quizCreated() {
                loadCustomQuizDefinitions();
            }
        }, null, false);
    }

    private void infoForQuiz() {
        final CustomQuizDef sel = _listViewCq.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CmMessageBox.showAlert("Select a custom program quiz");
            return;
        }
        new CustomQuizInfoSubDialog(sel).setVisible(true);
    }

    private void displayCCSSForQuiz() {
        final CustomQuizDef sel = _listViewCq.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CmMessageBox.showAlert("Select a custom program quiz");
            return;
        }
        this.hide();
        new CCSSCoverageForCustomQuizWindow(sel, new CallbackOnComplete() {
            @Override
            public void isComplete() {
                _instance.setVisible(true);
            }
        });
    }

    private void deleteProgram() {
        deleteCustomQuiz();
    }

    private void deleteCustomQuiz() {
        CustomQuizDef quiz = _listViewCq.getSelectionModel().getSelectedItem();

        if (quiz.isArchived() == true) {
            CmMessageBox.showAlert("Custom Quiz is archived and cannot be deleted.");
            return;
        }
        deleteCustomQuiz(quiz);
    }

    private void deleteCustomQuiz(final CustomQuizDef def) {

        new RetryAction<IntValueHolder>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true, false);
                CustomQuizDef quiz = _listViewCq.getSelectionModel().getSelectedItem();
                CustomQuizUsageCountAction action = new CustomQuizUsageCountAction(quiz.getQuizId());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(IntValueHolder count) {
                CmBusyManager.setBusy(false);

                if (count.getValue() > 0) {
                    archiveCustomQuizDo(def);
                } else {
                    deleteCustomQuizDo(def);
                }
            }
        }.register();
    }

    private void deleteCustomQuizDo(final CustomQuizDef def) {

        CmMessageBox.confirm("Delete Custom Quiz?", "Are you sure you want to delete custom quiz '" + def.getQuizName()
                + "'?", new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if (yesNo) {
                    new RetryAction<RpcData>() {
                        @Override
                        public void attempt() {
                            CmBusyManager.setBusy(true);
                            DeleteCustomQuizAction action = new DeleteCustomQuizAction(adminModel.getUid(), def
                                    .getQuizId());
                            setAction(action);
                            CmRpcCore.getCmService().execute(action, this);
                        }

                        @Override
                        public void oncapture(RpcData result) {
                            CmBusyManager.setBusy(false);

                            if (result.getDataAsString("status").equals("OK")) {
                                loadCustomQuizDefinitions();
                            }
                        }
                    }.register();
                }
            }
        });

    }

    private void archiveCustomQuizDo(final CustomQuizDef def) {

        CmMessageBox.confirm("Archive Custom Quiz?",
                "Are you sure you want to archive custom quiz '" + def.getQuizName() + "'?", new ConfirmCallback() {
                    @Override
                    public void confirmed(boolean yesNo) {
                        if (yesNo) {
                            new RetryAction<RpcData>() {
                                @Override
                                public void attempt() {
                                    CmBusyManager.setBusy(true);
                                    ArchiveCustomQuizAction action = new ArchiveCustomQuizAction(adminModel.getUid(),
                                            def.getQuizId());
                                    setAction(action);
                                    CmRpcCore.getCmService().execute(action, this);
                                }

                                @Override
                                public void oncapture(RpcData result) {
                                    CmBusyManager.setBusy(false);

                                    if (result.getDataAsString("status").equals("OK")) {
                                        loadCustomQuizDefinitions();
                                    }
                                }
                            }.register();
                        }
                    }
                });

    }

    private void loadCustomQuizDefinitions() {

        new RetryAction<CmList<CustomQuizDef>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetCustProgQuizDefsAction action = new GetCustProgQuizDefsAction(adminModel.getUid());
                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomQuizDef> defs) {
                CmBusyManager.setBusy(false);

                List<CustomQuizDef> gmodels = new ArrayList<CustomQuizDef>();
                for (int i = 0, t = defs.size(); i < t; i++) {
                    CustomQuizDef def = defs.get(i);
                    if (def.isArchived() == true && _includeArchivedQuizzes == false)
                        continue;
                    gmodels.add(def);
                }
                _listViewCq.getStore().clear();
                _listViewCq.getStore().addAll(gmodels);
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
                new CustomQuizDialog(cam);
            }
        });
    }
}
