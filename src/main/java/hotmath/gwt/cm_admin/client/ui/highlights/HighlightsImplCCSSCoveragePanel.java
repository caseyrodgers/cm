package hotmath.gwt.cm_admin.client.ui.highlights;

import hotmath.gwt.cm_admin.client.ui.StudentListDialog;
import hotmath.gwt.cm_admin.client.ui.StudentListWithCCSSDetailDialog;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.CmLogger;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CCSSDetail;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSDetailAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent;
import com.sencha.gxt.widget.core.client.event.CellDoubleClickEvent.CellDoubleClickHandler;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;

public class HighlightsImplCCSSCoveragePanel extends HighlightsImplDetailsPanelBase {
    public HighlightsImplCCSSCoveragePanel(HighlightsImplBase base) {
        super(base);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.CCSS_COVERAGE;
    }

    @Override
    protected ColumnModel<HighlightReportData> getColumns() {
    	List<ColumnConfig<HighlightReportData, ?>> cols = new ArrayList<ColumnConfig<HighlightReportData, ?>>();

    	ColumnConfig<HighlightReportData, ?> column = new ColumnConfig<HighlightReportData, String>(_gridProps.name(), 200, "CCSS Name");
    	column.setSortable(false);
    	cols.add(column);

        column = new ColumnConfig<HighlightReportData, Integer>(_gridProps.studentCount(), 100, "# Students");
    	column.setSortable(false);
    	cols.add(column);

    	return new ColumnModel<HighlightReportData>(cols);

    }

    @Override
    protected CellDoubleClickHandler getDoubleClickHandler() {
    	return new CellDoubleClickHandler() {
            @Override
            public void onCellClick(CellDoubleClickEvent event) {
                if (_grid.getSelectionModel().getSelectedItems().size() > 0) {
                    CmLogger.debug("click handler: CCSS Coverage Details");
                    showCCSSCoverageDetails();
                }
            }
        };
    }

    private void showCCSSCoverageDetails() {

        CmBusyManager.setBusy(true);
        final HighlightReportData item = _grid.getSelectionModel().getSelectedItem();

        new RetryAction<CCSSDetail>() {
            public void oncapture(CCSSDetail detail) {
                try {
                	StringBuilder sb = new StringBuilder("<div style='padding-top:10px; padding-bottom:10px; margin-left:10px; margin-right:10px; font-weight:500'>");
                	sb.append(detail.getSummary());
                	sb.append("</div>");
                	int height = (sb.length() / 40) * 12;
                    StudentListWithCCSSDetailDialog dialog = new StudentListWithCCSSDetailDialog(item.getName(), height);
                    dialog.loadStudents(item.getUidList());
                    dialog.addCCSSDetail(sb.toString());
                } finally {
                    CmBusyManager.setBusy(false);
                }
            }

            @Override
            public void attempt() {
                CmServiceAsync s = CmShared.getCmService();

                CCSSDetailAction action = new CCSSDetailAction(item.getName());
                setAction(action);
                s.execute(action, this);
            }
        }.register();
    }

    @Override
    protected String getGridToolTip() {
        return "Double click for details.";
    }

    @Override
    public String[] getReportColumns() {
        return new String[] {"CCSS Name", "# Students"};
    }
    
    @Override
    public String[][] getReportValues() {
        CmList<HighlightReportData> hd = getHighLightData();
        String[][] vals = new String[hd.size()][2];
        for(int i=0;i<hd.size();i++) {
            vals[i][0] = hd.get(i).getName();
            vals[i][1] = hd.get(i).getDbCount() + "";
        }
        return vals;
    }

}
