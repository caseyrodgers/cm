package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentProblemStatusAction;
import hotmath.gwt.cm_rpc_assignments.client.model.ProblemStatus;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentProblemDto;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetStudentAssignmentAction;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.assignment.GradeBookUtils;
import hotmath.gwt.cm_tools.client.ui.assignment.StudentProblemGridCell;
import hotmath.gwt.cm_tools.client.ui.assignment.StudentProblemGridCell.ProblemGridCellCallback;
import hotmath.gwt.cm_tools.client.util.DefaultGxtLoadingPanel;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent;
import com.sencha.gxt.widget.core.client.event.CompleteEditEvent.CompleteEditHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;
import com.sencha.gxt.widget.core.client.info.Info;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

public class AssignmentGradingPanel extends ContentPanel {

    private StudentProblemProperties spProps = GWT.create(StudentProblemProperties.class);

    StudentAssignment _studentAssignment;
    Grid<StudentProblemDto> _gradingGrid;
    GridEditing<StudentProblemDto> editing;
    List<ColumnConfig<StudentProblemDto, ?>> colConfList;
    ColumnModel<StudentProblemDto> colMdl;
    ColumnConfig<StudentProblemDto, Integer> problemNumberCol;
    ColumnConfig<StudentProblemDto, String> problemCol;
    ColumnConfig<StudentProblemDto, String> statusCol;
    ListStore<StudentProblemDto> _store;
    Map<String, Integer> _correctIncorrectMap;
    StudentProblemDto _lastProblem;

    static public interface ProblemSelectionCallback {
        void problemWasSelected(StudentProblemDto selection);
    }

    ProblemSelectionCallback _problemSelectionCallBack;

    static public interface UpdateGradeCallback {
        void updateGrade(int percent);

        void updateGrade(String percent);
    }

    UpdateGradeCallback _updateGradeCallback;

