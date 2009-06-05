package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_admin.client.CatchupMathAdmin;
import hotmath.gwt.cm_admin.client.model.ChapterModel;
import hotmath.gwt.cm_admin.client.model.CmAdminModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudyProgramModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;
import hotmath.gwt.cm_admin.client.service.RegistrationServiceAsync;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RegisterStudent extends LayoutContainer {
	
	private Window fw;
	final Grid<StudentModel> eg;
	private boolean isNew;
	private StudentModel stuMdl;
	private CmAdminModel cmAdminMdl;
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
	
	private FieldSet fs;
	
	private int formHeight = 380;
	private int formWidth  = 340;
	
	public RegisterStudent(final Grid<StudentModel> grid, StudentModel sm, CmAdminModel cm) {
		inProcessCount = 0;
		isNew = (sm == null);
		stuMdl = sm;
		if (stuMdl != null) {
			subjectId = stuMdl.getSubjId();
		}
		cmAdminMdl = cm;
		eg = grid;
		fw = new Window();
		fw.add(createForm());
 		fw.show();
		setComboBoxSelections();
	}
	
	private FormPanel createForm() {
		final CombinedFormPanel fp = new CombinedFormPanel();
		fp.setStyleName("register-student-form-panel");
		fp.setLabelWidth(75);
		fp.setHeight(formHeight);
		fp.setFooter(true);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.setLayout(new FormLayout());
		
		FormLayout fm = (FormLayout)fp.getLayout();
		
		FieldSet fs = new FieldSet();
		FormLayout fL = new FormLayout();
		fL.setLabelWidth(fm.getLabelWidth());
        fL.setDefaultWidth(175);
	    fs.setLayout(fL);
	    
		fs.setHeading("Define User Login");
		TextField<String> name = new TextField<String>();  
		name.setFieldLabel("Name");
		name.focus();
		name.setAllowBlank(false);
		name.setId("name");
		name.setEmptyText("-- enter name --");
		if (! isNew) {
			name.setValue((String)stuMdl.getName());
		}
		fs.add(name);
		TextField<String> passCode = new TextField<String>();
		passCode.setFieldLabel("Passcode");
		passCode.setEmptyText("-- enter passcode --");
		passCode.setAllowBlank(false);
		passCode.setId("passcode");
		if (! isNew) {
			passCode.setValue((String)stuMdl.getPasscode());
		}
		fs.add(passCode);

/* don't need email field for now
		TextField<String> email = new TextField<String>();
		email.setFieldLabel("Email");
		email.setEmptyText("-- enter email --");
		email.setAllowBlank(false);
		//TODO: define Validator
		//email.setValidator(validator);
		email.setId(StudentModel.EMAIL_KEY);
		if (! isNew) {
			email.setValue((String)stuMdl.getEmail());
		}
		fp.add(email);
*/

		groupStore = new ListStore <GroupModel> ();
		getGroupListRPC(cmAdminMdl.getId(), groupStore);
		groupCombo = groupCombo(groupStore);
		fs.add(groupCombo);
        fp.add(fs);


		
		fs = new FieldSet();
		fs.setHeading("Assign Program");
		fs.setStyleName("register-student-fieldset");
		
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(fL.getLabelWidth());
		fl.setDefaultWidth(fL.getDefaultWidth());
		
		// this should be in CSS
		// fl.setPadding(5);
		
		fs.setLayout(fl);
		
		progStore = new ListStore <StudyProgram> ();
		getStudyProgramListRPC(progStore);
		progCombo = programCombo(progStore, fs);
		fs.add(progCombo);
		
		subjStore = new ListStore <SubjectModel> ();
		getSubjectListRPC(subjStore);
		subjCombo = subjectCombo(subjStore);
		fs.add(subjCombo);

		chapStore = new ListStore <ChapterModel> ();
        getChapterListRPC((stuMdl != null)?stuMdl.getProgId():null, subjectId, false, chapStore);
		chapCombo = chapterCombo(chapStore);
		fs.add(chapCombo);        
		
		List<PassPercent> passList = getPassPercentList();
		passStore = new ListStore <PassPercent> ();
		passStore.add(passList);
		passCombo = passPercentCombo(passStore);
		fs.add(passCombo);
		
		fp.add(fs);

		fw.setHeading((isNew)?"Register a New Student":"Edit Student");
		fw.setWidth(formWidth + 40);
		fw.setHeight(formHeight+20);
		fw.setLayout(new FitLayout());
		fw.setResizable(false);
		fw.setDraggable(true);
		fw.setModal(true);

		Button cancelBtn = cancelButton();
        cancelBtn.setStyleName("register-student-cancel");
        
		Button saveBtn = saveButton(fs, fp);
		saveBtn.setStyleName("register-student-btn");
		
		fp.setButtonAlign(HorizontalAlignment.RIGHT);  
        fp.addButton(saveBtn);
        fp.addButton(cancelBtn);
		return fp;
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
	            StudyProgram sp = se.getSelectedItem();
				int needsSubject = ((Integer)sp.get("needsSubject")).intValue();
				int needsChapters = ((Integer)sp.get("needsChapters")).intValue();
				int needsPassPercent = ((Integer)sp.get("needsPassPercent")).intValue();
				
	        	ComboBox <SubjectModel> cb = (ComboBox<SubjectModel>) fs.getItemByItemId("subj-combo");
	        	if (needsSubject > 0) {
	        		cb.enable();
	        		cb.setForceSelection(true);
	        	}
	        	else {
	        		cb.clearSelections();
	        		cb.disable();
	        		cb.setForceSelection(false);
	        		subjectId = null;
	        	}
	        	ComboBox <ChapterModel> cc = (ComboBox<ChapterModel>) fs.getItemByItemId("chap-combo");
	        	if (needsChapters > 0) {
	        		cc.enable();
	        		cc.setForceSelection(true);
	        	}
	        	else {
	        		cc.clearSelections();
	        		cc.disable();
	        		cc.setForceSelection(false);
	        	}
	        	ComboBox <PassPercent> cp = (ComboBox<PassPercent>) fs.getItemByItemId("pass-combo");
	        	if (needsPassPercent > 0) {
	        		cp.enable();
	        		cp.setForceSelection(true);
	        	}
	        	else {
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
			public void selectionChanged(SelectionChangedEvent<SubjectModel> se) {
	        	SubjectModel sm = se.getSelectedItem();
	        	if (subjectId == null || ! subjectId.equals(sm.getAbbrev())) {
		        	//System.out.println("old: " + ((subjectId==null)?"none":subjectId) + ", new: " + sm.getAbbrev());
		        	subjectId = sm.getAbbrev();
		        	ComboBox<StudyProgram> cb = (ComboBox<StudyProgram>) fs.getItemByItemId("prog-combo");
		        	StudyProgram sp = cb.getValue();
		        	String progId = sp.get("shortTitle");
		        	chapStore.removeAll();
		            getChapterListRPC(progId, subjectId, true, chapStore);
	        	}
	        }
	    });
		return combo;
	}

	private ComboBox<GroupModel> groupCombo(ListStore<GroupModel> store) {
		ComboBox<GroupModel> combo = new ComboBox<GroupModel>();
		combo.setFieldLabel("Group");
		combo.setValue(store.getAt(0));	
		combo.setForceSelection(false);
		combo.setDisplayField(GroupModel.NAME_KEY);
		combo.setEditable(false);
		combo.setMaxLength(30);
		combo.setAllowBlank(false);
		combo.setTriggerAction(TriggerAction.ALL);
		combo.setStore(store);
		combo.setTitle("Select a group");
		combo.setId("group-combo");
		combo.setTypeAhead(true);
		combo.setSelectOnFocus(true);
		combo.setEmptyText("-- select a group --");
		//combo.disable();
		combo.setWidth(280);
		return combo;
	}
	
	private ComboBox<ChapterModel> chapterCombo(ListStore<ChapterModel> store) {
		ComboBox<ChapterModel> combo = new ComboBox<ChapterModel>();
		combo.setFieldLabel("Chapter");
		combo.setForceSelection(false);
		combo.setDisplayField("chapter");
		combo.setEditable(false);
		combo.setMaxLength(60);
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
	        	fw.close();
	        }  
	    });
		return cancelBtn;
	}

	private Button saveButton(final FieldSet fs, final CombinedFormPanel fp) {
		Button saveBtn = new Button("Save", new SelectionListener<ButtonEvent>() {  
	        @SuppressWarnings("unchecked")
			@Override  
	    	public void componentSelected(ButtonEvent ce) {
	        	TextField<String> tf = (TextField<String>)fp.getItemByItemId("name");
	        	String name = tf.getValue();
	        	if (name == null) {
	        		tf.focus();
	        		return;
	        	}
	        	tf = (TextField<String>)fp.getItemByItemId("passcode");
	        	String passcode = tf.getValue();
	        	if (passcode == null) {
	        		tf.focus();
	        		return;
	        	}
/* don't need email field for now
	        	tf = (TextField<String>)fp.getItemByItemId(StudentModel.EMAIL_KEY);
	        	String email = tf.getValue();
	        	if (email == null) {
	        		tf.focus();
	        		return;
	        	}
*/
	        	ComboBox<GroupModel> cg = (ComboBox<GroupModel>) fp.getItemByItemId("group-combo");
	        	GroupModel g = cg.getValue();
	        	if (g == null) {
	        		cg.focus();
	        		return;
	        	}
	        	String groupId = g.getId();
	        	String group = g.getName();
	        	
	        	ComboBox<StudyProgram> cb = (ComboBox<StudyProgram>) fs.getItemByItemId("prog-combo");
	        	StudyProgram sp = cb.getValue();
	        	if (sp == null) {
	        		cb.focus();
	        		return;
	        	}
	        	String prog = sp.get("shortTitle");

	        	ComboBox<SubjectModel> cs = (ComboBox<SubjectModel>) fs.getItemByItemId("subj-combo");
	        	SubjectModel sub = cs.getValue();
	        	if (sub != null) {
	        		prog = sub.get("abbrev") + " " + prog;
	        	}
	        	if (((Integer)sp.get("needsSubject")).intValue() > 0 && sub == null) {
	        		cs.focus();
	        		return;
	        	}

	        	ComboBox<ChapterModel> cc = (ComboBox<ChapterModel>) fs.getItemByItemId("chap-combo");
	        	ChapterModel chap = cc.getValue();
	        	if (chap != null) {
	        		prog = prog + " " + chap.get("number");
	        	}
	        	if (((Integer)sp.get("needsChapters")).intValue() > 0 && chap == null) {
	        		cc.focus();
	        		return;
	        	}

	        	ComboBox<PassPercent> cp = (ComboBox<PassPercent>) fs.getItemByItemId("pass-combo");
	        	PassPercent pass = cp.getValue();
	        	if (((Integer)sp.get("needsPassPercent")).intValue() > 0 && pass == null) {
	        		cp.focus();
	        		return;
	        	}

	        	StudentModel sm = new StudentModel();
	        	sm.setName(name);
	        	sm.setPasscode(passcode);
	        	//sm.setEmail(email);
	        	sm.setProgramDescr(prog);
	        	sm.setGroupId(groupId);
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
	        	
		        if (isNew) {	        	    
		        	sm.setSectionNum(0);
		        	sm.setStatus("Not started");
		        	sm.setTotalUsage("0");
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
	        		
	        		if (! name.equals(stuMdl.getName())) {
	        			stuChanged = true;
	        		}
	        		if (! passcode.equals(stuMdl.getPasscode())) {
	        			passcodeChanged = true;
	        			stuChanged = true;
	        		}
/* don't need email field for now
	        		if (! email.equals(stuMdl.getEmail())) {
	        			stuChanged = true;
	        		}
*/
	        		if (! groupId.equals(stuMdl.getGroupId())) {
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
	        	}
	        }
	    });
		return saveBtn;
	}

	private void getStudyProgramListRPC(final ListStore <StudyProgram> progStore) {

		inProcessCount++;
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
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
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	}

	private void getSubjectListRPC(final ListStore <SubjectModel> subjStore) {
		
		inProcessCount++;
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		s.getSubjectDefinitions(new AsyncCallback <List<SubjectModel>>() {

			public void onSuccess(List<SubjectModel> result) {
				subjStore.add(result);
				inProcessCount--;
				setComboBoxSelections();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
		
	}
	
	private void getGroupListRPC(Integer uid, final ListStore <GroupModel> store) {
		
		inProcessCount++;
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		s.getActiveGroups(uid, new AsyncCallback <List<GroupModel>>() {

			public void onSuccess(List<GroupModel> result) {
				groupStore.add(result);
				inProcessCount--;
				setComboBoxSelections();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
		
	}
	
	protected void addUserRPC(final StudentModel sm) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		
		s.addUser(sm, new AsyncCallback <StudentModel> () {
			
			public void onSuccess(StudentModel ai) {
				StudentGridPanel.instance.refreshDataNow(ai.getUid());
				fw.close();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	}

	protected void updateUserRPC(final StudentModel sm, Boolean stuChanged, Boolean progChanged, Boolean progIsNew,
			Boolean passcodeChanged) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		
		s.updateUser(sm, stuChanged, progChanged, progIsNew, passcodeChanged, new AsyncCallback <StudentModel> () {
			
			public void onSuccess(StudentModel ai) {
			    StudentGridPanel.instance.refreshDataNow(null);
				fw.close();
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
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
		if (! isNew && inProcessCount < 1) {
			
			setGroupSelection();
			
    		StudyProgram sp = setProgramSelection();

    		if (sp == null) {
    			CatchupMathAdmin.showAlert("Program not found!");
    			return;
    		}
    		int needsSubject = ((Integer)sp.get("needsSubject")).intValue();
    		if (needsSubject != 0) setSubjectSelection();

    		int needsChapters = ((Integer)sp.get("needsChapters")).intValue();
    		if (needsChapters != 0) setChapterSelection();

    		int needsPassPercent = ((Integer)sp.get("needsPassPercent")).intValue();
    		if (needsPassPercent != 0) setPassPercentSelection();

		}
	}

	private void setGroupSelection() {
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
				String pass = p.getPassPercent();
				if (passPercent.equals(p.getPassPercent())) {
					passCombo.setOriginalValue(p);
					passCombo.setValue(p);
					break;
				}
			}
		}
	}

	// TODO obtain from service
	private List<ChapterModel> getChapterList(String subjId) {
		List<ChapterModel> list = new ArrayList<ChapterModel> ();
		list.add(new ChapterModel("1", "Solving Linear Equations"));
		list.add(new ChapterModel("2", "Graphing Linear Equations"));
		list.add(new ChapterModel("3", "Proportions"));
		list.add(new ChapterModel("4", "Inequalities and Absolute Value Equations"));
		list.add(new ChapterModel("5", "Solving Linear Systems"));
		list.add(new ChapterModel("6", "Exponents, Polynomials and Factoring"));

		return list;
	}
	
	private void getChapterListRPC(String progId, String subjId, final Boolean chapOnly, final ListStore <ChapterModel> chapStore) {
		if (progId == null || !progId.equalsIgnoreCase("chap")) return;
		
		inProcessCount++;
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
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
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	
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
}



/** Search for field in nested FieldSets
 * 
 * @author casey
 *
 */
class CombinedFormPanel extends FormPanel {
    public Component getItemByItemId(String itemId) {
        
        Component foundObject=super.getItemByItemId(itemId);
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