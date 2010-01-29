package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.data.CmAsyncRequest;
import hotmath.gwt.shared.client.model.TrendingData;

import java.util.List;

import com.extjs.gxt.charts.client.Chart;
import com.extjs.gxt.charts.client.event.ChartEvent;
import com.extjs.gxt.charts.client.event.ChartListener;
import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.charts.PieChart;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class TrendingDataWindowChart extends Chart {

    private ChartListener listener = new ChartListener() {
        public void chartClick(ChartEvent ce) {
            if(_callback != null)
               _callback.requestComplete(ce.getValue().toString());
        }
    };
    
    CmAsyncRequest _callback;

    String colors[] = {"#ff0000", "#00aa00", "#0000ff", "#ff9900", "#ff00ff"};
    
    public TrendingDataWindowChart(CmAsyncRequest callback) {
        this("/gwt-resources/gxt/chart/open-flash-chart.swf",callback);
        setVisible(false);
    }
    
    public TrendingDataWindowChart(String path,CmAsyncRequest callback) {
        super(path);
        _callback = callback;
    }

    protected void setModelData(String title, List<TrendingData> data) {
        ContentPanel cp = new ContentPanel();
        cp.setHeading("Pie chart");
        cp.setFrame(true);
        cp.setSize(400, 400);
        cp.setLayout(new FitLayout());

        setBorders(true);
        setChartModel(getPieChartData(title, data));
        setVisible(true);
    }

    private ChartModel getPieChartData(String title, List<TrendingData> data) {
        ChartModel cm = new ChartModel(title, "font-size: 14px; font-family: Verdana; text-align: center;");
        cm.setBackgroundColour("#fffff5");
        Legend lg = new Legend(Position.RIGHT, true);
        lg.setPadding(10);
        cm.setLegend(lg);

        PieChart pie = new PieChart();
        pie.setAlpha(0.5f);
        pie.setNoLabels(true);
        
        
        pie.setTooltip("#label# #val#");
        pie.setColours(colors);
        
        for(int i=0,t=data.size();i<t;i++) {
            TrendingData td = data.get(i);
            pie.addSlices(new PieChart.Slice(td.getCountAssigned(), td.getLessonName(), td.getLessonName()));
        }
        pie.addChartListener(listener);

        cm.addChartConfig(pie);
        return cm;
    }
}
