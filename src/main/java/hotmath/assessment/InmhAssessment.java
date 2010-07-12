package hotmath.assessment;

import hotmath.concordance.ConcordanceEntry;
import hotmath.gwt.shared.client.util.CmException;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpManager;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/** Lookup related items based on the INMH items
 *  of a given list of problem ids.
 *  
 *  creates a union of all inmh help items
 *  for the given list pids
 *  
 * @author Casey
 *
 */
public class InmhAssessment {
	
	private static final Logger logger = Logger.getLogger(InmhAssessment.class);

	String _pids[];
	MyItemsMap inmhItemsMap = new MyItemsMap();
	

	/** Create InmhAssessment of array of pids
	 * 
	 * This will create a list of INeedMoreHelpItems
	 * that are associated by list of pids.
	 * 
	 * @param pids
	 */
	public InmhAssessment(final Connection conn, int runId, String pids[]) throws CmException {
        logger.debug("InmhAssessment(): pids: " + pids);
        _pids = pids;
        for(String p : _pids) {
            try {
            	logger.info("Getting Help Items for '" + p + ": " + runId);
            	
                List<INeedMoreHelpItem> inmhItems = new ArrayList<INeedMoreHelpItem>();          
                INeedMoreHelpItem items[] = INeedMoreHelpManager.getInstance().getHelpItems(conn,p, "run_id=" + runId);
                logger.info("Getting Help Items for '" + p + ": " + runId);
                for(INeedMoreHelpItem h:items)
                    inmhItems.add(h);
                inmhItemsMap.put(p, inmhItems);
            }
            catch(Exception e) {
                throw new CmException("ERROR obtaining INMH for pids: " + pids, e);
            }
        }
	}
	
    /** Create list of help items based on comma
     *  separated list of pids (pid1,pid2,etc..)
     *  
     * @param pids
     */	
	public InmhAssessment(final Connection conn, int runId, String pids) throws CmException {
		this(conn,runId, pids.split(","));
	}
	
	/** Return array of pids used in assessment
	 *  	
	 * @return
	 */
	public String[] getPids() {
		return _pids;
	}
	
	/** Return the list of distinct help items
	 * 
	 * @return
	 */
	public Set<INeedMoreHelpItem> getDistinctInmhItems() {
		return inmhItemsMap.getDistinctInmhItems();
	}

	public String toString() {
		return inmhItemsMap.toString();
	}

	
	/** Return list of union of INMH items and 
	 * associated problem ids
	 * 
	 * return only inmh types that match inmhType or all
	 * if inmhType == null
	 *  
	 * @return
	 */
	public List<InmhItemData> getInmhItemUnion(String inmhType) {
		List<InmhItemData> list = new ArrayList<InmhItemData>();
		for(InmhItemData itemData: inmhItemsMap.getInmhData()) {
			if(inmhType == null || itemData.getInmhItem().getType().equals(inmhType))
				list.add(itemData);
		}
		return list;
	}
	
	 
	/** Return list of solutions in this item pool
	 * 
	 * @param item
	 * @return
	 * @throws Exception
	 */
	static public String[] getItemSolutionPool(String item) throws Exception {
		
		Connection conn=null;
		Statement stmt=null;
		try {
			conn = HMConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("select * from inmh_assessment where file = '" + item + "'");
			List<String> guids = new ArrayList<String>();
            while(rs.next()) {			
				String range = rs.getString("range");
				if(range == null || range.length() == 0)
					throw new Exception("Range is null for this item");
				ConcordanceEntry con = new ConcordanceEntry(conn, range);
				
				guids.addAll(Arrays.asList( con.getGUIDs() ) ) ;
            }
			return guids.toArray(new String[guids.size()]);
		}
		finally {
			SqlUtilities.releaseResources(null,stmt, conn);
		}
	}
}

/** Class to encapsulate the concept of the shared INMH items
 * 
 *  If a basic Map that contains high level methods to provide 
 *  specific information.  Like distinct items.
 *  
 *  
 * @author Casey
 *
 */
class MyItemsMap extends HashMap<String,List<INeedMoreHelpItem>> {
	
	Map<INeedMoreHelpItem,InmhItemData> itemData = new HashMap<INeedMoreHelpItem,InmhItemData>();

	
	/** Collect the distinct INMH items and their related pids as
	 *  data is being inserted into structure.
	 */
	@Override
	public List<INeedMoreHelpItem> put(String key, List<INeedMoreHelpItem> value) {
		for(INeedMoreHelpItem item: value) {
			InmhItemData iD = itemData.get(item);
			if(iD == null) {
				iD = new InmhItemData(item);
				itemData.put(item,iD);
			}
			iD.addProblemIndex(key);
		}
		return super.put(key, value);
	}
	
	
	/** Return a distinct list of INMH items shared
	 * by all guids.
	 * 
	 * This map is a map of unique guids to lists of INMH items.
	 * 
	 * @return
	 */
	public Set<INeedMoreHelpItem>getDistinctInmhItems() {
         return this.itemData.keySet();
	}
	
	/** Return the collect INMH data
	 * 
	 * @return
	 */
	public Collection<InmhItemData> getInmhData() {
		return this.itemData.values();
	}
}