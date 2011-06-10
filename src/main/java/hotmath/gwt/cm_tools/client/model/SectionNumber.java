package hotmath.gwt.cm_tools.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;


public class SectionNumber extends BaseModelData {

	private static final long serialVersionUID = 5784605077473777063L;

	public SectionNumber(String number) {
        set("section-number", number);
    }

    public String getSectionNumber() {
        return get("section-number");
    }
    
    
}

