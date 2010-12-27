package hotmath.gwt.solution_editor.client;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextArea;

public class TinyMCE extends Composite {
    
    static {
        initTinyMce();
    }

    private TextArea ta;
    private String id;

    public TinyMCE(int width, int height) {
        super();

        LayoutContainer panel = new LayoutContainer();
        panel.setWidth("100%");

        id = HTMLPanel.createUniqueId();
        ta = new TextArea();
        ta.setCharacterWidth(width);
        ta.setVisibleLines(height);
        DOM.setElementAttribute(ta.getElement(), "id", id);
        DOM.setStyleAttribute(ta.getElement(), "width", "100%");
        panel.add(ta);

        initWidget(panel);
    }

    /**
     * getID() -
     *
     * @return the MCE element's ID
     */
    public String getID() {
        return id;
    }

    
    /**
     * setText() -
     *
     * NOTE:
     * @param text
     */
    public void setText(String text) {
        ta.setText(text);
    }

    public String getText() {
        getTextData();
        return ta.getText();
    }

    /**
     * @see com.google.gwt.user.client.ui.Widget#onLoad()
     */
    protected void onLoad() {
        super.onLoad();

        
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                setWidth("100%");
                setTextAreaToTinyMCE(id);
                focusMCE(id);
            }
        });
    }

    /**
     * focusMCE() -
     *
     * Use this to set the focus to the MCE area
     * @param id - the element's ID
     */
    protected native void focusMCE(String id) /*-{
        $wnd.tinyMCE.execCommand('mceFocus', true, id);
    }-*/;

    /**
     * resetMCE() -
     *
     * Use this if reusing the same MCE element, but losing focus
     */
    protected native void resetMCE() /*-{
        $wnd.tinyMCE.execCommand('mceResetDesignMode', true);
    }-*/;
    
    public native void insertText(String text) /*-{
        $wnd.tinyMCE.execCommand('mceInsertContent',false,text);
    }-*/;

    /**
     * unload() -
     *
     * Unload this MCE editor instance from active memory.
     * I use this in the onHide function of the containing widget. This helps
     * to avoid problems, especially when using tabs.
     */
    public void unload() {
        unloadMCE(id);
    }

    /**
     * unloadMCE() -
     *
     * @param id - The element's ID
     * JSNI method to implement unloading the MCE editor instance from memory
     */
    protected native void unloadMCE(String id) /*-{
        $wnd.tinyMCE.execCommand('mceRemoveControl', false, id);
    }-*/;

    /**
     * updateContent() -
     *
     * Update the internal referenced content. Use this if you programatically change
     * the original text area's content (eg. do a clear)
     * @param id - the ID of the text area that contains the content you wish to copy
     */
    protected native void updateContent(String id) /*-{
        $wnd.tinyMCE.selectedInstance = $wnd.tinyMCE.getInstanceById(id);
        $wnd.tinyMCE.setContent($wnd.document.getElementById(id).value);
    }-*/;

    /**
     * getTextArea() -
     *
     */
    protected native void getTextData() /*-{
        $wnd.tinyMCE.triggerSave();
    }-*/;

    /**
     * encodeURIComponent() -
     *
     * Wrapper for the native URL encoding methods
     * @param text - the text to encode
     * @return the encoded text
     */
    protected native String encodeURIComponent(String text) /*-{
        return encodeURIComponent(text);
    }-*/;

    /**
     * setTextAreaToTinyMCE() -
     *
     * Change a text area to a tiny MCE editing field
     * @param id - the text area's ID
     */
    protected native void setTextAreaToTinyMCE(String id) /*-{
        $wnd.tinyMCE.execCommand('mceAddControl', true, id);
    }-*/;

    /**
     * removeMCE() -
     *
     * Remove a tiny MCE editing field from a text area
     * @param id - the text area's ID
     */
    protected native void removeMCE(String id) /*-{
        $wnd.tinyMCE.execCommand('mceRemoveControl', true, id);
    }-*/;
    
    static protected native void initTinyMce() /*-{
    
        $wnd.gwt_onTinyMCESave = function(x) {
            alert('tinyMCE has saved!: ' + x.getContent());
        }
        
        $wnd.tinyMCE.init({
            // General options
            convert_urls : false,
            mode : "none",
            theme : "advanced",
            //skin : "o2k7",
            plugins : "pagebreak,style,layer,table,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,wordcount,advlist,autosave",
    
            // Theme options
            theme_advanced_buttons1 : "save,|,styleselect,|,fmath_formula,|,charmap,media,|image,|,fullscreen,preview",
            theme_advanced_buttons2 : "",
            theme_advanced_buttons3 : "",
            theme_advanced_buttons4 : "",
            save_enablewhendirty : false,
            save_onsavecallback : "gwt_onTinyMCESave",
            theme_advanced_toolbar_location : "top",
            theme_advanced_toolbar_align : "left",
            theme_advanced_statusbar_location : "bottom",
            theme_advanced_resizing : true,
    
            // Example content CSS (should be your site CSS)
            content_css : "css/content.css",
    
            // Drop lists for link/image/media/template dialogs
            template_external_list_url : "lists/template_list.js",
            external_link_list_url : "lists/link_list.js",
            external_image_list_url : "lists/image_list.js",
            media_external_list_url : "lists/media_list.js",
    
            // Style formats
            style_formats : [
                {title : 'Bold text', inline : 'b'},
                {title : 'Red text', inline : 'span', styles : {color : '#ff0000'}},
                {title : 'Red header', block : 'h1', styles : {color : '#ff0000'}},
                {title : 'Example 1', inline : 'span', classes : 'example1'},
                {title : 'Example 2', inline : 'span', classes : 'example2'},
                {title : 'Table styles'},
                {title : 'Table row 1', selector : 'tr', classes : 'tablerow1'}
            ],
    
            // Replace values for the template plugin
            template_replace_values : {
                username : "Some User",
                staffid : "991234"
            }
        });    
        }-*/;    
}