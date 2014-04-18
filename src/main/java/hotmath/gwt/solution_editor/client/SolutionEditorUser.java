package hotmath.gwt.solution_editor.client;

public class SolutionEditorUser {
	
	String userName;
	String password;
	
	public SolutionEditorUser(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
