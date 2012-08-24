package hotmath.gwt.cm_tools.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

public class ProblemListPanel extends SimpleContainer {
    
    public interface ProblemListPanelProperties extends PropertyAccess<String> {
        ModelKeyProvider<StudentProblemDto> pid();
        ValueProvider<StudentProblemDto, String> pidLabel();
        ValueProvider<StudentProblemDto, String> status();
      }
    
    
    Callback _callBack;
    Grid<StudentProblemDto> _grid;
    
    public ProblemListPanel(Callback callback) {
        this._callBack = callback;
        
        ProblemListPanelProperties props = GWT.create(ProblemListPanelProperties.class);
        
        ColumnConfig<StudentProblemDto, String> labelCol = new ColumnConfig<StudentProblemDto, String>(props.pidLabel(),50, "Problem");
        ColumnConfig<StudentProblemDto, String> labelStatus = new ColumnConfig<StudentProblemDto, String>(props.status(),50, "Status");
        
        List<ColumnConfig<StudentProblemDto, ?>> l = new ArrayList<ColumnConfig<StudentProblemDto, ?>>();
        
        l.add(labelCol);
        l.add(labelStatus);
        ColumnModel<StudentProblemDto> cm = new ColumnModel<StudentProblemDto>(l);        

        ListStore<StudentProblemDto> store = new ListStore<StudentProblemDto>(props.pid());
                
        _grid = new Grid<StudentProblemDto>(store, cm);
        
        _grid.getView().setAutoExpandColumn(labelCol);
        _grid.getView().setStripeRows(true);
        _grid.getView().setColumnLines(true);
        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        

        _grid.addHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                //loadProblem()
            }
        },DoubleClickEvent.getType());
        
        _grid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<StudentProblemDto>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<StudentProblemDto> event) {
                loadProblemStatement(event.getSelection().get(0));
            }
        });
        
        add(_grid);
    }
    
    private void loadProblemStatement(StudentProblemDto studentProb) {
        _callBack.problemSelected(studentProb.getProblem());
        
        if(studentProb.getStatus().equals("Not Viewed")) {
            studentProb.setStatus("Viewed");
            _grid.getStore().update(studentProb);
        }
    }

    public void loadAssignment(StudentAssignment assignment) {
        _grid.getStore().clear();
        _grid.getStore().addAll(assignment.getAssigmentStatuses());
    }
    
    public interface Callback {
        void problemSelected(ProblemDto problem);
    }
}
