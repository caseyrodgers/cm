<%@ page import="hotmath.flusher.HotmathFlusher" %>
<%@ page import="java.sql.Connection, java.util.List" %>
<%@ page import="hotmath.gwt.cm_admin.server.model.CmProgramListingDao" %>
<%@ page import="hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson" %>
<%@ page import="hotmath.gwt.shared.client.CmProgram" %>
<%@ page import="hotmath.util.HMConnectionPool" %>

<%
try {
	GetLessonInfo gli = new GetLessonInfo();
	gli.testGetAlg1ProfLessons();
	gli.testGetAlg2ProfLessons();
	gli.testGetGeomProfLessons();
	gli.testGetEssentialsProfLessons();
	gli.testGetPreAlgProfLessons();
	gli.testGetBasicMathProfLessons();
} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
%>
<%!
class GetLessonInfo {
Connection conn;

GetLessonInfo() {
}
public void testGetAlg1ProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG1_PROF.getDefId(), 1, null, 6);
listLessons(lessons, "Alg1 Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG1_PROF.getDefId(), 2, null, 6);
listLessons(lessons, "Alg1 Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG1_PROF.getDefId(), 3, null, 6);
listLessons(lessons, "Alg1 Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG1_PROF.getDefId(), 4, null, 6);
listLessons(lessons, "Alg1 Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG1_PROF.getDefId(), 5, null, 6);
listLessons(lessons, "Alg1 Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG1_PROF.getDefId(), 6, null, 6);
listLessons(lessons, "Alg1 Prof [6]");
}


public void testGetAlg2ProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG2_PROF.getDefId(), 1, null, 6);
listLessons(lessons, "Alg2 Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG2_PROF.getDefId(), 2, null, 6);
listLessons(lessons, "Alg2 Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG2_PROF.getDefId(), 3, null, 6);
listLessons(lessons, "Alg2 Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG2_PROF.getDefId(), 4, null, 6);
listLessons(lessons, "Alg2 Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG2_PROF.getDefId(), 5, null, 6);
listLessons(lessons, "Alg2 Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ALG2_PROF.getDefId(), 6, null, 6);
listLessons(lessons, "Alg2 Prof [6]");
}

public void testGetGeomProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(CmProgram.GEOM_PROF.getDefId(), 1, null, 6);
listLessons(lessons, "Geom Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.GEOM_PROF.getDefId(), 2, null, 6);
listLessons(lessons, "Geom Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.GEOM_PROF.getDefId(), 3, null, 6);
listLessons(lessons, "Geom Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.GEOM_PROF.getDefId(), 4, null, 6);
listLessons(lessons, "Geom Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.GEOM_PROF.getDefId(), 5, null, 6);
listLessons(lessons, "Geom Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.GEOM_PROF.getDefId(), 6, null, 6);
listLessons(lessons, "Geom Prof [6]");
}

public void testGetEssentialsProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ESSENTIALS.getDefId(), 1, null, 6);
listLessons(lessons, "Essentials Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ESSENTIALS.getDefId(), 2, null, 6);
listLessons(lessons, "Essentials Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ESSENTIALS.getDefId(), 3, null, 6);
listLessons(lessons, "Essentials Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ESSENTIALS.getDefId(), 4, null, 6);
listLessons(lessons, "Essentials Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ESSENTIALS.getDefId(), 5, null, 6);
listLessons(lessons, "Essentials Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.ESSENTIALS.getDefId(), 6, null, 6);
listLessons(lessons, "Essentials Prof [6]");
}

public void testGetPreAlgProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(CmProgram.PREALG_PROF.getDefId(), 1, null, 6);
listLessons(lessons, "PreAlg Prof [1]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.PREALG_PROF.getDefId(), 2, null, 6);
listLessons(lessons, "PreAlg Prof [2]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.PREALG_PROF.getDefId(), 3, null, 6);
listLessons(lessons, "PreAlg Prof [3]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.PREALG_PROF.getDefId(), 4, null, 6);
listLessons(lessons, "PreAlg Prof [4]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.PREALG_PROF.getDefId(), 5, null, 6);
listLessons(lessons, "PreAlg Prof [5]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.PREALG_PROF.getDefId(), 6, null, 6);
listLessons(lessons, "PreAlg Prof [6]");
}

public void testGetBasicMathProfLessons() throws Exception {
List<ProgramLesson> lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 1, null, 6);
listLessons(lessons, "College Basic Math [1]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 2, null, 6);
listLessons(lessons, "College Basic Math [2]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 3, null, 6);
listLessons(lessons, "College Basic Math [3]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 4, null, 6);
listLessons(lessons, "College Basic Math [4]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 5, null, 6);
listLessons(lessons, "College Basic Math [5]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 6, null, 6);
listLessons(lessons, "College Basic Math [6]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 7, null, 6);
listLessons(lessons, "College Basic Math [7]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 8, null, 6);
listLessons(lessons, "College Basic Math [8]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 9, null, 6);
listLessons(lessons, "College Basic Math [9]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 10, null, 6);
listLessons(lessons, "College Basic Math [10]");
lessons = new CmProgramListingDao().getLessonsFor(CmProgram.BASICMATH.getDefId(), 11, null, 6);
listLessons(lessons, "College Basic Math [11]");
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
