package hotmath.gwt.shared.server.service.command;

import hotmath.HotMathException;
import hotmath.ProblemID;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionService;
import hotmath.solution.SolutionParts;
import hotmath.solution.writer.SolutionHTMLCreator;
import hotmath.solution.writer.TutorProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.Connection;

import sb.util.SbFile;



/** Provide solution creator that reads from static files
 * 
 *  if solution.server property set, then call back to know
 *  service to retrieve actual solution text.
 * 
 * @author casey
 *
 */
public class SolutionHTMLCreatorImplFileSystem implements SolutionHTMLCreator {
    
    public SolutionHTMLCreatorImplFileSystem(String template, String data) {}

    @Override
    public SolutionParts getSolutionHTML(final Connection conn, TutorProperties tutorProps, String guid) throws HotMathException {
        try {
            
            String solutionServer = CatchupMathProperties.getInstance().getSolutionsServer();
            if(solutionServer != null && solutionServer.length() > 0) {
               return readSolutionFromServer(solutionServer, guid);
            }
            else {
                String base = CatchupMathProperties.getInstance().getSolutionBase();
                
                ProblemID pid = new ProblemID(guid);
                String path = pid.getSolutionPath_DirOnly("solutions");
        
                String solutionHtml = new SbFile(new File(base, path + "/" +  "/tutor_steps.html")).getFileContents().toString("\n");
                String solutionData = new SbFile(new File(base, path + "/" +  "/tutor_data.js")).getFileContents().toString("\n");
                
                SolutionParts sp = new SolutionParts();
                sp.setData(solutionData);
                sp.setMainHtml(solutionHtml);
                
                return sp;
            }
        }
        catch(Exception e) {
            throw new HotMathException(e,"Error loading solution: " + guid);
        }
   }

    private SolutionParts readSolutionFromServer(String solutionServer, String pid) throws Exception {
        String url = solutionServer + "/solution_service?pid=" + pid;
        InputStream in = new URL(url).openStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        
        StringBuffer data = new StringBuffer();
        String line=null;
        while((line = reader.readLine()) != null) {
            data.append(line);
        }
        String allLines = data.toString();
        
        String s[] = allLines.split(SolutionService.HTML_JS_DELIMITER);
        if(s.length != 2) {
            throw new Exception("Could not parse solution data");
        }
        
        SolutionParts parts = new SolutionParts();
        parts.setMainHtml(s[0]);
        parts.setData(s[1]);
        return parts;
    }
}