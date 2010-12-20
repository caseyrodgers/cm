<%@ page import="hotmath.util.*" %>
<%@ page import="hotmath.cm.server.listener.ContextListener" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    String dbPoolStatus = HMConnectionPool.getInstance().getStatus();
    boolean dbPoolOK = HMConnectionPool.getInstance().getIsOK();
    
    java.util.Date startDate = ContextListener.getStartDate();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    String start = sdf.format(startDate);
    
%>
<html>
  <body>
    <h1>
      System Check
    </h1>
    <pre>
    <%= dbPoolStatus %>
    
    CM Started: <%= start %>
    </pre>
  </body>
</html>

