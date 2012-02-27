package hotmath.gwt.cm_rpc.client.rpc;


/** Return solution information for a given user and pid
 * 
 * @author casey
 *
 */
public class GetSolutionTemplateAction extends ActionBase implements Action<SolutionTemplateInfo> {
    String templateKey;

    public GetSolutionTemplateAction() {}
    
    public GetSolutionTemplateAction(String templateKey) {
        this.templateKey = templateKey;
        setActionInfo(new ActionInfo(ActionType.STUDENT));
    }

    @Override
    public String toString() {
        return "GetSolutionTemplateAction [templateKey=" + templateKey + "]";
    }

    public String getTemplateKey() {
        return templateKey;
    }

    public void setTemplateKey(String templateKey) {
        this.templateKey = templateKey;
    }
}
