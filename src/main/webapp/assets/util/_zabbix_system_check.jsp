<%@page import="java.sql.*"%>
<%
  String url = "jdbc:mysql://hotmath-live.cpcll61ssmu3.us-west-1.rds.amazonaws.com/";
  String dbName = "zabbix";
  String driver = "com.mysql.jdbc.Driver";
  String userName = "hotmath";
  String password = "geometry";
  Connection conn = null;
  int numDisabledItems=0;
  try {
      // Load the driver
      Class.forName(driver);
      // Get a connection
      conn = DriverManager.getConnection(url + dbName, userName, password);
      ResultSet rs = conn.createStatement().executeQuery("select count(*) from items where status = 3");
      rs.first();
      numDisabledItems = rs.getInt(0);
  }
  finally {
      conn.close();
  }
%>
<body>
<%= numDisabledItems %>
</body>
</html>
