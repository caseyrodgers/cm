package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.model.ProgramData;
import hotmath.gwt.shared.client.model.ProgramSegmentData;

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

    public TrendingDataWindowChartBar2(CmAsyncRequest callback) {
        super("/gwt-resources/gxt/chart/open-flash-chart.swf", callback);
        setVisible(false);
    }

    protected void setBarModelData(String title, ProgramData programData) {
        ContentPanel cp = new ContentPanel();
        cp.setHeading(title);
        
        cp.setFrame(true);
        cp.setSize(400, 400);
        cp.setLayout(new FitLayout());

        setBorders(true);
        setChartModel(getBarChartData(title, programData.getSegments()));
        setVisible(true);
    }

    private ChartModel getBarChartData(String title, List<ProgramSegmentData> segmentData) {
        ChartModel cm = new ChartModel(title, "font-size: 14px; font-family: Verdana; text-align: center;");
        cm.setBackgroundColour("#fffff5");
        Legend lg = new Legend(Position.RIGHT, true);
        lg.setPadding(10);
        cm.setLegend(lg);
        
        int max=0;
        XAxis xa = new XAxis();  
        for (int i=0,t=segmentData.size();i<t;i++) {
            ProgramSegmentData psd = segmentData.get(i);
            Label l = new Label("Quiz " + (i+1), 45);  
            l.setSize(10);
            xa.addLabels(l);
            
            if(psd.getCountCompleted() > max)
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

        chart.setTooltip("#val#");
        for(int i=0,t=segmentData.size();i<t;i++) {
            ProgramSegmentData td = segmentData.get(i);
            
            BarChart.Bar bar = new BarChart.Bar(td.getCountCompleted());
            bar.setColour(colors[i]);
            chart.addBars(bar);  
        }
        
        cm.addChartConfig(chart);
        return cm;
    }
}
