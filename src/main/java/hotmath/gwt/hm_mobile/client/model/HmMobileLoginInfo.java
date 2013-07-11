package hotmath.gwt.hm_mobile.client.model;


import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import com.allen_sauer.gwt.log.client.Log;

public class HmMobileLoginInfo implements Response {
    private String user;
    private String password;
    private String accountType;

    public HmMobileLoginInfo() {}
    
    public HmMobileLoginInfo(String tokenized) {
        String p[] = tokenized.split("\\|");
        if(p.length != 3) {
            Log.info("Could not parse loginInfo: " + tokenized);
        }
        else {
            this.user = p[0];
            this.password = p[1];
            this.accountType = p[2];
        }
    }
    public HmMobileLoginInfo(String user, String password, String accountType) {
        this.user = user;
        this.password = password;
        this.accountType = accountType;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Override
    public String toString() {
        return "HmMobileLoginInfo [user=" + user + ", password=" + password + ", accountType=" + accountType + "]";
    }
}
