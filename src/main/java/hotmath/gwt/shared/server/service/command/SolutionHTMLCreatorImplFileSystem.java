package hotmath.gwt.shared.server.service.command;

import hotmath.HotMathException;
import hotmath.HotMathProperties;
import hotmath.ProblemID;
import hotmath.solution.SolutionParts;
import hotmath.solution.writer.SolutionHTMLCreator;
import hotmath.solution.writer.TutorProperties;

import java.io.File;

import sb.util.SbFile;



/** Provide creator that reads from static files 
 * 
 * @author casey
 *
 */
public class SolutionHTMLCreatorImplFileSystem implements SolutionHTMLCreator {
    
    String base = HotMathProperties.getInstance().getHotMathWebBase();
    public SolutionHTMLCreatorImplFileSystem(String template, String data) {
        /** unused .. 
         * 
         * TODO: generalize construct*/
    }

    @Override
    public SolutionParts getSolutionHTML(TutorProperties tutorProps, String guid) throws HotMathException {
        try {
            ProblemID pid = new ProblemID(guid);
            String path = pid.getSolutionPath_DirOnly("solutions");
    
            String solutionHtml = new SbFile(new File(base, path + "/" +  "/tutor_steps.html")).getFileContents().toString("\n");
            String solutionData = new SbFile(new File(base, path + "/" +  "/tutor_data.js")).getFileContents().toString("\n");
            
            SolutionParts sp = new SolutionParts();
            sp.setData(solutionData);
            sp.setMainHtml(solutionHtml);
            
            return sp;
        }
        catch(Exception e) {
            throw new HotMathException(e,"Error loading solution: " + guid);
        }
   }
}