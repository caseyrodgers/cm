<%@ page import="hotmath.flusher.HotmathFlusher" %>

<%
    HotmathFlusher.getInstance().flushAll();
    hotmath.SolutionManager.flushCache();
%>
<html>
  <body>
    <h1>
       All system caches flushed
    </h1>
  </body>
</html>
