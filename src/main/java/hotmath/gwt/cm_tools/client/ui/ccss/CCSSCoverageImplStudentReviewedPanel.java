package hotmath.gwt.cm_tools.client.ui.ccss;

import java.util.ArrayList;
import java.util.List;

import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.CCSSCoverageData;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageDataAction;

/**
 * CCSS Reviewed Lesson Coverage for selected student
 */
public class CCSSCoverageImplStudentReviewedPanel extends CCSSCoverageImplPanelBase {
    public CCSSCoverageImplStudentReviewedPanel(CCSSCoverageImplBase base) {
        super(base);
    }

    @Override
    protected ColumnModel<CCSSCoverageData> getColumns() { 
        List<ColumnConfig<CCSSCoverageData, ?>> cols = new ArrayList<ColumnConfig<CCSSCoverageData, ?>>();

        ColumnConfig<CCSSCoverageData, String> column = new ColumnConfig<CCSSCoverageData, String>(_gridProps.ccssName(), 140, "Reviewed Lesson CCSS coverage");
        column.setWidth(300);
        cols.add(column);
        
        return new ColumnModel<CCSSCoverageData>(cols);
    }
    
    @Override
    public String[] getPanelColumns() {
        return new String[]{"CCSS Name"};
    }

    @Override
    public CCSSCoverageDataAction.ReportType getReportType() {
        return CCSSCoverageDataAction.ReportType.STUDENT_REVIEWED;
    }

}
