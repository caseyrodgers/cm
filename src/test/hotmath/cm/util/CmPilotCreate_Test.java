package hotmath.cm.util;

import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.AccountInfoModel;
import junit.framework.TestCase;

public class CmPilotCreate_Test extends TestCase {
	
	public CmPilotCreate_Test(String name) {
		super(name);
	}
	
	public void testCreateCollege() throws Exception {
	    CmPilotCreate.addPilotRequest("Test Title", "Test Name", "Test School", "99999","test@hotmath.com", "123-123-1234", "test", "", "cba", true, 100, null, null,"", true);
	}
	
	public void _testCreate() throws Exception {
		int adminId = CmPilotCreate.addPilotRequest("Test title", "Test name", "Test school", "12345", "test@hotmath.com", "123-123-1234", "test", "", "cba", 100);
		AccountInfoModel aim = CmAdminDao.getInstance().getAccountInfo(adminId);
		assert(aim.getMaxStudents() == 100);
		assert(aim.getAdminUserName().equals("Test name"));
	}
}
