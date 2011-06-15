package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.model.CmProgramType;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.CustomProgramComposite;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentSettingsModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramExt;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.RetryAction;
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

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.Timer;

/**
 * Provides UI for registering new students and modifying the registration of
 * existing students.
 * 
 * @author bob
 *
 */

public class RegisterStudent extends LayoutContainer implements ProcessTracker {
	
	protected CmWindow _window;
	
	private boolean isNew;
	private boolean skipComboSet;
	private boolean loading;
	private boolean passPercentReqd;
	private Boolean sectionSelectAvail = false;
	private Integer sectionCount;
	private Integer activeSection;
	
	private StudentModelI stuMdl;
	private StudentSettingsModel stuSettingsMdl;
	protected CmAdminModel cmAdminMdl;
	private AccountInfoModel acctInfoMdl;
	private int inProcessCount;
	private String subjectId;
	
	private ListStore <StudyProgramExt> progStore;
	private ComboBox<StudyProgramExt> progCombo;
	
	private ListStore <SubjectModel> subjStore;
	private ComboBox<SubjectModel> subjCombo;
	
	private ListStore <ChapterModel> chapStore;
	private ComboBox <ChapterModel> chapCombo;
	
	static private ListStore <GroupInfoModel> __groupStore;
	private ComboBox <GroupInfoModel> groupCombo;
	
	private TextField<String> userName;
	
	//private Map<String, Object> advOptionsMap;
	
	private int formHeight = 410;
	protected int formWidth  = 475;
	
	protected CombinedFormPanel _formPanel;
	private Button advOptionsBtn;
	
	public static final String ENTRY_REQUIRED_MSG = "This field is required";
	
