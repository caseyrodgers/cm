package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.CmAdminModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

/**
 * Date Range Advanced Options UI
 * 
 * set 'Logged in', 'Started Quiz', 'Took Quiz', 'Viewed Lessons', 'Used Resources', and 'Registered'
 * 
 * @author bob
 *
 */
public class DateRangeAdvancedOptionsDialog extends LayoutContainer {
	
	private CmWindow advOptWindow;

	private FieldSet advOptions;
	private CheckBox isLoggedIn;
	private CheckBox isStartedQuiz;
	private CheckBox isTookQuiz;
	private CheckBox isViewedLessons;
	private CheckBox isUsedResources;
	private CheckBox isRegistered;
	private CheckBoxGroup loggedIn;
	private CheckBoxGroup startedQuiz;
	private CheckBoxGroup tookQuiz;
	private CheckBoxGroup viewedLessons;
	private CheckBoxGroup usedResources;
	private CheckBoxGroup registered;

	private FieldSet fs;
	private int formHeight = 240;
	private int formWidth  = 340;
	private AdvOptCallback callback;
	private Map<String,Boolean> advOptionsMap;

	public DateRangeAdvancedOptionsDialog(AdvOptCallback callback, Map <String,Boolean> optionMap) {

		this.callback = callback;
		this.advOptionsMap = optionMap;

		advOptWindow = new CmWindow();
		advOptWindow.add(optionsForm());
		
		setForm();

	}
	
	private FormPanel optionsForm() {
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
        advOptions.addStyleName("advanced-options-fieldset");

		isLoggedIn = new CheckBox();
        isLoggedIn.setId("logged_in");
        isLoggedIn.setValue(advOptionsMap.get("logged_in"));
        isLoggedIn.setToolTip("Select students that logged in.");
        loggedIn = new CheckBoxGroup(); 
        loggedIn.setFieldLabel("Logged in");
        loggedIn.setId("logged_in");
        loggedIn.add(isLoggedIn);
        advOptions.add(viewedLessons);

        isStartedQuiz = new CheckBox();
        isStartedQuiz.setId("started_quiz");
        isStartedQuiz.setValue(advOptionsMap.get("started_quiz"));
        isStartedQuiz.setToolTip("Select students that started a quiz.");
        startedQuiz = new CheckBoxGroup();
        startedQuiz.setFieldLabel("Started quiz");
        startedQuiz.setId("started_quiz");
        startedQuiz.add(isStartedQuiz);
		advOptions.add(startedQuiz); 

        isTookQuiz = new CheckBox();
        isTookQuiz.setId("took_quiz");
        isTookQuiz.setValue(advOptionsMap.get("took_quiz"));
        isTookQuiz.setToolTip("Select students that completed a quiz.");
        tookQuiz = new CheckBoxGroup();
        tookQuiz.setFieldLabel("Took quiz");
        tookQuiz.setId("took_quiz");
        tookQuiz.add(isTookQuiz);
		advOptions.add(tookQuiz);

		isViewedLessons = new CheckBox();
        isViewedLessons.setId("viewed_lessons");
        isViewedLessons.setValue(advOptionsMap.get("viewed_lessons"));
        isViewedLessons.setToolTip("Select students that viewed a lesson.");
        viewedLessons = new CheckBoxGroup(); 
        viewedLessons.setFieldLabel("Viewed lessons");
        viewedLessons.setId("viewed_lessons");
        viewedLessons.add(isViewedLessons);
        advOptions.add(viewedLessons);

		isUsedResources = new CheckBox();
        isUsedResources.setId("used_resources");
        isUsedResources.setValue(advOptionsMap.get("used_resources"));
        isUsedResources.setToolTip("Select students that completed a practice problem, viewed a video, or played a math game.");
        usedResources = new CheckBoxGroup(); 
        usedResources.setFieldLabel("Used resources");
        usedResources.setId("used_resources");
        usedResources.add(isUsedResources);
        advOptions.add(usedResources);

		isRegistered = new CheckBox();
        isRegistered.setId("registered");
        isRegistered.setValue(advOptionsMap.get("registered"));
        registered = new CheckBoxGroup(); 
        registered.setFieldLabel("Registered");
        registered.setId("registered");
        registered.add(isRegistered);
        advOptions.add(registered);

		advOptWindow.setHeading("Set Options");
		advOptWindow.setWidth(formWidth+10);
		advOptWindow.setHeight(formHeight+20);
		advOptWindow.setLayout(new FitLayout());
		advOptWindow.setResizable(false);
		advOptWindow.setDraggable(true);
		advOptWindow.setModal(true);

		fp.add(advOptions);
		
		Button resetBtn = resetButton(fp);

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
	}

	private Button resetButton(final FormPanel fp) {
		Button cancelBtn = new Button("Reset", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {
	    		
	        	isLoggedIn.setValue(true);
	            isStartedQuiz.setValue(true);
	            isTookQuiz.setValue(true);
	            isViewedLessons.setValue(true);
	            isUsedResources.setValue(true);
	            isRegistered.setValue(true);
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

                Map<String, Boolean> optionMap = new HashMap<String, Boolean>();

                boolean isValid = false;
                boolean value;

                value = isLoggedIn.getValue();
                optionMap.put("logged_in", value);
                isValid = (isValid || value);

                value = isStartedQuiz.getValue();
                optionMap.put("started_quiz", value);
                isValid = (isValid || value);

                value = isTookQuiz.getValue();
                optionMap.put("took_quiz", isTookQuiz.getValue());
                isValid = (isValid || value);

                value = isViewedLessons.getValue();
                optionMap.put("viewed_lessons", isViewedLessons.getValue());
                isValid = (isValid || value);

                value = isUsedResources.getValue();
                optionMap.put("used_resources", isUsedResources.getValue());
                isValid = (isValid || value);

                value = isRegistered.getValue();
                optionMap.put("registered", isRegistered.getValue());
                isValid = (isValid || value);

                if (isValid) {
                    callback.setAdvancedOptions(optionMap);
                    advOptWindow.close();
                }
                else {
                    CatchupMathTools.showAlert("Error", "Please select at least one option.");
                }
	        }
	    });
		return saveBtn;
	}

}
