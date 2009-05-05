package hotmath.gwt.cm_admin.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class StudentActivityModel extends BaseModelData  {

	private static final long serialVersionUID = 6939494404062103623L;
	
	public static final String ACTIVITY_KEY = "activity";
	public static final String PROGRAM_KEY = "program";
	public static final String USE_DATE_KEY = "use-date";
	public static final String RESULT_KEY = "result";
	public static final String START_KEY = "start";
	public static final String STOP_KEY = "stop";
	
	public String getActivity() {
		return get(ACTIVITY_KEY);
	}

	public void setActivity(String activity) {
		set(ACTIVITY_KEY, activity);
	}

	public String getProgramDescr() {
		return get(ACTIVITY_KEY);
	}

	public void setProgramDescr(String program) {
		set(PROGRAM_KEY, program);
	}

	public String getUseDate() {
		return get(USE_DATE_KEY);
	}

	public void setUseDate(String useDate) {
		set(USE_DATE_KEY, useDate);
	}

	public String getResult() {
		return get(RESULT_KEY);
	}

	public void setResult(String result) {
		set(RESULT_KEY, result);
	}

	public String getStart() {
		return get(START_KEY);
	}

	public void setStart(String start) {
		set(START_KEY, start);
	}

	public String getStop() {
		return get(STOP_KEY);
	}

	public void setStop(String stop) {
		set(STOP_KEY, stop);
	}

	public StudentActivityModel() {
	}
}
