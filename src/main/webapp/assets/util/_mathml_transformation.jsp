<%@page contentType="text/html" pageEncoding="UTF-8"%>
<html>
<head>
<%@page import="hotmath.mathml.MathMlTransform"%>
<%

    String html = (String)request.getParameter("uncookedText");
    String cookedHtml=null; 
    if(html != null) {
    	cookedHtml = new MathMlTransform().processMathMlTransformations(html);
    }
%>
<style>
     textarea {
         width: 600px;
         height: 200px;
     }


</style>
</head>
<body>
    <form action="#" method="POST">
        <h2>Raw HTML</h2>
        <div>
            <textarea name="uncookedText" id='textArea'></textarea>
        </div>
        <button type='submit'>Transform</button>
        
         <h2>Processed HTML</h2>
         <div>
            <%= cookedHtml %>
        </div
        
    </form>
</body>

</html>