<%@ page import="hotmath.util.*" %>
<%@ page import="hotmath.util.sql.SqlUtilities" %>
<%@ page import="hotmath.cm.util.CmMultiLinePropertyReader" %>
<%@ page import="hotmath.cm.server.model.CmPurchaseOrder" %>
<%@ page import="hotmath.cm.server.model.CmPurchaseOrderDao" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.Statement" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.List" %>

<%
	CmPurchaseOrderDao dao = CmPurchaseOrderDao.getInstance();

	String errMsg = null;

	int count = 0;

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");

	String today = sdf.format(new java.util.Date());

	String header =
		String.format("%-30s\t%-10s\t%-25s\t%-30s\t%-10s\t%-8s\t%-5s\t%-7s\t%-7s\t%-7s\t%10s\t%-10s\n\n", 
				"School Name", "login", "Contact Name", "Contact email", "Rep", "#Student", "#Year", "Success", "Last4CC", "TransID", "Total", "Submitted");

	StringBuilder sb = new StringBuilder();

	try {
		List<CmPurchaseOrder> list = dao.getAll();

		count = list.size();
		for (int i=0; i<list.size(); i++) {

			CmPurchaseOrder po = list.get(i);
			java.util.Date orderDate = po.getOrderDate();
			String orderDateStr = sdf.format(orderDate);
			String loginName = (po.getSchool().getLoginName().length() > 10)?
					po.getSchool().getLoginName().substring(0,10):po.getSchool().getLoginName();
			String transId = (po.getPayment().getTransactionIdCC() == null) ? "NONE" : po.getPayment().getTransactionIdCC();

			sb.append(
			String.format("%-30s\t%-10s\t%-25s\t%-30s\t%-10s\t%8s\t%5s\t%7s\t%7s\t%7s\t%10.2f\t%10s\n", 
					po.getSchool().getName(), loginName, po.getContact().getName(), po.getContact().getEmail(), po.getSalesZone(), 
					po.getLicense().getNumStudents(), po.getLicense().getNumYears(), po.getPayment().isSuccess()?"Yes":"No",
					po.getPayment().getLastFourCC(), transId, po.getTotal(), orderDateStr));
		}
	}
	catch (Exception e){
		errMsg = e.getMessage();
	}
%>

<html>
  <body>
	<h2>
	  CM Purchase Order Report
	</h2>
	<pre>
 Date: <%= today %>

	<%
	if (errMsg != null) {
	%>
 Error message: <%= errMsg %>
	<%
	}
	%>
	<%
	if (count == 0) { %>
 No Purchase Orders Found
	<%
	} else { %>
<%=header.toString() %>
<%=sb.toString() %>	
	<%
	} %>
	
	</pre>
  </body>
</html>