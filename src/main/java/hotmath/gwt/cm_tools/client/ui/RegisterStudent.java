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
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
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
	
	private int formHeight = 350;
	private int formWidth  = 340;
	
	public RegisterStudent(final Grid<StudentModel> grid, StudentModel sm, CmAdminModel cm) {
		inProcessCount = 0;
		isNew = (sm == null);
		stuMdl = sm;
		cmAdminMdl = cm;
		eg = grid;
		fw = new Window();
		fw.add(createForm());
 		fw.show();
		setComboBoxSelections();
	}
	
	private FormPanel createForm() {
		final FormPanel fp = new FormPanel();
		fp.setLabelWidth(75);
		fp.setWidth(formWidth);
		fp.setHeight(formHeight);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setStyleAttribute("padding", "10px 10px 5px 10px");
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.setLayout(new FormLayout());
		
		TextField<String> name = new TextField<String>();  
		name.setFieldLabel("Name");
		name.focus();
		name.setAllowBlank(false);
		name.setId("name");
		name.setEmptyText("-- enter name --");
		if (! isNew) {
			name.setValue((String)stuMdl.getName());
		}
		fp.add(name);

		TextField<String> passCode = new TextField<String>();
		passCode.setFieldLabel("Passcode");
		passCode.setEmptyText("-- enter passcode --");
		passCode.setAllowBlank(false);
		passCode.setId("passcode");
		if (! isNew) {
			passCode.setValue((String)stuMdl.getPasscode());
		}
		fp.add(passCode);

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

		groupStore = new ListStore <GroupModel> ();
		//TODO: use admin id
		getGroupListRPC(cmAdminMdl.getPassCode(), groupStore);
		groupCombo = groupCombo(groupStore);
		fp.add(groupCombo);
		
		FieldSet fs = new FieldSet();
		fs.setHeading("&nbsp;Select Program&nbsp;");
		fs.setStyleAttribute("margin-top", "20px");
		
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(85);
		
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

        List<ChapterModel> chapList = getChapterList();
		chapStore = new ListStore <ChapterModel> ();
		chapStore.add(chapList);
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

		LayoutContainer buttonHolder = new LayoutContainer();
        buttonHolder.setStyleName("register-student-btn-holder");

		Button cancelBtn = cancelButton();
        cancelBtn.setStyleName("register-student-cancel");
		buttonHolder.add(cancelBtn);

		Button saveBtn = saveButton(fs, fp);
		saveBtn.setStyleName("register-student-btn");
        buttonHolder.add(saveBtn);

		fp.add(buttonHolder);

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
		return combo;
	}

	private ComboBox<GroupModel> groupCombo(ListStore<GroupModel> store) {
		ComboBox<GroupModel> combo = new ComboBox<GroupModel>();
		combo.setFieldLabel("Group");
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
		combo.setMaxLength(30);
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

	private Button saveButton(final FieldSet fs, final FormPanel fp) {
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
	        	tf = (TextField<String>)fp.getItemByItemId(StudentModel.EMAIL_KEY);
	        	String email = tf.getValue();
	        	if (email == null) {
	        		tf.focus();
	        		return;
	        	}

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

	        	if (isNew) {
		        	StudentModel sm = new StudentModel();
		        	sm.setName(name);
		        	sm.setPasscode(passcode);
		        	sm.setEmail(email);
		        	sm.setProgramDescr(prog);
		        	sm.setGroup(groupId);
		        	sm.setStatus("Not started");

	        	    eg.getStore().add(sm);
	        	    
	        	    addUserRPC(stuMdl);

	        	    //TODO: update DB - HA_TEST
	        	}
	        	else {
	        		boolean stuChanged = false;
	        		boolean progChanged = false;
	        		if (! name.equals(stuMdl.getName())) {
	        			stuMdl.setName(name);
	        			stuChanged = true;
	        		}
	        		if (! passcode.equals(stuMdl.getPasscode())) {
	        			stuMdl.setPasscode(passcode);
	        			stuChanged = true;
	        		}
	        		if (! email.equals(stuMdl.getEmail())) {
	        			stuMdl.setEmail(email);
	        			stuChanged = true;
	        		}
	        		if (! groupId.equals(stuMdl.getGroupId())) {
	        			stuMdl.setGroupId(groupId);
	        			stuMdl.setGroup(group);
	        			stuChanged = true;
	        		}
	        		if (! pass.getPassPercent().equals(stuMdl.getPassPercent())) {
			            stuMdl.setPassPercent(pass.getPassPercent());
			            stuChanged = true;
	        		}
		        	if (! stuMdl.getProgramDescr().equals(prog)) {
			        	stuMdl.setProgramDescr(prog);
			        	stuMdl.setStatus("Not started");
			        	progChanged = true;
	        		}
		        	if (stuChanged || progChanged) {
     	        		eg.getStore().update(stuMdl);
     	        		
     	        		if (stuChanged) {
     	        			updateUserRPC(stuMdl);
     	        		}
     	        		if (progChanged) {
     	        			//TODO: update DB - HA_TEST
     	        		}
		        	}
	        	}

	        	fw.close();
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
	
	private void getGroupListRPC(String adminPasscode, final ListStore <GroupModel> store) {
		
		inProcessCount++;
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		s.getActiveGroups(adminPasscode, new AsyncCallback <List<GroupModel>>() {

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
	
	protected void addUserRPC(StudentModel sm) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		
		s.addUser(sm, new AsyncCallback <StudentModel> () {
			
			public void onSuccess(StudentModel ai) {
				// empty
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
	}

	protected void updateUserRPC(StudentModel sm) {
		RegistrationServiceAsync s = (RegistrationServiceAsync) Registry.get("registrationService");
		
		s.updateUser(sm, new AsyncCallback <StudentModel> () {
			
			public void onSuccess(StudentModel ai) {
				// empty
        	}

			public void onFailure(Throwable caught) {
        		String msg = caught.getMessage();
        		CatchupMathAdmin.showAlert(msg);
        	}
        });
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
		String prog = stuMdl.getProgramDescr();

		if (prog != null) {
			int offset = prog.indexOf("Chap") + 4;
			if (offset > 4 && offset < prog.length()) {
				String chap = prog.substring(offset).trim();
				List <ChapterModel> list = chapStore.getModels();
				for (ChapterModel c : list) {
					if (chap.equals(c.getNumber())) {
						chapCombo.setOriginalValue(c);
						chapCombo.setValue(c);
						break;
					}
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
	private List<ChapterModel> getChapterList() {
		List<ChapterModel> list = new ArrayList<ChapterModel> ();
		list.add(new ChapterModel("1", "Solving Linear Equations"));
		list.add(new ChapterModel("2", "Graphing Linear Equations"));
		list.add(new ChapterModel("3", "Proportions"));
		list.add(new ChapterModel("4", "Inequalities and Absolute Value Equations"));
		list.add(new ChapterModel("5", "Solving Linear Systems"));
		list.add(new ChapterModel("6", "Exponents, Polynomials and Factoring"));

		return list;
	}
	
	private List<ChapterModel> getChapterListRPC(SubjectModel subject) {
		return null;
	}
	
	private List<PassPercent> getPassPercentList() {
		List<PassPercent> list = new ArrayList<PassPercent> ();
		list.add(new PassPercent("60 %"));
		list.add(new PassPercent("70 %"));
		list.add(new PassPercent("80 %"));
		list.add(new PassPercent("90 %"));
		list.add(new PassPercent("100 %"));
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
