package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_tools.client.ui.MyValidatorDef;
import hotmath.gwt.cm_tools.client.ui.MyValidatorDef.Verifier;
import hotmath.gwt.cm_tools.client.ui.MyValidators;

import com.sencha.gxt.widget.core.client.form.TextField;

public class IntegerTextField extends TextField {
	
	public IntegerTextField(String tip) {
		this();
		setToolTip(tip);
	}
	public IntegerTextField() {
		this(MyValidators.INTEGER, new IntegerVerifier());
	}
	public IntegerTextField(MyValidators type, Verifier verifier) {
		super();
		addValidator(new MyValidatorDef(type, verifier));
	}

}
