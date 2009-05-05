package hotmath.gwt.cm.client.ui;



/** Main CM GUI Controller
 * 
 * Controls the overall flow of CM, manages the 
 * different GUI controls the current context.
 * 
 * 
 */
public class CmModelDoNotNeed {
	
	CmGuiDefinition resource;
	private CmModelDoNotNeed() {
	}
	
	public CmModelDoNotNeed(CmGuiDefinition initialResource) {
		this();
		resource = initialResource;
	}
	
	
	/** Return the current GUI definition
	 * 
	 * @return
	 */
	public CmGuiDefinition getGuiDef() {
		return resource;
	}

	
	/** Set the current GUI definition
	 * 
	 * @param resource
	 */
	public void setResource(CmGuiDefinition resource) {
		this.resource = resource;
	}
}
