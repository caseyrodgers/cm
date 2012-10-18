package hotmath.gwt.cm_admin.client.ui.assignment;

import hotmath.gwt.cm_rpc.client.model.assignment.ProblemDto;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentAssignment;
import hotmath.gwt.cm_rpc.client.model.assignment.StudentProblemDto;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.Converter;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.StringLabelProvider;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.PropertyEditor;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
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
    ColumnConfig<StudentProblemDto, String> problemCol;
    ColumnConfig<StudentProblemDto, String> statusCol;
    ListStore<StudentProblemDto> _store;
    Map<String, Integer> _correctIncorrectMap;
    StudentProblemDto _lastProblem;

    enum GradedStatus {
    	NO("No"), YES("Yes");
    	static GradedStatus parseString(String object) {
    		if (GradedStatus.NO.toString().equals(object)) {
    			return GradedStatus.NO;
    		} else if (GradedStatus.YES.toString().equals(object)) {
    			return GradedStatus.YES;
    		} else {
    			return GradedStatus.NO;
    		}
    	}
    	private String status;

    	GradedStatus(String status) {
    		this.status = status;
    	}

    	@Override
    	public String toString() {
    		return status;
    	}
    }      

    static public interface ProblemSelectionCallback {
        void problemWasSelected(ProblemDto selection);
    }

    ProblemSelectionCallback _problemSelectionCallBack;

    static public interface UpdateGradeCallback {
    	void updateGrade(int percent);
    }

    UpdateGradeCallback _updateGradeCallback;

    public AssignmentGradingPanel(StudentAssignment studentAssignment, ProblemSelectionCallback callBack, UpdateGradeCallback updateGradeCallback){
        _problemSelectionCallBack = callBack;
        _updateGradeCallback = updateGradeCallback;
        _correctIncorrectMap = initCorrectIncorrectMap(studentAssignment);
        
        super.setHeadingText("Problems for Student/Assignment");
        super.getHeader().setHeight("30px");

        problemCol = new ColumnConfig<StudentProblemDto, String>(spProps.pidLabel(), 120, "Problem");
        problemCol.setRowHeader(true);

        statusCol = new ColumnConfig<StudentProblemDto, String>(spProps.status(), 100, "Status");
        statusCol.setRowHeader(true);

//        gradedCol = new ColumnConfig<StudentProblemDto, String>(spProps.isGraded(), 75, "Accepted");
//        gradedCol = new ColumnConfig<StudentProblemDto, String>(new StudentProblemGradedStatusValueProvider(), 50,
//        				"Graded");
//        gradedCol.setRowHeader(true);

        colConfList = new ArrayList<ColumnConfig<StudentProblemDto, ?>>();
        colConfList.add(problemCol);
        colConfList.add(statusCol);
        //colConfList.add(gradedCol);
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
            return object == null ? ProblemStatus.NOT_VIEWED.toString() : object.toString();
          }
        });
        combo.setTriggerAction(TriggerAction.ALL);
        combo.add(ProblemStatus.NOT_VIEWED);
        combo.add(ProblemStatus.VIEWED);
        combo.add(ProblemStatus.PENDING);
        combo.add(ProblemStatus.CORRECT);
        combo.add(ProblemStatus.INCORRECT);
        combo.setForceSelection(true);
        combo.setPropertyEditor(new PropertyEditor<ProblemStatus>() {
            @Override
            public ProblemStatus parse(CharSequence text) throws ParseException {
              return ProblemStatus.parseString(text.toString());
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
				if (pStatus.equals(ProblemStatus.CORRECT) || pStatus.equals(ProblemStatus.INCORRECT)) {
					_correctIncorrectMap.put(pid, (pStatus.equals(ProblemStatus.CORRECT)?1:0));
					_lastProblem.setIsGraded(GradedStatus.YES.toString());
				}
				else {
					_correctIncorrectMap.put(pid, null);
					_lastProblem.setIsGraded(GradedStatus.NO.toString());
				}
				int numCorrect = 0;
				for (StudentProblemDto sbDto : _store.getAll()) {
					Integer value = _correctIncorrectMap.get(sbDto.getPid());
					numCorrect += (value != null && value.intValue() > 0) ? 1 : 0;
				}
				int percent = Math.round((float)numCorrect*100.0f/(float)_store.getAll().size());
				_updateGradeCallback.updateGrade(percent);
	            _gradingGrid.getStore().update(_lastProblem);

			}
        	
        });
