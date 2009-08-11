package hotmath.gwt.shared.client.util;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Validator;

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
        public String validate(Field<?> field, String value) {
          String res = null;
          if(!value.matches(type.regex)){
            res = value + "isn't a valid " + type.name;
          }
          return res;
        }

}
