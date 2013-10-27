package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_tools.client.model.GroupInfoModel;

import java.util.ArrayList;
import java.util.List;

public class WebLinkModel implements Response {

    private String url;
    private String name;
    private int adminId;
    private String comments;
    private boolean publicAvailability;
    
    public enum AvailableOn {DESKTOP_AND_MOBILE, DESKTOP_ONLY, MOBILE_ONLY};

    private List<LessonModel> linkTargets = new ArrayList<LessonModel>();
    private List<GroupInfoModel> linkGroups = new ArrayList<GroupInfoModel>();
    private int linkId;
    private AvailableOn availableWhen;

    public WebLinkModel() {}
    
    public WebLinkModel(int linkId, int adminId, String name, String url, String comments, AvailableOn available, boolean isPublic) {
        this.linkId = linkId;
        this.adminId = adminId;
        this.name = name;
        this.url = url;
        this.comments = comments;
        this.availableWhen = available;
        this.publicAvailability = isPublic;
    }
    
    
    public boolean isPublicAvailability() {
        return publicAvailability;
    }

    public void setPublicAvailability(boolean publicAvailability) {
        this.publicAvailability = publicAvailability;
    }

    public AvailableOn getAvailableWhen() {
        return availableWhen;
    }

    public void setAvailableWhen(AvailableOn availableWhen) {
        this.availableWhen = availableWhen;
    }

    public List<GroupInfoModel> getLinkGroups() {
        return linkGroups;
    }

    public void setLinkGroups(List<GroupInfoModel> linkGroups) {
        this.linkGroups = linkGroups;
    }

    public void setLinkTargets(List<LessonModel> linkTargets) {
        this.linkTargets = linkTargets;
    }

    public int getLinkId() {
        return linkId;
    }

    public void setLinkId(int linkId) {
        this.linkId = linkId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    /** Return the link targets.  Will be empty
     *  list if no targets, null is never returned.
     * @return
     */
    public List<LessonModel> getLinkTargets() {
        return linkTargets;
    }

    public boolean isAllGroups() {
        return (linkGroups.size() == 0 || linkGroups.get(0).getGroupName().toLowerCase().equals("all groups"));
    }

    public boolean isAllLessons() {
        return (linkTargets.size() == 0 || linkTargets.get(0).getLessonName().toLowerCase().equals("all lessons"));
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }


    @Override
    public String toString() {
        return "WebLinkModel [url=" + url + ", name=" + name + ", adminId=" + adminId + ", comments=" + comments + ", linkTargets=" + linkTargets
                + ", linkGroups=" + linkGroups + ", linkId=" + linkId + "]";
    }
    
}
