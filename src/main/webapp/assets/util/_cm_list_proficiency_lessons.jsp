<%@ page import="hotmath.flusher.HotmathFlusher" %>
<%@ page import="java.sql.Connection, java.util.List" %>
<%@ page import="hotmath.gwt.cm_admin.server.model.CmProgramListingDao" %>
<%@ page import="hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson" %>
<%@ page import="hotmath.gwt.shared.client.CmProgram" %>
<%@ page import="hotmath.util.HMConnectionPool" %>

<%
Connection conn = null;
try {
	conn = HMConnectionPool.getConnection();
	GetLessonInfo gli = new GetLessonInfo(conn);
	gli.testGetAlg1ProfLessons();
	gli.testGetAlg2ProfLessons();
	gli.testGetGeomProfLessons();
	gli.testGetEssentialsProfLessons();
	gli.testGetPreAlgProfLessons();
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
finally {
	try {
	conn.close();
	}
	catch (Exception e){}
}

class GetLessonInfo {
Connection conn;

GetLessonInfo(Connection conn) {
	this.conn = conn;
}
public void testGetAlg1ProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG1_PROF.getDefId(), 1, null, 6);
listLessons(lessons, "Alg1 Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG1_PROF.getDefId(), 2, null, 6);
listLessons(lessons, "Alg1 Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG1_PROF.getDefId(), 3, null, 6);
listLessons(lessons, "Alg1 Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG1_PROF.getDefId(), 4, null, 6);
listLessons(lessons, "Alg1 Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG1_PROF.getDefId(), 5, null, 6);
listLessons(lessons, "Alg1 Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG1_PROF.getDefId(), 6, null, 6);
listLessons(lessons, "Alg1 Prof [6]");
}


public void testGetAlg2ProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG2_PROF.getDefId(), 1, null, 6);
listLessons(lessons, "Alg2 Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG2_PROF.getDefId(), 2, null, 6);
listLessons(lessons, "Alg2 Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG2_PROF.getDefId(), 3, null, 6);
listLessons(lessons, "Alg2 Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG2_PROF.getDefId(), 4, null, 6);
listLessons(lessons, "Alg2 Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG2_PROF.getDefId(), 5, null, 6);
listLessons(lessons, "Alg2 Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ALG2_PROF.getDefId(), 6, null, 6);
listLessons(lessons, "Alg2 Prof [6]");
}

public void testGetGeomProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.GEOM_PROF.getDefId(), 1, null, 6);
listLessons(lessons, "Geom Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.GEOM_PROF.getDefId(), 2, null, 6);
listLessons(lessons, "Geom Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.GEOM_PROF.getDefId(), 3, null, 6);
listLessons(lessons, "Geom Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.GEOM_PROF.getDefId(), 4, null, 6);
listLessons(lessons, "Geom Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.GEOM_PROF.getDefId(), 5, null, 6);
listLessons(lessons, "Geom Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.GEOM_PROF.getDefId(), 6, null, 6);
listLessons(lessons, "Geom Prof [6]");
}

public void testGetEssentialsProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ESSENTIALS.getDefId(), 1, null, 6);
listLessons(lessons, "Essentials Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ESSENTIALS.getDefId(), 2, null, 6);
listLessons(lessons, "Essentials Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ESSENTIALS.getDefId(), 3, null, 6);
listLessons(lessons, "Essentials Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ESSENTIALS.getDefId(), 4, null, 6);
listLessons(lessons, "Essentials Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ESSENTIALS.getDefId(), 5, null, 6);
listLessons(lessons, "Essentials Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.ESSENTIALS.getDefId(), 6, null, 6);
listLessons(lessons, "Essentials Prof [6]");
}

public void testGetPreAlgProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.PREALG_PROF.getDefId(), 1, null, 6);
listLessons(lessons, "PreAlg Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.PREALG_PROF.getDefId(), 2, null, 6);
listLessons(lessons, "PreAlg Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.PREALG_PROF.getDefId(), 3, null, 6);
listLessons(lessons, "PreAlg Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.PREALG_PROF.getDefId(), 4, null, 6);
listLessons(lessons, "PreAlg Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.PREALG_PROF.getDefId(), 5, null, 6);
listLessons(lessons, "PreAlg Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(conn, CmProgram.PREALG_PROF.getDefId(), 6, null, 6);
listLessons(lessons, "PreAlg Prof [6]");
}

public void listLessons(List<ProgramLesson> list, String name) {
System.out.println("Lessons for: " + name);
int i = 0;
for (ProgramLesson lesson : list) {
	System.out.println("[" + i++ + "]: " + lesson.getName());
}
}
}

%>
<html>
  <body>
    <h1>
System Flushed
    </h1>
  </body>
</html>
