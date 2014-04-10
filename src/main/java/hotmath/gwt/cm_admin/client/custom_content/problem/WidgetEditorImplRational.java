package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_core.client.model.WidgetDefModel;
import hotmath.gwt.cm_tools.client.ui.MyFieldLabel;
import hotmath.gwt.cm_tools.client.ui.MyValidators;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.TabPanel;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.form.TextField;

public class WidgetEditorImplRational extends
		WidgetEditorImpFractionWithAdvanced {
	TextField _decimal = new NumericalTextField(MyValidators.DECIMAL);

	private TabPanel _tabPanel;

	private FlowLayoutContainer _panDec;

	private FlowLayoutContainer _panFrac;

	public WidgetEditorImplRational(WidgetDefModel widgetDef) {
		super(widgetDef);
	}

	protected void buildUi() {
		super.buildUi();

		_decimal.setAllowBlank(false);
		super.setFormatValue(_widgetDef.getFormat());

		_fields.clear();

		_panDec = new FlowLayoutContainer();
		_panDec.getElement().setAttribute("style",
				"background: #DFE8F6;padding: 5px;");
		_panDec.add(new HTML("<br/>"));
		_panDec.add(new MyFieldLabel(_decimal, "Decimal", 80, 60));
		_tabPanel = new TabPanel();
		_tabPanel.getElement().setAttribute("style", "background: transparent");
		_tabPanel.add(_panDec, "Decimal");

		_panFrac = new FlowLayoutContainer();
		_panFrac.getElement().setAttribute("style",
				"background: #DFE8F6;padding: 5px;");
		_panFrac.add(new MyFieldLabel(_numerator, "Numerator", 80, 60));
		_panFrac.add(new MyFieldLabel(_denominator, "Denominator", 80, 60));
		_tabPanel.add(_panFrac, "Fraction");
		_fields.add(_tabPanel);

		_fields.add(super.createAdvanced());


		_tabPanel.addSelectionHandler(new SelectionHandler<Widget>() {
			@Override
			public void onSelection(SelectionEvent<Widget> event) {
			}
		});
	}

	
	@Override
	public void setupValue() {
		String p[] = (_widgetDef.getValue() != null ? _widgetDef.getValue()
				: "").split("/");
		if (p.length == 2) {
			_tabPanel.setActiveWidget(_panFrac);
		} else {
			_tabPanel.setActiveWidget(_panDec);
			_decimal.setValue(p[0]);
		}
	}
	
	
	@Override
	public String getWidgetJson() {
		if (isDecimal()) {
			WidgetDefModel wd = createWidgetDefModel();
			wd.setValue(_decimal.getCurrentValue());
			return wd.getJson();
		} else {
			return super.getWidgetJson();
		}
	}

	@Override
	protected WidgetDefModel createWidgetDefModel() {
		WidgetDefModel wd = super.createWidgetDefModel();
		wd.setType("number_rational");
		wd.setAnsFormat("lowest_term");
		wd.setAllowMixed(false);

		String format = _format.getCurrentValue() != null ? _format
				.getCurrentValue() : null;
		if (format != null) {
			if (!format.startsWith("measure_")) {
				format = "measure_" + format;
			}
		}
		wd.setFormat(format);
		return wd;
	}

	private boolean isDecimal() {
		return _tabPanel.getActiveWidget() == _panDec;
	}

	@Override
	public String checkValid() {
		if (isDecimal()) {
			if (!_decimal.validate()) {
				return "Not a valid decimal value";
			}
		} else {
			String mesg = super.checkValid();
			if(mesg != null && mesg.indexOf("Rational") == -1) {
				return mesg;
			}
			int n = Integer.parseInt(_numerator.getCurrentValue());
			if(n == 0) {
				return "Use the Decimal tab for integer values.";
			}
			int d = Integer.parseInt(_denominator.getCurrentValue());
			if(d == 0) {
				return "Division by zero is undefined";
			}
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "Enter an integer, decimal number, or fraction (use \"/\"). If a fraction is correct but not in lowest terms, students are prompted to reduce. Answers must be exact.";
	}

	@Override
	public String getValueLabel() {
		return "Correct Value (decimal or fraction)";
	}
}

class Rational {

	private int num, denom;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getDenom() {
		return denom;
	}

	public void setDenom(int denom) {
		this.denom = denom;
	}

	public Rational(double d) {
		String s = String.valueOf(d);
		int digitsDec = s.length() - 1 - s.indexOf('.');

		int denom = 1;
		for (int i = 0; i < digitsDec; i++) {
			d *= 10;
			denom *= 10;
		}
		int num = (int) Math.round(d);

		this.num = num;
		this.denom = denom;
	}

	public Rational(int num, int denom) {
		this.num = num;
		this.denom = denom;
	}

	public String toString() {
		return String.valueOf(num) + "/" + String.valueOf(denom);
	}

	public static void main(String[] args) {
		System.out.println(new Rational(123.456));
	}
}