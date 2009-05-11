package hotmath.assessment;

import hotmath.HotMathException;
import hotmath.ProblemID;
import hotmath.assessment.AssessmentPrescription.SessionData;
import hotmath.inmh.INeedMoreHelpItem;
import hotmath.inmh.INeedMoreHelpItemFactory;
import hotmath.inmh.INeedMoreHelpManager;
import hotmath.inmh.INeedMoreHelpResourceType;
import hotmath.inmh.INeedMoreHelpResourceTypeDef;
import hotmath.util.HMConnectionPool;
import hotmath.util.sql.SqlUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sb.logger.SbLogger;

public class AssessmentPrescriptionSession {
    List<SessionData> pids = new ArrayList<SessionData>();
    String name;
    AssessmentPrescription prescription;

    public AssessmentPrescriptionSession(AssessmentPrescription prescription, String name) {
        this.name = name;
        this.prescription = prescription;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSolution(String pid, INeedMoreHelpItem item) {
        pids.add(new SessionData(item, pid, 0, 0));
    }

    public String getName() {
        return name;
    }

    public List<SessionData> getSessionItems() {
        return pids;
    }

    public String toString() {
        return name;
    }

    /**
     * Return list of solutions in session as ProblemIDs
     * 
     * @return
     */
    public List<ProblemID> getSessionProblemIds() {
        List<ProblemID> list = new ArrayList<ProblemID>();
        for (SessionData sessData : this.pids) {
            list.add(new ProblemID(sessData.getPid()));
        }
        return list;
    }

    /**
     * Return the help item associated with named solution
     * 
     * @param pid
     *            The solution to find its help item
     * 
     * @return The INMH help item that caused this solution to be included
     * 
     * @throws Exception
     *             if related item could not be found in this session
     * 
     */
    public INeedMoreHelpItem getHelpItemFor(ProblemID pid) throws Exception {
        for (SessionData sessData : this.pids) {
            if (sessData.getPid().equals(pid.getGUID())) {
                return sessData.getItem();
            }
        }

        throw new HotMathException("INMH item not found for solution: " + pid);
    }

    /**
     * Return all sessionData objects that existing for named item
     * 
     * @param helpItem
     * @return
     */
    public List<SessionData> getSessionDataFor(String helpItem) {
        List<SessionData> list = new ArrayList<SessionData>();

        for (SessionData sd : this.pids) {
            if (sd.getItem().getTitle().equals(helpItem)) {
                list.add(sd);
            }
        }
        return list;
    }

    /**
     * Return INMH items for all items matching title
     * 
     * @param helpItem
     * @return
     */
    public List<INeedMoreHelpItem> getInmhItemsFor(String helpItem) {
        List<INeedMoreHelpItem> items = new ArrayList<INeedMoreHelpItem>();
        for (INeedMoreHelpItem item : getSessionCategories()) {
            if (item.getTitle().equals(helpItem))
                items.add(item);
        }
        return items;
    }

    /**
     * Return comma separated list of pids that reference this item
     * 
     * @param item
     * @return
     * @throws HotMathException
     */
    public String getInmhReferencedPids(INeedMoreHelpItem item) throws HotMathException {
        List<String> list = new ArrayList<String>();

        String pids = "";
        for (SessionData it : this.pids) {
            INeedMoreHelpItem[] i2 = INeedMoreHelpManager.getInstance().getHelpItems(it.getPid(), true);

            boolean found = false;
            for (INeedMoreHelpItem i : i2) {
                if (i.equals(item)) {
                    if (pids.length() > 0)
                        pids += ",";
                    pids += it.getPid();
                    found = true;
                }
            }
        }
        return pids;
    }

    /**
     * Return distinct list of INMH help items for this session
     * 
     * @return
     */
    public List<INeedMoreHelpItem> getSessionCategories() {
        List<INeedMoreHelpItem> list = new ArrayList<INeedMoreHelpItem>();
        for (SessionData sessData : this.pids) {
            if (!list.contains(sessData.getItem()))
                list.add(sessData.getItem());
        }
        return list;
    }

    /**
     * Return the main topic of this session
     * 
     * @return
     */
    public String getTopic() {
        // return the topic of this session
        List<INeedMoreHelpItem> list = getSessionCategories();
        // all are the title...
        return list.get(0).getTitle();
    }

    public List<INeedMoreHelpResourceType> getInmhTypes() throws Exception {
        List<INeedMoreHelpResourceType> resourceTypes = new ArrayList<INeedMoreHelpResourceType>();

        INeedMoreHelpResourceType reviewRt = new INeedMoreHelpResourceType(INeedMoreHelpResourceTypeDef.RESOURCE_TYPES
                .get(INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_videos));

        // get list of linked items related to this item.
        for (INeedMoreHelpItem item : getSessionCategories()) {
            // get list of all linked items for this item
            List<INeedMoreHelpItem> relatedItems = item.getLinkedItems();
            for (INeedMoreHelpItem linked : relatedItems) {
                reviewRt.addResource(new INeedMoreHelpItem(linked.getType(), linked.getFile(), linked.getTitle()));
                resourceTypes.add(reviewRt);
            }
        }
        return resourceTypes;
    }

    /**
     * Look up the specific INMH types for this prescription.
     * 
     * This is done by finding all resources that are directly linked to each
     * topic item via inmh_link.
     * 
     * Each item should be filtered to only show items that are at the users
     * level or below (ie, keep advanced videos from cluttering list).
     * 
     */
    public List<INeedMoreHelpResourceType> getPrescriptionInmhTypes() throws HotMathException {
        return getPrescriptionInmhTypes(null);
    }

    /**
     * Return distinct INMH items used by this session.
     * 
     * Return unique resource types (ie, video, workbook) and all the items that
     * are of that type.
     * 
     * 
     * @param linkType
     * @return
     * @throws Exception
     */
    public Collection<INeedMoreHelpResourceType> getPrescriptionInmhTypesDistinct() throws HotMathException {
        Map<String, INeedMoreHelpResourceType> types = new HashMap<String, INeedMoreHelpResourceType>();
        INeedMoreHelpResourceType resourceType = null;
        for (INeedMoreHelpResourceType type : getPrescriptionInmhTypes()) {
            if (type.getResources().size() == 0)
                continue;

            resourceType = types.get(type.getTypeDef().getType());
            if (resourceType == null) {
                resourceType = type;
                types.put(type.getTypeDef().getType(), type);
            } else {
                resourceType.getResources().add(type.getResources().get(0));
            }
        }
        Collection<INeedMoreHelpResourceType> coll = types.values();

        // sort list to achieve predictable order
        // @TODO: what is the correct order?
        List<INeedMoreHelpResourceType> list = new ArrayList<INeedMoreHelpResourceType>(coll);
        Collections.sort(list, new Comparator<INeedMoreHelpResourceType>() {
            @Override
            public int compare(INeedMoreHelpResourceType o1, INeedMoreHelpResourceType o2) {
                return o1.getTypeDef().getType().compareTo(o2.getTypeDef().getType());
            }
        });

        return list;
    }

    
    /** Return list of Filtered resource types, showing only items 
     *  that are app for the current test/segement
     * 
     * @param linkTypeIn
     * @return
     * @throws HotMathException
     */
    public List<INeedMoreHelpResourceType> getPrescriptionInmhTypes(String linkTypeIn) throws HotMathException {

        if (linkTypeIn == null)
            linkTypeIn = "";

        List<INeedMoreHelpResourceType> resourceTypes = new ArrayList<INeedMoreHelpResourceType>();

        String topicList = "";
        for (INeedMoreHelpItem item : getSessionCategories()) {
            if (topicList.length() > 0)
                topicList += ",";

            topicList += "'" + item.getFile() + "'";
        }

        Connection conn = null;
        PreparedStatement pstat = null;
        try {
            // String sql =
            // " select distinct l.link_type,l.link_key,l.link_title,m.file,m.title "
            // +
            // " from   inmh_map m, inmh_link l " +
            // " where  m.file = l.file " +
            // " and  m.file in (" + topicList + ")";

            String sql = "select distinct m.file,l.link_title, l.link_key,l.link_type "
                    + " from   inmh_assessment m, inmh_link l " + " where  m.file = l.file " + " and  m.file in ("
                    + topicList + ") " + " and l.link_type like '%" + linkTypeIn + "'"
                    + " order by l.link_type, link_title ";

            conn = HMConnectionPool.getConnection();
            pstat = conn.prepareStatement(sql);
            ResultSet rs = pstat.executeQuery();
            while (rs.next()) {

                int type = 0;
                String linkType = rs.getString("link_type");
                if (linkType.equals("video"))
                    type = INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_videos;
                else if (linkType.equals("activity"))
                    type = INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_activity;
                else if (linkType.equals("workbook"))
                    type = INeedMoreHelpResourceTypeDef.RESOURCE_TYPE_workbook;

                INeedMoreHelpResourceType inmhType = new INeedMoreHelpResourceType(
                        INeedMoreHelpResourceTypeDef.RESOURCE_TYPES.get(type));
                for (INeedMoreHelpItem item : getSessionCategories()) {
                    if (item.getFile().equals(rs.getString("file"))) {
                        inmhType.setTopic(item.getTitle());
                        break;
                    }
                }
                INeedMoreHelpItem inmhItem = INeedMoreHelpItemFactory.create(rs.getString("link_type"), rs.getString("link_key"), rs.getString("link_title"));

                // only add items that at, or below the current
                // test grade level.
                if (inmhItem.getGradeLevel() <= this.prescription.getGradeLevel()) {
                    inmhType.addResource(inmhItem);
                } else {
                    SbLogger
                            .postMessage("AssessmentPrescriptionSession: inmh item not included due to higher grade level: "
                                    + inmhItem);
                }
                resourceTypes.add(inmhType);
            }
        } catch (HotMathException hme) {
            throw hme;
        } catch (Exception e) {
            throw new HotMathException(e, "Error reading prescription inmh items: " + e.getMessage());
        } finally {
            SqlUtilities.releaseResources(null, pstat, conn);
        }

        return resourceTypes;
    }
}
