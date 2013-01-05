package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.model.AdvancedOptionsModel;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.SectionNumber;
import hotmath.gwt.cm_tools.client.model.StudentSettingsModel;

import java.util.List;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;


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
public class RegisterStudentAdvancedOptionsProto extends SimpleContainer {
	
	private GWindow advOptWindow;

	private FieldSet advOptions;
	private CheckBox isShowWorkRequired;
	private CheckBox isGamesLimited;
	private CheckBox isStopAtProgramEnd;
	private MyCheckBoxGroup requireShowWork;
	private MyCheckBoxGroup limitGames;
	private MyCheckBoxGroup stopAtProgramEnd;

	private ComboBox <PassPercent> passCombo;
	private ComboBox <SectionNumber> sectionCombo;

	private FieldSet fs;
	private CmAdminModel cmAdminMdl;
	private int formHeight = 240;
	private int formWidth  = 340;
	private AdvOptCallback2 callback;
	private boolean passPercentReqd;
	AdvancedOptionsModel options;

	private int sectionCount;
	private int currentSection;
	private boolean sectionIsSettable;
	private boolean progStopIsSettable;
	
	public RegisterStudentAdvancedOptionsProto(AdvOptCallback2 callback, CmAdminModel cm, AdvancedOptionsModel options, boolean isNew,
		boolean passPercentReqd) {

		this.callback = callback;
		this.cmAdminMdl = cm;
		this.options = options;
		this.passPercentReqd = passPercentReqd;
		this.currentSection = options.getSectionNum();
		this.sectionCount = options.getSectionCount();
		this.sectionIsSettable = options.isSectionIsSettable();
		this.progStopIsSettable = options.isProgStopIsSettable();

		advOptWindow = new GWindow(true);
		advOptWindow.setWidget(optionsForm(isNew, passPercentReqd));
		
		setForm();

	}
	
	private ContentPanel optionsForm(boolean isNew, boolean passPercentReqd) {
	    
		ContentPanel  fp = new ContentPanel();
		
		//fp.setLabelWidth(180);
		fp.setHeight(formHeight);
		
		//fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		//fp.setIconStyle("icon-form");
		fp.setButtonAlign(BoxLayoutPack.CENTER);
		
		VerticalLayoutContainer vertAdvOptions = new VerticalLayoutContainer();
        advOptions = new FieldSet();
        advOptions.setWidget(vertAdvOptions);
        advOptions.addStyleName("register-student-fieldset");        
		//fl.setLabelWidth(fp.getLabelWidth());
		//fl.setDefaultWidth(100);

        passCombo = new PassPercentCombo(passPercentReqd);
        setPassPercentSelection();
		advOptions.add(passCombo);

		isShowWorkRequired = new CheckBox();
        isShowWorkRequired.setId("show_work");
        isShowWorkRequired.setValue(options.getSettings().getShowWorkRequired());
        requireShowWork = new MyCheckBoxGroup(); 
        requireShowWork.setId("show_work");
        requireShowWork.add(isShowWorkRequired);
        vertAdvOptions.add(new FieldLabel(requireShowWork, "Require Show Work"));

        isGamesLimited = new CheckBox();
        isGamesLimited.setId("limit_games");
        isGamesLimited.setValue(options.getSettings().getLimitGames());
        isGamesLimited.setToolTip("If checked, then no Games can be played.");
        
        limitGames = new MyCheckBoxGroup();
        //limitGames.setFieldLabel("Disallow Games");
        limitGames.setId("limit_games");
		limitGames.add(isGamesLimited);
		
		vertAdvOptions.add(new FieldLabel(limitGames, "Disallow Games")); 

        isStopAtProgramEnd = new CheckBox();
        isStopAtProgramEnd.setId("stop_at_program_end");
        isStopAtProgramEnd.setValue(options.getSettings().getStopAtProgramEnd());
        
        stopAtProgramEnd = new MyCheckBoxGroup();
        //stopAtProgramEnd.setFieldLabel("Stop at End of Program");
        stopAtProgramEnd.setId("stop_at_program_end");
		stopAtProgramEnd.add(isStopAtProgramEnd);
		if (! progStopIsSettable) stopAtProgramEnd.disable();
		vertAdvOptions.add(new FieldLabel(stopAtProgramEnd, "Stop at End of Program"));

		if (sectionIsSettable) {
    		sectionCombo = new SectionNumberCombo(sectionCount);
            setSectionNumberSelection();
		    advOptions.add(sectionCombo);
        }

		advOptWindow.setHeadingText((isNew)?"Set Options":"Edit Options");
		advOptWindow.setWidth(formWidth+10);
		advOptWindow.setHeight(formHeight+20);
		advOptWindow.setResizable(false);
		advOptWindow.setDraggable(true);
		advOptWindow.setModal(true);

		fp.add(advOptions);
		
		TextButton resetBtn = resetButton();
		//resetBtn.addStyleName("reset-button");

		TextButton cancelBtn = cancelButton();
        cancelBtn.addStyleName("cancel-button");
        
		TextButton saveBtn = saveButton();
		saveBtn.addStyleName("save-button");
		
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

		boolean sectionSelectAvail = options.isSectionIsSettable();
		if (! sectionSelectAvail)
			sectionCombo.disable();
		else
	        setSectionNumberSelection();

	}

