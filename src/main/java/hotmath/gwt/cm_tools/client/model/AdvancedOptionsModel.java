package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

public class AdvancedOptionsModel implements Response{
    
    
    public AdvancedOptionsModel(){}
    
    
    private String passPercent;
    private StudentSettingsModel ssm;
    private int sectionNum;
    private boolean loggedIn;
    private boolean startedQuiz;
    private boolean tookQuiz;
    private boolean viewedLessons;
    private boolean usedResources;
    private boolean registered;
    private int sectionCount;
    private boolean sectionIsSettable;
    private boolean progStopIsSettable;
    private int activeSection;
    

    public void setPassPercent(String passPercent) {
        this.passPercent = passPercent;
    }

    public void setSettings(StudentSettingsModel ssm) {
        this.ssm = ssm;
    }

    public void setSectionNum(int sectionNum) {
        this.sectionNum = sectionNum;
    }

    public StudentSettingsModel getSettings() {
        return ssm;
    }

    public String getPassPercent() {
        return passPercent;
    }

    public int getSectionNum() {
        return this.sectionNum;
    }

    public void setLoggedIn(boolean value) {
        this.loggedIn = value;
    }

    public void setStartedQuiz(boolean value) {
        this.startedQuiz = value;
    }

    public void setTookQuiz(boolean value) {
        this.tookQuiz = value;
    }

    public void setViewedLessons(boolean value) {
        this.viewedLessons = value;
    }

    public void setUsedResources(boolean value) {
        this.usedResources = value;
    }

    public void setRegistered(boolean value) {
        this.registered = value;
    }

    public boolean getLoggedIn() {
        return loggedIn;
    }


    public StudentSettingsModel getSsm() {
        return ssm;
    }

    public void setSsm(StudentSettingsModel ssm) {
        this.ssm = ssm;
    }

    public boolean isStartedQuiz() {
        return startedQuiz;
    }

    public boolean isTookQuiz() {
        return tookQuiz;
    }

    public boolean isViewedLessons() {
        return viewedLessons;
    }

    public boolean isUsedResources() {
        return usedResources;
    }

    public boolean isRegistered() {
        return registered;
    }

    public int getSectionCount() {
        return this.sectionCount;
    }

    public boolean isSectionIsSettable() {
        return this.sectionIsSettable;
    }

    public boolean isProgStopIsSettable() {
        return this.progStopIsSettable;
    }

    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }

    public void setActiveSection(int activeSection) {
        this.activeSection = activeSection;
    }

    public void setSectionIsSettable(boolean sectionSelectAvail) {
        this.sectionIsSettable = sectionSelectAvail;
    }

    public void setProgStopIsSettable(boolean b) {
        this.progStopIsSettable = b;
    }

    public int getActiveSection() {
        return activeSection;
    }
    
    
}
