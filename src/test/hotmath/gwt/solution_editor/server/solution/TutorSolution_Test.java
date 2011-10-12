package hotmath.gwt.solution_editor.server.solution;

import hotmath.cm.util.service.SolutionDef;
import junit.framework.TestCase;

public class TutorSolution_Test extends TestCase {

    public TutorSolution_Test(String name) {
        super(name);
    }
    SolutionDef def = new SolutionDef("MYBOOK_MYCHAP_MYSEC_MYSET_MYPN_MYPAGE");
    
    
    public void testCreateSolutionWithFigure() throws Exception {
        TutorSolution ts = new TutorSolution("test", def,"The Statement");
        ts.addStep("The Hint", "The Step","theFigure");
        
        String xml = ts.toXml();
        System.out.println(xml);
        assertTrue(xml.contains("theFigure"));
    }
    
    public void testCreateSolutionXml() throws Exception {
        TutorSolution ts = new TutorSolution("test",def,"The Statement");
        String xml = ts.toXml();
        assertTrue(xml.contains("chapter=\"MYCHAP\""));
        assertTrue(xml.contains("createdby=\"test\""));
        assertTrue(xml.contains("The Statement"));
    }
    
    public void testCreateSolutionXml2() throws Exception {
        TutorSolution ts = new TutorSolution("test", def,"The Statement");
        ts.addStep("The Hint", "The Step",null);
        
        String xml = ts.toXml();
        assertTrue(xml.contains("The Hint"));
        assertTrue(xml.contains("The Step"));
    }
    
}