    public AssignmentGradingPanel(StudentAssignment studentAssignment, ProblemSelectionCallback callBack, UpdateGradeCallback updateGradeCallback) {
        _problemSelectionCallBack = callBack;
        _updateGradeCallback = updateGradeCallback;

        super.setHeadingText("Click any problem status to adjust grade");
        super.getHeader().setHeight("30px");

        setWidget(new DefaultGxtLoadingPanel());

        addTool(new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                getDataFromServer();
            }
        }));

        problemNumberCol = new ColumnConfig<StudentProblemDto, Integer>(spProps.problemNumberOrdinal(), 25, "");
        problemCol = new ColumnConfig<StudentProblemDto, String>(spProps.pidLabel(), 120, "Problem");

        problemCol.setCell(new StudentProblemGridCell(new ProblemGridCellCallback() {
            @Override
            public ProblemDto getProblem(int which) {
                return _gradingGrid.getStore().get(which).getProblem();
            }
        }));

        problemCol.setRowHeader(true);

        statusCol = new ColumnConfig<StudentProblemDto, String>(spProps.status(), 100, "Status");
        statusCol.setRowHeader(true);

        // gradedCol = new ColumnConfig<StudentProblemDto,
        // String>(spProps.isGraded(), 75, "Accepted");
        // gradedCol = new ColumnConfig<StudentProblemDto, String>(new
        // StudentProblemGradedStatusValueProvider(), 50,
        // "Graded");
        // gradedCol.setRowHeader(true);

        colConfList = new ArrayList<ColumnConfig<StudentProblemDto, ?>>();
        colConfList.add(problemNumberCol);
        colConfList.add(problemCol);
        colConfList.add(statusCol);
        // colConfList.add(gradedCol);
        colMdl = new ColumnModel<StudentProblemDto>(colConfList);

        _studentAssignment = studentAssignment;
        // List<StudentProblemDto> problems =
        // _studentAssignment.getStudentStatuses().getAssigmentStatuses();
        _store = new ListStore<StudentProblemDto>(spProps.pid());
        // _store.setAutoCommit(true);
        // _store.addAll(problems);

        _gradingGrid = new Grid<StudentProblemDto>(_store, colMdl);
        _gradingGrid.setWidth(380);
        _gradingGrid.getView().setStripeRows(true);
        _gradingGrid.getView().setColumnLines(true);
        _gradingGrid.getView().setAutoExpandColumn(problemCol);

        /** highlight problems with showwork */
        _gradingGrid.getView().setViewConfig(new GridViewConfig<StudentProblemDto>() {

            @Override
            public String getRowStyle(StudentProblemDto model, int rowIndex) {
                if (model != null) {
                    if (model.isHasShowWork()) {
                        return "assign-showwork-admin";
                    }
                }
                return null;
            }

            @Override
            public String getColStyle(StudentProblemDto model, ValueProvider<? super StudentProblemDto, ?> valueProvider, int rowIndex, int colIndex) {
                return null;
            }
        });

        SimpleComboBox<ProblemStatus> combo = new SimpleComboBox<ProblemStatus>(new StringLabelProvider<ProblemStatus>());
        combo.setPropertyEditor(new PropertyEditor<ProblemStatus>() {

            @Override
            public ProblemStatus parse(CharSequence text) throws ParseException {
                return ProblemStatus.valueOf(text.toString().toUpperCase());
            }

            @Override
            public String render(ProblemStatus object) {
                return object == null ? ProblemStatus.NOT_VIEWED.toString() : object.toString();
            }
        });
        combo.setTriggerAction(TriggerAction.ALL);
        combo.add(ProblemStatus.VIEWED);
        combo.add(ProblemStatus.NOT_VIEWED);
        combo.add(ProblemStatus.PENDING);
        combo.add(ProblemStatus.CORRECT);
        combo.add(ProblemStatus.INCORRECT);
        combo.add(ProblemStatus.HALF_CREDIT);
        combo.add(ProblemStatus.SUBMITTED);
        combo.setForceSelection(true);
        combo.setPropertyEditor(new PropertyEditor<ProblemStatus>() {
            @Override
            public ProblemStatus parse(CharSequence text) throws ParseException {
                return ProblemStatus.valueOf(text.toString().toUpperCase().replace(" ", "_"));
            }

            @Override
            public String render(ProblemStatus object) {
                return object == null ? ProblemStatus.NOT_VIEWED.toString() : object.toString();
            }
        });
        combo.addValueChangeHandler(new ValueChangeHandler<ProblemStatus>() {

            @Override
            public void onValueChange(ValueChangeEvent<ProblemStatus> vcEvent) {
                ProblemStatus pStatus = vcEvent.getValue();

                String pid = _lastProblem.getPid();
                if (pStatus.equals(ProblemStatus.CORRECT) || pStatus.equals(ProblemStatus.INCORRECT) || pStatus.equals(ProblemStatus.HALF_CREDIT)) {
                    _lastProblem.setGraded(true);
                    switch (pStatus) {
                    case CORRECT:
                        _correctIncorrectMap.put(pid, 100);
                        break;

                    case HALF_CREDIT:
                        _correctIncorrectMap.put(pid, 50);
                        break;

                    case INCORRECT:
                        _correctIncorrectMap.put(pid, 0);
                        break;
                    }
                } else {
                    _correctIncorrectMap.put(pid, null);
                    _lastProblem.setGraded(false);
                }

                updateGrade();
            }
        });
        editing = createGridEditing(_gradingGrid);
        editing.addEditor(statusCol, new Converter<String, ProblemStatus>() {

            @Override
            public String convertFieldValue(ProblemStatus object) {
                return object == null ? ProblemStatus.NOT_VIEWED.toString() : object.toString();
            }

            @Override
            public ProblemStatus convertModelValue(String object) {
                return ProblemStatus.valueOf(object.toUpperCase().replace(" ", "_"));
            }

        }, combo);

        _gradingGrid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<StudentProblemDto>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<StudentProblemDto> event) {
                if (event.getSelection().size() > 0) {
                    _problemSelectionCallBack.problemWasSelected(event.getSelection().get(0));
                    _lastProblem = event.getSelection().get(0);
                } else {
                    _lastProblem = null;
                }
            }
        });

        getDataFromServer();
    }

    private Map<String, Integer> initCorrectIncorrectMap(StudentAssignment studentAssignment) {
        Map<String, Integer> map = new HashMap<String, Integer>();

        for (StudentProblemDto dto : studentAssignment.getStudentStatuses().getAssigmentStatuses()) {
            String status=dto.getStatus();
            if (status.equalsIgnoreCase(ProblemStatus.CORRECT.toString())) {
                map.put(dto.getPid(), 100);
            } else if (status.equalsIgnoreCase(ProblemStatus.HALF_CREDIT.toString())) {
                map.put(dto.getPid(), 50);
            }
        }
        return map;
    }

    private void markAllAccepted() {
        for (StudentProblemDto dto : _gradingGrid.getStore().getAll()) {
            dto.setGraded(true);
            _gradingGrid.getStore().update(dto);
        }
    }

    private void updateGrade() {
        int numCorrect = 0;
        int numIncorrect=0;
        int numHalfCorrect=0;
        
        for (StudentProblemDto sbDto : _store.getAll()) {
            if (_correctIncorrectMap.containsKey(sbDto.getPid())) {
                Integer value = _correctIncorrectMap.get(sbDto.getPid());
                if (value != null) {
                    if (value == 100) {
                        numCorrect += 1;
                    } else if (value == 50) {
                        numHalfCorrect += 1;
                    }
                    else if(value == 0) {
                        numIncorrect += 1;
                    }
                }
            }
        }
        String grade = GradeBookUtils.getHomeworkGrade(_store.size(),numCorrect,  numIncorrect, numHalfCorrect);
        _updateGradeCallback.updateGrade(grade);
        _gradingGrid.getStore().update(_lastProblem);
    }
    
    
    boolean areChanges = false;

    protected GridEditing<StudentProblemDto> createGridEditing(Grid<StudentProblemDto> editableGrid) {
        GridInlineEditing<StudentProblemDto> editor = new GridInlineEditing<StudentProblemDto>(editableGrid);
        editor.addCompleteEditHandler(new CompleteEditHandler<StudentProblemDto>() {
            @Override
            public void onCompleteEdit(CompleteEditEvent<StudentProblemDto> event) {
                areChanges = true;
            }
        });

        return editor;
    }

    public boolean isChanges() {
        return areChanges;
    }

    public void setChanges(boolean yesNo) {
        areChanges = yesNo;
    }

    private void saveChange(final StudentProblemDto selectedItem) {
        Log.debug("Saving to server: " + selectedItem);

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                Info.display("Saving", "Updating problem status on server");

                Log.debug("Updating problem status on server");
                SaveAssignmentProblemStatusAction action = new SaveAssignmentProblemStatusAction(selectedItem.getUid(), _studentAssignment.getAssignment()
                        .getAssignKey(), selectedItem.getPid(), selectedItem.getStatus());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData results) {
                CmBusyManager.setBusy(false);
                Log.debug("Problem status was updated on server");
            }

            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);
                Log.debug("Error while saving problem status", error);
                super.onFailure(error);
            }

        }.register();
    }

    /*
     * private class StudentProblemGradedStatusValueProvider extends Object
     * implements ValueProvider<StudentProblemDto, String> {
     * 
     * StudentProblemGradedStatusValueProvider() { super(); }
     * 
     * @Override public String getValue(StudentProblemDto stuProbDto) { return
     * stuProbDto
     * .isGraded()?GradedStatus.YES.toString():GradedStatus.NO.toString(); }
     * 
     * @Override public void setValue(StudentProblemDto stuProbDto, String
     * value) {
     * stuProbDto.setIsGraded(value.equalsIgnoreCase(GradedStatus.YES.toString
     * ())); }
     * 
     * @Override public String getPath() { return null; }
     * 
     * }
     */

    private void getDataFromServer() {

        CmBusyManager.setBusy(true);

        new RetryAction<StudentAssignment>() {
            @Override
            public void attempt() {
                GetStudentAssignmentAction action = new GetStudentAssignmentAction(_studentAssignment.getUid(), _studentAssignment.getAssignment()
                        .getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(StudentAssignment results) {
                CmBusyManager.setBusy(false);
                /**
                 * transfer latest status info onto current StudentAssignment
                 * 
                 */
                _studentAssignment.setStudentStatuses(results.getStudentStatuses());
                _correctIncorrectMap = initCorrectIncorrectMap(_studentAssignment);
                _updateGradeCallback.updateGrade(GradeBookUtils.getHomeworkGrade(_studentAssignment.getStudentStatuses().getAssigmentStatuses()));
                /** select first one */

                StudentProblemDto selected = _gradingGrid.getSelectionModel().getSelectedItem();
                if (_gradingGrid.getStore().size() > 0) {
                    _gradingGrid.getStore().clear();
                }
                _gradingGrid.getStore().addAll(results.getStudentStatuses().getAssigmentStatuses());
                if (selected != null) {
                    for (StudentProblemDto p : _gradingGrid.getStore().getAll()) {
                        if (p.getPid().equals(selected.getPid())) {
                            _gradingGrid.getSelectionModel().select(false, p);
                            break;
                        }
                    }
                } else if (_gradingGrid.getStore().size() > 0) {
                    _gradingGrid.getSelectionModel().select(false, _gradingGrid.getStore().get(0));
                }
                setWidget(_gradingGrid);
                forceLayout();
            }

            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);

                Log.debug("Error saving statdent assignment status", error);
                super.onFailure(error);
            }

        }.register();

    }
    
    enum GradedStatus {
        NO("No"), YES("Yes");
        private String status;

        GradedStatus(String status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return status;
        }
        
        static GradedStatus parseString(String object) {
            if (GradedStatus.NO.toString().equals(object)) {
                return GradedStatus.NO;
            } else if (GradedStatus.YES.toString().equals(object)) {
                return GradedStatus.YES;
            } else {
                return GradedStatus.NO;
            }
        }
    }      
}
