package hotmath.gwt.solution_editor.server.solution;

import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.solution_editor.client.StepUnitPair;

import java.io.StringReader;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import sb.client.SbTesterFrameGeneric;
import sb.util.SbException;
import sb.util.SbTestImpl;

/** Provides a high-level way to externalize
 *  solution XML files.  Using this we can modify
 *  the solution steps as individual units and then
 *  export back out for persistent storage.
 *  
 *  main use:
 *  
 *  ts = new TutorSolution(SolutionDef problemID, String statement, List<StepUnitPair>)
 *  ts.getXml();
 *  
 *  This XML can then be read using the SolutionManagerImpl and processed like any other solution
 *  
 * @author casey
 *
 */
@Root(name="hmsl")
public class TutorSolution implements SbTestImpl {
    
    @Attribute (required=false)
    String version="2.0";
    
    @Attribute (required=false)
    String date;
    
    @Attribute (required=false)
    String createdBy;

    @Element
    TutorProblem problem = null;

    public TutorSolution(){}
    
    public TutorSolution(String createdBy,SolutionDef def,String statement, List<StepUnitPair> steps) {
        this(createdBy, def, statement);
        
        for(StepUnitPair step: steps) {
            addStep(step.getHint(), step.getText());
        }
    }
  
    SimpleDateFormat _dateFormat = new SimpleDateFormat("MM/dd/yy");
    public TutorSolution(String createdBy, SolutionDef def, String statement) {
        Identification id = new Identification(def.getBook(), def.getChapter(), 
                def.getSection(), def.getSet(), def.getProblemNumber(), def.getPage());
        
        problem = new TutorProblem(createdBy, id, statement);
        
        this.date = _dateFormat.format(new Date());
    }
    
    /** Add a hint/step combination to solution
     * 
     * @param hint
     * @param text
     */
    public void addStep(String hint, String text) {
        TutorStepUnit hintSu = new TutorStepUnitImplHint(hint);
        TutorStepUnit textSu = new TutorStepUnitImplStep(text);
        
        problem.stepUnits.add(hintSu);
        problem.stepUnits.add(textSu);
    }
    
    public String toXml() throws Exception {
        Serializer serializer = new Persister();
        StringWriter sw = new StringWriter();
        serializer.write(this,sw);
        return sw.toString();
    }

    @Override
    public void doTest(Object arg0, String arg1) throws SbException {
        try {
            System.out.println(toXml());
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    
  
    
    static public TutorSolution parse(String xml) throws Exception  {
        try {
            /** Use the JDOM parser until we can work out details with 
             * SimpleXML dealing with elments containing text+child-elements.
             * 
             */
            
            if(true) {
                return TutorSolutionParser.parseXML(xml);
            }
            else {
                Serializer serializer = new Persister();
                //Configuration configuraiton = serializer.read(Configuration.class, fileLocation);
                StringReader sr = new StringReader(xml);
                TutorSolution tutorSolution = serializer.read(TutorSolution.class, sr);
                return tutorSolution;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
        
    
    static public void main(String as[]) {
        try {
            SolutionDef def = new SolutionDef("MYBOOK_MYCHAP_MYSEC_MYSET_MYPN_MYPAGE");
            new SbTesterFrameGeneric(new TutorSolution("me", def, "This is the statement"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public TutorProblem getProblem() {
        return problem;
    }

    public void setProblem(TutorProblem problem) {
        this.problem = problem;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public SimpleDateFormat get_dateFormat() {
        return _dateFormat;
    }

    public void set_dateFormat(SimpleDateFormat _dateFormat) {
        this._dateFormat = _dateFormat;
    }
}

enum StepType {HINT,STEP,QUESSTION,PROOF}
