package hotmath.gwt.cm_tools.client.ui;

import hotmath.gwt.cm_tools.client.CatchupMathTools;
import hotmath.gwt.shared.client.data.CmAsyncRequest;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.HTML;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent;
import com.sencha.gxt.widget.core.client.event.SubmitCompleteEvent.SubmitCompleteHandler;
import com.sencha.gxt.widget.core.client.form.FileUploadField;
import com.sencha.gxt.widget.core.client.form.FormPanel;

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
        // setFrame(false);
        setStyleName("register-student-upload-form");
//        setStyleAttribute("padding-left", "0px");
//        setStyleAttribute("padding-top", "0px");
//        setStyleAttribute("padding-right", "0px");
//        setStyleAttribute("padding-bottom", "0px");
//        setStyleAttribute("padding", "0px");
        
        StringBuffer sb = new StringBuffer("/cm_admin/bulkRegister");
        sb.append("?aid=").append(aid);
        setAction(sb.toString());
        setEncoding(Encoding.MULTIPART);

        setMethod(Method.POST);
        //setButtonAlign(HorizontalAlignment.CENTER);
        setWidth(500);
        //setBodyBorder(false);
        setLabelWidth(110);
        setBorders(false);
        //setFieldWidth(295);
        //setHeaderVisible(false);
        //setShim(true);
        
        addSubmitCompleteHandler(new SubmitCompleteHandler() {
            
            @Override
            public void onSubmitComplete(SubmitCompleteEvent event) {
                
                String response = event.getResults();

                CmLogger.debug("CmUploadForm: response=" + response);

                if (response.toLowerCase().indexOf("<pre") != -1) {
                    response = extractJson(response);
                    CmLogger.info("CmUploadForm: done extracting JSON: " + response);
                }

                String uploadKey = null;
                String status = "";
                String msg = "";
                try {
                    CmLogger.debug("CmUploadForm: parsing JSON");
                    JSONValue rspValue = JSONParser.parse(response);
                    JSONObject rspObj = rspValue.isObject();
                    msg = rspObj.get("msg").isString().stringValue();
                    status = rspObj.get("status").isString().stringValue();
                    if (status.equals("Error")) {
                        CmLogger.info("CmUploadForm: Error while reading response");
                        CatchupMathTools.showAlert(msg);
                        return;
                    }
                    uploadKey = rspObj.get("key").isString().stringValue();

                } catch (Exception e) {
                    CmLogger.error("CmUploadForm: Error parsing JSON", e);
                    e.printStackTrace();
                }
                if (uploadKey != null) {
                	if (status.equalsIgnoreCase("warning")) {
                		CatchupMathTools.showAlert("Warning", msg, CmUploadForm.this.callback, uploadKey);
                	}
                	else {
                        CmUploadForm.this.callback.requestComplete(uploadKey);
                	}
                }
                else
                    CatchupMathTools.showAlert("There was a problem uploading your file, please re-try.");

            }
        });

        fileUpload = new FileUploadField();
        fileUpload.setAllowBlank(false);
        //fileUpload.setFieldLabel("File");
        fileUpload.setAllowBlank(false);
        fileUpload.setBorders(false);
        fileUpload.setName("bulk.reg.field");

//        String allowedContentTypes = "text/plain,text/tab-separated-values," +
//            "application/excel,application/vnd.ms-excel,application/x-excel,application/x-msexcel";
//        fileUpload.setAccept(allowedContentTypes);

        add(fileUpload);
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
