package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.AdvancedOptionsModel;

import java.util.HashMap;
import java.util.Map;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.FormPanel;

/**
 * Date Range Advanced Options UI
 * 
 * set 'Logged in', 'Started Quiz', 'Took Quiz', 'Viewed Lessons', 'Used Resources', and 'Registered'
 * 
 * @author bob
 *
 */
public class DateRangeAdvancedOptionsDialog extends LayoutContainer {
	
	private GWindow advOptWindow;

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

	private AdvancedOptionsModel advancedOptions;

    private AdvancedOptionsModel options;
	
	public static final String LOGGED_IN      = "logged_in";
	public static final String STARTED_QUIZ   = "started_quiz";
	public static final String TOOK_QUIZ      = "took_quiz";
	public static final String VIEWED_LESSONS = "viewed_lessons";
	public static final String USED_RESOURCES = "used_resources";
	public static final String REGISTERED     = "registered";

	public DateRangeAdvancedOptionsDialog(DateRangeAdvOptCallback callback, AdvancedOptionsModel options) {

		this.callback = callback;
		this.options = options;

		advOptWindow = new GWindow(false);

		advOptWindow.setHeadingText("Set Advanced Options");
		advOptWindow.setWidth(formWidth+10);
		advOptWindow.setHeight(formHeight+30);
		advOptWindow.setResizable(false);
		advOptWindow.setDraggable(true);
		advOptWindow.setModal(true);

		advOptWindow.setWidget(optionsForm());

		setForm();
	}
	
	private ContentPanel optionsForm() {
	    
	    return new ContentPanel();
	    
//		FormPanel fp = new FormPanel();
//		fp.setLabelWidth(170);
//		fp.setHeight(formHeight);
//		fp.setFooter(true);
//		fp.setFrame(false);
//		fp.setHeaderVisible(false);
//		fp.setBodyBorder(false);
//		fp.setIconStyle("icon-form");
//		fp.setButtonAlign(HorizontalAlignment.CENTER);
//		fp.setLayout(new FormLayout());
//		
//		Text text = new Text("Students will be filtered based on the activity options selected below.");
//        fp.add(text);
//
//        advOptions = new FieldSet();
//        advOptions.setWidth(233);
//        
//		FormLayout fl = new FormLayout();
//		fl.setLabelWidth(fp.getLabelWidth());
//		fl.setDefaultWidth(100);
//		
//		advOptions.setLayout(fl);
//        advOptions.addStyleName("date-range-adv-options-fieldset");
//
//		isLoggedIn = new CheckBox();
//        isLoggedIn.setId(LOGGED_IN);
//        isLoggedIn.setValue(advOptionsMap.get(LOGGED_IN));
//        isLoggedIn.setToolTip("Select students that logged in.");
//        loggedIn = new CheckBoxGroup(); 
//        loggedIn.setFieldLabel("Logged in");
//        loggedIn.setId(LOGGED_IN);
//        loggedIn.add(isLoggedIn);
//        advOptions.add(loggedIn);
//
//        isStartedQuiz = new CheckBox();
//        isStartedQuiz.setId(STARTED_QUIZ);
//        isStartedQuiz.setValue(advOptionsMap.get(STARTED_QUIZ));
//        isStartedQuiz.setToolTip("Select students that started a quiz.");
//        startedQuiz = new CheckBoxGroup();
//        startedQuiz.setFieldLabel("Started quiz");
//        startedQuiz.setId(STARTED_QUIZ);
//        startedQuiz.add(isStartedQuiz);
//		advOptions.add(startedQuiz); 
//
//        isTookQuiz = new CheckBox();
//        isTookQuiz.setId(TOOK_QUIZ);
//        isTookQuiz.setValue(advOptionsMap.get(TOOK_QUIZ));
//        isTookQuiz.setToolTip("Select students that completed a quiz.");
//        tookQuiz = new CheckBoxGroup();
//        tookQuiz.setFieldLabel("Took quiz");
//        tookQuiz.setId(TOOK_QUIZ);
//        tookQuiz.add(isTookQuiz);
//		advOptions.add(tookQuiz);
//
//		isViewedLessons = new CheckBox();
//        isViewedLessons.setId(VIEWED_LESSONS);
//        isViewedLessons.setValue(advOptionsMap.get(VIEWED_LESSONS));
//        isViewedLessons.setToolTip("Select students that viewed a lesson.");
//        viewedLessons = new CheckBoxGroup(); 
//        viewedLessons.setFieldLabel("Viewed lessons");
//        viewedLessons.setId(VIEWED_LESSONS);
//        viewedLessons.add(isViewedLessons);
//        advOptions.add(viewedLessons);
//
//		isUsedResources = new CheckBox();
//        isUsedResources.setId(USED_RESOURCES);
//        isUsedResources.setValue(advOptionsMap.get(USED_RESOURCES));
//        isUsedResources.setToolTip("Select students that viewed a practice problem or video, or played a math game.");
//        usedResources = new CheckBoxGroup(); 
//        usedResources.setFieldLabel("Used resources");
//        usedResources.setId(USED_RESOURCES);
//        usedResources.add(isUsedResources);
//        advOptions.add(usedResources);
//
//		isRegistered = new CheckBox();
//        isRegistered.setId(REGISTERED);
//        isRegistered.setValue(advOptionsMap.get(REGISTERED));
//        isRegistered.setToolTip("Select students that were registered.");
//        registered = new CheckBoxGroup(); 
//        registered.setFieldLabel("Registered");
//        registered.setId(REGISTERED);
//        registered.add(isRegistered);
//        advOptions.add(registered);
//
//		fp.add(advOptions);
//
//		Button resetBtn = resetButton(fp);
//
//		Button cancelBtn = cancelButton();
//        cancelBtn.addStyleName("cancel-button");
//        
//		Button saveBtn = saveButton(fs, fp);
//		saveBtn.addStyleName("save-button");
//		
//		fp.setButtonAlign(HorizontalAlignment.RIGHT);
//		fp.addButton(resetBtn);
//        fp.addButton(saveBtn);
//        fp.addButton(cancelBtn);
//        
//        return fp;
	}

	private void setForm() {
		advOptWindow.show();
	}

	private TextButton resetButton(final FormPanel fp) {
	    TextButton resetBtn = new TextButton("Reset", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
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
                
                AdvancedOptionsModel options = new AdvancedOptionsModel();

                boolean isValid = false;
                boolean value;

                value = isLoggedIn.getValue();
                options.setLoggedIn(value);
                isValid = (isValid || value);

                value = isStartedQuiz.getValue();
                options.setStartedQuiz(value);
                isValid = (isValid || value);

                value = isTookQuiz.getValue();
                options.setTookQuiz(isTookQuiz.getValue());
                isValid = (isValid || value);

                value = isViewedLessons.getValue();
                options.setViewedLessons(isViewedLessons.getValue());
                isValid = (isValid || value);

                value = isUsedResources.getValue();
                options.setUsedResources(isUsedResources.getValue());
                isValid = (isValid || value);

                value = isRegistered.getValue();
                options.setRegistered(isRegistered.getValue());
                isValid = (isValid || value);

                if (isValid) {
                    callback.setAdvancedOptions(options);
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
