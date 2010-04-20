package hotmath.gwt.cm_mobile.client.rpc;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class CmMobileUser implements Response {
    String name;
    int userId;


    public CmMobileUser() {}

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
