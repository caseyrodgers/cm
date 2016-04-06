package hotmath.mathml;

import java.util.Enumeration;

import org.apache.commons.lang.math.NumberUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import hotmath.cm.util.CatchupMathProperties;
import hotmath.cm.util.CmMultiLinePropertyReader;
import sb.client.SbTesterFrameGeneric;
import sb.util.SbException;
import sb.util.SbTestImpl;

/**
 * 
 * Process MathML transformations to add customized
 * mathml attributes.
 * 
 * 
 */
public class MathMlTransform {
	
	
	private CmMultiLinePropertyReader _mprops;
	MathTemplate[] _mathTemplates = {
			MathTemplates.MnMfracMn,
			MathTemplates.MfracWithVariableInMn,
			MathTemplates.MfracWithNumberInMn,
			MathTemplates.MrootMrowMsup,
			MathTemplates.MfracWithMo,
			MathTemplates.MfracWithMi,
			MathTemplates.MixedNumbers,
			MathTemplates.SquareRoot,
			MathTemplates.MsupMi,
			MathTemplates.MtrMtdMi
	};


	/** we do not want to process html entities (ie, turn them into extended chars)
	 * 
	 *  The only way I can see to do that is hide them ..
	 *  What we want to a JSoup.EscapeMode.noprocess .. but, that is not an option
	 *  
	 *  hack around .. by replacing all '@' charts before transformation, then replace back...
	 *  this is important because the processed text is embedded as json and delivered to
	 *  client ... 
	 *  
	 *  
	 */
	public String processMathMlTransformations(String solutionHtml) throws Exception {
        try {
        	solutionHtml = solutionHtml.replace("&", "+||+");
            Document doc = Jsoup.parse(solutionHtml,"", Parser.xmlParser());

            
            for(MathTemplate t: _mathTemplates) {
            	t.processDocument(doc);
            }
            
            
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
	
	
	
	
    private void processTemplate(String template, Document doc) {
    	Document docTemplate = Jsoup.parse(template,"", Parser.xmlParser());
    	
    	
    	
    	
    	
    	
    	
	}




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
    public String processMathMlTransformations2(String solutionHtml) throws Exception {
    	
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
            String propSqrtMi = p.getProperty("mathml.sqrt.mi", "1.1em");
            String propMsupMi = p.getProperty("mathml.msup.mi", "1em");
            String propMrootMi1 = p.getProperty("mathml.mroot.mrow.msup.mi.1", "1em");
            String propMrootMi2 = p.getProperty("mathml.mroot.mrow.msup.mi.2", "1.2em");
            String propMrootMiRow = p.getProperty("mathml.mroot.mrow.mi", "1.6em");
            String propMtrMtdMi = p.getProperty("mathml.mtr.mtd.mi", "1em");
            String propMsupFirst = p.getProperty("mathml.msup", null);
            String propMsupSecond = p.getProperty("mathml.msup", "1.2em");
            String propMfracMnVariable = p.getProperty("mathml.mfrac.mn.variable", "1.4em");
            String mrowMiProp = p.getProperty("mathml.mrow.mi", "1.2em");

            
            String propMrowMnFrac = p.getProperty("mathml.mrow.mn.mfrac", "1.2em");
            

            /** Apply broad matches first, then more specific 
             * 
             */

            
            
            
            
            String patternEquation="mn:,mi:1.1,mo:,mn:,mi:1.1,mo:,mn:";
            Elements els = doc.select("math mrow");
            for (Element e : els) {
            	if(MathTemplate_Base.matchesPattern(patternEquation, e.children())) {
            		setMathSizes(patternEquation, e.children());
            	}
            }

            
            /** mfrac with variable in mn
             * 
             */
            els = doc.select("math mfrac mn");
            for (Element e : els) {
            	if(!contentIsNumber(e)) {  
            		replaceIfNoExist(e, propMfracMnVariable);	
            	}
            }
            
            
            /** specific/absolute styles first (no calculations)
             * 
             */
            els = doc.select("math mfrac mn");
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
            els = doc.select("math msqrt mi");
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
            els = doc.select("math msub");
            for (Element e : els) {
                Elements mns = e.getElementsByTag("mn");
                if (mns.size() == 2) {
                	replaceIfNoExist(mns.get(0), propMsupFirst);
                	replaceIfNoExist(mns.get(1), propMsupSecond);
                }
                mns = e.getElementsByTag("mi");
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
            els = doc.select("math mrow mi");
            for (Element e : els) {
                replaceIfNoExist(e, mrowMiProp);
            }
//            els = doc.select("math mrow mn");
//            for (Element e : els) {
//                replaceIfNoExist(e, mrowMiProp);
//            }            
//            els = doc.select("math mrow mo");
//            for (Element e : els) {
//                replaceIfNoExist(e, mrowMiProp);
//            }            


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

    private void setMathSizes(String pattern, Elements children) {
    	String ptn[] = pattern.split(",");
    	if(ptn.length != children.size()) {
    		return;
    	}
    	for(int i=0;i<children.size();i++) {
    		String p[] = ptn[i].split(":");
    		String patSize=null;
    		if(p.length > 1) {
    			patSize = ptn[i].split(":")[1] + "em";
    		}
    		replaceIfNoExist(children.get(i),patSize);
    	}
	}




	private boolean contentIsNumber(Element e) {
    	String val = e.text();
    	return NumberUtils.isNumber(val);
	}


	/** Replace mathsize, only if it is not already set
     * @param ex 
     * 
     * @param prop
     */
	private void replaceIfNoExist(Element ex, String prop) {
    	if(!ex.hasAttr("mathsize")) {
    		if(prop == null) {
    			ex.removeAttr("mathsize");
    		}
    		else {
    			ex.attr("mathsize", prop);
    		}
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

	
	

	public void doTestSetup() throws Exception {
		
		if(false) {
			doTest("test_fractions");
			return;
		}
		_mprops = new CmMultiLinePropertyReader(CatchupMathProperties.getInstance().getCatchupRuntime() + "/test_mathml.mprop");
		Enumeration<Object> iter = _mprops.keys();

		try {
			while (iter.hasMoreElements()) {
				Object o = iter.nextElement();
				String k = o.toString();
				if (k.contains("_check")) {
					continue;
				}
				try {
					doTest(o.toString());
				}
				catch(Throwable e) {
					e.printStackTrace();
				}
			}
		}
		catch(Exception e) {
			System.out.println("Error testing mathml transform: " + e);
		}
		
		System.out.println("MathML Transform tests complete!");
	}

	private void doTest(String key) throws Exception {

		System.out.println("Testing key: " + key);

		_mprops = new CmMultiLinePropertyReader(
				CatchupMathProperties.getInstance().getCatchupRuntime() + "/test_mathml.mprop");

		String mathMl = _mprops.getProperty(key);
		String result = new MathMlTransform().processMathMlTransformations(mathMl);
		String check = _mprops.getProperty(key + "_check");
		try {
			if(!check.trim().equals(result.trim())) {
				System.out.println("Does not match!");
				System.out.println(result);
			}
		}
		catch(Throwable e) {
			e.printStackTrace();
			System.out.println(result);
		}
	}
	
	static public void main(String as[]) {
		
		try {
			
			new SbTesterFrameGeneric(new SbTestImpl() {
				@Override
				public void doTest(Object arg0, String arg1) throws SbException {
					try {
						new MathMlTransform().doTestSetup();
					}
					catch(Exception e) {
						throw new SbException(e);
					}
				}
			});
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
}
