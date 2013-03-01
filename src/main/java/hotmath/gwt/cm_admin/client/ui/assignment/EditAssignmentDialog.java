package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_admin.client.ui.assignment.AssignmentDesigner.AssignmentDesignerCallback;
import hotmath.gwt.cm_rpc.client.CmRpc;
import hotmath.gwt.cm_rpc.client.event.DataBaseHasBeenUpdatedEvent;
import hotmath.gwt.cm_rpc.client.model.AssignmentStatus;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentStatusDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentStatusAction;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentAction;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.sql.Time;
import java.util.Date;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer.HorizontalLayoutData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.form.TimeField;

public class EditAssignmentDialog {
    private static final int MAX_FIELD_LEN = 400;
    private static final int FIELD_LABEL_LEN = 70;
    TextField _assignmentName = new TextField();
    TextArea _comments = new TextArea();
    Assignment _assignment;
    DateField _dueDate;
    ComboBox<AssignmentStatusDto> _assignmentStatus;
    TextButton saveDraftMode, saveAssign;
    
    CheckBox _closeOnExpire;
    
    boolean _isDraftMode;

    AssignmentDesigner _assignmentDesigner;

    public EditAssignmentDialog(Assignment assignment) {
        this._assignment = assignment;
        _isDraftMode = _assignment.getStatus().equals("Draft");
        
        final GWindow window = new GWindow(false);
        window.setPixelSize(700, 480);
        window.setMaximizable(true);

        if (assignment.getAssignKey() == 0) {
            window.setHeadingHtml("Create Assignment");
        } else {
            window.setHeadingHtml("Edit Assignment");
        }

        final BorderLayoutContainer mainBorderPanel = new BorderLayoutContainer();
        mainBorderPanel.setBorders(true);

        VerticalLayoutContainer header = new VerticalLayoutContainer();
        VerticalLayoutContainer.VerticalLayoutData vd = new VerticalLayoutContainer.VerticalLayoutData();
        vd.setHeight(30);
        header.setLayoutData(vd);
        _assignmentName.setWidth(MAX_FIELD_LEN);
        FieldLabel assignmentNameLabel = new FieldLabel(_assignmentName, "Assignment Name");
        assignmentNameLabel.setLabelWidth(FIELD_LABEL_LEN);
        _assignmentName.setValue(assignment.getAssignmentName());

        // header.add(assignmentNameLabel);
        _comments.setWidth(MAX_FIELD_LEN);
        _comments.setHeight(50);
        _comments.setValue(_assignment.getComments());
        _comments.setEmptyText("Enter a comment here for students");
        _comments.setAllowBlank(false);
        FieldLabel commentsLabel = new FieldLabel(_comments, "Comment");
        commentsLabel.setLabelWidth(FIELD_LABEL_LEN);
        header.add(commentsLabel);
        
        
        _closeOnExpire = new CheckBox();
        _closeOnExpire.setValue(assignment.isClosePastDue());
        _closeOnExpire.setToolTip("Close automatically when past due date");
        
        HorizontalLayoutData hData1 = new HorizontalLayoutData();
        HorizontalLayoutContainer hCon = new HorizontalLayoutContainer();
        hData1.setMargins(new Margins(0, 20, 0, 20));
        
        _dueDate = new DateField();
        _dueDate.setWidth(100);
        _dueDate.setValue(assignment.getDueDate());

        hCon.add(new MyFieldLabel(_dueDate, "Due Date", FIELD_LABEL_LEN));
        _assignmentStatus = createAssignmentStatusCombo();
        _assignmentStatus.setValue(determineAssignmentStatus(assignment));
        _assignmentStatus.addBeforeSelectionHandler(new BeforeSelectionHandler<AssignmentStatusDto>() {
            @Override
            public void onBeforeSelection(BeforeSelectionEvent<AssignmentStatusDto> event) {
                if(!checkStatusIsValidChange(event.getItem())) {
                    event.cancel();
                }
            }   
        });        
        if(!_isDraftMode) {
            _assignmentStatus.getStore().remove(_assignmentStatus.getStore().size()-1);   // remove draft
            FieldLabel statusLabel = new MyFieldLabel(_assignmentStatus, "Status", 85, 100);
            statusLabel.setLabelWidth(FIELD_LABEL_LEN - 20);
            HorizontalLayoutData hData = new HorizontalLayoutData();
            hData.setMargins(new Margins(0, 20, 0, 20));
            hCon.add(statusLabel, hData1);
        }
        hCon.add(new MyFieldLabel(_closeOnExpire, "Close Past Due", 85, 20), hData1);
        header.add(hCon);
        
        BorderLayoutData bd = new BorderLayoutData();
        bd.setMargins(new Margins(20));
        mainBorderPanel.setNorthWidget(header,bd);

        _assignmentDesigner = new AssignmentDesigner(_assignment, new AssignmentDesignerCallback() {
            @Override
            public boolean isDraftMode() {
                return _assignment.getStatus().equals("Draft");
            }
        });
        BorderLayoutData data = new BorderLayoutData();
        _assignmentDesigner.setBorders(true);
        _assignmentDesigner.setLayoutData(data);
        mainBorderPanel.setCenterWidget(_assignmentDesigner);

        saveDraftMode = new TextButton("Save Draft", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(saveAssignment(true)) {
                    window.hide();
                }

            }
        });
        saveDraftMode.setToolTip("Save in draft mode allowing for future modifcations");
        String saveBtnName = _isDraftMode?"Save/Activate":"Save";
        
        saveAssign = new TextButton(saveBtnName, new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                if(_assignmentDesigner.getAssignmentPids().size() == 0) {
                    CmMessageBox.showAlert("You need to add problems to the assignment before saving");
                    _comments.focus();
                }
                else {
                    if(saveAssignment(false)) {
                        window.hide();
                    }
                }
            }
        });
        saveAssign.setToolTip("Save any changes. Once activated or closed problems cannot be edited.");

        if(_isDraftMode) {
            window.addButton(saveDraftMode);
        }
        window.addButton(saveAssign);

        if(!_isDraftMode) {
            saveDraftMode.setEnabled(false);
        }
        
        window.addCloseButton();

        window.setWidget(mainBorderPanel);

        window.show();
    }

    protected boolean  checkStatusIsValidChange(AssignmentStatusDto assignmentStatusDto) {
        String statusTo = assignmentStatusDto.getStatus();

        if(_isDraftMode) {
            CmMessageBox.showAlert("To activate this assignment, click the Save/Activate button");
            return false;
        }
        
        if(statusTo.equals("Draft")) {
            CmMessageBox.showAlert("You cannot change back to draft mode.");
            return false;
        }
        
        if (statusTo.equals("Active")) {
            // only allow change if
            // unless date is today or later.
            DateTimeFormat dtf = DateTimeFormat.getFormat("h:mm a");
            Date today = dtf.parse("12:00 AM");

            if (_dueDate.getValue().getTime() < today.getTime()) {
                CmMessageBox.showAlert("The due date needs to be changed before you can Activate this assignment.");
                return false;
            }
        }
        return true;
    }

    protected void checkChangeDraftMode() {
        
        new RetryAction<AssignmentStatus>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                int aid = UserInfoBase.getInstance().getUid();
                GetAssignmentStatusAction action = new GetAssignmentStatusAction(aid, _assignment.getAssignKey());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(AssignmentStatus assStats) {
                CmBusyManager.setBusy(false);
                if (assStats.isInUse()) {
                    CmMessageBox.showAlert("This assignment is in use cannot be set to draft mode.");
                    String value = _assignmentStatus.getValue().getStatus();
                }
                else {
                    _assignmentStatus.setText("Draft");
                }
            }

        }.register();
    }

    private AssignmentStatusDto determineAssignmentStatus(Assignment assignment) {
        for (AssignmentStatusDto d : _assignmentStatus.getStore().getAll()) {
            if (d.getStatus().equals(assignment.getStatus())) {
                return d;
            }
        }
        return _assignmentStatus.getStore().get(0);
    }

    public interface EditAssignDialogProperties extends PropertyAccess<String> {
        ModelKeyProvider<AssignmentStatusDto> status();

        LabelProvider<AssignmentStatusDto> statusLabel();
    }

    private ComboBox<AssignmentStatusDto> createAssignmentStatusCombo() {

        EditAssignDialogProperties props = GWT.create(EditAssignDialogProperties.class);

        ListStore<AssignmentStatusDto> assStore = new ListStore<AssignmentStatusDto>(props.status());
        assStore.add(new AssignmentStatusDto("Active"));
        assStore.add(new AssignmentStatusDto("Closed"));
        assStore.add(new AssignmentStatusDto("Draft"));

        ComboBox<AssignmentStatusDto> combo = new ComboBox<AssignmentStatusDto>(assStore, props.statusLabel());

        combo.setToolTip("Overall status for this Assignment");
        combo.setWidth(120);
        combo.setTypeAhead(true);
        combo.setTriggerAction(TriggerAction.ALL);

        combo.setAllowBlank(false);
        combo.setEmptyText("--Select Status--");
        combo.setForceSelection(true);

        return combo;
    }

    /** Save the assignment and return if
     *  the save is actually happening.
     *  
     * @param asDraft
     * @return
     */
    private boolean saveAssignment(boolean asDraft) {
        
        if(_comments.getValue() == null) {
            CmMessageBox.showAlert("Not Saved", "You must enter a comment before saving.");
            return false;
        }
        
        _assignment.setAssignmentName(_assignmentName.getValue());
        _assignment.setDueDate(_dueDate.getValue());
        _assignment.setComments(_comments.getValue());
        _assignment.setClosePastDue(_closeOnExpire.getValue());
        
        if(asDraft) {
            _assignment.setStatus("Draft");
        }
        else {
            if(_isDraftMode) {
                _assignment.setStatus("Active");
            }
            else {
                _assignment.setStatus(_assignmentStatus.getValue().getStatus());
            }
        }

        CmList<ProblemDto> cmPids = new CmArrayList<ProblemDto>();
        cmPids.addAll(_assignmentDesigner.getAssignmentPids());
        _assignment.setPids(cmPids);

        new RetryAction<RpcData>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                int aid = UserInfoBase.getInstance().getUid();
                SaveAssignmentAction action = new SaveAssignmentAction(aid, _assignment);
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(RpcData results) {
                CmBusyManager.setBusy(false);

                /** server might assign a new assign_key */
                int assignKey = results.getDataAsInt("key");
                _assignment.setAssignKey(assignKey);

                Log.debug("Assignment Saved", "Assignment (" + _assignment.getAssignKey() + ") saved successfully");
                
                CmRpc.EVENT_BUS.fireEvent(new DataBaseHasBeenUpdatedEvent());
            }
        }.register();
        
        
        return true;

    }

}
