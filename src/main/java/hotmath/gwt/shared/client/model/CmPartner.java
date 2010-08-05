package hotmath.gwt.shared.client.model;

/** represents a CM Parnter.  
 *  
 *  Defines various pieces of information that allow for 
 *  third-party branding of CM.
 *  
 * @author casey
 *
 */
public enum CmPartner {
	
	LCOM("lcom","Learning.Com", "/lcom/logout_admin.png","/lcom/logout_student.png","javascript:window.close();");
	
	public String key;
	public String name;
	public String logoutImageAdmin,logoutImageStudent;
	public String onCloseLink;

	CmPartner(String key, String name, String logoutImageAdmin, String logoutImageStudent, String onCloseLink) {
		this.key = key;
		this.name = name;
		this.logoutImageAdmin = logoutImageAdmin;
		this.logoutImageStudent = logoutImageStudent;
		this.onCloseLink = onCloseLink;
	}
}