package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.StudentModelI;

public class StudentModelExt implements StudentModelI {

    private boolean hasExtendedData = false;
    private boolean selfPay = false;
    private boolean isCollege = false;
    private String group;
    private Integer groupId;
    private Integer sectionCount;
    private Integer sectionNum;
    private Integer userProgram;
    private String chapter;
    private String lastQuiz;
    private String lastLogin;
    private String status;
    private Integer tutoringUse;
    private Integer passingCount;
    private Integer notPassingCount;
    private String showWorkState;
    private String tutoringState;
    private String json;
    private Integer uid;
    private String email;
    private Integer adminUid;
    private String passPercent;
    private int totalUsage;
    private StudentProgramModel program;
    private String name;
    private String passcode;
    private String backgroundStyle;
    private Boolean demoUser;
    private String programDescription;
    private StudentSettingsModel settings;
    private boolean programChanged;
    private boolean hasTutoringUse;
    private boolean hasPassingCount;
    private boolean hasLastQuiz;
    private boolean hasLastLogin;

    public StudentModelExt() {
    }

    public StudentModelExt(StudentModelI student) {
        setStudent(student);
    }

    public void setStudent(StudentModelI student) {
        this.group = student.getGroup();
        this.groupId = student.getGroupId();
        this.sectionCount = student.getSectionCount();
        this.sectionNum = student.getSectionNum();
        this.userProgram = student.getProgram().getProgramId();
        this.chapter = student.getChapter();

        this.lastQuiz = student.getLastQuiz();

        this.lastLogin = student.getLastLogin();
        this.status = student.getStatus();
        this.tutoringUse = student.getTutoringUse();
        this.passingCount = student.getPassingCount();
        this.notPassingCount = student.getNotPassingCount();
        this.showWorkState = (student.getSettings().getShowWorkRequired()) ? "REQUIRED" : "OPTIONAL";
        this.tutoringState = (student.getSettings().getTutoringAvailable()) ? "ON" : "OFF";

        this.json = student.getJson();
        this.uid = student.getUid();

        this.email = student.getEmail();
        this.adminUid = student.getAdminUid();

        this.passPercent = student.getPassPercent();

        this.totalUsage = (student.getTotalUsage() != null) ? student.getTotalUsage() : 0;

        this.name = student.getName();
        this.passcode = student.getPasscode();
        this.backgroundStyle = student.getBackgroundStyle();
        this.demoUser = student.getIsDemoUser();
        this.programDescription = student.getProgram().getProgramDescription();

        this.program = student.getProgram();
        this.settings = student.getSettings();
    }

    public void assignExtendedData(StudentModelI sm) {
        setHasExtendedData(true);
        setLastQuiz(sm.getLastQuiz());
        setLastLogin(sm.getLastLogin());
        setTutoringUse(sm.getTutoringUse());
        setPassingCount(sm.getPassingCount());
        setNotPassingCount(sm.getNotPassingCount());
    }

    
    
    
    @Override
    public Integer getTutoringUse() {
        return this.tutoringUse;
    }

    @Override
    public void setTutoringUse(Integer x) {
        this.tutoringUse = (x != null) ? x : 0;
    }

    @Override
    public void setLastLogin(String lastLogin) {
        this.lastLogin = (lastLogin != null) ? lastLogin : " ";
    }

    @Override
    public void setStatus(String status) {
        this.status = (status != null) ? status : " ";
    }

    @Override
    public void setProgramChanged(Boolean changed) {
        this.programChanged = changed;
    }

    /**
     * return true if program has been changed, or false if program was not
     * changed.
     *
     * If value is null, return false
     *
     * @return
     */
    @Override
    public Boolean getProgramChanged() {
        return this.programChanged;
    }

    @Override
    public void setLastQuiz(String lastQuiz) {
        this.lastQuiz = (lastQuiz != null) ? lastQuiz : " ";
    }

