package hotmath.gwt.solution_editor.server.solution;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;



@Root (name="response")
public class Response {
    
    @Element (required=false,data=true)
    String response;
    
    @Element (required=false)
    public Response() { }
}

