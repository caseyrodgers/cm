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
    
    private Integer sectionCount;
    
    private Integer customQuizId;
    private String customQuizName;

    private Integer customProgramId;
    private String customProgramName;

    private Boolean isTemplate;
    private Boolean isProficiency;
    private Boolean isGradPrep;

    public StudyProgramModel() {
    }

    public StudyProgramModel(Integer programId, String title, String shortTitle, String descr, Integer customProgramId,
            String customProgramName, Integer customQuizId, String customQuizName, Integer needsSubject, Integer needsChapters, Integer needsPassPercent,
            Integer needsState, Integer sectionCount) {
        this.programId = programId;
        this.customProgramId = customProgramId;
        this.customProgramName = customProgramName;
        this.customQuizId = customQuizId;
        this.customQuizName = customQuizName;
        this.title = title;
        this.shortTitle = shortTitle;
        this.descr = descr;
        this.needsSubject = needsSubject;
        this.needsChapters = needsChapters;
        this.needsPassPercent = needsPassPercent;
        this.needsState = needsState;
        this.sectionCount = sectionCount;
    }

    public Integer getCustomQuizId() {
        return customQuizId;
    }

    public void setCustomQuizId(Integer customQuizId) {
        this.customQuizId = customQuizId;
    }

    public String getCustomQuizName() {
        return customQuizName;
    }

    public void setCustomQuizName(String customQuizName) {
        this.customQuizName = customQuizName;
    }

    public Boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(Boolean isTemplate) {
        this.isTemplate = isTemplate;
    }

    public Boolean getIsProficiency() {
		return isProficiency;
	}

	public void setIsProficiency(Boolean isProficiency) {
		this.isProficiency = isProficiency;
	}

	public Boolean getIsGradPrep() {
		return isGradPrep;
	}

	public void setIsGradPrep(Boolean isGradPrep) {
		this.isGradPrep = isGradPrep;
	}

	public String getCustomProgramName() {
        return customProgramName;
    }

    public void setCustomProgramName(String customProgramName) {
        this.customProgramName = customProgramName;
    }

    public boolean isCustom() {
        return isCustomQuiz() || isCustomProgram();
    }
    
    public boolean isCustomQuiz() {
        return (customQuizId != null && customQuizId > 0);
    }
    
    public boolean isCustomProgram() {
        return (customProgramId != null && customProgramId > 0);
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

    public Integer getSectionCount() {
		return sectionCount;
	}

	public void setSectionCount(Integer sectionCount) {
		this.sectionCount = sectionCount;
	}

	public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    @Override
    public String toString() {
        return "StudyProgramModel [programId=" + programId + ", title=" + title + ", shortTitle=" + shortTitle
                + ", descr=" + descr + ", subjectId=" + subjectId + ", needsSubject=" + needsSubject
                + ", needsChapters=" + needsChapters + ", needsPassPercent=" + needsPassPercent + ", needsState="
                + needsState + ", customQuizId=" + customQuizId + ", customQuizName=" + customQuizName
                + ", customProgramId=" + customProgramId + ", customProgramName=" + customProgramName + ", isTemplate="
                + isTemplate + ", isProficiency=" + isProficiency + ", isGradPrep=" + isGradPrep 
                + ", sectionCount= " + sectionCount + "]";
    }
}