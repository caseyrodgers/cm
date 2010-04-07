package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_tools.client.ui.PassPercentCombo;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * <code>StudentSettingsModel</code> represents the settings for a single student
 * 
 * @author bob
 *
 */

public class StudentSettingsModel implements IsSerializable {

	Boolean tutoringAvailable;
	Boolean showWorkRequired;
	Boolean limitGames;
	Boolean stopAtProgramEnd;

	public Boolean getTutoringAvailable() {
		return tutoringAvailable;
	}

	public void setTutoringAvailable(Boolean tutoringAvailable) {
		this.tutoringAvailable = tutoringAvailable;
	}

	public Boolean getShowWorkRequired() {
		return showWorkRequired;
	}

	public void setShowWorkRequired(Boolean showWorkRequired) {
		this.showWorkRequired = showWorkRequired;
	}

	public Boolean getLimitGames() {
		return limitGames;
	}

	public void setLimitGames(Boolean limitGames) {
		this.limitGames = limitGames;
	}

	public Boolean getStopAtProgramEnd() {
		return stopAtProgramEnd;
	}

	public void setStopAtProgramEnd(Boolean stopAtProgramEnd) {
		this.stopAtProgramEnd = stopAtProgramEnd;
	}

}
