package hotmath.cm.server.rest;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class ActionDispatcherRest {
    
    @POST
    @GET
    @Path("/user/{userId}/program/current")
    public String getUserProgram(@PathParam("userId") int userId) throws Exception {
        return Cm2ActionManager.getUserCurrentProgram(userId);
    }
    
    @POST
    @Path("/login/user")
    public String loginUser(String userInfo) throws Exception {
        JSONObject jo = new JSONObject(userInfo);
        String un = jo.getString("user");
        String pwd = jo.getString("pass");
        int uid = jo.getInt("uid");
        
        return Cm2ActionManager.loginUser(uid, un, pwd);
    }
    
    @POST
    @GET
    @Path("/quiz/{testId}/check")
    public String checkQuiz(@PathParam("testId") int testId) throws Exception {
        return Cm2ActionManager.checkQuiz(testId);
    }

}
