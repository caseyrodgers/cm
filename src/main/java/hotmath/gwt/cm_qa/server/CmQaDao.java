package hotmath.gwt.cm_qa.server;

import hotmath.gwt.cm_rpc.client.model.CategoryModel;
import hotmath.gwt.cm_rpc.client.model.QaEntryModel;
import hotmath.gwt.cm_rpc.client.rpc.CmArrayList;
import hotmath.gwt.cm_rpc.client.rpc.CmList;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;

public class CmQaDao {

	public CmList<QaEntryModel> getQaItems(final Connection conn,
			String category) throws Exception {

		PreparedStatement ps = null;
		try {
			String sql = "select category, item, description, verified_time, is_problem "
					+ "from QA_ITEM i "
					+ " where i.category like ? "
					+ "order by load_order, item";
			ps = conn.prepareStatement(sql);

			if (category == null) {
				category = "NOT_SPECIFIED";
			}
			ps.setString(1, category.equals("all") ? "%" : category);
			ResultSet rs = ps.executeQuery();

			CmList<QaEntryModel> items = new CmArrayList<QaEntryModel>();
			while (rs.next()) {
				boolean verified = rs.getDate("verified_time") != null;
				boolean isProblem = rs.getInt("is_problem") != 0;
				items.add(new QaEntryModel(rs.getString("item"), rs
						.getString("description"), verified, isProblem));
			}
			return items;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	public CmList<CategoryModel> getQaCategories(final Connection conn)
			throws Exception {

		PreparedStatement ps = null;
		try {
			String sql = "select * from QA_CATEGORY ORDER BY category_name";
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			CmList<CategoryModel> items = new CmArrayList<CategoryModel>();
			while (rs.next()) {
				items.add(new CategoryModel(rs.getString("category_name")));
			}
			return items;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	public boolean saveQaItem(final Connection conn, String userName,
			String item, boolean verified, boolean isProblem) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "update QA_ITEM set verified_by = ?, verified_time = ?, is_problem = ? where item = ?";
			ps = conn.prepareStatement(sql);

			ps.setString(1, userName);
			if (verified) {
				ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			} else {
				ps.setNull(2, Types.DATE);
			}
			ps.setInt(3, isProblem ? 1 : 0);
			ps.setString(4, item);

			int cnt = ps.executeUpdate();

			return cnt == 1;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	public boolean saveQaItemProblem(final Connection conn, String userName,
			String item, String problem) throws Exception {

		saveQaItem(conn, userName, item, false, true);

		PreparedStatement ps = null;
		try {
			String sql = "insert into QA_ITEM_PROBLEM(qa_item, problem_date, problem_description, user_name)values(?,now(),?,?)";
			ps = conn.prepareStatement(sql);
			ps.setString(1, item);
			ps.setString(2, problem);
			ps.setString(3, userName);
			int cnt = ps.executeUpdate();
			return cnt == 1;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	public void updateQaItem(final Connection conn, String item,
			String description) throws Exception {
		PreparedStatement ps = null;
		try {
			String sql = "update QA_ITEM set description = ? where item = ? ";
			ps = conn.prepareStatement(sql);
			ps.setString(1, description);
			ps.setString(2, item);
			int cnt = ps.executeUpdate();
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}
}
