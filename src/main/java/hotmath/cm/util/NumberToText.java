package hotmath.cm.util;

public class NumberToText {

	private static String[] oneToNineteen = {
		"zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten", "eleven",
		"twelve", "thirteen", "fourteen", "fifteen", "sixteen", "seventeen", "eighteen", "nineteen"
	};
	
	private static String[] twentyToNinety = {
		"twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty", "ninety"
	};

	public static String getText(int number) {
		if (number < 0 ||number > 99) {
			return "undefined";
		}
		else if (number < 20) {
			return oneToNineteen[number];
		}
		else {
	        int tens = number/10;
	        int ones = number - (tens*10);
	        String text = twentyToNinety[tens-2];
	        if (ones < 1) {
	        	return text;
	        } else {
	        	return text + "-" + oneToNineteen[ones];
	        }
		}
	}
}
