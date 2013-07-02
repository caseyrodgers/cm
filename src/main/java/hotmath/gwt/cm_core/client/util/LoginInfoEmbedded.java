package hotmath.gwt.cm_core.client.util;

/** hold data externalized json from LoginService
 * 
 * @author casey
 *
 */
public class LoginInfoEmbedded {

    private String securityKeyVal;
    private int uid;
    private String partner;
    private String startType;
    private String email;
    private boolean mobile;

    public LoginInfoEmbedded(String keyVal, int userId, String cmStartType, String partner, String email, boolean isMobile) {
        this.securityKeyVal = keyVal;
        this.uid = userId;
        this.startType = cmStartType;
        this.partner = partner;
        this.email = email;
        this.mobile = isMobile;
    }
    
    public boolean isMobile() {
        return mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSecurityKeyVal() {
        return securityKeyVal;
    }

    public void setSecurityKeyVal(String securityKeyVal) {
        this.securityKeyVal = securityKeyVal;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getStartType() {
        return startType;
    }

    public void setStartType(String startType) {
        this.startType = startType;
    }

}
