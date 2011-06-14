package hotmath.gwt.cm_tools.client.model;

//import hotmath.gwt.cm_rpc.client.model.CmProgramType;

import hotmath.gwt.cm_rpc.client.model.CmProgramType;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StudyProgramExt extends BaseModelData {

	private static final long serialVersionUID = 5574506049604177840L;
	StudyProgramModel program;

	public StudyProgramExt(StudyProgramModel program, String title, String shortTitle, String descr, Integer needsSubject,
	    Integer needsChapters, Integer needsPassPercent, Integer customProgramId, String customProgramName) {

	    this.program = program;
	    
		set("title", title);
		set("shortTitle", shortTitle);
		set("descr", descr);
		set("needsSubject", needsSubject);
		set("needsChapters", needsChapters);
		set("needsPassPercent", needsPassPercent);
		set("customProgramName", customProgramName);
		set("customProgramId", customProgramId);
		set("customQuizId", program.getCustomQuizId());
		set("customQuizName", program.getCustomQuizName());
		set("sectionCount", program.getSectionCount());
		set("programType", program.getProgramType());

		/** set css style to identify as custom program
		 * 
		 */
		String pre="";
		if(program.isCustomProgram()) {
		    set("styleIsCustomProgram","isCustom");
		    pre="CP:";
		}
		else if(program.isCustomQuiz()) {
		    set("styleIsCustomProgram","isCustom");
		    pre="CQ:";
		}
		set("label", pre + title);
	}
	
	public Integer getCustomProgramId() {
	    return get("customProgramId");
	}
    public Integer getCustomQuizId() {
        return get("customQuizId");
    }
    
    public String getCustomProgramName() {
        return get("customProgramName");
    }

    public String getCustomQuizName() {
        return get("customQuizName");
    }    
    
    public Integer getSectionCount() {
    	return get("sectionCount");
    }
    
    public boolean isProficiency() {
    	return (CmProgramType.PROF == (CmProgramType) get("programType"));
    }
    
    public boolean isGradPrep() {
    	CmProgramType progType = (CmProgramType) get("programType");

    	return (CmProgramType.GRADPREP == progType ||
    			CmProgramType.GRADPREPTX == progType ||
    			CmProgramType.GRADPREPNATIONAL == progType);    	
    }
    
}