<%@ page import="hotmath.util.*" %>
<%@ page import="hotmath.gwt.cm_rpc.server.rpc.ContextListener" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%
    String dbPoolStatus = HMConnectionPool.getInstance().getStatus();
    boolean dbPoolOK = HMConnectionPool.getInstance().getIsOK();
    java.util.Date startDate = ContextListener.getStartDate();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss z");
    String start = sdf.format(startDate);
    
    long lFree = Runtime.getRuntime().freeMemory();
    long lTotal = Runtime.getRuntime().totalMemory() ;
    long lUsed = lTotal - lFree;

    String memory = "f=" +lFree + ":t=" + lTotal + ":u=" + lUsed;
%>

<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher"%><html>
  <body>
    <h1>
      System Check
    </h1>
    <pre>
    CM Started: <%= start %>

    <%= dbPoolStatus + " memory: " + memory%> 
    <%= hotmath.system.SystemCheck.getThreadPoolInfo(8010) %>
    </pre>
  </body>
</html>
