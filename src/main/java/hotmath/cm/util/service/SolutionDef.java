package hotmath.cm.util.service;

import hotmath.HotMathProperties;
import hotmath.ProblemID;

public class SolutionDef extends ProblemID {
    public SolutionDef(String pid) {
        super(pid);
    }
    
    public String getSolutionPath() {
        return HotMathProperties.getInstance().getHotMathWebBase() + 
               "/" + HotMathProperties.getInstance().getStaticSolutionsDir() +
               "/" + super.getSolutionPath() +
               "/" + getGUID();
    }
    
    public String getResourcesPath() {
        return getSolutionPath() + "/resources";
    }
}
