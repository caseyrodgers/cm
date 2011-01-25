package hotmath.gwt.cm_admin.client.ui;

import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

public class HighlightImplLoginsWeekDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplLoginsWeekDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.LOGINS_PER_WEEK;
    }
    
    @Override
    protected ColumnModel getColumns() {
        // TODO Auto-generated method stub
        ColumnModel cm = super.getColumns();
        cm.getColumn(1).setHeader("Logins Per Week");
        return cm;
    }
   
}
