package hotmath.gwt.solution_editor.server;

import hotmath.HotMathException;
import hotmath.HotMathLogger;
import hotmath.HotMathProperties;
import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.service.SolutionDef;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.gwt.solution_editor.client.SolutionSearchModel;
import hotmath.gwt.solution_editor.server.solution.TutorSolution;
import hotmath.solution.StaticWriter;
import hotmath.solution.writer.SolutionHTMLCreatorIimplVelocity;
import hotmath.solution.writer.TutorProperties;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import sb.util.MD5;



public class CmSolutionManagerDao {
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
                createNewSolution(conn, pid);
            }
            
            String sql = "update SOLUTIONS set local_edit = 1, solutionxml = ?, active = ?  where problemindex = ?";
            ps = conn.prepareStatement(sql);
            ps.setString(1, xml);
            ps.setInt(2, isActive?1:0);
            ps.setString(3, pid);
            if(ps.executeUpdate() != 1)
                throw new Exception("Could not save solution xml: " + pid);

            
            /** Save tutor define into SOLUTION_DYNAMIC if not null
             * 
             */
            if(tutorDefine == null || tutorDefine.length() == 0) {
                PreparedStatement psDeleteContext = null;
                try {
                    psDeleteContext = conn.prepareStatement("delete from SOLUTION_DYNAMIC where pid = ?");
                    psDeleteContext.execute();
                }
                finally {
                    SqlUtilities.releaseResources(null, psDeleteContext,null);
                }
                
            }
            else {
                PreparedStatement psUpdateContext=null;
                try {
                    psUpdateContext = conn.prepareStatement("update SOLUTION_DYNAMIC set tutor_define = ? where pid = ?");
                    psUpdateContext.setString(1,tutorDefine);
                    psUpdateContext.setString(2, pid);
                    int cntUpdated = psUpdateContext.executeUpdate();
                    if(cntUpdated==0) {
                        PreparedStatement psInsertContext = conn.prepareStatement("insert into SOLUTION_DYNAMIC(pid, tutor_define)values(?,?)");
                        try {
                            psInsertContext.setString(1, pid);
                            psInsertContext.setString(2, tutorDefine);
                            int cntInserted = psInsertContext.executeUpdate();
                            if(cntInserted != 1) {
                                throw new CmException("Could not insert into SOLUTION_DYNAMIC");
                            }
                        }
                        finally {
                            SqlUtilities.releaseResources(null,  psInsertContext,  null);
                        }
                    }
                }
                finally {
                    SqlUtilities.releaseResources(null,  psUpdateContext, null);
                }
            }
            
            
            String outputBase = CatchupMathProperties.getInstance().getSolutionBase() + HotMathProperties.getInstance().getStaticSolutionsDir();
            
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
        return createNewSolution(conn,"test_chap0_s-new_ps-new_" + "pb-" + System.currentTimeMillis() + "_1");
    }
    public String createNewSolution(final Connection conn, String newSolutionPid) throws Exception {
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
            ps.setString(8,solution.getCreateNewXml(createdBy));
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
        
        if(searchFullText == null)
            searchFullText="";
        PreparedStatement ps=null;
        try {
            String sql = "select problemindex,active from SOLUTIONS where problemindex like ? and solutionXML like '%" + searchFullText + "%' ";
            
            if(!includeInactive) {
                sql += " and active = 1 ";
            }
            ps = conn.prepareStatement(sql);
            ps.setString(1, searchFor + "%");
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
            TutorSolution ts = TutorSolution.parse(rs.getString("solutionxml"));
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
    
    
    static SolutionHTMLCreatorIimplVelocity __creator;
    static TutorProperties __tutorProps = new TutorProperties();
    static {
        try {
            __creator = new SolutionHTMLCreatorIimplVelocity(__tutorProps.getTemplate(), __tutorProps.getTutor());
        } catch (HotMathException hme) {
            HotMathLogger.logMessage(hme, "Error creating solution creator: " + hme);
        }
    }
}

