package hotmath.gwt.cm_tools.client.ui;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class PassPercent extends BaseModelData {

    private static final long serialVersionUID = 6852777405039991570L;

    public PassPercent(String percent) {
        set("pass-percent", percent);
    }

    public String getPassPercent() {
        return get("pass-percent");
    }
    
    
}

