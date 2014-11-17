<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="com.cedarsoftware.util.io.JsonWriter"%>
<%@page import="hotmath.gwt.cm_rpc.client.model.Topic"%>
<%@page import="hotmath.gwt.cm_rpc_core.client.rpc.CmList"%>
<%@page import="hotmath.gwt.cm_rpc_core.server.rpc.ActionDispatcher"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction.SearchType"%>
<%@page import="hotmath.gwt.cm_rpc.client.rpc.SearchTopicAction"%>
<%
   String callback = request.getParameter("callback");
   String query = request.getParameter("query");
   
   SearchTopicAction action = new SearchTopicAction(SearchType.LESSON_LIKE,query);
   CmList<Topic> topics = ActionDispatcher.getInstance().execute(action);
   
   JSONObject root = new JSONObject();
   root.put("totalCount", Integer.toString(topics.size()));
   JSONArray jaTopics = new JSONArray();
   for(Topic t: topics) {
       JSONObject jo = new JSONObject();
   	   jo.put("file", t.getFile());
   	   jo.put("name", t.getName());
   	   jo.put("excerpt", t.getExcerpt()	);
   	jaTopics.put(jo);
   }
   root.put("topics", jaTopics);
   String json = root.toString();
%>
<%= callback %>(<%= json %>);