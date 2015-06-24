package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
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
    	//if (purchaseOrder != null) return;
        final String sql = CmMultiLinePropertyReader.getInstance().getProperty("PURCHASE_ORDER_CREATE");
        int count = getJdbcTemplate().update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, purchaseOrder.getSchool().name);
                ps.setString(2, purchaseOrder.getSchool().loginName);
                ps.setString(3, purchaseOrder.getSchool().address.department);
                ps.setString(4, purchaseOrder.getSchool().address.city);
                ps.setString(5, purchaseOrder.getSchool().address.state);
                ps.setString(6, purchaseOrder.getSchool().address.zipCode);

                ps.setString(7, " "); // blank first_name for school CC
                ps.setString(8, purchaseOrder.getPayment().cardholder); // put school CC holder in last_name
                ps.setString(9, purchaseOrder.getPayment().type);
                ps.setString(10, purchaseOrder.getPayment().lastFourCC);
                ps.setString(11, purchaseOrder.getPayment().expirationMonthCC);
                ps.setString(12, purchaseOrder.getPayment().expirationYearCC);
                ps.setString(13, purchaseOrder.getPayment().transactionIdCC);
                ps.setInt(14, purchaseOrder.getPayment().isSuccess?1:0);

                ps.setString(15, purchaseOrder.getPayment().address.street1);
                ps.setString(16, purchaseOrder.getPayment().address.city);
                ps.setString(17, purchaseOrder.getPayment().address.state);
                ps.setString(18, purchaseOrder.getPayment().address.zipCode);

                ps.setString(19, purchaseOrder.getPayment().checkNumber);
                ps.setString(20, purchaseOrder.getPayment().poNumber);
                ps.setString(21, purchaseOrder.getSalesZone());

                ps.setString(22, purchaseOrder.getContact().name);
                ps.setString(23, purchaseOrder.getContact().title);
                ps.setString(24, purchaseOrder.getContact().email);
                ps.setString(25, purchaseOrder.getContact().phone);
                ps.setString(26, purchaseOrder.getContact().alternateContact);

                ps.setInt(27, purchaseOrder.getLicense().numStudents);
                ps.setInt(28, purchaseOrder.getLicense().numYears);
                ps.setDouble(29, purchaseOrder.getLicense().total);

                ps.setInt(30, purchaseOrder.getAddlSchools().numSchools);
                ps.setInt(31, purchaseOrder.getAddlSchools().feePerSchool);
                ps.setDouble(32, purchaseOrder.getAddlSchools().total);

                ps.setInt(33, purchaseOrder.getProfDevl().numDays);
                ps.setInt(34, purchaseOrder.getProfDevl().feePerDay);
                ps.setDouble(35, purchaseOrder.getProfDevl().total);

                ps.setDouble(36, purchaseOrder.getTotal());
                ps.setTimestamp(37, new Timestamp(purchaseOrder.orderDate.getTime()));
                return ps;
            }
        });
    }

}
