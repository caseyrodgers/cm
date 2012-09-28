package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.editing.GridEditing;
import com.sencha.gxt.widget.core.client.grid.editing.GridInlineEditing;

public class AssignmentGradingPanel extends ContentPanel {

    private StudentProblemProperties spProps = GWT.create(StudentProblemProperties.class);

    StudentAssignment _studentAssignment;
    Grid<StudentProblemDto> _gradingGrid;
    GridEditing<StudentProblemDto> editing;
    List<ColumnConfig<StudentProblemDto, ?>> colConfList;
    ColumnModel<StudentProblemDto> colMdl;
    ColumnConfig<StudentProblemDto, String> problemCol;
    ColumnConfig<StudentProblemDto, String> statusCol;
    ListStore<StudentProblemDto> _store;

    enum ProblemStatus {
    	NONE("-"), VIEWED("Viewed"), PENDING("Pending"), CORRECT("Correct"), INCORRECT("Incorrect");
    	static ProblemStatus parseString(String object) {
    		if (ProblemStatus.VIEWED.toString().equals(object)) {
    			return ProblemStatus.VIEWED;
    		} else if (ProblemStatus.CORRECT.toString().equals(object)) {
    			return ProblemStatus.CORRECT;
    		} else if (ProblemStatus.NONE.toString().equals(object)) {
    			return ProblemStatus.NONE;
    		} else if (ProblemStatus.PENDING.toString().equals(object)) {
    			return ProblemStatus.PENDING;
    		} else {
    			return ProblemStatus.INCORRECT;
    		}
    	}
    	private String status;

    	ProblemStatus(String status) {
    		this.status = status;
    	}

    	@Override
    	public String toString() {
    		return status;
    	}
    }      

    public AssignmentGradingPanel(StudentAssignment studentAssignment){
        super.setHeadingText("Problems for Student/Assignment");
        super.getHeader().setHeight("30px");

        problemCol = new ColumnConfig<StudentProblemDto, String>(spProps.pidLabel(), 120, "Problem");
        problemCol.setRowHeader(true);

        statusCol = new ColumnConfig<StudentProblemDto, String>(spProps.status(), 100, "Status");
        statusCol.setRowHeader(true);


        colConfList = new ArrayList<ColumnConfig<StudentProblemDto, ?>>();
        colConfList.add(problemCol);
        colConfList.add(statusCol);
        colMdl = new ColumnModel<StudentProblemDto>(colConfList);

        _studentAssignment = studentAssignment;
        List<StudentProblemDto> problems = _studentAssignment.getAssigmentStatuses();
        _store = new ListStore<StudentProblemDto>(spProps.pid());
        _store.addAll(problems);

        _gradingGrid = new Grid<StudentProblemDto>(_store, colMdl);
        _gradingGrid.setWidth(380);
        _gradingGrid.getView().setStripeRows(true);
        _gradingGrid.getView().setColumnLines(true);
        _gradingGrid.getView().setAutoExpandColumn(problemCol);
        
        SimpleComboBox<ProblemStatus> combo = new SimpleComboBox<ProblemStatus>(new StringLabelProvider<ProblemStatus>());
        combo.setPropertyEditor(new PropertyEditor<ProblemStatus>() {
     
          @Override
          public ProblemStatus parse(CharSequence text) throws ParseException {
            return ProblemStatus.parseString(text.toString());
          }
     
          @Override
          public String render(ProblemStatus object) {
            return object == null ? ProblemStatus.NONE.toString() : object.toString();
          }
        });
        combo.setTriggerAction(TriggerAction.ALL);
        combo.add(ProblemStatus.NONE);
        combo.add(ProblemStatus.VIEWED);
        combo.add(ProblemStatus.PENDING);
        combo.add(ProblemStatus.CORRECT);
        combo.add(ProblemStatus.INCORRECT);
        combo.setForceSelection(true);

        editing = createGridEditing(_gradingGrid);
        editing.addEditor(statusCol, new Converter<String, ProblemStatus>() {
     
          @Override
          public String convertFieldValue(ProblemStatus object) {
            return object == null ? ProblemStatus.NONE.toString() : object.toString();
          }
     
          @Override
          public ProblemStatus convertModelValue(String object) {
            return ProblemStatus.parseString(object);
          }
     
        }, combo);
        
        setWidget(_gradingGrid);

    }
    
    protected GridEditing<StudentProblemDto> createGridEditing(Grid<StudentProblemDto> editableGrid) {
        return new GridInlineEditing<StudentProblemDto>(editableGrid);
    }

}