	public RegisterStudent(StudentModelI sm, CmAdminModel cm) {
	    
	    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
	    
		inProcessCount = 0;
		isNew = (sm == null);
		stuMdl = sm;
		if (stuMdl != null) {
			subjectId = stuMdl.getProgram().getSubjectId();
			passPercent = stuMdl.getPassPercent();
			stuSettingsMdl = stuMdl.getSettings();
			activeSection = stuMdl.getSectionNum();
			sectionCount = stuMdl.getSectionCount();
			sectionSelectAvail = isSectionSelectAvail(stuMdl.getProgram().getProgramType());
		}

		cmAdminMdl = cm;
		_window = new CmWindow();
		_window.addListener(Events.Hide, new Listener<BaseEvent>() {
		    public void handleEvent(BaseEvent be) {
		        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_CLOSED));
		        _groupSelector.release();
		    }
		});
		_window.add(createForm());
 	     
        skipComboSet = isNew;

		setComboBoxSelections();
	}

	public void showWindow() {
        _window.show();
        if (isNew) {
           userName.focus();
        }
	}

	/** Return list of Buttons to add to the Button bar
	 * 
	 * @return
	 */
	protected List<Button> getActionButtons() {
	    List<Button> list = new ArrayList<Button>();
        Button cancelBtn = cancelButton();
        cancelBtn.addStyleName("register-student-cancel");
        list.add(saveButton(_fsProgram, _formPanel));
        list.add(cancelButton());
        for(int i=0;i<list.size();i++) {
            list.get(i).setWidth(75);
        }
        return list;
	}
	
	public FieldSet _fsProfile, _fsProgram;

	GroupSelectorWidget _groupSelector;

	protected FormPanel createForm() {
		_formPanel = new CombinedFormPanel();
		_formPanel.addStyleName("register-student-form-panel");
		_formPanel.setLabelWidth(120);
		_formPanel.setHeight(formHeight);
		_formPanel.setFooter(true);
		_formPanel.setFrame(false);
		_formPanel.setHeaderVisible(false);
		_formPanel.setBodyBorder(false);
		_formPanel.setIconStyle("icon-form");
		_formPanel.setButtonAlign(HorizontalAlignment.CENTER);
		_formPanel.setLayout(new FormLayout());
		//fp.getLayout();

		_fsProfile = new FieldSet();
		FormLayout fL = new FormLayout();
		fL.setLabelWidth(_formPanel.getLabelWidth());
        fL.setDefaultWidth(295);
        _fsProfile.setLayout(fL);
	    
        _fsProfile.setHeading("Define Profile");
        userName = new TextField<String>();  
        userName.setFieldLabel("Name");
        userName.setAllowBlank(false);
        userName.setId("name");
        userName.setEmptyText("-- enter name --");
		if (! isNew) {
		    userName.setValue((String)stuMdl.getName());
		}
		_fsProfile.add(userName);
		
	    TextField<String> passCode = new TextField<String>();
		passCode.setFieldLabel("Passcode");
		passCode.setEmptyText("-- enter passcode --");
		passCode.setAllowBlank(false);
		passCode.setId("passcode");
		if (! isNew) {
			passCode.setValue((String)stuMdl.getPasscode());
		}
		_fsProfile.add(passCode);
		
		if(__groupStore == null) {
            __groupStore = new ListStore <GroupInfoModel> ();
		}
		
		_groupSelector = new GroupSelectorWidget(cmAdminMdl, __groupStore, true, this, "group-combo", true);
		groupCombo = _groupSelector.groupCombo();
		if(UserInfo.getInstance() == null || !UserInfo.getInstance().isSingleUser()) {
		    _fsProfile.add(groupCombo);
		}

		_formPanel.add(_fsProfile);

        _fsProgram = new FieldSet();
        _fsProgram.setHeading("Assign Program");
        _fsProgram.addStyleName("register-student-fieldset");
		
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(_formPanel.getLabelWidth());
		fl.setDefaultWidth(fL.getDefaultWidth());
		
		_fsProgram.setLayout(fl);
		
		progStore = new ListStore <StudyProgramExt> ();
		getStudyProgramListRPC(progStore);
		progCombo = programCombo(progStore, _fsProgram);
		_fsProgram.add(progCombo);
		
		subjStore = new ListStore <SubjectModel> ();
		getSubjectList((stuMdl != null)?stuMdl.getProgram().getProgramType().getType():null, subjStore);
		subjCombo = subjectCombo(subjStore);
		_fsProgram.add(subjCombo);

		chapStore = new ListStore <ChapterModel> ();
        getChapterListRPC((stuMdl != null)?stuMdl.getProgram().getProgramType().getType():null, subjectId, false, chapStore);
		chapCombo = chapterCombo(chapStore);
		_fsProgram.add(chapCombo);        

		getAccountInfoRPC(cmAdminMdl.getId());

		advOptionsBtn = advancedOptionsBtn();
		advOptionsBtn.disable();
	    _fsProgram.add(advOptionsBtn);
	    
        _formPanel.add(_fsProgram);

        _window.setHeading((isNew)?"Register a New Student":"Edit Student");
        _window.setWidth(formWidth + 40);
        _window.setHeight(formHeight + 20);
        _window.setLayout(new FitLayout());
        _window.setResizable(false);
        _window.setDraggable(true);
        _window.setModal(true);

        /** Assign buttons to the button bar on the Window
         * 
         */
        _formPanel.setButtonAlign(HorizontalAlignment.RIGHT);
        for(Button btn: getActionButtons()) {
            btn.addStyleName("register-student-btn");
            _window.addButton(btn);
        }
        
        /** Seems like a bug with setting focus, so the only way to 
         *  it to work is to set a timer and hope ... 
         *  
         *  It seems the form is validating before it needs to and showing error 
         *  messages before any input has been added .. which removes the focus
         *  
         *  @TODO: get name.focus() to just work without the need for timing tricks.
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

	private String  passPercent;

	private Button advancedOptionsBtn() {
		Button btn = new Button("Advanced Options");
		btn.setToolTip("Disallow games, Change pass percentage, etc.");
		btn.setWidth("110px");
        btn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                AdvOptCallback callback = new AdvOptCallback() {
					@Override
					void setAdvancedOptions(Map<String, Object> optionMap) {
						stuSettingsMdl = (StudentSettingsModel) optionMap.get(StudentModelExt.SETTINGS_KEY);
						passPercent = (String) optionMap.get(StudentModelExt.PASS_PERCENT_KEY);
						activeSection = (Integer) optionMap.get(StudentModelExt.SECTION_NUM_KEY);
					}
                };
                final Map<String,Object>advOptionsMap = new HashMap <String,Object> ();
                final StudentSettingsModel ssm = new StudentSettingsModel();

                /** only set options if not null */
                if(stuSettingsMdl != null) {
                    ssm.setLimitGames(stuSettingsMdl.getLimitGames());
                    ssm.setShowWorkRequired(stuSettingsMdl.getShowWorkRequired());
                    ssm.setStopAtProgramEnd(stuSettingsMdl.getStopAtProgramEnd());
                    ssm.setTutoringAvailable(stuSettingsMdl.getTutoringAvailable());
                }
                else {
                	/** use account data to set tutoring available */
                	if (acctInfoMdl != null) {
                	    ssm.setTutoringAvailable(acctInfoMdl.getIsTutoringEnabled());
                	}
                }
                advOptionsMap.put(StudentModelExt.PASS_PERCENT_KEY, passPercent);
                advOptionsMap.put(StudentModelExt.SETTINGS_KEY, ssm);

                advOptionsMap.put(StudentModelExt.SECTION_COUNT_KEY, sectionCount);
                if (activeSection == null) activeSection = new Integer(0);
                advOptionsMap.put(StudentModelExt.SECTION_NUM_KEY, activeSection);
                advOptionsMap.put("section-is-settable", sectionSelectAvail);

                new RegisterStudentAdvancedOptions(callback, cmAdminMdl, advOptionsMap, isNew, passPercentReqd).setVisible(true);              
            }
        });
		return btn;
	}

	private boolean isSectionSelectAvail(CmProgramType type) {
		return (type == CmProgramType.PROF             ||
				type == CmProgramType.GRADPREP         ||
				type == CmProgramType.GRADPREPNATIONAL ||
				type == CmProgramType.GRADPREPTX);
	}

	private ComboBox<StudyProgramExt> programCombo(ListStore<StudyProgramExt> store, final FieldSet fs) {
		ComboBox<StudyProgramExt> combo = new ComboBox<StudyProgramExt>();
		combo.setFieldLabel("Program");
		combo.setForceSelection(true);
		combo.setDisplayField("title");
		combo.setEditable(false);
		combo.setMaxLength(45);
		combo.setAllowBlank(false);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setStore(store);
		combo.setTemplate(getProgramTemplate());
		combo.setTitle("Select a program");
		combo.setId("prog-combo");
		combo.setTypeAhead(true);
		combo.setSelectOnFocus(true);
		combo.setEmptyText("-- select a program --");
		combo.setWidth(280);

	    combo.addSelectionChangedListener(new SelectionChangedListener<StudyProgramExt>() {
	        @SuppressWarnings("unchecked")
			public void selectionChanged(SelectionChangedEvent<StudyProgramExt> se) {

	        	if (loading) return;

	            StudyProgramExt sp = se.getSelectedItem();
				int needsSubject = ((Integer)sp.get("needsSubject")).intValue();
				int needsChapters = ((Integer)sp.get("needsChapters")).intValue();
				passPercentReqd = ((Integer)sp.get("needsPassPercent")).intValue() > 0;
				
				sectionSelectAvail = sp.isGradPrep() || sp.isProficiency();

				sectionCount = sp.getSectionCount();
				activeSection = 1;

				advOptionsBtn.enable();

	        	ComboBox <SubjectModel> cb = (ComboBox<SubjectModel>) fs.getItemByItemId("subj-combo");

	        	skipComboSet = true;
	        	subjectId = null;

	        	if (needsSubject > 0) {
	        		cb.clearSelections();
	        		cb.enable();
	        		cb.setForceSelection(true);
	        	    getSubjectList((String)sp.get("shortTitle"), subjStore);
	        	}
	        	else {
	        		cb.clearInvalid();
	        		cb.clearSelections();
	        		cb.disable();
	        		cb.setForceSelection(false);
	        		subjectId = null;
	        	}
	        	ComboBox <ChapterModel> cc = (ComboBox<ChapterModel>) fs.getItemByItemId("chap-combo");
	        	if (needsChapters > 0) {
	        		cc.clearSelections();
	        		cc.enable();
	        		cc.setForceSelection(true);
	        	}
	        	else {
	        		cc.clearInvalid();
	        		cc.clearSelections();
	        		cc.disable();
	        		cc.setForceSelection(false);
	        	}
	        }
	    });

	    return combo;
	}

	private native String getProgramTemplate() /*-{ 
	    return  [ 
	   '<tpl for=".">', 
	   '<div class="x-combo-list-item {styleIsCustomProgram}" qtip="{descr}">{label}</div>', 
	   '</tpl>' 
	   ].join(""); 
	   }-*/;  

	private ComboBox<SubjectModel> subjectCombo(ListStore<SubjectModel> store) {
		ComboBox<SubjectModel> combo = new ComboBox<SubjectModel>();
		combo.setFieldLabel("Subject");
		combo.setForceSelection(false);
		combo.setDisplayField("subject");
		combo.setEditable(false);
		combo.setMaxLength(30);
		combo.setAllowBlank(false);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setStore(store);
		combo.setTitle("Select a subject");
		combo.setId("subj-combo");
		combo.setTypeAhead(true);
		combo.setSelectOnFocus(true);
		combo.setEmptyText("-- select a subject --");
		combo.disable();
		combo.setWidth(280);
		
	    combo.addSelectionChangedListener(new SelectionChangedListener<SubjectModel>() {
			@SuppressWarnings("unchecked")
			public void selectionChanged(SelectionChangedEvent<SubjectModel> se) {

				if (loading) return;

	        	SubjectModel sm = se.getSelectedItem();
	        	if (subjectId == null || ! subjectId.equals(sm.getAbbrev())) {
	        	    try {
	    	        	skipComboSet = true;
    		        	subjectId = sm.getAbbrev();
    		        	ComboBox<StudyProgramExt> cb = (ComboBox<StudyProgramExt>) _formPanel.getItemByItemId("prog-combo");
    		        	StudyProgramExt sp = cb.getValue();
    		        	String progId = sp.get("shortTitle");
    		        	chapStore.removeAll();
    		            getChapterListRPC(progId, subjectId, true, chapStore);
	        	    }
	        	    catch(Exception e) {
	        	        e.printStackTrace();
	        	        CatchupMathTools.showAlert(e.getMessage());
	        	    }
	        	}
	        }
	    });
		return combo;
	}

	private ComboBox<ChapterModel> chapterCombo(ListStore<ChapterModel> store) {
		ComboBox<ChapterModel> combo = new ComboBox<ChapterModel>();
		combo.setFieldLabel("Chapter");
		combo.setForceSelection(false);
		combo.setDisplayField("chapter");
		combo.setEditable(false);
		combo.setMaxLength(160);
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

	private Button cancelButton() {
		Button cancelBtn = new Button("Cancel", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {
	    	    _window.close();
	        }  
	    });
		return cancelBtn;
	}

	private Button saveButton(final FieldSet fs, final CombinedFormPanel fp) {
		Button saveBtn = new Button("Save", new SelectionListener<ButtonEvent>() {  
			@Override  
	    	public void componentSelected(ButtonEvent cx) {
	            try {
	                doSubmitAction(fs, fp, null);
	            }
	            catch(CmException cm) {
	                cm.printStackTrace();
	            }
	        }
	    });
		return saveBtn;
	}
	
	private void getStudyProgramListRPC(final ListStore <StudyProgramExt> progStore) {

		inProcessCount++;

		
		new RetryAction<CmList<StudyProgramModel>>() {
		    @Override
		    public void attempt() {
		        GetProgramDefinitionsAction action = new GetProgramDefinitionsAction(cmAdminMdl.getId());
		        setAction(action);
		        CmShared.getCmService().execute(action, this);
		    }
            public void oncapture(CmList<StudyProgramModel> spmList) {
                List<StudyProgramExt> progList = new ArrayList <StudyProgramExt> ();
                for (StudyProgramModel spm : spmList) {
                    progList.add(new StudyProgramExt(spm, spm.getTitle(), spm.getShortTitle(), spm.getDescr(), 
                                                  spm.getNeedsSubject(), spm.getNeedsChapters(), spm.getNeedsPassPercent(),
                                                  spm.getCustomProgramId(), spm.getCustomProgramName()));
                }
                progStore.add(progList);
                inProcessCount--;
                setComboBoxSelections();
            }
        }.register();
	}

	private Map<String, List<SubjectModel>> programSubjectMap = new HashMap<String, List<SubjectModel>>();
	
	private void getSubjectList(final String progId, final ListStore <SubjectModel> subjStore) {

		if (progId == null) return;

		List<SubjectModel> subjList = programSubjectMap.get(progId);
		if (subjList != null) {
			subjStore.removeAll();
			subjStore.add(subjList);
			setComboBoxSelections();
		}
		else {
			getSubjectListRPC(progId, subjStore);
		}
	}
	
	private void getSubjectListRPC(final String progId, final ListStore <SubjectModel> subjStore) {

		inProcessCount++;
		
		
		new RetryAction <CmList<SubjectModel>>() {

		    @Override
		    public void attempt() {
		        GetSubjectDefinitionsAction action = new GetSubjectDefinitionsAction(progId);
		        setAction(action);
		        CmShared.getCmService().execute(action,this);		        
		    }
            public void oncapture(CmList<SubjectModel> result) {
                subjStore.removeAll();
                subjStore.add(result);
                inProcessCount--;
                programSubjectMap.put(progId, result);
                setComboBoxSelections();
            }
        }.register();
		
	}
		
	protected void addUserRPC(final StudentModel sm) {
	    
	    /** execute outside the RetryManager
	     *  to allow processing exceptions.
	     */
		new RetryAction <StudentModelI> () {
		    @Override
		    public void attempt() {
		        CmBusyManager.setBusy(true);
		        AddStudentAction action = new AddStudentAction(sm);
		        setAction(action);
		        CmShared.getCmService().execute(action,this);
		    }
            public void oncapture(StudentModelI ai) {
                CmBusyManager.setBusy(false);
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_PROGRAM_CHANGED,ai.getProgramChanged()));
                _window.close();
            }
            
            @Override
            public void onFailure(Throwable caught) {
                CmBusyManager.setBusy(false);
                if(caught.getMessage().indexOf("already in use") > -1) {
                    CatchupMathTools.showAlert("Password In Use", "This password is currently in use");
                    return;
                }
                super.onFailure(caught);
            }
        }.attempt();
	}

	protected void updateUserRPC(final StudentModel sm, final Boolean stuChanged, final Boolean progChanged, final Boolean progIsNew,
	        final Boolean passcodeChanged, final Boolean passPercentChanged) {
		new RetryAction<StudentModelI> () {
		    @Override
		    public void attempt() {
		        CmBusyManager.setBusy(true);
		        UpdateStudentAction action = new UpdateStudentAction(sm,stuChanged,progChanged,progIsNew,passcodeChanged,passPercentChanged);
		        setAction(action);
		        CmShared.getCmService().execute(action,this);
		    }
            public void oncapture(StudentModelI ai) {
                CmBusyManager.setBusy(false);
                _window.close();
                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_PROGRAM_CHANGED,ai.getProgramChanged()));
            }
        }.register();
	}
	
	private void setComboBoxSelections() {
		if (this.stuMdl != null && !skipComboSet && inProcessCount < 1) {

			loading = true;
			
			setGroupSelection();
			
    		StudyProgramExt sp = setProgramSelection();

    		if (sp == null) {
    			CatchupMathTools.showAlert("Program not found!");
    			loading = false;
    			return;
    		}
    		int needsSubject = ((Integer)sp.get("needsSubject")).intValue();
    		if (needsSubject != 0) setSubjectSelection();

    		int needsChapters = ((Integer)sp.get("needsChapters")).intValue();
    		if (needsChapters != 0) setChapterSelection();

    		passPercentReqd = ((Integer)sp.get("needsPassPercent")).intValue() > 0;

            advOptionsBtn.enable();

            loading = false;
		}
	}

	private void setGroupSelection() {
	    if(stuMdl == null) {
	        new Exception().printStackTrace();
	        return;
	    }
	    
		Integer groupId = Integer.parseInt(stuMdl.getGroupId());
		if (groupId != null) {
			List<GroupInfoModel> l = __groupStore.getModels();
			for (GroupInfoModel g : l) {
			    Integer gi = g.getId();
				if (groupId.equals(gi)) {
					groupCombo.setOriginalValue(g);
					groupCombo.setValue(g);
				}
			}
		}
	}
	
	private StudyProgramExt setProgramSelection() {
	    StudentProgramModel program = stuMdl.getProgram();
		String shortName = program.getProgramType().getType();
		
		if(program.getCustom().isCustom()) {
		    shortName = program.getCustom().getCustomName();
		}
		
		if (shortName != null) {
			List<StudyProgramExt> list = progStore.getModels();
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
	
	/** perform hack to determine correct program.
	 * 
	 * @NOTE: add a way to identify programs correctly.
	 * 
	 * @param shortName
	 * @param sp
	 * @return
	 */
	private boolean progNameCheckHack(String shortName, StudyProgramExt sp) {
        String st=((String) sp.get("shortTitle")).toLowerCase();
        
        if(sp.get("customProgramName") != null && sp.get("customProgramName").equals(shortName)) {
            return true;
        }
        
        if(sp.get("customQuizName") != null && sp.get("customQuizName").equals(shortName)) {
            return true;
        }
        
        if(st.equals("chap") && shortName.toLowerCase().indexOf(" chap") > -1) {
            return true;
        }
        else if(st.equals("prof") && shortName.toLowerCase().indexOf(" prof") > -1)
            return true;
        else {
            return shortName.equalsIgnoreCase(st);
        }
	}
	
	private void setSubjectSelection() {
		String shortName = stuMdl.getProgram().getProgramDescription();

		if (shortName != null) {
			List<SubjectModel> list = subjStore.getModels();
			for (SubjectModel s : list) {
				if (shortName.indexOf((String) s.get("abbrev")) > -1) {
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
			List <ChapterModel> list = chapStore.getModels();
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
	
	private void getChapterListRPC(final String progId, final String subjId, final Boolean chapOnly, final ListStore <ChapterModel> chapStore) {

		if (progId == null || !progId.equalsIgnoreCase("chap")) return;
		
		inProcessCount++;
		new RetryAction <CmList<ChapterModel>> () {
		    
		    @Override
		    public void attempt() {
		        CmServiceAsync s = CmShared.getCmService();
		        GetChaptersForProgramSubjectAction action = new GetChaptersForProgramSubjectAction(progId, subjId);
		        setAction(action);
		        s.execute(action, this);
		    }

            public void oncapture(CmList<ChapterModel> result) {
                chapStore.add(result);
                inProcessCount--;
                if (! chapOnly)
                    setComboBoxSelections();
                else {
                    chapCombo.setOriginalValue(null);
                    chapCombo.setValue(null);
                    chapCombo.clearSelections();
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
                s.execute(action,this);
            }

            public void oncapture(AccountInfoModel ai) {
                acctInfoMdl = ai;
            }
        }.register();        
    }
    
	/** Perform the form save operation and display any required validation.
	 * 
	 * Throws CmExeptionValidationFailed on failed validation attempt.
	 * 
	 * if callback == null, then default operation, saving data, is performed.
	 * If callback is provided, then after validation of form callback is called.
	 * 
	 * 
	 * @TODO: separate into validation/action.  Perhaps strategy pattern.
	 * 
	 * @param fs
	 * @param fp
	 */
	@SuppressWarnings("unchecked")
	protected void doSubmitAction(final FieldSet fs, final CombinedFormPanel fp, AfterValidation callback) throws CmException {
	    
	    TextField<String> tf = (TextField<String>)fp.getItemByItemId("name");
	    String name="";
        if(tf != null) {
            tf.clearInvalid();
            name = tf.getValue();
            if (name == null) {
                tf.focus();
                tf.forceInvalid(ENTRY_REQUIRED_MSG);
                throw new CmExceptionValidationFailed();
            }
        }
        
        String passcode = null;
        tf = (TextField<String>)fp.getItemByItemId("passcode");
        if(tf != null) {
            tf.clearInvalid();
            passcode = tf.getValue();
            if (passcode == null) {
                tf.focus();
                tf.forceInvalid(ENTRY_REQUIRED_MSG);
                throw new CmExceptionValidationFailed();
            }
            else {
                if(passcode.contains(" ")) {
                    tf.focus();
                    tf.forceInvalid("Password cannot contain spaces");
                    throw new CmExceptionValidationFailed();
                }

            }
        }

        String groupId=null;
        String group=null;
        ComboBox<GroupInfoModel> cg = (ComboBox<GroupInfoModel>) fp.getItemByItemId("group-combo");
        if(cg != null) {
            cg.clearInvalid();
            if(cg != null) {
                GroupInfoModel g = cg.getValue();
                if (g == null) {
                    cg.focus();
                    cg.forceInvalid(ENTRY_REQUIRED_MSG);
                    cg.expand();
                    throw new CmExceptionValidationFailed();
                }
                groupId = g.getId().toString();
                group = g.getName();
            }
            else {
                groupId = "1";
                group = "none";
            }
        }
        
        ComboBox<StudyProgramExt> cb = (ComboBox<StudyProgramExt>) fs.getItemByItemId("prog-combo");
        StudyProgramExt studyProgExt = cb.getValue();
        cb.clearInvalid();
        if (studyProgExt == null) {
            cb.focus();
            cb.forceInvalid(ENTRY_REQUIRED_MSG);
            cb.expand();
            throw new CmExceptionValidationFailed();
        }
        String prog = studyProgExt.get("shortTitle");

        ComboBox<SubjectModel> cs = (ComboBox<SubjectModel>) fs.getItemByItemId("subj-combo");
        SubjectModel sub = cs.getValue();
        cs.clearInvalid();
        if (sub != null) {
            prog = sub.get("abbrev") + " " + prog;
        }
        if (((Integer)studyProgExt.get("needsSubject")).intValue() > 0 && sub == null) {
            cs.focus();
            cs.forceInvalid(ENTRY_REQUIRED_MSG);
            cs.expand();
            throw new CmExceptionValidationFailed();
        }

        ComboBox<ChapterModel> cc = (ComboBox<ChapterModel>) fs.getItemByItemId("chap-combo");
        ChapterModel chap = cc.getValue();
        cc.clearInvalid();
        if (chap != null) {
            prog = prog + " " + chap.get("number");
        }
        if (((Integer)studyProgExt.get("needsChapters")).intValue() > 0 && chap == null) {
            cc.focus();
            cc.forceInvalid(ENTRY_REQUIRED_MSG);
            cc.expand();
            throw new CmExceptionValidationFailed();
        }

        if (passPercentReqd &&
        	(passPercent == null || Integer.parseInt(passPercent.substring(0,passPercent.length()-1)) == 0)) {
        	// set pass percent to default value
        	// TODO: (?) allow Admin to specify default pass percent (account, group, program)
            ComboBox <PassPercent> passCombo = new PassPercentCombo(passPercentReqd);
        	passPercent = passCombo.getStore().getAt(PassPercentCombo.DEFAULT_PERCENT_IDX).getPassPercent();
        }
        
        /** Collect all the values and create a new StudentModel
         *  to hold validated values.
         *  
         */
        StudentModel sm = new StudentModel();
        sm.setName(name);
        sm.setPasscode(passcode);
        //sm.setEmail(email);
        sm.getProgram().setProgramDescription(prog);
        sm.setGroupId(groupId);
        if(stuSettingsMdl != null)
            sm.setSettings(stuSettingsMdl);
        sm.setGroup(group);
        sm.setAdminUid(cmAdminMdl.getId());
        sm.setPassPercent(passPercent);
        sm.setSectionNum(activeSection);

        String chapTitle = (chap != null) ? chap.getTitle() : null;
        sm.setChapter(chapTitle);

        String progId = (studyProgExt != null) ? (String)studyProgExt.get("shortTitle") : null;        
        String subjId = (sub != null) ? sub.getAbbrev() : "";

        
        /** Why is this constructed here?
         *  Why not pass in the StudyProgramModel (Ext)
         *  
         *  Perhaps, have a .convert on StudyProgramModelExt.
         *  
         */
        StudentProgramModel program = sm.getProgram();
        program.setProgramId((isNew == false)?stuMdl.getProgram().getProgramId():null);
        program.setProgramType(progId);
        program.setSubjectId(subjId);
        
        program.setCustom(new CustomProgramComposite(
                studyProgExt.getCustomProgramId(),studyProgExt.getCustomProgramName(),
                studyProgExt.getCustomQuizId(),studyProgExt.getCustomQuizName()));


        /** Validation complete 
         * 
         * if callback has been provided, then jump to call back
         * 
         */
        if (callback != null) {
            callback.afterValidation(sm);
            return;
        }
        
        /** If callback not provided, then perform default operation
         * 
         * @TODO: all this logic about what is updated should be on the server
         *        the client should only have to update the POJO and say go.
         * 
         */
        if (isNew) {
            sm.setSectionNum( (activeSection != null) ? activeSection : 0);
            sm.setStatus("Not started");
            sm.setTotalUsage(0);
            addUserRPC(sm);
        }
        else {
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
            if (! name.equals(stuMdl.getName()) ||
                ! (groupId != null && groupId.equals(stuMdl.getGroupId()))) {
                stuChanged = true;
            }
            if (! passcode.equals(stuMdl.getPasscode())) {
                passcodeChanged = true;
                stuChanged = true;
            }

            Integer prevSectionNum = stuMdl.getSectionNum();
            if ( (prevSectionNum == null && activeSection != null) ||
            	 (prevSectionNum != null && activeSection == null) ||
            	 (prevSectionNum != null && ! prevSectionNum.equals(activeSection))) {
                sm.setSectionNum(activeSection);
            	stuChanged = true;
            }
            else {
                sm.setSectionNum(prevSectionNum);
            }
            
            if (stuMdl.getProgram().getProgramDescription() == null || isDifferentProgram(stuMdl,prog)) {
                sm.setStatus("Not started");
                sm.setSectionNum(0);
                sm.setProgramChanged(true);
                // don't know what the program Id will be so set to null
                sm.getProgram().setProgramId(null);

                progIsNew = true;
                progChanged = false;
                stuChanged = true;
            }

            if (! stuChanged && settingsChanged(stuMdl.getSettings(), stuSettingsMdl)) {
            	stuChanged = true;
            }

            if (valueChanged(stuMdl.getPassPercent(), passPercent))
            	passPercentChanged = ! sm.getProgramChanged();

            if (stuChanged || progChanged || progIsNew || passPercentChanged) {
                updateUserRPC(sm, stuChanged, progChanged, progIsNew, passcodeChanged, passPercentChanged);
            }
            else {
                _window.close();
            }
        }	    
	}
	
	private boolean isDifferentProgram(StudentModelI stuMdl, String prog) {
	    if (prog.equals("Custom")) {
	            
	        /** compare the name, maybe ... 
	         * for now always update
	         * 
	         */
	        return true;
	    }
	    else {
	        return !stuMdl.getProgram().getProgramDescription().equals(prog);
	    }
	}

	private boolean settingsChanged(StudentSettingsModel origValue, StudentSettingsModel newValue) {
        if (origValue == null && newValue != null) return true;

        if (origValue != null && newValue == null) return true;

        if (origValue == null && newValue == null) return false;

        return ( ! origValue.getLimitGames() == newValue.getLimitGames() ||
        		 ! origValue.getShowWorkRequired() == newValue.getShowWorkRequired() ||
        		 ! origValue.getStopAtProgramEnd() == newValue.getStopAtProgramEnd() ||
        		 ! origValue.getTutoringAvailable() == newValue.getTutoringAvailable());
	}

	private boolean valueChanged(String origValue, String newValue) {
        if (origValue == null && newValue != null) return true;
        
        if (origValue != null && ! origValue.equals(newValue)) return true;
        
        if (origValue != null && origValue.equals(newValue)) return false;
        
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
}

/** Search for field in nested FieldSets
 * 
 * @author casey
 *
 */
class CombinedFormPanel extends FormPanel {
    public Component getItemByItemId(String itemId) {
        
        Component foundObject = super.getItemByItemId(itemId);
        if(foundObject != null)
            return foundObject;
        
        // search all fieldsets, looking for named item
        for(Component comp: getItems()) {
            if(comp instanceof FieldSet) {
                foundObject = ((FieldSet)comp).getItemByItemId(itemId);
                if(foundObject != null)
                    return foundObject;
            }
        }
        return null;
    }
}

abstract class AdvOptCallback {
	abstract void setAdvancedOptions(Map<String,Object> optionMap);
}

