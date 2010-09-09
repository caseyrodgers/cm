package hotmath.gwt.cm_rpc.client;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class ClientInfo implements Response {
	
	private static final long serialVersionUID = 8801661196897352987L;

	private int userId;

	private UserType userType;
	
	private String userAgent;
	
	private String platform;
	
	private String ipAddress;
	
	public ClientInfo() {
	}

	public enum UserType {STUDENT, ADMIN}

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
	};
	
	
	

}
