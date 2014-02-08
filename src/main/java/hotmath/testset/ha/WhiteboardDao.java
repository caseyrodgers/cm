    package hotmath.testset.ha;

import hotmath.cm.util.CmWebResourceManager;
import hotmath.cm.util.CompressHelper;
import hotmath.crypto.Base64;
import hotmath.gwt.cm_core.client.model.WhiteboardModel;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc.client.rpc.WhiteboardCommand;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.spring.SpringManager;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import sb.util.MD5;

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

    /** Save the whiteboard image as a template for this admin
     * 
     * @param uid
     * @param name
     * @param dataUrl
     * @throws Exception
     */
    public void saveWhiteboardAsTemplate(final int uid, final String name, String dataUrl) throws Exception {
        int comma = dataUrl.indexOf(",");
        String base64Data = dataUrl.substring(comma+1);
        byte[] data = Base64.decode(base64Data);
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
        
        File directory = new File(CmWebResourceManager.getInstance().getFileBase() + "/../help/whiteboard_template/" + uid);
        if(!directory.exists()) {
            directory.mkdirs();
        }
        
        String fileName = MD5.getMD5(name) + ".png";
        File fullFile = new File(directory, fileName);
        
        ImageIO.write(bufferedImage, "png", fullFile);
        
        final Integer cntMatch = getJdbcTemplate().queryForObject("select count(*) as cnt from CM_WHITEBOARD_TEMPLATE where uid = ? and template_name = ?", new Object[] { uid, name }, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("cnt");
            }
        });

        if(cntMatch == 0) {
            final String url = "/help/whiteboard_template/" + uid + "/" + fileName;
            getJdbcTemplate().update(new PreparedStatementCreator() {
                @Override
                public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                    String sql = "insert into CM_WHITEBOARD_TEMPLATE(uid, template_name, template_path)values(?,?,?)";                
                    PreparedStatement ps = con.prepareStatement(sql);
                    ps.setInt(1, uid);
                    ps.setString(2, name);
                    ps.setString(3,url);
                    return ps;
                }
            });
        }
        
        createThumbnail(fullFile);
    }

    public String getWhiteboardTemplates(int adminId) {
        String sql = "select * from CM_WHITEBOARD_TEMPLATE where uid = ? order by template_name";

        List<String> list = getJdbcTemplate().query(sql, new Object[] { adminId}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getString("template_path"); 
            }
        });

        String files = "";
        for(String f: list) {
            if(files.length() > 0) {
                files += ", ";
            }
            
            if(!f.startsWith("/")) {
                f = "/" + f;
            }
            files += "\"" + f + "\"";
        }
        
        String json = "{" +
                "\"type\":\"img\"," +
                "\"path\":\"\"," +
                "\"icon\":\"tn\"," +
                "\"list\":["+ files + "]" + 
                "}";
        
        return json;
    }
    
    
    
    private void createThumbnail(File imageFile) throws Exception {
        BufferedImage img = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
        img.createGraphics().drawImage(ImageIO.read(imageFile).getScaledInstance(1000, 1000, Image.SCALE_SMOOTH),0,0,null);
        BufferedImage newBufferedImage = new BufferedImage(img.getWidth(),  img.getHeight(), BufferedImage.TYPE_INT_RGB);
        newBufferedImage.createGraphics().drawImage(img, 0, 0, Color.WHITE, null);
        

        int idx = imageFile.getName().indexOf(".png");
        if(idx > -1) {
            File tnFile = new File(imageFile.getParentFile(), imageFile.getName().substring(0, idx) + "-tn.png");
            ImageIO.write(newBufferedImage, "jpg", tnFile);        
        }
    }
}
