    package hotmath.testset.ha;

import hotmath.cm.util.CompressHelper;
import hotmath.gwt.cm_core.client.model.WhiteboardModel;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * Provide DAO functionality for Whiteboards
 * 
 * @author casey
 * 
 */
public class WhiteboardDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(WhiteboardDao.class);

    static private WhiteboardDao __instance;

    static public WhiteboardDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (WhiteboardDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(WhiteboardDao.class.getName());
        }
        return __instance;
    }

    private WhiteboardDao() {
        /** Empty */
    }

    /**
     * Provide standardized way to store whiteboard data
     * 
     * TODO: Move other whiteboard backend operations here.
     * 
     * @param adminId
     * @param pid
     * @param commandType
     * @param data
     */
    public void saveStaticWhiteboardData(final int teacherId, final String pid, CommandType commandType, String commandData) throws Exception {

        switch(commandType) {
        
            case DELETE:
                
                final int rowToDelete = Integer.parseInt(commandData);
    
                String sqlD = "select id from SOLUTION_WHITEBOARD where pid = ? order by id limit ?,1";
                final Integer whiteboardId = getJdbcTemplate().queryForObject(sqlD, new Object[] { pid, rowToDelete }, new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getInt("id");
                    }
                });
    
                int countDeleted = getJdbcTemplate().update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        String sqlD = "delete from  SOLUTION_WHITEBOARD where id = ?";
                        PreparedStatement ps = con.prepareStatement(sqlD);
                        ps.setInt(1, whiteboardId);
                        return ps;
                    }
                });
                break;
            
            case CLEAR:
                
                getJdbcTemplate().update(new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        String sql = "delete from SOLUTION_WHITEBOARD where pid = ?";
                        PreparedStatement ps = con.prepareStatement(sql);
                        ps.setString(1, pid);
                        return ps;
                    }
                });
                break;
                
                
                
                
            case UNDO:

                /**
                 * Delete the newest command
                 * 
                 */
                String sql = "select max(id) as whiteboard_id " + " from SOLUTION_WHITEBOARD " + " where pid = ?";
                final Integer maxWhiteboardId = getJdbcTemplate().queryForObject(sql, new Object[] { pid }, new RowMapper<Integer>() {
                    @Override
                    public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return rs.getInt(1);
                    }
                });
    
                if (maxWhiteboardId != null) {
                    getJdbcTemplate().update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            String sql = "delete from SOLUTION_WHITEBOARD where id = ?";
                            PreparedStatement ps = con.prepareStatement(sql);
                            ps.setInt(1, maxWhiteboardId);
                            return ps;
                        }
                    });
                }
                
                break;

                
            case DRAW:
                
                try {
                    byte[] inBytes = commandData.getBytes("UTF-8");
                    final byte[] outBytes = CompressHelper.compress(inBytes);
    
                    getJdbcTemplate().update(new PreparedStatementCreator() {
                        @Override
                        public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                            String sql = "insert into SOLUTION_WHITEBOARD(wb_id, pid, wb_command, wb_data, insert_time_mills) "
                                    + " values('wb_ps',?,?,?,?) ";
                            PreparedStatement ps = con.prepareStatement(sql);
                            ps.setString(1, pid);
                            ps.setString(2, "draw");
                            ps.setBytes(3, outBytes);
                            ps.setLong(4, System.currentTimeMillis());
    
                            return ps;
                        }
                    });
                } catch (UnsupportedEncodingException e) {
                    __logger.error("Error saving SOLUTION_WHITEBOARD data: + " + pid, e);
                    throw new Exception(e);
                }
                
                break;
                
                
            default: 
                throw new CmException("Unknown whiteboard command: " + commandType);
                
        }
    }
    
    
    /**
     * Return any defined static whiteboards for this problem
     * 
     * 
     * TODO: deal with multiple whiteboards per solution, currently only one assumed
     * 
     * @param pid
     * @return
     * @throws Exception
     */
    public List<WhiteboardModel> getStaticWhiteboards(String pid) throws Exception {
        String sql = "select * from SOLUTION_WHITEBOARD where pid = ? order by wb_id, id";
        List<WhiteboardCommand> list = getJdbcTemplate().query(sql, new Object[] { pid }, new RowMapper<WhiteboardCommand>() {
            @Override
            public WhiteboardCommand mapRow(ResultSet rs, int rowNum) throws SQLException {
                
                
                return new WhiteboardCommand(rs.getString("wb_command"), loadCommandData(rs.getBytes("wb_data")),false);
            }
        });
        
        List<WhiteboardModel> whiteboards = new ArrayList<WhiteboardModel>();
        if(list.size() > 0) {
            WhiteboardModel whiteboard = new WhiteboardModel("wb_ps", list);
            whiteboards.add(whiteboard);
        }
        return whiteboards;
    }
    
    
    private String loadCommandData(byte[] commandDataCompressed) throws SQLException {
        try {
            if (commandDataCompressed != null && commandDataCompressed.length > 0 &&  commandDataCompressed[0] != "{".getBytes("UTF-8")[0]) {
                return CompressHelper.decompress(commandDataCompressed);
            } else {
                return new String(commandDataCompressed);
            }
        } catch (Exception e) {
            throw new SQLException(e.getLocalizedMessage());
        }
    }
    

}
