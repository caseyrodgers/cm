package hotmath.cm.server.rest;

public class TestingPid {

	private String pid;
	private String input;

	public TestingPid(String pid, String input) {
		this.pid = pid;
		this.input = input;
	}

	public String getPid() {
		return pid;
	}

	public String getInput() {
		return input;
	}

}
