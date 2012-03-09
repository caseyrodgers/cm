package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.ui.CustomProgramAddQuizDialog.Callback;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomLessonModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.model.CustomQuizDef;
import hotmath.gwt.shared.client.model.CustomQuizInfoModel;
import hotmath.gwt.shared.client.model.IntValueHolder;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.ArchiveCustomQuizAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction;
import hotmath.gwt.shared.client.rpc.action.CustomProgramDefinitionAction.ActionType;
import hotmath.gwt.shared.client.rpc.action.CustomProgramUsageCountAction;
import hotmath.gwt.shared.client.rpc.action.CustomQuizInfoAction;
import hotmath.gwt.shared.client.rpc.action.CustomQuizUsageCountAction;
import hotmath.gwt.shared.client.rpc.action.DeleteCustomQuizAction;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;

/**
 * Provides lists of available custom programs and quizzes
 * 
 * @author casey
 * 
 */
public class CustomProgramDialog extends CmWindow {

    CmAdminModel adminModel;

    ListView<CustomProgramModel> _listViewCp;
    ListView<CustomLessonModel> _listViewCq;
    boolean _isDebug;
    TabPanel tabPanelType = new TabPanel();
    boolean _includeArchivedPrograms = true;
    boolean _includeArchivedQuizzes = true;
    
    CheckBox _includeArchivedChkBox;
    CheckBoxGroup _includeArchivedChkBoxGrp;

    public CustomProgramDialog(CmAdminModel adminModel) {
        this.adminModel = adminModel;
        setStyleName("custom-prescription-dialog");
        setHeading("Catchup Math Custom Program Definitions");

        setModal(true);
        setSize(400, 420);

        _isDebug = CmShared.getQueryParameter("debug") != null;

        buildGui();
        getCustomProgramDefinitions();
        setVisible(true);
    }

    ListView<CustomProgramModel> _custPrograms;

