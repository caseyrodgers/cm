function setupPageLocal() {
        YAHOO.hm_standard = new YAHOO.widget.Panel("yui_standard_panel",
                    {modal:true,underlay: "none",
                    fixedcenter:true,
                    zIndex: 999,
                    width: "340px",
                    visible:true,close:false,
                    contraintoviewport:true,
                    draggable:true});
            
            YAHOO.hm_standard.setHeader("The Header");
            YAHOO.hm_standard.setBody("The Body");
            YAHOO.hm_standard.setFooter("The Footer");
            YAHOO.hm_standard.render(document.body);
}
