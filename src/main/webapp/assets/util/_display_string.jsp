<%@ page contentType="text/plain"%>
<%
  String str = request.getParameter("str");

  str = java.net.URLDecoder.decode(str);
%>

<%= str %>
