<%@page import="java.sql.*"%>
<%@page import="hotmath.util.sql.SqlUtilities" %>
<%
  String url = "jdbc:mysql://hotmath-live.cpcll61ssmu3.us-west-1.rds.amazonaws.com/";
  String dbName = "zabbix";
  String driver = "com.mysql.jdbc.Driver";
  String userName = "hotmath";
  String password = "geometry";
  Connection conn = null;
  ResultSet rs = null;
  int numDisabledItems=0;
  try {
      // Load the driver
      Class.forName(driver);
      // Get a connection
      conn = DriverManager.getConnection(url + dbName, userName, password);
      rs = conn.createStatement().executeQuery("select count(*) from items where status = 3");
      rs.first();
      numDisabledItems = rs.getInt(1);
  }
  finally {
      SqlUtilities.releaseResources(rs, null, conn);
  }
%>
<body>
<%= numDisabledItems %>
</body>
</html>
