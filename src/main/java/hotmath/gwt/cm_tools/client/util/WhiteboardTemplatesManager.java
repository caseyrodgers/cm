package hotmath.gwt.cm_tools.client.util;

import hotmath.gwt.cm_rpc.client.CallbackOnComplete;
import hotmath.gwt.cm_rpc.client.model.WhiteboardTemplate;
import hotmath.gwt.cm_rpc.client.model.WhiteboardTemplatesResponse;
import hotmath.gwt.cm_rpc.client.rpc.GetWhiteboardTemplatesAction;
import hotmath.gwt.cm_rpc.client.rpc.ManageWhiteboardTemplatesAction;
import hotmath.gwt.cm_rpc.client.rpc.ManageWhiteboardTemplatesAction.ManageType;
import hotmath.gwt.cm_rpc.client.rpc.SaveWhiteboardDataAction.CommandType;
import hotmath.gwt.cm_rpc_core.client.rpc.Action;
import hotmath.gwt.cm_rpc_core.client.rpc.Response;
import hotmath.gwt.cm_rpc_core.client.rpc.RpcData;
import hotmath.gwt.cm_tools.client.ui.GWindow;
import hotmath.gwt.cm_tools.client.util.CmMessageBox.ConfirmCallback;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2;
import hotmath.gwt.cm_tutor.client.view.ShowWorkPanel2.ShowWorkPanel2Callback;
import hotmath.gwt.shared.client.CmShared;
import hotmath.gwt.shared.client.model.UserInfoBase;
import hotmath.gwt.shared.client.rpc.RetryAction;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractSafeHtmlRenderer;
import com.google.gwt.user.client.Cookies;
import com.sencha.gxt.cell.core.client.SimpleSafeHtmlCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.XTemplates;
import com.sencha.gxt.core.client.XTemplates.Formatter;
import com.sencha.gxt.core.client.XTemplates.FormatterFactories;
import com.sencha.gxt.core.client.XTemplates.FormatterFactory;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.core.client.util.Format;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.ListView;
import com.sencha.gxt.widget.core.client.ListViewCustomAppearance;
import com.sencha.gxt.widget.core.client.box.PromptMessageBox;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.HideEvent;
import com.sencha.gxt.widget.core.client.event.HideEvent.HideHandler;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent;
import com.sencha.gxt.widget.core.client.selection.SelectionChangedEvent.SelectionChangedHandler;

public class WhiteboardTemplatesManager extends GWindow {

    ListView<WhiteboardTemplate, WhiteboardTemplate> _listView;
    private ShowWorkPanel2 showWorkPanel2;

