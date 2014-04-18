package hotmath.gwt.solution_editor.client;

import hotmath.gwt.solution_editor.client.SolutionEditorLoginPanel.SolutionEditorCallback;

import com.google.code.gwt.storage.client.Storage;


public class Login {

	private static Login __instance;

	public static Login getInstance() {
		if (__instance == null) {
			__instance = new Login();
		}

		return __instance;
	}

	
	public SolutionEditorUser getUser() {
		return _user;
	}
	
	public void logout() {
		_user = null;
		Storage.getLocalStorage().removeItem("user");
	}
	
	SolutionEditorUser _user;
	
	public void login() {
		final Storage storage = Storage.getLocalStorage();
		String user = storage.getItem("user");
		new SolutionEditorLoginPanel(user, new SolutionEditorCallback() {
			@Override
			public void loggedIn(SolutionEditorUser user) {
				_user = user;
				storage.setItem("user",  user.getUserName());
			}
		});
	}
	
	public void makeSureLoggedIn() {
		final Storage storage = Storage.getLocalStorage();
		String user = storage.getItem("user");
		if(user != null) {
			_user = new SolutionEditorUser(user, user);
		}
		else {
			login();
		}
	}
}
