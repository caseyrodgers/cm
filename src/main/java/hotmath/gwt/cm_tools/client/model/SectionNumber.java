package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;
    
public class SectionNumber implements Response  {
    int sectionNumber;
    
	public SectionNumber(int number) {
        sectionNumber = number;
    }

    public int getSectionNumber() {
        return sectionNumber;
    }
    
    public String getSectionLabel() {
        return Integer.toString(sectionNumber);
    }

    public void setSectionNumber(int sectionNumber) {
        this.sectionNumber = sectionNumber;
    }
}

