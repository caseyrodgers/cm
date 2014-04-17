<%@ page import="java.util.List" %>
<%@ page import="hotmath.gwt.cm_admin.server.model.CmProgramListingDao" %>
<%@ page import="hotmath.gwt.cm_rpc.client.model.program_listing.ProgramChapter" %>
<%@ page import="hotmath.gwt.cm_rpc.client.model.program_listing.ProgramLesson" %>
<%@ page import="hotmath.gwt.cm_rpc.client.model.program_listing.ProgramListing" %>
<%@ page import="hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSection" %>
<%@ page import="hotmath.gwt.cm_rpc.client.model.program_listing.ProgramSubject" %>
<%@ page import="hotmath.gwt.cm_rpc.client.model.program_listing.ProgramType" %>

<%
try {
	GetLessonInfo gli = new GetLessonInfo();
	gli.getBasicMathProfLessons();
} catch (Exception e) {
	e.printStackTrace();
}
%>
<%!
class GetLessonInfo {

	CmProgramListingDao dao = new CmProgramListingDao();

    GetLessonInfo() {
    }

    public void getBasicMathProfLessons() throws Exception {
    	ProgramListing progListing = dao.getProgramListing();

    	List<ProgramType> typeList = progListing.getProgramTypes();

    	for (ProgramType type : typeList) {
    		if (type.getType().equalsIgnoreCase("PROF")) {
    			List<ProgramSubject> subjList = type.getProgramSubjects();
    			for (ProgramSubject subj : subjList) {
    				if (subj.getName().equalsIgnoreCase("BASICMATH")) {
    					List<ProgramChapter> chapList = subj.getChapters();
    					for (ProgramChapter chap : chapList){
    						List<ProgramSection> sectList = chap.getSections();
    						for (ProgramSection sect : sectList) {
        						System.out.println("Section: " + sect.getNumber());

        						List<ProgramLesson> lessonList = dao.getLessonsFor(subj.getTestDefId(),
        								sect.getNumber(), chap.getLabel(), 99);
        	
        						StringBuilder sb = new StringBuilder();
        						for (ProgramLesson lesson : lessonList) {
        							sb.append(lesson.getName()).append(", ");
        						}
        						String lessons = sb.toString();
        						int index = lessons.lastIndexOf(", ");
        						if (index > 0) {
            						lessons = lessons.substring(0, index);
            						System.out.println(lessons);
        						}
    						}
    					}
    				}
    			}
    		}
    	}
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
