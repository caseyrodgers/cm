package hotmath.gwt.cm_rpc.client.model;

/* parse pid into pieces
 * 
 */
public class Pid {
	
	String problem;
	String problemSet;
	int page;

	String pid;
	private String textCode;
	private String chapter;
	private String section;

	
	public Pid(String pid) {
		this.pid = pid;
		parsePid();
	}
	
	public String getProblem() {
		return problem;
	}

	public String getProblemSet() {
		return problemSet;
	}

	public int getPage() {
		return page;
	}

	public String getPid() {
		return pid;
	}

	public String getTextCode() {
		return textCode;
	}

	public String getChapter() {
		return chapter;
	}

	public String getSection() {
		return section;
	}

	private void parsePid() {
		String ps[] = pid.split("_");
		
		this.textCode = ps[0];
		this.chapter = ps[1];
		this.section = ps[2];
		this.problemSet = ps[3];
		this.problem = ps[4];
		this.page = Integer.parseInt(ps[5]);
	}
	
	@Override
	public String toString() {
		return pid;
	}
}
