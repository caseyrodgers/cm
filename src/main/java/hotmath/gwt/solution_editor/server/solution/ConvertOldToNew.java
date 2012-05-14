package hotmath.gwt.solution_editor.server.solution;

import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.solution_editor.client.StepUnitPair;
import hotmath.gwt.solution_editor.client.rpc.LoadSolutionMetaAction;
import hotmath.gwt.solution_editor.client.rpc.SolutionMeta;
import hotmath.gwt.solution_editor.client.rpc.SolutionMetaStep;
import hotmath.gwt.solution_editor.server.CmSolutionManagerDao;
import hotmath.gwt.solution_editor.server.rpc.LoadSolutionMetaCommand;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/** Convert old style Hotmath XML to new style
 * 
 * @author casey
 *
 */
public class ConvertOldToNew {
    Connection conn;
    public ConvertOldToNew(Connection conn) {
        this.conn = conn;
    }
    
    public boolean convert(String pidSt) throws Exception  {
        
        /** read existing
         * 
         */
        SolutionMeta meta = new LoadSolutionMetaCommand().execute(conn,  new LoadSolutionMetaAction(pidSt));
 
        TutorSolution tutorSolution = new CmSolutionManagerDao().getTutorSolution(conn, pidSt);
        if(tutorSolution.getVersion() != null && tutorSolution.getVersion().indexOf("2.0") > -1) {
            System.out.println("Not converted: " + pidSt);
        }
        else {
            System.out.println("Converting solution: " + pidSt);
            
            /** write new format
             * 
             */
            SolutionDef def = new SolutionDef(pidSt);
            
            List<StepUnitPair> stepUnitPairs = new ArrayList<StepUnitPair>();
            for(SolutionMetaStep step: meta.getSteps()) {
                stepUnitPairs.add(new StepUnitPair(step.getHint(),step.getText(), step.getFigure()));
            }
            
            TutorSolution ts = new TutorSolution("sm", def, meta.getProblemStatement(), meta.getFigure(), stepUnitPairs,meta.isActive());
            new CmSolutionManagerDao().saveSolutionXml(conn, pidSt, ts.toXml(), meta.getTutorDefine(),meta.isActive());
        }
        
        
        return true;
    }
    
    static public String FIND_REGEX1=".*div&gt;&lt;p.*";
    
 
    static public void main(String as[]) {
        System.out.println("Converting");
        
        String pid = "prealgptests2_CourseTest_1_Pre-AlgebraPracticeTest_3_1";
        
        Connection conn=null;
        try {
            conn = HMConnectionPool.getConnection();
            ConvertOldToNew converter = new ConvertOldToNew(conn);
            
            //ResultSet rs = conn.createStatement().executeQuery("select problemindex from SOLUTIONS where booktitle in (select distinct textcode from HA_TEST_DEF)");
            ResultSet rs = conn.createStatement().executeQuery("select problemindex from SOLUTIONS where booktitle = 'placement'");
            
            while(rs.next()) {
                converter.convert(rs.getString("problemindex"));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
        
        System.out.println("Convertions complete");
    }
}
