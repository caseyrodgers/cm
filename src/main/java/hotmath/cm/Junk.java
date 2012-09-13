package hotmath.cm;

import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Junk {

    PreparedStatement _ps;

    public Junk() throws Exception {

        String sql = "select id,comments from SUBSCRIBERS where comments like '%cc_email%'";
        Connection conn = null;
        try {
            conn = HMConnectionPool.getConnection();

            _ps = conn.prepareStatement("insert into SUBSCRIBERS_INFO(subscriber_id,type,value)values(?,'cc_email',?)");

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                extractCcEmails(conn, rs.getString("id"), rs.getString("comments"));
            }
        } finally {
            SqlUtilities.releaseResources(null, null, conn);
        }
    }

    private void extractCcEmails(Connection conn, String id, String comments) {
        try {
            String s[] = comments.split("[cC][cC]\\_[eE]mails[:=]");
            String ccEmails = s[1].split("[\\n\\)]")[0];
            for (String teacherEmail : ccEmails.split(",")) {
                _ps.setString(1, id);
                _ps.setString(2, teacherEmail.trim());
                if(_ps.executeUpdate() != 1) {
                    System.out.println("Could not save record ...?");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static public void main(String as[]) {
        try {
            new Junk();
            System.out.println("Complete!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.exit(0);
    }

}
