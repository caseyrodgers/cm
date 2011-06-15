package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.SectionNumber;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentSettingsModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * Register Student Advanced Options UI
 * 
 * set pass percent, require show work, enable tutoring, disallow games
 * 
 * reset active section if in Proficiency or Grad Prep program
 * 
 * @author bob
 *
 */
public class RegisterStudentAdvancedOptions extends LayoutContainer {
	
	private CmWindow advOptWindow;

	private FieldSet advOptions;
	private CheckBox isShowWorkRequired;
	private CheckBox isGamesLimited;
	private CheckBox isStopAtProgramEnd;
	private CheckBoxGroup requireShowWork;
	private CheckBoxGroup limitGames;
	private CheckBoxGroup stopAtProgramEnd;

	private ComboBox <PassPercent> passCombo;
	private ComboBox <SectionNumber> sectionCombo;

	private FieldSet fs;
	private CmAdminModel cmAdminMdl;
	private int formHeight = 240;
	private int formWidth  = 340;
	private AdvOptCallback callback;
	private boolean passPercentReqd;
	private Map<String,Object> advOptionsMap;

	private int sectionCount;
	private String currentSection;
	private boolean sectionIsSettable;
	
	public RegisterStudentAdvancedOptions(AdvOptCallback callback, CmAdminModel cm, Map <String,Object> optionMap, boolean isNew,
		boolean passPercentReqd) {

		this.callback = callback;
		this.cmAdminMdl = cm;
		this.advOptionsMap = optionMap;
		this.passPercentReqd = passPercentReqd;
		this.currentSection = String.valueOf((Integer) advOptionsMap.get(StudentModelExt.SECTION_NUM_KEY));
		this.sectionCount = (Integer) advOptionsMap.get(StudentModelExt.SECTION_COUNT_KEY);
		this.sectionIsSettable = (Boolean) advOptionsMap.get("section-is-settable");

		advOptWindow = new CmWindow();
		advOptWindow.add(optionsForm(isNew, passPercentReqd));
		
		setForm();

	}
	
	private FormPanel optionsForm(boolean isNew, boolean passPercentReqd) {
		FormPanel fp = new FormPanel();
		fp.setLabelWidth(180);
		fp.setHeight(formHeight);
		fp.setFooter(true);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.setLayout(new FormLayout());

        advOptions = new FieldSet();
        
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(fp.getLabelWidth());
		fl.setDefaultWidth(100);
		
		advOptions.setLayout(fl);
        advOptions.addStyleName("register-student-fieldset");

        passCombo = new PassPercentCombo(passPercentReqd);
        setPassPercentSelection();
		advOptions.add(passCombo);

		isShowWorkRequired = new CheckBox();
        isShowWorkRequired.setId("show_work");
        isShowWorkRequired.setValue(((StudentSettingsModel) advOptionsMap.get(StudentModelExt.SETTINGS_KEY)).getShowWorkRequired());
        requireShowWork = new CheckBoxGroup(); 
        requireShowWork.setFieldLabel("Require Show Work");
        requireShowWork.setId("show_work");
        requireShowWork.add(isShowWorkRequired);
        advOptions.add(requireShowWork);

        isGamesLimited = new CheckBox();
        isGamesLimited.setId("limit_games");
        isGamesLimited.setValue(((StudentSettingsModel) advOptionsMap.get(StudentModelExt.SETTINGS_KEY)).getLimitGames());
        isGamesLimited.setToolTip("If checked, then no Games can be played.");
        
        limitGames = new CheckBoxGroup();
        limitGames.setFieldLabel("Disallow Games");
        limitGames.setId("limit_games");
		limitGames.add(isGamesLimited);
		
		advOptions.add(limitGames); 

        isStopAtProgramEnd = new CheckBox();
        isStopAtProgramEnd.setId("stop_at_program_end");
        isStopAtProgramEnd.setValue(((StudentSettingsModel) advOptionsMap.get(StudentModelExt.SETTINGS_KEY)).getStopAtProgramEnd());
        stopAtProgramEnd = new CheckBoxGroup();
        stopAtProgramEnd.setFieldLabel("Stop at End of Program");
        stopAtProgramEnd.setId("stop_at_program_end");
		stopAtProgramEnd.add(isStopAtProgramEnd);
		advOptions.add(stopAtProgramEnd);

		if (sectionIsSettable) {
    		sectionCombo = new SectionNumberCombo(sectionCount);
            setSectionNumberSelection();
		    advOptions.add(sectionCombo);
        }

		advOptWindow.setHeading((isNew)?"Set Options":"Edit Options");
		advOptWindow.setWidth(formWidth+10);
		advOptWindow.setHeight(formHeight+20);
		advOptWindow.setLayout(new FitLayout());
		advOptWindow.setResizable(false);
		advOptWindow.setDraggable(true);
		advOptWindow.setModal(true);

		fp.add(advOptions);
		
		Button resetBtn = resetButton(fp);
		//resetBtn.addStyleName("reset-button");

		Button cancelBtn = cancelButton();
        cancelBtn.addStyleName("cancel-button");
        
		Button saveBtn = saveButton(fs, fp);
		saveBtn.addStyleName("save-button");
		
		fp.setButtonAlign(HorizontalAlignment.RIGHT);
		fp.addButton(resetBtn);
        fp.addButton(saveBtn);
        fp.addButton(cancelBtn);
        return fp;
	}

