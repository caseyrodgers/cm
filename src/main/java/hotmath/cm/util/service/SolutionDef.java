package hotmath.cm.util.service;

import hotmath.HotMathProperties;
import hotmath.ProblemID;

public class SolutionDef extends ProblemID {
    public SolutionDef(String pid) {
        super(pid);
    }
    
    public String getSolutionPathOnDisk() {
        return HotMathProperties.getInstance().getHotMathWebBase() + 
               "/" + HotMathProperties.getInstance().getStaticSolutionsDir() +
               "/" + super.getSolutionPath() +
               "/" + getGUID();
    }
    
    /** Return Absolute path to the resource
     *  directory for this solution.  This is 
     *  the file system path .. 
     * @return
     */
    public String getResourcesPath() {
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
