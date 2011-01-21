package hotmath.gwt.solution_editor.server.solution;

import hotmath.HotMathException;
import hotmath.cm.util.service.SolutionDef;
import hotmath.util.HtmlCleanser;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import sb.logger.SbLogger;
import sb.util.SbFile;
import sb.util.SbUtilities;

import com.sdicons.json.model.JSONObject;
import com.sdicons.json.model.JSONValue;
import com.sdicons.json.parser.JSONParser;

/** Parse the XML file, and create the abstractions */
public class TutorSolutionParser {

    static SAXBuilder __builder;

    @SuppressWarnings("rawtypes")
    public static TutorSolution parseXML(String sXML) throws Exception {
        if (__builder == null) {
            __builder = new SAXBuilder();
        }

        Document doc = __builder.build(new StringReader(sXML));
        Element rootElement = doc.getRootElement();

        TutorSolution solution = new TutorSolution();
        solution.setProblem(new TutorProblem());
        

        // first get a list of problems
        List eleList = rootElement.getChildren("problem");
        int iTotalProblems = eleList.size();

        if (iTotalProblems == 0) {
            throw new Exception("No <problem> elements defined in XML");
        }

        for (int iProblem = 0; iProblem < iTotalProblems; iProblem++) {
            Element eleProblem = (Element) eleList.get(iProblem);

            // the compiler version is a comment in the XML ... it should
            // an attribute that can be retrieved. We just yank it out
            // of the XML. This is done here to not hide this shit.
            String compilerVersion = SbUtilities.getToken(sXML, 2, "\n"); // first
                                                                          // comment
            
            if(compilerVersion != null) {
                String p[] = compilerVersion.split("version");
                if (p.length > 1) {
                    compilerVersion = p[1];
                    compilerVersion = compilerVersion.replace("-->", "").trim();
                }
            }

            String sCreator = eleProblem.getAttributeValue("createdby", compilerVersion);

            solution.setVersion(compilerVersion);
            solution.setCreatedBy(sCreator);

            // get the identification of this problem
            Element eleId = eleProblem.getChild("identification");
            String sBook = trimIfNotNull(eleId.getAttributeValue("book"));
            String sChapter = trimIfNotNull(eleId.getAttributeValue("chapter"));
            String sSection = trimIfNotNull(eleId.getAttributeValue("section"));
            String sProbNumber = trimIfNotNull(eleId.getAttributeValue("problemnumber"));
            String sProblemSet = trimIfNotNull(eleId.getAttributeValue("set"));
            int iPageNumber = SbUtilities.getInt(eleId.getAttributeValue("page"));

            if (iPageNumber == 0) {
                throw new Exception("Page number is zero (compiler bug?)");
            }

            /**
             * extract the statement from XML (if exists)
             */
            String sProblemStatement = "";
            String sProblemStatementFigure = "";

            Element eleStat = eleProblem.getChild("statement");
            if (eleStat != null) {
                sProblemStatement = eleStat.getText().trim();
                sProblemStatementFigure = eleStat.getAttributeValue("figure");
                solution.getProblem().setStatement(sProblemStatement);
                solution.getProblem().setStatementFigure(sProblemStatementFigure);
            }

            Identification ident = new Identification(sBook, sChapter, sSection, sProblemSet, sProbNumber, iPageNumber);
            solution.getProblem().setIdentification(ident);

            // now get list of step units defined for this solution
            List listUnits = eleProblem.getChildren();
            int iTotalUnits = listUnits.size();
            if (iTotalUnits == 0) {
                throw new HotMathException("No stepunit elements found for solution");
            }
            /**
             * For each stepUnit, create the appropriate type, set the stepunits
             * role
             */
            for (int iUnit = 0; iUnit < iTotalUnits; iUnit++) {
                Element stepEle = (Element) listUnits.get(iUnit);
                if (stepEle.getName().equals("stepunit")) {
                    TutorStepUnit stepUnit = create(ident.toString(), stepEle);
                    solution.getProblem().getStepUnits().add(stepUnit);
                }
            }
        }

        return solution;
    }

    static public String trimIfNotNull(String s) {
        if (s != null)
            return s.trim();
        return null;
    }

