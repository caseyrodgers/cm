package hotmath.testset.ha;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;

import java.util.Date;

abstract public class HaBasicUserImpl implements HaBasicUser {
    
	Date expireDate;
	String accountType;

	public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    String password;
    String userName;

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public Date getExpireDate() {
    	return expireDate;
    }
    
    public void setExpireDate(Date expireDate) {
    	this.expireDate = expireDate;
    }

    public boolean isExpired() {
      return expireDate == null || expireDate.before(new Date());	
    }

    public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
    
    abstract public Object getUserObject();
    abstract public UserType getUserType();
    
}
