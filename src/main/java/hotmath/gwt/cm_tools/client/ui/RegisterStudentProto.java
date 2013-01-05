package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.model.AdvancedOptionsModel;

/**
 * Provides UI for registering new students and modifying the registration of
 * existing students.
 * 
 * @author bob
 *
 */

public class RegisterStudentProto {
//	
//	protected GWindow _window;
//	
//	GroupInfoProperties __propsGroupInfo = GWT.create(GroupInfoProperties.class);
//	StudyProgramProperties __propsStudyProgram = GWT.create(StudyProgramProperties.class);
//	SubjectModelProperties __propsSubject = GWT.create(SubjectModelProperties.class);
//	
//	private boolean isNew;
//	private boolean skipComboSet;
//	private boolean loading;
//	private boolean passPercentReqd;
//	private Boolean sectionSelectAvail = false;
//	private Integer sectionCount = 0;
//	private Integer activeSection = 0;
//	
//	private StudentModelI stuMdl;
//	private StudentSettingsModel stuSettingsMdl;
//	protected CmAdminModel cmAdminMdl;
//	private AccountInfoModel acctInfoMdl;
//	private int inProcessCount;
//	private String subjectId;
//	
//	private CardLayoutContainer cardPanel;
//
//	private ListStore <StudyProgramExt> progStore;
//	private ListStore <StudyProgramExt> customProgStore;
//	private ComboBox<StudyProgramExt> progCombo;
//	private ComboBox<StudyProgramExt> cstmCombo;
//	
//	private ListStore <SubjectModel> subjStore;
//	private ComboBox<SubjectModel> subjCombo;
//	
//	private ListStore <ChapterModel> chapStore;
//	private ComboBox <ChapterModel> chapCombo;
//	
//	static private ListStore <GroupInfoModel> __groupStore;
//	private ComboBox <GroupInfoModel> groupCombo;
//	
//	private TextField userName;
//	
//	//private Map<String, Object> advOptionsMap;
//	
//	static final int LAYOUT_WIDTH = 295;
//	static final int CUSTOM_ID = 9999;
//
//	private int formHeight = 385;
//	protected int formWidth  = 475;
//
//    protected ContentPanel _formPanel;
//	private Button stdAdvOptionsBtn;
//	private Button customAdvOptionsBtn;
//	
//	public static final String ENTRY_REQUIRED_MSG = "This field is required";
//	
//	boolean excludeAutoEnroll;
//	
//	public RegisterStudentProto(StudentModelI sm, CmAdminModel cm) {
//        this(sm, cm, false);
//	}
//
//	public RegisterStudentProto(StudentModelI sm, CmAdminModel cm, boolean excludeAutoEnroll) {
//		
//		this.excludeAutoEnroll = excludeAutoEnroll;
//
//	    
//		inProcessCount = 0;
//		isNew = (sm == null);
//		stuMdl = sm;
//		if (stuMdl != null) {
//			subjectId = stuMdl.getProgram().getSubjectId();
//			passPercent = stuMdl.getPassPercent();
//			stuSettingsMdl = stuMdl.getSettings();
//			activeSection = stuMdl.getSectionNum();
//			sectionCount = stuMdl.getSectionCount();
//			sectionSelectAvail = isSectionSelectAvail(stuMdl.getProgram().getProgramType());
//		}
//
//		cmAdminMdl = cm;
//		_window = new GWindow(true);
//		_window.addHideHandler(new HideHandler() {
//            @Override
//            public void onHide(HideEvent event) {
//		        _groupSelector.release();
//		    }
//		});
//		_window.add(createForm());
// 	     
//        skipComboSet = isNew;
//
//		setComboBoxSelections();
//	}
//
//	public void showWindow() {
//        _window.show();
//        if (isNew) {
//           userName.focus();
//        }
//	}
//
//	/** Return list of Buttons to add to the Button bar
//	 * 
//	 * @return
//	 */
//	protected List<TextButton> getActionButtons() {
//	    List<TextButton> list = new ArrayList<TextButton>();
//        TextButton cancelBtn = cancelButton();
//        cancelBtn.addStyleName("register-student-cancel");
//        list.add(saveButton());
//        list.add(cancelButton());
//        for(int i=0;i<list.size();i++) {
//            list.get(i).setWidth(75);
//        }
//        return list;
//	}
//	
//	protected void hideAdvancedOptionsButton() {
//		stdAdvOptionsBtn.hide();
//	}
//	
//	public FieldSet _fsProfile, _fsProgram, _fsStdProg, _fsCustomProg;
//
//	GroupSelectorWidget _groupSelector;
//
//	protected ContentPanel createForm() {
//	    
//	    VerticalLayoutContainer vertMain = new VerticalLayoutContainer();
//		_formPanel = new ContentPanel();
//		_formPanel.addStyleName("register-student-form-panel");
//		//_formPanel.setLabelWidth(120);
//		_formPanel.setHeight(formHeight);
//		_formPanel.setHeaderVisible(false);
//		_formPanel.setBodyBorder(false);
//		//_formPanel.setIconStyle("icon-form");
//		_formPanel.setButtonAlign(BoxLayoutPack.CENTER);
//		_formPanel.setWidget(vertMain);
//
//		_fsProfile = new FieldSet();
//        _fsProfile.setHeadingText("Define Profile");
//		VerticalLayoutContainer vertFsProfile = new VerticalLayoutContainer();
//		_fsProfile.setWidget(vertFsProfile);
//		//fL.setLabelWidth(_formPanel.getLabelWidth());
//        // fL.setDefaultWidth(LAYOUT_WIDTH);
//        
//		vertMain.add(_fsProfile);
//
//        userName = new TextField();  
//        //userName.setFieldLabel("Name");
//        userName.setAllowBlank(false);
//        userName.setId("name");
//        userName.setEmptyText("-- enter name --");
//		if (! isNew) {
//		    userName.setValue((String)stuMdl.getName());
//		}
//		vertFsProfile.add(new FieldLabel(userName, "Name"));
//
//		
//	    TextField passCode = new TextField();
//		//passCode.setFieldLabel("Passcode");
//		passCode.setEmptyText("-- enter passcode --");
//		passCode.setAllowBlank(false);
//		passCode.setId("passcode");
//		if (! isNew) {
//			passCode.setValue((String)stuMdl.getPasscode());
//		}
//		vertFsProfile.add(new FieldLabel(passCode, "Passcode"));
//
//		if(__groupStore == null) {
//            __groupStore = new ListStore <GroupInfoModel> (__propsGroupInfo.id());
//		}
//
//		_groupSelector = new GroupSelectorWidget(cmAdminMdl, __groupStore, true, this, "group-combo", true, __propsGroupInfo.groupName());
//		groupCombo = _groupSelector.groupCombo();
//		if(UserInfo.getInstance() == null || !UserInfo.getInstance().isSingleUser()) {
//		    vertFsProfile.add(new FieldLabel(groupCombo, groupCombo.getTitle()));
//		}
//
//		progStore = new ListStore <StudyProgramExt> (__propsStudyProgram.id());
//		customProgStore = new ListStore <StudyProgramExt> (__propsStudyProgram.id());
//        getStudyProgramListRPC();
//
//		stdAdvOptionsBtn = stdAdvancedOptionsBtn();
//		stdAdvOptionsBtn.disable();
//		customAdvOptionsBtn = customAdvancedOptionsBtn();
//		customAdvOptionsBtn.disable();
//
//		
//		//fl.setLabelWidth(_formPanel.getLabelWidth());
//		//fl.setDefaultWidth(fL.getDefaultWidth());
//
//		_fsProgram = new FieldSet();
//		VerticalLayoutContainer vertFsProgram = new VerticalLayoutContainer();
//		_fsProfile.setWidget(vertFsProgram);
//        _fsProgram.setHeadingText("Assign Program");
//        _fsProgram.addStyleName("register-student-proto2-fieldset");
//		
//		progCombo = programCombo(progStore);
//		vertFsProfile.add(new FieldLabel(progCombo, "Program Type" ));
//		_fsProgram.add(progCombo);
//
//		setupStdProgramUI();
//		setupCustomProgramUI();
//	    
//		getAccountInfoRPC(cmAdminMdl.getUid());
//
//	    cardPanel = new CardLayoutContainer();
//	    
//	    cardPanel.add(_fsStdProg);
//	    cardPanel.add(_fsCustomProg);
//
//	    _fsProgram.add(cardPanel);
//        _formPanel.add(_fsProgram);
//
//        _window.setHeadingText((isNew)?"Register a New Student":"Edit Student");
//        _window.setWidth(formWidth + 40);
//        _window.setHeight(formHeight + 20);
//        _window.setResizable(false);
//        _window.setDraggable(true);
//        _window.setModal(true);
//
//        /**
//         *  Assign buttons to the button bar on the Window
//         */
//        for(TextButton btn: getActionButtons()) {
//            btn.addStyleName("register-student-btn");
//            _window.addButton(btn);
//        }
//        
//        /** Seems like a bug with setting focus, so the only way to 
//         *  it to work is to set a timer and hope ... 
//         *  
//         *  It seems the form is validating before it needs to and showing error 
//         *  messages before any input has been added .. which removes the focus
//         *  
//         *  @TODO: get name.focus() to just work without the need for timing tricks.
//         */
//        if (isNew) {
//            new Timer() {
//                public void run() {
//                    userName.focus();
//                }
//            }.schedule(2000);
//        }
//        return _formPanel;
//	}
//
//	private String  passPercent;
//	
//    private void setupStdProgramUI() {
//        _fsStdProg = new FieldSet();
//        VerticalLayoutContainer vertFsStdProg = new VerticalLayoutContainer();
//        _fsStdProg.setWidget(vertFsStdProg);
//        _fsStdProg.setHeadingText("");
//        _fsStdProg.addStyleName("register-student-inner-fieldset");
//        _fsStdProg.setId("std-prog-fs");
//        
//		//fl.setLabelWidth(_formPanel.getLabelWidth());
//		//fl.setDefaultWidth(LAYOUT_WIDTH);
//		
//		subjStore = new ListStore <SubjectModel> (__propsSubject.id());
//		getSubjectList((stuMdl != null)?stuMdl.getProgram().getProgramType().getType():null, subjStore);
//		subjCombo = subjectCombo(subjStore);
//		_fsStdProg.add(subjCombo);
//
//		chapStore = new ListStore <ChapterModel> (_);
//        getChapterListRPC((stuMdl != null)?stuMdl.getProgram().getProgramType().getType():null, subjectId, false, chapStore);
//		chapCombo = chapterCombo(chapStore);
//		_fsStdProg.add(chapCombo);        
//
//	    _fsStdProg.add(stdAdvOptionsBtn);
//    }
//
//    private void setupCustomProgramUI() {
//        _fsCustomProg = new FieldSet();
//        _fsCustomProg.setHeading("");
//        _fsCustomProg.addStyleName("register-student-inner-fieldset");
//        _fsCustomProg.setId("custom-prog-fs");
//        FormLayout fl = new FormLayout();
//		fl.setLabelWidth(_formPanel.getLabelWidth());
//		fl.setDefaultWidth(LAYOUT_WIDTH);
//		_fsCustomProg.setLayout(fl);
//
//		cstmCombo = customCombo(customProgStore, _fsCustomProg, "Program or Quiz", "Select a Custom Program|Quiz");
//		_fsCustomProg.add(cstmCombo);
//
//	    _fsCustomProg.add(customAdvOptionsBtn);
//    }
//
//    private SelectionListener<ButtonEvent> selectionListener = new SelectionListener<ButtonEvent>() {
//    		public void componentSelected(ButtonEvent ce) {
//    			AdvOptCallback2 callback = new AdvOptCallback2() {
//    				@Override
//    				void setAdvancedOptions(Map<String, Object> optionMap) {
//    					stuSettingsMdl = (StudentSettingsModel) optionMap.get(StudentModelExt.SETTINGS_KEY);
//    					passPercent = (String) optionMap.get(StudentModelExt.PASS_PERCENT_KEY);
//    					activeSection = (Integer) optionMap.get(StudentModelExt.SECTION_NUM_KEY);
//    				}
//    			};
//    			
//    			final Map<String,Object>advOptionsMap = new HashMap <String,Object> ();
//    			final StudentSettingsModel ssm = new StudentSettingsModel();
//
//    			/** only set options if not null */
//    			if(stuSettingsMdl != null) {
//    				ssm.setLimitGames(stuSettingsMdl.getLimitGames());
//    				ssm.setShowWorkRequired(stuSettingsMdl.getShowWorkRequired());
//    				ssm.setStopAtProgramEnd(stuSettingsMdl.getStopAtProgramEnd());
//    				ssm.setTutoringAvailable(stuSettingsMdl.getTutoringAvailable());
//    			}
//    			else {
//    				/** use account data to set tutoring available */
//    				if (acctInfoMdl != null) {
//    					ssm.setTutoringAvailable(acctInfoMdl.getIsTutoringEnabled());
//    				}
//    			}
//    			
//    			advOptionsMap.put(StudentModelExt.PASS_PERCENT_KEY, passPercent);
//    			advOptionsMap.put(StudentModelExt.SETTINGS_KEY, ssm);
//
//    			advOptionsMap.put(StudentModelExt.SECTION_COUNT_KEY, sectionCount);
//    			if (activeSection == null) activeSection = 0;
//    			advOptionsMap.put(StudentModelExt.SECTION_NUM_KEY, activeSection);
//    			advOptionsMap.put("section-is-settable", sectionSelectAvail);
//    			
//    			if ("custom-adv-opt-btn".equals(ce.getButton().getId())) {
//    				ssm.setStopAtProgramEnd(true);
//    				advOptionsMap.put("prog-stop-is-settable", new Boolean(false));
//    			}
//    			else {
//    				advOptionsMap.put("prog-stop-is-settable", new Boolean(true));
//    			}
//
//    			new RegisterStudentAdvancedOptionsProto(callback, cmAdminMdl, advOptionsMap, isNew, passPercentReqd).setVisible(true);              
//    		}
//    	};
//
//    private Button stdAdvancedOptionsBtn() {
//		Button btn = new Button("Advanced Options");
//		btn.setToolTip("Disallow games, Change pass percentage, etc.");
//		btn.setWidth("110px");
//        btn.addSelectionListener(selectionListener);
//        btn.addStyleName("register-student-advanced-options-btn");
//        btn.setId("std-adv-opt-btn");
//		return btn;
//	}
//
//    private Button customAdvancedOptionsBtn() {
//		Button btn = new Button("Advanced Options");
//		btn.setToolTip("Disallow games, Change pass percentage, etc.");
//		btn.setWidth("110px");
//        btn.addSelectionListener(selectionListener);
//        btn.addStyleName("register-student-advanced-options-btn");
//        btn.setId("custom-adv-opt-btn");
//		return btn;
//	}
//
//	private boolean isSectionSelectAvail(CmProgramType type) {
//		return (type == CmProgramType.PROF             ||
//				type == CmProgramType.GRADPREP         ||
//				type == CmProgramType.GRADPREPNATIONAL ||
//				type == CmProgramType.GRADPREPTX);
//	}
//
//	private ComboBox<StudyProgramExt> programCombo(ListStore<StudyProgramExt> store) {
//		ComboBox<StudyProgramExt> combo = new ComboBox<StudyProgramExt>(new ComboBoxCell<StudyProgramExt>(store, __propsStudyProgram.title()) {
//		    @Override
//		    public void render(com.google.gwt.cell.client.Cell.Context context, StudyProgramExt value, SafeHtmlBuilder sb) {
//		        super.render(context, value, sb);
//		        
//		        Info.display("DEBUG", "NEED TEMPLATE: " + getProgramTemplate());
//		    }
//		});
//		//combo.setFieldLabel("Program type");
//		combo.setForceSelection(true);
//		combo.setEditable(false);
//		//combo.setMaxLength(45);
//		combo.setAllowBlank(false);
//		combo.setTriggerAction(TriggerAction.ALL);
//		combo.setStore(store);
//		//combo.setTemplate(getProgramTemplate());
//		combo.setTitle("Select a program type");
//		combo.setId("prog-combo");
//		combo.setTypeAhead(true);
//		combo.setSelectOnFocus(true);
//		combo.setEmptyText("-- select a program type --");
//		combo.setWidth(280);
//
//	    combo.addSelectionHandler(new SelectionHandler<StudyProgramExt>() {
//            
//            @Override
//            public void onSelection(SelectionEvent<StudyProgramExt> event) {
//
//	        	if (loading) return;
//
//	            StudyProgramExt sp = event.getSelectedItem();
//				boolean needsSubject = sp.isNeedsSubject();
//				boolean needsChapters = sp.isNeedsChapters();
//				passPercentReqd = sp.isNeedsPassPercent();
//				CmProgramType progType = sp.getProgramType();
//
//				if (CmProgramType.CUSTOM != progType) {
//
//					if (! cardPanel.getActiveWidget().equals(_fsStdProg)) {
//					    cardPanel.setActiveWidget(_fsStdProg);
//	                }
//
//					sectionSelectAvail = isSectionSelectAvail(progType); //sp.isGradPrep() || sp.isProficiency();
//
//					sectionCount = sp.getSectionCount();
//					activeSection = 0;
//
//					stdAdvOptionsBtn.enable();
//
//					ComboBox <SubjectModel> cb = (ComboBox<SubjectModel>) _fsStdProg.getItemByItemId("subj-combo");
//
//					skipComboSet = true;
//					subjectId = null;
//
//					if (needsSubject) {
//						cb.clear();
//						cb.enable();
//						cb.setForceSelection(true);
//						getSubjectList(sp.getShortTitle(), subjStore);
//					}
//					else {
//						cb.clearInvalid();
//						cb.clear();
//						cb.disable();
//						cb.setForceSelection(false);
//						subjectId = null;
//					}
//
//					ComboBox <ChapterModel> cc = (ComboBox<ChapterModel>) _fsStdProg.getItemByItemId("chap-combo");
//
//					if (needsChapters) {
//						cc.clear();
//						cc.enable();
//						cc.setForceSelection(true);
//					}
//					else {
//						cc.clearInvalid();
//						cc.clear();
//						cc.disable();
//						cc.setForceSelection(false);
//					}
//				}
//				else {
//					cardPanel.setActiveWidget(_fsCustomProg);
//					skipComboSet = true;
//					subjectId = null;
//					sectionSelectAvail = false;
//					activeSection = 0;
//				}
//	        }
//	    });
//
//	    return combo;
//	}
//
//	private ComboBox<StudyProgramExt> customCombo(ListStore<StudyProgramExt> store, final FieldSet fs, String label, String title) {
//		ComboBox<StudyProgramExt> combo = new ComboBox<StudyProgramExt>();
//		combo.setFieldLabel(label);
//		combo.setForceSelection(true);
//		combo.setDisplayField("title");
//		combo.setEditable(false);
//		combo.setMaxLength(45);
//		combo.setAllowBlank(false);
//		combo.setTriggerAction(TriggerAction.ALL);
//		combo.setStore(store);
//		combo.setTemplate(getProgramTemplate());
//		combo.setTitle(title);
//		combo.setId("custom-combo");
//		combo.setTypeAhead(true);
//		combo.setSelectOnFocus(true);
//		combo.setEmptyText("-- make a selection --");
//		combo.setWidth(280);
//
//	    combo.addSelectionChangedListener(new SelectionChangedListener<StudyProgramExt>() {
//			public void selectionChanged(SelectionChangedEvent<StudyProgramExt> se) {
//
//	        	if (loading) return;
//
//	            StudyProgramExt sp = se.getSelectedItem();
//
//	            passPercentReqd = ((Integer)sp.get("needsPassPercent")).intValue() > 0;
//
//				sectionSelectAvail = false;
//
//				customAdvOptionsBtn.enable();
//
//	        }
//	    });
//
//	    return combo;
//	}
//
//	private native String getProgramTemplate() /*-{ 
//	    return  [ 
//	   '<tpl for=".">', 
//	   '<div class="x-combo-list-item {styleIsTemplate} {styleIsArchived}" qtip="{descr}">{label}</div>', 
//	   '</tpl>' 
//	   ].join(""); 
//	   }-*/;  
//
//	private ComboBox<SubjectModel> subjectCombo(ListStore<SubjectModel> store) {
//		ComboBox<SubjectModel> combo = new ComboBox<SubjectModel>();
//		combo.setFieldLabel("Subject");
//		combo.setForceSelection(false);
//		combo.setDisplayField("subject");
//		combo.setEditable(false);
//		combo.setMaxLength(30);
//		combo.setAllowBlank(false);
//		combo.setTriggerAction(TriggerAction.ALL);
//		combo.setStore(store);
//		combo.setTitle("Select a subject");
//		combo.setId("subj-combo");
//		combo.setTypeAhead(true);
//		combo.setSelectOnFocus(true);
//		combo.setEmptyText("-- select a subject --");
//		combo.disable();
//		combo.setWidth(280);
//		
//	    combo.addSelectionChangedListener(new SelectionChangedListener<SubjectModel>() {
//			@SuppressWarnings("unchecked")
//			public void selectionChanged(SelectionChangedEvent<SubjectModel> se) {
//
//				if (loading) return;
//
//	        	SubjectModel sm = se.getSelectedItem();
//	        	if (subjectId == null || ! subjectId.equals(sm.getAbbrev())) {
//	        	    try {
//	    	        	skipComboSet = true;
//    		        	subjectId = sm.getAbbrev();
//    		        	ComboBox<StudyProgramExt> cb = (ComboBox<StudyProgramExt>) _fsProgram.getItemByItemId("prog-combo");
//    		        	StudyProgramExt sp = cb.getValue();
//    		        	String progId = sp.get("shortTitle");
//    		        	chapStore.removeAll();
//    		            getChapterListRPC(progId, subjectId, true, chapStore);
//	        	    }
//	        	    catch(Exception e) {
//	        	        e.printStackTrace();
//	        	        CatchupMathTools.showAlert(e.getMessage());
//	        	    }
//	        	}
//	        }
//	    });
//		return combo;
//	}
//
//	private ComboBox<ChapterModel> chapterCombo(ListStore<ChapterModel> store) {
//		ComboBox<ChapterModel> combo = new ComboBox<ChapterModel>();
//		combo.setFieldLabel("Chapter");
//		combo.setForceSelection(false);
//		combo.setDisplayField("chapter");
//		combo.setEditable(false);
//		combo.setMaxLength(160);
//		combo.setAllowBlank(false);
//		combo.setTriggerAction(TriggerAction.ALL);
//		combo.setStore(store);
//		combo.setTitle("Select a Chapter");
//		combo.setId("chap-combo");
//		combo.setTypeAhead(true);
//		combo.setSelectOnFocus(true);
//		combo.setEmptyText("-- select a chapter --");
//		combo.disable();
//		combo.setWidth(280);
//		return combo;
//	}
//
//	private TextButton cancelButton() {
//		TextButton cancelBtn = new TextButton("Cancel", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//	    	    _window.close();
//	        }  
//	    });
//		return cancelBtn;
//	}
//
//	private TextButton saveButton() {
//		TextButton saveBtn = new TextButton("Save", new SelectHandler() {
//            @Override
//            public void onSelect(SelectEvent event) {
//	            try {
//	                doSubmitAction(null);
//	            }
//	            catch(CmException cm) {
//	                cm.printStackTrace();
//	            }
//	        }
//	    });
//		return saveBtn;
//	}
//
//	private void getStudyProgramListRPC() {
//
//		inProcessCount++;
//
//		new RetryAction<CmList<StudyProgramModel>>() {
//		    @Override
//		    public void attempt() {
//		        GetProgramDefinitionsAction action = new GetProgramDefinitionsAction(cmAdminMdl.getUid());
//		        setAction(action);
//		        CmShared.getCmService().execute(action, this);
//		    }
//            public void oncapture(CmList<StudyProgramModel> spmList) {
//                List<StudyProgramExt> progList = new ArrayList <StudyProgramExt> ();
//                List<StudyProgramExt> customProgList = new ArrayList <StudyProgramExt> ();
//
//                int stuCustomProgramId = (isNew == false && stuMdl.getProgram().getCustom().getCustomProgramId() != 0) ?
//            			stuMdl.getProgram().getCustom().getCustomProgramId(): -1;
//            	int stuCustomQuizId = (isNew == false && stuMdl.getProgram().getCustom().getCustomQuizId() != 0) ?
//            			stuMdl.getProgram().getCustom().getCustomQuizId() : -1;
//
//                for (StudyProgramModel spm : spmList) {
//                	if (excludeAutoEnroll && spm.getShortTitle().equalsIgnoreCase("AUTO-ENROLL")) continue;
//
//                	if ((isNew == true && spm.getIsArchived() == true)) continue;
//
//                	if (isNew == false && spm.getIsArchived() == true &&
//                		spm.getCustomProgramId() != stuCustomProgramId &&
//                		spm.getCustomQuizId() != stuCustomQuizId)
//                		continue;
//
//                	if (spm.getCustomProgramId() == 0 && spm.getCustomQuizId() == 0) {
//                    	progList.add(new StudyProgramExt(spm, spm.getTitle(), spm.getShortTitle(), spm.getDescr(), 
//                                spm.getNeedsSubject(), spm.getNeedsChapters(), spm.getNeedsPassPercent(),
//                                spm.getCustomProgramId(), spm.getCustomProgramName()));                		
//                	}
//                	else {
//                    	customProgList.add(new StudyProgramExt(spm, spm.getTitle(), spm.getShortTitle(), spm.getDescr(),
//                                spm.getNeedsSubject(), spm.getNeedsChapters(), spm.getNeedsPassPercent(),
//                                spm.getCustomProgramId(), spm.getCustomProgramName()));
//                	}
//                }
//                StudyProgramModel spm = new StudyProgramModel(CUSTOM_ID, "Custom", "Custom", "Custom Programs and Quizzes", 0, " ", 0, " ", 0, 0, 0, 0, 0);
//                spm.setProgramType(CmProgramType.CUSTOM);
//                spm.setIsArchived(false);
//            	progList.add(new StudyProgramExt(spm, "Custom", "Custom", "Custom Programs and Quizzes", 0, 0, 0, 0, null));                		
//                progStore.add(progList);
//                customProgStore.add(customProgList);
//                
//                inProcessCount--;
//                setComboBoxSelections();
//            }
//        }.register();
//	}
//
//	private Map<String, List<SubjectModel>> programSubjectMap = new HashMap<String, List<SubjectModel>>();
//	
//	private void getSubjectList(final String progId, final ListStore <SubjectModel> subjStore) {
//
//		if (progId == null) return;
//
//		List<SubjectModel> subjList = programSubjectMap.get(progId);
//		if (subjList != null) {
//			subjStore.removeAll();
//			subjStore.add(subjList);
//			setComboBoxSelections();
//		}
//		else {
//			getSubjectListRPC(progId, subjStore);
//		}
//	}
//	
//	private void getSubjectListRPC(final String progId, final ListStore <SubjectModel> subjStore) {
//
//		inProcessCount++;
//		
//		new RetryAction <CmList<SubjectModel>>() {
//
//		    @Override
//		    public void attempt() {
//		        GetSubjectDefinitionsAction action = new GetSubjectDefinitionsAction(progId);
//		        setAction(action);
//		        CmShared.getCmService().execute(action,this);		        
//		    }
//            public void oncapture(CmList<SubjectModel> result) {
//                subjStore.removeAll();
//                subjStore.add(result);
//                inProcessCount--;
//                programSubjectMap.put(progId, result);
//                setComboBoxSelections();
//            }
//        }.register();
//		
//	}
//		
//	protected void addUserRPC(final StudentModel sm) {
//	    
//	    /** execute outside the RetryManager
//	     *  to allow processing exceptions.
//	     */
//		new RetryAction <StudentModelI> () {
//		    @Override
//		    public void attempt() {
//		        CmBusyManager.setBusy(true);
//		        AddStudentAction action = new AddStudentAction(sm);
//		        setAction(action);
//		        CmShared.getCmService().execute(action,this);
//		    }
//            public void oncapture(StudentModelI ai) {
//                CmBusyManager.setBusy(false);
//                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_PROGRAM_CHANGED,ai.getProgramChanged()));
//                _window.close();
//            }
//            
//            @Override
//            public void onFailure(Throwable caught) {
//                CmBusyManager.setBusy(false);
//                if(caught.getMessage().indexOf("already in use") > -1) {
//                    CatchupMathTools.showAlert("Password In Use", "This password is currently in use");
//                    return;
//                }
//                super.onFailure(caught);
//            }
//        }.attempt();
//	}
//
//	protected void updateUserRPC(final StudentModel sm, final Boolean stuChanged, final Boolean progChanged, final Boolean progIsNew,
//	        final Boolean passcodeChanged, final Boolean passPercentChanged, final Boolean sectionNumChanged) {
//		new RetryAction<StudentModelI> () {
//		    @Override
//		    public void attempt() {
//		        CmBusyManager.setBusy(true);
//		        UpdateStudentAction action =
//		        	new UpdateStudentAction(sm,stuChanged,progChanged,progIsNew,passcodeChanged,passPercentChanged,sectionNumChanged);
//		        setAction(action);
//		        CmShared.getCmService().execute(action,this);
//		    }
//            public void oncapture(StudentModelI ai) {
//                CmBusyManager.setBusy(false);
//                _window.close();
//                EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_PROGRAM_CHANGED,ai.getProgramChanged()));
//            }
//        }.register();
//	}
//	
//	private void setComboBoxSelections() {
//		if (this.stuMdl != null && !skipComboSet && inProcessCount < 1) {
//			
//			loading = true;
//			
//			setGroupSelection();
//			
//    		StudyProgramExt sp = setProgramSelection();
//
//    		if (sp == null) {
//    			CatchupMathTools.showAlert("Program not found!");
//    			loading = false;
//    			return;
//    		}
//    		
//			CmProgramType progType = (CmProgramType) sp.get("programType");
//			if (CmProgramType.CUSTOM != progType) {
//				cardPanel.setActiveItem(_fsStdProg);
//
//				int needsSubject = ((Integer)sp.get("needsSubject")).intValue();
//    		    if (needsSubject != 0) setSubjectSelection();
//
//    		    int needsChapters = ((Integer)sp.get("needsChapters")).intValue();
//    		    if (needsChapters != 0) setChapterSelection();
//     		}
//    		else {
//				cardPanel.setActiveItem(_fsCustomProg);
//    			setCustomProgramSelection();
//    		}
//    		passPercentReqd = ((Integer)sp.get("needsPassPercent")).intValue() > 0;
//
//			if (stdAdvOptionsBtn.isVisible())
//                stdAdvOptionsBtn.enable();
//
//			if (customAdvOptionsBtn.isVisible())
//                customAdvOptionsBtn.enable();
//
//            loading = false;
//		}
//	}
//
//	private void setGroupSelection() {
//	    if(stuMdl == null) {
//	        new Exception().printStackTrace();
//	        return;
//	    }
//	    
//	    if (_fsProfile.getItemByItemId("group-combo") == null) {
//	    	return;
//	    }
//
//	    Integer groupId = Integer.parseInt(stuMdl.getGroupId());
//		if (groupId != null) {
//			List<GroupInfoModel> l = __groupStore.getModels();
//			for (GroupInfoModel g : l) {
//			    Integer gi = g.getId();
//				if (groupId.equals(gi)) {
//					groupCombo.setOriginalValue(g);
//					groupCombo.setValue(g);
//				}
//			}
//		}
//	}
//	
//	private StudyProgramExt setProgramSelection() {
//	    StudentProgramModel program = stuMdl.getProgram();
//		String shortName = program.getProgramType().getType();
//		
//		if(program.isCustom()) {
//			List<StudyProgramExt> list = progStore.getModels();
//			
//			for (StudyProgramExt sp : list) {
//				if (sp.get("shortTitle").equals("Custom")) {
//					progCombo.setOriginalValue(sp);
//					progCombo.setValue(sp);
//                    return sp;
//				}
//			}
//			return null;
//		}
//		
//		if (shortName != null) {
//			List<StudyProgramExt> list = progStore.getModels();
//			for (StudyProgramExt sp : list) {
//				
//				if (progNameCheckHack(shortName, sp)) {
//					progCombo.setOriginalValue(sp);
//					progCombo.setValue(sp);
//					return sp;
//				}
//			}
//		}
//		return null;
//	}
//
//	private StudyProgramExt setCustomProgramSelection() {
//	    StudentProgramModel program = stuMdl.getProgram();
//		
//		List<StudyProgramExt> list = customProgStore.getModels();
//		
//		for (StudyProgramExt sp : list) {
//			
//        	if ((program.getCustom().getCustomProgramId() != 0 && program.getCustom().getCustomProgramId() == sp.getCustomProgramId()) ||
//				(program.getCustom().getCustomQuizId() != 0 && program.getCustom().getCustomQuizId() == sp.getCustomQuizId())) {
//				cstmCombo.setOriginalValue(sp);
//				cstmCombo.setValue(sp);
//				return sp;
//			}
//		}
//		return null;
//
//	}
//
//	/** perform hack to determine correct program.
//	 * 
//	 * @NOTE: add a way to identify programs correctly.
//	 * 
//	 * @param shortName
//	 * @param sp
//	 * @return
//	 */
//	private boolean progNameCheckHack(String shortName, StudyProgramExt sp) {
//        String st=((String) sp.get("shortTitle")).toLowerCase();
//        
//        if(sp.get("customProgramName") != null && sp.get("customProgramName").equals(shortName)) {
//            return true;
//        }
//        
//        if(sp.get("customQuizName") != null && sp.get("customQuizName").equals(shortName)) {
//            return true;
//        }
//        
//        if(st.equals("chap") && shortName.toLowerCase().indexOf(" chap") > -1) {
//            return true;
//        }
//        else if(st.equals("prof") && shortName.toLowerCase().indexOf(" prof") > -1)
//            return true;
//        else {
//            return shortName.equalsIgnoreCase(st);
//        }
//	}
//	
//	private void setSubjectSelection() {
//		String shortName = stuMdl.getProgram().getProgramDescription();
//
//		if (shortName != null) {
//			List<SubjectModel> list = subjStore.getModels();
//			for (SubjectModel s : list) {
//				if (shortName.indexOf((String) s.get("abbrev")) > -1) {
//					subjCombo.setOriginalValue(s);
//					subjCombo.setValue(s);
//					subjCombo.enable();
//					break;
//				}
//			}
//		}
//
//	}
//	
//	private void setChapterSelection() {
//		String chap = stuMdl.getChapter();
//
//		if (chap != null) {
//			List <ChapterModel> list = chapStore.getModels();
//			for (ChapterModel c : list) {
//				if (chap.equals(c.getTitle())) {
//					chapCombo.setOriginalValue(c);
//					chapCombo.setValue(c);
//					chapCombo.enable();
//					break;
//				}
//			}
//    	}
//	}
//	
//	private void getChapterListRPC(final String progId, final String subjId, final Boolean chapOnly, final ListStore <ChapterModel> chapStore) {
//
//		if (progId == null || !progId.equalsIgnoreCase("chap")) return;
//
//		inProcessCount++;
//		new RetryAction <CmList<ChapterModel>> () {
//
//		    @Override
//		    public void attempt() {
//		        CmServiceAsync s = CmShared.getCmService();
//		        GetChaptersForProgramSubjectAction action = new GetChaptersForProgramSubjectAction(progId, subjId);
//		        setAction(action);
//		        s.execute(action, this);
//		    }
//
//            public void oncapture(CmList<ChapterModel> result) {
//                chapStore.add(result);
//                inProcessCount--;
//                if (! chapOnly)
//                    setComboBoxSelections();
//                else {
//                    chapCombo.setOriginalValue(null);
//                    chapCombo.setValue(null);
//                    chapCombo.clearSelections();
//                }
//            }
//        }.register();
//	}
//
//    protected void getAccountInfoRPC(final Integer uid) {
//        new RetryAction<AccountInfoModel>() {
//            @Override
//            public void attempt() {
//                CmServiceAsync s = CmShared.getCmService();
//                GetAccountInfoForAdminUidAction action = new GetAccountInfoForAdminUidAction(uid);
//                setAction(action);
//                CmLogger.info("AccountInfoPanel: reading admin info RPC");
//                s.execute(action,this);
//            }
//
//            public void oncapture(AccountInfoModel ai) {
//                acctInfoMdl = ai;
//            }
//        }.register();        
//    }
//    
//	/**
//	 * Perform the form save operation and display any required validation.
//	 * 
//	 * Throws CmExeptionValidationFailed on failed validation attempt.
//	 * 
//	 * if callback == null, then default operation, saving data, is performed.
//	 * If callback is provided, then after validation of form callback is called.
//	 * 
//	 * 
//	 * @TODO: separate into validation/action.  Perhaps strategy pattern.
//	 * 
//	 * @param fs
//	 * @param fp
//	 */
//	@SuppressWarnings("unchecked")
//	protected void doSubmitAction(AfterValidation callback) throws CmException {
//		
//	    TextField<String> tf = (TextField<String>)_fsProfile.getItemByItemId("name");
//	    String name="";
//        if(tf != null) {
//            tf.clearInvalid();
//            name = tf.getValue();
//            if (name == null) {
//                tf.focus();
//                tf.forceInvalid(ENTRY_REQUIRED_MSG);
//                throw new CmExceptionValidationFailed();
//            }
//        }
//        
//        String passcode = null;
//        tf = (TextField<String>)_fsProfile.getItemByItemId("passcode");
//        if(tf != null) {
//            tf.clearInvalid();
//            passcode = tf.getValue();
//            if (passcode == null) {
//                tf.focus();
//                tf.forceInvalid(ENTRY_REQUIRED_MSG);
//                throw new CmExceptionValidationFailed();
//            }
//            else {
//                if(passcode.contains(" ")) {
//                    tf.focus();
//                    tf.forceInvalid("Password cannot contain spaces");
//                    throw new CmExceptionValidationFailed();
//                }
//
//            }
//        }
//
//        String groupId=null;
//        String group=null;
//        ComboBox<GroupInfoModel> cg = (ComboBox<GroupInfoModel>) _fsProfile.getItemByItemId("group-combo");
//        if(cg != null) {
//        	cg.clearInvalid();
//        	GroupInfoModel g = cg.getValue();
//        	if (g == null) {
//        		cg.focus();
//        		cg.forceInvalid(ENTRY_REQUIRED_MSG);
//        		cg.expand();
//        		throw new CmExceptionValidationFailed();
//        	}
//        	groupId = g.getId().toString();
//        	group = g.getName();
//        }
//        
//        String fsId = null;
//        List<Component> list = cardPanel.getItems();
//        for (Component c : list) {
//        	if (c.isVisible()) {
//        		fsId = c.getId();
//        	}
//        }
//
//        StudyProgramExt studyProgExt = null;
//        SubjectModel sub = null;
//        ChapterModel chap = null;
//        String prog = null;
//
//        if (fsId.equals("std-prog-fs")) {
//        	ComboBox<StudyProgramExt> cb = (ComboBox<StudyProgramExt>) _fsProgram.getItemByItemId("prog-combo");
//        	studyProgExt = cb.getValue();
//        	cb.clearInvalid();
//        	if (studyProgExt == null) {
//        		cb.focus();
//        		cb.forceInvalid(ENTRY_REQUIRED_MSG);
//        		cb.expand();
//        		throw new CmExceptionValidationFailed();
//        	}
//        	prog = studyProgExt.get("shortTitle");
//
//        	ComboBox<SubjectModel> cs = (ComboBox<SubjectModel>) _fsStdProg.getItemByItemId("subj-combo");
//        	sub = cs.getValue();
//        	cs.clearInvalid();
//        	if (sub != null) {
//        		prog = sub.get("abbrev") + " " + prog;
//        	}
//        	if (((Integer)studyProgExt.get("needsSubject")).intValue() > 0 && sub == null) {
//        		cs.focus();
//        		cs.forceInvalid(ENTRY_REQUIRED_MSG);
//        		cs.expand();
//        		throw new CmExceptionValidationFailed();
//        	}
//
//        	ComboBox<ChapterModel> cc = (ComboBox<ChapterModel>) _fsStdProg.getItemByItemId("chap-combo");
//        	chap = cc.getValue();
//        	cc.clearInvalid();
//        	if (chap != null) {
//        		prog = prog + " " + chap.get("number");
//        	}
//        	if (((Integer)studyProgExt.get("needsChapters")).intValue() > 0 && chap == null) {
//        		cc.focus();
//        		cc.forceInvalid(ENTRY_REQUIRED_MSG);
//        		cc.expand();
//        		throw new CmExceptionValidationFailed();
//        	}
//        }
//        else {
//        	ComboBox<StudyProgramExt> cb = (ComboBox<StudyProgramExt>) _fsCustomProg.getItemByItemId("custom-combo");
//        	studyProgExt = cb.getValue();
//        	cb.clearInvalid();
//        	if (studyProgExt == null) {
//        		cb.focus();
//        		cb.forceInvalid(ENTRY_REQUIRED_MSG);
//        		cb.expand();
//        		throw new CmExceptionValidationFailed();
//        	}
//        	prog = studyProgExt.get("shortTitle");
//        }
//
//        if (passPercentReqd &&
//        	(passPercent == null || Integer.parseInt(passPercent.substring(0,passPercent.length()-1)) == 0)) {
//        	// set pass percent to default value
//        	// TODO: (?) allow Admin to specify default pass percent (account, group, program)
//            ComboBox <PassPercent> passCombo = new PassPercentCombo(passPercentReqd);
//        	passPercent = passCombo.getStore().getAt(PassPercentCombo.DEFAULT_PERCENT_IDX).getPassPercent();
//        }
//        
//        /** Collect all the values and create a new StudentModel
//         *  to hold validated values.
//         *  
//         */
//        StudentModel sm = new StudentModel();
//        sm.setName(name);
//        sm.setPasscode(passcode);
//        //sm.setEmail(email);
//        sm.getProgram().setProgramDescription(prog);
//        sm.setGroupId(groupId);
//        if(stuSettingsMdl != null)
//            sm.setSettings(stuSettingsMdl);
//        sm.setGroup(group);
//        sm.setAdminUid(cmAdminMdl.getId());
//        sm.setPassPercent(passPercent);
//        sm.setSectionNum(activeSection);
//
//        String chapTitle = (chap != null) ? chap.getTitle() : null;
//        sm.setChapter(chapTitle);
//
//        String progId = (studyProgExt != null) ? (String)studyProgExt.get("shortTitle") : null;        
//        String subjId = (sub != null) ? sub.getAbbrev() : "";
//
//        
//        /** Why is this constructed here?
//         *  Why not pass in the StudyProgramModel (Ext)
//         *  
//         *  Perhaps, have a .convert on StudyProgramModelExt.
//         *  
//         */
//        StudentProgramModel program = sm.getProgram();
//        program.setProgramId((isNew == false)?stuMdl.getProgram().getProgramId():null);
//        program.setProgramType(progId);
//        program.setSubjectId(subjId);
//        
//        program.setCustom(new CustomProgramComposite(
//                studyProgExt.getCustomProgramId(),studyProgExt.getCustomProgramName(),
//                studyProgExt.getCustomQuizId(),studyProgExt.getCustomQuizName()));
//
//
//        /** Validation complete 
//         * 
//         * if callback has been provided, then jump to call back
//         * 
//         */
//        if (callback != null) {
//            callback.afterValidation(sm);
//            return;
//        }
//        
//        /**
//         * If callback not provided, then perform default operation
//         * 
//         * @TODO: all this logic about what is updated should be on the server
//         *        the client should only have to update the POJO and say go.
//         * 
//         */
//        if (isNew) {
//            sm.setSectionNum( (activeSection != null) ? activeSection : 0);
//            sm.setStatus("Not started");
//            sm.setTotalUsage(0);
//            addUserRPC(sm);
//        }
//        else {
//            Boolean stuChanged = false;
//            Boolean passcodeChanged = false;
//            Boolean passPercentChanged = false;
//            Boolean progChanged = false;
//            Boolean progIsNew = false;
//            
//            sm.setUid(stuMdl.getUid());
//            sm.getProgram().setProgramId(stuMdl.getProgram().getProgramId());
//            sm.setJson(stuMdl.getJson());
//            sm.setStatus(stuMdl.getStatus());
//            sm.setProgramChanged(false);
//            if (! name.equals(stuMdl.getName()) ||
//                ! (groupId != null && groupId.equals(stuMdl.getGroupId()))) {
//                stuChanged = true;
//            }
//            if (! passcode.equals(stuMdl.getPasscode())) {
//                passcodeChanged = true;
//                stuChanged = true;
//            }
//
//            Integer prevSectionNum = stuMdl.getSectionNum();
//            boolean sectionNumChanged = false;
//            if ( (prevSectionNum == null && activeSection != null) ||
//            	 (prevSectionNum != null && activeSection == null) ||
//            	 (prevSectionNum != null && ! prevSectionNum.equals(activeSection))) {
//                sm.setSectionNum(activeSection);
//            	stuChanged = true;
//            	sectionNumChanged = true;
//            }
//            else {
//                sm.setSectionNum(prevSectionNum);
//            }
//            
//            if (stuMdl.getProgram().getProgramDescription() == null || isDifferentProgram(stuMdl,prog)) {
//                sm.setStatus("Not started");
//                if (sectionNumChanged == false) sm.setSectionNum(0);
//                sm.setProgramChanged(true);
//                // don't know what the program Id will be so set to null
//                sm.getProgram().setProgramId(null);
//
//                progIsNew = true;
//                progChanged = false;
//                sectionNumChanged = false;
//                stuChanged = true;
//            }
//
//            if (! stuChanged && settingsChanged(stuMdl.getSettings(), stuSettingsMdl)) {
//            	stuChanged = true;
//            }
//
//            if (valueChanged(stuMdl.getPassPercent(), passPercent))
//            	passPercentChanged = ! sm.getProgramChanged();
//
//            if (stuChanged || progChanged || progIsNew || passPercentChanged) {
//                updateUserRPC(sm, stuChanged, progChanged, progIsNew, passcodeChanged, passPercentChanged, sectionNumChanged);
//            }
//            else {
//                _window.close();
//            }
//        }	    
//	}
//	
//	private boolean isDifferentProgram(StudentModelI stuMdl, String prog) {
//	    if (prog.equals("Custom")) {
//	            
//	        /** compare the name, maybe ... 
//	         * for now always update
//	         * 
//	         */
//	        return true;
//	    }
//	    else {
//	        return !stuMdl.getProgram().getProgramDescription().equals(prog);
//	    }
//	}
//
//	private boolean settingsChanged(StudentSettingsModel origValue, StudentSettingsModel newValue) {
//        if (origValue == null && newValue != null) return true;
//
//        if (origValue != null && newValue == null) return true;
//
//        if (origValue == null && newValue == null) return false;
//
//        return ( ! origValue.getLimitGames() == newValue.getLimitGames() ||
//        		 ! origValue.getShowWorkRequired() == newValue.getShowWorkRequired() ||
//        		 ! origValue.getStopAtProgramEnd() == newValue.getStopAtProgramEnd() ||
//        		 ! origValue.getTutoringAvailable() == newValue.getTutoringAvailable());
//	}
//
//	private boolean valueChanged(String origValue, String newValue) {
//        if (origValue == null && newValue != null) return true;
//        
//        if (origValue != null && ! origValue.equals(newValue)) return true;
//        
//        if (origValue != null && origValue.equals(newValue)) return false;
//        
//        return false;
//	}
//
//	@Override
//	public void beginStep() {
//		inProcessCount++;
//	}
//
//	@Override
//	public void completeStep() {
//		inProcessCount--;
//	}
//
//	@Override
//	public void finish() {
//		setComboBoxSelections();
//	}

}

abstract class AdvOptCallback2 {
	abstract void setAdvancedOptions(AdvancedOptionsModel options);
}



