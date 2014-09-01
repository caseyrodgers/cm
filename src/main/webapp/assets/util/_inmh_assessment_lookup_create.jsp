<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<html>
<head>
<%
    StringBuffer results = new hotmath.testset.ha.InmhAssessmentLookupBuilder().buildLookupTable();
%>
</head>
<body>
  <h1>inmh_assessment_lookup table created</h1>
  
  <h2>Results</h2>
  <pre>
  <%=results%>
  </pre>
</body>
</html>
