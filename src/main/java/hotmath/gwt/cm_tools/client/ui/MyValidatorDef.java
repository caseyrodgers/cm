package hotmath.gwt.cm_tools.client.ui;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorError;
import com.sencha.gxt.widget.core.client.form.Validator;
import com.sencha.gxt.widget.core.client.form.error.DefaultEditorError;


public class MyValidatorDef implements Validator<String> {

    private MyValidators type;
    
    public MyValidatorDef(MyValidators type){
      this.type = type;
    }

    @Override
    public List<EditorError> validate(Editor editor, String value) {
        List<EditorError> errors = null;
        if(value != null && !value.matches(type.regex)){
            errors = new ArrayList<EditorError>();
            errors.add(new DefaultEditorError(editor, value + " isn't a valid " + type.name, value));
        }
        return errors;
    }

  }