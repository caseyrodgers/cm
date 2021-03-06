package hotmath.assessment;

import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
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
	public InmhAssessment(final Connection conn, int userId, String pids[]) throws CmException {
        logger.debug("InmhAssessment(): pids: " + pids);
        _pids = pids;
        for(String p : _pids) {
            try {
            	logger.debug("Getting Help Items for '" + p + ": " + userId);
            	
                List<INeedMoreHelpItem> inmhItems = new ArrayList<INeedMoreHelpItem>();   
                
                INeedMoreHelpItem items[] = (INeedMoreHelpItem[])CmCacheManager.getInstance().retrieveFromCache(CacheName.INMH_ITEMS, p);
                if(items == null) {
                	logger.debug("Retrieving INMH Items for '" + p + ": " + userId);
                	items = INeedMoreHelpManager.getInstance().getHelpItems(conn, p, "user_id=" + userId);
                	CmCacheManager.getInstance().addToCache(CacheName.INMH_ITEMS, p, items);
                }

            	for(INeedMoreHelpItem h:items) {
            		inmhItems.add(h);
            	}
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
	public InmhAssessment(final Connection conn, int userId, String pids) throws CmException {
		this(conn,userId, pids.split(","));
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
	
	 
	/** Return list of RppWidgets along with their grade levels
	 * 
	 * @param item
	 * @return
	 * @throws Exception
	 */
	static public List<RppWidget> getItemSolutionPool(String item) throws Exception {
		
		Connection conn=null;
		Statement stmt=null;
		try {
			conn = HMConnectionPool.getConnection();
			stmt = conn.createStatement();
			
			ResultSet rs = stmt.executeQuery("select * from inmh_assessment where file = '" + item + "'");
			List<RppWidget> rppWidgets = new ArrayList<RppWidget>();
            while(rs.next()) {			
				String r = rs.getString("range");
				if(r == null || r.length() == 0)
					throw new Exception("Range is null for this item");
				
				Range range = new Range(r);
				
				if(!range.getRange().startsWith("{")) {
					ConcordanceEntry con = new ConcordanceEntry(conn, range.getRange());
					
					for(String c: con.getGUIDs()) {
					    rppWidgets.add(new RppWidget(c, range.getGradeLevels()));
					}
				}
				else {
					rppWidgets.add(new RppWidget(range.getRange(), range.getGradeLevels()));
				}
            }
            
			return rppWidgets;
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