	private void setPassPercentSelection() {
		String passPercent = options.getPassPercent();
		
		if (passPercent == null && passPercentReqd) {
			PassPercent p = passCombo.getStore().get(PassPercentCombo.DEFAULT_PERCENT_IDX);
			passCombo.setOriginalValue(p);
			passCombo.setValue(p);
			passCombo.enable();
			return;
		}
		
		if (passPercent != null) {
			List<PassPercent> list = passCombo.getStore().getAll();
			for (PassPercent p : list) {
				if (passPercent.equals(p.getPercent())) {
					passCombo.setOriginalValue(p);
					passCombo.setValue(p);
					passCombo.enable();
					break;
				}
			}
		}
	}

	private void setSectionNumberSelection() {

		List<SectionNumber> list = sectionCombo.getStore().getAll();

        /*
         * don't want to place "0" in the section number list...
         * If incoming section number was "0", then select "1"
         */
		int selectSection = (currentSection == 0) ? 1 : currentSection;

		for (SectionNumber n : list) {
			if (selectSection == n.getSectionNumber()) {
				sectionCombo.setOriginalValue(n);
				sectionCombo.setValue(n);
				sectionCombo.enable();
				break;
			}
		}
	}

	private TextButton resetButton() {
		TextButton cancelBtn = new TextButton("Reset", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                
				PassPercent p = passCombo.getStore().get(PassPercentCombo.DEFAULT_PERCENT_IDX);
				passCombo.setOriginalValue(p);
				passCombo.setValue(p);
				
				SectionNumber n = sectionCombo.getStore().get(0);
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
	
	private TextButton cancelButton() {
		TextButton cancelBtn = new TextButton("Cancel",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                advOptWindow.close();
	        }  
	    });
		return cancelBtn;
	}

	private TextButton saveButton() {
		TextButton saveBtn = new TextButton("Save", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {

	    		PassPercent pp = passCombo.getValue();
	        	String passPercent = (pp != null) ? pp.getPercent() : null;

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
                Integer sectionNum = 0;
                if(sectionIsSettable) {
	                SectionNumber sn = sectionCombo.getValue();
	                sectionNum = sn.getSectionNumber();
	                if (sectionNum == 1 && currentSection == 0) {
	                	sectionNum = 0;
	                }
                }
                
                AdvancedOptionsModel options = new AdvancedOptionsModel();
                
                
                options.setPassPercent(passPercent);
                options.setSettings(ssm);
                options.setSectionNum(sectionNum);
                
                callback.setAdvancedOptions(options);

                advOptWindow.close();
	        }
	    });
		return saveBtn;
	}

}
