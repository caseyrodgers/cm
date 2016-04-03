package hotmath.mathml;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MathTemplates {

	
	 public static final MathTemplate FractionsWithMtext = new MathTemplate_Base() {
			@Override
			public void processDocument(Document doc) {
				Elements els = doc.select("math mo+mn+mo+mn+mo+mfrac");
				for(Element e: els) {
					if(matchesPattern("mrow,mrow", e.children())) {
						
						Element mrow1 = e.child(0);
						Element mrow2 = e.child(1);
						
						if(matchesPattern("mtext", mrow1.children()) && matchesPattern("mtext", mrow2.children())) {
							replaceIfNoExist(mrow1.child(0), "1.3em");
							replaceIfNoExist(mrow2.child(0), "1.3em");
							
							Element mo = getPreviousSibling(e);
							mo.attr("stretchy", "false");
							
							
							Element mn = getPreviousSibling(mo);
							mo = getPreviousSibling(mn);
							mo.attr("stretchy", "false");
						}
					}
				}
			}
		};	
		
	 public static final MathTemplate FractionsWithVariables = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math mfrac");
			for(Element e: els) {
				if(matchesPattern("mrow,mn", e.children())) {
					
					Element mrow = e.child(0);
					
					if(matchesPattern("mi,mo,mn", mrow.children())) {
						replaceIfNoExist(mrow.child(0), "1.4em");
						replaceIfNoExist(mrow.child(1), "1.3em");
						replaceIfNoExist(mrow.child(2), "1.2em");
						
						
						replaceIfNoExist(e.child(1), "1.2em");
					}
				}
			}
		}
	};	

	 public static final MathTemplate ExponentWithVariableVariableToVariableCheck = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math msup");
			for(Element e: els) {
				if(matchesPattern("mi,mi", e.children())) {
					replaceIfNoExist(e.child(0), "1.1em");
					replaceIfNoExist(e.child(1), "1.4em");
				}
			}
		}
	};	

	 public static final MathTemplate ExponentWithVariableNumberToANumberCheck = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math msup");
			for(Element e: els) {
				if(matchesPattern("mn,mn", e.children())) {
					replaceIfNoExist(e.child(0), "1em");
					replaceIfNoExist(e.child(1), "1.1em");
				}
			}
		}
	};	
	
	public static final MathTemplate ExponentWithVariableNumericBaseNumericExponent = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math msup");
			for(Element e: els) {
				if(matchesPattern("mi,mn", e.children())) {
					replaceIfNoExist(e.child(0), "1.1em");
					replaceIfNoExist(e.child(1), "1.1em");
				}
			}
		}
	};	
	
	public static final MathTemplate ExponentWithVariableNumericBaseVariableExponent = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math msup");
			for(Element e: els) {
				if(matchesPattern("mn,mi", e.children())) {
					replaceIfNoExist(e.child(0), "1em");
					replaceIfNoExist(e.child(1), "1.4em");
				}
			}
		}
	};	
	
	
	public static final MathTemplate NthRootWithVariables = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math mrow mroot");
			for(Element e: els) {
				if(matchesPattern("mrow,mi", e.children())) {
					
					Element mrow = e.child(0);
					
					Element msup;
					if(matchesPattern("msup", mrow.children())) {
						msup = mrow.child(0);
						if(matchesPattern("mi,mi", msup.children())) {
							replaceIfNoExist(msup.child(0), "1em");
							replaceIfNoExist(msup.child(1), "1.2em");
							
							
							replaceIfNoExist(e.child(1), "1.3em");
						}
					}
				}
			}
		}
	};
	
	
	public static final MathTemplate NthRootWithNumbers = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math mrow mroot");
			for(Element e: els) {
				if(matchesPattern("mrow,mn", e.children())) {
					Element mrow = e.child(0);
					
					if(matchesPattern("mn", mrow.children())) {
						
					replaceIfNoExist(mrow.child(0), ".9em");
						replaceIfNoExist(e.child(1), "1.3em");
					}
				}
			}
		}
	};


	public static final MathTemplate SubscriptWithVarCheck = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("msub");
			for(Element e: els) {
				if(matchesPattern("mi,mrow", e.children())) {
				
					if(matchesPattern("mi,mo,mn", e.child(1).children())) {
						
						replaceIfNoExist(e.child(0), null);
						
						Element mrow = e.child(1);
						replaceIfNoExist(mrow.child(0), "1.2em");
						replaceIfNoExist(mrow.child(1), "1.2em");
						replaceIfNoExist(mrow.child(2), "1.2em");
					}
				}
			}
		}
	};
	
	
	
	
	
	public static final MathTemplate Equation = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("mrow");
			for(Element e: els) {
				if(matchesPattern("mn,mi,mo,mn,mi,mo,mn", e.children())) {
				
					replaceIfNoExist(e.child(0), null);
					replaceIfNoExist(e.child(1), "1.1em");
					replaceIfNoExist(e.child(2), null);
					replaceIfNoExist(e.child(3), null);
					replaceIfNoExist(e.child(4), "1.1em");
					replaceIfNoExist(e.child(5), null);
					replaceIfNoExist(e.child(6), null);
				}
			}
		}
	};

	
	public static final MathTemplate SquareRoots = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("msqrt");
			for(Element e: els) {
				
				if(e.children().size() == 1) {
					
					if( e.child(0).tagName().equals("mi") ) {
						replaceIfNoExist(e.child(0), "1.1em");
					}
					
				}
			}
		}
	};
	
	
	public static final MathTemplate MixedNumber = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("mn+mfrac");
			for(Element e: els) {
				
				if(e.children().size() == 2) {
					
					if(matchesPattern("mn,mn", e.children())) {
						
						replaceIfNoExist(getPreviousSibling(e), "1em");
						
						replaceIfNoExist(e.child(0), "1.2em");
						replaceIfNoExist(e.child(1), "1.2em");
					}
					
				}
			}
		}
	};

	
	
	public static final MathTemplate FactionsWithMoVariablesAndNumbers = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math mrow mfrac");
			for(Element e: els) {
				if(e.children().size() == 2) {
					if(e.child(0).tagName().equals("mn")) {
						if(e.child(1).tagName().equals("mn")) {
							e.child(0).attr("mathsize", "1.2em");
							e.child(1).attr("mathsize", "1.2em");
						}
					}
				}
			}
		}
	};
	static public MathTemplate Fractions = new MathTemplate_Base() {

		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math mrow mfrac");
			for(Element e: els) {
				if(e.children().size() == 2) {
					if(e.child(0).tagName().equals("mrow")) {
						if(e.child(1).tagName().equals("mi")) {
							Element mrow = e.child(0);
							
							if( matchesPattern("mn,mo,mi,mo,mi", mrow.children())) {
								
								replaceIfNoExist(mrow.child(0), "1.2em");
								replaceIfNoExist(mrow.child(1), "1.3em");
								replaceIfNoExist(mrow.child(2), "1.3em");
								replaceIfNoExist(mrow.child(3), "1.3em");
								replaceIfNoExist(mrow.child(4), "1.3em");
								
								// bottom mi
								replaceIfNoExist(e.child(1), "1.3em");
							}
						}
					}
				}
			}
		}
	};
	
	
	static public MathTemplate FactionsWithMlMoVariablesAndNumbers = new MathTemplate_Base() {
		@Override
		public void processDocument(Document doc) {
			Elements els = doc.select("math mrow mfrac");
			for(Element e: els) {
				if(e.children().size() == 2) {
					if(e.child(0).tagName().equals("mrow")) {
						if(e.child(1).tagName().equals("mi")) {
							Element mrow = e.child(0);
							
							if( matchesPattern("mn,mo,mi,mo,mi", mrow.children())) {
								
								replaceIfNoExist(mrow.child(0), "1.2em");
								replaceIfNoExist(mrow.child(1), "1.3em");
								replaceIfNoExist(mrow.child(2), "1.3em");
								replaceIfNoExist(mrow.child(3), "1.3em");
								replaceIfNoExist(mrow.child(4), "1.3em");
								
								// bottom mi
								replaceIfNoExist(e.child(1), "1.3em");
							}
						}
					}
				}
			}
		}		
	};
}
