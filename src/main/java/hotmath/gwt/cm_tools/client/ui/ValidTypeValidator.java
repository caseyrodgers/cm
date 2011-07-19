package hotmath.gwt.cm_tools.client.ui;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

public class ValidTypeValidator implements Validator {

	private ValidType type;

	public ValidTypeValidator(ValidType type){
		this.type = type;
	}

	@Override
	public String validate(Field<?> field, String value) {

		String result = null;

		if ( ! value.matches(type.getRegex())){
			result = value + " isn't a valid " + type.getName();
		}
		return result;
	}

}