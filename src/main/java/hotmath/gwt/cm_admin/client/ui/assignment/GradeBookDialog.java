package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentGradingPanel.ProblemSelectionCallback;
import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentGradingPanel.UpdateGradeCallback;
import hotmath.gwt.cm_core.client.util.CmAlertify.ConfirmCallback;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedHandler.TypeOfUpdate;
import hotmath.gwt.cm_rpc.client.event.WindowHasBeenResizedEvent;
import hotmath.gwt.cm_rpc.client.rpc.UpdateStudentAssignmentStatusAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentStatusDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto.ProblemType;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_core.client.CmRpcCore;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.box.ConfirmMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent;
import com.sencha.gxt.widget.core.client.event.DialogHideEvent.DialogHideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
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
    private CallbackOnComplete callbackOnComplete;
    private GWindow _window;
    final BorderLayoutContainer _mainBorderPanel;

    public GradeBookDialog(final StudentAssignment stuAssignment, final CallbackOnComplete callbackOnComplete) {
        this._stuAssignment = stuAssignment;
        this.callbackOnComplete = callbackOnComplete;
        _window = new GWindow(false);
        _window.setPixelSize(800, 600);
        _window.setMaximizable(true);

        _window.setHeadingHtml("Grade Assignment: Due: " + stuAssignment.getAssignment().getDueDate());

        _mainBorderPanel = new BorderLayoutContainer();
        _mainBorderPanel.setBorders(true);

        VerticalLayoutContainer header = new VerticalLayoutContainer();

        BorderLayoutData headerData = new BorderLayoutData();
        headerData.setMargins(new Margins(20));
        header.setLayoutData(headerData);

        _studentName.setWidth(MAX_FIELD_LEN);
        _studentName.setReadOnly(true);
        _studentName.setValue(stuAssignment.getStudentName());
        FieldLabel studentNameLabel = new FieldLabel(_studentName, "Student Name");
        studentNameLabel.setLabelWidth(FIELD_LABEL_LEN);
        header.add(studentNameLabel);

        _grade.setReadOnly(true);
        _grade.setWidth(50);

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
        dd.setMargins(new Margins(0, 20, 0, 20));
        header.add(turnInDateLabel);

        BorderLayoutData bd = new BorderLayoutData(80);
        bd.setMargins(new Margins(20));
        _mainBorderPanel.setNorthWidget(header, bd);

        UpdateGradeCallback updateGradeCallback = new UpdateGradeCallback() {
            @Override
            public void updateGrade(int percent) {
                String value = percent + "%";
                updateGrade(value);
            }

            @Override
            public void updateGrade(String percent) {
                _grade.setValue(percent);
            }
        };

        agPanel = new AssignmentGradingPanel(_stuAssignment, new ProblemSelectionCallback() {
            @Override
            public void problemWasSelected(StudentProblemDto selection) {
                _questionViewer.viewQuestion(_stuAssignment, selection);
            }
        }, updateGradeCallback);

        BorderLayoutContainer blContainer = new BorderLayoutContainer();
        BorderLayoutData data = new BorderLayoutData();
        data.setSize(400.0);
        data.setSplit(true);
        data.setCollapsible(true);
        agPanel.setBorders(true);
        agPanel.setLayoutData(data);
        blContainer.setWestWidget(agPanel);

        blContainer.setCenterWidget(_questionViewer);
        blContainer.forceLayout();

        _mainBorderPanel.setCenterWidget(blContainer);
        _mainBorderPanel.forceLayout();

        TextButton saveBtn = new TextButton("Save");

        if (stuAssignment.isGraded()) {
            saveBtn.addSelectHandler(new SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    saveStudentGradeBookDirect(false);
                    closeThisWindow();
                }
            });
        } else {
            Menu menu = new Menu();
            menu.add(new MenuItem("Save, do not release grade", new SelectionHandler<MenuItem>() {
                @Override
                public void onSelection(SelectionEvent<MenuItem> event) {
                    saveStudentGradeBookDirect(false);
                    closeThisWindow();
                }
            }));
            menu.add(new MenuItem("Save, and release grade", new SelectionHandler<MenuItem>() {
                @Override
                public void onSelection(SelectionEvent<MenuItem> event) {
                    if (saveStudentGradeBook(true)) {
                        closeThisWindow();
                    }
                }
            }));
            saveBtn.setMenu(menu);
        }

        _window.addButton(saveBtn);

        TextButton closeButton = new TextButton("Close");
        closeButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if (agPanel.isChanges()) {
                    ConfirmMessageBox box = new ConfirmMessageBox("Pending changes",
                            "There are pending changes.  Would you like to save them?");
                    box.addDialogHideHandler(new DialogHideHandler() {
                        @Override
                        public void onDialogHide(DialogHideEvent event) {
                            Dialog btn = (Dialog) event.getSource();
                            if (event.getHideButton() == PredefinedButton.YES) {
                                saveStudentGradeBook(false);
                            }
                            _window.hide();
                        }
                    });
                    box.setVisible(true);
                } else {
                    _window.hide();
                }
            }
        });

        _window.addButton(closeButton);

        _window.setWidget(_mainBorderPanel);

        _window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                CmRpcCore.EVENT_BUS.fireEvent(new WindowHasBeenResizedEvent());
            }
        });

        _window.show();
    }

    private void closeThisWindow() {
        _window.hide();
        callbackOnComplete.isComplete();
    }

    public interface EditAssignDialogProperties extends PropertyAccess<String> {
        ModelKeyProvider<AssignmentStatusDto> status();

        LabelProvider<AssignmentStatusDto> statusLabel();
    }

    private boolean saveStudentGradeBook(final boolean releaseGrades) {

        if (releaseGrades) {
            agPanel._store.commitChanges();

            int cnt = 0;
            for (StudentProblemDto sd : this._stuAssignment.getStudentStatuses().getAssigmentStatuses()) {
                if (sd.getProblem().getProblemType() == ProblemType.WHITEBOARD) {
                    if (sd.getStatus().equals("Submitted")) {
                        cnt++;
                    }
                }
            }
            if (cnt > 0) {
                String msg = " that you have not yet graded.";
                String wbMsg = "";
                if (cnt == 1) {
                    wbMsg = "is 1 whiteboard answer" + msg;
                } else {
                    wbMsg = "are " + cnt + " whiteboard answers" + msg;
                }

                msg = "There " + wbMsg + "<br/><br/>Are you sure you want to release this student's grade?";
                CmMessageBox.confirm("Pending Whiteboard Problems", msg, new ConfirmCallback() {
                    @Override
                    public void confirmed(boolean yesNo) {
                        if (yesNo) {
                            saveStudentGradeBookDirect(releaseGrades);
                            closeThisWindow();
                        }
                    }
                });
                return false;
            }
        }

        saveStudentGradeBookDirect(releaseGrades);
        return true;
    }

    private void saveStudentGradeBookDirect(final boolean releaseGrades) {
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

                _stuAssignment.getStudentStatuses().setAssigmentStatuses(spList);

                UpdateStudentAssignmentStatusAction action = new UpdateStudentAssignmentStatusAction(_stuAssignment,
                        releaseGrades);

                setAction(action);
                CmRpcCore.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData results) {
                CmBusyManager.setBusy(false);

                agPanel.setChanges(false);

                CmRpcCore.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent(TypeOfUpdate.ASSIGNMENTS));

                Log.debug("Student assignment status (" + _stuAssignment.getAssignment().getAssignKey()
                        + ") saved successfully");
            }

            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);

                Log.debug("Error saving statdent assignment status", error);
                super.onFailure(error);
            }

        }.register();
    }

}
