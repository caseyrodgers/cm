package hotmath.cm.server.rest;

import hotmath.gwt.cm_core.client.model.TopicResource;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;

import com.cedarsoftware.util.io.JsonReader;

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
    
    @POST
    @GET
    @Path("/prescription/{runId}/topic/{topicIndex}")
    public String getPrescription(@PathParam("runId") int runId,@PathParam("topicIndex") int topicIndex) throws Exception {
        return Cm2ActionManager.getPrescriptionTopic(runId, topicIndex);
    }
    
    
    @POST
    @Path("/prescription/{runId}/topic/{topicIndex}/resource")
    //     /prescription/3545220/topic/1/resource/practice/7
    public String getPrescriptionResource(@PathParam("runId") int runId,@PathParam("topicIndex") int topicIndex, String json) throws Exception {
        TopicResource resource = (TopicResource)JsonReader.jsonToJava(json);
        return Cm2ActionManager.getPrescriptionResource(resource.getFile());
    }
    
    @POST
    @Path("/solution/{pid}")
    //     /prescription/3545220/topic/1/resource/practice/7
    public String getSolution(@PathParam("pid") String pid) throws Exception {
        return Cm2ActionManager.getSolution(pid);
    }

    @POST
    @Path("/run/{rid}/resource/view")
    public String markRppAsViewed(@PathParam("rid") int rid, String jsonData) throws Exception {
        JSONObject jo = new JSONObject(jsonData);
        int topicIndex = jo.getInt("topicIndex");
        String pid = jo.getString("pid");
        
        return Cm2ActionManager.markRppAsViewed(rid, topicIndex, pid);
    }

}
