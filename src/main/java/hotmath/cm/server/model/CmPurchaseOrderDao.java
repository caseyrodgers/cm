package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.payment.PaymentResult;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

/**
 * 
 * @author bob
 * 
 */

public class CmPurchaseOrderDao extends SimpleJdbcDaoSupport {

    static Logger __logger = Logger.getLogger(CmPurchaseOrderDao.class);

    static private CmPurchaseOrderDao __instance;

    static public CmPurchaseOrderDao getInstance() throws Exception {
        if (__instance == null) {
            __instance = (CmPurchaseOrderDao) SpringManager.getInstance().getBeanFactory()
                    .getBean(CmPurchaseOrderDao.class.getName());
        }
        return __instance;
    }

    private CmPurchaseOrderDao() {
        /** empty */
    }


    public void create(final CmPurchaseOrder purchaseOrder)  throws Exception {
        final String sql = CmMultiLinePropertyReader.getInstance().getProperty("PURCHASE_ORDER_CREATE");
        getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, purchaseOrder.getSchool().name);
                ps.setString(2, purchaseOrder.getSchool().address.street);
                ps.setString(3, purchaseOrder.getSchool().address.city);
                ps.setString(4, purchaseOrder.getSchool().address.state);
                ps.setString(5, purchaseOrder.getSchool().address.zipCode);

                ps.setString(6, purchaseOrder.getPayment().cardholder);
                ps.setString(7, purchaseOrder.getPayment().type);
                ps.setString(8, purchaseOrder.getPayment().lastFourCC);
                ps.setString(9, purchaseOrder.getPayment().expirationMonthCC);
                ps.setString(10, purchaseOrder.getPayment().expirationMonthCC);
                ps.setString(11, purchaseOrder.getPayment().transactionIdCC);
                ps.setInt(12, purchaseOrder.getPayment().isSuccess?1:0);

                ps.setString(13, purchaseOrder.getPayment().address.street);
                ps.setString(14, purchaseOrder.getPayment().address.city);
                ps.setString(15, purchaseOrder.getPayment().address.state);
                ps.setString(16, purchaseOrder.getPayment().address.zipCode);

                ps.setString(17, purchaseOrder.getPayment().checkNumber);
                ps.setString(18, purchaseOrder.getPayment().poNumber);

                ps.setString(19, purchaseOrder.getContact().name);
                ps.setString(20, purchaseOrder.getContact().title);
                ps.setString(21, purchaseOrder.getContact().phone);
                ps.setString(22, purchaseOrder.getContact().alternateContact);

                ps.setInt(23, purchaseOrder.getLicense().numStudents);
                ps.setInt(24, purchaseOrder.getLicense().numYears);
                ps.setDouble(25, purchaseOrder.getLicense().total);
                ps.setTimestamp(26, new Timestamp(purchaseOrder.getLicense().startDate.getTime()));

                ps.setInt(27,  purchaseOrder.getAddlSchools().numSchools);
                ps.setInt(28,  purchaseOrder.getAddlSchools().feePerSchool);
                ps.setInt(29,  purchaseOrder.getAddlSchools().total);

                ps.setInt(30,  purchaseOrder.getProfDevl().numDays);
                ps.setInt(31,  purchaseOrder.getProfDevl().feePerDay);
                ps.setInt(32,  purchaseOrder.getProfDevl().total);

                ps.setInt(33, purchaseOrder.getTotal());
                ps.setString(34, purchaseOrder.getSalesZone());
                return ps;
            }
        });
    }

}
