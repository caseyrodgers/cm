package hotmath.gwt.cm_admin.client.ui.highlights;

import java.util.List;

import hotmath.gwt.cm_admin.client.ui.StudentGridPanel;
import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.ui.DateRangePanel;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageBarChart;
import hotmath.gwt.cm_tools.client.ui.ccss.CCSSCoverageUniqueBarChart;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.CCSSCoverageBar;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.CCSSCoverageChartDataAction;
import hotmath.gwt.shared.client.rpc.action.HighlightReportData;
import hotmath.gwt.shared.client.rpc.action.HighlightsGetReportAction;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;

/**
 * 
 * @author bob
 *
 */

public class HighlightsImplCCSSCoverageChartPanel extends Composite implements HighlightPanel {
    
    HighlightsImplBase base;

    private static final DateTimeFormat _dateFormat = DateTimeFormat.getFormat("yyyy-mm-dd"); 

    TabPanel _tabPanel = new TabPanel();

    public HighlightsImplCCSSCoverageChartPanel(HighlightsImplBase base) {
        this.base = base;
        initWidget(_tabPanel);
    }
    
    @Override
    public HighlightsGetReportAction.ReportType getReportType() {
        return HighlightsGetReportAction.ReportType.CCSS_COVERAGE_CHART;
    }

    @Override
    public void getDataFromServer(final CallbackOnComplete callbackOnComplete) {
        if(_chartWidget1 != null) {
            callbackOnComplete.isComplete();
            return;
        }
        
        new RetryAction<CmList<CCSSCoverageBar>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);

                CCSSCoverageChartDataAction action = new CCSSCoverageChartDataAction();
                action.setStudentGridPageAction(StudentGridPanel.instance.getPageAction());
                action.setFrom(DateRangePanel.getInstance().getFromDate());
                action.setTo(DateRangePanel.getInstance().getToDate());
                action.setType(CCSSCoverageChartDataAction.ReportType.MULTI_STUDENT_CUMULATIVE_CHART);
                
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(CmList<CCSSCoverageBar> allData) {
                CmBusyManager.setBusy(false);
                drawGui();
                if (allData != null && allData.size() > 0)
                    addCharts(allData);
                else
                	showNoData();
                setVisible(true);

                callbackOnComplete.isComplete();
            }
            
            @Override
            public void onFailure(Throwable error) {
                super.onFailure(error);
                CmBusyManager.setBusy(false);
                drawGui();
            	showNoData();
                
                callbackOnComplete.isComplete();
            }
        }.register();
    }

    @Override
    public String[] getReportColumns() {
        return null;
    }

    @Override
    public String[][] getReportValues() {
        return null;
    }

    private void drawGui() {
        _tabPanel.setAnimScroll(true);
        _tabPanel.setTabScroll(true);
        _tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {

			@Override
			public void onSelection(SelectionEvent<Widget> event) {
				_tabPanel.forceLayout();
			}
        	
        });
    }

    Widget _chartWidget1 = null;
    Widget _chartWidget2 = null;

    private void addCharts(List<CCSSCoverageBar> data) {
    	if (_chartWidget1 != null) {
    		_chartWidget1.setVisible(false);
    		_chartWidget1.removeFromParent();
    		_tabPanel.remove(_chartWidget1);
    	}
    	if (_chartWidget2 != null) {
    		_chartWidget2.setVisible(false);
    		_chartWidget2.removeFromParent();
    		_tabPanel.remove(_chartWidget2);
    	}

    	String title = createTitle(data);
    	
        CCSSCoverageBarChart _barChart = new CCSSCoverageBarChart(title, data);
        _chartWidget1 = _barChart.asWidget();
        _tabPanel.add(_chartWidget1, "Activity");

        CCSSCoverageUniqueBarChart _uniqueBarChart = new CCSSCoverageUniqueBarChart(title, data);
        _chartWidget2 = _uniqueBarChart.asWidget();
        _tabPanel.add(_chartWidget2, "Unique");

    }

    private String createTitle(List<CCSSCoverageBar> data) {
    	String title = "";
    	if (data != null && data.size() > 0) {
    		CCSSCoverageBar bar = data.get(0);
    		title = _dateFormat.format(bar.getBeginDate());
    		if (data.size() > 1) {
    			title += " to " + _dateFormat.format(data.get(data.size()-1).getEndDate());
    		}
    	}
    	return title;
    }

    private void showNoData() {
        //removeAll();
    	CenterLayoutContainer clContainer = new CenterLayoutContainer();
    	FramedPanel fp = new FramedPanel();
    	fp.setHeaderVisible(false);
    	fp.add(new HTML("<h2 style='color:red'>No data found</h2>"));
        clContainer.add(fp);
        _tabPanel.clear();
    	_tabPanel.add(clContainer);
    	_tabPanel.forceLayout();
    }


}
