package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelExt;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.ProgramSegmentData;
import hotmath.gwt.shared.client.rpc.RetryAction;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction;

import java.util.List;

import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.axis.Label;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.BarStyle;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class TrendingDataWindowChartBar2 extends TrendingDataWindowChart {

    ProgramData programData;

    public TrendingDataWindowChartBar2() {
        _callback = new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                showUsersWhoHaveGoneThroughProgramSegment(new Integer(requestData)+1);
            }
        };
    }

    private void showUsersWhoHaveGoneThroughProgramSegment(final int segment) {
        new RetryAction<CmList<StudentModelExt>>() {
            @Override
            public void attempt() {
                CmBusyManager.setBusy(true);
                GetAdminTrendingDataDetailAction action = new GetAdminTrendingDataDetailAction(StudentGridPanel.instance._cmAdminMdl.getId(),
                        StudentGridPanel.instance._pageAction, programData.getTestDefId(), segment);
                setAction(action);
                CmShared.getCmService().execute(action,this);
            }
            public void oncapture(CmList<StudentModelExt> students) {
                CmBusyManager.setBusy(false);                
                new TrendingDataStudentListDialog("Students in " + programData.getProgramName() + " Segment " + segment,students);
            }
        }.register();    
    }

    protected void setBarModelData(String title, ProgramData programData) {
        this.programData = programData;
        ContentPanel cp = new ContentPanel();
        cp.setHeading(title);

        cp.setFrame(true);
        cp.setSize(400, 400);
        cp.setLayout(new FitLayout());

        setBorders(true);
        setChartModel(getBarChartData(title, programData.getSegments()));
        addChartListener(listener);
        enableEvents(true);

        setVisible(true);
    }

    public ProgramData getProgramData() {
        return programData;
    }

    public void setProgramData(ProgramData programData) {
        this.programData = programData;
    }

    private ChartModel getBarChartData(String title, List<ProgramSegmentData> segmentData) {
        ChartModel cm = new ChartModel(title, "font-size: 14px; font-family: Verdana; text-align: center;");
        cm.setBackgroundColour("#fffff5");
        Legend lg = new Legend(Position.RIGHT, true);
        lg.setPadding(10);
        cm.setLegend(lg);

        int max = 0;
        XAxis xa = new XAxis();
        for (int i = 0, t = segmentData.size(); i < t; i++) {
            ProgramSegmentData psd = segmentData.get(i);
            Label l = new Label("Section-" + (i + 1), 45);
            l.setSize(10);
            xa.addLabels(l);

            if (psd.getCountCompleted() > max)
                max = psd.getCountCompleted();
        }
        cm.setXAxis(xa);

        YAxis yz = new YAxis();

        /** move to next even multiple depending on count */
        max = getChartMaxRange(max);
        yz.setSteps(getChartSteps(max));
        yz.setMax(max);
        cm.setYAxis(yz);

        BarChart chart = new BarChart(BarStyle.GLASS);
        chart.setEnableEvents(true);

        chart.setTooltip("#val# (click to see student list)");
        for (int i = 0, t = segmentData.size(); i < t; i++) {
            ProgramSegmentData td = segmentData.get(i);

            BarChart.Bar bar = new BarChart.Bar(td.getCountCompleted());
            bar.setColour(colors[i]);
            chart.addBars(bar);
        }

        cm.addChartConfig(chart);
        return cm;
    }
}
