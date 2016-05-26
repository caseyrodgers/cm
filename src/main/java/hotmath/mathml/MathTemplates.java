package hotmath.mathml;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;

public class MathTemplates {

	/**
	 * rule: every mi, every mn set: 1em
	 */
	public static final MathTemplate EveryMnMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;

			/**
			 * todo: how to do select with 'or'
			 * 
			 */
			Elements els = doc.select("math mn");
			for (Element e : els) {
				fired = true;
				replaceIfNoExist(e, "1em");
			}

			els = doc.select("math mi");
			for (Element e : els) {
				fired = true;
				replaceIfNoExist(e, "1em");
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "EveryMnMi";
		}
	};

	/**
	 * Add: all mn and mi not in mfrac or msup are 1.1 em
	 */
	public static final MathTemplate EveryMnMiNotInMfrac = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;

			/**
			 * todo: how to do select with 'or'
			 * 
			 */
			Elements els = doc.select("math mn");
			for (Element e : els) {

				if (e.parent() != null && !e.parent().tagName().equals("mfrac")) {
					fired = true;
					replaceIfNoExist(e, "1.1em");
				}

			}

			els = doc.select("math mi");
			for (Element e : els) {
				if (e.parent() != null && !e.parent().tagName().equals("mfrac")) {

					fired = true;
					replaceIfNoExist(e, "1.1em");
				}
			}
			return fired;
		}

		@Override
		public String getRuleName() {
			return "EveryMnMi";
		}
	};

	/**
	 * rule: mfrac mn set: mn 1.2em
	 */
	public static final MathTemplate MfracWithVariableInMn = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mfrac mn");
			for (Element e : els) {
				if (!contentIsNumber(e)) {
					fired = true;
					replaceIfNoExist(e, "1.2em");
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MfracWithVariableInMn";
		}
	};

	public static final MathTemplate MfracWithNumberInMn = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mfrac mn");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(e, "1.2em");
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MfracWithNumberInMn";
		}
	};

	public static final MathTemplate MfracWithMo = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mfrac mo");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(e, "1.3em");
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MfracWithMo";
		}
	};

	/**
	 * rule: mfrac mi set: mi 1.3em
	 */
	public static final MathTemplate MfracWithMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mfrac mi");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(e, "1.3em");
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MfracWithMi";
		}
	};

	public static final MathTemplate MixedNumbers = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			/**
			 * search for mixed numbers .. alter the number before the mfrac
			 */
			Elements els = doc.select("math mrow mn+mfrac");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(getPreviousSibling(e), "1.2em");
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MixedNumbers";
		}
	};

	/**
	 * rule: msqrt mi set: mi 1em
	 */
	public static final MathTemplate MsqrtMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			/**
			 * search for mixed numbers .. alter the number before the mfrac
			 */
			Elements els = doc.select("math msqrt mi");
			for (Element e : els) {
				fired = true;
				replaceIfNoExist(e, "1em");

			}

			return fired;
		}

		public String getRuleName() {
			return "SquareRoot";
		}
	};

	/**
	 * rule: msup mi set: mi .9em
	 */
	public static final MathTemplate MsupMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math msup mi");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(e, "1em");
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MsupMi";
		}
	};

	/**
	 * MSub rule: in msub set: all mn, mi, mo to mathsize = 1.2 em
	 */
	public static final MathTemplate MsubMiMnMo = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math msub");
			for (Element e : els) {
				String tg = e.tagName();
				if (tg.equals("mn") || tg.equals("mi") || tg.equals("mo")) {

					fired = true;
					replaceIfNoExist(e, "1.2em");
				}
			}
			return fired;
		}

		@Override
		public String getRuleName() {
			return "MsubMiMnMo";
		}
	};

	/**
	 * rule: mi set: mi 1.1em
	 */
	public static final MathTemplate AllMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mi");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(e, "1em");
			}
			return fired;
		}

		@Override
		public String getRuleName() {
			return "AllMi";
		}
	};

	/**
	 * rule: mtr mtd mi set: mi 1em
	 */
	public static final MathTemplate MtrMtdMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mtr mtd mi");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(e, "1em");
			}
			return fired;
		}

		@Override
		public String getRuleName() {
			return "MtrMtdMi";
		}
	};

	public static final MathTemplate MrootMrowMsup = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mroot mrow msup");
			for (Element e : els) {
				Elements mns = e.getElementsByTag("mi");
				if (mns.size() == 2) {
					fired = true;
					
					replaceIfNoExist(mns.get(0), "1em");
					replaceIfNoExist(mns.get(1), "1.2em");

					Element par = e.parent();
					if (nextSiblingIs("mi", par)) {
						Element a = getNextSibling(par);
						replaceIfNoExist(a, "1.6em");
					}
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MrootMrowMsup";
		}
	};

	public static final MathTemplate MnMfracMn = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mn+mfrac mn");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(e, "1.2em");
			}

			return fired;

		}

		@Override
		public String getRuleName() {
			return "MnMfracMn";
		}
	};

	public static final MathTemplate MfracMtext = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mfrac mtext");
			for (Element e : els) {

				fired = true;
				replaceIfNoExist(e, "1.2em");
			}

			return fired;
		}

		public String getRuleName() {
			return "MfracMtext";
		}
	};

	/**
	 * rule: inside mfrac, exactly 2 mrows set: if one mrow has mfrac, make sure
	 * both have mtext with 1.3em
	 */
	public static final MathTemplate MFracMtextBalance = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mfrac");
			for (Element e : els) {
				if (matchesPattern("mrow,mrow", e.children())) {

					/**
					 * if one mrow has mtext, then they all must
					 * 
					 */
					if (hasMtext(e.child(0)) || hasMtext(e.child(1))) {

						balanceMtext(e.child(0), e.child(1));
					}
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MFracMtextBalance";
		}
	};

	/**
	 * rule: inside msup last mn or mi with previous sibling mrow set:
	 * mathsiz=1.1em
	 */
	public static final MathTemplate MsupLastMnWithPrevSiblingMrow = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math msup");
			for (Element e : els) {

				// if last child is a mn or mn
				Elements kids = e.children();
				Element lastNode = kids.get(kids.size() - 1);
				if (lastNode.tagName().equals("mn") || lastNode.tagName().equals("mi")) {
					Element ps = getPreviousSibling(lastNode);
					if (ps != null && ps.tagName().equals("mrow")) {

						if (lastNode.tagName().equals("mn")) {
							fired = true;
							replaceIfNoExist(lastNode, "1.1em");
						} else {
							// must be mi
							fired = true;
							replaceIfNoExist(lastNode, "1.2em");
						}

					}
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MsupLastMnWithPrevSiblingMrow";
		}
	};

	/**
	 * rule: math is followed by msup and followed by 2 mi, set: : first mi to
	 * 1.1 em, and set second mi to 1.3 em
	 * 
	 */
	public static final MathTemplate MsupWithExactlyTwoMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math msup");
			for (Element e : els) {

				if (matchesPattern("mi,mi", e.children())) {

					fired = true;
					replaceIfNoExist(e.child(0), "1.1em");
					fired = true;
					replaceIfNoExist(e.child(1), "1.3em");
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MsupWithExactlyTwoMi";
		}
	};

	/**
	 * rule: msup with exactly two mn set: first mi 1em, second mi 1.1em
	 */
	public static final MathTemplate MsupWithExactlyTwoMn = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math msup");
			for (Element e : els) {

				if (matchesPattern("mn,mn", e.children())) {
					fired = true;
					replaceIfNoExist(e.child(0), "1em");
					fired = true;
					replaceIfNoExist(e.child(1), "1.1em");
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MsupWithExactlyTwoMn";
		}
	};

	/**
	 * rule: msup with two elements mi, mn in that order set: set both to 1.1em
	 */
	public static final MathTemplate MsupWithExactlyMiMn = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math msup");
			for (Element e : els) {

				if (matchesPattern("mi,mn", e.children())) {
					fired = true;
					replaceIfNoExist(e.child(0), "1em");
					replaceIfNoExist(e.child(1), "1em");
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MsupWithExactlyMiMn";
		}
	};

	/**
	 * 
	 * rule: msup with excactly two elements mn, mi in that order set: mn .9em,
	 * mi 1.3em
	 */
	public static final MathTemplate MsupWithExactlyMnMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math msup");
			for (Element e : els) {

				if (matchesPattern("mn,mi", e.children())) {
					fired = true;
					replaceIfNoExist(e.child(0), ".9em");
					fired = true;
					replaceIfNoExist(e.child(1), "1.3em");
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MsupWithExactlyMnMi";
		}
	};
	
	
	

	/**
	 *  	

	MrootMn
	rule: mroot with last direct decendant mn
	set: that mn to 1.3em

	 */

	public static final MathTemplate MrootMn = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mroot");
			for (Element e : els) {

				if (hasAtLeast(e, 2, "mn")) {
					
					Elements mis = e.select("mn");
					for(Element k2: mis) {
						if(k2.parent().tagName().equals("mroot") && getNextSibling(k2) == null) {
							fired = true;
							replaceIfNoExist(k2, "1.3em");
						}
					}
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MrootMn";
		}
	};
	
	
	/** 
	MrootMi
	rule: mroot with last direct descendant mi
	set: that mi to 1.4em
    */
	public static final MathTemplate MrootMi = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mroot");
			for (Element e : els) {
				Elements mis = e.select("mi");
				for(Element k2: mis) {
					if(k2.parent().tagName().equals("mroot") && getNextSibling(k2) == null) {
						fired = true;
						replaceIfNoExist(k2, "1.4em");
					}
				}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MrootMi";
		}
	};

	
	/** 
	MrootOther 
	rule: mroot with mn or mi
	set: mn to 1em, mi 1.2em
	*/
	
	public static final MathTemplate MrootOther = new MathTemplate_Base() {
		@Override
		public boolean processDocument(Document doc) {
			boolean fired = false;
			Elements els = doc.select("math mroot");
			for (Element e : els) {

					Elements mis = e.select("mi");
					for(Element k2: mis) {
						fired = true;
						replaceIfNoExist(k2, "1.2em");
					}					

					
					Elements mns = e.select("mn");
					for(Element k2: mns) {
						fired = true;						
						replaceIfNoExist(k2, "1em");
					}
			}

			return fired;
		}

		@Override
		public String getRuleName() {
			return "MrootOther";
		}
	};

	/**
	 * does this element have an mtext as a direct child
	 * 
	 * @param child
	 * @return
	 */
	protected static boolean hasMtext(Element element) {
		for (Element c : element.children()) {
			if (c.tagName().equals("mtext")) {
				return true;
			}
		}
		return false;
	}

	protected static void balanceMtext(Element e1, Element e2) {

		Element[] elems = new Element[] { e1, e2 };
		for (Element e : elems) {
			boolean foundMtext = false;
		for (Element c : e.children()) {
				if (c.tagName().equals("mtext")) {
					foundMtext = true;
					break;
				}
			}
			if (!foundMtext) {
				Element mtext = new Element(Tag.valueOf("mtext"), "");
				mtext.html("&nbsp;");
				e.appendChild(mtext);
			}
		}
	}

}
