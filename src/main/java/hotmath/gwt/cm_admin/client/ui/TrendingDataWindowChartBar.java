package hotmath.gwt.cm_admin.client.ui;

import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmServiceAsync;
import hotmath.gwt.cm_tools.client.CmBusyManager;
import hotmath.gwt.cm_tools.client.model.StudentModelI;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.data.CmAsyncRequestImplDefault;
import hotmath.gwt.shared.client.model.TrendingData;
import hotmath.gwt.shared.client.rpc.action.GetAdminTrendingDataDetailAction;
import hotmath.gwt.shared.client.util.CmAsyncCallback;

import java.util.List;

import com.extjs.gxt.charts.client.model.ChartModel;
import com.extjs.gxt.charts.client.model.Legend;
import com.extjs.gxt.charts.client.model.Legend.Position;
import com.extjs.gxt.charts.client.model.axis.Label;
import com.extjs.gxt.charts.client.model.axis.XAxis;
import com.extjs.gxt.charts.client.model.axis.YAxis;
import com.extjs.gxt.charts.client.model.charts.BarChart;
import com.extjs.gxt.charts.client.model.charts.BarChart.BarStyle;

public class TrendingDataWindowChartBar extends TrendingDataWindowChart {

    List<TrendingData> trendingData;
    
    public TrendingDataWindowChartBar() {
        _callback = new CmAsyncRequestImplDefault() {
            @Override
            public void requestComplete(String requestData) {
                showUsersWhoHaveBeenAssignedLesson(new Integer(requestData));
            }
        };
    }
    
    private void showUsersWhoHaveBeenAssignedLesson(int lessonNumber) {
        CmBusyManager.setBusy(true);

        final String lessonName = trendingData.get(lessonNumber).getLessonName();
        CmServiceAsync service = CmShared.getCmService();
        GetAdminTrendingDataDetailAction action = new GetAdminTrendingDataDetailAction(StudentGridPanel.instance._pageAction, lessonName);
        service.execute(action,
                new CmAsyncCallback<CmList<StudentModelI>>() {
                    public void onSuccess(CmList<StudentModelI> students) {
                        new TrendingDataStudentListDialog("Students assigned lesson '" +lessonName + "'",students);
                        CmBusyManager.setBusy(false);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        CmBusyManager.setBusy(false);
                        super.onFailure(caught);
                    }
                });
    }

    protected void setModelData(String title, List<TrendingData> data) {
        this.trendingData = data;
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
        chart.setEnableEvents(true);
        chart.setTooltip("#val# (click to see student list)");
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
