package hotmath.gwt.cm_admin.client.ui;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CatchupMathStudent implements IsSerializable {
	
	private int uid;
	
	private int adminUid;
	
	private String name;
	
	private String passcode;
	
	private String email;
	
	private String currentProgram;
	
	private String lastUseTimestamp;
	
	private int solutionUsageCount;
	
	private int videoUsageCount;
	
	private int reviewUsageCount;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getAdminUid() {
		return adminUid;
	}

	public void setAdminUid(int adminUid) {
		this.adminUid = adminUid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasscode() {
		return passcode;
	}

	public void setPasscode(String passcode) {
		this.passcode = passcode;
	}

	public String getCurrentProgram() {
		return currentProgram;
	}

	public void setCurrentProgram(String currentProgram) {
		this.currentProgram = currentProgram;
	}

	public String getLastUseTimestamp() {
		return lastUseTimestamp;
	}

	public void setLastUseTimestamp(String lastUseTimestamp) {
		this.lastUseTimestamp = lastUseTimestamp;
	}

	public int getSolutionUsageCount() {
		return solutionUsageCount;
	}

	public void setSolutionUsageCount(int solutionUsageCount) {
		this.solutionUsageCount = solutionUsageCount;
	}

	public int getVideoUsageCount() {
		return videoUsageCount;
	}

	public void setVideoUsageCount(int videoUsageCount) {
		this.videoUsageCount = videoUsageCount;
	}

	public int getReviewUsageCount() {
		return reviewUsageCount;
	}

	public void setReviewUsageCount(int reviewUsageCount) {
		this.reviewUsageCount = reviewUsageCount;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
