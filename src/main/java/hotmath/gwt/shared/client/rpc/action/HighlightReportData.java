package hotmath.gwt.shared.client.rpc.action;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class HighlightReportData implements Response {
    
    int uid;
    String data;
    String name;
    
    public HighlightReportData(){}
    public HighlightReportData(Integer uid, String name, String data) {
        this.uid = uid;
        this.name = name;
        this.data = data;
    }
    public String getData() {
        return data;
    }
    public void setData(String data) {
        this.data = data;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getUid() {
        return uid;
    }
    public void setUid(int uid) {
        this.uid = uid;
    }
    
}
