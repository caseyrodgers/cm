package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentGradingPanel.ProblemSelectionCallback;
import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentGradingPanel.UpdateGradeCallback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
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
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
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

public class GradeBookDialog {
    private static final int MAX_FIELD_LEN = 400;
    private static final int FIELD_LABEL_LEN = 90;
    TextField _studentName = new TextField();
    TextArea _comments = new TextArea();
    StudentAssignment _stuAssignment;
    DateField _dueDate;
    TextField _grade = new TextField();
    ComboBox<AssignmentStatusDto> _assignmentStatus;
    AssignmentGradingPanel agPanel;
    AssignmentQuestionViewerPanel _questionViewer = new AssignmentQuestionViewerPanel();

    public GradeBookDialog(final StudentAssignment stuAssignment, final CallbackOnComplete callbackOnComplete) {
        this._stuAssignment = stuAssignment;
        final GWindow window = new GWindow(false);
        window.setPixelSize(800,600);
        window.setMaximizable(true);

        window.setHeadingHtml("Assignment: " + stuAssignment.getAssignment().getAssignmentName());

        final BorderLayoutContainer con = new BorderLayoutContainer();
        con.setBorders(true);

        VerticalLayoutContainer header = new VerticalLayoutContainer();
        
        BorderLayoutData headerData = new BorderLayoutData();
        headerData.setMargins(new Margins(20));
        headerData.setSize(70);
        header.setLayoutData(headerData);
        
        _studentName.setWidth(MAX_FIELD_LEN);
        _studentName.setReadOnly(true);
        _studentName.setValue(stuAssignment.getStudentName());
        FieldLabel studentNameLabel = new FieldLabel(_studentName,"Student Name");
        studentNameLabel.setLabelWidth(FIELD_LABEL_LEN);        
        header.add(studentNameLabel);

        _grade.setReadOnly(true);
        _grade.setWidth(50);
        _grade.setValue(getHomeworkGrade());
        FieldLabel gradeLabel = new FieldLabel(_grade, "Grade");
        gradeLabel.setLabelWidth(FIELD_LABEL_LEN);
        
        HorizontalLayoutContainer hCon = new HorizontalLayoutContainer();
        hCon.add(gradeLabel);
 
        _dueDate = new DateField();
        _dueDate.setReadOnly(true);
        _dueDate.setWidth(100);
        _dueDate.setValue(stuAssignment.getAssignment().getDueDate());
        FieldLabel dueDateLabel = new FieldLabel(_dueDate, "Due Date");
        dueDateLabel.setLabelWidth(FIELD_LABEL_LEN);
        HorizontalLayoutData hlData = new HorizontalLayoutData();
        hlData.setMargins(new Margins(0, 20,0, 20));
        hCon.add(dueDateLabel, hlData);

        _assignmentStatus = createAssignmentStatusCombo();
        _assignmentStatus.setValue(determineAssignmentStatus(stuAssignment.getAssignment()));
        _assignmentStatus.setReadOnly(true);
        hCon.add(_assignmentStatus);

        FieldLabel _statusLabel = new FieldLabel(_assignmentStatus, "Status");
        _statusLabel.setLabelWidth(FIELD_LABEL_LEN-20);
        HorizontalLayoutData hData = new HorizontalLayoutData();
        hData.setMargins(new Margins(0, 20,0, 20));
        hCon.add(_statusLabel, hData);

        header.add(hCon);

        con.setNorthWidget(header);

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
            public void problemWasSelected(ProblemDto selection) {
                _questionViewer.viewQuestion(stuAssignment, selection);
            }
        }, updateGradeCallback);
        BorderLayoutData data = new BorderLayoutData();
        data.setSize(400.0);
        agPanel.setBorders(true);
        agPanel.setLayoutData(data);
        blContainer.setWestWidget(agPanel);

        blContainer.setCenterWidget(_questionViewer);
        blContainer.forceLayout();

        con.setCenterWidget(blContainer);

        window.addButton(new TextButton("Save",new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                saveStudentGradeBook();
                window.hide();
                callbackOnComplete.isComplete();
            }
        }));
        
        window.addHideHandler(new HideHandler() {
            @Override
            public void onHide(HideEvent event) {
                if(agPanel.isChanges()) {
                    ConfirmMessageBox box = new ConfirmMessageBox("Pending changes", "There are pending changes, do you want to save them to the server?");
                    box.addHideHandler(new HideHandler() {
                        @Override
                        public void onHide(HideEvent event) {
                            Dialog btn = (Dialog) event.getSource();
                            if(!btn.getHideButton().getText().equalsIgnoreCase("Yes")) {
                                window.setVisible(true);
                            }
                            else {
                                saveStudentGradeBook();
                            }
                        }
                    });
                }
            }
        });

        window.addCloseButton();

        window.setWidget(con);
        
        
        window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                CmRpc.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        });

        window.show();    
        
        
        
    }
    
    private String getHomeworkGrade() {
    	int percent = 0;
    	int numCorrect = 0;

    	for (StudentProblemDto dto : _stuAssignment.getAssigmentStatuses()) {
    		numCorrect += (dto.getStatus().equalsIgnoreCase(ProblemStatus.CORRECT.toString())) ? 1 : 0;
    	}
    	if (_stuAssignment.getAssigmentStatuses().size() > 0)
        	percent = Math.round((float)numCorrect * 100.0f / (float)_stuAssignment.getAssigmentStatuses().size());

		return percent + "%";
	}

	private AssignmentStatusDto determineAssignmentStatus(Assignment assignment) {
        for(AssignmentStatusDto d: _assignmentStatus.getStore().getAll()) {
            if(d.getStatus().equals(assignment.getStatus())) {
                return d;
            }
        }
        return null;
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
        assStore.add(new AssignmentStatusDto("Expired"));
   
        ComboBox<AssignmentStatusDto> combo = new ComboBox<AssignmentStatusDto>(assStore, props.statusLabel());
        
        combo.setToolTip("Overall status for this Assignment");
        combo.setWidth(120);
        combo.setTypeAhead(false);
        combo.setTriggerAction(TriggerAction.ALL);
        combo.setAllowBlank(false);
        
        return combo;        
    }
    
    private void saveStudentGradeBook() {
        
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

                UpdateStudentAssignmentStatusAction action = new UpdateStudentAssignmentStatusAction(_stuAssignment);

                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData results) {
                CmBusyManager.setBusy(false);

                agPanel.setChanges(false);
                
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
