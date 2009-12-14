package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public class CmUploadForm extends FormPanel {

    FileUploadField fileUpload;
    CmAsyncRequest callback;

    /**
     * Create Upload Field form that will call back with the upload key after
     * complete.
     * 
     * @param aid
     * @param formWidth
     * @param callback
     *            Called back with upload key after completion.
     */
    public CmUploadForm(Integer aid, CmAsyncRequest callback) {

        this.callback = callback;
        setFrame(false);
        setStyleName("register-student-upload-form");
        setStyleAttribute("padding-left", "0px");
        setStyleAttribute("padding-top", "0px");
        setStyleAttribute("padding-right", "0px");
        setStyleAttribute("padding-bottom", "0px");
        setStyleAttribute("padding", "0px");
        StringBuffer sb = new StringBuffer("/cm_admin/bulkRegister");
        sb.append("?aid=").append(aid);
        setAction(sb.toString());
        setEncoding(Encoding.MULTIPART);

        setMethod(Method.POST);
        setButtonAlign(HorizontalAlignment.CENTER);
        setWidth(500);
        setBodyBorder(false);
        setLabelWidth(110);
        setBorders(false);
        setFieldWidth(295);
        setHeaderVisible(false);
        setShim(true);

        addListener(Events.Submit, new Listener<FormEvent>() {

            public void handleEvent(FormEvent be) {

                String response = be.getResultHtml();

                Log.info("CmUploadForm: response=" + response);

                if (response.toLowerCase().indexOf("<pre") != -1) {
                    response = extractJson(response);
                    Log.info("CmUploadForm: done extracting JSON: " + response);
                }

                try {
                    Log.info("CmUploadForm: parsing JSON");
                    JSONValue rspValue = JSONParser.parse(response);
                    JSONObject rspObj = rspValue.isObject();
                    String msg = rspObj.get("msg").isString().stringValue();
                    String status = rspObj.get("status").isString().stringValue();
                    if (status.equals("Error")) {
                        Log.info("CmUploadForm: Error while reading response");
                        CatchupMathTools.showAlert(msg);
                        return;
                    }
                    String uploadKey = rspObj.get("key").isString().stringValue();

                    CmUploadForm.this.callback.requestComplete(uploadKey);
                } catch (Exception e) {
                    Log.error("CmUploadForm: Error parsing JSON", e);
                    e.printStackTrace();
                }
            }
        });

        fileUpload = new FileUploadField();
        fileUpload.setAllowBlank(false);
        fileUpload.setFieldLabel("File");
        fileUpload.setAllowBlank(false);
        fileUpload.setFieldLabel("File");
        fileUpload.setAllowBlank(false);
        fileUpload.setBorders(false);
        fileUpload.setName("bulk.reg.field");

        add(fileUpload);

        getButtonBar().setAlignment(HorizontalAlignment.RIGHT);
    }
    
    
	/** Return first set of {JSON} construct
	 *  
	 * @TODO: provide safer extraction routine.
	 * 
	 * @param html
	 * @return
	 */
    static public String extractJson(String html) {
    	String json = html.substring(html.indexOf("{"));
    	json = json.substring(0,json.indexOf("}")+1);
    	return json;
    }
}
