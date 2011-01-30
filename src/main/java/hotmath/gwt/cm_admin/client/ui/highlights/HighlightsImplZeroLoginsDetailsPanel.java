package hotmath.gwt.cm_admin.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;

import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

public class HighlightImplZeroLoginsDetailsPanel extends HighlightImplDetailsPanelBase {
    public HighlightImplZeroLoginsDetailsPanel(HighlightImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.ZERO_LOGINS;
    }
    
    
    @Override
    protected ColumnModel getColumns() {
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
        ColumnConfig column = new ColumnConfig();
        column.setId("name");
        column.setHeader("Students with zero logins");
        column.setWidth(140);
        column.setSortable(false);
        configs.add(column);
        ColumnModel cm = new ColumnModel(configs);
        return cm;
    }
   
   
}
