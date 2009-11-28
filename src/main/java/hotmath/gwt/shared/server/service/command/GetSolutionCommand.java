package hotmath.gwt.shared.server.service.command;

import hotmath.HotMathException;
import hotmath.HotMathLogger;
import hotmath.HotMathUtilities;
import hotmath.ProblemID;
import hotmath.gwt.shared.client.rpc.Action;
import hotmath.gwt.shared.client.rpc.Response;
import hotmath.gwt.shared.client.rpc.action.GetSolutionAction;
import hotmath.gwt.shared.client.util.CmRpcException;
import hotmath.gwt.shared.client.util.RpcData;
import hotmath.gwt.shared.server.service.ActionHandler;
import hotmath.solution.writer.SolutionHTMLCreatorIimplVelocity;
import hotmath.solution.writer.TutorProperties;
import hotmath.util.HMConnectionPool;
import hotmath.util.VelocityTemplateFromStringManager;
import hotmath.util.sql.SqlUtilities;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Return the raw HTML that makes up the solution
 * 
 * Use the uid to lookup if this solution has any ShowWork applied
 * 
 * Return RpcData with the following members: solutionHtml, hasShowWork
 * 
 */
public class GetSolutionCommand implements ActionHandler<GetSolutionAction, RpcData> {

    static SolutionHTMLCreatorIimplVelocity __creator;
    static TutorProperties __tutorProps = new TutorProperties();
    static {
        try {
            __creator = new SolutionHTMLCreatorIimplVelocity(__tutorProps.getTemplate(), __tutorProps.getTutor());
        } catch (HotMathException hme) {
            HotMathLogger.logMessage(hme, "Error creating solution creator: " + hme);
        }
    }

    @Override
    public RpcData execute(final Connection conn, GetSolutionAction action) throws Exception {
        try {

            String pid = action.getPid();
            int uid = action.getUid();
            
            String solutionHtml = __creator.getSolutionHTML(__tutorProps, pid).getMainHtml();

            ProblemID ppid = new ProblemID(pid);
            String path = ppid.getSolutionPath_DirOnly("solutions");
            solutionHtml = HotMathUtilities.makeAbsolutePaths(path, solutionHtml);

            Map<String, String> map = new HashMap<String, String>();
            map.put("solution_html", solutionHtml);
            map.put("pid", pid);

            InputStream is = getClass().getResourceAsStream("tutor_wrapper.vm");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String tutorWrapper = null;
            StringBuilder sb = new StringBuilder();
            while ((tutorWrapper = br.readLine()) != null) {
                sb.append(tutorWrapper);
            }
            tutorWrapper = sb.toString();

            solutionHtml = VelocityTemplateFromStringManager.getInstance().processTemplate(tutorWrapper, map);

            RpcData rpcData = new RpcData();
            rpcData.putData("solutionHtml", solutionHtml);
            rpcData.putData("hasShowWork", getHasShowWork(conn,uid, pid) ? 1 : 0);
            // solutionHtml = "<b><img src='images/logo_1.gif'/>TEST 1</b>";
            return rpcData;
        } catch (Exception e) {
            e.printStackTrace();
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
        try {
            String sql = "select count(*) as cnt from HA_TEST_RUN_WHITEBOARD " + " where user_id = ? and pid = ?";
            pstat = conn.prepareStatement(sql);

            pstat.setInt(1, uid);
            pstat.setString(2, pid);

            ResultSet rs = pstat.executeQuery();
            rs.first();
            int cnt = rs.getInt("cnt");
            return cnt > 0;
        } finally {
            SqlUtilities.releaseResources(null, pstat, null);
        }
    }    

}
