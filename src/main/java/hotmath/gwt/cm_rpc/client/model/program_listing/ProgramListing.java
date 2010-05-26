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
public class ProgramListing implements CmTreeNode, Response {
    List<ProgramType> programTypes = new ArrayList<ProgramType>();
    
    public ProgramListing(){
    }
    public List<ProgramType> getProgramTypes() {
        return programTypes;
    }
    public void setProgramTypes(List<ProgramType> programTypes) {
        this.programTypes = programTypes;
    }
    
    @Override
    public String getLabel() {
        return "Program Listing";
    }
    @Override
    public int getLevel() {
        return LEVEL_ROOT;
    }
    
    final public static int LEVEL_ROOT=0;
    final public static int LEVEL_TYPE=1;
    final public static int LEVEL_SUBJ=2;
    final public static int LEVEL_CHAP=3;
    final public static int LEVEL_SECT=4;
    final public static int LEVEL_LESS=5;

	@Override
	public CmTreeNode getParent() {
		return null;
	}
}
