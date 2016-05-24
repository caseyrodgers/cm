package hotmath.mathml;

import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class MathTemplate_Base implements MathTemplate {

	@Override
	abstract public boolean processDocument(Document doc);
	abstract public String getRuleName();

    /** pattern is token containing the pattern tag, and the mathsize 
     *  to use if pattern matched.
     *  
     * @param pattern
     * @param children
     * @return
     */
     static public boolean matchesPattern(String pattern, Elements children) {
    	String ptn[] = pattern.split(",");
    	if(ptn.length != children.size()) {
    		return false;
    	}
    	
    	for(int i=0;i<children.size();i++) {
    		
    		String patTag = ptn[i].split(":")[0];
    		if(!patTag.equals(children.get(i).tagName())) {
	    			return false;
	    	}
    	}
    	return true;
	}	

 	/** Replace mathsize, only if it is not already set
      * @param ex 
      * 
      * @param prop
      */
 	void replaceIfNoExist(Element ex, String prop) {
     	if(!ex.hasAttr("mathsize")) {
     		if(prop == null) {
     			ex.removeAttr("mathsize");
     		}
     		else {
     			ex.attr("mathsize", prop);
     		}
     	}		
 	}
 	
 	
	Element getNextSibling(Element e) {
		Element p = e.parent();
		for(int i=0;i<p.children().size();i++) {
			Element c = p.children().get(i);
			if(e == c) {
				return p.children().get(i+1);
			}
		}
		return null;
	}
	
	Element getPreviousSibling(Element e) {
		Element p = e.parent();
		for(int i=0;i<p.children().size();i++) {
			Element c = p.children().get(i);
			if(e == c) {
				if(i > 0) {
				    return p.children().get(i-1);
				}
				else {
					break;
				}
			}
		}
		return null;
	}
	
	boolean previousSiblingIs(String tagName, Element e) {
		Element p = e.parent();
		for(int i=0;i<p.children().size();i++) {
			Element c = p.children().get(i);
			if(e == c) {
				if(i > 0 && p.children().get(i-1).tagName().equals(tagName)) {
					return true;
				}
			}
		}
		return false;
	}

	


	public boolean contentIsNumber(Element e) {
    	String val = e.text();
    	return NumberUtils.isNumber(val);
	}
	
	

	public boolean nextSiblingIs(String tagName, Element e) {
		Element p = e.parent();
		for(int i=0;i<p.children().size();i++) {
			Element c = p.children().get(i);
			if(e == c) {
				if(p.children().size() > i+1 && p.children().get(i+1).tagName().equals(tagName)) {
					return true;
				}
			}
		}
		return false;
	}



}



