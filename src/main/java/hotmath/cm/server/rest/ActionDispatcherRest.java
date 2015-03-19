package hotmath.cm.server.rest;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/")
public class ActionDispatcherRest {
    
    @POST
    @Path("/user/{userId}/program")
    public String getUserProgram(@PathParam("userId") int userId) throws Exception {
        return Cm2ActionManager.getUserProgram(userId);
    }

}
