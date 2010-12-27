package hotmath.gwt.solution_editor.server.solution;

import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.solution_editor.client.StepUnitPair;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
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
    
    @Attribute
    String version="2.0";
    
    @Attribute
    String date;
    
    @Element
    TutorProblem problem = null;

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
        TutorStepUnit hintSu = new TutorStepUnit(StepType.HINT,hint);
        TutorStepUnit textSu = new TutorStepUnit(StepType.STEP,text);
        
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
    
    static public void main(String as[]) {
        try {
            SolutionDef def = new SolutionDef("MYBOOK_MYCHAP_MYSEC_MYSET_MYPN_MYPAGE");
            new SbTesterFrameGeneric(new TutorSolution("me", def, "This is the statement"));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

@Root (name="problem")
class TutorProblem {
    
    public TutorProblem(String createdBy, Identification id, String statement) {
        this.identification = id;
        this.statement = statement;
        this.createdBy = createdBy;
    }
    
    @Attribute(name="createdby")
    String createdBy;

    @Element
    Identification identification;
    
    @Element (data=true,required=false)
    String statement;
    
    @ElementList (inline=true)
    List<TutorStepUnit> stepUnits = new ArrayList<TutorStepUnit>();
}

@Root (name="identification")
class Identification {
    
    @Attribute
    String book;
        
    @Attribute
    String chapter;
    
    @Attribute 
    String section;
    
    @Attribute 
    String set;
    
    @Attribute (name="problemnumber") 
    String problemNumber;
    
    @Attribute 
    Integer page;
    
    
    public Identification(String book, String chapter, String section, String set, String problemNumber, Integer page) {
        this.book = book;
        this.chapter = chapter;
        this.section = section;
        this.set = set;
        this.problemNumber = problemNumber;
        this.page = page;
    }


    public String getBook() {
        return book;
    }


    public void setBook(String book) {
        this.book = book;
    }


    public String getChapter() {
        return chapter;
    }


    public void setChapter(String chapter) {
        this.chapter = chapter;
    }


    public String getSection() {
        return section;
    }


    public void setSection(String section) {
        this.section = section;
    }


    public String getSet() {
        return set;
    }


    public void setSet(String set) {
        this.set = set;
    }


    public String getProblemNumber() {
        return problemNumber;
    }


    public void setProblemNumber(String problemNumber) {
        this.problemNumber = problemNumber;
    }


    public Integer getPage() {
        return page;
    }


    public void setPage(Integer page) {
        this.page = page;
    }
}


@Root (name="stepunit")
class TutorStepUnit {
    
    @Element (required=false,data=true)
    String step;
    
    @Element (required=false,data=true)
    String hint;
    
    public TutorStepUnit(StepType type, String text) {
        switch(type) {
           case HINT:
               hint = text;
               break;
               
           case STEP:
               step = text;
               break;
        }
        
    }
}

enum StepType {HINT,STEP}

@Root (name="step")
class TutorStep {}


@Root (name="hint")
class TutorHint {}