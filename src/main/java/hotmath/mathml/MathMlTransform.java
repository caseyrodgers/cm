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
            String propSqrtMi = p.getProperty("mathml.sqrt.mi", "1em");
            String propMsupMi = p.getProperty("mathml.msup.mi", "1em");
            String propMrootMi1 = p.getProperty("mathml.mroot.mrow.msup.mi.1", "1em");
            String propMrootMi2 = p.getProperty("mathml.mroot.mrow.msup.mi.2", "1.2em");
            String propMrootMiRow = p.getProperty("mathml.mroot.mrow.mi", "1.6em");
            String propMtrMtdMi = p.getProperty("mathml.mtr.mtd.mi", "1em");
            
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
            
            /** Square root
             * 
             */
            els = doc.select("math msqtr mi");
            for (Element e : els) {
            	e.attr("mathsize", propSqrtMi);
            }
            
            els = doc.select("math msup mi");
            for (Element e : els) {
            	e.attr("mathsize", propMsupMi);
            }
            
            
            els = doc.select("math mroot mrow msup");
            for (Element e : els) {
            	Elements mns = e.getElementsByTag("mi");
            	if(mns.size() == 2) {
            		mns.get(0).attr("mathsize", propMrootMi1);
            		mns.get(1).attr("mathsize", propMrootMi2);
            		
            		Element par = e.parent();
            		if(nextSiblingIs("mi", par)) {
            			Element a = getNextSibling(par);
            			
            			a.attr("mathsize", propMrootMiRow);
            		}
            	}
            }
            
            
            /** 
             * 
             *  <mtable columnalign="left">
				    <mtr>
				      <mtd>
				        <mi mathsize="1em">x</mi>
				        <mo>&#x2265;</mo>
				        <mn>5</mn>
				      </mtd>
				    </mtr>
				    <mtr>
				      <mtd>
				        <mi mathsize="1em">y</mi>
				        <mo>&#x2264;</mo>
				        <mn>2</mn>
				      </mtd>
				    </mtr>
				    <mtr>
				      <mtd>
				        <mi mathsize="1em">z</mi>
				        <mo>&#x2260;</mo>
				        <mn>1</mn>
				      </mtd>
				    </mtr>
				  </mtable>
             * 
             * 
             */
            
            els = doc.select("math mtr mtd mi");
            for (Element e : els) {
            	e.attr("mathsize", propMtrMtdMi);
            }
            
            els = doc.select("mtable");
            for (Element e : els) {
            	e.attr("columnalign", "left");
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
