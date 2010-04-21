package hotmath.gwt.cm_tools.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StudyProgramExt extends BaseModelData {

	private static final long serialVersionUID = 5574506049604177840L;

	public StudyProgramExt(String title, String shortTitle, String descr, Integer needsSubject,
	    Integer needsChapters, Integer needsPassPercent,Integer customProgramId, String customProgramName) {

		set("title", title);
		set("shortTitle", shortTitle);
		set("descr", descr);
		set("needsSubject", needsSubject);
		set("needsChapters", needsChapters);
		set("needsPassPercent", needsPassPercent);
		set("customProgramName", customProgramName);
		set("customProgramId", customProgramId);

		/** set css style to identify as custom program
		 * 
		 */
		String customStyle="";
		if(customProgramId != null & customProgramId > 0) {
		    customStyle = "customProgram";
		}
		set("styleIsCustomProgram",customStyle);
	}
}