    public WhiteboardTemplatesManager(final ShowWorkPanel2 showWorkPanel2) {
        super(true);

        this.showWorkPanel2 = showWorkPanel2;
        
        setHeadingText("Template Manager");

        
        addTool(new TextButton("Use Template", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                useTemplate();
            }
        }));
        
        
        addTool(new TextButton("Create New", new SelectHandler() {
            
            @Override
            public void onSelect(SelectEvent event) {
                createNewTemplate(showWorkPanel2);
            }
        }));
        
        addTool(new TextButton("Delete", new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                deleteSelected();
            }
        }));
        
        _listView = createListView();
        setWidget(_listView);
        
        loadWhiteboardTemplates();
        
        setVisible(true);
    }
    
    protected void createNewTemplate(final ShowWorkPanel2 showWorkPanel2) {
        String tmplName = Cookies.getCookie("wb_template");
        final PromptMessageBox mb = new PromptMessageBox("Save As Template", "Template Name");
        mb.getTextField().setValue(tmplName != null?tmplName:"My Template");
        mb.addHideHandler(new HideHandler() {
          public void onHide(HideEvent event) {
            if (mb.getHideButton() == mb.getButtonById(PredefinedButton.OK.name())) {
                String name =mb.getTextField().getCurrentValue();
                Cookies.setCookie("wb_template", name);
                showWorkPanel2.saveAsTemplate(UserInfoBase.getInstance().getUid(), name, new CallbackOnComplete() {
                    @Override
                    public void isComplete() {
                        loadWhiteboardTemplates();
                    }
                });
            } else if (mb.getHideButton() == mb.getButtonById(PredefinedButton.CANCEL.name())) {
            }
          }
        });
        mb.setWidth(300);
        mb.show();                
    }
    
    private void useTemplate() {
        final WhiteboardTemplate item = _listView.getSelectionModel().getSelectedItem();
        if(item == null) {
            CmMessageBox.showAlert("Select a template first");
            return;
        }
        showWorkPanel2.setWhiteboardTemplate(item.getName(), item.getPath());
        
        hide();
    }

    protected void deleteSelected() {
        final WhiteboardTemplate item = _listView.getSelectionModel().getSelectedItem();
        if(item == null) {
            CmMessageBox.showAlert("Select a template first");
            return;
        }

        if(item.getPath().startsWith("/gwt-resources")) {
            CmMessageBox.showAlert("This is a system template and cannot be deleted.");
            return;
        }
        
        CmMessageBox.confirm("Delete Template",  "Are you sure your want to delete this template?", new ConfirmCallback() {
            @Override
            public void confirmed(boolean yesNo) {
                if(!yesNo) {
                    return;
                }
                new RetryAction<RpcData>() {
                    @Override
                    public void attempt() {
                        ManageWhiteboardTemplatesAction action = new ManageWhiteboardTemplatesAction(UserInfoBase.getInstance().getUid(),item.getName(), ManageType.DELETE);
                        setAction(action);
                        CmShared.getCmService().execute(action, this);
                    }

                    @Override
                    public void oncapture(RpcData response) {
                        loadWhiteboardTemplates();
                    }
                }.register();
            }
        });
    }

    protected void loadWhiteboardTemplates() {
        new RetryAction<WhiteboardTemplatesResponse>() {
            @Override
            public void attempt() {
                GetWhiteboardTemplatesAction action = new GetWhiteboardTemplatesAction(UserInfoBase.getInstance().getUid());
                setAction(action);
                CmShared.getCmService().execute(action, this);
            }

            @Override
            public void oncapture(WhiteboardTemplatesResponse templates) {
                setTemplates(templates);
            }

        }.register();
    }

    protected void setTemplates(WhiteboardTemplatesResponse templates) {
        _listView.getStore().clear();
        
        /** add standard templates
         * 
         */
        templates.getTemplates().add(0, new WhiteboardTemplate("Number Line", "/gwt-resources/images/whiteboard/nL.png"));
        templates.getTemplates().add(0, new WhiteboardTemplate("Graph 2D", "/gwt-resources/images/whiteboard/gr2D.png"));
        templates.getTemplates().add(0, new WhiteboardTemplate("Equation 1", "/gwt-resources/images/whiteboard/templates/math1.png"));
        templates.getTemplates().add(0, new WhiteboardTemplate("Equation 2", "/gwt-resources/images/whiteboard/templates/math2.png"));
        
        _listView.getStore().addAll(templates.getTemplates());
    }

    @FormatterFactories(@FormatterFactory(factory = ShortenFactory.class, name = "shorten"))
    interface Renderer extends XTemplates {
        @XTemplate(source = "WhiteboardTemplatesManager.html")
        public SafeHtml renderItem(WhiteboardTemplate whiteboardTemplate, Style style);
    }

    interface Resources extends ClientBundle {
        @Source("WhiteboardTemplatesManager.css")
        Style css();
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

    interface Style extends CssResource {
        String over();

        String select();

        String thumb();

        String thumbWrap();
    }

    public ListView<WhiteboardTemplate, WhiteboardTemplate> createListView() {

        ModelKeyProvider<WhiteboardTemplate> kp = new ModelKeyProvider<WhiteboardTemplate>() {
            @Override
            public String getKey(WhiteboardTemplate item) {
                return item.getName();
            }
        };

        ListStore<WhiteboardTemplate> store = new ListStore<WhiteboardTemplate>(kp);
        
        final Resources resources = GWT.create(Resources.class);
        resources.css().ensureInjected();
        final Style style = resources.css();

        final Renderer r = GWT.create(Renderer.class);

        ListViewCustomAppearance<WhiteboardTemplate> appearance = new ListViewCustomAppearance<WhiteboardTemplate>("." + style.thumbWrap(),
                style.over(), style.select()) {

            @Override
            public void renderEnd(SafeHtmlBuilder builder) {
                String markup = new StringBuilder("<div class=\"").append(CommonStyles.get().clear()).append("\"></div>").toString();
                builder.appendHtmlConstant(markup);
            }

            @Override
            public void renderItem(SafeHtmlBuilder builder, SafeHtml content) {
                builder.appendHtmlConstant("<div class='" + style.thumbWrap() + "' style='border: 1px solid white'>");
                builder.append(content);
                builder.appendHtmlConstant("</div>");
            }

        };

        ListView<WhiteboardTemplate, WhiteboardTemplate> view = new ListView<WhiteboardTemplate, WhiteboardTemplate>(store,
                new IdentityValueProvider<WhiteboardTemplate>() {

                    @Override
                    public void setValue(WhiteboardTemplate object, WhiteboardTemplate value) {

                    }
                }, appearance);
        view.setCell(new SimpleSafeHtmlCell<WhiteboardTemplate>(new AbstractSafeHtmlRenderer<WhiteboardTemplate>() {

            @Override
            public SafeHtml render(WhiteboardTemplate object) {
                return r.renderItem(object, style);
            }
        }));
        view.getSelectionModel().addSelectionChangedHandler(new SelectionChangedHandler<WhiteboardTemplate>() {
            @Override
            public void onSelectionChanged(SelectionChangedEvent<WhiteboardTemplate> event) {
                //panel.setHeadingText("Simple ListView (" + event.getSelection().size() + " items selected)");
            }
        });
        
        return view;
    }

    public static void doTest() {
        new WhiteboardTemplatesManager(new ShowWorkPanel2(new ShowWorkPanel2Callback() {
            
            @Override
            public void windowResized() {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void showWorkIsReady(ShowWorkPanel2 showWork) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void saveWhiteboardAsTemplate(ShowWorkPanel2 showWorkPanel2) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void manageTemplates(ShowWorkPanel2 showWorkPanel2) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public Action<? extends Response> createWhiteboardSaveAction(String pid, CommandType commandType, String data) {
                // TODO Auto-generated method stub
                return null;
            }
        }));
    }
}