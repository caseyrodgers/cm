package hotmath.gwt.cm_admin.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.cm_tools.client.model.AssignmentModel;
import hotmath.gwt.cm_tools.client.model.GradeBookModel;
import hotmath.gwt.cm_tools.client.ui.CmWindow.CmWindow;

public class HomeworkDetailsWindow extends CmWindow {

	GradeBookModel gradeBookMdl;
	
	public HomeworkDetailsWindow(GradeBookModel mdl) {
        setHeading("Homework Details for " + mdl.getUserName());
        setSize("640px", "480px");
        setLayout(new FillLayout());
        setModal(true);

        gradeBookMdl = mdl;

        add(defineGrid());
        
        addCloseButton();
	}

	private Grid<AssignmentModel> defineGrid() {

		ListStore<AssignmentModel> store = new ListStore<AssignmentModel>();

		ColumnModel cm = defineColumns();

        final Grid<AssignmentModel> grid = new Grid<AssignmentModel>(store, cm);
        grid.setBorders(true);
        grid.setStripeRows(true);
        grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        grid.getSelectionModel().setFiresEvents(true);

        grid.setWidth("650px");
        grid.setHeight("300px");
        grid.setStateful(true);
        grid.setLoadMask(true);

        grid.getStore().add(gradeBookMdl.getAssignmentList());

        return grid;
	}

	private ColumnModel defineColumns() {
		List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

		ColumnConfig column = new ColumnConfig();
		column.setId("name");
		column.setHeader("Assignment");
		column.setWidth(80);
		column.setSortable(true);

		configs.add(column);

		column = new ColumnConfig();
		column.setId("cpName");
		column.setHeader("Homework");
		column.setWidth(140);
		column.setSortable(true);

		configs.add(column);

		column = new ColumnConfig();
		column.setId("lessonName");
		column.setHeader("Lesson");
		column.setWidth(140);
		column.setSortable(true);

		configs.add(column);
		
		column = new ColumnConfig();
		column.setId("numCorrect");
		column.setHeader("Right");
		column.setWidth(50);
		column.setSortable(true);

		configs.add(column);

		column = new ColumnConfig();
		column.setId("numWrong");
		column.setHeader("Wrong");
		column.setWidth(50);
		column.setSortable(true);

		configs.add(column);

		column = new ColumnConfig();
		column.setId("numNotAnswered");
		column.setHeader("Not Answered");
		column.setWidth(85);
		column.setSortable(true);

		configs.add(column);

		column = new ColumnConfig();
		column.setId("percentCorrect");
		column.setHeader("% Correct");
		column.setWidth(80);
		column.setSortable(true);

		configs.add(column);

		ColumnModel cm = new ColumnModel(configs);
		return cm;

	}

}
