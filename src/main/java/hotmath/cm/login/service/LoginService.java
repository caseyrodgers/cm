package hotmath.cm.login.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Provide redirect back to main server to get login information.
 * 
 * Login information is stored in CmLoginInfo which provivdes basic login
 * information, such as:
 * 
 * if is admin user (should show the admin tool on login) or a student user,
 * which would load the student tool
 * 
 * Information is stored in CmLoginInfo object serialized as JSON
 * 
 * 
 * (provide quick dirty proxy to main server )
 * 
 * 
 * @author casey
 * 
 */
public class LoginService extends HttpServlet {

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String user = req.getParameter("user");
        String pass = req.getParameter("pwd");
        String url = "http://localhost:8081/cm_login/services/loginService?user=" + user + "&pwd=" + pass;
        URL loginService = new URL(url);
        BufferedReader in = new BufferedReader(new InputStreamReader(loginService.openStream()));
        StringBuffer sb = new StringBuffer();
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            sb.append(inputLine);
        }
        in.close();
        resp.getWriter().write(sb.toString());
    }
}
