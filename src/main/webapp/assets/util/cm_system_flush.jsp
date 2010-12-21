<%@ page import="hotmath.flusher.HotmathFlusher" %>

<%
    HotmathFlusher.getInstance().flushAll();
%>
<html>
  <body>
    <h1>
System Flushed
    </h1>
  </body>
</html>
