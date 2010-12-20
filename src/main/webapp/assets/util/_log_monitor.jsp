<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcher"%>
<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcherListenerImplLogListener"%>
<%@page import="hotmath.gwt.cm_rpc.server.rpc.ActionDispatcherListener"%>
<%@page import="hotmath.cm.util.LogMonitor"%>
<%@page import="hotmath.cm.util.ActionInfo"%><html>
<head>
<title>CM Action Dispatcher Log Monitor</title>
<%@ page import="sb.util.*,hotmath.inmh.*,java.util.*,hotmath.solution.*,hotmath.*,hotmath.assessment.*,hotmath.util.*, hotmath.util.sql.*,java.sql.*" %>
<%
   
   String flush=request.getParameter("flush");
   String r = request.getParameter("refresh");
   int refresh = (r != null)?Integer.parseInt(r):10;
   response.setHeader("Refresh", Integer.toString(refresh));
   
   int totalCount=0;
   int totalTime=0;
   long lastUseMills=0;
   String timeStamp = new java.util.Date().toString();

   if(flush != null)
       ActionDispatcherListenerImplLogListener.getInstance().clearActionInfo();

   Map<String,ActionInfo> logInfo = ActionDispatcherListenerImplLogListener.getInstance().getActionInfo();
%>
</head>
<body>
  <h1>CM Action Dispatcher </h1>

  <h1>Action Use: <%= timeStamp %></h1>
  <table>
    <tr>
       <th>Action</th>
       <th>Count</th>
       <th>Total</th>
       <th>Max</th>
    </tr>
     <%
        for(String key: logInfo.keySet()) {
            ActionInfo ai = logInfo.get(key);
            %>
            <tr>
               <td><a href='#' onclick='showArgs("<%= key %>")'><%= ai.getName() %></a> </td>
				<td><%= ai.getCount() %> </td>
				<td><%= ai.getTotal() %> </td>
				<td><%= ai.getMax() %> </td>               
				<td><%= ai.getLastUseString() %> </td>
            </tr>
           <%
           totalCount += ai.getCount();
           totalTime += ai.getTotal();
           if(ai.getLastUse() != null && ai.getLastUse().getTime() > lastUseMills)
               lastUseMills = ai.getLastUse().getTime();
        }
      %>
  </table>
  <h2>Total count: <%= totalCount %>  Total time: <%= totalTime %></h2>
  <script>
      function showArgs(name) {
    	  
      }
  </script>
</body>
