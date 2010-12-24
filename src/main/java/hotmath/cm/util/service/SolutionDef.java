package hotmath.cm.util.service;

import hotmath.HotMathProperties;
import hotmath.ProblemID;
import hotmath.cm.util.CatchupMathProperties;

import java.util.HashMap;
import java.util.Map;

import sb.util.SbUtilities;

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
    
    public String getCreateNewXml(String createdBy) {
        Map<String,String> map = new HashMap<String,String>();
        map.put("DATE", SbUtilities.getDateStamp());
        map.put("CREATEDBY", createdBy);
        map.put("BOOK", getBook());
        map.put("CHAPTER",getChapter());
        map.put("SECTION",getSection());
        map.put("SET",getProblemSet());
        map.put("PROBLEM",getProblemNumber());
        map.put("PAGE",Integer.toString(getPage()));
        map.put("PAGE",Integer.toString(getPage()));
        
        String result = SbUtilities.replaceTokens(newSolutionXml, map);
        return result;
    }
    
    String newSolutionXml = 
        "<?xml version =\"1.0\" encoding=\"UTF-8\"?>" +
        "  <hmsl version=\"2.0\" date=\"$$DATE$$\">" +
        "     <problem createdby=\"$$CREATEDBY$$\">" +
        "        <identification book=\"$$BOOK$$\" chapter=\"$$CHAPTER$$\" section=\"$$SECTION$$\" set=\"$$SET$$\" problemnumber=\"$$PROBLEM$$\" page=\"$$PAGE$$\" />" +
        "           <statement>The Problem Statement</statement>" +
        "     </problem>" +
        "  </hmsl>";
    
}
