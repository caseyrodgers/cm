var _fcContent = '<div class="yui3-g" id="bd"><div class="yui3-u" id="main"><div id="flashcards" class="yui3-menu yui3-menu-horizontal" role="menu"><div class="yui3-menu-content" role="presentation" id="flashcard_content2"><ul class="first-of-type" role="presentation" id="flashcard_menu2">';

var _lang = "";
var _category = "";
var _langLabel;
var _index = 0;
var _categIndex = 0;
var _noSubMenu = false;

function listFlashcards() {
	for (var i=0; i < _flashcardJSON.length; i++) {
		if (_lang == null || _lang.localeCompare(_flashcardJSON[i].lang) != 0) {
			// starting a new lang (EN or ES)
			if (_lang.localeCompare("") != 0) {
				_fcContent += '</ul></div></div></li>';
				if (_noSubMenu == false) {
					_fcContent += '</ul></div></div></li>';
				}
				_categIndex = 0;
			}
			_noSubMenu = false;
			_index++;
			_lang = _flashcardJSON[i].lang;
			_fcContent += '<li role="presentation" id="flashcard_menu_' + _index + '">';
			if (_lang.localeCompare("EN") == 0) {
				_langLabel = 'English';
				_fcContent += '<li role="presentation" class="menu-label-spacer" id="flashcard_menu_' + _index + '">';
			}
			else if (_lang.localeCompare("ES") == 0) {
				_langLabel = 'Spanish';
				_fcContent += '<li role="presentation" id="flashcard_menu_' + _index + '">';
			}
			_fcContent += '<div class="yui3-menu-label" id="flashcard_menu_' + _langLabel + '" role="menuitem" aria-haspopup="true" tabindex="-1">' + _langLabel + '</div>';
			_fcContent += '<div id="' + _langLabel + '-div" class="yui3-menu yui3-menu-hidden" role="menu" aria-hidden="true" style="height: 184px; width: 155px;">';
            _fcContent += '<div class="yui3-menu-content" role="presentation" id="' + _langLabel + '-menu-div">';
            _fcContent += '<ul role="presentation" class="first-of-type" id="' + _langLabel + '-menu-list">';
		}

		if (_category.localeCompare("") == 0 || _category.localeCompare(_flashcardJSON[i].category) != 0) {

			if (_flashcardJSON[i].category != null) {
				// starting a new category
				if (_category.localeCompare("") != 0 && _categIndex > 0 && _noSubMenu == false) {
					_fcContent += '</ul></div></div></li>';
				}
				_noSubMenu = false;
				_categIndex++;
				_category = _flashcardJSON[i].category;
				_fcContent += '<li role="presentation" id="' + _langLabel + '-menu-item-' + _categIndex + '">';
				_fcContent += '<div class="yui3-menu-label" id="' + _category + '_label" role="menuitem" aria-haspopup="true">' + _category + '</div>';
				_fcContent += '<div id="' + _category + '" class="yui3-menu yui3-menu-hidden" role="menu" aria-hidden="true" style="height: 154px; width: 121px;">';
				_fcContent += '<div class="yui3-menu-content" role="presentation">';
				_fcContent += '<ul role="presentation" class="first-of-type">';
			}
			else {
				if (_noSubMenu == false) {
					_fcContent += '</ul></div></div></li>';
				}
				_noSubMenu = true;
			}
		}
		addMenuItem(_flashcardJSON[i].location, _flashcardJSON[i].description);
 	}
	_fcContent += '</ul></div></div></li></ul></div></div></li></ul></div></div></div></div>';
	var elem = document.getElementById("math-flashcard-content");
	elem.innerHTML = _fcContent;
}

function addMenuItem(location, description) {
	// add the item
	_fcContent += '<li class="yui3-menuitem" role="presentation"><a class="yui3-menuitem-content" href="http://catchupmath.com';
	_fcContent += location + '" target="_blank" role="menuitem">';
	_fcContent += description + '</a></li>';	
}

