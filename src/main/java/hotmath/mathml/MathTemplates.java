package hotmath.mathml;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MathTemplates {

	
	 public static final MathTemplate MfracWithVariableInMn = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
				Elements els = doc.select("math mfrac mn");
		        for (Element e : els) {
		        	if(!contentIsNumber(e)) {  
		            		replaceIfNoExist(e, "1.4em");	
		            	}
		            }
			}
	};
	
	 public static final MathTemplate MfracWithNumberInMn = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
				Elements els = doc.select("math mfrac mn");
			    for (Element e : els) {
			    	replaceIfNoExist(e,  "1.2em");
			    }
			}
	};
		
	
	 public static final MathTemplate MfracWithMo = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
				Elements els = doc.select("math mfrac mo");
		        for (Element e : els) {
		        	replaceIfNoExist(e, "1.3em");
		        }
			}
	};
	
	
	 public static final MathTemplate MfracWithMi = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
				Elements els = doc.select("math mfrac mi");
		        for (Element e : els) {
		        	replaceIfNoExist(e, "1.3em");
		        }
			}
	};
	
	 public static final MathTemplate MixedNumbers = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    /** search for mixed numbers .. alter the number 
			     *  before the mfrac
			     */
			    Elements els = doc.select("math mrow mn+mfrac");
			    for (Element e : els) {
			    	replaceIfNoExist(getPreviousSibling(e), "1.2em");
			    }				
			}
	};
	
	
	 public static final MathTemplate SquareRoot = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    /** search for mixed numbers .. alter the number 
			     *  before the mfrac
			     */
			    Elements els = doc.select("math msqtr mi");
			    for (Element e : els) {
			    	replaceIfNoExist(e, "1em");
			    }		
			}
	};
  

	 public static final MathTemplate MsupMi = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math msup mi");
			    for (Element e : els) {
			    	replaceIfNoExist(e, "1em");
			    }
			}
	};
	
	
	 public static final MathTemplate MtrMtdMi = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math mtr mtd mi");
			    for (Element e : els) {
			    	replaceIfNoExist(e, "1em");
			    } 
			}
	};
	
	
	 public static final MathTemplate MrootMrowMsup = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
				Elements els = doc.select("math mroot mrow msup");
				for (Element e : els) {
					Elements mns = e.getElementsByTag("mi");
					if(mns.size() == 2) {
						replaceIfNoExist(mns.get(0), "1em");
						replaceIfNoExist(mns.get(1), "1.2em");
						
						Element par = e.parent();
						if(nextSiblingIs("mi", par)) {
							Element a = getNextSibling(par);
							a.attr("mathsize", "1.6em");
						}
					}
				}
			}
	};
	

	
	 public static final MathTemplate MnMfracMn = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math mn+mfrac mn");
			    for (Element e : els) {
			    	replaceIfNoExist(e, "1.2em");
			    }
			}
	};

    

}
