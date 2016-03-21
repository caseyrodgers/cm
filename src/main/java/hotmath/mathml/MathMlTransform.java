package hotmath.mathml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import hotmath.cm.util.CatchupMathProperties;

/**
 * 
 * Process MathML transformations to add customized
 * mathml attributes.
 * 
 * 
 */
public class MathMlTransform {
	
	
	public MathMlTransform() {}
	
	
    /**
     * process any mathml with customize styles
     *
     * 
     * 
     * 
     * @param solutionHtml
     * @return
     * @throws Exception
     */
    public String processMathMlTransformations(String solutionHtml) throws Exception {
    	
    	/** we do not want to process html entitites (ie, turn them into extended chars)
    	 * 
    	 *  The only way I can see to do that is hide them ..
    	 *  What we want to a JSoup.EscapeMode.noprocess .. but, that is not an option
    	 *  
    	 *  hack around .. by replacing all '@' charts before transformatiion, then replace back...
    	 *  this is important because the processed text is embedded as json and delivered to
    	 *  client ... 
    	 *  
    	 *  
    	 */
        try {
        	solutionHtml = solutionHtml.replace("&", "+||+");
            Document doc = Jsoup.parse(solutionHtml,"", Parser.xmlParser());

            CatchupMathProperties p = CatchupMathProperties.getInstance();

            
            /** add mathsize to each mfrac number
             *
             *  add to mn
             */
            String miProp = p.getProperty("mathml.mi", "1em");
            String miFracProp = p.getProperty("mathml.mfrac.mi", "1.3em");
            
            String normalFraction = p.getProperty("mathml.mfrac.mn", "1.2em");
            String propMixWhole = p.getProperty("mathml.mixed.mn.mfrac", "1em");
            String propMixFract = p.getProperty("mathml.mixed.mn.mfrac.mn", "1.2em"); 
            String propSqrtMi = p.getProperty("mathml.sqrt.mi", "1em");
            String propMsupMi = p.getProperty("mathml.msup.mi", "1em");
            String propMrootMi1 = p.getProperty("mathml.mroot.mrow.msup.mi.1", "1em");
            String propMrootMi2 = p.getProperty("mathml.mroot.mrow.msup.mi.2", "1.2em");
            String propMrootMiRow = p.getProperty("mathml.mroot.mrow.mi", "1.6em");
            String propMtrMtdMi = p.getProperty("mathml.mtr.mtd.mi", "1em");
            String propMsupFirst = p.getProperty("mathml.msup", "1em");
            String propMsupSecond = p.getProperty("mathml.msup", "1.1em");

            
            String propMrowMnFrac = p.getProperty("mathml.mrow.mn.mfrac", "1.2em");
            

            /** Apply broad matches first, then more specific 
             * 
             */

            /** specific/absolute styles first (no calculations)
             * 
             */
            Elements els = doc.select("math mfrac mn");
            for (Element e : els) {
            	replaceIfNoExist(e,  normalFraction);
            }
            /** modify both mn and mo inside mfrac 
             * 
             */
            els = doc.select("math mfrac mn");
            for (Element e : els) {
            	replaceIfNoExist(e, miFracProp);
            }
            els = doc.select("math mfrac mo");
            for (Element e : els) {
            	replaceIfNoExist(e, miFracProp);
            }
            els = doc.select("math mfrac mi");
            for (Element e : els) {
            	replaceIfNoExist(e, miFracProp);
            }            
            /** search for mixed numbers .. alter the number 
             *  before the mfrac
             */
            els = doc.select("math mrow mn mfrac");
            for (Element e : els) {
            	replaceIfNoExist(e.parent(), propMrowMnFrac);
            }
            /** Square root
             * 
             */
            els = doc.select("math msqtr mi");
            for (Element e : els) {
            	replaceIfNoExist(e, propSqrtMi);
            }
            els = doc.select("math msup mi");
            for (Element e : els) {
            	replaceIfNoExist(e, propMsupMi);
            }
            els = doc.select("math mtr mtd mi");
            for (Element e : els) {
            	replaceIfNoExist(e, propMtrMtdMi);
            }            
            els = doc.select("math mroot mrow msup");
            for (Element e : els) {
            	Elements mns = e.getElementsByTag("mi");
            	if(mns.size() == 2) {
            		replaceIfNoExist(mns.get(0), propMrootMi1);
            		replaceIfNoExist(mns.get(1), propMrootMi2);
            		
            		Element par = e.parent();
            		if(nextSiblingIs("mi", par)) {
            			Element a = getNextSibling(par);
            			
            			a.attr("mathsize", propMrootMiRow);
            		}
            	}
            }

            
            
            /** Styles that require some calculation
             * 
             */
            
            /** first mfrac man as a sibling of mn
             * 
             */
            els = doc.select("math mn+mfrac mn");
            for (Element e : els) {
            	replaceIfNoExist(e, propMixFract);
            }
            /** second mn in a msup 
             * 
             */
            els = doc.select("math msup");
            for (Element e : els) {
                Elements mns = e.getElementsByTag("mn");
                if (mns.size() == 2) {
                	replaceIfNoExist(mns.get(0), propMsupFirst);
                	replaceIfNoExist(mns.get(1), propMsupSecond);
                }
            }
            /** look for mn preceding a mfrac
            /** for a mixed fraction
             */
            els = doc.select("math mn");
            for(Element e: els) {
            	if(nextSiblingIs("mfrac", e)) {
            		replaceIfNoExist(e, propMixWhole);
            	}
            }

            
            
            /** The very generic styles that will get applied when no previous 
             *  match was found
             */
            els = doc.select("mtable");
            for (Element e : els) {
            	e.attr("columnalign", "left");
            }
            els = doc.select("math mi");
            for (Element e : els) {
                replaceIfNoExist(e, miProp);
            }            
            
            
            /** 
             *  OUTPUT -- no pretty print ... leave unaltered.
             */
            doc.outputSettings().prettyPrint(false);
            String html = doc.toString();

            
            /** replace back tokens used to make sure 
             *  html entities are not replaced in JSoup
             */
            html = html.replace("+||+", "&");
            return html;
        } catch (Exception e) {
            throw e;
        }
    }

    /** Replace mathsize, only if it is not already set
     * @param ex 
     * 
     * @param prop
     */
	private void replaceIfNoExist(Element ex, String prop) {
    	if(!ex.hasAttr("mathsize")) {
    		ex.attr("mathsize", prop);
    	}		
	}


	private boolean nextSiblingIs(String tagName, Element e) {
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
	
	
	private Element getNextSibling(Element e) {
		Element p = e.parent();
		for(int i=0;i<p.children().size();i++) {
			Element c = p.children().get(i);
			if(e == c) {
				return p.children().get(i+1);
			}
		}
		return null;
	}
	
	private boolean previousSiblingIs(String tagName, Element e) {
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

}
