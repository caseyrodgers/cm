package hotmath.cm.util;

import junit.framework.TestCase;

public class CmPilotCreate_Test extends TestCase {
	
	public CmPilotCreate_Test(String name) {
		super(name);
	}
	
	public void testCreateCollege() throws Exception {
	    CmPilotCreate.addPilotRequest("Test Title", "Test Name", "Test School", "99999","test@hotmath.com", "123-123-1234", "test", "", "cba", true, 100, null, null,"", true);
	}
	
	public void _testCreate() throws Exception {
		PilotCreatedInfo pilotCreate = CmPilotCreate.addPilotRequest("Test title", "Test name", "Test school", "12345", "test@hotmath.com", "123-123-1234", "test", "", "cba", 100);
		assertTrue(pilotCreate.getAdminId() > 0);
	}
}
