package hotmath.cm.util.service;

import hotmath.HotMathProperties;
import hotmath.ProblemID;
import hotmath.cm.util.CatchupMathProperties;

public class SolutionDef extends ProblemID {
    public SolutionDef(String pid) {
        super(pid);
    }
    
    public String getSolutionPathOnDisk() throws Exception {
        return CatchupMathProperties.getInstance().getSolutionBase() + 
               "/" + HotMathProperties.getInstance().getStaticSolutionsDir() +
               "/" + super.getSolutionPath() +
               "/" + getGUID();
    }
    
    /** Return Absolute path to the resource
     *  directory for this solution.  This is 
     *  the file system path .. 
     * @return
     */
    public String getResourcesPath() throws Exception {
        return getSolutionPathOnDisk() + "/resources";
    }
    
    public String getSolutionPathHttp() {
        String path = getSolutionPath();
        
        path = HotMathProperties.getInstance().getStaticSolutionsDir() 
            + "/"
            + path
            + "/"
            + getGUID();
        
        return path;
               
    }
}
