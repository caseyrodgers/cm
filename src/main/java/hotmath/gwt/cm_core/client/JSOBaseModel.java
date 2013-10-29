package hotmath.gwt.cm_core.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public abstract class JSOBaseModel {

    protected JSOModel data;

    public JSOBaseModel(JSOModel data) {
        this.data = data;
    }

    public String get(String field) {
        String val = this.data.get(field);
        if (val != null && "null".equals(val) || "undefined".equals(val)) {
            return null;
        } else {
            return escapeHtml(val);
        }
    }

    public Map<String, String> getFields() {
        Map<String, String> fieldMap = new HashMap<String, String>();

        if (data != null) {
            JsArrayString array = data.keys();

            for (int i = 0; i < array.length(); i++) {
                fieldMap.put(array.get(i), data.get(array.get(i)));
            }
        }
        return fieldMap;
    }

    private static String escapeHtml(String maybeHtml) {
        final Element div = DOM.createDiv();
        DOM.setInnerText(div, maybeHtml);
        return DOM.getInnerHTML(div);
    }
}