package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.GetProgramMetaInfoAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.model.AdvancedOptionsModel;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.SectionNumber;
import hotmath.gwt.cm_tools.client.model.StudentProgramModel;
import hotmath.gwt.cm_tools.client.model.StudentSettingsModel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.List;

import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;

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
public class RegisterStudentAdvancedOptions extends FramedPanel {
	
	private GWindow advOptWindow;

	private MyFieldSet advOptions;
	private CheckBox isShowWorkRequired;
	private CheckBox isGamesLimited;
	private CheckBox isStopAtProgramEnd;
	private CheckBox isDisableCalcAlways;
	private CheckBox isDisableCalcQuizzes;
	private CheckBox isNoPublicWebLinks;

	private ComboBox <PassPercent> passCombo;
	private SectionNumberCombo sectionCombo;

	private CmAdminModel cmAdminMdl;
    int FIELD_LEN = 200;
    int LABEL_LEN = 245;
    int FORM_LEN = 430;
    int FORM_HEIGHT = 300;
    
    
	private AdvOptCallback callback;
	private boolean passPercentReqd;
	AdvancedOptionsModel options;

	private int sectionCount;
	private int currentSection;
	private boolean sectionIsSettable;
	private boolean progStopIsSettable;

    private StudentProgramModel program;
	
	public RegisterStudentAdvancedOptions(AdvOptCallback callback, CmAdminModel cm, AdvancedOptionsModel options, boolean isNew,
		boolean passPercentReqd, StudentProgramModel selectedProgram) {

		this.callback = callback;
		this.cmAdminMdl = cm;
		this.options = options;
		this.passPercentReqd = passPercentReqd;
		this.currentSection = options.getSectionNum();
		this.sectionCount = options.getSectionCount();
        this.sectionIsSettable = options.isSectionIsSettable();

		this.progStopIsSettable = options.isProgStopIsSettable();
		this.program = selectedProgram;
		
		
		
		setHeadingText("Advanced Options");

		advOptWindow = new GWindow(false);
		advOptWindow.setResizable(false);
		advOptWindow.setModal(true);
		advOptWindow.setWidget(optionsForm(isNew, passPercentReqd));
		
		setForm();

	}
	
