package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.cm_tools.client.service.CmServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
import hotmath.gwt.shared.client.eventbus.EventType;
import hotmath.gwt.shared.client.rpc.action.AddStudentAction;
import hotmath.gwt.shared.client.rpc.action.CmList;
import hotmath.gwt.shared.client.rpc.action.GetChaptersForProgramSubjectAction;
import hotmath.gwt.shared.client.rpc.action.GetProgramDefinitionsAction;
import hotmath.gwt.shared.client.rpc.action.GetSubjectDefinitionsAction;
import hotmath.gwt.shared.client.rpc.action.UpdateStudentAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.shared.client.util.UserInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
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
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
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
	
	private StudentModelI stuMdl;
	protected CmAdminModel cmAdminMdl;
	private int inProcessCount;
	private String subjectId;
	
	private ListStore <StudyProgram> progStore;
	private ComboBox<StudyProgram> progCombo;
	
	private ListStore <SubjectModel> subjStore;
	private ComboBox<SubjectModel> subjCombo;
	
	
	private ListStore <ChapterModel> chapStore;
	private ComboBox <ChapterModel> chapCombo;
	
	static private ListStore <GroupInfoModel> __groupStore;
	private ComboBox <GroupInfoModel> groupCombo;
	
	private TextField<String> userName;
	
	private int formHeight = 380;
	protected int formWidth  = 475;
	
	protected CombinedFormPanel _formPanel;
	
	private static final String ENTRY_REQUIRED_MSG = "This field is required";
	
	public RegisterStudent(StudentModelI sm, CmAdminModel cm) {
	    
	    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_MODAL_WINDOW_OPEN));
	    
		inProcessCount = 0;
		isNew = (sm == null);
		stuMdl = sm;
		if (stuMdl != null) {
			subjectId = stuMdl.getSubjId();
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
	public CheckBoxGroup _showWorkGrp, _tutoringEnabled;
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
		
		progStore = new ListStore <StudyProgram> ();
		getStudyProgramListRPC(progStore);
		progCombo = programCombo(progStore, _fsProgram);
		_fsProgram.add(progCombo);
		
		subjStore = new ListStore <SubjectModel> ();
		getSubjectList((stuMdl != null)?stuMdl.getProgId():null, subjStore);
		subjCombo = subjectCombo(subjStore);
		_fsProgram.add(subjCombo);

		chapStore = new ListStore <ChapterModel> ();
        getChapterListRPC((stuMdl != null)?stuMdl.getProgId():null, subjectId, false, chapStore);
		chapCombo = chapterCombo(chapStore);
		_fsProgram.add(chapCombo);        
		
		CheckBox isShowWorkRequired = new CheckBox();
        isShowWorkRequired.setId(StudentModelExt.SHOW_WORK_KEY);
        if (! isNew) {
        	isShowWorkRequired.setValue(stuMdl.getShowWorkRequired());
        }
        else {
        	// require 'Show Work' OFF by default
        	isShowWorkRequired.setValue(false);
        }

        _showWorkGrp = new CheckBoxGroup(); 
        _showWorkGrp.setFieldLabel("Require Show Work");
        _showWorkGrp.setId(StudentModelExt.SHOW_WORK_KEY);
        _showWorkGrp.add(isShowWorkRequired);
        _fsProgram.add(_showWorkGrp);
        
        
        
        CheckBox isTutoringEnabled = new CheckBox();
        isTutoringEnabled.setId(StudentModelExt.TUTORING_AVAIL_KEY);
        if (! isNew) {
            isTutoringEnabled.setValue(stuMdl.getTutoringAvail());
        }
        else {
            // require 'Show Work' OFF by default
            isTutoringEnabled.setValue(false);
        }        
        
        _tutoringEnabled = new CheckBoxGroup();
        _tutoringEnabled.setFieldLabel("Tutoring Enabled");
        _tutoringEnabled.setId(StudentModelExt.TUTORING_AVAIL_KEY);
		_tutoringEnabled.add(isTutoringEnabled);
		_fsProgram.add(_tutoringEnabled);
		
		
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

	private ComboBox<StudyProgram> programCombo(ListStore<StudyProgram> store, final FieldSet fs) {
		ComboBox<StudyProgram> combo = new ComboBox<StudyProgram>();
		combo.setFieldLabel("Program");
		combo.setForceSelection(true);
		combo.setDisplayField("title");
		combo.setEditable(false);
		combo.setMaxLength(30);
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
        		
	    combo.addSelectionChangedListener(new SelectionChangedListener<StudyProgram>() {
	        @SuppressWarnings("unchecked")
			public void selectionChanged(SelectionChangedEvent<StudyProgram> se) {
	        	
	        	if (loading) return;
	        	
	            StudyProgram sp = se.getSelectedItem();
				int needsSubject = ((Integer)sp.get("needsSubject")).intValue();
				int needsChapters = ((Integer)sp.get("needsChapters")).intValue();

				
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
	   '<div class="x-combo-list-item" qtip="{descr}">{title}</div>', 
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
    		        	ComboBox<StudyProgram> cb = (ComboBox<StudyProgram>) _formPanel.getItemByItemId("prog-combo");
    		        	StudyProgram sp = cb.getValue();
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
	
	private void getStudyProgramListRPC(final ListStore <StudyProgram> progStore) {

		inProcessCount++;
        CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		GetProgramDefinitionsAction action = new GetProgramDefinitionsAction(); 
		s.execute(action, new CmAsyncCallback<CmList<StudyProgramModel>>() {
			public void onSuccess(CmList<StudyProgramModel> spmList) {
				List<StudyProgram> progList = new ArrayList <StudyProgram> ();
				for (StudyProgramModel spm : spmList) {
					progList.add(new StudyProgram(spm.getTitle(), spm.getShortTitle(), spm.getDescr(), spm.getNeedsSubject(),
							spm.getNeedsChapters(), spm.getNeedsPassPercent()));
				}
				progStore.add(progList);
				inProcessCount--;
				setComboBoxSelections();
        	}
        });
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
		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		
		GetSubjectDefinitionsAction action = new GetSubjectDefinitionsAction(progId);
		s.execute(action, new CmAsyncCallback <CmList<SubjectModel>>() {

			public void onSuccess(CmList<SubjectModel> result) {
				subjStore.removeAll();
				subjStore.add(result);
				inProcessCount--;
				programSubjectMap.put(progId, result);
				setComboBoxSelections();
        	}
        });
		
	}
		
	protected void addUserRPC(final StudentModel sm) {
	    CmBusyManager.setBusy(true);
	    CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
	    AddStudentAction action = new AddStudentAction(sm);
		s.execute(action, new CmAsyncCallback <StudentModelI> () {
			public void onSuccess(StudentModelI ai) {
			    EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_PROGRAM_CHANGED,ai.getProgramChanged()));
			    _window.close();
			    
			    CmBusyManager.setBusy(false);
        	}
			
			@Override
			public void onFailure(Throwable caught) {
	            CmBusyManager.setBusy(false);
			    super.onFailure(caught);
			}
        });
	}

	protected void updateUserRPC(final StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
			Boolean passcodeChanged) {
		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		
		CmBusyManager.setBusy(true);
		UpdateStudentAction action = new UpdateStudentAction(sm,stuChanged,progChanged,progIsNew,passcodeChanged);
		s.execute(action, new CmAsyncCallback <StudentModelI> () {
			public void onSuccess(StudentModelI ai) {
		        EventBus.getInstance().fireEvent(new CmEvent(EventType.EVENT_TYPE_USER_PROGRAM_CHANGED,ai.getProgramChanged()));
		        _window.close();
		        CmBusyManager.setBusy(false);
        	}
			public void onFailure(Throwable caught) {
		        CmBusyManager.setBusy(false);
				super.onFailure(caught);
        	}
        });
	}

	private void copyStudent(StudentModel from, StudentModel to) {
		to.setChapter(from.getChapter());
		to.setGroup(from.getGroup());
		to.setGroupId(from.getGroupId());
		to.setJson(from.getJson());
		to.setName(from.getName());
		to.setPasscode(from.getPasscode());
		to.setPassPercent(from.getPassPercent());
		to.setProgId(from.getProgId());
		to.setProgramDescr(from.getProgramDescr());		
	}
	
	private void setComboBoxSelections() {
		if (this.stuMdl != null && !skipComboSet && inProcessCount < 1) {

			loading = true;
			
			setGroupSelection();
			
    		StudyProgram sp = setProgramSelection();

    		if (sp == null) {
    			CatchupMathTools.showAlert("Program not found!");
    			return;
    		}
    		int needsSubject = ((Integer)sp.get("needsSubject")).intValue();
    		if (needsSubject != 0) setSubjectSelection();

    		int needsChapters = ((Integer)sp.get("needsChapters")).intValue();
    		if (needsChapters != 0) setChapterSelection();

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
	
	private StudyProgram setProgramSelection() {
		String shortName = stuMdl.getProgramDescr();
		
		if (shortName != null) {
			List<StudyProgram> list = progStore.getModels();
			for (StudyProgram sp : list) {
				if (shortName.indexOf((String) sp.get("shortTitle")) > -1) {
					progCombo.setOriginalValue(sp);
					progCombo.setValue(sp);
					return sp;
				}
			}
		}
		return null;
	}
	
	private void setSubjectSelection() {
		String shortName = stuMdl.getProgramDescr();

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

	
	private void getChapterListRPC(String progId, String subjId, final Boolean chapOnly, final ListStore <ChapterModel> chapStore) {

		if (progId == null || !progId.equalsIgnoreCase("chap")) return;
		
		inProcessCount++;
		CmServiceAsync s = (CmServiceAsync) Registry.get("cmService");
		GetChaptersForProgramSubjectAction action = new GetChaptersForProgramSubjectAction(progId, subjId);
		
		s.execute(action, new CmAsyncCallback <CmList<ChapterModel>> () {

			public void onSuccess(CmList<ChapterModel> result) {
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
        });
	
	}
	
	
	/** helper to simplify determing if user has tutoring enabled
	 * 
	 * @return
	 */
	private boolean getTutoringEnabled() {
        boolean tta = _tutoringEnabled.getValue()!=null?_tutoringEnabled.getValue().getValue():false;
        return tta;
	}

	/** Perform the form save operation and display any required validation.
	 * 
	 * Throws CmExeptionValidationFailed on failed validation attempt.
	 * 
	 * if callback == null, then default operation which is saving data is performed.
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
        
        CheckBoxGroup cbg = (CheckBoxGroup) fp.getItemByItemId(StudentModelExt.SHOW_WORK_KEY);
        CheckBox cbv = cbg.getValue();
        Boolean showWork = new Boolean(cbv != null);
        
        
        ComboBox<StudyProgram> cb = (ComboBox<StudyProgram>) fs.getItemByItemId("prog-combo");
        StudyProgram sp = cb.getValue();
        cb.clearInvalid();
        if (sp == null) {
            cb.focus();
            cb.forceInvalid(ENTRY_REQUIRED_MSG);
            cb.expand();
            throw new CmExceptionValidationFailed();
        }
        String prog = sp.get("shortTitle");

        ComboBox<SubjectModel> cs = (ComboBox<SubjectModel>) fs.getItemByItemId("subj-combo");
        SubjectModel sub = cs.getValue();
        cs.clearInvalid();
        if (sub != null) {
            prog = sub.get("abbrev") + " " + prog;
        }
        if (((Integer)sp.get("needsSubject")).intValue() > 0 && sub == null) {
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
        if (((Integer)sp.get("needsChapters")).intValue() > 0 && chap == null) {
            cc.focus();
            cc.forceInvalid(ENTRY_REQUIRED_MSG);
            cc.expand();
            throw new CmExceptionValidationFailed();
        }

        
        /** Collect all the values and create a new StudentModel
         *  to hold validated values.
         *  
         */
        StudentModel sm = new StudentModel();
        sm.setName(name);
        sm.setPasscode(passcode);
        //sm.setEmail(email);
        sm.setProgramDescr(prog);
        sm.setGroupId(groupId);
        sm.setTutoringAvail(getTutoringEnabled());
        sm.setShowWorkRequired(showWork);
        sm.setGroup(group);
        sm.setAdminUid(cmAdminMdl.getId());
        sm.setPassPercent("70%"); // static default
        String progId = (sp != null) ? (String)sp.get("shortTitle") : null;
        sm.setProgId(progId);
        String subjId = (sub != null) ? sub.getAbbrev() : "";
        sm.setSubjId(subjId);
        String chapTitle = (chap != null) ? chap.getTitle() : null;
        sm.setChapter(chapTitle);


        /** Validation complete 
         * 
         * if callback has been provided, then jump to call back
         * 
         */
        if(callback != null) {
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
            sm.setSectionNum(0);
            sm.setStatus("Not started");
            sm.setTotalUsage(0);
            addUserRPC(sm);
        }
        else {
            Boolean stuChanged = false;
            Boolean passcodeChanged = false;
            Boolean progChanged = false;
            Boolean progIsNew = false;
            
            sm.setUid(stuMdl.getUid());
            sm.setUserProgramId(stuMdl.getUserProgramId());
            sm.setJson(stuMdl.getJson());
            sm.setStatus(stuMdl.getStatus());
            sm.setSectionNum(stuMdl.getSectionNum());
            if (! name.equals(stuMdl.getName()) ||
                ! showWork.equals(stuMdl.getShowWorkRequired()) ||
                ! groupId.equals(stuMdl.getGroupId())) {
                stuChanged = true;
            }
            if (! passcode.equals(stuMdl.getPasscode())) {
                passcodeChanged = true;
                stuChanged = true;
            }
            
            if (stuMdl.getProgramDescr() == null || !stuMdl.getProgramDescr().equals(prog)) {
                sm.setStatus("Not started");
                sm.setSectionNum(0);
                sm.setProgramChanged(true);

                progIsNew = true;
                progChanged = false;
                stuChanged = true;
            }
            

            
            if(getTutoringEnabled() != stuMdl.getTutoringAvail()) {
                stuChanged = true;
            }
            
            
            if (stuChanged || progChanged || progIsNew) {
                updateUserRPC(sm, stuChanged, progChanged, progIsNew, passcodeChanged);
            }
            else {
                _window.close();
            }
        }	    
	}
	
	
	class StudyProgram extends BaseModelData {
		private static final long serialVersionUID = 5574506049604177840L;

		StudyProgram(String title, String shortTitle, String descr, Integer needsSubject, Integer needsChapters, Integer needsPassPercent) {
			set("title", title);
			set("shortTitle", shortTitle);
			set("descr", descr);
			set("needsSubject", needsSubject);
			set("needsChapters", needsChapters);
			set("needsPassPercent", needsPassPercent);
			
		}
	}


	//@Override
	public void beginStep() {
		inProcessCount++;
	}

	//@Override
	public void completeStep() {
		inProcessCount--;
	}

	//@Override
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

class CmExceptionValidationFailed extends CmException {
    
}

