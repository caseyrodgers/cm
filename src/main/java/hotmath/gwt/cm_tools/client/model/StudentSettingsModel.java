package hotmath.gwt.cm_tools.client.model;

import hotmath.gwt.cm_core.client.model.SearchAllowMode;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;

/**
 * <code>StudentSettingsModel</code> represents the settings for a single student
 * 
 *
 */

public class StudentSettingsModel implements Response {
	boolean tutoringAvailable;
	boolean showWorkRequired;
	boolean limitGames;
	boolean stopAtProgramEnd;
	boolean disableCalcAlways;
	boolean disableCalcQuizzes;
	boolean noPublicWebLinks;
	SearchAllowMode searchAllowMode;

	public StudentSettingsModel() {}
	

	/** return the SearchAllowMode
	 *  if null, return default of ENABLED_EXCEPT_TESTS
	 *  
	 * @return
	 */
    public SearchAllowMode getSearchAllowMode() {
        return searchAllowMode != null?searchAllowMode:SearchAllowMode.ENABLED_EXCEPT_TESTS;  // defaul
    }


    public void setSearchAllowMode(SearchAllowMode searchAllowMode) {
        this.searchAllowMode = searchAllowMode;
    }


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

    public boolean isNoPublicWebLinks() {
        return noPublicWebLinks;
    }

    public void setNoPublicWebLinks(boolean noPublicWebLinks) {
        this.noPublicWebLinks = noPublicWebLinks;
    }

    @Override
    public String toString() {
        return "StudentSettingsModel [tutoringAvailable=" + tutoringAvailable + ", showWorkRequired="
                + showWorkRequired + ", limitGames=" + limitGames + ", stopAtProgramEnd=" + stopAtProgramEnd
                + ", disableCalcAlways=" + disableCalcAlways + ", disableCalcQuizzes=" + disableCalcQuizzes
                + ", noPublicWebLinks=" + noPublicWebLinks + ", searchAllowMode=" + searchAllowMode + "]";
    }

}
