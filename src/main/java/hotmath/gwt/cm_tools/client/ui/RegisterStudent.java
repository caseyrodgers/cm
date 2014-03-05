package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.CmProgramType;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.AdvancedOptionsModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.ChapterModelProperties;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramComposite;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoProperties;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentSettingsModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramExt;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramProperties;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.cm_tools.client.model.SubjectModelProperties;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.RetryActionManager;
import hotmath.gwt.shared.client.rpc.action.AddStudentAction;
import hotmath.gwt.shared.client.rpc.action.GetAccountInfoForAdminUidAction;
import hotmath.gwt.shared.client.rpc.action.GetChaptersForProgramSubjectAction;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;
import hotmath.gwt.shared.client.rpc.action.GetSubjectDefinitionsAction;
import hotmath.gwt.shared.client.rpc.action.UpdateStudentAction;
import hotmath.gwt.shared.client.util.CmException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.cell.core.client.LabelProviderSafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.CardLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.Field;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.FormPanel;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;

/**
 * Provides UI for registering new students and modifying the registration of
 * existing students.
 * 
 * @author bob
 * 
 */

public class RegisterStudent extends FramedPanel implements ProcessTracker {

    GWindow _window;

    static GroupInfoProperties __propsGroupInfo = GWT.create(GroupInfoProperties.class);
    static StudyProgramProperties __propsStudyProgram = GWT.create(StudyProgramProperties.class);
    static SubjectModelProperties __propsSubject = GWT.create(SubjectModelProperties.class);
    static ChapterModelProperties __propsChap = GWT.create(ChapterModelProperties.class);

    private boolean isNew;
    private boolean skipComboSet;
    private boolean loading;
    private boolean passPercentReqd;
    private Integer sectionCount = 0;
    private Integer activeSection = 0;

    private StudentModelI stuMdl;
    private StudentSettingsModel stuSettingsMdl;
    protected CmAdminModel cmAdminMdl;
    private AccountInfoModel acctInfoMdl;
    private int inProcessCount;
    private String subjectId;

    private CardLayoutContainer cardPanel;

    private ListStore<StudyProgramExt> progStore;
    private ListStore<StudyProgramExt> customProgStore;
    protected ComboBox<StudyProgramExt> progCombo;
    protected ComboBox<StudyProgramExt> cstmCombo;

    protected ListStore<SubjectModel> subjStore;
    protected ComboBox<SubjectModel> subjCombo;

    protected ListStore<ChapterModel> chapStore;
    protected ComboBox<ChapterModel> chapCombo;

    static private ListStore<GroupInfoModel> __groupStore;
    protected ComboBox<GroupInfoModel> groupCombo;

    protected TextField userName, passCode;
    

    // private Map<String, Object> advOptionsMap;

    static final int FIELD_WIDTH = 295;
    static final int LABEL_WIDTH = 100;
    static final int CUSTOM_ID = 9999;

    private int formHeight = 485;
    protected int formWidth = 475;

    protected FormPanel _formPanel;
    protected TextButton stdAdvOptionsBtn;
    protected TextButton customAdvOptionsBtn;

    public static final String ENTRY_REQUIRED_MSG = "This field is required";

    boolean excludeAutoEnroll;
    
    MyFieldLabel _stdAdvOptionsLabel;

    public RegisterStudent(StudentModelI sm, CmAdminModel cm) {
        this(sm, cm, false);
    }

    public RegisterStudent(StudentModelI sm, CmAdminModel cm, boolean excludeAutoEnroll) {
        this(sm, cm, excludeAutoEnroll, false);
    }

    public RegisterStudent(StudentModelI sm, CmAdminModel cm, boolean excludeAutoEnroll, boolean addSaveButton) {

        this.excludeAutoEnroll = excludeAutoEnroll;

        inProcessCount = 0;
        isNew = (sm == null);
        stuMdl = sm;
        if (stuMdl != null) {
            subjectId = stuMdl.getProgram().getSubjectId();
            passPercent = stuMdl.getPassPercent();
            stuSettingsMdl = stuMdl.getSettings();
            activeSection = stuMdl.getSectionNum();
            sectionCount = stuMdl.getSectionCount();
        }

        cmAdminMdl = cm;
        skipComboSet = isNew;

        setWidget(createForm());
        setComboBoxSelections();

        if (addSaveButton) {
            addButton(saveButton(_fsProgram));
        }
        
        
        
        if(sm != null && (sm.getProgram().getProgramType() == CmProgramType.AUTOENROLL || sm.getProgram().getProgramType() == CmProgramType.ASSIGNMENTS_ONLY)) {
            stdAdvOptionsBtn.setEnabled(false);
        }
        else {
            stdAdvOptionsBtn.setEnabled(true);
        }

    }

