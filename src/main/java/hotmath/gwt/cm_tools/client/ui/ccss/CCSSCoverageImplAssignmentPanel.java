package hotmath.gwt.cm_tools.client.ui.ccss;

import hotmath.gwt.shared.client.model.CCSSCoverageData;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageDataAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

/**
 * CCSS Coverage for selected Assignment
 */
public class CCSSCoverageImplAssignmentPanel extends CCSSCoverageImplPanelBase {

    public CCSSCoverageImplAssignmentPanel(CCSSCoverageImplBase base, int assignKey) {
        super(base, assignKey, 0);
    }

    @Override
    protected ColumnModel<CCSSCoverageData> getColumns() { 
        List<ColumnConfig<CCSSCoverageData, ?>> cols = new ArrayList<ColumnConfig<CCSSCoverageData, ?>>();

        ColumnConfig<CCSSCoverageData, String> column =
        		new ColumnConfig<CCSSCoverageData, String>(_gridProps.lessonName(), 200, "Lesson name");
        column.setWidth(200);
        cols.add(column);
        
        column = new ColumnConfig<CCSSCoverageData, String>(_gridProps.ccssName(), 100, "CCSS name");
        column.setWidth(100);
        cols.add(column);

        return new ColumnModel<CCSSCoverageData>(cols);
    }
    
    @Override
    public String[] getPanelColumns() {
        return new String[]{"Lesson Name", "CCSS Name"};
    }

    @Override
    public CCSSCoverageDataAction.ReportType getReportType() {
        return CCSSCoverageDataAction.ReportType.ASSIGNMENT;
    }

	@Override
	public HTML getNoDataMessage() {
		return new HTML("<h1 style='color:#1C97D1; font-size:1.2em; margin:10px; padding:10px'>There is no CCSS Coverage data available for the selected Assignment.</h1>");
	}
}
