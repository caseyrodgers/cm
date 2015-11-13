package hotmath.mathml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
        try {
            Document doc = Jsoup.parse(solutionHtml);

            CatchupMathProperties p = CatchupMathProperties.getInstance();

            
            /** add mathsize to each mfrac number
             *
             *  add to mn
             */
            
            String normalFraction = p.getProperty("mathml.mfrac.mn", "1.2em");
            String propMixWhole = p.getProperty("mathml.mixed.mn.mfrac", "1em");
            String propMixFract = p.getProperty("mathml.mixed.mn.mfrac.mn", "1.3em"); 
        
            
            Elements els = doc.select("math mfrac mn");
            for (Element e : els) {
            	
            	if(previousSiblingIs("mn", e.parent())) {
            		e.attr("mathsize", propMixFract);
            	}
            	else {
	        		// is normal fraction
	        		e.attr("mathsize", normalFraction);
            	}
            }

            els = doc.select("math mi");
            String prop = p.getProperty("mathml.mi", "1em");
            for (Element e : els) {
                e.attr("mathsize", prop);
            }

            // look for mn precending a mfrac
            // for a mixed fraction
            els = doc.select("math mn");
            for(Element e: els) {
            	if(nextSiblingIs("mfrac", e)) {
            		e.attr("mathsize", propMixWhole);
            	}
            }

            /** second mn in a msup 
             * 
             */
            els = doc.select("math msup");
            prop = p.getProperty("mathml.msup", "1.1em");
            for (Element e : els) {
                Elements mns = e.getElementsByTag("mn");
                if (mns.size() == 2) {
                    mns.get(1).attr("mathsize", prop);
                }
            }
            doc.outputSettings().prettyPrint(false);
            String html = doc.toString();
            return html;
        } catch (Exception e) {
            throw e;
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
