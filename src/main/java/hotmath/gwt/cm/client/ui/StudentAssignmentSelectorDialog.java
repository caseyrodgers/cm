package hotmath.gwt.cm.client.ui;

import hotmath.gwt.cm.client.CatchupMath;
import hotmath.gwt.cm_core.client.UserInfoBase;
import hotmath.gwt.cm_rpc.client.UserInfo;
import hotmath.gwt.cm_rpc.client.rpc.GetAssignmentsForUserAction;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.Assignment;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.StudentAssignmentInfo;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.assignment.GotoNextAnnotationButton;
import hotmath.gwt.cm_tools.client.util.CmMessageBox;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
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
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridViewConfig;

public class StudentAssignmentSelectorDialog extends GWindow {

    Grid<StudentAssignmentInfo> _grid;
    StudentAssignmentSelectorDialogProperties props = GWT.create(StudentAssignmentSelectorDialogProperties.class);
    public StudentAssignmentSelectorDialog() {
        super(false);
        setHeadingText("Your Assignments");
        setPixelSize(500, 400);
        
        addTool(createRefreshButton());
        TextButton nextNoteButton = new GotoNextAnnotationButton();
        nextNoteButton.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                hide();
            }
        });
        
        addButton(nextNoteButton);
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
        
        
        _grid.getView().setViewConfig(new GridViewConfig<StudentAssignmentInfo>() {
            @Override
            public String getRowStyle(StudentAssignmentInfo model, int rowIndex) {
                if (model != null) {
                    if (model.isChanged()) {
                        return "assign-showwork-admin-unseen";
                    }
                }
                return null;
            }

            @Override
            public String getColStyle(StudentAssignmentInfo model, ValueProvider<? super StudentAssignmentInfo, ?> valueProvider, int rowIndex, int colIndex) {
                return null;
            }
        });
        
        
        bCont.setCenterWidget(_grid);
        
        BorderLayoutData bld = new BorderLayoutData(20);
        bld.setMargins(new Margins(10));
        bCont.setNorthWidget(createHeader(), bld);
        bCont.setSouthWidget(createLedgend(), new BorderLayoutData(20));
        setWidget(bCont);
    }
    
    
    private boolean anyAssignmentHasChanged() {
        if(UserInfo.getInstance().getAssignmentMetaInfo() != null && UserInfo.getInstance().getAssignmentMetaInfo().isChanged()) {
            return true;
        }
        else {
            return false;
        }
    }
    
    private IsWidget createLedgend() {
        String html="";
        if(anyAssignmentHasChanged()) {
             html = "<div style='margin: 5px 5px;float: left;background: red;width: 10px'>&nbsp;</div><div style='float: left;margin-top: 2px;'>Has changed</div>";
        }
        return new HTML(html);
    }

    CheckBox _cbActive = new CheckBox();
    CheckBox _cbClosed = new CheckBox();
    CheckBox _cbTurnedIn = new CheckBox();
    
    private Widget createHeader() {
        ValueChangeHandler<Boolean> changeHandler = new ValueChangeHandler<Boolean>() {
            @Override
            public void onValueChange(ValueChangeEvent<Boolean> arg0) {
                setGridFiltered(_assignments);
            }
        };
        _cbActive.addValueChangeHandler(changeHandler);
        _cbClosed.addValueChangeHandler(changeHandler);
        _cbTurnedIn.addValueChangeHandler(changeHandler);
        
        _cbActive.setValue(true);
        _cbTurnedIn.setValue(true);
        _cbClosed.setValue(true);
        
        MyFieldLabel closeLabel = new MyFieldLabel(_cbClosed, "Show Closed Assignments", 150,30);
        FlowLayoutContainer flc = new FlowLayoutContainer();
        flc.add(closeLabel);
        return flc;
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
        TextButton btn = new TextButton("View Assignment", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                showSelectedAssignment();
            }
        });
        btn.setToolTip("View the selected assignment");
        return btn;
    }
    
    private void showSelectedAssignment() {
        StudentAssignmentInfo ass = getSelectedAssignment();
        if(ass != null) {
            hide();
            CatchupMath.getThisInstance().showAssignment(ass.getAssignKey(),null);
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
            if(s.equals("Closed") && _cbClosed.getValue()) {
                filtered.add(sai);
            }
            else if(s.equals("Open") && _cbActive.getValue()) {
                filtered.add(sai);
            }
            else if(s.equals("Turned In") && _cbTurnedIn.getValue()) {
                filtered.add(sai);
            }
            else {
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
        cols.add(new ColumnConfig<StudentAssignmentInfo, String>(props.dueDate(), 80, "Due Date"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, String>(props.status(), 70, "Status"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, String>(props.score(), 70, "Score"));
        cols.add(new ColumnConfig<StudentAssignmentInfo, String>(props.comments(), 235, "Comments"));
        ColumnModel<StudentAssignmentInfo> colModel = new ColumnModel<StudentAssignmentInfo>(cols);
        return  colModel;
    }


    interface StudentAssignmentSelectorDialogProperties extends PropertyAccess<String> {
        @Path("assignKey")
        ModelKeyProvider<StudentAssignmentInfo> key();
        ValueProvider<StudentAssignmentInfo, String> score();
        ValueProvider<StudentAssignmentInfo, Boolean> graded();
        ValueProvider<StudentAssignmentInfo, Date> turnInDate();
        ValueProvider<StudentAssignmentInfo, Integer> cntSubmitted();
        ValueProvider<StudentAssignmentInfo, Integer> cntProblems();
        ValueProvider<StudentAssignmentInfo, Boolean> complete();
        ValueProvider<StudentAssignmentInfo, String> status();
        ValueProvider<StudentAssignmentInfo, String> comments();
        
        @Path("dueDateFormatted")
        ValueProvider<StudentAssignmentInfo, String> dueDate();
    }
    
    
    private static StudentAssignmentSelectorDialog __lastInstance;
    public static void showSharedDialog() {
        
        if(true || CmShared.getQueryParameter("debug") == null) {
            __lastInstance = null;
        }
        if(__lastInstance == null) {
            __lastInstance = new StudentAssignmentSelectorDialog();
        }
        else {
            __lastInstance.show();
        }
    }
}
