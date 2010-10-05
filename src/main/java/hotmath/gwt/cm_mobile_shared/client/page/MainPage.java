package hotmath.gwt.cm_mobile_shared.client.page;

import hotmath.gwt.cm_mobile_shared.client.ControlAction;

import java.util.ArrayList;
import java.util.List;

public class MainPage implements IPage {

	public MainPage() {

	}

	public class Tournament {
		private String mName;
		private String mRelativeUrl;

		public Tournament(String name, String relativeUrl) {
			mName = name;
			mRelativeUrl = relativeUrl;
		}

		public String getName() {
			return mName;
		}

		public String getRelativeUrl() {
			return mRelativeUrl;
		}
	}

	private ArrayList<Tournament> mTournaments = new ArrayList<Tournament>();

	public Iterable<Tournament> getTournaments() {
		return mTournaments;
	}

	@Override
	public String getBackButtonText() {
		return null;
	}

	@Override
	public String getTitle() {
		return "Catchup Math Mobile";
	}

    @Override
    public List<ControlAction> getControlFloaterActions() {
        return null;        
    }

}
