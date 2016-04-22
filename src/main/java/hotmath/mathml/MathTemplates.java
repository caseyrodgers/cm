package hotmath.mathml;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class MathTemplates {

	/** 
	 * rule: every mi, every mn
     * set: 1.2em
	 */
	 public static final MathTemplate EveryMnMi = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
				/** todo: how to do select with 'or'
				 * 
				 */
				Elements els = doc.select("math mn");
		        for (Element e : els) {
		        	replaceIfNoExist(e, "1.2em");
		        }
		        
				els = doc.select("math mi");
		        for (Element e : els) {
		        	replaceIfNoExist(e, "1.2em");
		        }

			}

			@Override
			public String getRuleName() {
				return "EveryMnMi";
			}
	};
	
	
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MfracWithVariableInMn";
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MfracWithNumberInMn";
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MfracWithMo";
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MfracWithMi";
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MixedNumbers";
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
			
			public String getRuleName() {
				return "SquareRoot";
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MsupMi";
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MtrMtdMi";
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MrootMrowMsup";
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

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MnMfracMn";
			}
	};

	 public static final MathTemplate MfracMtext = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math mfrac mtext");
			    for (Element e : els) {
			    	replaceIfNoExist(e, "1.3em");
			    }
			}
			
			public String getRuleName() {
				return "MfracMtext";
			}
	};

	
	/** 
	 * rule: inside mfrac, exactly 2 mrows
     * set: if one mrow has mfrac, make sure both have mtext with 1.3em
	 */
	 public static final MathTemplate MFracMtextBalance = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math mfrac");
			    for (Element e : els) {
			    	if(matchesPattern("mrow,mrow", e.children())) {
			    		
			    		/** if one mrow has mtext, then they all must
			    		 * 
			    		 */
			    		if(hasMtext(e.child(0)) || hasMtext(e.child(1))) {
			    			balanceMtext(e.child(0), e.child(1));
			    		}
			    	}
			    }
			}

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MFracMtextBalance";
			}
	};

	
	
	/** 
	 * 	rule: inside msup last mn or mi with previous sibling mrow
	 * set:  mathsiz=1.2em
	 */
	 public static final MathTemplate MsupLastMnWithPrevSiblingMrow = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math msup");
			    for (Element e : els) {
			    	
			    	// if last child is a mn or mn
			    	Elements kids = e.children();
			    	Element lastNode = kids.get(kids.size()-1);
			    	if(lastNode.tagName().equals("mn") || lastNode.tagName().equals("mi")) {
			    		Element ps = getPreviousSibling(lastNode);
			    		if(ps != null && ps.tagName().equals("mrow")) {
			    			replaceIfNoExist(lastNode,"1.2em");
			    		}
			    	}
			    }
			}

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MsupLastMnWithPrevSiblingMrow";
			}
	};

	/** rule: msup with exactly two mi
	 *  set: first mi 1.1em, second mi 1.4em
	 * 
	 */
	 public static final MathTemplate MsupWithExactlyTwoMi = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math msup");
			    for (Element e : els) {
			    	
			    	if(matchesPattern("mi,mi", e.children())) {
			    		replaceIfNoExist(e.child(0), "1.1em");
			    		replaceIfNoExist(e.child(1), "1.4em");
			    	}
			    }
			}

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MsupWithExactlyTwoMi";
			}
	};
	
	/** 
	 * 	rule: msup with exactly two mn
	 *   set: first mi 1em, second mi 1.1em
	 */
	 public static final MathTemplate MsupWithExactlyTwoMn = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math msup");
			    for (Element e : els) {
			    	
			    	if(matchesPattern("mn,mn", e.children())) {
			    		replaceIfNoExist(e.child(0), "1em");
			    		replaceIfNoExist(e.child(1), "1.1em");
			    	}
			    }
			}

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MsupWithExactlyTwoMn";
			}
	};

	/** rule: msup with two elements mi, mn in that order
	 * set:  set both to 1.1em
	 */
	 public static final MathTemplate MsupWithExactlyMiMn = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math msup");
			    for (Element e : els) {
			    	
			    	if(matchesPattern("mi,mn", e.children())) {
			    		replaceIfNoExist(e.child(0), "1.1em");
			    		replaceIfNoExist(e.child(1), "1.1em");
			    	}
			    }
			}

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MsupWithExactlyMiMn";
			}
	};
	

	/** 
	 * 	
	     rule: msup with excactly two elements mn, mi in that order
	     set: mn 1em, mi 1.4em
	 */
	 public static final MathTemplate MsupWithExactlyMnMi = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
			    Elements els = doc.select("math msup");
			    for (Element e : els) {
			    	
			    	if(matchesPattern("mn,mi", e.children())) {
			    		replaceIfNoExist(e.child(0), "1em");
			    		replaceIfNoExist(e.child(1), "1.4em");
			    	}
			    }
			}

			@Override
			public String getRuleName() {
				// TODO Auto-generated method stub
				return "MsupWithExactlyMnMi";
			}
	};

	
	
	
	
	/** does this element have an mtext as 
	 *  a direct child
	 *  
	 * @param child
	 * @return
	 */
	protected static boolean hasMtext(Element element) {
		for(Element c: element.children()) {
			if(c.tagName().equals("mtext")) {
				return true;
			}
		}
		return false;
	}


	protected static void balanceMtext(Element e1, Element e2) {
		
		Element[] elems = new Element[]{e1, e2};
		for(Element e: elems) {
			boolean foundMtext=false;
			for(Element c: e.children()) {
				if(c.tagName().equals("mtext")) {
					foundMtext=true;
					break;
				}
			}
			if(!foundMtext) {
				Element mtext = new Element(Tag.valueOf("mtext"),"");
				mtext.html("&nbsp;");
				e.appendChild(mtext);
			}
		}
	}

}
