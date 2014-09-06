package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.CustomProblemModel;
import hotmath.gwt.cm_rpc_assignments.client.model.assignment.AssignmentModel;
import hotmath.gwt.cm_rpc_assignments.client.rpc.GetCustomProblemAssignmentInfoAction;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.rpc.RetryAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;

public class AssignmentsUsedPanel extends ContentPanel {
	
	private CustomProblemModel _customProblem;

	public AssignmentsUsedPanel(){
		
		setHeadingText("Assigned To");
		drawUi();
	}
	
	public interface AssProperties extends PropertyAccess<String> {
		ModelKeyProvider<AssignmentModel> lessonName();

		ValueProvider<AssignmentModel, String> name();
		@Path("group.name")
		ValueProvider<AssignmentModel, String> groupName();
	}
	AssProperties props = GWT.create(AssProperties.class);
	Grid<AssignmentModel> _grid; 
	private void drawUi() {
		ListStore<AssignmentModel> store = new ListStore<AssignmentModel>(props.lessonName());
		List<ColumnConfig<AssignmentModel, ?>> cols = new ArrayList<ColumnConfig<AssignmentModel,?>>();
		cols.add(new ColumnConfig<AssignmentModel, String>(props.name(), 160,  "Assignment"));
		cols.add(new ColumnConfig<AssignmentModel, String>(props.groupName(), 160,  "Group"));
		ColumnModel<AssignmentModel> colModel = new ColumnModel<AssignmentModel>(cols);
		
		_grid = new Grid<AssignmentModel>(store, colModel);
		

        _grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        _grid.getView().setAutoExpandColumn(cols.get(0));
		
		setWidget(_grid);
	}
	
	
	public void readDataFromServer(final String pid) {
		
		CmBusyManager.setBusy(true);
		
		new RetryAction<CmList<AssignmentModel>>() {
			@Override
			public void attempt() {
				CmBusyManager.setBusy(false);
				GetCustomProblemAssignmentInfoAction action = new GetCustomProblemAssignmentInfoAction(pid);
				setAction(action);
				CmShared.getCmService().execute(action,  this);
			}
			
			public void oncapture(hotmath.gwt.cm_rpc_core.client.rpc.CmList<AssignmentModel> asses) {
				_grid.getStore().clear();
				_grid.getStore().addAll(asses);
				CmBusyManager.setBusy(false);
			}
		}.attempt();;
	}



}
