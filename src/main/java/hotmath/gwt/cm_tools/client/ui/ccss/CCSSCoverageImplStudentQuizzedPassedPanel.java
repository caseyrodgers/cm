package hotmath.gwt.cm_tools.client.ui.ccss;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.CCSSCoverageData;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageDataAction;

/**
 * CCSS Quizzed and Passed Coverage for selected student
 */
public class CCSSCoverageImplStudentQuizzedPassedPanel extends CCSSCoverageImplPanelBase {

    public CCSSCoverageImplStudentQuizzedPassedPanel(CCSSCoverageImplBase base, int userId) {
        super(base, userId);
    }

    @Override
    protected ColumnModel<CCSSCoverageData> getColumns() { 
        List<ColumnConfig<CCSSCoverageData, ?>> cols = new ArrayList<ColumnConfig<CCSSCoverageData, ?>>();

        ColumnConfig<CCSSCoverageData, String> column = new ColumnConfig<CCSSCoverageData, String>(_gridProps.lessonName(), 200, "Lesson name");
        column.setWidth(200);
        cols.add(column);
        
        column = new ColumnConfig<CCSSCoverageData, String>(_gridProps.ccssName(), 100, "CCSS name");
        column.setWidth(100);
        cols.add(column);

        return new ColumnModel<CCSSCoverageData>(cols);
    }
    
    @Override
    public String[] getPanelColumns() {
        return new String[]{"CCSS Name"};
    }

    @Override
    public CCSSCoverageDataAction.ReportType getReportType() {
        return CCSSCoverageDataAction.ReportType.STUDENT_QUIZZED_PASSED;
    }

	@Override
	public HTML getNoDataMessage() {
		return new HTML("<h1 style='color:#1C97D1; font-size:1.2em; margin:10px; padding:10px'>This student did not answer any quiz questions correctly.</h1>");
	}
}
