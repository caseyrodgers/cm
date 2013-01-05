package hotmath.gwt.cm_tools.client.model;



/**
 * <code>StudentModelBase</code> represents the Base student information,
 * that is, information that can be quickly obtained from the DB for a student.
 *
 * @author bob
 *
 */

public class StudentModelBase implements StudentModelI {

    private static final long serialVersionUID = 3692580752279795982L;
    
    Integer uid;
    Integer adminUid;

    String  name;
    String  passcode;
    String  email;
    String  backgroundStyle;
    Boolean isDemoUser;
    int  groupId;
    String  group;
    Integer sectionCount;
    Integer sectionNum;
    Integer userProgramId;
    
    String chapter;
    String progId;
    String programDescr;
    String status;
    String subjId;
    String testConfigJson;
    
    boolean hasExtendedData;
    
    StudentProgramModel program = new StudentProgramModel();
    
    String passPercent;

    StudentSettingsModel settings = new StudentSettingsModel();
    
    private String showWorkState;
    
    @Override
    public void setShowWorkState(String swState) {
        this.showWorkState = swState;
    }

    @Override
    public String getShowWorkState() {
        return this.showWorkState;
    }

    public StudentModelBase() {
    }

    
    @Override
    public void setHasExtendedData(boolean extended) {
        this.hasExtendedData = extended;
    }
    
    @Override
    public boolean getHasExtendedData() {
        return hasExtendedData;
    }
    
    @Override
    public Boolean getIsDemoUser() {
        return isDemoUser;
    }

    @Override
    public void setIsDemoUser(Boolean isDemoUser) {
        this.isDemoUser = isDemoUser;
    }

    @Override
    public Integer getUid() {
        return uid;
    }

    @Override
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public Integer getAdminUid() {
        return adminUid;
    }

    @Override
    public void setAdminUid(Integer adminUid) {
        this.adminUid = adminUid;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPasscode() {
        return passcode;
    }

    @Override
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    @Override
    public String getBackgroundStyle() {
        return backgroundStyle;
    }

    @Override
    public void setBackgroundStyle(String backgroundStyle) {
        this.backgroundStyle = backgroundStyle;
    }

    @Override
    public Integer getGroupId() {
        return groupId;
    }

    @Override
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String getChapter() {
        return chapter;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public String getJson() {
        return testConfigJson;
    }

    @Override
    public String getLastLogin() {
        // not implemented
        return null;
    }

    @Override
    public String getLastQuiz() {
        // not implemented
        return null;
    }

    @Override
    public Integer getNotPassingCount() {
        // not implemented
        return null;
    }

    @Override
    public String getPassPercent() {
        return passPercent;
    }

    @Override
    public Integer getPassingCount() {
        // not implemented
        return null;
    }

    @Override
    public Boolean getProgramChanged() {
        // not implemented
        return null;
    }

    @Override
    public Integer getSectionCount() {
        return sectionCount;
    }

    @Override
    public Integer getSectionNum() {
        return sectionNum;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public Integer getTotalUsage() {
        // not implemented
        return null;
    }

    @Override
    public Integer getTutoringUse() {
        // not implemented
        return null;
    }

    @Override
    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public void setJson(String json) {
        testConfigJson = json;
    }

    @Override
    public void setLastLogin(String lastLogin) {
        // not implemented
    }

    @Override
    public void setLastQuiz(String lastQuiz) {
        // not implemented
    }

    @Override
    public void setNotPassingCount(Integer count) {
        // not implemented
    }

    @Override
    public void setPassPercent(String passPercent) {
        this.passPercent = passPercent;
    }

    @Override
    public void setPassingCount(Integer count) {
        // not implemented here
    }


    @Override
    public void setProgramChanged(Boolean changed) {
        // not implemented
    }

    @Override
    public void setSectionCount(Integer sectionCount) {
        this.sectionCount = sectionCount;
    }

    @Override
    public void setSectionNum(Integer sectionNum) {
        this.sectionNum = sectionNum;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public void setTotalUsage(Integer totalUsage) {
        // not implemented
    }

    @Override
    public void setTutoringUse(Integer x) {
        // not implemented
    }

    @Override
    public StudentProgramModel getProgram() {
        return this.program; 
    }

    @Override
    public void setProgram(StudentProgramModel studyProgram) {
        this.program = studyProgram;
    }

    @Override
    public StudentSettingsModel getSettings() {
        return settings;
    }

    @Override
    public void setSettings(StudentSettingsModel settings) {
        this.settings = settings;
    }
}
