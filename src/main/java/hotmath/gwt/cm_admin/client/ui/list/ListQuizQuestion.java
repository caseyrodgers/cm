package hotmath.gwt.cm_admin.client.ui.list;

import hotmath.gwt.cm_core.client.model.QuizQuestionModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.Formatter;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewCustomAppearance;

public class ListQuizQuestion extends ListView<QuizQuestionModel, QuizQuestionModel>  {
    
	interface QuesionProps extends PropertyAccess<String> {
		ModelKeyProvider<QuizQuestionModel> pid();
		ValueProvider<QuizQuestionModel, QuizQuestionModel> question();
	}
	
	
	final Renderer renderer = GWT.create(Renderer.class);
	
    static final ListViewCustomAppearance<QuizQuestionModel> appearance = new ListViewCustomAppearance<QuizQuestionModel>(".thumb_wrap", ".over", ".select") {
        @Override
        public void renderEnd(SafeHtmlBuilder builder) {
            String markup = new StringBuilder("<div class=\"").append(CommonStyles.get().clear()).append("\"></div>").toString();
            builder.appendHtmlConstant(markup);
        }

        @Override
        public void renderItem(SafeHtmlBuilder builder, SafeHtml content) {
            String style = "test";
            builder.appendHtmlConstant("<div class='" + style + "' style='border: 1px solid white'>");
            String str = content.asString();
            builder.append(SafeHtmlUtils.fromString(str));
            builder.appendHtmlConstant("</div>");
        }
    };


    static ModelKeyProvider<QuizQuestionModel> kp = new ModelKeyProvider<QuizQuestionModel>() {
        @Override
        public String getKey(QuizQuestionModel item) {
            return item.getPid();
        }
    };

    
	static QuesionProps props = GWT.create(QuesionProps.class);
	public ListQuizQuestion() {
    	super(new ListStore<QuizQuestionModel>(kp), 
                new IdentityValueProvider<QuizQuestionModel>() {

            @Override
            public void setValue(QuizQuestionModel object, QuizQuestionModel value) {

            }
        }, appearance);
    	
    	setCell(new SimpleSafeHtmlCell<QuizQuestionModel>(new AbstractSafeHtmlRenderer<QuizQuestionModel>() {
            @Override
            public SafeHtml render(QuizQuestionModel question) {
                return renderer.renderItem(question);
            }
        }));
	}
	
    @FormatterFactories(@FormatterFactory(factory = ShortenFactory.class, name = "shorten"))
    interface Renderer extends XTemplates {
        @XTemplate(source = "ListQuizQuestion.html")
        public SafeHtml renderItem(QuizQuestionModel model);
    }
    
    
    static class Shorten implements Formatter<String> {

        private int length;

        public Shorten(int length) {
            this.length = length;
        }

        @Override
        public String format(String data) {
            return Format.ellipse(data, length);
        }
    }

    static class ShortenFactory {
        public static Shorten getFormat(int length) {
            return new Shorten(length);
        }
    }    
}
