package hotmath.gwt.cm_rpc_core.client.rpc;



/** Create a single user account using the Auto Registration system
 * 
 * @author casey
 *
 */
public class CreateAutoRegistrationAccountAction implements Action<RpcData> {
	private static final long serialVersionUID = 1489635908171332401L;

	Integer userId;
    String user;
    String password;
    String email;
    boolean isSelfPay;

    public CreateAutoRegistrationAccountAction() {
    }
    
    public CreateAutoRegistrationAccountAction(Integer userId, String user, String password) {
        this.userId = userId;
        this.user = user;
        this.password = password;
    }

    public CreateAutoRegistrationAccountAction(Integer userId, String user, String password, String email) {
        this.userId = userId;
        this.user = user;
        this.password = password;
        this.email = email;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isSelfPay() {
		return isSelfPay;
	}

	public void setSelfPay(boolean isSelfPay) {
		this.isSelfPay = isSelfPay;
	}
    
}
