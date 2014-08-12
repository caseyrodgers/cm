package hotmath.gwt.cm_core.client.model;

import hotmath.gwt.cm_core.client.JSOBaseModel;
import hotmath.gwt.cm_core.client.JSOModel;

/**
 * Defines a Widget Definition as defined in JSON
 * 
 * @author casey
 * 
 */
public class WidgetDefModel extends JSOBaseModel {

	String id;
	String type;
	String value;
	Integer width;
	Integer height;
	String format;
	private String ansFormat;
	boolean allowMixed;
	private boolean useBase64;

	public WidgetDefModel() {
		this(JSOModel.fromJson("{}"));
	}

	public WidgetDefModel(JSOModel data) {
		super(data);
		id = get("id");
		type = get("type");
		value = get("value");
		width = getInt(get("width"));
		height = getInt(get("height"));
		format = get("format");

		String am = get("allowMixed");
		if (am != null && am.equals("true")) {
			allowMixed = true;
		}
	}

	public void setUseBase64(boolean yesNo) {
		this.useBase64 = yesNo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Boolean getAllowMixed() {
		return allowMixed;
	}

	public void setAllowMixed(Boolean allowMixed) {
		this.allowMixed = allowMixed;
	}

	/**
	 * return the complete definition of this widget
	 * 
	 * @return
	 */
	public String getJson() {
		String formatStr = (format != null ? format : "");
		String ansFormatStr = (ansFormat != null ? ansFormat : "");

		String allowMixedStr = ", allowMixed: " + allowMixed;
		String json = "{type:'" + type + "'" + ", useBase64: " + useBase64
				+ ",value:'" + value + "', format:'" + formatStr
				+ "', ans_format: '" + ansFormatStr + "'" + allowMixedStr;

		json += ", width:" + width + ", height:" + height + "}";
		return json;
	}

	public String getWidgetHtml() {
		return createWidgetHtml(getJson());
	}

	private String createWidgetHtml(String json) {
		String widgetDiv = "<div name='hm_flash_widget'><div name='hm_flash_widget_def' style='display: none'>"
				+ json + "</div></div>";
		return widgetDiv;
	}

	public Integer getInt(String o) {
		try {
			return Integer.parseInt(o);
		} catch (Exception e) {
			return 0;
		}
	}

	public void setAnsFormat(String ansFormat) {
		this.ansFormat = ansFormat;
	}

}