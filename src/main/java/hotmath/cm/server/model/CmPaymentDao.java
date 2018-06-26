package hotmath.cm.server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import org.apache.log4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcDaoSupport;

import com.google.gson.Gson;

import hotmath.cm.util.CmMultiLinePropertyReader;
import hotmath.payment.PaymentResult;
import hotmath.spring.SpringManager;

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
		 org.slf4j.LoggerFactory lf;
		
		return __instance;
	}

	private CmPaymentDao() {
		/** empty */
	}

	public void create(final PaymentResult payment, final int studentUid, final double amount) throws Exception {
		final String sql = CmMultiLinePropertyReader.getInstance().getProperty("PAYMENT_CREATE");
		getJdbcTemplate().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, payment.getOrderNumber());
				ps.setInt(2, studentUid);
				String amt = String.format("$%.2f", amount);
				ps.setString(3, amt);
				ps.setInt(4, payment.isSuccess() ? 1 : 0);
				return ps;
			}
		});
	}

	public CmPurchases getPurchases(int uid) throws Exception {
		final String sql = "select * from CM_RETAIL_PURCHASES where uid = " + uid + " order by purchase_time";

		List<CmPurchase> res = getJdbcTemplate().query(sql, new RowMapper<CmPurchase>() {
			public CmPurchase mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
				return new CmPurchase(rs.getString("purchase"), rs.getInt("free_program")==0?false:true);
			};
		});
		CmPurchases purchases = new CmPurchases(res);

		return purchases;
	}

	public static class PurchaseData {
		private String name;
		private Object results;
		private boolean freeProgram;

		public PurchaseData(String name, boolean freeProgram) {
			this.name = name;
			this.freeProgram = freeProgram;
		}

		public Object getResults() {
			return results;
		}

		public String getName() {
			return name;
		}
		
		public boolean isFreeProgram() {
			return freeProgram;
		}
		
		public void setFreeProgram(boolean freeProgram) {
			this.freeProgram = freeProgram;
		}
	}
	
	public class PurchasedDataRestore {
		String productId;
		String date;
		String transactionId;
		boolean freeProgram;
		int state;
	}

	public void addPurchase(final int userId, final PurchaseData purchaseData) {
		__logger.info("Adding purchase: " + userId + ", " + purchaseData.getName());
		try {
			final String sql = "insert into CM_RETAIL_PURCHASES(uid, purchase, purchase_data,free_program,purchase_time)values(?,?,?,?,now())";
			getJdbcTemplate().update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection conn) throws SQLException {
					PreparedStatement ps = conn.prepareStatement(sql);
					ps.setInt(1, userId);
					ps.setString(2, purchaseData.getName());
					if (purchaseData == null) {
						ps.setNull(3, Types.VARCHAR);
					} else {
						ps.setString(3, new Gson().toJson(purchaseData.getResults()));
					}
					
					ps.setInt(4,purchaseData.isFreeProgram()?-1:0);
					
					return ps;
				}
			});
		} catch (Exception e) {
			__logger.error("Error adding purchase", e);
		}
	}

	public void restorePurchases(int userId, PurchasedDataRestore[] purchases) throws Exception {
		for(PurchasedDataRestore pur: purchases) {
			
			PurchaseData purchaseData = new PurchaseData(pur.productId, pur.freeProgram);
			purchaseData.results = pur;
			
			/** will sync (silent error if already exist)
			 * 
			 */
			addPurchase(userId,  purchaseData);
		}
	}

}
