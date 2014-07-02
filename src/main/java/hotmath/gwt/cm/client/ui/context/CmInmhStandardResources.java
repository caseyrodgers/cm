package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.InmhItemData.CmResourceType;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.SubMenuItem;
import hotmath.gwt.cm_tools.client.model.FlashcardModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Encapsulates the standard resources.
 * 
 * Probably, we will need to include this data during login. But, since this
 * data does not change we might as well have it static.
 * 
 * @author casey
 * 
 */
public class CmInmhStandardResources extends ArrayList<PrescriptionSessionDataResource> {

	private static final long serialVersionUID = 3102645302438691500L;
	
	static List<FlashcardModel> englishFlashcardList;
	static {
		englishFlashcardList = new ArrayList<FlashcardModel>();

		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Adding Like Fractions",
				"/learning_activities/interactivities/flashcard_addfrac_like.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Adding Unlike Fractions",
				"/learning_activities/interactivities/flashcard_addfrac_unlike.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Multiplying a Fraction by an Integer",
				"/learning_activities/interactivities/flashcard_multi_frac_int.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Multiplying Fractions",
				"/learning_activities/interactivities/flashcard_muti_frac.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Dividng Fractions",
				"/learning_activities/interactivities/flashcard_div_frac.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Converting Improper Fractions",
				"/learning_activities/interactivities/flashcard_convert_improper.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Converting Mixed Numbers",
				"/learning_activities/interactivities/flashcard_convert_mixed.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Reducing Fractions",
				"/learning_activities/interactivities/flashcard_reducingfractions.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Converting Fractions to Decimals",
				"/learning_activities/interactivities/flashcard_convert_fractodeci.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Fractions", "Reciprocals",
				"/learning_activities/interactivities/flashcard_reciprocals.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", "Decimals", "Adding Decimals",
				"/learning_activities/interactivities/flashcard_add_sub_deci.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Decimals", "Converting Decimals to Fractions",
				"/learning_activities/interactivities/flashcard_convert_decitofrac.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Decimals", "Multiplying Decimals",
				"/learning_activities/interactivities/flashcard_multi_deci.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Decimals", "Dividing Decimals",
				"/learning_activities/interactivities/flashcard_div_deci.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", "Percents", "Percents: Level 1",
				"/learning_activities/interactivities/flashcard_percents_level1.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Percents", "Percents: Level 2",
				"/learning_activities/interactivities/flashcard_percents_level2.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", "Negatives", "Adding with Negatives",
				"/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=add"));
		englishFlashcardList.add(new FlashcardModel("EN", "Negatives", "Subtracting with Negatives",
				"/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=sub"));
		englishFlashcardList.add(new FlashcardModel("EN", "Negatives", "Multiplying with Negatives",
				"/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=multi"));
		englishFlashcardList.add(new FlashcardModel("EN", "Negatives", "Dividing with Negatives",
				"/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=div"));

		englishFlashcardList.add(new FlashcardModel("EN", "Order of Operations", "Order of Operations: Level 1",
				"/learning_activities/interactivities/flashcard_oops_level1.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Order of Operations", "Order of Operations: Level 2",
				"/learning_activities/interactivities/flashcard_oops_level2.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", null, "Comparing Quantities",
				"/learning_activities/interactivities/flashcard_comparing_quantities.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", "Multiplication", "Multiplication Facts",
				"/learning_activities/interactivities/flashcard_multi.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Multiplication", "The Distributive Property",
				"/learning_activities/interactivities/flashcard_dist_prop.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", null, "Prime Factorization",
				"/learning_activities/interactivities/prime_factorization.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", null, "Greatest Common Factors",
				"/learning_activities/interactivities/flashcard_gcf.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", "Exponents", "Exponents: Level 1",
				"/learning_activities/interactivities/flashcard_exponents_level1.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Exponents", "Exponents: Level 2",
				"/learning_activities/interactivities/flashcard_exponents_level2.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Exponents", "Properties of Exponents",
				"/learning_activities/interactivities/flashcard_props_exponents.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Exponents", "Quotient of Powers Property",
				"/learning_activities/interactivities/flashcard_exp_quotient_power.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Exponents", "Power of a Quotient Property",
				"/learning_activities/interactivities/flashcard_exp_power_quotient.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Exponents", "Power of a Power Property",
				"/learning_activities/interactivities/flashcard_exp_power_power.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Exponents", "Power of a Product Property",
				"/learning_activities/interactivities/flashcard_exp_power_prod.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Exponents", "Product of Powers Property",
				"/learning_activities/interactivities/flashcard_exp_prod_power.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", "Graphing", "Inequality in One Variable",
				"/learning_activities/interactivities/inequality_one_variable.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Graphing", "Plotting Coordinates",
				"/learning_activities/interactivities/plot_coordplane.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Graphing", "Slope",
				"/learning_activities/interactivities/slope.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Graphing", "Distance Formula",
				"/learning_activities/interactivities/distance_formula.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Graphing", "Finding Coordinates",
				"/learning_activities/interactivities/catchthefly_cm.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Graphing", "Slope-Intercept Form",
				"/learning_activities/interactivities/slope_intercept_form.swf"));
		englishFlashcardList.add(new FlashcardModel("EN", "Graphing", "Plot on the Number Line",
				"/learning_activities/interactivities/plot_numberline.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", null, "Word Problems",
				"/learning_activities/interactivities/flashcard_wordproblems_level1.swf"));

		englishFlashcardList.add(new FlashcardModel("EN", null, "Combine Like Terms",
				"/learning_activities/interactivities/flashcard_combine_liketerms.swf"));		
	}

	static List<FlashcardModel> spanishFlashcardList;
	static {
		spanishFlashcardList = new ArrayList<FlashcardModel>();

		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Sumando fracciones semejantes",
				"/learning_activities/interactivities/flashcard_addfrac_like.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Sumando fracciones no semejantes",
				"/learning_activities/interactivities/flashcard_addfrac_unlike.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Multiplicando una fracci&oacute;n por un entero",
				"/learning_activities/interactivities/flashcard_multi_frac_int.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Multiplicando Fracciones",
				"/learning_activities/interactivities/flashcard_muti_frac.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Dividiendo fracciones",
				"/learning_activities/interactivities/flashcard_div_frac.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Converting Improper Fracciones",
				"/learning_activities/interactivities/flashcard_convert_improper.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Convirtiendo n&uacute;meros mixtos",
				"/learning_activities/interactivities/flashcard_convert_mixed.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Reduciendo fracciones",
				"/learning_activities/interactivities/flashcard_reducingFracciones.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Convirtiendo fracciones en decimales",
				"/learning_activities/interactivities/flashcard_convert_fractodeci.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Fracciones", "Rec&iacute;procos",
				"/learning_activities/interactivities/flashcard_reciprocals.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", "Decimales", "Sumando decimales",
				"/learning_activities/interactivities/flashcard_add_sub_deci.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Decimales", "Convirtiendo decimales en fracciones",
				"/learning_activities/interactivities/flashcard_convert_decitofrac.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Decimales", "Multiplicando decimales",
				"/learning_activities/interactivities/flashcard_multi_deci.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Decimales", "Dividiendo decimales",
				"/learning_activities/interactivities/flashcard_div_deci.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", "Porcentajes", "Porcentajes: Nivel 1",
				"/learning_activities/interactivities/flashcard_percents_level1.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Porcentajes", "Porcentajes: Nivel 2",
				"/learning_activities/interactivities/flashcard_percents_level2.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", "Negativos", "Sumas con negativos",
				"/learning_activities/interactivities/flashcard_add_sub_neg.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Negativos", "Multiplicando con negativos",
				"/learning_activities/interactivities/flashcard_multi_div_neg.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", "Orden de las operaciones", "Orden de las operaciones: Nivel 1",
				"/learning_activities/interactivities/flashcard_oops_level1.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Orden de las operaciones", "Orden de las operaciones: Nivel 2",
				"/learning_activities/interactivities/flashcard_oops_level2.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", null, "Comparando cantidades",
				"/learning_activities/interactivities/flashcard_comparing_quantities.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", "Multiplicaci&oacute;n", "Hechos de la multiplicaci&oacute;n",
				"/learning_activities/interactivities/flashcard_multi.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Multiplicaci&oacute;n", "La propiedad distributiva",
				"/learning_activities/interactivities/flashcard_dist_prop.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", null, "Factorizaci&oacute;n prima",
				"/learning_activities/interactivities/prime_factorization.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", null, "Factores comunes m&aacute;s grandes",
				"/learning_activities/interactivities/flashcard_gcf.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", "Exponentes", "Exponentes: Nivel 1",
				"/learning_activities/interactivities/flashcard_exponents_level1.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Exponentes", "Exponentes: Nivel 2",
				"/learning_activities/interactivities/flashcard_exponents_level2.swf?lang=es"));
		spanishFlashcardList.add(new FlashcardModel("SP", "Exponentes", "Propiedades de los exponentes",
				"/learning_activities/interactivities/flashcard_props_exponents.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", "Graficando", "Desigualdad con una variable",
				"/learning_activities/interactivities/inequality_one_variable.swf?lang=es"));
		//spanishFlashcardList.add(new FlashcardModel("SP", "Graficando", "Graficando coordenadas",
		//		"/learning_activities/interactivities/plot_coordplane.swf?lang=es"));
		//spanishFlashcardList.add(new FlashcardModel("SP", "Graficando", "Pendiente",
		//		"/learning_activities/interactivities/slope.swf?lang=es"));
		//spanishFlashcardList.add(new FlashcardModel("SP", "Graficando", "F&oacute;rmula de la distancia",
		//		"/learning_activities/interactivities/distance_formula.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", null, "Problemas con palabras",
				"/learning_activities/interactivities/flashcard_wordproblems_level1.swf?lang=es"));

		spanishFlashcardList.add(new FlashcardModel("SP", null, "Combinar como t&eacute;rminos",
				"/learning_activities/interactivities/flashcard_combine_liketerms.swf?lang=es"));		
	}

	static public List<FlashcardModel> getEnglishFlashcardList() {
		return englishFlashcardList;
	}

	static public List<FlashcardModel> getSpanishFlashcardList() {
		return spanishFlashcardList;
	}

    public CmInmhStandardResources() {

        /**
         * Add the standard Activities
         * 
         */
        CmResourceType t = CmResourceType.ACTIVITY_STANDARD;
        PrescriptionSessionDataResource resourceType = new PrescriptionSessionDataResource();
        resourceType.setType(t);
        resourceType.setLabel("Math Games");
        resourceType.setDescription("Math games that might help you");

        List<InmhItemData> items = new ArrayList<InmhItemData>();
        items.add(new InmhItemData(t, "/hotmath_help/games/numberchef/numchef.swf", "Number Chef"));
        items.add(new InmhItemData(t, "/hotmath_help/games/ctf/ctf_hotmath.swf", "Catch The Fly"));
        items.add(new InmhItemData(t, "/hotmath_help/games/numbercop/numbercop_hotmath.swf", "Number Cop"));
        items.add(new InmhItemData(t, "/hotmath_help/games/kp/kp_hotmath_sound.swf", "Algebra vs. the Cockroaches"));
        items.add(new InmhItemData(t, "/hotmath_help/games/factortris/factortris_hotmath_sound.swf", "Factortris"));
        resourceType.setItems(items);
        add(resourceType);

        /**
         * Add the Standard Flash Cards
         * 
         */
        t = CmResourceType.FLASHCARD;

        List<SubMenuItem> menu = new ArrayList<SubMenuItem>();

        resourceType = new PrescriptionSessionDataResource();

        resourceType = new PrescriptionSessionDataResource();
        resourceType.setType(t);
        resourceType.setLabel("Flash Cards and Skill Builders");
        resourceType.setDescription("Flash Card and Skill Builder resources that might help you");

        SubMenuItem subMainEnglish = new SubMenuItem("English");
        menu.add(subMainEnglish);
        subMainEnglish.setChildren(createEnglishSubmenu(t, resourceType));

        SubMenuItem subMainSpanish = new SubMenuItem("Spanish");
        menu.add(subMainSpanish);
        subMainSpanish.setChildren(createSpanishSubmenu(t, resourceType));

        resourceType.setSubMenuItems(menu);
        add(resourceType);
    }

    private List<SubMenuItem> createEnglishSubmenu(CmResourceType type, PrescriptionSessionDataResource resourceType) {

        List<SubMenuItem> subMenu = new ArrayList<SubMenuItem>();
        SubMenuItem smItem = null;
        String category = "";

        for (FlashcardModel model : englishFlashcardList) {
        	if ((model.getCategory() == null && category != null) ||
            	(model.getCategory() != null && category == null) ||
            	(category != null && category.equals(model.getCategory()) == false)) {
        		if (smItem != null) subMenu.add(smItem);
        		category = model.getCategory();
        		smItem = new SubMenuItem(category);
        	}
            smItem.getItemData().add(new InmhItemData(type, model.getLocation(), model.getDescription()));
        }
		if (smItem != null) subMenu.add(smItem);
        return subMenu;
    }

    /**
     * 
     * 
     * Spanish
     * ------------------------------------------------------------------
     * 
     * -- Problemas con palabras (flashcard_wordproblems_level1.swf) #NO
     * SUBMENU#
     */

    private List<SubMenuItem> createSpanishSubmenu(CmResourceType type, PrescriptionSessionDataResource resourceType) {

        List<SubMenuItem> subMenu = new ArrayList<SubMenuItem>();
        SubMenuItem smItem = null;
        String category = "";

        for (FlashcardModel model : spanishFlashcardList) {
        	if ((model.getCategory() == null && category != null) ||
        		(model.getCategory() != null && category == null) ||
            	(category != null && category.equals(model.getCategory()) == false)) {
        		if (smItem != null) subMenu.add(smItem);
        		category = model.getCategory();
        		smItem = new SubMenuItem(category);
        	}
            smItem.getItemData().add(new InmhItemData(type, model.getLocation(), model.getDescription()));
        }
		if (smItem != null) subMenu.add(smItem);
        return subMenu;
    }

}
