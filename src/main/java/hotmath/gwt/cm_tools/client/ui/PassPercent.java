package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_rpc.client.rpc.Response;


public class PassPercent implements Response {
    private String percent;

    public PassPercent(String percent) {
        this.percent = percent;
    }

    public String getPercent() {
        return percent;
    }

    public void setPercent(String percent) {
        this.percent = percent;
    }
    
}

