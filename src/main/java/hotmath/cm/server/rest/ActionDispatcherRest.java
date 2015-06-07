package hotmath.cm.server.rest;

import hotmath.gwt.cm_core.client.model.TopicResource;
import hotmath.gwt.cm_rpc.client.rpc.GetCmProgramFlowAction.FlowType;

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
        String subject = jo.getString("subject");

        return Cm2ActionManager.loginUser(uid, un, pwd, subject);
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
    @Path("/prescription/{rid}/solution/{pid}")
    //     /prescription/3545220/topic/1/resource/practice/7
    public String getSolutionForTestRun(@PathParam("rid") int rid, @PathParam("pid") String pid) throws Exception {
        return Cm2ActionManager.getSolution(rid, pid);
    }

    @POST
    @Path("/run/{rid}/resource/view")
    public String markResourceAsViewed(@PathParam("rid") int rid, String jsonData) throws Exception {
        JSONObject jo = new JSONObject(jsonData);
        int topicIndex = jo.getInt("topicIndex");
        String type = jo.getString("type");
        String file = jo.getString("file");

        return Cm2ActionManager.markResourceAsViewed(rid, topicIndex, type, file);
    }


    @POST
    @Path("/quiz/{tid}/answer")
    public String setQuizAnswer(@PathParam("tid") int tid, String jsonData) throws Exception {
        JSONObject jo = new JSONObject(jsonData);
        int choiceIndex = jo.getInt("choiceIndex");
        String pid = jo.getString("pid");
        boolean isCorrect = jo.getBoolean("isCorrect");

        return Cm2ActionManager.setQuizAnswer(tid, pid, choiceIndex, isCorrect);
    }


    @POST
    @Path("/prescription/{rid}/context/{pid}")
    public String setPrescriptionSolutionContext(@PathParam("rid") int rid, @PathParam("pid") String pid, String context) throws Exception {
        return Cm2ActionManager.setPrescriptionSolutionContext(rid, pid, context);
    }



    @POST
    @Path("/prescription/{rid}/solution/{pid}/whiteboard/{uid}")
    public String getPrescriptionSolutionWhiteboard(@PathParam("rid") int rid, @PathParam("pid") String pid, @PathParam("uid") int uid) throws Exception {
        return Cm2ActionManager.getPrescriptionSolutionWhiteboard(uid, rid, pid);
    }

    @POST
    @Path("/whiteboard/write")
    public String writePrescriptionSolutionWhiteboard(String jsonData) throws Exception {
        JSONObject jo = new JSONObject(jsonData);
        int uid = jo.getInt("uid");
        int rid = jo.getInt("rid");
        int tid = jo.getInt("tid");
        String pid = jo.getString("pid");
        String cmdType = jo.getString("type");
        String json = jo.getString("json");

        return Cm2ActionManager.savePrescriptionSolutionWhiteboard(uid, tid, rid, pid, cmdType, json);
    }


    @POST
    @Path("/search")
    public String getSearchResults(String jsonData) throws Exception {
        JSONObject jo = new JSONObject(jsonData);
        int uid = jo.getInt("uid");
        String search = jo.getString("searchFor");
        return Cm2ActionManager.getSearchResults(uid, search);
    }


    @POST
    @Path("/search/topic")
    public String doSearchLesson(String jsonData) throws Exception {
        JSONObject jo = new JSONObject(jsonData);
        int uid = jo.getInt("uid");
        String search = jo.getString("topic");
        return Cm2ActionManager.getSearchTopic(search);
    }
    
    @POST
    @Path("/user/{uid}/program/advance")
    public String advanceUserProgramSection(@PathParam("uid") int uid, String json) throws Exception {
        JSONObject jo = new JSONObject(json);
        boolean retake = jo.getInt("retake")==0?false:true;

        return Cm2ActionManager.advanceUserProgram(uid, !retake);
    }
    
    
    @POST
    @Path("/assignments/{uid}")
    public String getAssignmentsListing(@PathParam("uid") int uid) throws Exception {
        return Cm2ActionManager.getAssignmentsListing(uid);
    }
 
}
