package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.payment.PaymentResult;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * 
 * @author bob
 * 
 */

public class CmPaymentDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(CmPaymentDao.class);

    static private CmPaymentDao __instance;

    static public CmPaymentDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (CmPaymentDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(CmPaymentDao.class.getName());
        }
        return __instance;
    }

    private CmPaymentDao() {
        /** empty */
    }


    public void create(final PaymentResult payment, final int studentUid, final double amount)  throws Exception {
        final String sql = CmMultiLinePropertyReader.getInstance().getProperty("PAYMENT_CREATE");
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, payment.getOrderNumber());
                ps.setInt(2, studentUid);
                String amt = String.format("$%.2f", amount);
                ps.setString(3, amt);
                ps.setInt(4, payment.isSuccess()?1:0);
                return ps;
            }
        });
    }

}
