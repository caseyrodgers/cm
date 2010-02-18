package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.model.BaseModel;
import hotmath.gwt.shared.client.rpc.Response;


public class NetTestModel extends BaseModel implements Response {
    public NetTestModel() {}
    
    public NetTestModel(String testName, String testResults) {
        set("name", testName);
        set("results", testResults);
    }
    
    public String getName() {
        return get("name");
    }
    
    public String getResults() {
        return get("results");
    }
    
    public long getTime() {
        return get("time");
    }
    
    public void setTime(long time) {
        set("time", time);
    }
    
    public long getSize() {
        return get("size");
    }
    
    public void setSize(long time) {
        set("size", time);
    }    
    
    public int getNumber() {
        return get("number");
    }
    
    public void setNumber(int num) {
        set("number", num);
    }
}