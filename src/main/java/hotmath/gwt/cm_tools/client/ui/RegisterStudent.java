package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.ChapterModel;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupModel;
import hotmath.gwt.cm_tools.client.model.StudentModel;
import hotmath.gwt.cm_tools.client.model.StudyProgramModel;
import hotmath.gwt.cm_tools.client.model.SubjectModel;
import hotmath.gwt.cm_tools.client.service.PrescriptionServiceAsync;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.cm_tools.client.util.ProcessTracker;
import hotmath.gwt.shared.client.eventbus.CmEvent;
import hotmath.gwt.shared.client.eventbus.EventBus;
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
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RegisterStudent extends LayoutContainer implements ProcessTracker {
	
	protected CmWindow _window;
	
	private boolean isNew;
	private boolean skipComboSet;
	private boolean loading;
	
	private StudentModel stuMdl;
	protected CmAdminModel cmAdminMdl;
	private int inProcessCount;
	private String subjectId;
	
	private ListStore <StudyProgram> progStore;
	private ComboBox<StudyProgram> progCombo;
	
	private ListStore <SubjectModel> subjStore;
	private ComboBox<SubjectModel> subjCombo;
	
	private ListStore <PassPercent> passStore;
	private ComboBox <PassPercent> passCombo;
	
	private ListStore <ChapterModel> chapStore;
	private ComboBox <ChapterModel> chapCombo;
	
	private ListStore <GroupModel> groupStore;
	private ComboBox <GroupModel> groupCombo;
	
	private TextField<String> userName;
	
	private int formHeight = 410;
	protected int formWidth  = 475;
	
	protected CombinedFormPanel _formPanel;
	
	private static final String ENTRY_REQUIRED_MSG = "This field is required";
	
	public RegisterStudent(StudentModel sm, CmAdminModel cm) {
	    
	    EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_MODAL_WINDOW_OPEN));
	    
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
		        EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_MODAL_WINDOW_CLOSED));
		    }
		});
		_window.add(createForm());
		_window.show();
 		if (isNew) {
 		   userName.focus();
 		}
 	     
        skipComboSet = isNew;

		setComboBoxSelections();
	}
	

	/** Return list of Buttons to add to the Button bar
	 * 
	 * @return
	 */
	protected List<Button> getActionButtons() {
	    List<Button> list = new ArrayList<Button>();
        Button cancelBtn = cancelButton();
        cancelBtn.setStyleName("register-student-cancel");
        list.add(saveButton(_fsProgram, _formPanel));
        list.add(cancelButton());
        return list;
	}
	
	FieldSet _fsProfile, _fsProgram;
	protected FormPanel createForm() {
		_formPanel = new CombinedFormPanel();
		_formPanel.setStyleName("register-student-form-panel");
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
		
        groupStore = new ListStore <GroupModel> ();
		GroupSelectorWidget gsw = new GroupSelectorWidget(cmAdminMdl, groupStore, true, this, "group-combo");
		groupCombo = gsw.groupCombo();
		if(UserInfo.getInstance() == null || !UserInfo.getInstance().isSingleUser()) {
		    _fsProfile.add(groupCombo);
		}

		_formPanel.add(_fsProfile);

        _fsProgram = new FieldSet();
        _fsProgram.setHeading("Assign Program");
        _fsProgram.setStyleName("register-student-fieldset");
		
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
		
		List<PassPercent> passList = getPassPercentList();
		passStore = new ListStore <PassPercent> ();
		passStore.add(passList);
		passCombo = passPercentCombo(passStore);
		_fsProgram.add(passCombo);
		
        CheckBox isShowWorkRequired = new CheckBox();
        isShowWorkRequired.setBoxLabel("(recommended)");
        isShowWorkRequired.setId(StudentModel.SHOW_WORK_KEY);
        if (! isNew) {
        	isShowWorkRequired.setValue(stuMdl.getShowWorkRequired());
        }
        else {
        	// require 'Show Work' by default
        	isShowWorkRequired.setValue(true);
        }

        CheckBoxGroup showWorkGrp = new CheckBoxGroup(); 
        showWorkGrp.setFieldLabel("Require Show Work");
        showWorkGrp.setId(StudentModel.SHOW_WORK_KEY);
        showWorkGrp.add(isShowWorkRequired);
        _fsProgram.add(showWorkGrp);
        
        CheckBox isTutoringNotAvail = new CheckBox();
        isTutoringNotAvail.setBoxLabel("(if/when enabled)");
        isTutoringNotAvail.setId(StudentModel.TUTORING_AVAIL_KEY);
        if (! isNew) {
        	// we save is_tutoring_available; form displays 'Disable tutoring'; hence, value is negated
        	isTutoringNotAvail.setValue(! stuMdl.getTutoringAvail());
        }
        else {
        	// disable tutoring by default
        	isTutoringNotAvail.setValue(true);
        }
        CheckBoxGroup tutoringGrp = new CheckBoxGroup();  
        tutoringGrp.setFieldLabel("Disallow Tutoring");
        tutoringGrp.setId(StudentModel.TUTORING_AVAIL_KEY);
        tutoringGrp.add(isTutoringNotAvail);
        _fsProgram.add(tutoringGrp);
		
        _formPanel.add(_fsProgram);

        _window.setHeading((isNew)?"Register a New Student":"Edit Student");
        _window.setWidth(formWidth + 40);
        _window.setHeight(formHeight + 20);
        _window.setLayout(new FitLayout());
        _window.setResizable(false);
        _window.setDraggable(true);
        _window.setModal(true);


        /** Assign buttons to the button bar
         * 
         */
        _formPanel.setButtonAlign(HorizontalAlignment.RIGHT);
        for(Button btn: getActionButtons()) {
            btn.setStyleName("register-student-btn");
            _formPanel.addButton(btn);
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
				int needsPassPercent = ((Integer)sp.get("needsPassPercent")).intValue();
				
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
	        	ComboBox <PassPercent> cp = (ComboBox<PassPercent>) fs.getItemByItemId("pass-combo");
	        	if (needsPassPercent > 0) {
	        		cp.clearSelections();
	        		cp.enable();
	        		cp.setForceSelection(true);
	        	}
	        	else {
	        		cp.clearInvalid();
	        		cp.clearSelections();
	        		cp.disable();
	        		cp.setForceSelection(false);
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
	
	private ComboBox<PassPercent> passPercentCombo(ListStore<PassPercent> store) {
		ComboBox<PassPercent> combo = new ComboBox<PassPercent>();
		combo.setValue(store.getAt(2));
		combo.setFieldLabel("Pass Percent");
		combo.setForceSelection(false);
		combo.setDisplayField("pass-percent");
		combo.setEditable(false);
		combo.setMaxLength(30);
		combo.setAllowBlank(false);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setStore(store);
		combo.setTitle("Select a percentage");
		combo.setId("pass-combo");
		combo.setTypeAhead(true);
		combo.setSelectOnFocus(true);
		combo.setEmptyText("-- select a value --");
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
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getProgramDefinitions(new AsyncCallback<List<StudyProgramModel>>() {

			public void onSuccess(List<StudyProgramModel> spmList) {
				List<StudyProgram> progList = new ArrayList <StudyProgram> ();
				for (StudyProgramModel spm : spmList) {
					progList.add(new StudyProgram(spm.getTitle(), spm.getShortTitle(), spm.getDescr(), spm.getNeedsSubject(),
							spm.getNeedsChapters(), spm.getNeedsPassPercent()));
				}
				progStore.add(progList);
				inProcessCount--;
				setComboBoxSelections();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
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
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getSubjectDefinitions(progId, new AsyncCallback <List<SubjectModel>>() {

			public void onSuccess(List<SubjectModel> result) {
				subjStore.removeAll();
				subjStore.add(result);
				inProcessCount--;
				programSubjectMap.put(progId, result);
				setComboBoxSelections();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
        	}
        });
		
	}
		
	protected void addUserRPC(final StudentModel sm) {
	    PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		
		s.addUser(sm, new AsyncCallback <StudentModel> () {
			
			public void onSuccess(StudentModel ai) {
			    EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_USER_PROGRAM_CHANGED,ai.getProgramChanged()));
			    _window.close();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
        	}
        });
	}

	protected void updateUserRPC(final StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
			Boolean passcodeChanged) {
	    PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		
		s.updateUser(sm, stuChanged, progChanged, progIsNew, passcodeChanged, new AsyncCallback <StudentModel> () {
			
			public void onSuccess(StudentModel ai) {
		        EventBus.getInstance().fireEvent(new CmEvent(EventBus.EVENT_TYPE_USER_PROGRAM_CHANGED,ai.getProgramChanged()));
		        _window.close();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
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

    		int needsPassPercent = ((Integer)sp.get("needsPassPercent")).intValue();
    		if (needsPassPercent != 0) setPassPercentSelection();

            loading = false;
		}
	}

	private void setGroupSelection() {
	    if(stuMdl == null) {
	        new Exception().printStackTrace();
	        return;
	    }
	    
		String groupId = stuMdl.getGroupId();
		if (groupId != null) {
			List<GroupModel> l = groupStore.getModels();
			for (GroupModel g : l) {
				if (groupId.equals(g.getId())) {
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

	private void setPassPercentSelection() {
		String passPercent = stuMdl.getPassPercent();
		
		if (passPercent != null) {
			List<PassPercent> list = passStore.getModels();
			for (PassPercent p : list) {
				if (passPercent.equals(p.getPassPercent())) {
					passCombo.setOriginalValue(p);
					passCombo.setValue(p);
					passCombo.enable();
					break;
				}
			}
		}
	}

	private void getChapterListRPC(String progId, String subjId, final Boolean chapOnly, final ListStore <ChapterModel> chapStore) {

		if (progId == null || !progId.equalsIgnoreCase("chap")) return;
		
		inProcessCount++;
		PrescriptionServiceAsync s = (PrescriptionServiceAsync) Registry.get("prescriptionService");
		s.getChaptersForProgramSubject(progId, subjId, new AsyncCallback <List<ChapterModel>> () {

			public void onSuccess(List<ChapterModel> result) {
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

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathTools.showAlert(msg);
        	}
        });
	
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
        ComboBox<GroupModel> cg = (ComboBox<GroupModel>) fp.getItemByItemId("group-combo");
        if(cg != null) {
            cg.clearInvalid();
            if(cg != null) {
                GroupModel g = cg.getValue();
                if (g == null) {
                    cg.focus();
                    cg.forceInvalid(ENTRY_REQUIRED_MSG);
                    cg.expand();
                    throw new CmExceptionValidationFailed();
                }
                groupId = g.getId();
                group = g.getName();
            }
            else {
                groupId = "1";
                group = "none";
            }
        }
        
        CheckBoxGroup cbg = (CheckBoxGroup) fp.getItemByItemId(StudentModel.SHOW_WORK_KEY);
        CheckBox cbv = cbg.getValue();
        Boolean showWork = new Boolean(cbv != null);
        
        cbg = (CheckBoxGroup) fp.getItemByItemId(StudentModel.TUTORING_AVAIL_KEY);
        cbv = cbg.getValue();
        Boolean tutoring = new Boolean(cbv == null);
        
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

        ComboBox<PassPercent> cp = (ComboBox<PassPercent>) fs.getItemByItemId("pass-combo");
        PassPercent pass = cp.getValue();
        cp.clearInvalid();
        if (((Integer)sp.get("needsPassPercent")).intValue() > 0 && pass == null) {
            cp.focus();
            cp.forceInvalid(ENTRY_REQUIRED_MSG);
            cp.expand();
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
        sm.setTutoringAvail(tutoring);
        sm.setShowWorkRequired(showWork);
        sm.setGroup(group);
        sm.setAdminUid(cmAdminMdl.getId());
        String passVal = (pass != null) ? pass.getPassPercent() : null;
        sm.setPassPercent(passVal);
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
                ! tutoring.equals(stuMdl.getTutoringAvail()) ||
                ! showWork.equals(stuMdl.getShowWorkRequired()) ||
                ! groupId.equals(stuMdl.getGroupId())) {
                stuChanged = true;
            }
            if (! passcode.equals(stuMdl.getPasscode())) {
                passcodeChanged = true;
                stuChanged = true;
            }
            
            String oldPassVal = stuMdl.getPassPercent();
            String newPassVal = (pass != null) ? pass.getPassPercent() : null;
            
            if (! (newPassVal == null && oldPassVal == null)) {
                if (newPassVal == null && oldPassVal != null ||
                    newPassVal != null && oldPassVal == null ||
                    ! newPassVal.equals(oldPassVal)) {
                    progChanged = true;
                }
            }
            if (stuMdl.getProgramDescr() == null || !stuMdl.getProgramDescr().equals(prog)) {
                sm.setStatus("Not started");
                sm.setSectionNum(0);
                sm.setProgramChanged(true);

                progIsNew = true;
                progChanged = false;
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
	
	
	private List<PassPercent> getPassPercentList() {
		List<PassPercent> list = new ArrayList<PassPercent> ();
		list.add(new PassPercent("60%"));
		list.add(new PassPercent("70%"));
		list.add(new PassPercent("80%"));
		list.add(new PassPercent("90%"));
		list.add(new PassPercent("100%"));
		return list;
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

	class PassPercent extends BaseModelData {

		private static final long serialVersionUID = 6852777405039991570L;

		PassPercent(String percent) {
			set("pass-percent", percent);
		}
		
		String getPassPercent() {
			return get("pass-percent");
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

