package hotmath.cm.server.model;

/** a single quiz selection
 * 
 * @author casey
 *
 */
public class QuizSelection {

	private String pid;
	private boolean isCorrect;
	private int choice;

	public QuizSelection(String pid, int choice, boolean isCorrect) {
		this.pid = pid;
		this.choice = choice;
		this.isCorrect = isCorrect;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public boolean isCorrect() {
		return isCorrect;
	}

	public void setCorrect(boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}

	
}
