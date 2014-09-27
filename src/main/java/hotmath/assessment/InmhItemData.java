package hotmath.assessment;

import hotmath.SolutionManager;
import hotmath.cm.util.CmCacheManager;
import hotmath.cm.util.CmCacheManager.CacheName;
import hotmath.concordance.ConcordanceEntry;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import sb.util.SbUtilities;


/**
 * Represents a single INMH item and all the PIDS that reference it
 * 
 * @author Casey
 * 
 */
public class InmhItemData {

    INeedMoreHelpItem item;
    List<String> pidsReferenced = new ArrayList<String>();

    Logger logger = Logger.getLogger(InmhItemData.class);

    public InmhItemData() {
    }

    public InmhItemData(INeedMoreHelpItem item) {
        super();
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

    public void setINeedMoreHelpItem(INeedMoreHelpItem item) {
        this.item = item;
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

    public void setPids(List<String> pids) {
        this.pidsReferenced = pids;
    }

    /** Return pool of RppWidgets that will make up the prescription
     *  for this INMH item.
     * 
     * 
     * NOTE: A RppWidget can be either a RPP or an RPA
     * 
     * If flash-supported-environment then Flash based RPAs can be used.
     * If non-flash-supported-enviorment then only NON flash content (ie, RPP) are returned.
     * 
     * Flash capability is defined in ClientEnvironment
     * 
     * Default is RPP as solution ... If any widgets are specified and allowed for a lesson,
     * it trumps all .. and only widgets will be shown. Meaning, solutions and
     * widgets will not be shown together.

     * 
     */
    @SuppressWarnings("unchecked")
    public List<RppWidget> getWidgetPool(final Connection conn, String logTag) throws Exception {
        /** check if in cache and return.  Side effect is if browserType changes no new pool will be created
         *  I'm not sure that is problem ... but, I think it is.
         *  
         *  TODO: We could save the browser type with the cached object and verify.
         *  
         */
        String cacheKey = this.item.getFile();
    	List<RppWidget> widgets = (List<RppWidget>)CmCacheManager.getInstance().retrieveFromCache(CacheName.WOOKBOOK_POOL, cacheKey);
    	if(widgets != null)
    		return widgets;
    	
    	// if random range specified, then we cannot 
    	boolean canCache=true;
    	
        // SQL to get list of ranges that match each INMH item
        String sql = "select `range` from inmh_assessment i where i.file = ? order by id";
        PreparedStatement ps = null;

        logger.debug("getting solution pool " + logTag);
        
        widgets = new ArrayList<RppWidget>();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, this.item.getFile());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Range range = new Range(rs.getString("range"));
                if (range.getRange() == null || range.getRange().length() == 0)
                    continue;
                
                if (range.getRange().startsWith("{")) {
                    
                    /** there is just one
                     * 
                     */
                    RppWidget rpa = new RppWidget(range.getRange());
                    rpa.getGradeLevels().addAll(range.getGradeLevels());
                    
                    /** ignore Flash RPAs */
                    if(!rpa.isFlashRequired()) {
                        widgets.add(rpa);
                    }
                    else {
                        logger.warn("Flash RPAs ignored: " + rpa);
                    }
                    
                } else {
                    List<String> rangePids = null;
                    
                    // contains a random range
                    if(range.getRange().contains("[")) {
                        
                        logger.info("Generating random range: " + range);
                        rangePids = findSolutionsInRandomRange(conn, range.getRange());
                        if(rangePids.size() == 0) {
                            logger.warn("No random problems found: " + this + ", " + range.getRange());
                        }
                        
                        canCache=false;
                    }
                    
                	logger.debug("find solutions in range " + logTag);
                	
                	List<String> related=null;
                	if(rangePids != null) {
                	    related = rangePids;
                	}
                	else {
                	    related = findSolutionsMatchingRange(conn, range.getRange());
                	}
                    logger.debug("finished finding solutions in range " + logTag);
                    for (String s : related) {
                        RppWidget widget = new RppWidget(s);
                        widget.getGradeLevels().addAll(range.getGradeLevels());
                        if (!widgets.contains(widget)) {
                            if (SolutionManager.getInstance().doesSolutionExist(conn, widget.getFile())) {
                                widgets.add(widget);
                            } else {
                                logger.debug("Inmh: PID does not exist: " + s);
                            }
                        }
                        else {
                            logger.info(String.format("Duplicate RPP %s for item '%s'",widget,this));
                        }
                    }
                }
            }
        } catch (Exception e) {
        	logger.error(String.format("*** Load of RppWidget for %s failed", item.getFile(), e));
        } finally {
            SqlUtilities.releaseResources(null, ps, null);
            logger.debug("finished getting solution pool " + logTag);
        }
        
