/**
 * @license Copyright (c) 2003-2014, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For the complete reference:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config
	
	
	// Add WIRIS to the plugin list
    config.extraPlugins += (config.extraPlugins.length == 0 ? '' : ',') + 'ckeditor_wiris';


	// The toolbar groups arrangement, optimized for two toolbar rows.
	config.toolbarGroups = [
		{ name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
		{ name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
		{ name: 'links' },
		{ name: 'insert' },
		{ name: 'forms' },
		{ name: 'tools' },
		{ name: 'document',	   groups: [ 'mode', 'document', 'doctools' ] },
		{ name: 'others' },
		'/',
		{ name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
		{ name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align', 'bidi' ] },
		{ name: 'styles' },
		{ name: 'colors' },
		{ name: 'about' }
	];

	if(!CKEDITOR.stylesSet.get('my_styles')) {
		CKEDITOR.stylesSet.add( 'my_styles', [
		                                      // Block-level styles
		                                      { name: 'Blue Title', element: 'h2', styles: { 'color': 'Blue' } },
		                                      { name: 'Red Title' , element: 'h3', styles: { 'color': 'Red' } },
	
		                                      // Inline styles
		                                      { name: 'CSS Style', element: 'span', attributes: { 'class': 'my_style' } },
		                                      { name: 'Marker: Yellow', element: 'span', styles: { 'background-color': 'Yellow' } }
		                                  ]);
	}
	config.stylesSet = 'my_styles';
	
	
	config.toolbarGroups = [
	                		{ name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
	                		{ name: 'insert' },
	                		{ name: 'tools' },
	                		{ name: 'document',	   groups: [ 'mode', 'document', 'doctools' ] },
	                		{ name: 'others' },
	                		{ name: 'styles' },
	                		{ name: 'colors' }
	                	];

    config.removeButtons = 'Source';
    
    
    config.removePlugins = 'elementspath';
    config.resize_enabled = false;
	
//	config.toolbar = [
//	                  {name: 'basicstyles', items: [ 'Bold', 'Italic','Superscript', 'Subscript', 'FontSize' ] },
//	                  { name: 'tools' },
//	                  { name: 'wiris',items : [ 'ckeditor_wiris_formulaEditor', 'ckeditor_wiris_CAS' ]},
//	                  ['insert', 'Styles', 'FontSize', 'TextColor', 'SpecialChar', 'Table', 'Font','Maximize','mathjax'],
//	                  { name: 'colors', items: [ 'TextColor', 'BGColor' ] }
//	              ];


	// ,	{ name: 'wiris',items : [ 'ckeditor_wiris_formulaEditor', 'ckeditor_wiris_CAS' ]}
	
	
	
	// Remove some buttons, provided by the standard plugins, which we don't
	// need to have in the Standard(s) toolbar.
	// config.removeButtons = 'Underline,Subscript,Superscript';

	// Se the most common block elements.
	config.format_tags = 'p;h1;h2;h3;pre';

	// Make dialogs simpler.
	config.removeDialogTabs = 'image:advanced;link:advanced';
	
    
};
