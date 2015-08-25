package hotmath.cm.util;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;

import sb.client.SbTesterFrameGeneric;
import sb.util.SbException;
import sb.util.SbTestImpl;
import sb.util.SbUtilities;

/** Identify skipped quizzes by looking at meta data
 * 
 * @author casey
 *
 */
public class FindBug_SkippedQuizzesGui {
    
    static public void main(String as[]) {
        
        SbTesterFrameGeneric tester = new SbTesterFrameGeneric(new SbTestImpl() {
            @Override
            public void doTest(Object objLogger, String sFromGUI) throws SbException {
                
                int adminId = SbUtilities.getInt(sFromGUI);
                if(adminId == 0) {
                    return;
                }

                Connection conn=null;
                try {
                    conn = HMConnectionPool.getConnection();
                    new FindBug_SkippedQuizzes().findSkippedQuizzes(conn, adminId);
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
                finally {
                    SqlUtilities.releaseResources(null,null,conn);
                }
            }
        });
}

}
