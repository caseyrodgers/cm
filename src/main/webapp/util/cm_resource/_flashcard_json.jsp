<%@page import="hotmath.gwt.shared.client.ui.resource.CmInmhStandardResources"%>
<%@page import="hotmath.gwt.cm_tools.client.model.FlashcardModel"%>
<%@page import="org.json.JSONException,org.json.JSONStringer"%>
<%@page import="java.lang.String"%>
<html>
<%@ page isErrorPage="false" %>
<%
	String json = "";
    String exceptionMsg = null;
	JSONStringer jsonStr = new JSONStringer();
	try {
    	jsonStr.array();
    	for (FlashcardModel model : CmInmhStandardResources.getEnglishFlashcardList()) {
    		jsonStr.object().key("lang").value(model.getLanguage());
    		jsonStr.key("category").value(model.getCategory());
    		jsonStr.key("description").value(model.getDescription());
    		jsonStr.key("location").value(model.getLocation()).endObject();
    	}
    	for (FlashcardModel model : CmInmhStandardResources.getSpanishFlashcardList()) {
    		jsonStr.object().key("lang").value(model.getLanguage());
    		jsonStr.key("category").value(model.getCategory());
    		jsonStr.key("description").value(model.getDescription());
    		jsonStr.key("location").value(model.getLocation()).endObject();
    	}
    	jsonStr.endArray();
    	json = jsonStr.toString().replaceAll("\\{", "<br/>\\{");
	}
    catch (Exception e) {
    	exceptionMsg = "Exception: " + e.getMessage();
    }
%>
<head>
</head>
<body>
<h1>CM Flashcard Resource JSON</h1>
<br/>
<br/>
<%= json %>
<%if (exceptionMsg != null) { %>
  <%= exceptionMsg %>
<%}%>  
</body>
</html>