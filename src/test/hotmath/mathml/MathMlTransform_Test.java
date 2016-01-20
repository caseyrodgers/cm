package hotmath.mathml;

import junit.framework.TestCase;

public class MathMlTransform_Test extends TestCase {
	
	public MathMlTransform_Test(String name) {
		super(name);
	}
	
	
	public void test1() throws Exception {
	    String test = "<div></div>";
		String result = new MathMlTransform().processMathMlTransformations(test);
		assertTrue(result.equals(test));
	}
	
	
	public void test2() throws Exception {
	    String test = "<math><mfrac><mn mathsize=\"NO\">1</mn></mfrac></math>";
	    String testRes = "<math><mfrac><mn mathsize=\"1.1em\">1</mn></mfrac></math>";
		String result = new MathMlTransform().processMathMlTransformations(test);
		assertTrue(result.equals(testRes));
	}
	
	public void testFracMn() throws Exception {
	    String test = "<math><mfrac><mn mathsize=\"FIRST_MN\">1</mn><mrow><mn mathsize=\"SECOND_MN\"></mn></mrow></mfrac></math>";
	    String testRes = "<math><mfrac><mn mathsize=\"1.1em\">1</mn><mrow><mn mathsize=\"1.1em\"></mn></mrow></mfrac></math>";
		String result = new MathMlTransform().processMathMlTransformations(test);
		assertTrue(result.equals(testRes));
	}
	
	public void testMixNumber() throws Exception {
	    String test = "<math><mn>100</mn><mfrac><mn mathsize=\"FIRST_MN\">1</mn><mrow><mn mathsize=\"SECOND_MN\">2</mn></mrow></mfrac></math>";
	    String testRes = "<math><mn mathsize=\"1em\">100</mn><mfrac><mn mathsize=\"1.3em\">1</mn><mrow><mn mathsize=\"1.3em\">2</mn></mrow></mfrac></math>";
	    
		String result = new MathMlTransform().processMathMlTransformations(test);
		assertTrue(result.equals(testRes));
	}
	
	public void testFracMrowMn() throws Exception {
	    String test = "<math><mn>100</mn><mfrac><mn mathsize=\"FIRST_MN\">1</mn><mrow><mn mathsize=\"SECOND_MN\"></mn></mrow></mfrac></math>";
	    String testRes = "<math><mn mathsize=\"1em\">100</mn><mfrac><mn mathsize=\"1.3em\">1</mn><mrow><mn mathsize=\"1.3em\"></mn></mrow></mfrac></math>";
	    
		String result = new MathMlTransform().processMathMlTransformations(test);
		assertTrue(result.equals(testRes));
	}
	
	
	
	String mixedFraction = 
			"<math><mn>3</mn><mfrac> <mn mathsize=\"1.1em\">2</mn><mn mathsize=\"1em\">5</mn></mfrac></math>";
	String mixedFractionExpected = 
			"<math><mn mathsize=\"1em\">3</mn><mfrac> <mn mathsize=\"1.3em\">2</mn><mn mathsize=\"1.3em\">5</mn></mfrac></math>";
	public void testIt() throws Exception {
		String result = new MathMlTransform().processMathMlTransformations(mixedFraction);
		assertTrue(result.equals(mixedFractionExpected));
	}

}