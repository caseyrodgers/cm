package hotmath.gwt.solution_editor.server;

import hotmath.HotMathDatabaseLoader;
import hotmath.HotMathProperties;
import hotmath.ProblemID;
import hotmath.SolutionManager;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc_core.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc_core.client.rpc.CmList;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;
import hotmath.solution.Solution;
import hotmath.solution.StaticWriter;
import hotmath.solution.writer.SolutionHTMLCreatorIimplVelocity;
import hotmath.solution.writer.TutorProperties;
import hotmath.util.sql.SqlUtilities;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import sb.util.MD5;

public class CmSolutionManagerDao {

	static Logger __logger = Logger.getLogger(CmSolutionManagerDao.class);

    static SolutionHTMLCreatorIimplVelocity __creator;
    static TutorProperties __tutorProps = new TutorProperties();
    public CmSolutionManagerDao() throws Exception {
        if(__creator == null) {
            // HotMathProperties.getInstance().getPropertiesObject().put("velocity.template.dir",  CatchupMathProperties.getInstance().getHotMathHome());
            __creator = new SolutionHTMLCreatorIimplVelocity(__tutorProps.getTemplate(), __tutorProps.getTutor()); // CatchupMathProperties.getInstance().getHotMathTutorTemplateDir());
        }
    }
    
