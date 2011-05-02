<%@page contentType="text/plain"  %>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="hotmath.cm.util.CatchupMathProperties"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileOutputStream"%>
<%@page import="java.io.InputStream"%>
<%@page import="hotmath.util.sql.SqlUtilities"%>
<%@page import="java.sql.Connection"%>
<%@page import="hotmath.assessment.InmhAssessment"%>
<%@ page import="sb.util.*,hotmath.concordance.*, hotmath.*,hotmath.testset.*, hotmath.help.*" %>
<%

   String serverName = request.getServerName();
   int port = request.getServerPort();
   if(port != 80) {
       serverName += ":" + port;
   }

   // get POSTED data
   // use servlet input stream
   ServletInputStream ins = request.getInputStream();
   //count length of data
   int len = request.getContentLength();
   //allocate bytes buffer
   byte[] buf = new byte[len];
   int offset = 0;
   do {
       int inputLen = ins.read(buf, offset, len - offset);
       if (inputLen <= 0){
           String msg = "read finished early - read " +
           offset + " of " + len + " bytes(contentLength)" ;
           throw new Exception(msg);
       }
       offset += inputLen;
   } while(offset < len);
   
   SimpleDateFormat format = new SimpleDateFormat("yy_dd_MM_hh_ss");
   String outputFile=CatchupMathProperties.getInstance().getSolutionBase();
   
   String baseFile = "/screen_shots/" + format.format(new Date()) + ".jpg";
   
   outputFile += baseFile;
   String screenShotUrl = "http://" + serverName + baseFile; 
   FileOutputStream fos = null;
   try {
       fos = new FileOutputStream(new File(outputFile));
       fos.write(buf);
   }
   finally {
       fos.flush();
       fos.close();
   }
%>
<%= screenShotUrl %>
