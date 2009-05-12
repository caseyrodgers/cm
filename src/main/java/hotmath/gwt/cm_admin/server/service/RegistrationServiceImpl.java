package hotmath.gwt.cm_admin.server.service;

import java.util.ArrayList;
import java.util.List;

import java.util.Random;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import hotmath.gwt.cm_admin.client.model.AccountInfoModel;
import hotmath.gwt.cm_admin.client.model.GroupModel;
import hotmath.gwt.cm_admin.client.model.StudentActivityModel;
import hotmath.gwt.cm_admin.client.model.StudentModel;
import hotmath.gwt.cm_admin.client.model.StudyProgramModel;
import hotmath.gwt.cm_admin.client.model.SubjectModel;
import hotmath.gwt.cm_admin.client.service.RegistrationService;
import hotmath.gwt.cm_admin.server.model.CmAdminDao;


public class RegistrationServiceImpl extends RemoteServiceServlet implements RegistrationService {

	private static final long serialVersionUID = -1395419758214416432L;
	
	Random rand = new Random();

	public RegistrationServiceImpl() {
	}

	public List<StudyProgramModel> getProgramDefinitions() {
		
		List <StudyProgramModel> progList = new ArrayList<StudyProgramModel>();
		
		// TODO obtain from DB
		progList.add(new StudyProgramModel("Placement Test", "Placement", "Place student into a Subject Proficiency Program",
				0, 0, 0, 0));
		progList.add(new StudyProgramModel("Subject Proficiency", "Prof", "Catchup Math will provide subject specific quizzes, review, and practice",
				1, 0, 1, 0));
		progList.add(new StudyProgramModel("Subject & Chapter", "Chap", "Select subject and a specific chapter topic",
				1, 1, 1, 0));
		progList.add(new StudyProgramModel("Graduation Preparation", "Grad Prep", "Prepare students for success on high-stakes tests",
				0, 0, 0, 1));

		return progList;
	}

	public List<StudentModel> getSummariesForActiveStudents(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getSummariesForActiveStudents(adminUid);
	}
	
	public List<StudentModel> getSummariesForInactiveStudents(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getSummariesForInactiveStudents(adminUid);
	}
	
	public List<SubjectModel> getSubjectDefinitions() {
		CmAdminDao cma = new CmAdminDao();
		return cma.getSubjectDefinitions();
	}

	public List<StudentActivityModel> getStudentActivity(StudentModel sm) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getStudentActivity(sm.getName());
/*
		int r = rand.nextInt(3);

		List <StudentActivityModel> samList = new ArrayList<StudentActivityModel>();

		StudentActivityModel sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Quiz-2");
		sam.setUseDate("2009-04-12");
		String text = String.format("retake completed, %d out of 10 correct", 6 + r);
		sam.setResult(text);
		sam.setStart("2:47pm");
		sam.setStop("3:19pm");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Review practice");
		sam.setUseDate("2009-04-12");
		sam.setResult("3 out of 5 completed");
		sam.setStart("2:00pm");
		sam.setStop("2:45pm");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Quiz-2");
		sam.setUseDate("2009-04-09");
		text = String.format("retake completed, %d out of 10 correct", 3 + r);
		sam.setResult(text);
		sam.setStart("11:41pm");
		sam.setStop("1:30am");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Review practice");
		sam.setUseDate("2009-04-09");
		sam.setResult("3 out of 7 completed");
		sam.setStart("11:15pm");
		sam.setStop("11:40pm");
		samList.add(sam);
		
		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Quiz-1");
		sam.setUseDate("2009-04-05");
		sam.setResult("retake completed, 9 out of 10 correct");
		sam.setStart("10:30am");
		sam.setStop("10:49pm");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Review practice");
		sam.setUseDate("2009-04-05");
		sam.setResult("2 out of 2 completed");
		sam.setStart("11:45am");
		sam.setStop("10:40pm");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Review practice 1");
		sam.setUseDate("2009-04-04");
		sam.setResult("completed");
		sam.setStart("2:37pm");
		sam.setStop("3:19pm");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Quiz-1");
		sam.setUseDate("2009-04-04");
		sam.setResult("retake started");
		sam.setStart("2:56pm");
		sam.setStop("3:05pm");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Quiz-1");
		sam.setUseDate("2009-04-03");
		sam.setResult("completed, 3 out of 10 correct");
		sam.setStart("2:55pm");
		sam.setStop("3:13pm");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Review practice");
		sam.setUseDate("2009-04-03");
		sam.setResult("completed, 3 out of 7 completed");
		sam.setStart("2:30pm");
		sam.setStop("2:55pm");
		samList.add(sam);

		sam = new StudentActivityModel();
		sam.setProgramDescr("Pre-Alg Chap 2");
		sam.setActivity("Quiz 1");
		sam.setUseDate("2009-04-03");
		sam.setResult("started");
		sam.setStart("10:23am");
		sam.setStop("");
		samList.add(sam);

		return samList;
*/
	}
	
	public List<GroupModel> getActiveGroups(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getActiveGroups(adminUid);
	}

	public AccountInfoModel getAccountInfo(Integer adminUid) {
		CmAdminDao cma = new CmAdminDao();
		return cma.getAccountInfo(adminUid);
	}

	public StudentModel addUser(StudentModel sm) {
		CmAdminDao cma = new CmAdminDao();
		return cma.addStudent(sm);
	}

	public StudentModel deactivateUser(StudentModel sm) {
		CmAdminDao cma = new CmAdminDao();
		return cma.deactivateUser(sm);
	}

	public StudentModel updateUser(StudentModel sm) {
		CmAdminDao cma = new CmAdminDao();
		return cma.updateStudent(sm);
	}
}
