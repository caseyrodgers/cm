package hotmath.gwt.cm_tools.client.ui;


public class Topic {
    
	private String topic;
    private String text;

    public Topic(String name) {
		this.topic = name;
		this.text = name;
	}

	public String getTopic() {
		return this.topic;
	}

    public String getText() {
        return text;
    }
}