
<%@page import="hotmath.SolutionManager"%>
<%@page import="hotmath.solution.Solution"%><%@page import="org.apache.log4j.Logger"%>
<%@page import="hotmath.solution.writer.TutorProperties"%>
<%@page import="hotmath.solution.writer.SolutionHTMLCreatorIimplVelocity"%>
<%@page import="hotmath.solution.SolutionParts"%>
<%
  Logger logger = Logger.getLogger(this.getClass());
  SolutionHTMLCreatorIimplVelocity creator = (SolutionHTMLCreatorIimplVelocity)request.getSession().getAttribute("creator");
  if(creator == null) {
   	creator = new SolutionHTMLCreatorIimplVelocity("template_tutor4", "/web/tutor/solution_fragment.html");
   	request.getSession().setAttribute("creator", creator);
  }
  TutorProperties tProps = new TutorProperties();

  String pid = "samples_1_1_SampleExercises_1-Algebra_1";
  Solution solution = SolutionManager.getSolution(pid, tProps.getTemplate(), false);
  int times=Integer.parseInt(request.getParameter("times"));
  for(int i=times;i>-1;i--) {
      String pstr = creator.processSolution(tProps, solution);
      String data = creator.processSolutionData(tProps, solution);
      
      if((i % 100) == 0)
      	logger.info("Testing: " + i);
    }
%>
Test Complete: <%= times %>