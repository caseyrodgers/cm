package hotmath.cm.server.model;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.spring.SpringManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
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
                ps.setDouble(28, purchaseOrder.getLicense().numYears);
                ps.setDouble(29, purchaseOrder.getLicense().total);

                ps.setInt(30, purchaseOrder.getAddlSchools().numSchools);
                ps.setDouble(31, purchaseOrder.getAddlSchools().feePerSchool);
                ps.setDouble(32, purchaseOrder.getAddlSchools().total);

                ps.setInt(33, purchaseOrder.getProfDevl().numDays);
                ps.setDouble(34, purchaseOrder.getProfDevl().feePerDay);
                ps.setDouble(35, purchaseOrder.getProfDevl().total);

                ps.setDouble(36, purchaseOrder.getTotal());
                ps.setTimestamp(37, new Timestamp(purchaseOrder.orderDate.getTime()));
                return ps;
            }
        });
    }

    public List<CmPurchaseOrder> getAll() throws Exception {
    	String sql = CmMultiLinePropertyReader.getInstance().getProperty("GET_ALL_PURCHASE_ORDERS");
    	List<CmPurchaseOrder> ppList = null;
    	try {
    		ppList = this.getJdbcTemplate().query(
    				sql,
    				new RowMapper<CmPurchaseOrder>() {
    					public CmPurchaseOrder mapRow(ResultSet rs, int rowNum) throws SQLException {
    						CmPurchaseOrder po = new CmPurchaseOrder();
    		                po.getSchool().name = rs.getString("school_name");
    		                po.getSchool().loginName = rs.getString("login_name");
    		                po.getSchool().address.department = rs.getString("school_department");
    		                po.getSchool().address.city = rs.getString("school_city");
    		                po.getSchool().address.state = rs.getString("school_state");
    		                po.getSchool().address.zipCode = rs.getString("school_zip");
    		                po.getPayment().cardholder = rs.getString("cc_last_name");
    		                po.getPayment().type = rs.getString("cc_type");
    		                po.getPayment().lastFourCC = rs.getString("cc_last_four");
    		                po.getPayment().expirationMonthCC = rs.getString("cc_expire_month");
    		                po.getPayment().expirationYearCC = rs.getString("cc_expire_year");
    		                po.getPayment().transactionIdCC = rs.getString("cc_transaction_id");
    		                po.getPayment().address.street1 = rs.getString("cc_street");
    		                po.getPayment().address.city = rs.getString("cc_city");
    		                po.getPayment().address.state = rs.getString("cc_state");
    		                po.getPayment().address.zipCode = rs.getString("cc_zip");
    		                po.getPayment().checkNumber = rs.getString("check_number");
    		                po.getPayment().poNumber = rs.getString("po_number");
    		                po.setSalesZone(rs.getString("sales_zone"));
    		                po.getContact().name = rs.getString("instructor_name");
    		                po.getContact().title = rs.getString("instructor_title");
    		                po.getContact().email = rs.getString("instructor_email");
    		                po.getContact().phone = rs.getString("instructor_phone");
    		                po.getContact().alternateContact = (rs.getString("instructor_alt_contact"));
    		                po.getLicense().numStudents = rs.getInt("number_students");
    		                po.getLicense().numYears = rs.getDouble("number_years");
    		                po.getLicense().total = rs.getDouble("license_total");
    		                po.getAddlSchools().numSchools = rs.getInt("number_addl_schools");
    		                po.getAddlSchools().feePerSchool = rs.getDouble("fee_per_addl_school");
    		                po.getAddlSchools().total = rs.getDouble("addl_school_fee_total");
    		                po.getProfDevl().numDays = rs.getInt("number_prof_dev_days");
    		                po.getProfDevl().feePerDay = rs.getDouble("fee_per_prof_dev_day");
    		                po.getProfDevl().total = rs.getDouble("prof_dev_fee_total");
    		                po.setTotal(rs.getDouble("order_total"));
    		                po.setOrderDate(rs.getDate("order_date"));

    						return po;
    					}
    				});
    	}
    	catch(Exception e) {
    		__logger.error("Error getting Purchase Orders", e);
    		throw new Exception(e.getMessage());
    	}
    	return ppList;
    }

}
