package hotmath.gwt.shared.client.util;

import com.extjs.gxt.ui.client.widget.InfoConfig;

public class CmInfoConfig extends InfoConfig {
    public CmInfoConfig(String t, String m) {
        super(t, m);
        display = 3000;
        width = 300;
    }
}
