package hotmath.gwt.shared.client.util;

import java.util.List;

import com.google.gwt.editor.client.Editor;
import com.sencha.gxt.widget.core.client.form.Validator;


/** Provides simple method to apply validation for GXT forms
 * 
 * @author casey
 *
 */
public class ValdationTypeValidator implements Validator {
        private ValidationType type;
        
        public ValdationTypeValidator(ValidationType type){
          this.type = type;
        }

        @Override
        public List validate(Editor editor, Object value) {
//            String res = null;
//            if(!value.matches(type.regex)){
//              res = value + "isn't a valid " + type.name;
//            }
//            return res;
            return  null;
        }

}
