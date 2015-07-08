package hotmath.gwt.cm_tools.client.model;

//import hotmath.gwt.cm_rpc.client.model.CmProgramType;

import hotmath.gwt.cm_rpc.client.model.CmProgramType;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

public class StudyProgramExt implements Response {
    
    int uiId;
    
    StudyProgramModel program;
    String title;
    String shortTitle;
    String descr;
    boolean needsSubject;
    boolean needsChapters;
    boolean needsPassPercent;
    String customProgramName;
    int customProgramId;
    int customQuizId;
    String customQuizName;
    int sectionCount;
    CmProgramType programType;

    String styleIsFree, styleIsCustomProgram, styleIsTemplate, styleIsArchived, label;


    public StudyProgramExt(StudyProgramModel program, String title, String shortTitle, String descr,
            boolean needsSubject, boolean needsChapters, boolean needsPassPercent, Integer customProgramId,
            String customProgramName) {

        this.program = program;

        this.title = title;
        this.shortTitle = shortTitle;
        this.descr = descr;
        this.needsSubject = needsSubject;
        this.needsChapters = needsChapters;
        this.needsPassPercent = needsPassPercent;
        this.customProgramName = customProgramName;
        this.customProgramId = customProgramId;
        this.customQuizId = program.getCustomQuizId();
        this.customQuizName = program.getCustomQuizName();
        this.sectionCount = program.getSectionCount();
        this.programType = program.getProgramType();

        /**
         * set css style to identify as custom program
         * 
         */
        String pre = "";
        if (program.isCustomProgram()) {
            this.styleIsCustomProgram = "isCustom";
        } else if (program.isCustomQuiz()) {
            styleIsCustomProgram = "isCustom";
        }

        if (program.isTemplate()) {
            styleIsTemplate = "custom-template";
        }

        label = pre + title;

        /**
         * set css style and append archive date to identify archived programs
         */
        if (program.isArchived()) {
            this.styleIsArchived = "custom-archived";
            this.label = label + " (" + program.getArchiveDate() + ")";
        }
        
        this.uiId = __uiIdCounter++;
    }
    

    static int __uiIdCounter;
    
    /** Get auto assigned ID used in GUI grids */
    public int getUiId() {
        return uiId;
    }



    public StudyProgramModel getProgram() {
        return program;
    }



    public void setProgram(StudyProgramModel program) {
        this.program = program;
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



    public String getCustomProgramName() {
        return customProgramName;
    }



    public void setCustomProgramName(String customProgramName) {
        this.customProgramName = customProgramName;
    }



    public int getCustomProgramId() {
        return customProgramId;
    }



    public void setCustomProgramId(int customProgramId) {
        this.customProgramId = customProgramId;
    }



    public int getCustomQuizId() {
        return customQuizId;
    }



    public void setCustomQuizId(int customQuizId) {
        this.customQuizId = customQuizId;
    }



    public String getCustomQuizName() {
        return customQuizName;
    }



    public void setCustomQuizName(String customQuizName) {
        this.customQuizName = customQuizName;
    }



    public int getSectionCount() {
        return sectionCount;
    }



    public void setSectionCount(int sectionCount) {
        this.sectionCount = sectionCount;
    }



    public CmProgramType getProgramType() {
        return programType;
    }



    public void setProgramType(CmProgramType programType) {
        this.programType = programType;
    }



    public String getStyleIsCustomProgram() {
        return styleIsCustomProgram;
    }



    public void setStyleIsCustomProgram(String styleIsCustomProgram) {
        this.styleIsCustomProgram = styleIsCustomProgram;
    }



    public String getStyleIsTemplate() {
        return styleIsTemplate;
    }



    public void setStyleIsTemplate(String styleIsTemplate) {
        this.styleIsTemplate = styleIsTemplate;
    }



    public String getStyleIsArchived() {
        return styleIsArchived;
    }



    public void setStyleIsArchived(String styleIsArchived) {
        this.styleIsArchived = styleIsArchived;
    }



    public String getLabel() {
        return label;
    }



    public void setLabel(String label) {
        this.label = label;
    }

    


    public String getStyleIsFree() {
        return styleIsFree;
    }



    public void setStyleIsFree(String styleIsFree) {
        this.styleIsFree = styleIsFree;
    }



    public boolean isProficiency() {
        return (CmProgramType.PROF == (CmProgramType) this.programType);
    }

    public boolean isCustom() {
        int customProgId = ((Integer) this.customProgramId != null) ? (Integer) this.customProgramId : 0;
        int customQuizId = ((Integer) this.customQuizId != null) ? (Integer) this.customQuizId : 0;
        return (customProgId != 0 || customQuizId != 0);
    }

    public boolean isGradPrep() {
        CmProgramType progType = (CmProgramType) programType;

        return (CmProgramType.GRADPREP == progType || CmProgramType.GRADPREPTX == progType || CmProgramType.GRADPREPNATIONAL == progType);
    }

}