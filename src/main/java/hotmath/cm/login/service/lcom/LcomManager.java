package hotmath.cm.login.service.lcom;

import hotmath.cm.util.CmPilotCreate;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;
import hotmath.gwt.shared.client.model.CmPartner;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.testset.ha.HaUserFactory;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

/** Manage the process of logging in Learning.Com users
 * 
 * @author casey
 *
 */
public class LcomManager {

	/** Login the LCOM student:
	 * 
	 * - If the teacher has not been setup then show user message: check with your teacher, this account is not yet ready for you
	 * 
     * - If the class id is not already set up (but teacher is setup)
     *    -- Create new class as a CM Group
     *          
     * - If the teacher IS setup and class IS setup, but the student IS NOT already set up
     *     -- register new student and enroll her in Pre-Algebra Proficiency with default parameters assigned to the class group.
     *      
	 * @param student
	 * @throws Exception
	 */
	static public String loginStudent(LcomStudentSignup student) throws Exception  {
		LcomDom dom = new LcomDom();
		Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
			/** get assigned teacher, throw exception if teacher not currently defined
			 * 
			 */
			LcomTeacherSignup teacher = dom.getTeacher(conn, student.getCurrentCourseTeacherUserId());
			if(teacher == null) {
				throw new CmException("Check with your teacher, this account is not yet ready for you.");
			}
			
			/** Get this course, if not setup .. then create it.
			 * 
			 * PROBLEM: we only know the course id here, not the course name.
			 */
			LcomTeacherCourse course = dom.getTeacherCourse(conn,student.getStudentCourseId());
			if(course == null) {
				/** add new group based on this course, and assign this student to new group
				 * 
				 */
				
				/** create group based on the unique courseId
				 * 
				 */
				try {
					GroupInfoModel group = new CmAdminDao().getGroup(conn, teacher.getAdminId(), student.getStudentCourseId());
					if(group == null) {
						group = new GroupInfoModel();
						group.setGroupName(student.getStudentCourseId());
						group.setIsActive(true);
						new CmAdminDao().addGroup(conn, teacher.getAdminId(), group);
					}
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				dom.setupNewTeacherCourse(conn, teacher.getTeacherId(), student.getStudentCourseId(),"");
				course = dom.getTeacherCourse(conn,student.getStudentCourseId());
			}
			
			/** at this point, we know the teacher and the course
			 *
			 * We now need to register this student with the teacher's ADMIN 
			 * account and make sure it is in the proper group.
			 */
			LcomStudentSignup studentActive = dom.getStudent(conn, student.getStudentUserId());
			if(studentActive == null) {
				/** register as new student with ADMIN */
				String passwd = student.getStudentUserId();
				int uid = HaUserFactory.createUser(conn, teacher.getAdminId(), course.getCourseId(),student.getLastFirst(), passwd);
				student.setUserId(uid);
				dom.registerStudent(conn, student);
				studentActive = student;				
			}
			return "/loginService?type=STUDENT&partner=lcom&uid=" + studentActive.getUserId();
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		finally {
			SqlUtilities.releaseResources(null,null,conn);
		}
		
	}
	
	/** 
	 * -- If is the first time, we will set up the pilot based on the teacher guid
	 * and the teacher will be shown a welcome screen with some tips (explaining about the pilot, 
	 * telling them that they will get emailed tips, suggesting they read the Getting Started Guide, 
	 * and any special usage issues regarding use within Sky, etc.  
	 * 
	 * -- If not the first time, then they simply get logged in.  
	 * 
	 * -- If the 30 days has expired they will get a message saying to contact CM to request an extension or place an order.
	 * 
	 *  Returns URL used to login teacher, or throws Exception if error.
	 *  
	 * @param firstLast
	 * @param teacherId
	 * @param email
	 * @param district
	 * @param zip
	 * @param courseName
	 * @param courseId
	 */
	static public String loginTeacher(LcomTeacherSignup teacher) throws Exception {
		LcomDom dom = new LcomDom();
		Connection conn=null;
		try {
			conn = HMConnectionPool.getConnection();
			/** get assigned teacher, throw exception if teacher not currently defined
			 * 
			 */
			int teacherAid=0;
			LcomTeacherSignup teacherAccount = dom.getTeacher(conn, teacher.getTeacherId());
			if(teacherAccount == null) {
				teacherAid = setupNewTeacherAccount(conn, teacher);
			}
			else {
				teacherAid = teacherAccount.getAdminId();
			}
			return "/loginService?type=ADMIN&partner=lcom&uid=" + teacherAid;
		}
		finally {
			SqlUtilities.releaseResources(null,null,conn);
		}		
	}
	

	/** Setup a LCOM teacher for the first time
	 * 
	 * @param teacher
	 * @throws Exception
	 */
	static private Integer setupNewTeacherAccount(final Connection conn, LcomTeacherSignup teacher) throws Exception {
		int teacherAid = CmPilotCreate.addPilotRequest("LCOM Teacher: " + teacher.getTeacherId(), teacher.getFirstLast(), teacher.getSchoolName(), 
				teacher.getZip(), teacher.getEmail(), "", "LCOM Teacher signup", "", "lcom", false,CmPartner.LCOM);
		if(teacherAid == -1)
			throw new CmException("Could not setup LCOM Teacher account, please see server log");
		teacher.setAdminId(teacherAid);
		new LcomDom().registerTeacher(conn, teacher);
		
		return teacherAid;
	}
}
