package hotmath.cm.login.service.lcom;

import hotmath.gwt.cm.server.CmDbTestCase;

public class LcomManager_Test extends CmDbTestCase {
	
	public LcomManager_Test(String name) throws Exception {
		super(name);
	}
	
	public void testLoginTeacher() throws Exception {
		String teacherId = "teacher_id_" + System.currentTimeMillis();
		LcomTeacherSignup teacher = new LcomTeacherSignup("first_last", teacherId, "test@test.com", "district", "12345", "Course Name", "course_id");
		String urlFirst = LcomManager.loginTeacher(teacher);
		
		String urlSecond = LcomManager.loginTeacher(teacher);
		
		assertTrue(urlSecond.equals(urlFirst));
	}

}
