package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_admin.client.ui.MyFieldLabel;
import hotmath.gwt.cm_core.client.model.WidgetDefModel;

public class WidgetEditorImplFraction extends WidgetEditorImplDefault {

	NumericalTextField _numerator;
	NumericalTextField _denominator;

	public WidgetEditorImplFraction(WidgetDefModel widgetDef) {
		super(widgetDef);
	}

	@Override
	protected void buildUi() {
		_numerator = new NumericalTextField();
		_denominator = new NumericalTextField();

		String value = getWidgetDef().getValue();
		String p[] = value != null ? value.split("/") : new String[1];

		_numerator.setValue(p[0]);
		_denominator.setValue(p.length > 1 ? p[1] : "");

		_fields.add(new MyFieldLabel(_numerator, "Numerator", 80, 60));
		_fields.add(new MyFieldLabel(_denominator, "Denominator", 80, 60));
	}

	@Override
	protected WidgetDefModel createWidgetDefModel() {
		WidgetDefModel wd = super.createWidgetDefModel();

		wd.setValue(_numerator.getCurrentValue() + "/"
				+ _denominator.getCurrentValue());
		return wd;
	}

	@Override
	public String getDescription() {
		return "Enter a numerator and a denominator. If the answer is correct but not in lowest terms, students are prompted to reduce. ";
	}

	@Override
	protected String getWidgetType() {
		return "number_simple_fraction";
	}

	@Override
	public String checkValid() {
		if (!_numerator.validate() || !_denominator.validate()) {
			return "Both numerator and denomiator must be integer values";
		}
		
		int n = Integer.parseInt(_numerator.getCurrentValue());
		int d = Integer.parseInt(_denominator.getCurrentValue());

		if(n == 0) {
			return "If the answer to the problem is 0, " 
					+ "it's probably better to use the Rational input type, which allows students to enter " 
			        + "fractions OR whole numbers.";
		}
		
		if(d == 0) {
			return "Division by zero is undefined.";
		}
		
		Fraction fraction = new Fraction(n, d);
		fraction.reduce();
		
		_numerator.setValue(fraction.numerator + "");
		_denominator.setValue(fraction.denominator + "");

		return null;
	}
}

class Fraction {
	
	public Fraction(int n, int d) {
		this.numerator = n;
		this.denominator = d;
	}
	int numerator;
	int denominator;

	public void reduce() {
		int n = numerator;
		int d = denominator;

		while (d != 0) {
			int t = d;
			d = n % d;
			n = t;
		}

		int gcd = n;

		numerator /= gcd;
		denominator /= gcd;
	}
}