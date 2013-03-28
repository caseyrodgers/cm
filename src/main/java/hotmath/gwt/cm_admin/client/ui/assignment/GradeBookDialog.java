package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentGradingPanel.ProblemSelectionCallback;
import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentGradingPanel.UpdateGradeCallback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentStatusDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.UpdateStudentAssignmentStatusAction;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.assignment.GradeBookUtils;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.menu.Menu;
import com.sencha.gxt.widget.core.client.menu.MenuItem;

public class GradeBookDialog {
    private static final int MAX_FIELD_LEN = 400;
    private static final int FIELD_LABEL_LEN = 90;
    TextField _studentName = new TextField();
    TextArea _comments = new TextArea();
    StudentAssignment _stuAssignment;
    DateField _turnInDate;
    TextField _grade = new TextField();
    AssignmentGradingPanel agPanel;
    AssignmentQuestionViewerPanel _questionViewer = new AssignmentQuestionViewerPanel();

    public GradeBookDialog(final StudentAssignment stuAssignment, final CallbackOnComplete callbackOnComplete) {
        this._stuAssignment = stuAssignment;
        final GWindow window = new GWindow(false);
        window.setPixelSize(800,600);
        window.setMaximizable(true);

        window.setHeadingHtml("Grade Assignment: Due: " + stuAssignment.getAssignment().getDueDate());

        final BorderLayoutContainer mainBorderPanel = new BorderLayoutContainer();
        mainBorderPanel.setBorders(true);

        VerticalLayoutContainer header = new VerticalLayoutContainer();
        
        BorderLayoutData headerData = new BorderLayoutData();
        headerData.setMargins(new Margins(20));
        header.setLayoutData(headerData);

        _studentName.setWidth(MAX_FIELD_LEN);
        _studentName.setReadOnly(true);
        _studentName.setValue(stuAssignment.getStudentName());
        FieldLabel studentNameLabel = new FieldLabel(_studentName,"Student Name");
        studentNameLabel.setLabelWidth(FIELD_LABEL_LEN);        
        header.add(studentNameLabel);

        _grade.setReadOnly(true);
        _grade.setWidth(50);
        _grade.setValue(GradeBookUtils.getHomeworkGrade(_stuAssignment.getAssigmentStatuses()));
        FieldLabel gradeLabel = new FieldLabel(_grade, "Grade");
        gradeLabel.setLabelWidth(FIELD_LABEL_LEN);
        header.add(gradeLabel);

        _turnInDate = new DateField();
        _turnInDate.setReadOnly(true);
        _turnInDate.setWidth(100);
        _turnInDate.setValue(stuAssignment.getTurnInDate());
        FieldLabel turnInDateLabel = new FieldLabel(_turnInDate, "Turned In");
        turnInDateLabel.setLabelWidth(FIELD_LABEL_LEN);
        HorizontalLayoutData dd = new HorizontalLayoutData();
        dd.setMargins(new Margins(0, 20,0, 20));
        header.add(turnInDateLabel);

        BorderLayoutData bd = new BorderLayoutData(80);
        bd.setMargins(new Margins(20));
        mainBorderPanel.setNorthWidget(header, bd);

        BorderLayoutContainer blContainer = new BorderLayoutContainer();

        UpdateGradeCallback updateGradeCallback = new UpdateGradeCallback() {
        	@Override
        	public void updateGrade(int percent) {
        		String value = percent + "%";
        		_grade.setValue(value);
        	}
        };

        agPanel = new AssignmentGradingPanel(stuAssignment, new ProblemSelectionCallback() {
            @Override
            public void problemWasSelected(StudentProblemDto selection) {
                _questionViewer.viewQuestion(stuAssignment, selection);
            }
        }, updateGradeCallback);
        
        
        BorderLayoutData data = new BorderLayoutData();
        data.setSize(400.0);
        data.setSplit(true);
        data.setCollapsible(true);
        agPanel.setBorders(true);
        agPanel.setLayoutData(data);
        blContainer.setWestWidget(agPanel);

        blContainer.setCenterWidget(_questionViewer);
        blContainer.forceLayout();

        mainBorderPanel.setCenterWidget(blContainer);
        
        
        
        TextButton saveBtn = new TextButton("Save");
        
        Menu menu = new Menu();
        menu.add(new MenuItem("Save, do not release grade", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                saveStudentGradeBook(false);
                window.hide();
                callbackOnComplete.isComplete();
            }
        }));
        menu.add(new MenuItem("Save, and release grade", new SelectionHandler<MenuItem>() {
            @Override
            public void onSelection(SelectionEvent<MenuItem> event) {
                saveStudentGradeBook(true);
                window.hide();
                callbackOnComplete.isComplete();
            }
        }));
        saveBtn.setMenu(menu);

        window.addButton(saveBtn);
        
        TextButton closeButton = new TextButton("Close");
        closeButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(agPanel.isChanges()) {
                    ConfirmMessageBox box = new ConfirmMessageBox("Pending changes", "There are pending changes.  Would you like to save them?");
                    box.addHideHandler(new HideHandler() {
                        @Override
                        public void onHide(HideEvent event) {
                            Dialog btn = (Dialog) event.getSource();
                            if(btn.getHideButton().getText().equalsIgnoreCase("Yes")) {
                                saveStudentGradeBook(false);
                            }
                            
                            window.hide();
                        }
                    });
                    box.setVisible(true);
                }
                else {
                    window.hide();
                }
            }
        });
        
        window.addButton(closeButton);

        window.setWidget(mainBorderPanel);
        
        
        window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                CmRpc.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        });

        window.show();    
        
        
        
    }



    public interface EditAssignDialogProperties extends PropertyAccess<String> {
        ModelKeyProvider<AssignmentStatusDto> status();
        LabelProvider<AssignmentStatusDto> statusLabel();
      }
    
    
    private ComboBox<AssignmentStatusDto> createAssignmentStatusCombo() {
        
        EditAssignDialogProperties props = GWT.create(EditAssignDialogProperties.class);
        
        ListStore<AssignmentStatusDto> assStore = new ListStore<AssignmentStatusDto>(props.status());
        assStore.add(new AssignmentStatusDto("Open"));
        assStore.add(new AssignmentStatusDto("Closed"));
        assStore.add(new AssignmentStatusDto("Past Due"));
   
        ComboBox<AssignmentStatusDto> combo = new ComboBox<AssignmentStatusDto>(assStore, props.statusLabel());
        
        combo.setToolTip("Overall status for this Assignment");
        combo.setWidth(120);
        combo.setTypeAhead(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setAllowBlank(false);
        
        return combo;        
    }
    
    private void saveStudentGradeBook(final boolean releaseGrades) {
        
        Log.debug("Saving gradebook to server");
        
        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
   
                ListStore<StudentProblemDto> spStore = agPanel._store;

                // commit any edits
                spStore.commitChanges();
                
                CmList<StudentProblemDto> spList = new CmArrayList<StudentProblemDto>();
                spList.addAll(spStore.getAll());

                _stuAssignment.setAssigmentStatuses(spList);

                UpdateStudentAssignmentStatusAction action = new UpdateStudentAssignmentStatusAction(_stuAssignment, releaseGrades);

                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData results) {
                CmBusyManager.setBusy(false);

                agPanel.setChanges(false);
                
                
                CmRpc.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.ASSIGNMENTS));
                
                Log.debug("Student assignment status (" + _stuAssignment.getAssignment().getAssignKey() + ") saved successfully");
            }
            
            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);
                
                Log.debug("Error saving statdent assignment status", error);
                super.onFailure(error);
            }

        }.register();
        
    }
    
 
}
