package hotmath.gwt.shared.server.service.command;

import hotmath.HotMathException;
import hotmath.HotMathLogger;
import hotmath.HotMathProperties;
import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.gwt.cm_rpc.client.rpc.Action;
import hotmath.gwt.cm_rpc.client.rpc.CmRpcException;
import hotmath.gwt.cm_rpc.client.rpc.GetSolutionAction;
import hotmath.gwt.cm_rpc.client.rpc.Response;
import hotmath.gwt.cm_rpc.client.rpc.SolutionInfo;
import hotmath.gwt.cm_rpc.server.rpc.ActionHandler;
import hotmath.solution.SolutionParts;
import hotmath.solution.writer.SolutionHTMLCreator;
import hotmath.solution.writer.TutorProperties;
import hotmath.util.VelocityTemplateFromStringManager;
import hotmath.util.sql.SqlUtilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import sb.util.SbFile;

/**
 * Return the raw HTML that makes up the solution
 * 
 * Use the uid to lookup if this solution has any ShowWork applied
 * 
 * Return RpcData with the following members: solutionHtml, hasShowWork
 * 
 */
public class GetSolutionCommand implements ActionHandler<GetSolutionAction, SolutionInfo> {
	
	private static final Logger logger = Logger.getLogger(GetSolutionCommand.class);

    public static SolutionHTMLCreator __creator;
    static TutorProperties __tutorProps = new TutorProperties();
    static {
        try {
            __creator = new SolutionHTMLCreatorImplFileSystem(__tutorProps.getTemplate(), __tutorProps.getTutor());
        } catch (Exception hme) {
            HotMathLogger.logMessage(hme, "Error creating solution creator: " + hme);
        }
    }

    @Override
    public SolutionInfo execute(final Connection conn, GetSolutionAction action) throws Exception {
        try {
            long startTime = System.currentTimeMillis();
            String pid = action.getPid();
            int uid = action.getUid();
            
            ProblemID ppid = new ProblemID(pid);
            
            SolutionParts sp = __creator.getSolutionHTML(null, pid);
            String solutionHtml= sp.getMainHtml();
            
            String path = ppid.getSolutionPath_DirOnly("solutions");
            solutionHtml = HotMathUtilities.makeAbsolutePaths(path, solutionHtml);

            Map<String, String> map = new HashMap<String, String>();
            map.put("solution_html", solutionHtml);
            map.put("pid", pid);
            logger.debug(String.format("+++ execute(): solution_html done in: %d msec",
            	System.currentTimeMillis()-startTime));
            
            startTime = System.currentTimeMillis();
            InputStream is = getClass().getResourceAsStream("tutor_wrapper.vm");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String tutorWrapper = null;
            StringBuilder sb = new StringBuilder();
            while ((tutorWrapper = br.readLine()) != null) {
                sb.append(tutorWrapper);
            }
            tutorWrapper = sb.toString();

            solutionHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(tutorWrapper, map);
            logger.debug(String.format("+++ execute(): solutionHtml (Velocity) done in: %d msec",
                	System.currentTimeMillis()-startTime));
            
            startTime = System.currentTimeMillis();
            boolean hasShowWork = getHasShowWork(conn, uid, pid);
            logger.debug(String.format("+++ execute(): getHasShowWork() done in: %d msec",
                	System.currentTimeMillis()-startTime));

            startTime = System.currentTimeMillis();
            SolutionInfo solutionInfo = new SolutionInfo(solutionHtml,sp.getData(),hasShowWork);
            logger.debug(String.format("+++ execute(): SolutionInfo() done in: %d msec",
                	System.currentTimeMillis()-startTime));

            return solutionInfo;
        } catch (Exception e) {
        	logger.error(String.format("*** Error executing Action: %s", action.toString()), e);
            throw new CmRpcException(e);
        }
    }

    @Override
    public Class<? extends Action<? extends Response>> getActionType() {
        return GetSolutionAction.class;
    }
    
    

    


    /**
     * Return true if this solution has any Show Work activity for this user.
     * 
     * 
     * @param uid
     * @param pid
     * @return
     * @throws Exception
     */
    private boolean getHasShowWork(final Connection conn, int uid, String pid) throws Exception {
        PreparedStatement pstat = null;
        ResultSet rs = null;
        try {
            String sql = "select count(*) as cnt from HA_TEST_RUN_WHITEBOARD where user_id = ? and pid = ?";
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, uid);
            pstat.setString(2, pid);

            rs = pstat.executeQuery();
            rs.first();
            int cnt = rs.getInt("cnt");
            return cnt > 0;
        } finally {
            SqlUtilities.releaseResources(rs, pstat, null);
        }
    }    

}