    protected void createWindow() {
        _window = new GWindow(false);
        _window.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                _groupSelector.release();
            }
        });

        _window.setWidget(this);

        _window.setHeadingText((isNew) ? "Register a New Student" : "Edit Student");
        _window.setWidth(formWidth + 40);
        _window.setHeight(formHeight + 20);
        _window.setResizable(false);
        _window.setDraggable(true);
        _window.setModal(true);

        /**
         * Assign buttons to the button bar on the Window
         */

        for (TextButton btn : getActionButtons()) {
            btn.addStyleName("register-student-btn");
            _window.addButton(btn);
        }
    }

    public void showWindow() {
        if (_window == null) {
            createWindow();
        }

        _window.show();
        if (isNew) {
            userName.focus();
        }
    }

    /**
     * Return list of Buttons to add to the Button bar
     * 
     * @return
     */
    public List<TextButton> getActionButtons() {
        List<TextButton> list = new ArrayList<TextButton>();
        TextButton cancelBtn = cancelButton();
        cancelBtn.addStyleName("register-student-cancel");
        list.add(saveButton(_fsProgram));
        list.add(cancelButton());
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setWidth(75);
        }
        return list;
    }

    protected void hideAdvancedOptionsButton() {
        _stdAdvOptionsLabel.hide();
    }

    public MyFieldSet _fsProfile, _fsProgram, _fsStdProg, _fsCustomProg;

    GroupSelectorWidget _groupSelector;

    final int FIELDSET_WIDTH=470;
    protected FormPanel createForm() {
        _formPanel = new FormPanel();

        _formPanel.addStyleName("register-student-form-panel");
        // _formPanel.setLabelWidth(120);
        _formPanel.setHeight(formHeight);

        setHeaderVisible(false);
        setBodyBorder(false);
        // _formPanel.setIconStyle("icon-form");
        setButtonAlign(BoxLayoutPack.CENTER);

        VerticalLayoutContainer vertMainPanel = new VerticalLayoutContainer();
        _formPanel.setWidget(vertMainPanel);

        _fsProfile = new MyFieldSet("Define Profile", FIELDSET_WIDTH);
        
        vertMainPanel.add(_fsProfile);
        
        // fL.setLabelWidth(_formPanel.getLabelWidth());
        // fL.setDefaultWidth(LAYOUT_WIDTH);
        
        userName = new TextField();
        userName.setAllowBlank(false);
        userName.setId("name");
        userName.setEmptyText("-- enter name --");
        if (!isNew) {
            userName.setValue((String) stuMdl.getName());
        }
        _fsProfile.addThing(new MyFieldLabel(userName, "Name", LABEL_WIDTH,FIELD_WIDTH));

        passCode = new TextField();
        // passCode.setFieldLabel("Passcode");
        passCode.setEmptyText("-- enter passcode --");
        passCode.setAllowBlank(false);
        passCode.setId("passcode");
        if (!isNew) {
            passCode.setValue((String) stuMdl.getPasscode());
        }
        _fsProfile.addThing(new MyFieldLabel(passCode, "Passcode",LABEL_WIDTH, FIELD_WIDTH));

        if (__groupStore == null) {
            __groupStore = new ListStore<GroupInfoModel>(__propsGroupInfo.id());
        }

        CmAsyncRequest callback = new CmAsyncRequestImplDefault() {
            public void requestComplete(String groupName) {
                setGroupSelection(groupName);
            }
        };

        _groupSelector = new GroupSelectorWidget(cmAdminMdl, __groupStore, true, this, "group-combo", true,
        		__propsGroupInfo.groupName(), callback);
        groupCombo = _groupSelector.groupCombo();
        if (UserInfo.getInstance() == null || !UserInfo.getInstance().isSingleUser()) {
            _fsProfile.addThing(new MyFieldLabel(groupCombo, "Group", LABEL_WIDTH, FIELD_WIDTH));
        }

        progStore = new ListStore<StudyProgramExt>(__propsStudyProgram.id());
        customProgStore = new ListStore<StudyProgramExt>(__propsStudyProgram.id());
        getStudyProgramListRPC();

        stdAdvOptionsBtn = stdAdvancedOptionsBtn();
        stdAdvOptionsBtn.disable();
        customAdvOptionsBtn = customAdvancedOptionsBtn();
        customAdvOptionsBtn.disable();

        // fl.setLabelWidth(_formPanel.getLabelWidth());
        // fl.setDefaultWidth(fL.getDefaultWidth());

        _fsProgram = new MyFieldSet("Assign Program", FIELDSET_WIDTH);
        _fsProgram.addStyleName("register-student-outer-fieldset");

        progCombo = createProgramCombo(progStore);
        _fsProgram.addThing(new MyFieldLabel(progCombo, "Program type", LABEL_WIDTH, FIELD_WIDTH));


        setupStdProgramUI();
        setupCustomProgramUI();

        getAccountInfoRPC(cmAdminMdl.getUid());

        cardPanel = new CardLayoutContainer();
        cardPanel.add(_fsStdProg);
        cardPanel.add(_fsCustomProg);
        cardPanel.setActiveWidget(_fsStdProg);
        
        _fsProgram.addThing(cardPanel);

        
        vertMainPanel.add(_fsProgram);

        /**
         * Seems like a bug with setting focus, so the only way to it to work is
         * to set a timer and hope ...
         * 
         * It seems the form is validating before it needs to and showing error
         * messages before any input has been added .. which removes the focus
         * 
         * @TODO: get name.focus() to just work without the need for timing
         *        tricks.
         */
        if (isNew) {
            new Timer() {
                public void run() {
                    userName.focus();
                }
            }.schedule(2000);
        }
        return _formPanel;
    }

    private String passPercent;

    private void setupStdProgramUI() {
        _fsStdProg = new MyFieldSet("", FIELDSET_WIDTH);
        _fsStdProg.addStyleName("register-student-inner-fieldset");
        _fsStdProg.setId("std-prog-fs");

        // fl.setLabelWidth(_formPanel.getLabelWidth());
        // fl.setDefaultWidth(LAYOUT_WIDTH);

        subjStore = new ListStore<SubjectModel>(__propsSubject.id());
        getSubjectList((stuMdl != null) ? stuMdl.getProgram().getProgramType().getType() : null, subjStore);
        subjCombo = createSubjectCombo(subjStore);
        _fsStdProg.addThing(new MyFieldLabel(subjCombo, "Subject", LABEL_WIDTH, FIELD_WIDTH));

        chapStore = new ListStore<ChapterModel>(__propsChap.id());
        getChapterListRPC((stuMdl != null) ? stuMdl.getProgram().getProgramType().getType() : null, subjectId, false, chapStore);
        chapCombo = chapterCombo(chapStore);
        _fsStdProg.addThing(new MyFieldLabel(chapCombo, "Chapter", LABEL_WIDTH, FIELD_WIDTH));

        _stdAdvOptionsLabel = new MyFieldLabel(stdAdvOptionsBtn, "Options", LABEL_WIDTH, FIELD_WIDTH);
        _fsStdProg.addThing(_stdAdvOptionsLabel);
    }

    private void setupCustomProgramUI() {
        _fsCustomProg = new MyFieldSet("",FIELDSET_WIDTH);
        _fsCustomProg.setHeadingText("");
        _fsCustomProg.addStyleName("register-student-inner-fieldset");
        _fsCustomProg.setId("custom-prog-fs");

        // fl.setLabelWidth(_formPanel.getLabelWidth());
        // fl.setDefaultWidth(LAYOUT_WIDTH);

        cstmCombo = createCustomCombo(customProgStore, "Select a Custom Program|Quiz");
        _fsCustomProg.addThing(new MyFieldLabel(cstmCombo, "Program or Quiz", LABEL_WIDTH, FIELD_WIDTH));
        _fsCustomProg.addThing(new MyFieldLabel(customAdvOptionsBtn, "Options",LABEL_WIDTH, FIELD_WIDTH));
    }

    private SelectHandler advancedOptionsSelectionHandler = new SelectHandler() {
        @Override
        public void onSelect(final SelectEvent event) {
            
            try {
                doSubmitAction(new AfterValidation() {
                    @Override
                    public void afterValidation(StudentModel student) {
                        showAdvancedOptions(((TextButton)event.getSource()).getId(), student);
                    }
                }, false);
            }
            catch(Exception e) {
                Log.error("Error collection program info", e);
            }
        }
    };

    private void showAdvancedOptions(String typeOfAdvanced, StudentModel student) {
        
        AdvOptCallback callback = new AdvOptCallback() {
            @Override
            void setAdvancedOptions(AdvancedOptionsModel options) {
                stuSettingsMdl = options.getSettings();
                passPercent = options.getPassPercent();
                activeSection = options.getSectionNum();
            }
        };

        AdvancedOptionsModel options = new AdvancedOptionsModel();
        
        final StudentSettingsModel ssm = new StudentSettingsModel();

        /** only set options if not null */
        if (stuSettingsMdl != null) {
            ssm.setLimitGames(stuSettingsMdl.getLimitGames());
            ssm.setShowWorkRequired(stuSettingsMdl.getShowWorkRequired());
            ssm.setStopAtProgramEnd(stuSettingsMdl.getStopAtProgramEnd());
            ssm.setTutoringAvailable(stuSettingsMdl.getTutoringAvailable());
            ssm.setDisableCalcAlways(stuSettingsMdl.getDisableCalcAlways());
            ssm.setDisableCalcQuizzes(stuSettingsMdl.getDisableCalcQuizzes());
            ssm.setNoPublicWebLinks(stuSettingsMdl.isNoPublicWebLinks());
        } else {
            /** use account data to set tutoring available */
            if (acctInfoMdl != null) {
                ssm.setTutoringAvailable(acctInfoMdl.getIsTutoringEnabled());
            }
        }

        options.setPassPercent(passPercent);
        options.setSettings(ssm);

        options.setSectionCount(sectionCount);
        if (activeSection == null)
            activeSection = 0;
        options.setActiveSection(activeSection);
        options.setSectionNum(activeSection);
        options.setSectionIsSettable(isSectionSelectAvail(student.getProgram().getProgramType()));

        if ("custom-adv-opt-btn".equals(typeOfAdvanced)) {
            ssm.setStopAtProgramEnd(true);
            options.setProgStopIsSettable(false);
        } else {
            options.setProgStopIsSettable(true);
        }

        new RegisterStudentAdvancedOptions(callback, cmAdminMdl, options, isNew, passPercentReqd, student.getProgram()).setVisible(true);
    }
    
    private TextButton stdAdvancedOptionsBtn() {
        TextButton btn = new TextButton("Advanced Options");
        btn.setToolTip("Disallow games, Change pass percentage, etc.");
        btn.setWidth("110px");
        btn.addSelectHandler(advancedOptionsSelectionHandler);
        // btn.addStyleName("register-student-advanced-options-btn");
        btn.setId("std-adv-opt-btn");
        return btn;
    }

    private TextButton customAdvancedOptionsBtn() {
        TextButton btn = new TextButton("Advanced Options");
        btn.setToolTip("Disallow games, Change pass percentage, etc.");
        btn.setWidth("110px");
        btn.addSelectHandler(advancedOptionsSelectionHandler);
        btn.addStyleName("register-student-advanced-options-btn");
        btn.setId("custom-adv-opt-btn");
        return btn;
    }

    private boolean isSectionSelectAvail(CmProgramType type) {
        if(type == CmProgramType.PROF) {
            // make sure the subject has been selected
            return subjCombo.getCurrentValue() != null;
        }
        else if (type == CmProgramType.GRADPREP || type == CmProgramType.GRADPREPNATIONAL || type == CmProgramType.GRADPREPTX) {
            return true;
        }
        else {
            return false;
        }
    }

    private ComboBox<StudyProgramExt> createProgramCombo(ListStore<StudyProgramExt> store) {

        
        LabelProviderSafeHtmlRenderer<StudyProgramExt> renderer=new LabelProviderSafeHtmlRenderer<StudyProgramExt>(__propsStudyProgram.title()) {
            public SafeHtml render(StudyProgramExt value) {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                return sb.appendEscaped(_addSubjectValueTags(value, value.getTitle())).toSafeHtml();
            }
        };
        //getProgramTemplate()
        ComboBox<StudyProgramExt> combo = new ComboBox<StudyProgramExt>(new ComboBoxCell<StudyProgramExt>(store, __propsStudyProgram.title(), renderer));

        // combo.setFieldLabel("Program type");
        combo.setForceSelection(true);

        combo.setEditable(false);
        // combo.setMaxLength(45);
        combo.setAllowBlank(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setStore(store);

        // combo.setTemplate(getProgramTemplate());
        combo.setTitle("Select a program type");
        combo.setId("prog-combo");
        combo.setTypeAhead(true);
        combo.setSelectOnFocus(true);
        combo.setEmptyText("-- select a program type --");
        combo.setWidth(280);

        combo.addSelectionHandler(new SelectionHandler<StudyProgramExt>() {
            public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<StudyProgramExt> se) {

                if (loading)
                    return;

                StudyProgramExt sp = se.getSelectedItem();
                boolean needsSubject = sp.isNeedsSubject();
                boolean needsChapters = sp.isNeedsChapters();
                passPercentReqd = sp.isNeedsPassPercent();
                CmProgramType progType = sp.getProgramType();

                if (CmProgramType.CUSTOM != progType) {

                    if (!cardPanel.getActiveWidget().equals(_fsStdProg)) {
                        cardPanel.setActiveWidget(_fsStdProg);
                    }
                    sectionCount = sp.getSectionCount();
                    activeSection = 0;


                    if (progType == null || progType == CmProgramType.AUTOENROLL || progType == CmProgramType.ASSIGNMENTS_ONLY || progType == CmProgramType.AUTOENROLLCOLLEGE || progType == CmProgramType.NONE) {
                        stdAdvOptionsBtn.disable();
                    } else {
                        stdAdvOptionsBtn.enable();
                    }
                    //stdAdvOptionsBtn.enable();
                    
                    skipComboSet = true;
                    subjectId = null;

                    if (needsSubject) {
                        subjCombo.clear();
                        subjCombo.enable();
                        subjCombo.setForceSelection(true);
                        getSubjectList((String) sp.getShortTitle(), subjStore);
                    } else {
                        subjCombo.clearInvalid();
                        subjCombo.clear();
                        subjCombo.disable();
                        subjCombo.setForceSelection(false);
                        subjectId = null;
                    }


                    if (needsChapters) {
                        chapCombo.clear();
                        chapCombo.enable();
                        chapCombo.setForceSelection(true);
                    } else {
                        chapCombo.clearInvalid();
                        chapCombo.clear();
                        chapCombo.disable();
                        chapCombo.setForceSelection(false);
                    }
                } else {
                    cardPanel.setActiveWidget(_fsCustomProg);
                    skipComboSet = true;
                    subjectId = null;
                    activeSection = 0;
                }

            }
        });

        return combo;
    }

    private String _addSubjectValueTags(StudyProgramExt value, String textIn) {
        String text = textIn;
        if(value.getStyleIsTemplate() != null) {
            text += " [built-in]";
          }
          
          if(value.getStyleIsArchived() != null) {
              text += " [archived]";
          }
          
          text = _addIfFree(text, value.getStyleIsFree());
          
          return text;
    }
    
    private String _addIfFree(String text, String styleIfFree) {
        if(acctInfoMdl.getIsFreeAccount() && styleIfFree == null) {
            text += " [free]";
        }
        return text;
    }
    
    private ComboBox<StudyProgramExt> createCustomCombo(ListStore<StudyProgramExt> store, String title) {
        LabelProviderSafeHtmlRenderer<StudyProgramExt> renderer=new LabelProviderSafeHtmlRenderer<StudyProgramExt>(__propsStudyProgram.label()) {
            public SafeHtml render(StudyProgramExt value) {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                return sb.appendEscaped(_addSubjectValueTags(value, value.getLabel())).toSafeHtml();
            }
        };
        ComboBox<StudyProgramExt> combo = new ComboBox<StudyProgramExt>(new ComboBoxCell<StudyProgramExt>(store, __propsStudyProgram.label(), renderer));
        combo.setForceSelection(true);
        combo.setEditable(false);
        // combo.setMaxLength(45);
        combo.setAllowBlank(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setStore(store);

        combo.setTitle(title);
        combo.setId("custom-combo");
        combo.setTypeAhead(true);
        combo.setSelectOnFocus(true);
        combo.setEmptyText("-- make a selection --");
        combo.addSelectionHandler(new SelectionHandler<StudyProgramExt>() {

            @Override
            public void onSelection(SelectionEvent<StudyProgramExt> event) {

                if (loading)
                    return;

                StudyProgramExt sp = event.getSelectedItem();
                passPercentReqd = sp.isNeedsPassPercent();
                customAdvOptionsBtn.enable();
            }
        });
        return combo;
    }

    private native String getProgramTemplate() /*-{ 
                                               return  [ 
                                               '<tpl for=".">', 
                                               '<div class="x-combo-list-item {styleIsTemplate} {styleIsArchived} {styleIsFree}" qtip="{descr}">{label}</div>', 
                                               '</tpl>' 
                                               ].join(""); 
                                               }-*/;

    private native String getSubjectTemplate() /*-{ 
                                               return  [ 
                                               '<tpl for=".">', 
                                               '<div class="x-combo-list-item {styleIsFree}">{subject}</div>', 
                                               '</tpl>' 
                                               ].join(""); 
                                               }-*/;

    private native String getChapterTemplate() /*-{ 
                                               return  [ 
                                               '<tpl for=".">', 
                                               '<div class="x-combo-list-item {styleIsFree}">{chapter}</div>', 
                                               '</tpl>' 
                                               ].join(""); 
                                               }-*/;

    private ComboBox<SubjectModel> createSubjectCombo(ListStore<SubjectModel> store) {

        
        LabelProviderSafeHtmlRenderer<SubjectModel> renderer=new LabelProviderSafeHtmlRenderer<SubjectModel>(__propsSubject.subject()) {
            public SafeHtml render(SubjectModel value) {
                SafeHtmlBuilder sb = new SafeHtmlBuilder();
                return sb.appendEscaped(_addIfFree(value.getSubject(), value.getStyleIsFree())).toSafeHtml();
            }
        };
        ComboBox<SubjectModel> combo = new ComboBox<SubjectModel>(new ComboBoxCell<SubjectModel>(store, __propsSubject.subject(), renderer));
        // combo.setFieldLabel("Subject");
        combo.setForceSelection(false);
        // combo.setDisplayField("subject");
        combo.setEditable(false);
        // combo.setMaxLength(30);
        combo.setAllowBlank(false);
        combo.setTriggerAction(TriggerAction.ALL);

        combo.setTitle("Select a subject");
        combo.setId("subj-combo");
        combo.setTypeAhead(true);
        combo.setSelectOnFocus(true);
        combo.setEmptyText("-- select a subject --");

        // combo.setTemplate();

        combo.disable();
        combo.setWidth(280);

        combo.setStore(store);

        combo.addSelectionHandler(new SelectionHandler<SubjectModel>() {

            @Override
            public void onSelection(SelectionEvent<SubjectModel> event) {

                if (loading)
                    return;

                SubjectModel sm = event.getSelectedItem();
                if (subjectId == null || !subjectId.equals(sm.getAbbrev())) {
                    try {
                        skipComboSet = true;
                        subjectId = sm.getAbbrev();
                        ComboBox<StudyProgramExt> cb = progCombo;
                        StudyProgramExt sp = cb.getValue();
                        String progId = sp.getShortTitle();
                        chapStore.clear();
                        getChapterListRPC(progId, subjectId, true, chapStore);
                    } catch (Exception e) {
                        e.printStackTrace();
                        CmMessageBox.showAlert(e.getMessage());
                    }
                }
            }
        });
        return combo;
    }

    private ComboBox<ChapterModel> chapterCombo(ListStore<ChapterModel> store) {
        
        LabelProviderSafeHtmlRenderer<ChapterModel> renderer=new LabelProviderSafeHtmlRenderer<ChapterModel>(__propsChap.chapter()) {
            public SafeHtml render(ChapterModel value) {
                return super.render(value);
            }
        };
        //getChapterTemplate
        ComboBox<ChapterModel> combo = new ComboBox<ChapterModel>(new ComboBoxCell<ChapterModel>(store, __propsChap.chapter(), renderer));
        combo.setForceSelection(false);
        combo.setEditable(false);
        // combo.setMaxLength(160);
        combo.setAllowBlank(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setStore(store);
        combo.setTitle("Select a Chapter");
        combo.setId("chap-combo");
        combo.setTypeAhead(true);
        combo.setSelectOnFocus(true);
        combo.setEmptyText("-- select a chapter --");
        combo.disable();
        combo.setWidth(280);
        return combo;
    }

    private TextButton cancelButton() {
        TextButton cancelBtn = new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                _window.close();
            }
        });
        return cancelBtn;
    }

    private TextButton saveButton(final FieldSet fs) {

        TextButton saveBtn = new TextButton("Save", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                try {
                    doSubmitAction(null);
                } catch (CmException cm) {
                    cm.printStackTrace();
                }
            }
        });
        return saveBtn;
    }

    private void getStudyProgramListRPC() {

        inProcessCount++;

        new RetryAction<CmList<StudyProgramModel>>() {
            @Override
            public void attempt() {
                GetProgramDefinitionsAction action = new GetProgramDefinitionsAction(cmAdminMdl.getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<StudyProgramModel> spmList) {
                List<StudyProgramExt> progList = new ArrayList<StudyProgramExt>();
                List<StudyProgramExt> customProgList = new ArrayList<StudyProgramExt>();

                int stuCustomProgramId = (isNew == false && stuMdl.getProgram().getCustom().getCustomProgramId() != 0) ? stuMdl.getProgram().getCustom()
                        .getCustomProgramId() : -1;
                int stuCustomQuizId = (isNew == false && stuMdl.getProgram().getCustom().getCustomQuizId() != 0) ? stuMdl.getProgram().getCustom()
                        .getCustomQuizId() : -1;

                for (StudyProgramModel spm : spmList) {
                    if (excludeAutoEnroll && spm.getShortTitle().equalsIgnoreCase("AUTO-ENROLL"))
                        continue;

                    if ((isNew == true && spm.getIsArchived() == true))
                        continue;

                    if (isNew == false && spm.getIsArchived() == true && spm.getCustomProgramId() != stuCustomProgramId
                            && spm.getCustomQuizId() != stuCustomQuizId)
                        continue;

                    if (spm.getCustomProgramId() == 0 && spm.getCustomQuizId() == 0) {
                        progList.add(new StudyProgramExt(spm, spm.getTitle(), spm.getShortTitle(), spm.getDescr(), spm.isNeedsSubject(), spm.isNeedsChapters(), spm.isNeedsPassPercent(), spm.getCustomProgramId(), spm.getCustomProgramName()));
                    } else {
                        customProgList.add(new StudyProgramExt(spm, spm.getTitle(), spm.getShortTitle(), spm.getDescr(), spm.isNeedsSubject(), spm.isNeedsChapters(), spm.isNeedsPassPercent(), spm.getCustomProgramId(), spm.getCustomProgramName()));
                    }

                }
                
                /** When set, the student will only have access to the assignments.
                 * 
                 */
                StudyProgramModel spma = new StudyProgramModel(-1, CmProgramType.ASSIGNMENTS_ONLY.getType(), CmProgramType.ASSIGNMENTS_ONLY.getType(), CmProgramType.ASSIGNMENTS_ONLY.getType(), 0, " ", 0, " ",false, false, false,false, 0);
                progList.add(new StudyProgramExt(spma,spma.getTitle(), spma.getShortTitle(), spma.getDescr(), false, false, false, 0, null));

                
                
                StudyProgramModel spm = new StudyProgramModel(CUSTOM_ID, "Custom", "Custom", "Custom Programs and Quizzes", 0, " ", 0, " ",false, false, false,false, 0);
                spm.setProgramType(CmProgramType.CUSTOM);
                spm.setIsArchived(false);
                progList.add(new StudyProgramExt(spm, "Custom", "Custom", "Custom Programs and Quizzes", false, false, false, 0, null));

                /** If is free, then shown only Prof and Custom as available */
                for (StudyProgramExt md : progList) {
                    if (acctInfoMdl.getIsFreeAccount()) {
                        String pn = md.getTitle();
                        if (!pn.contains("Proficiency") && !pn.contains("Custom")) {
                            md.setStyleIsFree("is-free-account-label");
                        }
                    }
                }

                /** If is free, then show only Essentials as available. */
                for (StudyProgramExt md : customProgList) {
                    if (acctInfoMdl.getIsFreeAccount()) {
                        String pn = md.getTitle();
                        if (!pn.contains("Essentials")) {
                            md.setStyleIsFree("is-free-account-label");
                        }
                    }
                }

                progStore.addAll(progList);
                customProgStore.addAll(customProgList);

                inProcessCount--;
                setComboBoxSelections();
            }
        }.register();
    }

    private Map<String, List<SubjectModel>> programSubjectMap = new HashMap<String, List<SubjectModel>>();

    private void getSubjectList(final String progId, final ListStore<SubjectModel> subjStore) {

        if (progId == null)
            return;

        List<SubjectModel> subjList = programSubjectMap.get(progId);
        if (subjList != null) {
            subjStore.clear();
            subjStore.addAll(subjList);
            setComboBoxSelections();
        } else {
            getSubjectListRPC(progId, subjStore);
        }
    }

    private void getSubjectListRPC(final String progId, final ListStore<SubjectModel> subjStore) {

        inProcessCount++;

        new RetryAction<CmList<SubjectModel>>() {

            @Override
            public void attempt() {
                GetSubjectDefinitionsAction action = new GetSubjectDefinitionsAction(progId);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<SubjectModel> result) {

                for (SubjectModel sm : result) {
                    if (acctInfoMdl.getIsFreeAccount()) {
                        if (!sm.getSubject().equals("Essentials")) {
                            sm.setStyleIsFree("is-free-account-label");
                        }
                    }
                }
                subjStore.clear();
                subjStore.addAll(result);
                inProcessCount--;
                programSubjectMap.put(progId, result);
                setComboBoxSelections();
            }
        }.register();

    }

    protected void addUserRPC(final StudentModel sm) {

        /**
         * execute outside the RetryManager to allow processing exceptions.
         */
        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                AddStudentAction action = new AddStudentAction(sm);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(StudentModelI ai) {
                CmBusyManager.setBusy(false);
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_PROGRAM_CHANGED, ai.getProgramChanged()));
                _window.close();
            }

            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                if (caught.getMessage().indexOf("already in use") > -1) {
                	if (caught.getMessage().indexOf("pass") > -1)
                        CmMessageBox.showAlert("Passcode In Use", caught.getMessage());
                	else if (caught.getMessage().indexOf("name") > -1)
                        CmMessageBox.showAlert("Name In Use", caught.getMessage());
                    return;
                }
                super.onFailure(caught);
            }
        }.attempt();
    }

    /**
     * Will show a message if not allowed to save.
     * 
     * Return true only if OK to save changes.
     * 
     * @param sm
     * @return
     */
    protected boolean verifyOkToSave(StudentModel sm) {
        /**
         * Free accounts can only change to Essentials/Essentials Topics
         * 
         */
        if (acctInfoMdl.getIsFreeAccount()) {
            String progName = sm.getProgram().getProgramDescription();
            String pn = sm.getProgram().getCustom().getCustomProgramName();
            if (!progName.equals("Ess Prof") && !(pn != null && pn.equals("Essentials Topics"))) {
                CatchupMathTools.showAlert("Change not allowed", "Sorry, this program is not currently available under your school license.");
                return false;
            }
        }

        return true;
    }

    protected void updateUserRPC(final StudentModel sm, final Boolean stuChanged, final Boolean progChanged, final Boolean progIsNew,
            final Boolean passcodeChanged, final Boolean passPercentChanged, final Boolean sectionNumChanged) {

        if (!verifyOkToSave(sm)) {
            return;
        }

        new RetryAction<StudentModelI>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                UpdateStudentAction action = new UpdateStudentAction(sm, stuChanged, progChanged, progIsNew, passcodeChanged, passPercentChanged,
                        sectionNumChanged);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(StudentModelI ai) {
                CmBusyManager.setBusy(false);
                if (_window != null) {
                    _window.close();
                }
                
                Info.display("Information", "Save Complete");
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_PROGRAM_CHANGED, ai.getProgramChanged()));
            }

            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                if (caught.getMessage().indexOf("already in use") > -1) {
                	if (caught.getMessage().toLowerCase().indexOf("pass") > -1)
                        CmMessageBox.showAlert("Passcode In Use", caught.getMessage());
                	else if (caught.getMessage().indexOf("name") > -1)
                        CmMessageBox.showAlert("Name In Use", caught.getMessage());
                	
                	/** reset the RetryManager, otherwise other async operations 
                	 * will not be executed. 
                	 * 
                	 */
                	RetryActionManager.getInstance().requestComplete(this);
                    return;
                }
                super.onFailure(caught);
            }

        }.register();
    }

    private void setComboBoxSelections() {
        if (this.stuMdl != null && !skipComboSet && inProcessCount < 1) {

            loading = true;

            setGroupSelection();

            StudyProgramExt sp = setProgramSelection();

            if (sp == null) {
                CmMessageBox.showAlert("Program not found!");
                loading = false;
                return;
            }

            CmProgramType progType = (CmProgramType) sp.getProgramType();
            if (CmProgramType.CUSTOM != progType) {
                cardPanel.setActiveWidget(_fsStdProg);

                boolean needsSubject = sp.isNeedsSubject();
                if (needsSubject)
                    setSubjectSelection();

                boolean needsChapters = sp.isNeedsChapters();
                if (needsChapters)
                    setChapterSelection();
            } else {
                cardPanel.setActiveWidget(_fsCustomProg);
                setCustomProgramSelection();
            }
            passPercentReqd = sp.isNeedsPassPercent();

            
            if (stdAdvOptionsBtn.isVisible()) {
                CmProgramType pt = stuMdl.getProgram().getProgramType();
                if(pt == CmProgramType.AUTOENROLL ||  pt == CmProgramType.AUTOENROLLCOLLEGE || pt == CmProgramType.ASSIGNMENTS_ONLY || pt == CmProgramType.NONE) {
                    stdAdvOptionsBtn.setEnabled(false);
                }
                else {
                    stdAdvOptionsBtn.setEnabled(true);
                }
            }

            if (customAdvOptionsBtn.isVisible()) {
                customAdvOptionsBtn.enable();
            }

            loading = false;
        }
        skipComboSet = isNew;
    }

    private void setGroupSelection() {
        if (stuMdl == null) {
            new Exception().printStackTrace();
            return;
        }

        if (!groupCombo.isVisible()) {
            return;
        }

        int groupId = stuMdl.getGroupId();
        if (groupId > 0) {
            List<GroupInfoModel> l = __groupStore.getAll();
            for (GroupInfoModel g : l) {
                Integer gi = g.getId();
                if (groupId == gi) {
                    groupCombo.setOriginalValue(g);
                    groupCombo.setValue(g);
                }
            }
        }
    }

    protected void setGroupSelection(String groupName) {
    	skipComboSet = true;
        List<GroupInfoModel> l = __groupStore.getAll();
        for (GroupInfoModel g : l) {
            if (groupName.equals(g.getGroupName())) {
                groupCombo.finishEditing();
                groupCombo.setValue(g, true, true);
                break;
            }
        }
    	
    }

    
    private StudyProgramExt setProgramSelection() {
        StudentProgramModel program = stuMdl.getProgram();
        String shortName = program.getProgramType().getType();

        if (program.isCustom()) {
            List<StudyProgramExt> list = progStore.getAll();

            for (StudyProgramExt sp : list) {
                if (sp.getShortTitle().equals("Custom")) {
                    progCombo.setOriginalValue(sp);
                    progCombo.setValue(sp);
                    return sp;
                }
            }
            return null;
        }

        if (shortName != null) {
            List<StudyProgramExt> list = progStore.getAll();
            for (StudyProgramExt sp : list) {

                if (progNameCheckHack(shortName, sp)) {
                    progCombo.setOriginalValue(sp);
                    progCombo.setValue(sp);
                    return sp;
                }
            }
        }
        return null;
    }

    private StudyProgramExt setCustomProgramSelection() {
        StudentProgramModel program = stuMdl.getProgram();

        List<StudyProgramExt> list = customProgStore.getAll();

        for (StudyProgramExt sp : list) {

            if ((program.getCustom().getCustomProgramId() != 0 && program.getCustom().getCustomProgramId() == sp.getCustomProgramId())
                    || (program.getCustom().getCustomQuizId() != 0 && program.getCustom().getCustomQuizId() == sp.getCustomQuizId())) {
                cstmCombo.setOriginalValue(sp);
                cstmCombo.setValue(sp);
                return sp;
            }
        }
        return null;

    }

    /**
     * perform hack to determine correct program.
     * 
     * @NOTE: add a way to identify programs correctly.
     * 
     * @param shortName
     * @param sp
     * @return
     */
    private boolean progNameCheckHack(String shortName, StudyProgramExt sp) {
        String st = ((String) sp.getShortTitle()).toLowerCase();

        if (sp.getCustomProgramName() != null && sp.getCustomProgramName().equals(shortName)) {
            return true;
        }

        if (sp.getCustomQuizName() != null && sp.getCustomQuizName().equals(shortName)) {
            return true;
        }

        if (st.equals("chap") && shortName.toLowerCase().indexOf(" chap") > -1) {
            return true;
        } else if (st.equals("prof") && shortName.toLowerCase().indexOf(" prof") > -1)
            return true;
        else {
            return shortName.equalsIgnoreCase(st);
        }
    }

    private void setSubjectSelection() {
        String shortName = stuMdl.getProgram().getProgramDescription();

        if (shortName != null) {
            List<SubjectModel> list = subjStore.getAll();
            for (SubjectModel s : list) {
                if (shortName.indexOf((String) s.getAbbrev()) > -1) {
                    subjCombo.setOriginalValue(s);
                    subjCombo.setValue(s);
                    subjCombo.enable();
                    break;
                }
            }
        }

    }

    private void setChapterSelection() {
        String chap = stuMdl.getChapter();

        if (chap != null) {
            List<ChapterModel> list = chapStore.getAll();
            for (ChapterModel c : list) {
                if (chap.equals(c.getTitle())) {
                    chapCombo.setOriginalValue(c);
                    chapCombo.setValue(c);
                    chapCombo.enable();
                    break;
                }
            }
        }
    }

    private void getChapterListRPC(final String progId, final String subjId, final Boolean chapOnly, final ListStore<ChapterModel> chapStore) {

        if (progId == null || !progId.equalsIgnoreCase("chap"))
            return;

        inProcessCount++;
        new RetryAction<CmList<ChapterModel>>() {

            @Override
            public void attempt() {
                CmServiceAsync s = CmShared.getCmService();
                GetChaptersForProgramSubjectAction action = new GetChaptersForProgramSubjectAction(progId, subjId);
                setAction(action);
                s.execute(action, this);
            }

            public void oncapture(CmList<ChapterModel> result) {

                for (ChapterModel cm : result) {
                    if (acctInfoMdl.getIsFreeAccount()) {
                        cm.setStyleIsFree("is-free-account-label");
                    }
                }

                chapStore.addAll(result);
                inProcessCount--;
                if (!chapOnly)
                    setComboBoxSelections();
                else {
                    chapCombo.setOriginalValue(null);
                    chapCombo.setValue(null);
                    chapCombo.clear();
                }
            }
        }.register();
    }

    protected void getAccountInfoRPC(final Integer uid) {
        new RetryAction<AccountInfoModel>() {
            @Override
            public void attempt() {
                CmServiceAsync s = CmShared.getCmService();
                GetAccountInfoForAdminUidAction action = new GetAccountInfoForAdminUidAction(uid);
                setAction(action);
                CmLogger.info("AccountInfoPanel: reading admin info RPC");
                s.execute(action, this);
            }

            public void oncapture(AccountInfoModel ai) {
                acctInfoMdl = ai;
            }
        }.register();
    }

    /**
     * Perform the form save operation and display any required validation.
     * 
     * Throws CmExeptionValidationFailed on failed validation attempt.
     * 
     * if callback == null, then default operation, saving data, is performed.
     * If callback is provided, then after validation of form callback is
     * called.
     * 
     * 
     * @TODO: separate into validation/action. Perhaps strategy pattern.
     * 
     * @param fs
     * @param fp
     */
    protected void doSubmitAction(AfterValidation callback) throws CmException {
        doSubmitAction(callback, true);
    }
    protected void doSubmitAction(AfterValidation callback, boolean validate) throws CmException {

        try {
            if(validate) {
                if(userName.isVisible()) {
                    checkValid(userName);
                }
                if(passCode.isVisible()) {
                    checkValid(passCode);
                }
                if(groupCombo.isVisible()) {
                    checkValid(groupCombo);
                }
                if(progCombo.isVisible()) {
                    checkValid(progCombo);
                }
            }
        }
        catch(Exception e) {
            CmMessageBox.showAlert("Save Error", "Please provide values for all required fields.");
            return;
        }
        
        String name = "";
        if(validate) {
            if(userName.isVisible()) {
                TextField tf = userName;
                if (tf != null) {
                    
                    tf.clearInvalid();
                    name = tf.getValue();
                    if (name == null) {
                        tf.focus();
                        tf.forceInvalid(ENTRY_REQUIRED_MSG);
                        throw new CmExceptionValidationFailed();
                    }
                }
            }
        }

        String passcode = null;
        if(validate) {
            if(passCode.isVisible()) {
                TextField tf = passCode;
                if (tf != null) {
                    tf.clearInvalid();
                    passcode = tf.getValue();
                    if (passcode == null) {
                        tf.focus();
                        tf.forceInvalid(ENTRY_REQUIRED_MSG);
                        throw new CmExceptionValidationFailed();
                    } else {
                        if (passcode.contains(" ")) {
                            tf.focus();
                            tf.forceInvalid("Password cannot contain spaces");
                            throw new CmExceptionValidationFailed();
                        }
        
                    }
                }
            }
        }

        int  groupId = 0;
        String group = null;
        if(validate) {
            if(groupCombo.isVisible()) {
                ComboBox<GroupInfoModel> cg = groupCombo;
                if (cg != null) {
                    cg.clearInvalid();
                    GroupInfoModel g = cg.getValue();
                    if (g == null) {
                        cg.focus();
                        cg.forceInvalid(ENTRY_REQUIRED_MSG);
                        cg.expand();
                        throw new CmExceptionValidationFailed();
                    }
                    groupId = g.getId();
                    group = g.getGroupName();
                }
            }
        }

        String fsId = null;
        if(cardPanel.isVisible()) {
            for (int i=0;i<cardPanel.getWidgetCount();i++) {
                Component c = (Component)cardPanel.getWidget(i);
                if (c.isVisible()) {
                    fsId = c.getId();
                }
            }
        }

        StudyProgramExt studyProgExt = null;
        SubjectModel sub = null;
        ChapterModel chap = null;
        String prog = null;

        if (fsId.equals("std-prog-fs")) {
            ComboBox<StudyProgramExt> cb = progCombo;
            studyProgExt = cb.getValue();
            cb.clearInvalid();
            
            if(validate) {
                if (studyProgExt == null) {
                    cb.focus();
                    cb.forceInvalid(ENTRY_REQUIRED_MSG);
                    cb.expand();
                    throw new CmExceptionValidationFailed();
                }
                prog = studyProgExt.getShortTitle();
            }

            ComboBox<SubjectModel> cs = subjCombo;
            sub = cs.getValue();
            cs.clearInvalid();
            if (sub != null) {
                prog = sub.getAbbrev() + " " + prog;
            }
            
            if(validate) {
                if (studyProgExt.isNeedsSubject() && sub == null) {
                    cs.focus();
                    cs.forceInvalid(ENTRY_REQUIRED_MSG);
                    cs.expand();
                    throw new CmExceptionValidationFailed();
                }
            }

            ComboBox<ChapterModel> cc = chapCombo;
            chap = cc.getValue();
            cc.clearInvalid();
            if (chap != null) {
                prog = prog + " " + chap.getNumber();
            }
            
            if(validate) {
                if (studyProgExt.isNeedsChapters() && chap == null) {
                    cc.focus();
                    cc.forceInvalid(ENTRY_REQUIRED_MSG);
                    cc.expand();
                    throw new CmExceptionValidationFailed();
                }
            }
        } else {
            ComboBox<StudyProgramExt> cb = cstmCombo;
            studyProgExt = cb.getValue();
            cb.clearInvalid();
            if(validate) {
                if (studyProgExt == null) {
                    cb.focus();
                    cb.forceInvalid(ENTRY_REQUIRED_MSG);
                    cb.expand();
                    throw new CmExceptionValidationFailed();
                }
                prog = studyProgExt.getShortTitle();
            }
        }

        if (passPercentReqd && (passPercent == null || Integer.parseInt(passPercent.substring(0, passPercent.length() - 1)) == 0)) {
            // set pass percent to default value
            // TODO: (?) allow Admin to specify default pass percent (account,
            // group, program)
            ComboBox<PassPercent> passCombo = new PassPercentCombo(passPercentReqd);
            passPercent = passCombo.getStore().get(PassPercentCombo.DEFAULT_PERCENT_IDX).getPercent();
        }

        /**
         * Collect all the values and create a new StudentModel to hold
         * validated values.
         * 
         */
        StudentModel sm = new StudentModel();
        sm.setName(name);
        sm.setPasscode(passcode);
        // sm.setEmail(email);
        sm.getProgram().setProgramDescription(prog);
        sm.setGroupId(groupId);
        if (stuSettingsMdl != null)
            sm.setSettings(stuSettingsMdl);
        sm.setGroup(group);
        sm.setAdminUid(cmAdminMdl.getUid());
        sm.setPassPercent(passPercent);
        sm.setSectionNum(activeSection);

        String chapTitle = (chap != null) ? chap.getTitle() : null;
        sm.setChapter(chapTitle);

        String progId = (studyProgExt != null) ? (String) studyProgExt.getShortTitle() : null;
        String subjId = (sub != null) ? sub.getAbbrev() : "";

        /**
         * Why is this constructed here? Why not pass in the StudyProgramModel
         * (Ext)
         * 
         * Perhaps, have a .convert on StudyProgramModelExt.
         * 
         */
        StudentProgramModel program = sm.getProgram();
        program.setProgramId((isNew == false) ? stuMdl.getProgram().getProgramId() : null);
        program.setProgramType(progId);
        program.setSubjectId(subjId);

        if(studyProgExt != null) {
            program.setCustom(new CustomProgramComposite(studyProgExt.getCustomProgramId(), studyProgExt.getCustomProgramName(), studyProgExt.getCustomQuizId(),
                    studyProgExt.getCustomQuizName()));
        }

        /**
         * Validation complete
         * 
         * if callback has been provided, then jump to call back
         * 
         */
        if (callback != null) {
            callback.afterValidation(sm);
            return;
        }

        /**
         * If callback not provided, then perform default operation
         * 
         * @TODO: all this logic about what is updated should be on the server
         *        the client should only have to update the POJO and say go.
         * 
         */
        if (isNew) {
            sm.setSectionNum((activeSection != null) ? activeSection : 0);
            sm.setStatus("Not started");
            sm.setTotalUsage(0);
            addUserRPC(sm);
        } else {
            Boolean stuChanged = false;
            Boolean passcodeChanged = false;
            Boolean passPercentChanged = false;
            Boolean progChanged = false;
            Boolean progIsNew = false;

            sm.setUid(stuMdl.getUid());
            sm.getProgram().setProgramId(stuMdl.getProgram().getProgramId());
            sm.setJson(stuMdl.getJson());
            sm.setStatus(stuMdl.getStatus());
            sm.setProgramChanged(false);
            if (!name.equals(stuMdl.getName()) || !(groupId > 0 && groupId == stuMdl.getGroupId())) {
                stuChanged = true;
            }
            if (!passcode.equals(stuMdl.getPasscode())) {
                passcodeChanged = true;
                stuChanged = true;
            }

            Integer prevSectionNum = stuMdl.getSectionNum();
            boolean sectionNumChanged = false;
            if ((prevSectionNum == null && activeSection != null) || (prevSectionNum != null && activeSection == null)
                    || (prevSectionNum != null && !prevSectionNum.equals(activeSection))) {
                sm.setSectionNum(activeSection);
                stuChanged = true;
                sectionNumChanged = true;
            } else {
                sm.setSectionNum(prevSectionNum);
            }

            if (isDifferentProgram(stuMdl.getProgram(), sm.getProgram())) {
                sm.setStatus("Not started");
                if (sectionNumChanged == false)
                    sm.setSectionNum(0);
                sm.setProgramChanged(true);
                // don't know what the program Id will be so set to null
                sm.getProgram().setProgramId(null);

                progIsNew = true;
                progChanged = false;
                sectionNumChanged = false;
                stuChanged = true;
            }

            if (!stuChanged && settingsChanged(stuMdl.getSettings(), stuSettingsMdl)) {
                stuChanged = true;
            }

            if (valueChanged(stuMdl.getPassPercent(), passPercent))
                passPercentChanged = !sm.getProgramChanged();

            if (stuChanged || progChanged || progIsNew || passPercentChanged) {
                updateUserRPC(sm, stuChanged, progChanged, progIsNew, passcodeChanged, passPercentChanged, sectionNumChanged);
            } else {
                if (_window != null) {
                    _window.close();
                } else {
                    CmMessageBox.showAlert("Save complete");
                }
            }
        }
    }

	@SuppressWarnings("rawtypes")
    private void checkValid(Field field) throws Exception {
        if(!field.isValid()) {
            throw new Exception("Field not valid: " + field);
        }
    }
    
	private boolean isDifferentProgram(StudentProgramModel origProg, StudentProgramModel newProg) {

		if (origProg.getProgramDescription() == null) return true;

		boolean isDifferent = false;
		if (origProg.isCustom() == false) {
			isDifferent = (origProg.getProgramDescription().equals(newProg.getProgramDescription()) == false);
		} else {
			if (newProg.isCustom() == false) {
				isDifferent = false;
			}
			CustomProgramComposite origCustom = origProg.getCustom();
			CustomProgramComposite newCustom = newProg.getCustom();
			if (origCustom.getType() != newCustom.getType())
				isDifferent = true;
			else {
				switch (origCustom.getType()) {
				case LESSONS:
					isDifferent = (origCustom.getCustomProgramName().trim().equals(newCustom.getCustomProgramName().trim()) == false);
					break;
				case QUIZ:
					isDifferent = (origCustom.getCustomQuizName().trim().equals(newCustom.getCustomQuizName().trim()) == false);
					break;
				}
			}
		}

		return isDifferent;

	}

    private boolean settingsChanged(StudentSettingsModel origValue, StudentSettingsModel newValue) {
        if (origValue == null && newValue != null)
            return true;

        if (origValue != null && newValue == null)
            return true;

        if (origValue == null && newValue == null)
            return false;

        return (!origValue.getLimitGames() == newValue.getLimitGames() || !origValue.getShowWorkRequired() == newValue.getShowWorkRequired()
                || !origValue.getStopAtProgramEnd() == newValue.getStopAtProgramEnd() || !origValue.getTutoringAvailable() == newValue.getTutoringAvailable()
                || !origValue.getDisableCalcAlways() == newValue.getDisableCalcAlways() || !origValue.getDisableCalcQuizzes() == newValue
                .getDisableCalcQuizzes());
    }

    private boolean valueChanged(String origValue, String newValue) {
        if (origValue == null && newValue != null)
            return true;

        if (origValue != null && !origValue.equals(newValue))
            return true;

        if (origValue != null && origValue.equals(newValue))
            return false;

        return false;
    }

    @Override
    public void beginStep() {
        inProcessCount++;
    }

    @Override
    public void completeStep() {
        inProcessCount--;
    }

    @Override
    public void finish() {
        setComboBoxSelections();
    }

    public static void startTest() {
        StudentModel cm = null;new StudentModel();
        CmAdminModel adminM = new CmAdminModel();
        adminM.setUid(2);
        new RegisterStudent(cm,  adminM).showWindow();
    }
    
}

abstract class AdvOptCallback {
    abstract void setAdvancedOptions(AdvancedOptionsModel options);
}