	private ContentPanel optionsForm(boolean isNew, boolean passPercentReqd) {
	    
	    
	    ContentPanel mainFramed = this;
	    
	    VerticalLayoutContainer vMain = new VerticalLayoutContainer();
		//fp.setFrame(false);
	    mainFramed.setHeaderVisible(false);
	    //mainFramed.setBodyBorder(false);
		//fp.setIconStyle("icon-form");
	    mainFramed.setButtonAlign(BoxLayoutPack.CENTER);
		
	    mainFramed.setWidget(vMain);

        advOptions = new MyFieldSet("Options",FORM_LEN-15);
        vMain.add(advOptions);
        
        passCombo = new PassPercentCombo(passPercentReqd);
        setPassPercentSelection();
		advOptions.addThing(new MyFieldLabel(passCombo,  "Pass Percent", LABEL_LEN));
		

		isShowWorkRequired = new CheckBox();
        isShowWorkRequired.setId("show_work");
        isShowWorkRequired.setValue(options.getSettings().getShowWorkRequired());
        //requireShowWork.setFieldLabel("Require Show Work");
        
        advOptions.addThing(new MyFieldLabel(isShowWorkRequired, "Require Show Work", LABEL_LEN, 10));
        
        isGamesLimited = new CheckBox();
        isGamesLimited.setId("limit_games");
        isGamesLimited.setValue(options.getSettings().getLimitGames());
        isGamesLimited.setToolTip("If checked, then no Games can be played.");
        
		advOptions.addThing(new MyFieldLabel(isGamesLimited, "Disallow Games", LABEL_LEN, 10));
		

		isNoPublicWebLinks = new CheckBox();
		isNoPublicWebLinks.setId("isNoPublicWebLinks");
		isNoPublicWebLinks.setValue(options.getSettings().isNoPublicWebLinks());
		isNoPublicWebLinks.setToolTip("If checked, then no public web links will be shown to this student");
        
        advOptions.addThing(new MyFieldLabel(isNoPublicWebLinks, "Disallow All School's Web Links", LABEL_LEN, 10));

		
		

        isStopAtProgramEnd = new CheckBox();
        isStopAtProgramEnd.setId("stop_at_program_end");
        isStopAtProgramEnd.setValue(options.getSettings().getStopAtProgramEnd());
        //stopAtProgramEnd.setFieldLabel("Stop at End of Program");
		if (! progStopIsSettable) { 
		    isStopAtProgramEnd.disable();
		}
		
		advOptions.addThing(new MyFieldLabel(isStopAtProgramEnd, "Stop at End of Program", LABEL_LEN, 10));

		isDisableCalcAlways = new CheckBox();
		isDisableCalcAlways.setId("disable_calc_always");
		isDisableCalcAlways.setValue(options.getSettings().getDisableCalcAlways());
        advOptions.addThing(new MyFieldLabel(isDisableCalcAlways, "Disable whiteboard calculator always", LABEL_LEN, 10));

		isDisableCalcQuizzes = new CheckBox();
		isDisableCalcQuizzes.setId("disable_calc_quizzeass");
		isDisableCalcQuizzes.setValue(options.getSettings().getDisableCalcQuizzes());
        advOptions.addThing(new MyFieldLabel(isDisableCalcQuizzes, "Disable whiteboard calculator for quizzes", LABEL_LEN, 10));

        if (sectionIsSettable) {
            sectionCombo = new SectionNumberCombo(0);
            advOptions.addThing(new MyFieldLabel(sectionCombo, "Select Section", LABEL_LEN));
            setupSectionComboAsync();
        }

		advOptWindow.setHeadingText((isNew)?"Set Options":"Edit Options");
		advOptWindow.setWidth(FORM_LEN+10);
		advOptWindow.setHeight(FORM_HEIGHT+20);

		//		
		TextButton resetBtn = resetButton();
		//resetBtn.addStyleName("reset-button");

		TextButton cancelBtn = cancelButton();
        cancelBtn.addStyleName("cancel-button");
        
		TextButton saveBtn = saveButton();
		saveBtn.addStyleName("save-button");
		
		advOptWindow.addButton(resetBtn);
		advOptWindow.addButton(saveBtn);
		advOptWindow.addButton(cancelBtn);
        return mainFramed;
	}

	/** Lookup from server the number of 
	 * sections in the program.
	 *  
	 */
	private void setupSectionComboAsync() {
	    
	    new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                GetProgramMetaInfoAction action = new GetProgramMetaInfoAction(program);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(RpcData data) {
                sectionCombo.updateList(data.getDataAsInt("section_count"));
    	        setSectionNumberSelection();
            }
        }.register();	    
	}
	

    private void setForm() {
		advOptWindow.show();

 		if (passPercentReqd)
 			passCombo.focus();
 		else
 			passCombo.disable();

 		boolean sectionSelectAvail=options.isSectionIsSettable();
		if (sectionSelectAvail == false)
			sectionCombo.disable();

	}

	private void setPassPercentSelection() {
		String passPercent = (String) options.getPassPercent();
		
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
	            isDisableCalcAlways.setValue(false);
	            isDisableCalcQuizzes.setValue(false);
	        }  
	    });
		cancelBtn.setToolTip("Reset to default values");
		return cancelBtn;
	}
	
	private TextButton cancelButton() {
		TextButton cancelBtn = new TextButton("Cancel", new SelectHandler() {
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
                ssm.setDisableCalcAlways(isDisableCalcAlways.getValue());
                ssm.setDisableCalcQuizzes(isDisableCalcQuizzes.getValue());
                ssm.setNoPublicWebLinks(isNoPublicWebLinks.getValue());

                /*
                 * don't want to place "0" in the section number list...
                 * If incoming section number was "0" and selected value is "1", then
                 * return "0" as the selected value.
                 * 
                 */
                int sectionNum = 0;
                if(sectionIsSettable && sectionCombo != null) {
	                SectionNumber sn = sectionCombo.getValue();
	                sectionNum = (sn != null) ? sn.getSectionNumber() : 0;
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
