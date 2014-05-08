package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.TextField;

public class WidgetEditorImplPlot extends WidgetEditorImplDefault implements
		WidgetEditor {

	TextField _xValue = new DecimalTextField();
	TextField _yValue = new DecimalTextField();
	TextField _xMin = new DecimalTextField();
	TextField _xMax = new DecimalTextField();
	TextField _yMin = new DecimalTextField();
	TextField _yMax = new DecimalTextField();
	TextField _xInc = new DecimalTextField();
	TextField _yInc = new DecimalTextField();

	public WidgetEditorImplPlot(WidgetDefModel widgetDef) {
		super(widgetDef);
		setupFields(null);
	}

	@Override
	public void setupValue() {
		String val = _widgetDef.getValue();
		if (val == null) {
			setupFields(null);
		} else {
			String p[] = val.split("\\|");
			PlotInfo plotInfo = null;
			if (p.length == 8) {
				double x = Double.parseDouble(p[0]);
				double y = Double.parseDouble(p[1]);
				double xMin = Double.parseDouble(p[2]);
				double yMin = Double.parseDouble(p[3]);
				double xMax = Double.parseDouble(p[4]);
				double yMax = Double.parseDouble(p[5]);
				double xInc = Double.parseDouble(p[6]);
				double yInc = Double.parseDouble(p[7]);
				plotInfo = new PlotInfo(x, y, xMin, yMin, xMax, yMax, xInc,
						yInc);
			}
			setupFields(plotInfo);
		}
	}

	private PlotInfo getPlotInfo() {
		try {
			double x = Double.parseDouble(_xValue.getCurrentValue());
			double y = Double.parseDouble(_yValue.getCurrentValue());
			double xMin = Double.parseDouble(_xMin.getCurrentValue());
			double yMin = Double.parseDouble(_yMin.getCurrentValue());
			double xMax = Double.parseDouble(_xMax.getCurrentValue());
			double yMax = Double.parseDouble(_yMax.getCurrentValue());
			double xInc = Double.parseDouble(_xInc.getCurrentValue());
			double yInc = Double.parseDouble(_yInc.getCurrentValue());
			return new PlotInfo(x, y, xMin, yMin, xMax, yMax, xInc, yInc);
		} catch (Exception e) {
			Log.error("Invalid plot values", e);
		}
		return null;
	}

	@Override
	public String getWidgetJson() {

		PlotInfo pi = getPlotInfo();

		String pv = pi.x + "|" + pi.y + "|" + pi.xMin + "|" + pi.yMin + "|"
				+ pi.xMax + "|" + pi.yMax + "|" + pi.xInc + "|" + pi.yInc + "|";

		getWidgetDef().setValue(pv);
		return getWidgetDef().getJson();
	}

	private void setupFields(PlotInfo plot) {
		try {
			if (plot == null) {
				_xMax.setValue(10 + "");
				_yMax.setValue(10 + "");
				_xMin.setValue(-10 + "");
				_yMin.setValue(-10 + "");
				_xInc.setValue(1 + "");
				_yInc.setValue(1 + "");
			} else {
				_xValue.setValue(plot.x + "");
				_yValue.setValue(plot.y + "");
				_xMin.setValue(plot.xMin + "");
				_yMin.setValue(plot.yMin + "");
				_xMax.setValue(plot.xMax + "");
				_yMax.setValue(plot.yMax + "");
				_xInc.setValue(plot.xInc + "" + "");
				_yInc.setValue(plot.yInc + "");
			}
		} catch (Exception e) {
			Log.error("Error setting plot max", e);
		}
	}

	@Override
	public String getDescription() {
		return "Plot a point on the coordinate plane.";
	}

	@Override
	public String checkValid() {

		if (!_xValue.validate() || !_yValue.validate() || !_xMin.validate()
				|| !_xMax.validate() || !_yMin.validate() || !_yMax.validate()
				|| !_xInc.validate() || !_yInc.validate()) {
			return "Invalid";
		}

		PlotInfo pi = getPlotInfo();
		if (pi != null) {
			if (pi.xMin < 0 && pi.yMin < 0) {
				return null;
			}
		}

		return "All values are numerical and must be specified.";
	}

	@Override
	protected String getWidgetType() {
		return "widget_plot";
	}

	@Override
	public String getValueLabel() {
		return null;
	}

	@Override
	protected void buildUi() {

		_xValue.setAllowBlank(false);
		_yValue.setAllowBlank(false);

		_fields.add(new MyFieldLabel(_xValue, "X Value", 80, 60));
		_fields.add(new MyFieldLabel(_yValue, "Y Value", 80, 60));
		_fields.add(new HTML(
				"<div style='padding: 3px;font-style: italic'>Test to make sure the point is plottable.</div>"));

		DisclosurePanel advanced = new DisclosurePanel("Advanced Options");
		VerticalLayoutContainer adFields = new VerticalLayoutContainer();
		advanced.setContent(adFields);
		adFields.add(new HTML(
				"<h2 style='color: black;font: 14pt;margin-top: 6px;margin-bottom: 5px;'>Initial Window:</h2>"));
		adFields.add(new MyFieldLabel(_xMin, "X Min", 80, 60));
		adFields.add(new MyFieldLabel(_yMin, "Y Min", 80, 60));
		adFields.add(new MyFieldLabel(_xMax, "X Max", 80, 60));
		adFields.add(new MyFieldLabel(_yMax, "Y Max", 80, 60));
		adFields.add(new MyFieldLabel(_xInc, "X Increment", 80, 60));
		adFields.add(new MyFieldLabel(_yInc, "Y Increment", 80, 60));

		adFields.add(new TextButton("Default Values", new SelectHandler() {
			@Override
			public void onSelect(SelectEvent event) {
				setupFields(null);
			}
		}));

		_fields.add(advanced);
	}

	class PlotInfo {
		double x, xMin, xMax, y, yMin, yMax;
		double xInc, yInc;

		public PlotInfo(double x, double y, double xMin, double yMin,
				double xMax, double yMax, double xInc, double yInc) {
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

	@Override
	public String getWidgetTypeLabel() {
		return "Plot";
	}

	@Override
	public String getWidgetValueLabel() {
		String val = _widgetDef.getValue();
		String p[] = val.split("\\|");
		double x = Double.parseDouble(p[0]);
		double y = Double.parseDouble(p[1]);

		return '(' + x + ", " + y + ')';
	}

}
