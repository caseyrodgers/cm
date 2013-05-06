package hotmath.gwt.cm_rpc_assignments.client.model;

public enum ProblemStatus {

	CORRECT      ("Correct"),
	HALF_CREDIT  ("Half Credit"),
	INCORRECT    ("Incorrect"),
	NOT_VIEWED   ("Not Viewed"),
	PENDING      ("Pending"),
	SUBMITTED    ("Submitted"),
	UNANSWERED   ("Unanswered"),
	UNKNOWN      ("Unknown"),
	VIEWED       ("Viewed");

	static public ProblemStatus parseString(String value) {
		for (ProblemStatus p : ProblemStatus.values()) {
			if (value.equals(p.toString())) return p;
		}
		return ProblemStatus.UNKNOWN;
	}

	private String status;

	ProblemStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return status;
	}

}
