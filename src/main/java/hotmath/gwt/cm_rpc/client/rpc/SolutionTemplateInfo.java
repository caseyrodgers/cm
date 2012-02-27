package hotmath.gwt.cm_rpc.client.rpc;


public class SolutionTemplateInfo implements Response {
    String key;
    String template;
	public SolutionTemplateInfo() {}
	public SolutionTemplateInfo(String key, String template) {
	    this.key = key;
	    this.template = template;
	}
    @Override
    public String toString() {
        return "SolutionTemplateInfo [key=" + key + ", template=" + template + "]";
    }
    public String getKey() {
        return key;
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getTemplate() {
        return template;
    }
    public void setTemplate(String template) {
        this.template = template;
    }
}
