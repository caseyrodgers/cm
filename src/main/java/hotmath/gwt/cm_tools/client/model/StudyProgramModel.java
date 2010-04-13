package hotmath.gwt.cm_tools.client.model;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StudyProgramModel implements IsSerializable {

    private Integer programId;
    private String title;
    private String shortTitle;
    private String descr;
    private String subjectId;

    private Integer needsSubject;
    private Integer needsChapters;
    private Integer needsPassPercent;
    private Integer needsState;

    private Integer customProgramId;
    private String customProgramName;
    private Boolean isTemplate;

    public StudyProgramModel() {
    }

    public StudyProgramModel(Integer programId, String title, String shortTitle, String descr, Integer customProgramId,
            String customProgramName, Integer needsSubject, Integer needsChapters, Integer needsPassPercent,
            Integer needsState) {
        this.programId = programId;
        this.customProgramId = customProgramId;
        this.customProgramName = customProgramName;
        this.title = title;
        this.shortTitle = shortTitle;
        this.descr = descr;
        this.needsSubject = needsSubject;
        this.needsChapters = needsChapters;
        this.needsPassPercent = needsPassPercent;
        this.needsState = needsState;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public String getCustomProgramName() {
        return customProgramName;
    }

    public void setCustomProgramName(String customProgramName) {
        this.customProgramName = customProgramName;
    }

    public boolean isCustomProgram() {
        return customProgramId != null && customProgramId > 0;
    }

    public Integer getProgramId() {
        return programId;
    }

    public void setProgramId(Integer programId) {
        this.programId = programId;
    }

    public Integer getCustomProgramId() {
        return customProgramId;
    }

    public void setCustomProgramId(Integer customProgramId) {
        this.customProgramId = customProgramId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public Integer getNeedsSubject() {
        return needsSubject;
    }

    public void setNeedsSubject(Integer needsSubject) {
        this.needsSubject = needsSubject;
    }

    public Integer getNeedsChapters() {
        return needsChapters;
    }

    public void setNeedsChapters(Integer needsChapters) {
        this.needsChapters = needsChapters;
    }

    public Integer getNeedsPassPercent() {
        return needsPassPercent;
    }

    public void setNeedsPassPercent(Integer needsPassPercent) {
        this.needsPassPercent = needsPassPercent;
    }

    public Integer getNeedsState() {
        return needsState;
    }

    public void setNeedsState(Integer needsState) {
        this.needsState = needsState;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }
}