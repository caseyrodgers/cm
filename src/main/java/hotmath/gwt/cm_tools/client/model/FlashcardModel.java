package hotmath.gwt.cm_tools.client.model;

/**
 * 
 * @author bob
 *
 */

public class FlashcardModel {
	String category;
	String language;
	String description;
	String location;

	public FlashcardModel(String language, String category, String description, String location) {
		this.language = language;
		this.category = category;
		this.description = description;
		this.location = location;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

}
