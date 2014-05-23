/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For the complete reference:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config
	
	
	// Add WIRIS to the plugin list
    config.extraPlugins += (config.extraPlugins.length == 0 ? '' : ',') + 'ckeditor_wiris,font';
	config.fontSize_sizes = '12/12pt;16/16pt;24/24pt;48/48pt;';


	config.toolbar = 'CM';
	config.toolbar_CM = 
	    [
         ['Bold','Italic','Underline','Superscript', 'Subscript'],
         ['Font','FontSize'],
	     ['Table'],
	     ['ckeditor_wiris_formulaEditor']
	    ];
            config.removePlugins = 'elementspath';
            config.resize_enabled = false;
	

	// Make dialogs simpler.
	config.removeDialogTabs = 'image:advanced;link:advanced';
};

