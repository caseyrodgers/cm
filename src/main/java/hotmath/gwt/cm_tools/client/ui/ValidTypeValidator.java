package hotmath.gwt.cm_tools.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;



public class ValidTypeValidator implements Validator<String> {

	private ValidType type;

	public ValidTypeValidator(ValidType type){
		this.type = type;
	}

    @Override
    public List<EditorError> validate(Editor<String> editor, String value) {
        List<EditorError> errors = new ArrayList<EditorError>();
		if ( ! value.matches(type.getRegex())){
			errors.add(new DefaultEditorError(editor, value + " isn't a valid " + type.getName(), value));
		}
		return errors;
	}

}