package hotmath.gwt.cm_tools.client.ui.ccss;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import hotmath.gwt.shared.client.model.CCSSCoverageData;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageDataAction;

/**
 * CCSS Coverage by 75 to 99 percent of students in selected group
 */
public class CCSSCoverageImplGroupBy75to99PercentPanel extends CCSSCoverageImplPanelBase {
    public CCSSCoverageImplGroupBy75to99PercentPanel(CCSSCoverageImplBase base, int groupId, int adminId) {
        super(base, groupId, adminId);
    }

    @Override
    protected ColumnModel<CCSSCoverageData> getColumns() { 
        List<ColumnConfig<CCSSCoverageData, ?>> cols = new ArrayList<ColumnConfig<CCSSCoverageData, ?>>();

        ColumnConfig<CCSSCoverageData, String> column = new ColumnConfig<CCSSCoverageData, String>(_gridProps.ccssName(), 170, "CCSS Name");
        column.setSortable(false);
        cols.add(column);

        column = new ColumnConfig<CCSSCoverageData, String>(_gridProps.count(), 100, "Students");
        column.setSortable(false);
        cols.add(column);

        return new ColumnModel<CCSSCoverageData>(cols);
    }
    
    @Override
    public String[] getPanelColumns() {
        return new String[] { "CCSS Name", "# Students" };
    }

    @Override
    public CCSSCoverageDataAction.ReportType getReportType() {
        return CCSSCoverageDataAction.ReportType.GROUP_75_TO_99_PERCENT;
    }

	@Override
	protected HTML getNoDataMessage() {
		return new HTML("<h1 style='color:#1C97D1; font-size:1.2em; margin:10px; padding:10px'>No standards (CCSS) were covered by 75 to 99 percent of students.</h1>");
	}

}
