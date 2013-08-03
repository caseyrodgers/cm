package hotmath.gwt.hm_mobile.client.model;


import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.hm_mobile.client.persist.HmMobilePersistedPropertiesManager;

import java.util.Date;

import com.allen_sauer.gwt.log.client.Log;

public class HmMobileLoginInfo implements Response {
    private String user;
    private String password;
    private String accountType;
    private Date dateExpired;
    private boolean expired;
    private int solutionCount;

    public HmMobileLoginInfo() {}
    
    static public final String DEMO_STUDENT = "Demo Student";
    public HmMobileLoginInfo(String tokenized) {
        String p[] = tokenized.split("\\|");
        if(p.length != 4) {
            Log.info("Could not parse loginInfo: " + tokenized);
        }
        else {
            this.user = p[0];
            this.password = p[1];
            this.accountType = p[2];
            try {
                this.dateExpired = HmMobilePersistedPropertiesManager._expiredDateFormat.parse(p[3]);
            }
            catch(Exception e) {
                Log.error("Error setting date expired", e);
            }
        }
    }
    public HmMobileLoginInfo(String user, String password, String accountType, boolean isExpired, Date dateExpired, int solutionCount) {
        this.user = user;
        this.password = password;
        this.accountType = accountType;
        this.expired = isExpired;
        this.dateExpired = dateExpired;
        this.solutionCount = solutionCount;
    }
    
    public boolean isDemoAccount() {
        return this.user.equals(DEMO_STUDENT);
    }

    public int getSolutionCount() {
        return solutionCount;
    }
    public void setSolutionCount(int solutionCount) {
        this.solutionCount = solutionCount;
    }
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    public Date getDateExpired() {
        return dateExpired;
    }

    public void setDateExpired(Date dateExpired) {
        this.dateExpired = dateExpired;
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
        return "HmMobileLoginInfo [user=" + user + ", password=" + password + ", accountType=" + accountType + ", dateExpired=" + dateExpired + ", expired="
                + expired + ", solutionCount=" + solutionCount + "]";
    }

    public boolean isExpired() {
        return expired;
    }
}
