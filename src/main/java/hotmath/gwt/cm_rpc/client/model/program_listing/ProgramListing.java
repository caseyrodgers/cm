package hotmath.gwt.cm_rpc.client.model.program_listing;


import hotmath.gwt.cm_rpc.client.rpc.Response;

import java.util.ArrayList;
import java.util.List;

/** Class to encapsulate all program offerings
 *  for CM
 *  
 * @author casey
 *
 */
public class ProgramListing implements Response {
    List<ProgramType> programTypes = new ArrayList<ProgramType>();
    
    public ProgramListing(){
    }
    public List<ProgramType> getProgramTypes() {
        return programTypes;
    }
    public void setProgramTypes(List<ProgramType> programTypes) {
        this.programTypes = programTypes;
    }
}
