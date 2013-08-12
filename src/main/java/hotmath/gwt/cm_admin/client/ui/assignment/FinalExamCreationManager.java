package hotmath.gwt.cm_admin.client.ui.assignment;


import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.GroupDto;
import hotmath.gwt.cm_rpc.client.rpc.CreateFinalExamAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;

public class FinalExamCreationManager extends GWindow {

    private int adminId;
    private GroupDto group;

    @UiField 
    HTMLPanel mainPanel;
    private SimpleComboBox<String> _quizTypeCombo;
    private SimpleComboBox<QuizProblem> _quizSize;
    
    interface MyUiBinder extends UiBinder<Widget, FinalExamCreationManager> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
    
    public FinalExamCreationManager(int adminId, GroupDto currentGroup) {
        super(false);

        setHeadingHtml("Course Tests");
        setPixelSize(400, 200);

        this.adminId = adminId;
        this.group = currentGroup;
        
        setWidget(uiBinder.createAndBindUi(this));

        drawGui();
        
        setBodyBorder(true);

        setVisible(true);
    }

    private void drawGui() {

        FlowLayoutContainer flow = new FlowLayoutContainer();
        
        _quizTypeCombo = createQuizCombo();
        _quizSize = createQuizSizeCombo();
        
        flow.add(new MyFieldLabel(_quizTypeCombo, "Course", 60, 200));
        flow.add(new MyFieldLabel(_quizSize, "Problems", 60, 120));
        mainPanel.add(flow);
        
        addButton(new TextButton("Continue", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                createFinalExam();
            }
        }));
        addButton(new TextButton("Cancel", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        }));
    }

    protected void createFinalExam() {
        if(_quizTypeCombo.getValue() == null) {
            CmMessageBox.showAlert("Info", "Select a type of course test to create.",new CallbackOnComplete() {
                @Override
                public void isComplete() {
                    _quizTypeCombo.expand();                    
                }
            });
            return;
        }
        
        if(_quizSize.getValue() == null) {
            CmMessageBox.showAlert("Info", "Select the number of problems to include in the course test",new CallbackOnComplete() {
                @Override
                public void isComplete() {
                    _quizSize.expand();
                }
            });
            return;
        }
        
        new RetryAction<Assignment>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                CreateFinalExamAction action = new CreateFinalExamAction(adminId, group, _quizTypeCombo.getValue(), _quizSize.getValue().size);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(Assignment result) {
                CmBusyManager.setBusy(false);
                Log.info("Course Test Created: " + result);
                new EditAssignmentDialog(result);
                
                hide();
            }
        }.register();
        
        
        
    }

    private SimpleComboBox<String> createQuizCombo() {
        SimpleComboBox<String> combo = new SimpleComboBox<String>(new StringLabelProvider<String>());

        combo.add("Essentials");
        combo.add("Pre-Algebra");
        combo.add("Algebra 1");
        combo.add("Geometry");
        combo.add("Algebra 2");

        combo.setAllowTextSelection(false);
        combo.setEmptyText("Select a Course");
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.setToolTip("Select the type of course test you would like to create.");
        return combo;
    }
    

    private SimpleComboBox<QuizProblem> createQuizSizeCombo() {
        SimpleComboBox<QuizProblem> combo = new SimpleComboBox<QuizProblem>(new StringLabelProvider<QuizProblem>());

        combo.add(new QuizProblem("15 problems", 15));
        combo.add(new QuizProblem("30 problems", 30));
        combo.add(new QuizProblem("45 problems", 45));
        combo.add(new QuizProblem("60 problems", 60));

        combo.setValue(combo.getStore().get(0));
        combo.setAllowTextSelection(false);
        combo.setForceSelection(true);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.setToolTip("Select the number of problems in the course test.");
        return combo;
    }

    public static void startTest() {
        new FinalExamCreationManager(2, new GroupDto(1, "test", "info", 2));
    }

    class QuizProblem {
        String label;
        int size;
        
        public QuizProblem(String label, int size) {
            this.label = label;
            this.size = size;
        }
        
        public String toString() {
            return label;
        }
    }
    
}
