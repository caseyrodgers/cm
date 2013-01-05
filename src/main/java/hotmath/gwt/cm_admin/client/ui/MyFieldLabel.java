package hotmath.gwt.cm_admin.client.ui;

import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.widget.core.client.Component;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.form.FieldLabel;

public class MyFieldLabel extends FieldLabel {

    public MyFieldLabel(Widget comp, String label, int labelLength) {
        this(comp, label, labelLength, 0);
    }
    
    public MyFieldLabel(Widget comp, String label, int labelLength, int fieldLength) {
        super(comp, label);
        
        if(comp instanceof Component && fieldLength > 0) {
            if(!(comp instanceof TextButton)) {
                ((Component) comp).setWidth(fieldLength);
            }
        }
        if(labelLength > 0) {
            setLabelWidth(labelLength);
        }
    }

}
