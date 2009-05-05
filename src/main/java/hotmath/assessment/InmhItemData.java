package hotmath.assessment;

import hotmath.ProblemID;
import hotmath.SolutionManager;
import hotmath.concordance.ConcordanceEntry;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Represents a single INMH item and all the PIDS that reference ie
 * 
 * @author Casey
 * 
 */
public class InmhItemData {

	INeedMoreHelpItem item;
	List<String> pidsReferenced = new ArrayList<String>();

	public InmhItemData(INeedMoreHelpItem item) {
		this.item = item;
	}

	/**
	 * Add a solution id that references this INMH item
	 * 
	 * @param pid
	 */
	public void addProblemIndex(String pid) {
		if (!pidsReferenced.contains(pid))
			pidsReferenced.add(pid);
	}

	/**
	 * Return a sorted map containing weighted values and the problem index.
	 * 
	 * entries are weight:solution
	 * 
	 * @return
	 */
	public List<String> getWeightedIndexes(int sumOfWeights,
			int totalNumSolsInPrescription) {
		List<String> values = new ArrayList<String>();
		for (String pid : pidsReferenced) {
			int weight = (getWeight() / sumOfWeights)
					* totalNumSolsInPrescription;
			values.add(String.format("%s:%s", weight, pid));
		}
		return values;
	}

	/**
	 * Get the weight of this InmhItem.
	 * 
	 * The weight is determined by how many times this item is referenced.
	 * 
	 * @return
	 */
	public int getWeight() {
		return pidsReferenced.size();
	}

	public INeedMoreHelpItem getInmhItem() {
		return item;
	}

	/**
	 * Return referencing solution ids
	 * 
	 * @return
	 */
	public List<String> getPids() {
		return pidsReferenced;
	}

	/**
	 * Return pool of solutions that can be used for this INMH item
	 * 
	 * @return
	 */
	public List<ProblemID> getWookBookSolutionPool() {

		List<ProblemID> pids = new ArrayList<ProblemID>();

		// SQL to get list of ranges that match each INMH item
		String sql = "select range " + " from   inmh_assessment i "
				+ " where  i.file = ? ";
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = HMConnectionPool.getConnection();
			ps = conn.prepareStatement(sql);
			ps.setString(1, this.item.getFile());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String range = rs.getString("range");
				if (range == null || range.length() == 0)
					continue;

				List<String> related = findSolutionsMatchingRange(range);
				for (String s : related) {
					ProblemID pid = new ProblemID(s);
					if (!pids.contains(pid)) {
						try {
							SolutionManager.getSolution(pid.getGUID());
							pids.add(pid);
						} catch (Exception e) {
							System.out.println("Inmh: GUID does not exist: "
									+ s);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SqlUtilities.releaseResources(null, ps, conn);
		}
		return pids;
	}

	/**
	 * Given a range, return all solutions matching the range
	 * 
	 * @param range
	 * @return
	 * @throws Exception
	 */
	private List<String> findSolutionsMatchingRange(String range)
			throws Exception {
		ConcordanceEntry con = new ConcordanceEntry(range);
		return (List<String>) Arrays.asList(con.getGUIDs());
	}
}
