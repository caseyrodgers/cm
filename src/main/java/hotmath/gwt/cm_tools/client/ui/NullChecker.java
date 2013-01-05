package hotmath.gwt.cm_tools.client.ui;

import com.sencha.gxt.core.client.XTemplates.Formatter;



public class NullChecker implements Formatter<String> {

    @Override
    public String format(String data) {
        return data != null? data: "";
    }
    
}