    public String getSolutionXml(final Connection conn, String pid) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "select solutionxml from SOLUTIONS where problemindex = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, pid);
            ResultSet rs = ps.executeQuery();
            if(!rs.first())
                throw new Exception("No such solution : " + pid);
            return rs.getString("solutionxml");
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }

    public String getSolutionMd5(final Connection conn, String pid) throws Exception {
        String xml = getSolutionXml(conn, pid);
        return MD5.getMD5(xml);
    }

    public void saveSolutionXml(final Connection conn, String pid, String xml, String tutorDefine, boolean isActive) throws Exception {
        PreparedStatement ps=null;
        try {

            if(!solutionExists(conn, pid)) {
                createNewSolution(conn, pid, null);
            }
            else {
                makeSequentialBackup(conn,  pid);
            }

            String sql = "update SOLUTIONS set local_edit = 1, solutionxml = ?, active = ?  where problemindex = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, xml);
            ps.setInt(2, isActive?1:0);
            ps.setString(3, pid);
            if(ps.executeUpdate() != 1)
                throw new Exception("Could not save solution xml: " + pid);

            HotMathDatabaseLoader.processSolutionDefine(conn, pid, tutorDefine);

            String outputBase = CatchupMathProperties.getInstance().getSolutionBase() + HotMathProperties.getInstance().getStaticSolutionsDir();
            
            __logger.debug("Writing solution: " + pid + ", to: " + outputBase);

            StaticWriter.writeSolutionFile(conn,__creator, pid, __tutorProps, outputBase, false, null);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }

    /** return true if the named solution already exists
     *
     * @param conn
     * @param pid
     * @return
     * @throws Exception
     */
    public boolean solutionExists(final Connection conn, String pid) throws Exception {
        PreparedStatement ps = null;
        try {
            String sql = "select 'x' from SOLUTIONS where problemindex = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, pid);
            return ps.executeQuery().first();
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    /** Create a brand spanking new solutions with just the
     * problem set defined.
     *
     * @param conn
     * @throws Exception
     */
    public String createNewSolution(final Connection conn) throws Exception {
        return createNewSolution(conn,"test_chap0_s-new_ps-new_" + "pb-" + System.currentTimeMillis() + "_1", null);
    }

    public String createNewSolution(final Connection conn, String newSolutionPid, String problemStatement) throws Exception {
        PreparedStatement ps=null;
        try {
            String createdBy="auto";

            /** TODO: get meta data from command line
             *
             */

            /** create new problem
             *
             */
            SolutionDef solution = new SolutionDef(newSolutionPid);

            String sql =
                "INSERT INTO SOLUTIONS(LOCAL_EDIT, " +
                "      PROBLEMINDEX,BOOKTITLE,CHAPTERTITLE,SECTIONTITLE,PROBLEMSET,PROBLEMNUMBER," +
                "      PAGENUMBER,SOLUTIONXML,INPUTTER,CREATEDBY,CREATEDATE,ACTIVE)" +
                   "VALUES(1, ?,?,?,?,?,?,?,?,?,?,now(),'1')";

            ps = conn.prepareStatement(sql);
            ps.setString(1, newSolutionPid);
            ps.setString(2, solution.getBook());
            ps.setString(3, solution.getChapter());
            ps.setString(4, solution.getSection());
            ps.setString(5,solution.getProblemSet());
            ps.setString(6,solution.getProblemNumber());
            ps.setInt(7,solution.getPage());
            ps.setString(8,solution.getCreateNewXml(createdBy, problemStatement));
            ps.setString(9,createdBy);
            ps.setString(10,createdBy);

            if(ps.executeUpdate() != 1)
                throw new Exception("Could not create solution xml: " + newSolutionPid);

            String outputBase = CatchupMathProperties.getInstance().getSolutionBase() + HotMathProperties.getInstance().getStaticSolutionsDir();
            StaticWriter.writeSolutionFile(conn,__creator, newSolutionPid, __tutorProps, outputBase, false, null);

            return newSolutionPid;
        }
        finally {
            SqlUtilities.releaseResources(null,ps,null);
        }
    }

    public CmList<SolutionSearchModel> searchForSolutions(final Connection conn, String searchFor, String searchFullText, boolean includeInactive) throws Exception {
        CmList<SolutionSearchModel> list = new CmArrayList<SolutionSearchModel>();

        if(searchFullText == null) {
            searchFullText="";
        }
        if(searchFor == null) {
        	searchFor = "";
        }
        
        PreparedStatement ps=null;
        try {
            String sql = "select lower(problemindex),active from SOLUTIONS where problemindex like ? and solutionXML like ?";

            if(!includeInactive) {
                sql += " and active = 1 ";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, searchFor + "%");
            ps.setString(2, "%" + searchFullText + "%");
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new SolutionSearchModel(rs.getString(1), rs.getInt("active")==1?true:false));
            }
            return list;
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    public TutorSolution getTutorSolution(final Connection conn, String pid) throws Exception {
        PreparedStatement ps=null;
        try {
            String sql = "select s.problemindex, s.solutionxml, s.active, d.tutor_define " +
                         " from   SOLUTIONS s " +
                         " left join SOLUTION_DYNAMIC d on d.pid = s.problemindex " +
                         " where s.problemindex = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, pid);

            ResultSet rs = ps.executeQuery();
            if(!rs.first()) {
                throw new Exception("No such solution: " + pid);
            }
            TutorSolution ts = TutorSolution.parse(rs.getString("problemindex"), rs.getString("solutionxml"));
            ts.setActive(rs.getInt("active")==1?true:false);
            ts.setTutorDefine(rs.getString("tutor_define"));

            return ts;
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
    }

    public String formatXml(String xml) throws Exception {
        return new XmlFormatter().format(xml);
    }

    static public class SearchResult {
        public List<String> getErrors() {
            return errors;
        }
        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
        public int getReplaced() {
            return replaced;
        }
        public void setReplaced(int replaced) {
            this.replaced = replaced;
        }
        public SearchResult(List<String> errors, int replaced) {
            this.errors = errors;
            this.replaced = replaced;
        }
        List<String> errors;
        int replaced;
    }
    public SearchResult replaceTextInSolutions(Connection conn, List<SolutionSearchModel> pidsToReplace, String searchFor,
            String replaceWith) throws Exception {
        
        int replaced=0;
        List<String> errors = new ArrayList<String>();
        for(SolutionSearchModel pm: pidsToReplace) {
            String pid = pm.getPid();
            __logger.info("Replacing text in solution: " + pid);
            
            try {
                if(doReplace(conn, pid, searchFor, replaceWith)) {
                    replaced++;
                };
            }
            catch(Exception e) {
                e.printStackTrace();
                errors.add(e.getMessage());
            }
        }
        
        return new SearchResult(errors, replaced);
    }

    private boolean doReplace(Connection conn, String pid, String searchFor, String replaceWith) throws Exception {
        
        
        makeSequentialBackup(conn,  pid);
        
        String solutionXml = getSolutionXml(conn,  pid);
        
        String newSolutionXml = solutionXml.replaceAll("(?i)" + searchFor, replaceWith);
        
        if(solutionXml.equals(newSolutionXml)) {
            return false;
        }
        
        String sql = "update SOLUTIONS set solutionxml = ? where problemindex = ?";
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, newSolutionXml);
            ps.setString(2, pid);
            if(ps.executeUpdate()!=1) {
                __logger.warn("did not update record: " + pid);
            };
            

            String outputBase = CatchupMathProperties.getInstance().getSolutionBase() + HotMathProperties.getInstance().getStaticSolutionsDir();
            
            __logger.debug("Writing solution: " + pid + ", to: " + outputBase);

            StaticWriter.writeSolutionFile(conn,__creator, pid, __tutorProps, outputBase, false, null);      
            
            return true;
        }
        finally {
            SqlUtilities.releaseResources(null, ps, null);
        }
        
    }

    /** Create a backup of the current solutionXML
     * 
     * @param conn
     * @param pid
     */
    private void makeSequentialBackup(Connection conn, String pid) throws Exception {
        
        ProblemID pidO = new ProblemID(pid);

        String solutionDir = HotMathProperties.getInstance().getSoulutionImageDir() + "/" + pidO.getSolutionPath() + "/" + pid;
        
        String xml = getSolutionXml(conn, pid);
        
        File backupDir = new File(solutionDir, "backup");
        if(!backupDir.exists()) {
            backupDir.mkdirs();
        }
        File backupFile = new File(backupDir, System.currentTimeMillis() + ".xml");
        
        __logger.debug("Writing solution xml backup file: " + backupFile);
        FileWriter fileOut = null;
        try {
            fileOut = new FileWriter(backupFile);
            fileOut.write(xml);
            fileOut.close();
        }
        finally {
            fileOut.close();
        }
    }

}
