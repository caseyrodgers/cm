package hotmath.gwt.cm_rpc.client.model;

import hotmath.gwt.cm_rpc_core.client.rpc.Response;

import java.util.List;

public class WhiteboardTemplatesResponse implements Response {
    
    private List<WhiteboardTemplate> templates;

    public WhiteboardTemplatesResponse(){}
    
    public WhiteboardTemplatesResponse(List<WhiteboardTemplate> templates) {
        this.templates = templates;
    }

    public List<WhiteboardTemplate> getTemplates() {
        return templates;
    }

    public void setTemplates(List<WhiteboardTemplate> templates) {
        this.templates = templates;
    }

    /** Get JSON used by ShowWork/Whiteboard to install templates
     * 
     * @return
     */
    public String getJsonRepresentation() {
        String files = "";
        for(WhiteboardTemplate template: templates) {
            String f = template.getPath();
            if(files.length() > 0) {
                files += ", ";
            }
            
            if(!f.startsWith("/")) {
                f = "/" + f;
            }
            files += "\"" + f + "\"";
        }
        
        String json = "{" +
                "\"type\":\"img\"," +
                "\"path\":\"\"," +
                "\"icon\":\"tn\"," +
                "\"list\":["+ files + "]" + 
                "}";
        
        return json;
    }
}
