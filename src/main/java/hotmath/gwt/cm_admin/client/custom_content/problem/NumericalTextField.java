package hotmath.gwt.cm_admin.client.custom_content.problem;

import hotmath.gwt.cm_tools.client.ui.MyValidatorDef;
import hotmath.gwt.cm_tools.client.ui.MyValidators;

import com.sencha.gxt.widget.core.client.form.TextField;

public class NumericalTextField extends TextField {
	
	public NumericalTextField() {
		super();
		
        addValidator(new MyValidatorDef(MyValidators.NUMERIC));
	}

}
