package hotmath.gwt.solution_editor.server.solution;

import java.io.IOException;
import java.io.StringWriter;

import org.jdom.CDATA;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class TutorSolutionXmlWriter {
    
    TutorSolution tutorSolution;
    
    public TutorSolutionXmlWriter(TutorSolution ts) {
        this.tutorSolution = ts;
    }

    public String toXml() throws Exception {
        XMLOutputter outputter = new XMLOutputter();
        outputter.setFormat(Format.getPrettyFormat());
        StringWriter out = new StringWriter();
        try {
            
          Element root = new Element("hmsl");
          root.setAttribute("version", "2.0");
          root.setAttribute("date", tutorSolution.getDate());
          
          TutorProblem problem = tutorSolution.getProblem();
          Element prob = new Element("problem");
          root.addContent(prob);
          if(problem.getCreatedBy() != null) {
              prob.setAttribute("createdby", problem.getCreatedBy());
          }
          
          
          Identification id = problem.getIdentification();
          Element ident = new Element("identification");
          ident.setAttribute("book", id.getBook());
          ident.setAttribute("chapter", id.getChapter());
          ident.setAttribute("section",id.getSection());
          ident.setAttribute("set", id.getSet());
          ident.setAttribute("problemNumber",id.getProblemNumber());
          ident.setAttribute("page","" + id.getPage());
          prob.addContent(ident);
          
          
          Element stmt = new Element("statement");
          if(problem.getStatementFigure() != null) {
              stmt.setAttribute("figure",problem.getStatementFigure());
          }
          stmt.addContent(new CDATA(problem.getStatement()));
          prob.addContent(stmt);
          
          
          for(TutorStepUnit su: problem.getStepUnits()) {
              
              Element stepUnit = new Element("stepunit");
              Element stepNode=null;
              switch(su.getRole()) {
              
              case HINT:
                  stepNode = new Element("hint");
                  stepNode.addContent(new CDATA(su.getContentAsString()));
                  break;
                  
              case STEP:
                  stepNode = new Element("step");
                  String figure = su.getFigure();
                  if(figure != null) {
                      stepNode.setAttribute("figure", figure);
                  }
                  stepNode.addContent(new CDATA(su.getContentAsString()));
                  break;
                  
                  default:
                      throw new Exception("Unknown step type: " + su);
              }
              
              if(stepNode != null) {
                  stepUnit.addContent(stepNode);
                  prob.addContent(stepUnit);
              }
          }
          
          outputter.output(root, out); 
        }
        catch (IOException e) {
          System.err.println(e);
        }        
        System.out.println(out.toString());
        return out.toString();        
    }
}
