package hotmath.gwt.cm_tools.client.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.AddGroupAction;

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
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

import com.google.gwt.user.client.Timer;

/**
 * Register Student Advanced Options UI
 * 
 * set pass percent, require show work, enable tutoring
 * 
 * @author bob
 *
 */
public class RegisterStudentAdvancedOptions extends LayoutContainer {
	
	private CmWindow advOptWindow;

	private FieldSet advOptions;
	private CheckBox isShowWorkRequired;
	private CheckBox isTutoringEnabled;
	private CheckBoxGroup requireShowWork;
	private CheckBoxGroup enableTutoring;

	private ComboBox <PassPercent> passCombo;

	private FieldSet fs;
	private CmAdminModel cmAdminMdl;
	private int formHeight = 190;
	private int formWidth  = 270;
	private AdvOptCallback callback;
	//private StudentModelI  stuMdl;
	private boolean passPercentReqd;
	private Map<String,Object> advOptionsMap;
	
	public RegisterStudentAdvancedOptions(AdvOptCallback callback, CmAdminModel cm, Map <String,Object> optionMap, boolean isNew,
		boolean passPercentReqd) {
	    this.callback = callback;
	    //this.stuMdl = sm;
		this.cmAdminMdl = cm;
		this.advOptionsMap = optionMap;
		this.passPercentReqd = passPercentReqd;

		advOptWindow = new CmWindow();
		advOptWindow.add(optionsForm(isNew, passPercentReqd));
 		advOptWindow.show();
 		if (passPercentReqd) passCombo.focus();
	}
	
	private FormPanel optionsForm(boolean isNew, boolean passPercentReqd) {
		FormPanel fp = new FormPanel();
		fp.setLabelWidth(120);
		fp.setHeight(formHeight);
		fp.setFooter(true);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.setLayout(new FormLayout());

		passCombo = new PassPercentCombo(passPercentReqd);
        setPassPercentSelection(isNew);

        advOptions = new FieldSet();
        
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(fp.getLabelWidth());
		fl.setDefaultWidth(100);
		
		advOptions.setLayout(fl);
        advOptions.addStyleName("register-student-fieldset");
		advOptions.add(passCombo);

		isShowWorkRequired = new CheckBox();
        isShowWorkRequired.setId(StudentModelExt.SHOW_WORK_KEY);
        if (! isNew) {
        	isShowWorkRequired.setValue((Boolean) advOptionsMap.get(StudentModelExt.SHOW_WORK_KEY));
        }
        else {
        	// require 'Show Work' OFF by default
        	isShowWorkRequired.setValue(false);
        }

        requireShowWork = new CheckBoxGroup(); 
        requireShowWork.setFieldLabel("Require Show Work");
        requireShowWork.setId(StudentModelExt.SHOW_WORK_KEY);
        requireShowWork.add(isShowWorkRequired);
        advOptions.add(requireShowWork);

        isTutoringEnabled = new CheckBox();
        isTutoringEnabled.setId(StudentModelExt.TUTORING_AVAIL_KEY);
        if (! isNew) {
            isTutoringEnabled.setValue((Boolean) advOptionsMap.get(StudentModelExt.TUTORING_AVAIL_KEY));
        }
        else {
            // enable tutoring OFF by default
            isTutoringEnabled.setValue(false);
        }        

        enableTutoring = new CheckBoxGroup();
        enableTutoring.setFieldLabel("Tutoring Enabled");
        enableTutoring.setId(StudentModelExt.TUTORING_AVAIL_KEY);
		enableTutoring.add(isTutoringEnabled);
		advOptions.add(enableTutoring);
		
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
        
		Button saveBtn = saveButton(fs, isNew, fp);
		saveBtn.addStyleName("save-button");
		
		fp.setButtonAlign(HorizontalAlignment.RIGHT);
		fp.addButton(resetBtn);
        fp.addButton(saveBtn);
        fp.addButton(cancelBtn);
        return fp;
	}

	private void setPassPercentSelection(boolean isNew) {
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

	private Button resetButton(final FormPanel fp) {
		Button cancelBtn = new Button("Reset", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {
                fp.reset();
	        }  
	    });
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

	private Button saveButton(final FieldSet fs, final boolean isNew, final FormPanel fp) {
		Button saveBtn = new Button("Save", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {

	    		PassPercent pp = passCombo.getValue();
	        	String passPercent = (pp != null) ? pp.getPassPercent() : null;

	        	Boolean showWorkRequired = isShowWorkRequired.getValue();
	        	
                Boolean tutoringEnabled = isTutoringEnabled.getValue();
                
                Map<String, Object> optionMap = new HashMap<String, Object>();
                
                optionMap.put(StudentModelExt.PASS_PERCENT_KEY, passPercent);
                optionMap.put(StudentModelExt.SHOW_WORK_KEY, showWorkRequired);
                optionMap.put(StudentModelExt.TUTORING_AVAIL_KEY, tutoringEnabled);
                
                callback.setAdvancedOptions(optionMap);

                advOptWindow.close();
	        }
	    });
		return saveBtn;
	}

}
