package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_admin.client.ui.assignment.EditAssignmentDialog.SubmitOptionsProperties;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldSet;

import com.google.gwt.core.client.GWT;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;

public class EditAssignmentOptionsDialog extends GWindow {

    private Assignment assignment;
    ComboBox<SubmitOptions> _submitOptions;
    CheckBox _autoReleaseGrades;
    CheckBox _personalizeAssignment;
    CheckBox _preventLessonAccess;


    private Callback callback;

    public interface Callback {
        void optionsSaved(String submitOption, boolean autoReleaseGrades, boolean isPersonalized, boolean preventLessonAccess);
    }
    
    public EditAssignmentOptionsDialog(Assignment assignment, Callback callbackOnSave) {
        super(false);
        this.assignment = assignment;
        this.callback = callbackOnSave;
        setModal(true);
        setHeadingText("Assignment Advanced Options");
        
        setPixelSize(450,200);
        setResizable(false);
        buildUi();

        addButton(new TextButton("Save",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                callback.optionsSaved(_submitOptions.getValue().getOption(), _autoReleaseGrades.getValue(), _personalizeAssignment.getValue(),_preventLessonAccess.getValue());
                hide();
            }
        }));

        
        addButton(new TextButton("Cancel",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
        
        setVisible(true);
    }

    int LABEL_LEN=100;
    
    private void buildUi() {
        FramedPanel framed = new FramedPanel();
        framed.setHeaderVisible(false);
        
        FlowLayoutContainer flowPanel = new FlowLayoutContainer();
        
        MyFieldSet fieldSet = new MyFieldSet("",400);
        fieldSet.setBorders(false);
        
        _submitOptions = createSubmitOptionsCombo();
        if(assignment.isAllowPastDueSubmits()) {
            _submitOptions.setValue(_submitOptions.getStore().get(0));
        }
        else {
            _submitOptions.setValue(_submitOptions.getStore().get(1));
        }
        fieldSet.addThing(new MyFieldLabel(_submitOptions, "Submit", 170, 200));
        
        
        _autoReleaseGrades = new CheckBox();
        _autoReleaseGrades.setToolTip("Release grades automatically when students turn in an assignment.");
        _autoReleaseGrades.setValue(assignment.isAutoRelease());        
        fieldSet.addThing(new MyFieldLabel(_autoReleaseGrades, "Auto Release Grades", 170, 20));
        
        
        _personalizeAssignment = new CheckBox();
        _personalizeAssignment.setToolTip("Multiple choice questions will be randomly selected for each student.");
        _personalizeAssignment.setValue(assignment.isPersonalized());

        fieldSet.addThing(new MyFieldLabel(_personalizeAssignment, "Individualize", 170, 20));
        
        
        _preventLessonAccess = new CheckBox();
        _preventLessonAccess.setToolTip("Do not allow access to assignment problem's lesson");
        _preventLessonAccess.setValue(assignment.isPreventLessonAccess());

        fieldSet.addThing(new MyFieldLabel(_preventLessonAccess, "Prevent Lesson Access", 170, 20));
        
        
        
        framed.setWidget(fieldSet);
        
        setWidget(framed);
    }

    
//    if(_autoReleaseGrades.getValue()) {
//        if(thereAreWbProbelms()) {
//            CmMessageBox.showAlert("You cannot auto release grades with assignments containing whiteboard problems.");
//            return false;
//        }
//    }
//    
//    _assignment.setAutoRelease(_autoReleaseGrades.getValue());


    private ComboBox<SubmitOptions> createSubmitOptionsCombo() {

        SubmitOptionsProperties props = GWT.create(SubmitOptionsProperties.class);

        ListStore<SubmitOptions> store = new ListStore<SubmitOptions>(props.key());
        store.add(new SubmitOptions("Allow submits after due date"));
        store.add(new SubmitOptions("No submits after due date"));

        ComboBox<SubmitOptions> combo = new ComboBox<SubmitOptions>(store, props.option());

        combo.setToolTip("Should the student be allowed to submit answers after the due date?");
        combo.setWidth(120);
        combo.setTypeAhead(true);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setEditable(false);

        combo.setAllowBlank(false);
        combo.setEmptyText("--Select Submit Option--");
        combo.setForceSelection(true);

        return combo;
    }

    
}
