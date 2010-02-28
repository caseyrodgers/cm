package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_tools.client.model.BaseModel;
import hotmath.gwt.shared.client.rpc.Response;


public class NetTestModel extends BaseModel implements Response {
    public NetTestModel() {}
    
    public NetTestModel(String testName, String testResults) {
        set("name", testName);
        set("results", testResults);
        set("number", -1);
        set("size", (long)-1);
        set("time", (long)-1);
    }
    
    public String getName() {
        return get("name");
    }
    
    public String getResults() {
        return get("results");
    }
    
    public Long  getTime() {
        return get("time");
    }
    
    public void setTime(Long time) {
        set("time", time);
    }
    
    public Long getSize() {
        return get("size");
    }
    
    public void setSize(Long size) {
        set("size", size);
    }    
    
    public Integer getNumber() {
        return get("number");
    }
    
    public void setNumber(Integer num) {
        set("number", num);
    }
}
