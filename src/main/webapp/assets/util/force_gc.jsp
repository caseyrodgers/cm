<%
  Long start = System.currentTimeMillis();
  System.gc();
  Long elapsed = System.currentTimeMillis() - start;
%>
Garbage collected in <%= elapsed %> msecs !