	private void setForm() {
		advOptWindow.show();

 		if (passPercentReqd)
 			passCombo.focus();
 		else
 			passCombo.disable();

		boolean sectionSelectAvail = (Boolean)advOptionsMap.get("SECTION_SELECT_AVAIL");
		if (! sectionSelectAvail)
			sectionCombo.disable();
		else
	        setSectionNumberSelection();

	}

	private void setPassPercentSelection() {
		String passPercent = (String) advOptionsMap.get(StudentModelExt.PASS_PERCENT_KEY);
		
		if (passPercent == null && passPercentReqd) {
			PassPercent p = passCombo.getStore().getAt(PassPercentCombo.DEFAULT_PERCENT_IDX);
			passCombo.setOriginalValue(p);
			passCombo.setValue(p);
			passCombo.enable();
			return;
		}
		
		if (passPercent != null) {
			List<PassPercent> list = passCombo.getStore().getModels();
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

	private void setSectionNumberSelection() {

		List<SectionNumber> list = sectionCombo.getStore().getModels();

        /*
         * don't want to place "0" in the section number list...
         * If incoming section number was "0", then select "1"
         */
		String selectSection = ("0".equals(currentSection)) ? "1" : currentSection;

		for (SectionNumber n : list) {
			if (selectSection.equals(n.getSectionNumber())) {
				sectionCombo.setOriginalValue(n);
				sectionCombo.setValue(n);
				sectionCombo.enable();
				break;
			}
		}
	}

	private Button resetButton(final FormPanel fp) {
		Button cancelBtn = new Button("Reset", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {
	    		
				PassPercent p = passCombo.getStore().getAt(PassPercentCombo.DEFAULT_PERCENT_IDX);
				passCombo.setOriginalValue(p);
				passCombo.setValue(p);
				
				SectionNumber n = sectionCombo.getStore().getAt(0);
				sectionCombo.setOriginalValue(n);
				sectionCombo.setValue(n);
	    		
	        	isShowWorkRequired.setValue(false);
	            isGamesLimited.setValue(false);
	            isStopAtProgramEnd.setValue(false);
	        }  
	    });
		cancelBtn.setToolTip("Reset to default values");
		return cancelBtn;
	}
	
	private Button cancelButton() {
		Button cancelBtn = new Button("Cancel", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {
                advOptWindow.close();
	        }  
	    });
		return cancelBtn;
	}

	private Button saveButton(final FieldSet fs, final FormPanel fp) {
		Button saveBtn = new Button("Save", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {

	    		PassPercent pp = passCombo.getValue();
	        	String passPercent = (pp != null) ? pp.getPassPercent() : null;

                StudentSettingsModel ssm = new StudentSettingsModel();
                
                ssm.setShowWorkRequired(isShowWorkRequired.getValue());
                ssm.setStopAtProgramEnd(isStopAtProgramEnd.getValue());
                ssm.setLimitGames(isGamesLimited.getValue());
                
                /*
                 * don't want to place "0" in the section number list...
                 * If incoming section number was "0" and selected value is "1", then
                 * return "0" as the selected value.
                 * 
                 */
                SectionNumber sn = sectionCombo.getValue();
                Integer sectionNum = (sn != null) ? Integer.parseInt(sn.getSectionNumber()) : 0;
                if (sectionNum == 1 && currentSection.equals("0")) sectionNum = 0;
                
                Map<String, Object> optionMap = new HashMap<String, Object>();
                
                optionMap.put(StudentModelExt.PASS_PERCENT_KEY, passPercent);
                optionMap.put(StudentModelExt.SETTINGS_KEY, ssm);
                optionMap.put(StudentModelExt.SECTION_NUM_KEY, sectionNum);
                
                callback.setAdvancedOptions(optionMap);

                advOptWindow.close();
	        }
	    });
		return saveBtn;
	}

}
