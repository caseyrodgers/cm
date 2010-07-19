package hotmath.cm.login.service.lcom;

import hotmath.gwt.shared.client.util.CmException;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

/** Manage the process of logging in Learning.Com users
 * 
 * @author casey
 *
 */
public class LcomManager {
	
	static private LcomManager __instance;
	static public LcomManager getInstance() {
		if(__instance == null) {
			__instance = new LcomManager();
		}
		return __instance;
	}
	
	private LcomManager() {}
	
	public void loginStudent(String lastFirst, String studentUserId, String courseId, String teacherId) throws Exception  {
		LcomStudentSignup student = new LcomStudentSignup(lastFirst, studentUserId, courseId, teacherId);
		LcomDom dom = new LcomDom();
		Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
			/** get assigned teacher, throw exception if teacher not currently defined
			 * 
			 */
			
			LcomTeacherSignup teacher = dom.getTeacher(conn, teacherId);
			if(teacher == null) {
				throw new CmException("Check with your teacher, this account is not yet ready for you.");
			}
			
		}
		finally {
			SqlUtilities.releaseResources(null,null,conn);
		}
		
	}
	
	public void loginTeacher(String firstName, String lastName, String email, String district, String zip, String courseName,String courseId) {
		LcomTeacherSignup teacher = new LcomTeacherSignup(firstName, lastName, email, district, zip, courseName, courseId);
	}
}
