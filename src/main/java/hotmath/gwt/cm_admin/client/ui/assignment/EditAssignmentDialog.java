package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.AssignmentStatusDto;
import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.RpcData;
import hotmath.gwt.cm_rpc.client.rpc.SaveAssignmentAction;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.Date;

import com.extjs.gxt.ui.client.widget.Label;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
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
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.DateField;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.TextArea;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.sencha.gxt.widget.core.client.info.Info;

public class EditAssignmentDialog {
    private static final int MAX_FIELD_LEN = 400;
    private static final int FIELD_LABEL_LEN = 70;
    TextField _assignmentName = new TextField();
    TextArea _comments = new TextArea();
    Assignment _assignment;
    DateField _dueDate;
    ComboBox<AssignmentStatusDto> _assignmentStatus;
    
    AssignmentDesigner _assignmentDesigner;
    public EditAssignmentDialog(Assignment assignment, final CallbackOnComplete callbackOnComplete) {
        this._assignment = assignment;
        final GWindow window = new GWindow(false);
        window.setPixelSize(800,600);
        
        if(assignment.getAssignKey() == 0) {
            window.setHeadingHtml("Edit Assignment: " + assignment.getAssignmentName());
        } else {
            window.setHeadingHtml("Create Assignment: " + assignment.getAssignmentName());
        }

        final BorderLayoutContainer con = new BorderLayoutContainer();
        con.setBorders(true);
        
        VerticalLayoutContainer header = new VerticalLayoutContainer();
        _assignmentName.setWidth(MAX_FIELD_LEN);
        FieldLabel assignmentNameLabel = new FieldLabel(_assignmentName,"Assignment Name");
        assignmentNameLabel.setLabelWidth(FIELD_LABEL_LEN);
        _assignmentName.setValue(assignment.getAssignmentName());
        
        LevelProperties props = GWT.create(LevelProperties.class);
        
        ModelKeyProvider<NameValueData> kp = new ModelKeyProvider<NameValueData>() {
            @Override
            public String getKey(NameValueData item) {
                return item.getName();
            }
        };

        LabelProvider<NameValueData> labProv = new LabelProvider<NameValueData>() {
            @Override
            public String getLabel(NameValueData item) {
                return item.getName();
            }
        };
        
        BorderLayoutData headerData = new BorderLayoutData();
        headerData.setMargins(new Margins(20));
        headerData.setSize(70);
        header.setLayoutData(headerData);
        
        
        // header.add(assignmentNameLabel);
        
        _comments.setWidth(MAX_FIELD_LEN);
        _comments.setHeight(50);
        _comments.setValue(_assignment.getComments());
        FieldLabel commentsLabel = new FieldLabel(_comments, "Comments");
        commentsLabel.setLabelWidth(FIELD_LABEL_LEN);
        header.add(commentsLabel);

        _dueDate = new DateField();
        _dueDate.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(ValueChangeEvent<Date> event) {
              Date d = event.getValue();
              DateTimeFormat f = DateTimeFormat.getFormat(PredefinedFormat.DATE_SHORT);
              Info.display("Value Changed", "You selected " + f.format(d));
            }
          });
        _dueDate.setWidth(100);
        _dueDate.setValue(assignment.getDueDate());
        
        
        HorizontalLayoutContainer hCon = new HorizontalLayoutContainer();
        
        FieldLabel dueDateField = new FieldLabel(_dueDate, "Due Date");
        dueDateField.setLabelWidth(FIELD_LABEL_LEN);
        hCon.add(dueDateField);
        
        _assignmentStatus = createAssignmentStatusCombo();
        _assignmentStatus.setValue(determineAssignmentStatus(assignment));
        hCon.add(_assignmentStatus);

        FieldLabel _statusLabel = new FieldLabel(_assignmentStatus, "Status");
        _statusLabel.setLabelWidth(FIELD_LABEL_LEN-20);
        HorizontalLayoutData hData = new HorizontalLayoutData();
        hData.setMargins(new Margins(0, 20,0, 20));
        hCon.add(_statusLabel, hData);

        header.add(hCon);
        
        
        con.setNorthWidget(header);
        
        _assignmentDesigner = new AssignmentDesigner(_assignment);
        BorderLayoutData data = new BorderLayoutData();
        _assignmentDesigner.setBorders(true);
        _assignmentDesigner.setLayoutData(data);
        con.setCenterWidget(_assignmentDesigner);
        
        
        
        window.addButton(new TextButton("Save",new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                saveAssignment();
                window.hide();
                callbackOnComplete.isComplete();
            }
        }));
        
        window.addCloseButton();
        
        window.setWidget(con);
        
        
        
        window.show();        
    }
    
    
    private AssignmentStatusDto determineAssignmentStatus(Assignment assignment) {
        for(AssignmentStatusDto d: _assignmentStatus.getStore().getAll()) {
            if(d.getStatus().equals(assignment.getStatus())) {
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
        assStore.add(new AssignmentStatusDto("Open"));
        assStore.add(new AssignmentStatusDto("Closed"));
        assStore.add(new AssignmentStatusDto("Expired"));
   
        ComboBox<AssignmentStatusDto> combo = new ComboBox<AssignmentStatusDto>(assStore, props.statusLabel());
        
        combo.setToolTip("Overall status for this Assignment");
        combo.setWidth(120);
        combo.setTypeAhead(false);
        combo.setTriggerAction(TriggerAction.ALL);
        
        combo.addSelectionHandler(new SelectionHandler<AssignmentStatusDto>() {
            
            @Override
            public void onSelection(SelectionEvent<AssignmentStatusDto> event) {
            }
        });
        
        combo.setAllowBlank(false);
        combo.setEmptyText("--Select Status--");
        combo.setForceSelection(true);
        
        return combo;        
    }
    
    private void saveAssignment() {
        
        _assignment.setAssignmentName(_assignmentName.getValue());
        _assignment.setDueDate(_dueDate.getValue());
        _assignment.setComments(_comments.getValue());
        _assignment.setStatus(_assignmentStatus.getValue().getStatus());
        
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
                
                Info.display("Assignment Saved", "Assignment (" + _assignment.getAssignKey() + ") saved successfully");
            }
            
            public void onFailure(Throwable error) {
                CmBusyManager.setBusy(false);
                super.onFailure(error);
            }

        }.register();
        
    }
    
 
}