    // TODO: static method, does this need to be synchronized ?
    static public synchronized TutorStepUnit create(String pid, Element element) throws Exception {

        String sStepUnitText = null;
        String sType = null;

        TutorStepUnit stepUnit = null;

        List kids = element.getChildren();
        if (kids.size() == 0)
            throw new HotMathException("no step unit found");

        Element eleUnit = (Element) kids.get(0);

        // do the Hotmath Token Replacements.
        // This will replace any special tokens
        // used in StepUnit text with the corresponding text
        sStepUnitText = eleUnit.getText();

        // HM COMPILER BUG WORKAROUND
        // remove leading and trailing &nbsp;
        sStepUnitText = cleanUpText(sStepUnitText);

        sType = eleUnit.getName();
        if (sType.equals("hint")) {
            String sHintText = sStepUnitText;
            stepUnit = new TutorStepUnitImplHint(sHintText);
        } else if (sType.equals("step")) {
            String sStepText = sStepUnitText;
            stepUnit = new TutorStepUnitImplStep(sStepText);
        } else if (sType.equals("proofstep")) {
            // should be proofset/stepunit/justification
            // get this elements next sibling (which must be a
            // stepunit/justification)
            if (kids.size() < 2) {
                throw new HotMathException("stepunit/proofstep does not have sibling with justication");
            }
            Element eleChildStep = (Element) kids.get(1);
            if (eleChildStep == null) {
                throw new HotMathException("No stepunit/justification found under proofstep");
            }
            
            stepUnit = new TutorStepUnitImplProof(eleUnit.getValue());
            return stepUnit;

        } else if (sType.equals("question")) {
            stepUnit = new TutorStepUnitImplHint(convertOldQuestionFormatToNew(pid, eleUnit));
        } else {
            throw new HotMathException("Unknown StepUnit type: " + sType);
        }

        // see if there is a equation attached to StepUnit
        String sFigure = eleUnit.getAttributeValue("figure");
        if (sFigure != null && sFigure.length() > 0) {
            stepUnit.getFigures().add(sFigure);
        }

        return stepUnit;

    }

    
    /** old:
     * <div class="question">QUESTION</div>
       <div class="question_guess">
           <img onmouseover="doQuestionResponse('rid_39','no');" onmouseout="doQuestionResponseEnd();" src="/images/tutor5/hint_question-16x16.gif" name="question_0" class="text-bottom">&nbsp;<p>29 and 31</p>
       </div>
       <div class="question_guess">
           <img onmouseover="doQuestionResponse('rid_40','yes');" onmouseout="doQuestionResponseEnd();" src="/images/tutor5/hint_question-16x16.gif" name="question_1" class="text-bottom">&nbsp;<p>35 and 37</p>
      </div>
      
      
      
      
      example tutor_data.js:
      {

      "tutorProperties": {
        "_isControlled":"false",
        "_textCode":"samples",
        "_category":"$category",
        "_bookTitle":"Sample Exercises"
        },
      "_stepUnits_moArray": {},
      "_strings_moArray": {"rid_99":"Combine similar terms.", 
                           "rid_102":"You\'ve found the first odd integer.",
                           "rid_100":"Next, gather the constants on the right side." 
                          }
     }
     */ 
     static private String convertOldQuestionFormatToNew(String pid, Element oldQuesEl) throws Exception {
        
        List<QuestionPiece> questionPieces = new ArrayList<QuestionPiece>();

        String questionText = HtmlCleanser.getInstance().cleanseHtml(oldQuesEl.getTextNormalize());
        List children = oldQuesEl.getChildren();
        for(Object og: children) {
            Element guess = (Element)og;
            String correct = guess.getAttributeValue("correct");
            String guessText = HtmlCleanser.getInstance().cleanseHtml(guess.getTextNormalize());
            Element response = (Element)guess.getChild("response");
            String responseText = HtmlCleanser.getInstance().cleanseHtml(response.getTextNormalize());
            
            questionPieces.add(new QuestionPiece(correct, guessText, responseText));
        }
        
        
        String newHtml = 
            "<div class='hm_question_def'>\n" +
            "    <div>" + questionText + "</div>\n" +
            "    <ul>\n";
        
        for(QuestionPiece p: questionPieces) {
            newHtml += 
            "        <li correct='" + p.correct + "'>\n" +
            "            <div>" + p.guess + "</div>\n" +
            "            <div>" + p.response + "</div>\n" +
            "        </li>\n";
        }
                
        newHtml += 
            "    </ul>\n" +
            "</div>\n";

        return newHtml;
    }
    
     static class QuestionPiece {
         String correct;
         String guess;
         String response;
         
         public QuestionPiece(String correct, String guess, String response) {
             this.correct = correct;
             this.guess = guess;
             this.response = response;
         }
     }
    
    
    /**
     * Clean up text by removing white space. This includes newlines
     * 
     * @param s
     * @return string stripped of leading/trailing whitespace
     */
    static public String cleanUpText(String s) {
       if (s == null)            return s;

        s = s.trim();

        if (s.length() == 0)
            return s;

        // remove new lines
        String[] ws = null;
        StringBuffer sb = new StringBuffer();

        // split on ASCII dec 160 (NON BREAK SPACE)
        // added by hand-editing? compiler output char encoding?
        char c = s.charAt(0);
        int x = (int) c; // "\0240"
        if (x == 160) {
            SbLogger.postMessage("solutionXML contains NON-BREAKING-SPACE (160)", "debug");
            StringBuffer si = new StringBuffer();
            si.append(c);
            ws = s.split(si.toString());
            if (ws.length > 1) {
                sb = new StringBuffer();
                for (int i = 0, t = ws.length; i < t; i++) {
                    sb.append(ws[i]);
                }
                s = sb.toString();
                s = s.trim();
            }
        }
        return s;
    }

    /**
     * removing leading/trailing &nbsp; added by HM Compiler
     * 
     * @param s
     * @return
     */
    static public String trimLeadingTrailingNbsp(String s) {
        s = s.trim();
        while (s.startsWith("&nbsp;")) {
            s = s.substring(6);
        }
        return s;
    }
}
