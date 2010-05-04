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
    
    public void addProgramType(ProgramType programType) {
        programTypes.add(programType);
        
        /** also always add to ALL
         * 
         */
        List<ProgramChapter> chaps = programType.getProgramChapters();
        for(int i=1;i<chaps.size();i++) {
            programTypes.get(0).getProgramChapters().add(chaps.get(i));
        }
    }
}