/*        
        SimpleComboBox<GradedStatus> gradedCombo = new SimpleComboBox<GradedStatus>(new StringLabelProvider<GradedStatus>());
        gradedCombo.setTriggerAction(TriggerAction.ALL);
        gradedCombo.add(GradedStatus.NO);
        gradedCombo.add(GradedStatus.YES);
        gradedCombo.setPropertyEditor(new PropertyEditor<GradedStatus>() {
            @Override
            public GradedStatus parse(CharSequence text) throws ParseException {
              return GradedStatus.parseString(text.toString());
            }
       
            @Override
            public String render(GradedStatus object) {
              return object == null ? GradedStatus.NO.toString() : object.toString();
            }
        });
*/
        editing = createGridEditing(_gradingGrid);
        editing.addEditor(statusCol, new Converter<String, ProblemStatus>() {
     
          @Override
          public String convertFieldValue(ProblemStatus object) {
            return object == null ? ProblemStatus.NOT_VIEWED.toString() : object.toString();
          }
     
          @Override
          public ProblemStatus convertModelValue(String object) {
            return ProblemStatus.parseString(object);
          }
     
        }, combo);
/*
        editing.addEditor(gradedCol, new Converter<String, GradedStatus>() {
            
            @Override
            public String convertFieldValue(GradedStatus object) {
              return object == null ? GradedStatus.NO.toString() : object.toString();
            }
       
            @Override
            public GradedStatus convertModelValue(String object) {
              return GradedStatus.parseString(object);
            }
       
          }, gradedCombo);
*/        
        
        _gradingGrid.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<StudentProblemDto>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<StudentProblemDto> event) {
                _problemSelectionCallBack.problemWasSelected(event.getSelection().get(0).getProblem());
                _lastProblem = event.getSelection().get(0);
            }
        });
        
        /** select first one */
        if(_gradingGrid.getStore().size() > 0) {
            _gradingGrid.getSelectionModel().select(false,  _gradingGrid.getStore().get(0));
        }
        
        addTool(createAcceptAllButton());
        
        
        setWidget(_gradingGrid);

    }
    
    private Map<String, Integer> initCorrectIncorrectMap(StudentAssignment studentAssignment) {
    	Map<String, Integer> map = new HashMap<String,Integer>();

    	for (StudentProblemDto dto : studentAssignment.getAssigmentStatuses()) {
    		if (dto.getStatus().equalsIgnoreCase(ProblemStatus.CORRECT.toString())) {
    			map.put(dto.getPid(), 1);
    		}
    	}
    	return map;
	}

	private TextButton createAcceptAllButton() {
        TextButton btn = new TextButton("Accept All");
        btn.addSelectHandler(new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                markAllAccepted();
            }
        });
        return btn;
    }
    
    private void markAllAccepted() {
        for(StudentProblemDto dto: _gradingGrid.getStore().getAll()) {
            dto.setIsGraded("Yes");
            _gradingGrid.getStore().update(dto);
        }
    }
    
    protected GridEditing<StudentProblemDto> createGridEditing(Grid<StudentProblemDto> editableGrid) {
        return new GridInlineEditing<StudentProblemDto>(editableGrid);
    }
/*
    private class StudentProblemGradedStatusValueProvider extends Object implements ValueProvider<StudentProblemDto, String> {

    	StudentProblemGradedStatusValueProvider() {
    	    super();
    	}
    	
		@Override
		public String getValue(StudentProblemDto stuProbDto) {
        	return stuProbDto.isGraded()?GradedStatus.YES.toString():GradedStatus.NO.toString();
		}

		@Override
		public void setValue(StudentProblemDto stuProbDto, String value) {
			stuProbDto.setIsGraded(value.equalsIgnoreCase(GradedStatus.YES.toString()));
		}

		@Override
		public String getPath() {
			return null;
		}

    }
*/    
}
