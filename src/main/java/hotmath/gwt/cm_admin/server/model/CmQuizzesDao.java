package hotmath.gwt.cm_admin.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.gwt.shared.client.model.QuizQuestion;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/** try to centralize quiz db operations
 * 
 * @author casey
 *
 */
public class CmQuizzesDao {

    public CmList<QuizQuestion> getQuestionsFor(final Connection conn, String lesson) throws Exception {
        CmList<QuizQuestion> list = new CmArrayList<QuizQuestion>();
        
        PreparedStatement ps=null;
        try {
            String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_LESSON_QUIZZES");
            ps = conn.prepareStatement(sql);
            ps.setString(1, lesson);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                list.add(new QuizQuestion(lesson, rs.getString("guid")));
            }
        } finally  {
            SqlUtilities.releaseResources(null, ps,null);
        }
        return list;        
    }
}
