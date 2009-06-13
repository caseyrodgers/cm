package hotmath.gwt.cm_tools.client.data;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Parent session data, containing a single active session used to encapsulate
 * session data
 * 
 * 
 * @author Casey
 * 
 */
public class PrescriptionData {
	List<String> sessionTopics = new ArrayList<String>();
	PrescriptionSessionData currSession;

	public PrescriptionData() {
		// 
	}


	/** Create and parse the JSON into PrescriptionData object
	 * 
	 * @param json
	 * @throws Exception
	 */
	public PrescriptionData(String json) throws Exception {
		parseJson(json);
	}

	/**
	 * Take JSON string and turn it into PrescriptionData
	 * 
	 * @param json
	 */

	private void parseJson(String json) {
		JSONValue jsonValue = JSONParser.parse(json);
		JSONObject o = jsonValue.isObject();

		// first the prescription data
		PrescriptionData preData = this;
		JSONArray topics = o.get("sessionTopics").isArray();
		for (int i = 0; i < topics.size(); i++) {
			preData.getSessionTopics().add(
					topics.get(i).isString().stringValue());
		}

		JSONObject op = o.get("currSession").isObject();
		JSONArray resources = op.get("inmhResources").isArray();

		PrescriptionSessionData sd = new PrescriptionSessionData();
		sd.setName(op.get("name").isString().stringValue());
		sd.setTopic(op.get("topic").isString().stringValue());
		sd.setSessionNumber((int) op.get("sessionNumber").isNumber()
				.doubleValue());
		for (int i = 0; i < resources.size(); i++) {
			JSONObject r = resources.get(i).isObject();
			PrescriptionSessionDataResource resource = new PrescriptionSessionDataResource();
			resource.setType(r.get("type").isString().stringValue());
			resource.setLabel(r.get("label").isString().stringValue());
			
			resource.setDescription(r.get("description").isString().stringValue());

			JSONArray items = r.get("items").isArray();
			for (int j = 0; j < items.size(); j++) {
				JSONObject item = items.get(j).isObject();

				InmhItemData itemData = new InmhItemData();
				itemData.setFile(item.get("file").isString().stringValue());
				itemData.setTitle(item.get("title").isString().stringValue());
				itemData.setType(item.get("type").isString().stringValue());
				itemData.setViewed(item.get("viewed").isBoolean().booleanValue());

				resource.getItems().add(itemData);
			}
			sd.getInmhResources().add(resource);
		}
		preData.setCurrSession(sd);
	}

	public List<String> getSessionTopics() {
		return sessionTopics;
	}

	public void setSessionTopics(List<String> sessionTopics) {
		this.sessionTopics = sessionTopics;
	}

	public PrescriptionSessionData getCurrSession() {
		return currSession;
	}

	public void setCurrSession(PrescriptionSessionData currSession) {
		this.currSession = currSession;
	}
}
