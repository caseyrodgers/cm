package hotmath.gwt.cm_tools.client.ui;

import java.util.ArrayList;
import java.util.Stack;

import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.InfoConfig;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

public class InfoPopupBox extends ContentPanel {

	private static Stack<InfoPopupBox> infoStack = new Stack<InfoPopupBox>();
	private static ArrayList<InfoPopupBox> slots = new ArrayList<InfoPopupBox>();

	/**
	 * Displays a message using the specified config.
	 * 
	 * @param config
	 *            the info config
	 */
	public static void display(InfoConfig config) {
		pop().show(config);
	}

	/**
	 * Displays a message with the given title and text.
	 * 
	 * @param title
	 *            the title
	 * @param text
	 *            the text
	 */
	public static void display(String title, String text) {
		InfoConfig config = new InfoConfig(title, text);
		config.display = 5000; // show message for 5 secs
		display(config);
	}

	/**
	 * Displays a message with the given title and text. The passed parameters
	 * will be applied to both the title and text before being displayed.
	 * 
	 * @param title
	 *            the info title
	 * @param text
	 *            the info text
	 * @param params
	 *            the paramters to be applied to the title and text
	 */
	public static void display(String title, String text, Params params) {
		InfoConfig config = new InfoConfig(title, text, params);
		display(config);
	}

	/**
	 * Displays a message with the given title and text. All {0},{1}... values
	 * in text will be replaced with values.
	 * 
	 * @param title
	 *            the message title
	 * @param text
	 *            the message
	 * @param values
	 *            the values to be substituted
	 */
	public static void display(String title, String text, String... values) {
		display(new InfoConfig(title, text, new Params((Object[]) values)));
	}

	private static int firstAvail() {
		int size = slots.size();
		for (int i = 0; i < size; i++) {
			if (slots.get(i) == null) {
				return i;
			}
		}
		return size;
	}

	private static InfoPopupBox pop() {
		InfoPopupBox info = infoStack.size() > 0 ? (InfoPopupBox) infoStack
				.pop() : null;
		if (info == null) {
			info = new InfoPopupBox();
		}
		return info;
	}

	private static void push(InfoPopupBox info) {
		infoStack.push(info);
	}

	protected InfoConfig config;
	protected int level;

	/**
	 * Creates a new info instance.
	 */
	public InfoPopupBox() {
		baseStyle = "x-info";
		frame = true;
		setShadow(true);
		setLayoutOnChange(true);
		
		setStyleName("cm-info-popup-box");
	}

	public void hide() {
		super.hide();
		afterHide();
	}

	/**
	 * Displays the info.
	 * 
	 * @param config
	 *            the info config
	 */
	public void show(InfoConfig config) {
		this.config = config;
		onShowInfo();
	}

	protected void onShowInfo() {
		RootPanel.get().add(this);
		el().makePositionable(true);

		setTitle();
		setText();

		level = firstAvail();
		slots.add(level, this);

		Point p = position();
		el().setLeftTop(p.x, p.y);
		setSize(config.width, config.height);

		afterShow();
	}

	private Point position() {
		/** Center popup box */
		 Size s = XDOM.getViewportSize();
		 int left = (s.width - config.width) / 2;
		 int top = (s.height - config.height) / 2;
		 return new Point(left, top);
		
		
		/** bottom right */
//		Size s = XDOM.getViewportSize();
//		int left = 3; // s.width - config.width - 10 + XDOM.getBodyScrollLeft();
//		int top = s.height - config.height - 10
//				- (level * (config.height + 10)) + XDOM.getBodyScrollTop();
//		return new Point(left, top);
	}

	private void afterHide() {
		RootPanel.get().remove(this);
		slots.set(level, null);
		push(this);
	}

	private void afterShow() {
		Timer t = new Timer() {
			public void run() {
				afterHide();
			}
		};
		t.schedule(config.display);
	}

	private void setText() {
		if (config.text != null) {
			if (config.params != null) {
				config.text = Format.substitute(config.text, config.params);
			}
			removeAll();
			addText(config.text);
		}
	}

	private void setTitle() {
		if (config.title != null) {
			head.setVisible(true);
			if (config.params != null) {
				config.title = Format.substitute(config.title, config.params);
			}
			setHeading(config.title);
		} else {
			head.setVisible(false);
		}
	}

}
