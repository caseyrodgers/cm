package hotmath.gwt.cm_tools.client.data;



/** Catchup math login information
 * 
 * 
 * 
 * @author casey
 *
 */
public class HaLoginInfo {

    String key;
    String type;
    int    userId;
    String loginName;
    
    
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


    Boolean isConsumed;
    
    public HaLoginInfo() {
        /** empty */
    }
    
    public Boolean getIsConsumed() {
        return isConsumed;
    }

    public void setIsConsumed(Boolean isConsumed) {
        this.isConsumed = isConsumed;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    
    public String toString() {
       return  this.key + ", " + this.type + ", " + this.userId;
    }
}


