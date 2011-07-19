package hotmath.gwt.cm_tools.client.ui;

public enum ValidType {

	ALPHABET("^[a-zA-Z_]+$", "Alpha"), 
	ALPHANUMERIC("^[a-zA-Z0-9_]+$", "Alphanumeric"), 
	NUMERIC("^[+0-9]+$", "Numeric"),
	EMAIL("^(\\w+)([-+.][\\w]+)*@(\\w[-\\w]*\\.){1,5}([A-Za-z]){2,4}$", "Email");

	private String regex;
	private String name;

	ValidType(String regex, String name) {
		this.regex = regex;
		this.name = name;
	}

	public String getRegex() {
		return regex;
	}

	public String getName() {
		return name;
	}

}
