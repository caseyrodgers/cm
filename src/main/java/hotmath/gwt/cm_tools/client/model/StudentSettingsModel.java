package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_rpc.client.rpc.Response;

/**
 * <code>StudentSettingsModel</code> represents the settings for a single student
 * 
 * 
 * KC: I changed to use native boolean instead of the Boolean wrapper to get around
 * NullPointer problems when reading (autoboxing) from null values.   For example, if limitGames
 * is null an error will be generated in JDBC due to Java's autoboxing feature ...
 * (ie, it wants a boolean .. so it does a ((Boolean)val).toString() .. which 
 * generates a NullPointer.  So, using the native types we are insulated from having
 * to constantly check for null.
 * 
 * 
 * 
 * @author bob
 *
 */

public class StudentSettingsModel implements Response {

	boolean tutoringAvailable;
	boolean showWorkRequired;
	boolean limitGames;
	boolean stopAtProgramEnd;
	boolean disableCalcAlways;
	boolean disableCalcQuizzes;

	public StudentSettingsModel() {}
	
	public boolean getTutoringAvailable() {
		return tutoringAvailable;
	}

	public void setTutoringAvailable(boolean tutoringAvailable) {
		this.tutoringAvailable = tutoringAvailable;
	}

	public boolean getShowWorkRequired() {
		return showWorkRequired;
	}

	public void setShowWorkRequired(boolean showWorkRequired) {
		this.showWorkRequired = showWorkRequired;
	}

	public boolean getLimitGames() {
		return limitGames;
	}

	public void setLimitGames(boolean limitGames) {
		this.limitGames = limitGames;
	}

	public boolean getStopAtProgramEnd() {
		return stopAtProgramEnd;
	}

	public void setStopAtProgramEnd(boolean stopAtProgramEnd) {
		this.stopAtProgramEnd = stopAtProgramEnd;
	}

	public boolean getDisableCalcAlways() {
		return disableCalcAlways;
	}

	public void setDisableCalcAlways(boolean disableCalcAlways) {
		this.disableCalcAlways = disableCalcAlways;
	}

	public boolean getDisableCalcQuizzes() {
		return disableCalcQuizzes;
	}

	public void setDisableCalcQuizzes(boolean disableCalcQuizzes) {
		this.disableCalcQuizzes = disableCalcQuizzes;
	}

}
