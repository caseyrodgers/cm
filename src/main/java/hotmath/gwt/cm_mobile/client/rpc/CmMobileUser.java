package hotmath.gwt.cm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class CmMobileUser implements Response {
    String name;
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CmMobileUser() {}

    @Override
    public String toString() {
        return "CmMobileUser [name=" + name + "]";
    }

}