    private void buildGui() {
        setLayout(new BorderLayout());

        _listViewCp = new ListView<CustomProgramModel>();
        _listViewCp.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ListStore<CustomProgramModel> store = new ListStore<CustomProgramModel>();
        _listViewCp.setStore(store);
        _listViewCp.setDisplayProperty("programName");
        _listViewCp.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                editCustomProgram(false);
            }
        });
        _listViewCp.setTemplate(getTemplateHtmlForPrograms());

        TabItem tabCustomProgram = new TabItem("Custom Programs");
        tabCustomProgram.setLayout(new FitLayout());
        tabCustomProgram.add(_listViewCp);
        tabPanelType.add(tabCustomProgram);

        _listViewCq = new ListView<CustomLessonModel>(new ListStore<CustomLessonModel>());
        _listViewCq.setDisplayProperty("customProgramItem");
        _listViewCq.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _listViewCq.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                editCustomQuiz(false);
            }
        });
        _listViewCq.setTemplate(getTemplateHtmlForQuizzes());

        TabItem tabCustomQuizzes = new TabItem("Custom Quizzes");

        tabCustomQuizzes.setEnabled(true);

        tabCustomQuizzes.setLayout(new FitLayout());
        tabCustomQuizzes.add(_listViewCq);
        tabPanelType.add(tabCustomQuizzes);

        /** lazy load the custom quizzes */
        tabPanelType.addListener(Events.Select, new Listener<BaseEvent>() {
            @Override
            public void handleEvent(BaseEvent be) {
                if (tabPanelType.getSelectedItem().getText().contains("Quiz")) {
                    if (_listViewCq.getStore().getCount() == 0) {
                        loadCustomQuizDefinitions();
                    }
                    _includeArchivedChkBox.setValue(_includeArchivedQuizzes);
                }
                else {
                	_includeArchivedChkBox.setValue(_includeArchivedPrograms);
                }
            }
        });

        add(tabPanelType, new BorderLayoutData(LayoutRegion.CENTER));

        ToolBar tb = new ToolBar();

        tb.add(new MyButtonWithTooltip("New", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                addNewCustom();
            }
        }, "Create a new blank custom item."));

        tb.add(new MyButtonWithTooltip("Copy", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                if (!isCpTabSelected()) {
                    editCustomQuiz(true);
                } else {
                    editCustomProgram(true);
                }
            }
        }, "Create new custom item by copying an existing one."));

        tb.add(new MyButtonWithTooltip("Edit", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                editCustom();
            }
        }, "Edit the selected custom item."));

        tb.add(new MyButtonWithTooltip("Delete/Archive", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                deleteProgram();
            }
        }, "Delete or archive selected custom item"));

        tb.add(new MyButtonWithTooltip("Info", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                if (!isCpTabSelected()) {
                    infoForQuiz();
                } else {
                    infoForProgram();
                }
            }
        }, "Get information about selected custom item."));

        tb.add(new MyButtonWithTooltip("Help", new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                new CustomProgramHelpWindow();
            }
        }, "Get information about selected custom item."));        

        add(tb, new BorderLayoutData(LayoutRegion.NORTH, 35));
        addCloseButton();
        
        _includeArchivedChkBox = new CheckBox();
        _includeArchivedChkBox.setId("include-archived");
        _includeArchivedChkBox.setToolTip("include archived items in list");
        _includeArchivedChkBox.setStyleName("custom-quiz-check-box");
        _includeArchivedChkBox.setBoxLabel("Include archived custom items");
        _includeArchivedChkBox.setLabelSeparator("");
        _includeArchivedChkBox.setValue(_includeArchivedPrograms);
        _includeArchivedChkBox.setFireChangeEventOnSetValue(true);

        _includeArchivedChkBox.addListener(Events.Change, new Listener<ComponentEvent>() {
            public void handleEvent(ComponentEvent be) {
                if (isCpTabSelected()) {
                	_includeArchivedPrograms = _includeArchivedChkBox.getValue();
                	getCustomProgramDefinitions();
                }
                else {
                	_includeArchivedQuizzes = _includeArchivedChkBox.getValue();
                	loadCustomQuizDefinitions();
                }
            }
        });

        _includeArchivedChkBoxGrp = new CheckBoxGroup();
        _includeArchivedChkBoxGrp.setId("include-archiveds");
        _includeArchivedChkBoxGrp.add(_includeArchivedChkBox);
        _includeArchivedChkBoxGrp.setLabelSeparator("");
        _includeArchivedChkBoxGrp.setStyleName("include-archived-check-box-grp");

        add(_includeArchivedChkBoxGrp,new BorderLayoutData(LayoutRegion.SOUTH, 40));
    }

    /**
     * Return true if the Custom Programs tab is selected
     * 
     * @return
     */
    private boolean isCpTabSelected() {
        return tabPanelType.getSelectedItem().getText().contains("Programs");
    }

    private void addNewCustom() {
        if (isCpTabSelected()) {
            addNewCustomProgram();
        } else {
            addNewCustomQuiz();
        }
    }

    private void editCustom() {
        if (isCpTabSelected()) {
            editCustomProgram(false);
        } else {
            editCustomQuiz(false);
        }
    }

    private void editCustomQuiz(boolean asCopy) {
        CustomLessonModel sel1 = null;
        if (asCopy) {
            sel1 = _listViewCq.getSelectionModel().getSelectedItem();
            if (sel1 == null) {
                CatchupMathTools.showAlert("Select a custom quiz first");
                return;
            }
        }

        CustomLessonModel quiz = _listViewCq.getSelectionModel().getSelectedItem();
        CustomQuizDef def = new CustomQuizDef(quiz.getQuizId(), quiz.getQuiz(), adminModel.getId(),
        		quiz.getIsAnswersViewable(), quiz.getIsInUse(), quiz.getIsArchived(), quiz.getArchiveDate());
        new CustomProgramAddQuizDialog(new Callback() {
            @Override
            public void quizCreated() {
                loadCustomQuizDefinitions();
            }
        }, def, asCopy);

    }

    private String getTemplateHtmlForPrograms() {
        String template = "<tpl for=\".\"><div class='x-view-item'><span class='{styleName}'>{programName}</span></div></tpl>";
        return template;
    }

    private String getTemplateHtmlForQuizzes() {
        String template = "<tpl for=\".\"><div class='x-view-item'><span class='{styleName}'>{customProgramItem}</span></div></tpl>";
        return template;
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

    public void addNewCustomQuiz() {
        new CustomProgramAddQuizDialog(new Callback() {
            @Override
            public void quizCreated() {
                loadCustomQuizDefinitions();
            }
        }, null, false);
    }

    private void infoForProgram() {
        final CustomProgramModel sel = _listViewCp.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }

        new CustomProgramInfoSubDialog(sel).setVisible(true);
    }

    private void infoForQuiz() {
        final CustomLessonModel sel = _listViewCq.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program quiz");
            return;
        }

        new CustomQuizInfoSubDialog(sel).setVisible(true);
    }

    private void editCustomProgram(boolean asCopy) {
        final CustomProgramModel sel = _listViewCp.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
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
        if (isCpTabSelected()) {
            deleteCustomProgram();
        } else {
            deleteCustomQuiz();
        }
    }

    private void deleteCustomQuiz() {
        CustomLessonModel quiz = _listViewCq.getSelectionModel().getSelectedItem();

        if (quiz.getIsArchived() == true) {
            CatchupMathTools.showAlert("Custom Quiz is archived and cannot be deleted.");
            return;
        }
        final CustomQuizDef def = new CustomQuizDef(quiz.getQuizId(), quiz.getQuiz(), adminModel.getId(), 
        	    quiz.getIsAnswersViewable(), quiz.getIsInUse(), quiz.getIsArchived(), quiz.getArchiveDate());
        deleteCustomQuiz(def);
    }

    private void deleteCustomQuiz(final CustomQuizDef def) {

        new RetryAction<IntValueHolder>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true,false);
                CustomLessonModel quiz = _listViewCq.getSelectionModel().getSelectedItem();
                CustomQuizUsageCountAction action = new CustomQuizUsageCountAction(quiz.getQuizId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
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
        
        MessageBox.confirm("Delete Custom Quiz?", "Are you sure you want to delete custom quiz '" + def.getQuizName()
                + "'?", new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (be.getButtonClicked().getText().equals("Yes")) {
                    new RetryAction<RpcData>() {
                        @Override
                        public void attempt() {
                            CmBusyManager.setBusy(true);
                            DeleteCustomQuizAction action = new DeleteCustomQuizAction(adminModel.getId(), def.getQuizId());
                            setAction(action);
                            CmShared.getCmService().execute(action, this);
                        }

                        @Override
                        public void oncapture(RpcData result) {
                            CmBusyManager.setBusy(false);
                            
                            if(result.getDataAsString("status").equals("OK")) {
                                loadCustomQuizDefinitions();
                            }
                        }
                    }.register();
                }
            }
        });
   
    }

    private void archiveCustomQuizDo(final CustomQuizDef def) {
        
        MessageBox.confirm("Archive Custom Quiz?", "Are you sure you want to archive custom quiz '" + def.getQuizName()
                + "'?", new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent be) {
                if (be.getButtonClicked().getText().equals("Yes")) {
                    new RetryAction<RpcData>() {
                        @Override
                        public void attempt() {
                            CmBusyManager.setBusy(true);
                            ArchiveCustomQuizAction action = new ArchiveCustomQuizAction(adminModel.getId(), def.getQuizId());
                            setAction(action);
                            CmShared.getCmService().execute(action, this);
                        }

                        @Override
                        public void oncapture(RpcData result) {
                            CmBusyManager.setBusy(false);
                            
                            if(result.getDataAsString("status").equals("OK")) {
                                loadCustomQuizDefinitions();
                            }
                        }
                    }.register();
                }
            }
        });
   
    }

    private void deleteCustomProgram() {
        final CustomProgramModel sel = _listViewCp.getSelectionModel().getSelectedItem();
        if (sel == null) {
            CatchupMathTools.showAlert("Select a custom program first");
            return;
        }

        if (sel.getIsArchived()) {
            CatchupMathTools.showAlert("This custom program has been archived and cannot be deleted.");
            return;
        }

        if (!_isDebug && sel.getIsTemplate()) {
            CatchupMathTools.showAlert("This program is built-in and cannot be deleted.");
            return;
        }

        if (sel.getAssignedCount() < 1) {
        	
            new RetryAction<IntValueHolder>() {
                @Override
                public void attempt() {
                    CmBusyManager.setBusy(true,false);
                    CustomProgramUsageCountAction action = new CustomProgramUsageCountAction(sel.getProgramId());
                    setAction(action);
                    CmShared.getCmService().execute(action, this);
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
        }
        else {
            archiveCustomProgram(sel);
        }
    }

    private void deleteCustomProgram(final CustomProgramModel program) {
    	MessageBox.confirm("Delete Custom Program",
    			"Are you sure you want to delete custom program '" + program.getProgramName() + "'?",
    			new Listener<MessageBoxEvent>() {
    		public void handleEvent(MessageBoxEvent be) {
    			String btnText = be.getButtonClicked().getText();
    			if (btnText.equalsIgnoreCase("yes")) {
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
                        adminModel.getId());
                action.setProgramId(program.getProgramId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomProgramModel> value) {
                CmBusyManager.setBusy(false);
                _listViewCp.getStore().remove(program);
            }
        }.register();
    }

    private void archiveCustomProgram(final CustomProgramModel program) {
    	MessageBox.confirm("Archive Custom Program",
    			"Are you sure you want to archive custom program '" + program.getProgramName() + "'?",
    			new Listener<MessageBoxEvent>() {
    		public void handleEvent(MessageBoxEvent be) {
    			String btnText = be.getButtonClicked().getText();
    			if (btnText.equalsIgnoreCase("yes")) {
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
                        adminModel.getId());
                action.setProgramId(program.getProgramId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomProgramModel> value) {
                CmBusyManager.setBusy(false);
                //_listViewCp.getStore().remove(program);
                int index = _listViewCp.getStore().indexOf(program);
                _listViewCp.getStore().remove(program);
                _listViewCp.getStore().insert(value, index);
            }
        }.register();
    }

    private void loadCustomQuizDefinitions() {

        new RetryAction<CmList<CustomQuizDef>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetCustProgQuizDefsAction action = new GetCustProgQuizDefsAction(adminModel.getId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CustomQuizDef> defs) {
                CmBusyManager.setBusy(false);

                List<CustomLessonModel> gmodels = new ArrayList<CustomLessonModel>();
                for (int i = 0, t = defs.size(); i < t; i++) {
                	CustomQuizDef def = defs.get(i);
                	if (def.isArchived() == true && _includeArchivedQuizzes == false) continue;
                    gmodels.add(new CustomLessonModel(def.getQuizId(), def.getQuizName(),
                    		def.isAnswersViewable(), def.isInUse(), def.isArchived(),
                    		def.getArchiveDate()));
                }
                _listViewCq.getStore().removeAll();
                _listViewCq.getStore().add(gmodels);
            }
        }.register();
    }

    private void getCustomProgramDefinitions() {
        new RetryAction<CmList<CustomProgramModel>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CustomProgramDefinitionAction action = new CustomProgramDefinitionAction(ActionType.GET,
                        adminModel.getId());
                setAction(action);
                CmShared.getCmService().execute(action, this);
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
                    if (program.getIsArchived() == true && _includeArchivedPrograms == false) continue;
                    if (program.getIsTemplate())
                        templates.add(program);
                    else
                        nonTemplates.add(program);
                    selected.add(program);
                }
                _listViewCp.getStore().removeAll();
                _listViewCp.getStore().add(selected);
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
        public MyMenuItem(CustomProgramModel program, String tip, SelectionListener<MenuEvent> listener) {
            this(program.getProgramName(), tip, listener);
            setData("program", program);
        }

        public MyMenuItem(String text, String tip, SelectionListener<MenuEvent> listener) {
            super(text, listener);
            setToolTip(tip);
        }
    }
}