        if(canCache) {
            CmCacheManager.getInstance().addToCache(CacheName.WOOKBOOK_POOL, cacheKey, widgets);
        }
        return widgets;
    }

    private List<String> findSolutionsInRandomRange(final Connection conn, String range) throws Exception {
        try {
            //range="test,casey,1.1,[1-5-2]";
            List<String> pids = new ArrayList<String>();
            if(!range.contains("[")) {
                pids.add(range);
            }
            else {
                
                logger.info("Parsing random range: " + range);
                
                String pieces[] = range.split(",");
                
                String s = range.substring(range.indexOf("[")+1,range.indexOf("]"));
                String spieces[] = s.split("-");
                int start = SbUtilities.getInt(spieces[0]);
                int end = SbUtilities.getInt(spieces[1]);
                int numToCreate = SbUtilities.getInt(spieces[2]);
                
                
                
                int maxAttempts=numToCreate * 5;
                List<Integer> added=new ArrayList<Integer>();
                while(maxAttempts-- > -1) {
                    
                    // get random selection
                    int problemNumberToTry = SbUtilities.getRandomNumber(end-(start-1));
                    
                    
                    // get the active associated pid
                    String book = pieces[0];
                    String problemSet = pieces[1];
                    String chapter = pieces[2].split("\\.")[0];
                    String section = pieces[2].split("\\.")[1];

                    problemNumberToTry += start;

                    logger.info("trying random problem: " + problemNumberToTry);

                    // only if not already read
                    if(!added.contains(problemNumberToTry)) {
                        added.add(problemNumberToTry);
                        String activePid = lookupActivePid(conn,book, chapter, section, problemSet, problemNumberToTry);
                        
                        logger.info("Read random pid: " + activePid);
                        

                        if(activePid != null) {
                            pids.add(activePid);
                            numToCreate--;
                            
                            if(numToCreate == 0) {
                                break;
                            }
                        }
                    }
                }
            }
            
            return pids;
        }
        catch(Exception e) {
            logger.error("Error", e);
            throw e;
        }
    }

    private String lookupActivePid(Connection conn, String book, String chapter, String section, String problemSet, int problemNumber) throws Exception  {
        String sql = 
                "select problemnumber, problemindex " + 
                "from SOLUTIONS " +
                "where booktitle = ?" + 
                "and chaptertitle = ? " +
                "and sectiontitle = ?" +
                "and problemnumber = ? " +
                "and active = 1"; 
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, book);
            ps.setString(2, chapter);
            ps.setString(3, section);
            ps.setString(4, problemNumber + "");
            
            logger.debug("lookupActivePid: " + ps);
            
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                String pid = rs.getString("problemindex");
                return pid;
            }
        }
        catch(Exception e) {
            throw e;
        }
        finally {
            SqlUtilities.releaseResources(null,ps, null);
        }
        return null;
    }

    /**
     * Given a range, return all solution PIDS matching the range
     * 
     * @param range
     * @return
     * @throws Exception
     */
    private List<String> findSolutionsMatchingRange(final Connection conn, String range) throws Exception {
    	ConcordanceEntry con = new ConcordanceEntry(conn, range);
        return (List<String>) Arrays.asList(con.getGUIDs());
    }
    
    @Override
    public String toString() {
        return this.item.toString() + ",pid_count=" + this.pidsReferenced.size();
    }
    
    static public enum BrowserType {
        BROWSER_WITH_FLASH,
        BROWSER_WITHOUT_FLASH
    }
}

