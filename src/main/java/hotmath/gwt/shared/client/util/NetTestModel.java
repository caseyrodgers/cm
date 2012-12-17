package hotmath.gwt.shared.client.util;

import hotmath.gwt.cm_rpc.client.rpc.Response;


public class NetTestModel implements Response {
    private String name;
    private String results;
    private int number;
    private long size;
    private long time;

    public NetTestModel() {}
    
    public NetTestModel(String testName, String testResults) {
        this.name = testName;
        this.results = testResults;
        this.number = -1;
        this.size = (long)-1;
        this.time = (long)-1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
