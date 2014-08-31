package hotmath.testset.ha;

import hotmath.HotMathException;
import hotmath.SolutionManager;
import hotmath.assessment.Range;
import hotmath.assessment.RppWidget;
import hotmath.concordance.ConcordanceEntry;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;



import sb.util.SbUtilities;

/** Create lesson/pid lookup table.  
 * 
 *  Based on inmh_assessment which the authors use
 *  to associated lessons with pids.  
 *  
 *  It is defined in all.inmh_assessment stored in
 *  hotmath:/inmh_assessment
 *  
 *  
 * @author casey
 *
 */
public class InmhAssessmentLookupBuilder {
	
	Logger logger = Logger.getLogger(InmhAssessmentLookupBuilder.class);
	private StringBuffer _logResults;

	/**
	 * Force the building of the inmh_assessment lookup table
	 * 
	 * @throws Exception
	 */
	public StringBuffer buildLookupTable() throws Exception {
		Connection conn = null;
		
		_logResults = new StringBuffer();

		logger.info(addLog("Building inmh_assessment_lookup table..."));
		try {
			conn = HMConnectionPool.getConnection();

			Statement stmt = conn.createStatement();
			stmt.executeUpdate("drop table if exists inmh_assessment_lookup");
			stmt.executeUpdate("create table inmh_assessment_lookup(file varchar(250) not null, pid varchar(200) not null, primary key(file, pid))");

			PreparedStatement psAdd = null;
			try {
				psAdd = conn.prepareStatement("insert into inmh_assessment_lookup(file, pid)values(?,?)");
				buildAssignmentMap(conn, psAdd);
			} finally {
				SqlUtilities.releaseResources(null, psAdd, null);
			}

		} finally {
			SqlUtilities.releaseResources(null, null, conn);

			logger.info(addLog("inmh_assessment_lookup table complete!"));
		}
		
		return _logResults;
	}
	
	private String addLog(String msg) {
		return addLog(msg, null);
	}
	
	private String addLog(String msg, Throwable e) {
		_logResults.append(msg += "\n");
		if(e != null) {
		   _logResults.append(e.getLocalizedMessage());	
		}
		return msg;
	}
	
