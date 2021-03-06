package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.model.CmProgramType;

import com.google.gwt.user.client.rpc.IsSerializable;

public class StudyProgramModel implements IsSerializable {

    private Integer programId;
    private String title;
    private String shortTitle;
    private String descr;
    private String subjectId;

    private boolean needsSubject;
    private boolean needsChapters;
    private boolean needsPassPercent;
    private boolean needsState;
    
    private Integer sectionCount;
    
    private Integer customQuizId;
    private String customQuizName;

    private Integer customProgramId;
    private String customProgramName;

    private Boolean isTemplate;

    private Boolean isArchived;
    private String archiveDate;
    
    private CmProgramType programType;
    

    public StudyProgramModel() {
    }

    public StudyProgramModel(Integer programId, String title, String shortTitle, String descr, Integer customProgramId,
            String customProgramName, Integer customQuizId, String customQuizName, boolean needsSubject, boolean needsChapters, boolean needsPassPercent,
            boolean needsState, Integer sectionCount) {
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

    public Boolean getIsArchived() {
		return isArchived;
	}

	public void setIsArchived(Boolean isArchived) {
		this.isArchived = isArchived;
	}

	public String getArchiveDate() {
		return archiveDate;
	}

	public void setArchiveDate(String archiveDate) {
		this.archiveDate = archiveDate;
	}

	public CmProgramType getProgramType() {
		return programType;
	}

	public void setProgramType(CmProgramType programType) {
		this.programType = programType;
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

    public boolean isTemplate() {
    	return (isTemplate != null && isTemplate);
    }

    public boolean isArchived() {
    	return (isArchived != null && isArchived);
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


    public boolean isNeedsSubject() {
        return needsSubject;
    }

    public void setNeedsSubject(boolean needsSubject) {
        this.needsSubject = needsSubject;
    }

    public boolean isNeedsChapters() {
        return needsChapters;
    }

    public void setNeedsChapters(boolean needsChapters) {
        this.needsChapters = needsChapters;
    }

    public boolean isNeedsPassPercent() {
        return needsPassPercent;
    }

    public void setNeedsPassPercent(boolean needsPassPercent) {
        this.needsPassPercent = needsPassPercent;
    }

    public boolean isNeedsState() {
        return needsState;
    }

    public void setNeedsState(boolean needsState) {
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
                + isTemplate  + ", sectionCount= " + sectionCount + ", programType= " + programType.getType() + "]";
    }
}