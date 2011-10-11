package hotmath.gwt.cm_rpc.client.model;

import hotmath.cm.program.CmProgramFlow;
import hotmath.gwt.cm_admin.server.model.CmStudentDao;
import hotmath.gwt.cm_rpc.client.rpc.Response;

@SuppressWarnings("serial")
public class CmProgram implements Response {
    
    CmProgramInfo cmProgInfo = new CmProgramInfo();
    int id;
    int adminId;
    int passPercent;
    String testConfigJson;
    int customProgId;
    int customQuizId;

    int userProgId;
    
    public CmProgram() {/* empty */}
    
    public CmProgram(CmProgramInfo cmProgInfo, int passPercent, String testConfigJson, int customProgId, int customQuizId) {
        this.cmProgInfo = cmProgInfo;
        this.passPercent = passPercent;
        this.testConfigJson = testConfigJson;
        this.customProgId = customProgId;
        this.customQuizId = customQuizId;
    }
    
    public StudentActiveInfo getActiveInfo(int userId) throws Exception {
        return CmStudentDao.getInstance().loadActiveInfo(userId);
    }

    public CmProgramInfo getCmProgInfo() {
		return cmProgInfo;
	}

	public void setCmProgInfo(CmProgramInfo cmProgInfo) {
		this.cmProgInfo = cmProgInfo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}

	public int getPassPercent() {
		return passPercent;
	}

	public void setPassPercent(int passPercent) {
		this.passPercent = passPercent;
	}

	public String getTestConfigJson() {
		return testConfigJson;
	}

	public void setTestConfigJson(String testConfigJson) {
		this.testConfigJson = testConfigJson;
	}

	public int getCustomProgId() {
		return customProgId;
	}

	public void setCustomProgId(int customProgId) {
		this.customProgId = customProgId;
	}

	public int getCustomQuizId() {
		return customQuizId;
	}

	public void setCustomQuizId(int customQuizId) {
		this.customQuizId = customQuizId;
	}

	public int getUserProgId() {
        return userProgId;
    }

    public void setUserProgId(int userProgId) {
        this.userProgId = userProgId;
    }

    @Override
    public String toString() {
        return "CmProgram [passPercent=" + passPercent + ", testConfigJson=" + testConfigJson + ", customProgId="
                + customProgId + ", customQuizId=" + customQuizId + ", " + cmProgInfo.toString() + "]";
    }
}
