package hotmath.gwt.cm_rpc.client.rpc;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import com.google.gwt.user.client.rpc.IsSerializable;


/** Represents a Catchup program that has been
 *  automatically advanced based on user's 
 *  current status.
 *  
 *  
 * @author casey
 *
 */
public class AutoUserAdvanced implements Response, IsSerializable{
    
    String programName;
    String chapter;

    public AutoUserAdvanced() {}
    
    public AutoUserAdvanced(String programName, String chapter) {
        this.programName = programName;
        this.chapter = chapter;
    }
    
    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
    
    public String getProgramTitle() {
        String title = programName;
        if(chapter != null)
            title += " (" + chapter + ")";
        
        return title;
    }
}
