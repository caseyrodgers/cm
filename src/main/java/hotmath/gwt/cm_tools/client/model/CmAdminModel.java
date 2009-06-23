package hotmath.gwt.cm_tools.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class CmAdminModel extends BaseModelData {

	private static final long serialVersionUID = -9047690530665830053L;
	
	private static final String UID_KEY = "uid";
    private static final String SUBSCRIBER_ID_KEY = "subscriberId";
    private static final String PASSCODE_KEY = "paddcode";

	public Integer getId() {
		return get(UID_KEY);
	}

	public void setId(Integer uid) {
		set(UID_KEY, uid);
	}

	public String getSubscriberId() {
		return get(SUBSCRIBER_ID_KEY);
	}

	public void setSubscriberId(String subscriberId) {
		set(SUBSCRIBER_ID_KEY, subscriberId);
	}

	public String getPassCode() {
		return get(PASSCODE_KEY);
	}

	public void setPassCode(String passCode) {
		set(PASSCODE_KEY, passCode);
	}

}
