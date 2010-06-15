package hotmath.assessment;

import hotmath.ProblemID;
import hotmath.SolutionManager;
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

    /**
     * Return a sorted map containing weighted values and the problem index.
     * 
     * entries are weight:solution
     * 
     * @return
     */
    public List<String> getWeightedIndexes(int sumOfWeights, int totalNumSolsInPrescription) {
        List<String> values = new ArrayList<String>();
        for (String pid : pidsReferenced) {
            int weight = (getWeight() / sumOfWeights) * totalNumSolsInPrescription;
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

    /**
     * Return pool of solutions that can be used for this INMH item
     * 
     * 
     * NOTE: deal with RPPs as widgets or as solutions
     * 
     * Default is RPP as solution ... If any widgets are specified for a lesson,
     * it trumps all .. and only widgets will be shown. Meaning, solutions and
     * widgets will not be shown together.
     * 
     * 
     * @return
     */
    public List<RppWidget> getWookBookSolutionPool(final Connection conn) {
        // SQL to get list of ranges that match each INMH item
        String sql = "select range   from inmh_assessment i   where i.file = ? ";
        PreparedStatement ps = null;

        List<RppWidget> widgets = new ArrayList<RppWidget>();
        try {
            ps = conn.prepareStatement(sql);
            ps.setString(1, this.item.getFile());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String range = rs.getString("range");
                if (range == null || range.length() == 0)
                    continue;

                if (range.startsWith("{")) {
                    /** is widget */
                    widgets.add(new RppWidget(range));
                } else {
                    List<String> related = findSolutionsMatchingRange(range);
                    for (String s : related) {
                        RppWidget widget = new RppWidget(new ProblemID(s));
                        if (!widgets.contains(widget)) {
                            if (SolutionManager.getInstance().doesSolutionExist(conn, widget.getPid().getGUID())) {
                                widgets.add(widget);
                            } else {
                                logger.debug("Inmh: GUID does not exist: " + s);
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
        }
        return widgets;
    }

    /**
     * Given a range, return all solutions matching the range
     * 
     * @param range
     * @return
     * @throws Exception
     */
    private List<String> findSolutionsMatchingRange(String range) throws Exception {
        ConcordanceEntry con = new ConcordanceEntry(range);
        return (List<String>) Arrays.asList(con.getGUIDs());
    }
    
    @Override
    public String toString() {
        return this.item.toString() + ",pid_count=" + this.pidsReferenced.size();
    }
}

