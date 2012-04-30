package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
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
	private int formHeight = 275;
	private int formWidth  = 260;
	private DateRangeAdvOptCallback callback;
	private Map<String,Boolean> advOptionsMap;
	
	public static final String LOGGED_IN      = "logged_in";
	public static final String STARTED_QUIZ   = "started_quiz";
	public static final String TOOK_QUIZ      = "took_quiz";
	public static final String VIEWED_LESSONS = "viewed_lessons";
	public static final String USED_RESOURCES = "used_resources";
	public static final String REGISTERED     = "registered";

	public DateRangeAdvancedOptionsDialog(DateRangeAdvOptCallback callback, Map <String,Boolean> optionMap) {

		this.callback = callback;
		this.advOptionsMap = optionMap;

		advOptWindow = new CmWindow();

		advOptWindow.setHeading("Set Advanced Options");
		advOptWindow.setWidth(formWidth+10);
		advOptWindow.setHeight(formHeight+30);
		advOptWindow.setLayout(new FitLayout());
		advOptWindow.setResizable(false);
		advOptWindow.setDraggable(true);
		advOptWindow.setModal(true);

		advOptWindow.add(optionsForm());

		setForm();
	}
	
	private FormPanel optionsForm() {
		FormPanel fp = new FormPanel();
		fp.setLabelWidth(170);
		fp.setHeight(formHeight);
		fp.setFooter(true);
		fp.setFrame(false);
		fp.setHeaderVisible(false);
		fp.setBodyBorder(false);
		fp.setIconStyle("icon-form");
		fp.setButtonAlign(HorizontalAlignment.CENTER);
		fp.setLayout(new FormLayout());
		
		Text text = new Text("Students will be filtered based on the activity options selected below.");
        fp.add(text);

        advOptions = new FieldSet();
        advOptions.setWidth(233);
        
		FormLayout fl = new FormLayout();
		fl.setLabelWidth(fp.getLabelWidth());
		fl.setDefaultWidth(100);
		
		advOptions.setLayout(fl);
        advOptions.addStyleName("date-range-adv-options-fieldset");

		isLoggedIn = new CheckBox();
        isLoggedIn.setId(LOGGED_IN);
        isLoggedIn.setValue(advOptionsMap.get(LOGGED_IN));
        isLoggedIn.setToolTip("Select students that logged in.");
        loggedIn = new CheckBoxGroup(); 
        loggedIn.setFieldLabel("Logged in");
        loggedIn.setId(LOGGED_IN);
        loggedIn.add(isLoggedIn);
        advOptions.add(loggedIn);

        isStartedQuiz = new CheckBox();
        isStartedQuiz.setId(STARTED_QUIZ);
        isStartedQuiz.setValue(advOptionsMap.get(STARTED_QUIZ));
        isStartedQuiz.setToolTip("Select students that started a quiz.");
        startedQuiz = new CheckBoxGroup();
        startedQuiz.setFieldLabel("Started quiz");
        startedQuiz.setId(STARTED_QUIZ);
        startedQuiz.add(isStartedQuiz);
		advOptions.add(startedQuiz); 

        isTookQuiz = new CheckBox();
        isTookQuiz.setId(TOOK_QUIZ);
        isTookQuiz.setValue(advOptionsMap.get(TOOK_QUIZ));
        isTookQuiz.setToolTip("Select students that completed a quiz.");
        tookQuiz = new CheckBoxGroup();
        tookQuiz.setFieldLabel("Took quiz");
        tookQuiz.setId(TOOK_QUIZ);
        tookQuiz.add(isTookQuiz);
		advOptions.add(tookQuiz);

		isViewedLessons = new CheckBox();
        isViewedLessons.setId(VIEWED_LESSONS);
        isViewedLessons.setValue(advOptionsMap.get(VIEWED_LESSONS));
        isViewedLessons.setToolTip("Select students that viewed a lesson.");
        viewedLessons = new CheckBoxGroup(); 
        viewedLessons.setFieldLabel("Viewed lessons");
        viewedLessons.setId(VIEWED_LESSONS);
        viewedLessons.add(isViewedLessons);
        advOptions.add(viewedLessons);

		isUsedResources = new CheckBox();
        isUsedResources.setId(USED_RESOURCES);
        isUsedResources.setValue(advOptionsMap.get(USED_RESOURCES));
        isUsedResources.setToolTip("Select students that viewed a practice problem or video, or played a math game.");
        usedResources = new CheckBoxGroup(); 
        usedResources.setFieldLabel("Used resources");
        usedResources.setId(USED_RESOURCES);
        usedResources.add(isUsedResources);
        advOptions.add(usedResources);

		isRegistered = new CheckBox();
        isRegistered.setId(REGISTERED);
        isRegistered.setValue(advOptionsMap.get(REGISTERED));
        isRegistered.setToolTip("Select students that were registered.");
        registered = new CheckBoxGroup(); 
        registered.setFieldLabel("Registered");
        registered.setId(REGISTERED);
        registered.add(isRegistered);
        advOptions.add(registered);

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
		Button resetBtn = new Button("Reset", new SelectionListener<ButtonEvent>() {  
	    	public void componentSelected(ButtonEvent ce) {
	    		
	        	isLoggedIn.setValue(true);
	            isStartedQuiz.setValue(true);
	            isTookQuiz.setValue(true);
	            isViewedLessons.setValue(true);
	            isUsedResources.setValue(true);
	            isRegistered.setValue(true);
	        }  
	    });
		resetBtn.setToolTip("Reset to default values");
		return resetBtn;
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
                optionMap.put(LOGGED_IN, value);
                isValid = (isValid || value);

                value = isStartedQuiz.getValue();
                optionMap.put(STARTED_QUIZ, value);
                isValid = (isValid || value);

                value = isTookQuiz.getValue();
                optionMap.put(TOOK_QUIZ, isTookQuiz.getValue());
                isValid = (isValid || value);

                value = isViewedLessons.getValue();
                optionMap.put(VIEWED_LESSONS, isViewedLessons.getValue());
                isValid = (isValid || value);

                value = isUsedResources.getValue();
                optionMap.put(USED_RESOURCES, isUsedResources.getValue());
                isValid = (isValid || value);

                value = isRegistered.getValue();
                optionMap.put(REGISTERED, isRegistered.getValue());
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
