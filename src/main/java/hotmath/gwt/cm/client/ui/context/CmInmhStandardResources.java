package hotmath.gwt.cm.client.ui.context;

import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.SubMenuItem;

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

    public CmInmhStandardResources() {

        /**
         * Add the standard Activities
         * 
         */
        String t = "activity_standard";
        PrescriptionSessionDataResource resourceType = new PrescriptionSessionDataResource();
        resourceType.setType(t);
        resourceType.setLabel("Math Games");
        resourceType.setDescription("Math games that might help you");

        List<InmhItemData> items = new ArrayList<InmhItemData>();
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
        t = "flashcard";

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

    private List<SubMenuItem> createEnglishSubmenu(String t, PrescriptionSessionDataResource resourceType) {

        List<SubMenuItem> subMenu = new ArrayList<SubMenuItem>();
        SubMenuItem si = new SubMenuItem("Fractions");

        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_addfrac_like.swf",
                        "Adding Like Fractions"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_addfrac_unlike.swf",
                        "Adding Unlike Fractions"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi_frac_int.swf",
                        "Multiplying a Fraction by an Integer"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_muti_frac.swf",
                        "Multiplying Fractions"));
        si.getItemData()
                .add(new InmhItemData(t, "/learning_activities/interactivities/flashcard_div_frac.swf",
                        "Dividing Fractions"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_convert_improper.swf",
                        "Converting Improper Fractions"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_convert_mixed.swf",
                        "Converting Mixed Numbers"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_reducingfractions.swf",
                        "Reducing Fractions"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_convert_fractodeci.swf",
                        "Converting Fractions to Decimals"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_reciprocals.swf", "Reciprocals"));

        subMenu.add(si);

        si = new SubMenuItem("Decimals");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_add_sub_deci.swf",
                        "Adding Decimals"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_convert_decitofrac.swf",
                        "Converting Decimals to Fractions"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi_deci.swf",
                        "Multiplying Decimals"));
        si.getItemData()
                .add(new InmhItemData(t, "/learning_activities/interactivities/flashcard_div_deci.swf",
                        "Dividing Decimals"));

        subMenu.add(si);

        si = new SubMenuItem("Percents");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_percents_level1.swf",
                        "Percents: Level 1"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_percents_level2.swf",
                        "Percents: Level 2"));
        subMenu.add(si);

        si = new SubMenuItem("Negatives");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=add",
                        "Adding with Negatives"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_add_sub_neg.swf?actType=sub",
                        "Subtracting with Negatives"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi_div_neg.swf?actType=multi",
                        "Multiplying with Negatives"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi_div_neg.swf?actType=div",
                        "Dividing with Negatives"));
        subMenu.add(si);

        si = new SubMenuItem("Order of Operations");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_oops_level1.swf",
                        "Order of Operations: Level 1"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_oops_level2.swf",
                        "Order of Operations: Level 2"));
        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_comparing_quantities.swf",
                        "Comparing Quantities"));
        subMenu.add(si);

        si = new SubMenuItem("Multiplication");
        si.getItemData()
                .add(new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi.swf",
                        "Multiplication Facts"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_dist_prop.swf",
                        "The Distributive Property"));
        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/prime_factorization.swf",
                        "Prime Factorization"));
        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData()
                .add(new InmhItemData(t, "/learning_activities/interactivities/flashcard_gcf.swf",
                        "Greatest Common Factors"));
        subMenu.add(si);

        si = new SubMenuItem("Exponents");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exponents_level1.swf",
                        "Exponents: Level 1"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exponents_level2.swf",
                        "Exponents: Level 2"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_props_exponents.swf",
                        "Properties of Exponents"));
        
        
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exp_quotient_power.swf",
                        "Quotient of Powers Property"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exp_power_quotient.swf",
                        "Power of a Quotient Property"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exp_power_power.swf",
                        "Power of a Power Property"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exp_power_prod.swf",
                        "Power of a Product Property"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exp_prod_power.swf",
                        "Product of Powers Property"));        

        subMenu.add(si);

        si = new SubMenuItem("Graphing");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/inequality_one_variable.swf",
                        "Inequality in One Variable"));
        si.getItemData()
                .add(new InmhItemData(t, "/learning_activities/interactivities/plot_coordplane.swf",
                        "Plotting Coordinates"));
        si.getItemData().add(new InmhItemData(t, "/learning_activities/interactivities/slope.swf", "Slope"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/distance_formula.swf", "Distance Formula"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/catchthefly_cm.swf ", "Finding Coordinates"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/slope_intercept_form.swf",
                        "Slope-Intercept Form"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/plot_numberline.swf",
                        "Plot on the Number Line"));

        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_wordproblems_level1.swf",
                        "Word Problems"));
        subMenu.add(si);
        
        
        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_combine_liketerms.swf",
                        "Combine Like Terms"));        
        
        subMenu.add(si);
        
        
        return subMenu;
    }

    /**
     * 
     * 
     * Spanish
     * ------------------------------------------------------------------
     * ------------
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * 
     * -- Problemas con palabras (flashcard_wordproblems_level1.swf) #NO
     * SUBMENU#
     */

    private List<SubMenuItem> createSpanishSubmenu(String t, PrescriptionSessionDataResource resourceType) {
        /**
         * Add the Standard Spanish Flash Cards
         * 
         */
        List<SubMenuItem> subMenu = new ArrayList<SubMenuItem>();

        SubMenuItem si = new SubMenuItem("Fracciones");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_addfrac_like.swf?lang=es",
                        "Sumando fracciones semejantes"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_addfrac_unlike.swf?lang=es",
                        "Sumando fracciones no semejantes"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi_frac_int.swf?lang=es",
                        "Multiplicando una fracci&oacute;n por un entero"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_muti_frac.swf?lang=es",
                        "Multiplicando fracciones"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_div_frac.swf?lang=es",
                        "Dividiendo fracciones"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_convert_mixed.swf?lang=es",
                        "Convirtiendo n&uacute;meros mixtos"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_reducingfractions.swf?lang=es",
                        "Reduciendo fracciones"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_convert_fractodeci.swf?lang=es",
                        "Convirtiendo fracciones en decimales"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_reciprocals.swf?lang=es",
                        "Rec&iacute;procos"));
        subMenu.add(si);

        si = new SubMenuItem("Decimales");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_add_sub_deci.swf?lang=es",
                        "Sumando decimales"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_convert_decitofrac.swf?lang=es",
                        "Convirtiendo decimales en fracciones"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi_deci.swf?lang=es",
                        "Multiplicando decimales"));
        subMenu.add(si);

        si = new SubMenuItem("Porcentajes");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_percents_level1.swf?lang=es",
                        "Porcentajes: Nivel 1"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_percents_level2.swf?lang=es",
                        "Porcentajes: Nivel 2"));
        subMenu.add(si);

        si = new SubMenuItem("Negativos");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_add_sub_neg.swf?lang=es",
                        "Sumas con negativos"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi_div_neg.swf?lang=es",
                        "Multiplicando con negativos"));
        subMenu.add(si);

        si = new SubMenuItem("Orden de las operaciones");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_oops_level1.swf?lang=es",
                        "Orden de las operaciones: Nivel 1"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_oops_level2.swf?lang=es",
                        "Orden de las operaciones: Nivel 2"));
        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_comparing_quantities.swf",
                        "Comparando cantidades"));
        subMenu.add(si);

        si = new SubMenuItem("Multiplicaci&oacute;n");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_multi.swf?lang=es",
                        "Hechos de la multiplicaci&oacute;n"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_dist_prop.swf?lang=es",
                        "La propiedad distributiva"));
        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/prime_factorization.swf",
                        "Factorizaci&oacute;n prima"));
        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_gcf.swf",
                        "Factores comunes m&aacute;s grandes"));
        subMenu.add(si);

        si = new SubMenuItem("Exponentes");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exponents_level1.swf?lang=es",
                        "Exponentes: Nivel 1"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_exponents_level2.swf?lang=es",
                        "Exponentes: Nivel 2"));
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_props_exponents.swf?lang=es",
                        "Propiedades de los exponentes"));
        subMenu.add(si);

        si = new SubMenuItem("Graficando");
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/inequality_one_variable.swf?lang=es",
                        "Desigualdad con una variable"));
        // si.getItemData().add(new InmhItemData(t,
        // "/learning_activities/interactivities/plot_coordplane.swf?lang=es","Graficando coordenadas"));
        // si.getItemData().add(new InmhItemData(t,
        // "/learning_activities/interactivities/slope.swf?lang=es","Pendiente"));
        // si.getItemData().add(new InmhItemData(t,
        // "/learning_activities/interactivities/distance_formula.swf?lang=es","F&oacute;rmula de la distancia"));
        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_wordproblems_level1.swf?lang=es",
                        "Problemas con palabras"));
        subMenu.add(si);
        
        
        si = new SubMenuItem(null);
        si.getItemData().add(
                new InmhItemData(t, "/learning_activities/interactivities/flashcard_combine_liketerms.swf?lang=es",
                        "Combinar como t&eacute;rminos"));        
        subMenu.add(si);

        return subMenu;
    }
}