    @Override
    public void setNotPassingCount(Integer count) {
        this.notPassingCount = (count != null) ? count : 0;
    }

    @Override
    public void setPassingCount(Integer count) {
        this.passingCount = (count != null) ? count : 0;
    }

    @Override
    public StudentSettingsModel getSettings() {
        if (this.settings == null) {
            setSettings(new StudentSettingsModel());
        }
        return this.settings;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getPasscode() {
        return this.passcode;
    }

    @Override
    public void setPasscode(String passcode) {
        this.passcode = passcode;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public Integer getGroupId() {
        return this.groupId; 
    }

    @Override
    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public void setSectionCount(Integer sectionCount) {
        this.sectionCount = sectionCount;
    }

    @Override
    public Integer getSectionCount() {
        return this.sectionCount;
    }

    @Override
    public void setSectionNum(Integer sectionNum) {
        this.sectionNum = sectionNum;
    }

    @Override
    public Integer getSectionNum() {
        return this.sectionNum;
    }

    @Override
    public String getLastLogin() {
        return this.lastLogin; 
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setTotalUsage(Integer totalUsage) {
        this.totalUsage = totalUsage;
    }

    @Override
    public Integer getTotalUsage() {
        return this.totalUsage;
    }

    @Override
    public Integer getPassingCount() {
        return this.passingCount;
    }

    @Override
    public Integer getNotPassingCount() {
        return this.notPassingCount;
    }

    @Override
    public String getPassPercent() {
        return this.passPercent;
    }

    @Override
    public void setPassPercent(String passPercent) {
        this.passPercent = passPercent;
    }

    @Override
    public void setUid(Integer uid) {
        this.uid = uid;
    }

    @Override
    public Integer getUid() {
        return this.uid;
    }

    @Override
    public void setEmail(String emailAddr) {
        this.email = emailAddr;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setAdminUid(Integer adminUid) {
        this.adminUid = adminUid;
    }

    @Override
    public Integer getAdminUid() {
        return this.adminUid;
    }

    @Override
    public String getChapter() {
        return this.chapter;
    }

    @Override
    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    @Override
    public String getLastQuiz() {
        return this.lastQuiz;
    }

    @Override
    public String getJson() {
        return this.json;
    }

    @Override
    public void setJson(String json) {
        this.json = json;
    }

    @Override
    public String getBackgroundStyle() {
        return this.backgroundStyle;
    }

    @Override
    public void setBackgroundStyle(String style) {
        this.backgroundStyle = style;
    }

    @Override
    public Boolean getIsDemoUser() {
        return demoUser;
    }

    @Override
    public void setIsDemoUser(Boolean isDemo) {
        this.demoUser = isDemo;
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
    public void setSettings(StudentSettingsModel optionSettings) {
        this.settings = optionSettings;
    }

    @Override
    public void setSelfPay(boolean selfPay) {
        this.selfPay = selfPay;
    }

    @Override
    public boolean getSelfPay() {
        return selfPay;
    }

    @Override
    public void setIsCollege(boolean isCollege) {
        this.isCollege = isCollege;
    }

    @Override
    public boolean getIsCollege() {
        return isCollege;
    }

    @Override
    public boolean getHasExtendedData() {
        return this.hasExtendedData;
    }

    @Override
    public void setHasExtendedData(boolean extended) {
        this.hasExtendedData = extended;
    }

    @Override
    public void setShowWorkState(String swState) {
        this.showWorkState = swState;
    }

    @Override
    public String getShowWorkState() {
        return this.showWorkState;
    }

    public void setHasTutoringUse(boolean b) {
        this.hasTutoringUse = b;
    }

    public void setHasPassingCount(boolean b) {
        this.hasPassingCount = b;
    }

    public void setHasLastQuiz(boolean b) {
        this.hasLastQuiz = b;
    }

    public void setHasLastLogin(boolean b) {
        this.hasLastLogin = b;
    }
}