	private void buildAssignmentMap(Connection conn, PreparedStatement psAdd)
			throws Exception {
		String logTag = "assignment map";

		// SQL to get list of ranges that match each INMH item
		String sql = "select distinct file from inmh_assessment i order by file";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String file = rs.getString("file");
				try {
					logger.info(addLog("Processing file: " + file));
					processSingleFile(conn, psAdd, file);
				} catch (Exception ex) {
					logger.error("Processing single file: " + file, ex);
				}
			}
		} catch (Exception e) {
			logger.info(addLog("Error getting assignment map", e));
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
			logger.debug("finished getting solution pool " + logTag);
		}
	}

	private boolean processSingleFile(Connection conn, PreparedStatement psAdd,
			String file) throws Exception {
		boolean canCache = false;
		String sql = "select `range` from inmh_assessment where file = ? order by id";
		PreparedStatement ps = null;

		List<RppWidget> widgets = new ArrayList<RppWidget>();
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, file);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {

				Range range = new Range(rs.getString("range"));

				// cmextrasalg2,+,factoring.part5-type1,1
				// cmextrasalg2_factoring_part5-type1_1_1_1
				// range = new Range("cmextrasalg2,+,factoring.part5-type1,1");

				if (range.getRange() == null || range.getRange().length() == 0)
					continue;

				if (range.getRange().startsWith("{")) {

					/**
					 * there is just one
					 * 
					 */
					RppWidget rpa = new RppWidget(range.getRange());
					rpa.getGradeLevels().addAll(range.getGradeLevels());

					/** ignore Flash RPAs */
					if (!rpa.isFlashRequired()) {

						widgets.add(rpa);

						addOneFile(psAdd, file, rpa.getFile());

					} else {
						logger.info(addLog("Flash RPAs ignored: " + rpa));
					}

				} else {
					List<String> rangePids = null;

					// contains a random range
					if (range.getRange().contains("[")) {

						logger.debug("Generating random range: " + range);
						rangePids = findSolutionsInRandomRange(conn,
								range.getRange());
						if (rangePids.size() == 0) {
							logger.info(addLog("No random problems found: " +  this + ", " + range.getRange()));
						}

						canCache = false;
					}

					logger.debug("find solutions in range");

					List<String> related = null;
					if (rangePids != null) {
						related = rangePids;
					} else {
						related = findSolutionsMatchingRange(conn,
								range.getRange());
					}
					logger.debug("finished finding solutions in range");
					for (String s : related) {
						RppWidget widget = new RppWidget(s);
						widget.getGradeLevels().addAll(range.getGradeLevels());
						if (!widgets.contains(widget)) {
							if (SolutionManager.getInstance()
									.doesSolutionExist(conn, widget.getFile())) {
								widgets.add(widget);

								addOneFile(psAdd, file, widget.getFile());

							} else {
								logger.debug("Inmh: PID does not exist: " + s);
							}
						} else {
							logger.info(addLog(String.format(
									"Duplicate RPP %s for item '%s'", widget,
									this)));
							// logger.info(msg);
						}
					}
				}
			}

			
			logger.info(addLog("problems: " + widgets.size()));
			
			return canCache;

		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
	}

	private void addOneFile(PreparedStatement psAdd, String file, String pid)
			throws Exception {
		psAdd.setString(1, file);
		psAdd.setString(2, pid);
		psAdd.executeUpdate();
	}

	private String lookupActivePid(Connection conn, String book,
			String chapter, String section, String problemSet, int problemNumber)
			throws Exception {
		String sql = "select problemnumber, problemindex " + "from SOLUTIONS "
				+ "where booktitle = ?" + "and chaptertitle = ? "
				+ "and sectiontitle = ?" + "and problemnumber = ? "
				+ "and active = 1";
		PreparedStatement ps = null;
		try {
			ps = conn.prepareStatement(sql);
			ps.setString(1, book);
			ps.setString(2, chapter);
			ps.setString(3, section);
			ps.setString(4, problemNumber + "");

			logger.debug("lookupActivePid: " + ps);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String pid = rs.getString("problemindex");
				return pid;
			}
		} catch (Exception e) {
			throw e;
		} finally {
			SqlUtilities.releaseResources(null, ps, null);
		}
		return null;
	}

	private List<String> findSolutionsMatchingRange(final Connection conn,
			String range) throws Exception {
		ConcordanceEntry con = new ConcordanceEntry(conn, range);
		return (List<String>) Arrays.asList(con.getGUIDs());
	}

	private List<String> findSolutionsInRandomRange(final Connection conn,
			String range) throws Exception {
		try {
			// range="test,casey,1.1,[1-5-2]";
			List<String> pids = new ArrayList<String>();
			if (!range.contains("[")) {
				pids.add(range);
			} else {

				logger.debug("Parsing random range: " + range);

				String pieces[] = range.split(",");

				String s = range.substring(range.indexOf("[") + 1,
						range.indexOf("]"));
				String spieces[] = s.split("-");
				int start = SbUtilities.getInt(spieces[0]);
				int end = SbUtilities.getInt(spieces[1]);
				int numToCreate = SbUtilities.getInt(spieces[2]);

				int maxAttempts = numToCreate * 5;
				List<Integer> added = new ArrayList<Integer>();
				while (maxAttempts-- > -1) {

					// get random selection
					int problemNumberToTry = SbUtilities.getRandomNumber(end
							- (start - 1));

					// get the active associated pid
					String book = pieces[0];
					String problemSet = pieces[1];

					String[] s2 = pieces[2].split("\\.");
					String chapter = s2[0];
					String section = null;
					if (s2.length > 1) {
						section = s2[1];
					} else {
						throw new HotMathException("Invalid range specified: "
								+ range);
					}

					problemNumberToTry += start;

					logger.debug("trying random problem: " + problemNumberToTry);

					// only if not already read
					if (!added.contains(problemNumberToTry)) {
						added.add(problemNumberToTry);
						String activePid = lookupActivePid(conn, book, chapter,
								section, problemSet, problemNumberToTry);

						logger.debug("Read random pid: " + activePid);

						if (activePid != null) {
							pids.add(activePid);
							numToCreate--;

							if (numToCreate == 0) {
								break;
							}
						}
					}
				}
			}

			return pids;
		} catch (Exception e) {
			logger.error("Error processing range: " + range, e);
			throw e;
		}
	}
}
