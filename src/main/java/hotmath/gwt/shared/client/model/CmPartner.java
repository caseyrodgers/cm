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
	
	LCOM("lcom","Learning.Com", "/lcom/logo.png", "javascript:window.close();");
	
	public String key;
	public String name;
	public String logoImage;
	public String onCloseLink;

	CmPartner(String key, String name, String logoImage,String onCloseLink) {
		this.key = key;
		this.name = name;
		this.logoImage = logoImage;
		this.onCloseLink = onCloseLink;
	}
}