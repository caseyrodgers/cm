/**
 * @license Copyright (c) 2003-2013, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.html or http://ckeditor.com/license
 */

CKEDITOR.editorConfig = function( config ) {
	// Define changes to default configuration here.
	// For the complete reference:
	// http://docs.ckeditor.com/#!/api/CKEDITOR.config
	
	
	// Add WIRIS to the plugin list
        config.extraPlugins += (config.extraPlugins.length == 0 ? '' : ',') + 'ckeditor_wiris,font,specialchar';
 	config.fontSize_sizes = '12/12pt;16/16pt;24/24pt;48/48pt;';



	config.toolbar = 'CM';
	config.toolbar_CM = 
	    [
         ['Bold','Italic','Underline','Superscript', 'Subscript'],
         ['Font','FontSize'],
	 ['Table'], ['SpecialChar'],['ckeditor_wiris_formulaEditor']
	    ];
        config.removePlugins = 'elementspath';
        config.resize_enabled = false;
	

	// Make dialogs simpler.
	config.removeDialogTabs = 'image:advanced;link:advanced';



        config.specialChars = [
["&radic;","Square Root"],
["&fnof;","fnof"],
["&Alpha;","Alpha"],
["&Beta;","Beta"],
["&Gamma;","Gamma"],
["&Delta;","Delta"],
["&Epsilon;","Epsilon"],
["&Zeta;","Zeta"],
["&Eta;","Eta"],
["&Theta;","Theta"],
["&Iota;","Iota"],
["&Kappa;","Kappa"],
["&Lambda;","Lambda"],
["&Mu;","Mu"],
["&Nu;","Nu"],
["&Xi;","Xi"],
["&Omicron;","Omicron"],
["&Pi;","Pi"],
["&Rho;","Rho"],
["&Sigma;","Sigma"],
["&Tau;","Tau"],
["&Upsilon;","Upsilon"],
["&Phi;","Phi"],
["&Chi;","Chi"],
["&Psi;","Psi"],
["&Omega;","Omega"],
["&alpha;","alpha"],
["&beta;","beta"],
["&gamma;","gamma"],
["&delta;","delta"],
["&epsilon;","epsilon"],
["&zeta;","zeta"],
["&eta;","eta"],
["&theta;","theta"],
["&iota;","iota"],
["&kappa;","kappa"],
["&lambda;","lambda"],
["&mu;","mu"],
["&nu;","nu"],
["&xi;","xi"],
["&omicron;","omicron"],
["&pi;","pi"],
["&rho;","rho"],
["&sigmaf;","sigmaf"],
["&sigma;","sigma"],
["&tau;","tau"],
["&upsilon;","upsilon"],
["&phi;","phi"],
["&chi;","chi"],
["&psi;","psi"],
["&omega;","omega"],
["&thetasym;","thetasym"],
["&upsih;","upsih"],
["&piv;","piv"],
["&bull;","bull"],
["&hellip;","hellip"],
["&prime;","prime"],
["&Prime;","Prime"],
["&oline;","oline"],
["&frasl;","frasl"],
["&weierp;","weierp"],
["&image;","image"],
["&real;","real"],
["&trade;","trade"],
["&alefsym;","alefsym"],
["&larr;","larr"],
["&rarr;","rarr"],
["&darr;","darr"],
["&harr;","harr"],
["&crarr;","crarr"],
["&lArr;","lArr"],
["&uArr;","uArr"],
["&rArr;","rArr"],
["&dArr;","dArr"],
["&hArr;","hArr"],
["&forall;","forall"],
["&part;","part"],
["&exist;","exist"],
["&empty;","empty"],
["&nabla;","nabla"],
["&isin;","isin"],
["&notin;","notin"],
["&ni;","ni"],
["&prod;","prod"],
["&sum;","sum"],
["&minus;","minus"],
["&lowast;","lowast"],
["&prop;","prop"],
["&infin;","infin"],
["&ang;","ang"],
["&and;","and"],
["&or;","or"],
["&cap;","cap"],
["&cup;","cup"],
["&int;","int"],
["&there4;","there4"],
["&sim;","sim"],
["&cong;","cong"],
["&asymp;","asymp"],
["&ne;","ne"],
["&equiv;","equiv"],
["&le;","le"],
["&ge;","ge"],
["&sub;","sub"],
["&sup;","sup"],
["&nsub;","nsub"],
["&sube;","sube"],
["&supe;","supe"],
["&oplus;","oplus"],
["&otimes;","otimes"],
["&perp;","perp"],
["&sdot;","sdot"],
["&lceil;","lceil"],
["&rceil;","rceil"],
["&lfloor;","lfloor"],
["&rfloor;","rfloor"],
["&lang;","lang"],
["&rang;","rang"],
["&loz;","loz"],
["&spades;","spades"],
["&clubs;","clubs"],
["&hearts;","hearts"],
["&diams;","diams"]
];




};