var _flashcardJSON =
[
  {"lang":"EN","category":"Fractions","description":"Adding Like Fractions","location":"/learning_activities/interactivities/flashcard_addfrac_like.swf"},
  {"lang":"EN","category":"Fractions","description":"Adding Unlike Fractions","location":"/learning_activities/interactivities/flashcard_addfrac_unlike.swf"},
  {"lang":"EN","category":"Fractions","description":"Multiplying a Fraction by an Integer","location":"/learning_activities/interactivities/flashcard_multi_frac_int.swf"},
  {"lang":"EN","category":"Fractions","description":"Multiplying Fractions","location":"/learning_activities/interactivities/flashcard_muti_frac.swf"},
  {"lang":"EN","category":"Fractions","description":"Dividng Fractions","location":"/learning_activities/interactivities/flashcard_div_frac.swf"},
  {"lang":"EN","category":"Fractions","description":"Converting Improper Fractions","location":"/learning_activities/interactivities/flashcard_convert_improper.swf"},
  {"lang":"EN","category":"Fractions","description":"Converting Mixed Numbers","location":"/learning_activities/interactivities/flashcard_convert_mixed.swf"},
  {"lang":"EN","category":"Fractions","description":"Reducing Fractions","location":"/learning_activities/interactivities/flashcard_reducingfractions.swf"},
  {"lang":"EN","category":"Fractions","description":"Converting Fractions to Decimals","location":"/learning_activities/interactivities/flashcard_convert_fractodeci.swf"},
  {"lang":"EN","category":"Fractions","description":"Reciprocals","location":"/learning_activities/interactivities/flashcard_reciprocals.swf"},
  {"lang":"EN","category":"Decimals","description":"Adding Decimals","location":"/learning_activities/interactivities/flashcard_add_sub_deci.swf"},
  {"lang":"EN","category":"Decimals","description":"Converting Decimals to Fractions","location":"/learning_activities/interactivities/flashcard_convert_decitofrac.swf"},
  {"lang":"EN","category":"Decimals","description":"Multiplying Decimals","location":"/learning_activities/interactivities/flashcard_multi_deci.swf"},
  {"lang":"EN","category":"Decimals","description":"Dividing Decimals","location":"/learning_activities/interactivities/flashcard_div_deci.swf"},
  {"lang":"EN","category":"Percents","description":"Percents: Level 1","location":"/learning_activities/interactivities/flashcard_percents_level1.swf"},
  {"lang":"EN","category":"Percents","description":"Percents: Level 2","location":"/learning_activities/interactivities/flashcard_percents_level2.swf"},
  {"lang":"EN","category":"Negatives","description":"Adding with Negatives","location":"/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=add"},
  {"lang":"EN","category":"Negatives","description":"Subtracting with Negatives","location":"/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=sub"},
  {"lang":"EN","category":"Negatives","description":"Multiplying with Negatives","location":"/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=multi"},
  {"lang":"EN","category":"Negatives","description":"Dividing with Negatives","location":"/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=div"},
  {"lang":"EN","category":"Order of Operations","description":"Order of Operations: Level 1","location":"/learning_activities/interactivities/flashcard_oops_level1.swf"},
  {"lang":"EN","category":"Order of Operations","description":"Order of Operations: Level 2","location":"/learning_activities/interactivities/flashcard_oops_level2.swf"},
  {"lang":"EN","category":null,"description":"Comparing Quantities","location":"/learning_activities/interactivities/flashcard_comparing_quantities.swf"},
  {"lang":"EN","category":"Multiplication","description":"Multiplication Facts","location":"/learning_activities/interactivities/flashcard_multi.swf"},
  {"lang":"EN","category":"Multiplication","description":"The Distributive Property","location":"/learning_activities/interactivities/flashcard_dist_prop.swf"},
  {"lang":"EN","category":null,"description":"Prime Factorization","location":"/learning_activities/interactivities/prime_factorization.swf"},
  {"lang":"EN","category":null,"description":"Greatest Common Factors","location":"/learning_activities/interactivities/flashcard_gcf.swf"},
  {"lang":"EN","category":"Exponents","description":"Exponents: Level 1","location":"/learning_activities/interactivities/flashcard_exponents_level1.swf"},
  {"lang":"EN","category":"Exponents","description":"Exponents: Level 2","location":"/learning_activities/interactivities/flashcard_exponents_level2.swf"},
  {"lang":"EN","category":"Exponents","description":"Properties of Exponents","location":"/learning_activities/interactivities/flashcard_props_exponents.swf"},
  {"lang":"EN","category":"Exponents","description":"Quotient of Powers Property","location":"/learning_activities/interactivities/flashcard_exp_quotient_power.swf"},
  {"lang":"EN","category":"Exponents","description":"Power of a Quotient Property","location":"/learning_activities/interactivities/flashcard_exp_power_quotient.swf"},
  {"lang":"EN","category":"Exponents","description":"Power of a Power Property","location":"/learning_activities/interactivities/flashcard_exp_power_power.swf"},
  {"lang":"EN","category":"Exponents","description":"Power of a Product Property","location":"/learning_activities/interactivities/flashcard_exp_power_prod.swf"},
  {"lang":"EN","category":"Exponents","description":"Product of Powers Property","location":"/learning_activities/interactivities/flashcard_exp_prod_power.swf"},
  {"lang":"EN","category":"Graphing","description":"Inequality in One Variable","location":"/learning_activities/interactivities/inequality_one_variable.swf"},
  {"lang":"EN","category":"Graphing","description":"Plotting Coordinates","location":"/learning_activities/interactivities/plot_coordplane.swf"},
  {"lang":"EN","category":"Graphing","description":"Slope","location":"/learning_activities/interactivities/slope.swf"},
  {"lang":"EN","category":"Graphing","description":"Distance Formula","location":"/learning_activities/interactivities/distance_formula.swf"},
  {"lang":"EN","category":"Graphing","description":"Finding Coordinates","location":"/learning_activities/interactivities/catchthefly_cm.swf"},
  {"lang":"EN","category":"Graphing","description":"Slope-Intercept Form","location":"/learning_activities/interactivities/slope_intercept_form.swf"},
  {"lang":"EN","category":"Graphing","description":"Plot on the Number Line","location":"/learning_activities/interactivities/plot_numberline.swf"},
  {"lang":"EN","category":null,"description":"Word Problems","location":"/learning_activities/interactivities/flashcard_wordproblems_level1.swf"},
  {"lang":"EN","category":null,"description":"Combine Like Terms","location":"/learning_activities/interactivities/flashcard_combine_liketerms.swf"},

  {"lang":"ES","category":"Fracciones","description":"Sumando fracciones semejantes","location":"/learning_activities/interactivities/flashcard_addfrac_like.swf?lang=es"},
  {"lang":"ES","category":"Fracciones","description":"Sumando fracciones no semejantes","location":"/learning_activities/interactivities/flashcard_addfrac_unlike.swf?lang=es"},
  {"lang":"ES","category":"Fracciones","description":"Multiplicando una fracci&oacute;n por un entero","location":"/learning_activities/interactivities/flashcard_multi_frac_int.swf?lang=es"},
  {"lang":"ES","category":"Fracciones","description":"Multiplicando Fracciones","location":"/learning_activities/interactivities/flashcard_muti_frac.swf?lang=es"},
  {"lang":"ES","category":"Fracciones","description":"Dividiendo fracciones","location":"/learning_activities/interactivities/flashcard_div_frac.swf?lang=es"},
  {"lang":"ES","category":"Fracciones","description":"Convirtiendo n&uacute;meros mixtos","location":"/learning_activities/interactivities/flashcard_convert_mixed.swf?lang=es"},
  {"lang":"ES","category":"Fracciones","description":"Reduciendo fracciones","location":"/learning_activities/interactivities/flashcard_reducingFracciones.swf?lang=es"},
  {"lang":"ES","category":"Fracciones","description":"Convirtiendo fracciones en decimales","location":"/learning_activities/interactivities/flashcard_convert_fractodeci.swf?lang=es"},
  {"lang":"ES","category":"Fracciones","description":"Rec&iacute;procos","location":"/learning_activities/interactivities/flashcard_reciprocals.swf?lang=es"},
  {"lang":"ES","category":"Decimales","description":"Sumando decimales","location":"/learning_activities/interactivities/flashcard_add_sub_deci.swf?lang=es"},
  {"lang":"ES","category":"Decimales","description":"Convirtiendo decimales en fracciones","location":"/learning_activities/interactivities/flashcard_convert_decitofrac.swf?lang=es"},
  {"lang":"ES","category":"Decimales","description":"Multiplicando decimales","location":"/learning_activities/interactivities/flashcard_multi_deci.swf?lang=es"},
  {"lang":"ES","category":"Decimales","description":"Dividiendo decimales","location":"/learning_activities/interactivities/flashcard_div_deci.swf?lang=es"},
  {"lang":"ES","category":"Porcentajes","description":"Porcentajes: Nivel 1","location":"/learning_activities/interactivities/flashcard_percents_level1.swf?lang=es"},
  {"lang":"ES","category":"Porcentajes","description":"Porcentajes: Nivel 2","location":"/learning_activities/interactivities/flashcard_percents_level2.swf?lang=es"},
  {"lang":"ES","category":"Negativos","description":"Sumas con negativos","location":"/learning_activities/interactivities/flashcard_add_sub_neg.swf?lang=es"},
  {"lang":"ES","category":"Negativos","description":"Multiplicando con negativos","location":"/learning_activities/interactivities/flashcard_multi_div_neg.swf?lang=es"},
  {"lang":"ES","category":"Orden de las operaciones","description":"Orden de las operaciones: Nivel 1","location":"/learning_activities/interactivities/flashcard_oops_level1.swf?lang=es"},
  {"lang":"ES","category":"Orden de las operaciones","description":"Orden de las operaciones: Nivel 2","location":"/learning_activities/interactivities/flashcard_oops_level2.swf?lang=es"},
  {"lang":"ES","category":null,"description":"Comparando cantidades","location":"/learning_activities/interactivities/flashcard_comparing_quantities.swf?lang=es"},
  {"lang":"ES","category":"Multiplicaci&oacute;n","description":"Hechos de la multiplicaci&oacute;n","location":"/learning_activities/interactivities/flashcard_multi.swf?lang=es"},
  {"lang":"ES","category":"Multiplicaci&oacute;n","description":"La propiedad distributiva","location":"/learning_activities/interactivities/flashcard_dist_prop.swf?lang=es"},
  {"lang":"ES","category":null,"description":"Factorizaci&oacute;n prima","location":"/learning_activities/interactivities/prime_factorization.swf?lang=es"},
  {"lang":"ES","category":null,"description":"Factores comunes m&aacute;s grandes","location":"/learning_activities/interactivities/flashcard_gcf.swf?lang=es"},
  {"lang":"ES","category":"Exponentes","description":"Exponentes: Nivel 1","location":"/learning_activities/interactivities/flashcard_exponents_level1.swf?lang=es"},
  {"lang":"ES","category":"Exponentes","description":"Exponentes: Nivel 2","location":"/learning_activities/interactivities/flashcard_exponents_level2.swf?lang=es"},
  {"lang":"ES","category":"Exponentes","description":"Propiedades de los exponentes","location":"/learning_activities/interactivities/flashcard_props_exponents.swf?lang=es"},
  {"lang":"ES","category":"Graficando","description":"Desigualdad con una variable","location":"/learning_activities/interactivities/inequality_one_variable.swf?lang=es"},
  {"lang":"ES","category":null,"description":"Problemas con palabras","location":"/learning_activities/interactivities/flashcard_wordproblems_level1.swf?lang=es"},
  {"lang":"ES","category":null,"description":"Combinar como t&eacute;rminos","location":"/learning_activities/interactivities/flashcard_combine_liketerms.swf?lang=es"}
];