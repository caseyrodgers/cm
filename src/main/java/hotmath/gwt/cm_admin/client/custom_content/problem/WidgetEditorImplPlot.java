package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class WidgetEditorImplPlot extends WidgetEditorImplDefault implements WidgetEditor {


	TextField _xValue = new TextField();
	TextField _yValue = new TextField();
	TextField _xMin = new TextField();
	TextField _xMax = new TextField();
	TextField _yMin = new TextField();
	TextField _yMax = new TextField();
	TextField _xInc = new TextField();
	TextField _yInc = new TextField();
	
	public WidgetEditorImplPlot(WidgetDefModel widgetDef) {
		super(widgetDef);
		
		String val = widgetDef.getValue();
		if(val == null) {
			setupFields(null);
		}
		else {
			String p[] = val.split("\\|");
			PlotInfo plotInfo=null;
			if(p.length == 8) {
				int x = Integer.parseInt(p[0]);
				int y = Integer.parseInt(p[1]);
				int xMin = Integer.parseInt(p[2]);
				int yMin = Integer.parseInt(p[3]);
				int xMax = Integer.parseInt(p[4]);
				int yMax = Integer.parseInt(p[5]);
				int xInc = Integer.parseInt(p[6]);
				int yInc = Integer.parseInt(p[7]);
				plotInfo = new PlotInfo(x,y, xMin,yMin, xMax,yMax,xInc,yInc);
			}
			setupFields(plotInfo);
		}
	}
	
	private PlotInfo getPlotInfo() {
		try {
			int x = Integer.parseInt(_xValue.getCurrentValue());
			int y = Integer.parseInt(_yValue.getCurrentValue());
			int xMin = Integer.parseInt(_xMin.getCurrentValue());
			int yMin = Integer.parseInt(_yMin.getCurrentValue());
			int xMax = Integer.parseInt(_xMax.getCurrentValue());
			int yMax = Integer.parseInt(_yMax.getCurrentValue());
			int xInc = Integer.parseInt(_xInc.getCurrentValue());
			int yInc = Integer.parseInt(_yInc.getCurrentValue());
			return new PlotInfo(x,y,xMin, yMin, xMax,yMax,xInc,yInc);
		}
		catch(Exception e) {
			Log.error("Invalid plot values", e);
		}
		return null;
	}

	@Override
	public String getWidgetJson() {
		
		PlotInfo pi = getPlotInfo();
				
		String pv =  pi.x + "|" + 
		             pi.y + "|" +
		            pi.xMin + "|" +
		            pi.yMin + "|" +
		            pi.xMax + "|" +
		            pi.yMax + "|" +
		            pi.xInc + "|" +
		            pi.yInc + "|";
		            
        getWidgetDef().setValue(pv);
        return getWidgetDef().getJson();
	}


	private void setupFields(PlotInfo plot) {
		try {
			if(plot == null) {
				_xMax.setValue(10 + "");
				_yMax.setValue(10 + "");
				_xMin.setValue(-10 + "");
				_yMin.setValue(-10 + "");
				_xInc.setValue(1 + "");
				_yInc.setValue(1 + "");
			}
			else {
				_xValue.setValue(plot.x + "");
				_yValue.setValue(plot.y + "");
				_xMin.setValue(plot.xMin + "");
				_yMin.setValue(plot.yMin + "");
				_xMax.setValue(plot.xMax + "");
				_yMax.setValue(plot.yMax + "");
				_xInc.setValue(plot.xInc + "" + "");
				_yInc.setValue(plot.yInc + "");
			}
		}
		catch(Exception e) {
			Log.error("Error setting plot max", e);
		}
	}

	@Override
	public String getDescription() {
		return "Plot a point on the coordinate plane.";
	}

	@Override
	public String checkValid() {
		PlotInfo pi = getPlotInfo();
		if(pi != null) {
			if(pi.xMin < 0 && pi.yMin < 0) {
				return null;
			}
		}
		
		return "All values are numerical and must be specified.  (xMin < 0 and yMin < 0)";
	}

	
	@Override
	protected String getWidgetType() {
		return "widget_plot";
	}


	@Override
	protected void buildUi() {
		_fields.add(new MyFieldLabel(_xValue, "X Value",  80, 40));
		_fields.add(new MyFieldLabel(_yValue, "Y Value",  80, 40));
		
		
		DisclosurePanel advanced = new DisclosurePanel("Advanced Options");
		VerticalLayoutContainer adFields = new VerticalLayoutContainer();
		advanced.setContent(adFields);
		adFields.add(new MyFieldLabel(_xMin, "X Min",  80, 40));
		adFields.add(new MyFieldLabel(_yMin, "Y Min",  80, 40));
		adFields.add(new MyFieldLabel(_xMax, "X Max",  80, 40));
		adFields.add(new MyFieldLabel(_yMax, "Y Max",  80, 40));
		adFields.add(new MyFieldLabel(_xInc, "X Increment",  80, 40));
		adFields.add(new MyFieldLabel(_yInc, "Y Increment",  80, 40));
		
		adFields.add(new TextButton("Default Values", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				setupFields(null);
			}
		}));
		
		
		_fields.add(advanced);
	}
	
	
	class PlotInfo {
		int x, xMin, xMax, y, yMin, yMax;
		int xInc, yInc;
		
		public PlotInfo(int x, int y, int xMin, int yMin, int xMax, int yMax, int xInc, int yInc) {
			this.x = x;
			this.y = y;
			this.xMin = xMin;
			this.yMin = yMin;
			this.xMax = xMax;
			this.yMax = yMax;
			this.xInc = xInc;
			this.yInc = yInc;
		}
	}

}
