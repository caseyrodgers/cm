package hotmath.testset.ha;

import hotmath.gwt.cm_tools.client.data.HaBasicUser;

import java.util.Date;

abstract public class HaBasicUserImpl implements HaBasicUser {
    
	Date expireDate;
	String accountType;
    String loginMessage;
    String password;
    String userName;
    String loginName;
    String email;


	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

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
	
    public String getLoginMessage() {
    	return loginMessage;
    }

    public void setLoginMessage(String msg) {
        this.loginMessage = msg;
    }
    
    abstract public Object getUserObject();
    abstract public UserType getUserType();
    
}
