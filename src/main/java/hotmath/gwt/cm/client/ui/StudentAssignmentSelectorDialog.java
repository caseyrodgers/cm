package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_rpc.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsForUserAction;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class StudentAssignmentSelectorDialog extends GWindow {

    Grid<StudentAssignmentInfo> _grid;
    StudentAssignmentSelectorDialogProperties props = GWT.create(StudentAssignmentSelectorDialogProperties.class);
    public StudentAssignmentSelectorDialog() {
        super(false);
        setHeadingText("Your Assignments");
        setPixelSize(500, 400);
        
        addTool(createRefreshButton());
        addButton(createOpenButton());
        addCloseButton();
        createUi();
        
        readAssignmentsFromServer();
        
        setVisible(true);
    }

    
    private void createUi() {
        BorderLayoutContainer bCont = new BorderLayoutContainer();
        
        ListStore<StudentAssignmentInfo> store = new ListStore<StudentAssignmentInfo>(props.key());
        _grid = new Grid<StudentAssignmentInfo>(store, createColumnModel());
        _grid.getColumnModel().getColumn(_grid.getColumnModel().getColumnCount()-1);
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _grid.addCellDoubleClickHandler(new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                showSelectedAssignment();
            }
        });
        bCont.setCenterWidget(_grid);
        
        BorderLayoutData bld = new BorderLayoutData(40);
        bld.setMargins(new Margins(10));
        bCont.setNorthWidget(createHeader(), bld);
        setWidget(bCont);
    }
    
    CheckBox _cbActive = new CheckBox();
    CheckBox _cbGraded = new CheckBox();
    CheckBox _cbPastDue = new CheckBox();
    CheckBox _cbClosed = new CheckBox();
    
    private Widget createHeader() {
        VerticalLayoutContainer vlc = new VerticalLayoutContainer();
        VerticalLayoutData vld = new VerticalLayoutData(1,-1);
        HorizontalLayoutContainer hlc = new HorizontalLayoutContainer();


        ValueChangeHandler<Boolean> changeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> arg0) {
                setGridFiltered(_assignments);
            }
        };
        _cbActive.addValueChangeHandler(changeHandler);
        _cbGraded.addValueChangeHandler(changeHandler);
        _cbPastDue.addValueChangeHandler(changeHandler);
        _cbClosed.addValueChangeHandler(changeHandler);
        
        _cbActive.setValue(true);
        _cbGraded.setValue(true);
        _cbPastDue.setValue(true);
        _cbClosed.setValue(false);
        
        hlc.add(new MyFieldLabel(_cbActive, "Active", 30,50));
        hlc.add(new MyFieldLabel(_cbGraded, "Graded", 30,50));
        hlc.add(new MyFieldLabel(_cbPastDue, "Past Due", 55,50));
        hlc.add(new MyFieldLabel(_cbClosed, "Closed", 30,50));
        vld.setMargins(new Margins(0, 0, 5, 0));
        vlc.add(new Label("Which assignments to show?"),vld);
        vlc.add(hlc,vld);
        return vlc  ;
    }
    
    
    private Widget createRefreshButton() {
        return new TextButton("Refresh", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                readAssignmentsFromServer();
            }
        });
    }
    
    private Widget createOpenButton() {
        TextButton btn = new TextButton("Open Assignment", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showSelectedAssignment();
            }
        });
        btn.setToolTip("Open the selected assignment");
        return btn;
    }
    
    private void showSelectedAssignment() {
        StudentAssignmentInfo ass = getSelectedAssignment();
        if(ass != null) {
            hide();
            CatchupMath.getThisInstance().showAssignment(ass.getAssignKey());
        }
    }
    
    

    protected StudentAssignmentInfo getSelectedAssignment() {
        StudentAssignmentInfo si = _grid.getSelectionModel().getSelectedItem();
        if(si == null) {
            CmMessageBox.showAlert("There is no selected Assignment");
            return null;
        }
        return si;
    }

    private CmList<StudentAssignmentInfo> _assignments;
    public void readAssignmentsFromServer() {
        CatchupMathTools.setBusy(true);
        new RetryAction<CmList<StudentAssignmentInfo>>() {


            @Override
            public void attempt() {
                GetAssignmentsForUserAction action = new GetAssignmentsForUserAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            public void oncapture(CmList<StudentAssignmentInfo> assignments) {
                _assignments = assignments;
                CatchupMathTools.setBusy(false);
                if(assignments.size() == 0) {
                    showNoAssignmentMessage();
                }                else {
                    setGridFiltered(_assignments);
                }
            }
        }.register();          
    }    

    protected void setGridFiltered(List<StudentAssignmentInfo> assignments) {
        /** Use the client's date/time to determine if expired
         * 
         */
        Assignment dummy = new Assignment();
        for(StudentAssignmentInfo si: assignments) {
            dummy.setStatus(si.getStatus());
            dummy.setDueDate(si.getDueDate());
            si.setStatus(dummy.getStatusLabel());
        }
        
        if(_grid.getStore().size() > 0) {
            _grid.getStore().clear();
        }
        _grid.getStore().addAll(filterAssignments(assignments));
        if(assignments.size() > 0) {
            _grid.getSelectionModel().select(0, false);
        }
        forceLayout();
    }


    protected Collection<StudentAssignmentInfo> filterAssignments(List<StudentAssignmentInfo> assignments) {
        List<StudentAssignmentInfo> filtered = new ArrayList<StudentAssignmentInfo>();
        for(StudentAssignmentInfo sai: assignments) {
            String s = sai.getStatus();
            if(s.equals("Past Due") && _cbPastDue.getValue()) {
                filtered.add(sai);
            }
            else if(s.equals("Closed") && _cbClosed.getValue()) {
                filtered.add(sai);
            }
            else if(s.equals("Active") && _cbActive.getValue()) {
                filtered.add(sai);
            }
            else if(sai.isGraded() && _cbGraded.getValue()) {
                filtered.add(sai);
            }
        }
        
        return filtered;
    }


    private void showNoAssignmentMessage() {
        setWidget(new Label("No Assignments"));
    }

    
    private ColumnModel<StudentAssignmentInfo> createColumnModel() {
        List<ColumnConfig<StudentAssignmentInfo, ?>> cols = new ArrayList<ColumnConfig<StudentAssignmentInfo, ?>>();
        cols.add(new ColumnConfig<StudentAssignmentInfo, Date>(props.dueDate(), 80, "Due Date"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, String>(props.status(), 70, "Status"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, Boolean>(props.complete(), 70, "Complete"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, Date>(props.turnInDate(), 70, "Turned In"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, Boolean>(props.graded(), 70, "Graded"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, String>(props.comments(), 235, "Comments"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, Integer>(props.cntProblems(), 70, "Problems"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, Integer>(props.cntSubmitted(), 70, "Submitted"));
        ColumnModel<StudentAssignmentInfo> colModel = new ColumnModel<StudentAssignmentInfo>(cols);
        return  colModel;
    }


    interface StudentAssignmentSelectorDialogProperties extends PropertyAccess<String> {
        @Path("assignKey")
        ModelKeyProvider<StudentAssignmentInfo> key();
        ValueProvider<StudentAssignmentInfo, Boolean> graded();
        ValueProvider<StudentAssignmentInfo, Date> turnInDate();
        ValueProvider<StudentAssignmentInfo, Integer> cntSubmitted();
        ValueProvider<StudentAssignmentInfo, Integer> cntProblems();
        ValueProvider<StudentAssignmentInfo, Boolean> complete();
        ValueProvider<StudentAssignmentInfo, String> status();
        ValueProvider<StudentAssignmentInfo, String> comments();
        ValueProvider<StudentAssignmentInfo, Date> dueDate();
    }
    
    
    private static StudentAssignmentSelectorDialog __lastInstance;
    public static void showSharedDialog() {
        __lastInstance = null;
        if(__lastInstance == null) {
            __lastInstance = new StudentAssignmentSelectorDialog();
        }
        else {
            __lastInstance.show();
        }
    }
}
