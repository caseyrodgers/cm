<%@page import="sb.util.SbUtilities"%>
<%@page import="hotmath.cm.util.CmPilotCreate"%>
<%
    String title = request.getParameter("fld_title");
    String name = request.getParameter("fld_first_name") + " " + request.getParameter("fld_last_name");
    String school = request.getParameter("fld_school");
    String zip = request.getParameter("fld_zip");
    String email = request.getParameter("fld_email").trim();
    email = (email != null) ? email.trim() : "";
    String institution = request.getParameter("institution");
    boolean isCollegePilot = (institution != null && institution.toLowerCase().indexOf("college") > -1);
    String phone = request.getParameter("fld_phone");
    String comments = request.getParameter("fld_comments");
    String phoneWhen = request.getParameter("fld_phone_when");
    String schoolPrefix = request.getParameter("fld_password_prefix");
    int studentCount = SbUtilities.getInt(request.getParameter("fld_student_count"));
    String additionalEmails = request.getParameter("fld_additional_emails");
    String motivation = request.getParameter("fld_motivation");

    String json = CmPilotCreate.addPilotRequest(title, name, school, zip, email, phone, comments, phoneWhen, schoolPrefix,
                                  true, studentCount, null, additionalEmails,motivation, isCollegePilot);
    out.write(json);
    out.flush();
%>
