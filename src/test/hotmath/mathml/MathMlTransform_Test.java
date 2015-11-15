package hotmath.mathml;

import junit.framework.TestCase;

public class MathMlTransform_Test extends TestCase {
	
	public MathMlTransform_Test(String name) {
		super(name);
	}
	
	
	String mixedFraction = 
			"<math><mn>3</mn><mfrac> <mn mathsize='1.1em'>2</mn><mn mathsize='1em'>5</mn></mfrac></math>";
	String mixedFractionExpected = 
			"<math><mn mathsize='1em'>3</mn><mfrac> <mn mathsize='1.3em'>2</mn><mn mathsize='1.3em'>5</mn></mfrac></math>";
	public void testIt() throws Exception {
		String result = new MathMlTransform().processMathMlTransformations(mixedFraction);
		assertTrue(result.equals(mixedFractionExpected));
	}

}
