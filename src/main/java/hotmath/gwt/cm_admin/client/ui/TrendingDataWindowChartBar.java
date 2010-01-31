package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.shared.client.model.TrendingData;

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

public class TrendingDataWindowChartBar extends TrendingDataWindowChart {

    public TrendingDataWindowChartBar() {
    }

    protected void setModelData(String title, List<TrendingData> data) {
        ContentPanel cp = new ContentPanel();
        cp.setHeading("Bar chart");
        cp.setFrame(true);
        cp.setSize(400, 400);
        cp.setLayout(new FitLayout());

        setBorders(true);
        setChartModel(getBarChartData(title, data));
        setVisible(true);
    }

    private ChartModel getBarChartData(String title, List<TrendingData> data) {
        ChartModel cm = new ChartModel(title, "font-size: 14px; font-family: Verdana; text-align: center;");
        cm.setBackgroundColour("#fffff5");
        Legend lg = new Legend(Position.RIGHT, true);
        lg.setPadding(10);
        cm.setLegend(lg);

        int max=0;
        XAxis xa = new XAxis();  
        for (TrendingData m : data) {  
          Label l = new Label(m.getLessonName(), 45);  
          l.setSize(10);
          xa.addLabels(l);
          
          if(m.getCountAssigned() > max)
              max = m.getCountAssigned();
        }
        cm.setXAxis(xa);
        
        YAxis yz = new YAxis();
        
        /** move to next height multiple of 100 */
        max = getChartMaxRange(max);
        yz.setSteps(getChartSteps(max));
        
        yz.setMax(max);
        cm.setYAxis(yz);
        
        BarChart chart = new BarChart(BarStyle.THREED);

        chart.setTooltip("#val#");
        for(int i=0,t=data.size();i<t;i++) {
            TrendingData td = data.get(i);
            
            BarChart.Bar bar = new BarChart.Bar(td.getCountAssigned());
            bar.setColour(colors[i]);
            chart.addBars(bar);  
        }
        
        cm.addChartConfig(chart);
        return cm;
    }
}
