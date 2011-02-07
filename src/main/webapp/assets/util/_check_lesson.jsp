<%@page import="hotmath.util.HMConnectionPool"%>
<%@page import="hotmath.cm.util.CmCacheManager.CacheName"%>
<%@page import="hotmath.cm.util.CmCacheManager"%>
<%@page import="org.apache.log4j.Level"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="hotmath.assessment.RppWidget"%>
<%@page import="java.util.List"%>
<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="java.sql.Connection"%>
<%@page import="hotmath.inmh.INeedMoreHelpItem"%>
<%@page import="hotmath.assessment.InmhItemData"%>
<%@page import="sb.mail.SbMailManager"%>
<%@page import="hotmath.subscriber.HotMathSubscriber"%>
<%


   Logger logger = Logger.getLogger(InmhItemData.class);
   Level level = Level.toLevel("DEBUG");
   logger.setLevel(level);


   INeedMoreHelpItem item = new INeedMoreHelpItem("review", "topics/transformations-of-graphs.html", "test");
   InmhItemData itemData = new InmhItemData(item);
   
   CmCacheManager.getInstance().addToCache(CacheName.WOOKBOOK_POOL, item.getFile(), null);
   
   List<RppWidget> widgets = null;
   Connection conn=null;
   try {
       conn = HMConnectionPool.getConnection();
       widgets = itemData.getWookBookSolutionPool(conn, "test");
   }
   finally {
       SqlUtilities.releaseResources(null,null,conn);
   }
%>
<%= widgets %>
