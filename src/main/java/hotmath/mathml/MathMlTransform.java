package hotmath.mathml;

import java.util.Enumeration;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

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

	static Logger __logger = Logger.getLogger(MathMlTransform.class);
	
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
			   MathTemplates.MtrMtdMi,
			   MathTemplates.MfracMtext,
			   MathTemplates.MFracMtextBalance,
			   MathTemplates.MsupLastMnWithPrevSiblingMrow,
			   MathTemplates.MsupWithExactlyTwoMi,
			   MathTemplates.MsupWithExactlyTwoMn,
			   MathTemplates.MsupWithExactlyMiMn,
			   MathTemplates.MsupWithExactlyMnMi,
			   MathTemplates.MsupMi
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

            
            __logger.debug("processing:\n" + solutionHtml);

            boolean atLeastOnMatch=false;
            for(MathTemplate t: _mathTemplates) {

            	String before = doc.toString();
            	t.processDocument(doc);
            	String after = doc.toString();
            	
            	if(!before.equals(after)) {
            		atLeastOnMatch=true;
            		//__logger.debug("Rule activated: " + t.getRuleName());
            		System.out.println("-> Rule activated: " + t.getRuleName());
            	}
            }
            if(atLeastOnMatch) {
                //__logger.info("processed: " + doc.toString());
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
	

	public void doTestSetup() throws Exception {
		
		if(false) {
			doTest("MsupWithExactlyMiMn");
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
		String result = new MathMlTransform().processMathMlTransformations(mathMl).trim();
		String check = _mprops.getProperty(key + "_check").trim();
		try {
			if(!check.equals(result)) {
				System.out.println("Does not match!");
				System.out.println(">" + result + "<" + result.length());
				System.out.println("Expecting:\n" + ">" + check + "<" + check.length());
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
