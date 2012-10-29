package hotmath.gwt.cm_tools.client.ui.assignment;

public enum ProblemStatus {

	NOT_VIEWED("Not viewed"), VIEWED("Viewed"), PENDING("Pending"), CORRECT("Correct"), INCORRECT("Incorrect");
	static public ProblemStatus parseString(String object) {
		if (ProblemStatus.VIEWED.toString().equals(object)) {
			return ProblemStatus.VIEWED;
		} else if (ProblemStatus.CORRECT.toString().equals(object)) {
			return ProblemStatus.CORRECT;
		} else if (ProblemStatus.NOT_VIEWED.toString().equals(object)) {
			return ProblemStatus.NOT_VIEWED;
		} else if (ProblemStatus.PENDING.toString().equals(object)) {
			return ProblemStatus.PENDING;
		} else {
			return ProblemStatus.INCORRECT;
		}
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
