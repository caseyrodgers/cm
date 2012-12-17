package hotmath.gwt.shared.client.util;

import com.sencha.gxt.widget.core.client.info.DefaultInfoConfig;


public class CmInfoConfig extends DefaultInfoConfig {
    public CmInfoConfig(String t, String m) {
        super(t, m);
        setDisplay(6000);
        setWidth(221);
    }
}
