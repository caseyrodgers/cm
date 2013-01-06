package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.AdvancedOptionsModel;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;

/**
 * Date Range Advanced Options UI
 * 
 * set 'Logged in', 'Started Quiz', 'Took Quiz', 'Viewed Lessons', 'Used Resources', and 'Registered'
 * 
 * @author bob
 *
 */
public class DateRangeAdvancedOptionsDialog extends GWindow {
	
	private MyFieldSet advOptions;
	private CheckBox isLoggedIn;
	private CheckBox isStartedQuiz;
	private CheckBox isTookQuiz;
	private CheckBox isViewedLessons;
	private CheckBox isUsedResources;
	private CheckBox isRegistered;
	private int formHeight = 275;
	private int formWidth  = 260;
	private DateRangeAdvOptCallback callback;


    private AdvancedOptionsModel options;

	public DateRangeAdvancedOptionsDialog(DateRangeAdvOptCallback callback, AdvancedOptionsModel options) {
	    super(false);
		this.callback = callback;
		this.options = options;
		setHeadingText("Set Advanced Options");
		setWidth(formWidth+10);
		setHeight(formHeight+30);
		setResizable(false);
		setModal(true);

		setWidget(createOptionsForm());
		

        TextButton resetBtn = resetButton();

        TextButton cancelBtn = cancelButton();
        cancelBtn.addStyleName("cancel-button");
        
        TextButton saveBtn = saveButton();
        saveBtn.addStyleName("save-button");
        
        addButton(resetBtn);
        addButton(saveBtn);
        addButton(cancelBtn);
        
		setForm();
		
		forceLayout();
	}
	
	int LABEL_LEN = 130;
	int FIELD_LEN = 12;
	
	private Widget createOptionsForm() {
	    
	    FramedPanel mainFrame = new FramedPanel();
	    
		VerticalLayoutContainer verMain = new VerticalLayoutContainer();
		mainFrame.setWidget(verMain);
		mainFrame.setHeaderVisible(false);
		
		//fp.setLabelWidth(170);
		//fp.setHeight(formHeight);
		//fp.setFrame(false);
		//mainFrame.setHeaderVisible(false);
		//mainFrame.setBodyBorder(false);
		//fp.setIconStyle("icon-form");
		
		Label text = new Label("Students will be filtered based on the activity options selected below.");
		verMain.add(text);

        advOptions = new MyFieldSet("Options", 244);
        
        advOptions.addStyleName("date-range-adv-options-fieldset");

		isLoggedIn = new CheckBox();
        isLoggedIn.setValue(options.getLoggedIn());
        isLoggedIn.setToolTip("Select students that logged in.");
        advOptions.addThing(new MyFieldLabel(isLoggedIn, "Logged in", LABEL_LEN, FIELD_LEN));

        isStartedQuiz = new CheckBox();
        isStartedQuiz.setValue(options.isStartedQuiz());
        isStartedQuiz.setToolTip("Select students that started a quiz.");
        advOptions.addThing(new MyFieldLabel(isStartedQuiz, "Started quiz", LABEL_LEN, FIELD_LEN));

        isTookQuiz = new CheckBox();
        isTookQuiz.setValue(options.isTookQuiz());
        isTookQuiz.setToolTip("Select students that completed a quiz.");
        advOptions.addThing(new MyFieldLabel(isTookQuiz, "Took quiz", LABEL_LEN, FIELD_LEN));

        
        isViewedLessons = new CheckBox();
        isViewedLessons.setValue(options.isViewedLessons());
        isViewedLessons.setToolTip("Select students that viewed a lesson.");
        advOptions.addThing(new MyFieldLabel(isViewedLessons, "Viewed lessons", LABEL_LEN, FIELD_LEN));
        
        
		isUsedResources = new CheckBox();
        isUsedResources.setValue(options.isUsedResources());
        isUsedResources.setToolTip("Select students that viewed a practice problem or video, or played a math game.");
        advOptions.addThing(new MyFieldLabel(isUsedResources, "Used resources", LABEL_LEN, FIELD_LEN));
        
        
		isRegistered = new CheckBox();
        isRegistered.setValue(options.isRegistered());
        isRegistered.setToolTip("Select students that were registered.");
        advOptions.addThing(new MyFieldLabel(isRegistered, "Registered", LABEL_LEN, FIELD_LEN));
        
		verMain.add(advOptions);
        
        return mainFrame;
	}

	private void setForm() {
		show();
	}
	
	private void resetForm() {
        isLoggedIn.setValue(true);
        isStartedQuiz.setValue(true);
        isTookQuiz.setValue(true);
        isViewedLessons.setValue(true);
        isUsedResources.setValue(true);
        isRegistered.setValue(true);
	}

	private TextButton resetButton() {
	    TextButton resetBtn = new TextButton("Reset", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                resetForm();
	        }  
	    });
		resetBtn.setToolTip("Reset to default values");
		return resetBtn;
	}
	
	private TextButton cancelButton() {
		TextButton cancelBtn = new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                close();
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
                    close();
                }
                else {
                    CatchupMathTools.showAlert("Error", "Please select at least one option.");
                }
	        }
	    });
		return saveBtn;
	}

}
