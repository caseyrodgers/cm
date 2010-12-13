package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ClientInfo implements Response {
	
	private static final long serialVersionUID = 8801661196897352987L;

	private int userId;

	private UserType userType;
	
	private String userAgent;
	
	private String platform;
	
	private String ipAddress;
	
	private String actionId;
	
	public ClientInfo() {
	}

	public enum UserType {STUDENT, ADMIN, UNKNOWN}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getActionId() {
		return actionId;
	}

	public void setActionId(String actionId) {
		this.actionId = actionId;
	}

    @Override
    public String toString() {
        return "ClientInfo [userId=" + userId + ", userType=" + userType + ", userAgent=" + userAgent + ", platform="
                + platform + ", ipAddress=" + ipAddress + ", actionId=" + actionId + "]";
    };
}
