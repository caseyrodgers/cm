package hotmath.gwt.cm.client.ui.context;


import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;
import hotmath.gwt.cm_rpc.client.rpc.SubMenuItem;

import java.util.ArrayList;
import java.util.List;

/** Encapsulates the standard resources.
 * 
 * Probably, we will need to include this data during login.
 * But, since this data does not change we might as well have it static.
 * 
 * @author casey
 *
 */
public class CmInmhStandardResources extends ArrayList<PrescriptionSessionDataResource>{
	
	
    public CmInmhStandardResources() {
        
        /** Add the standard Activities
         * 
         */
    	String t = "activity_standard";
        PrescriptionSessionDataResource resourceType = new PrescriptionSessionDataResource();
        resourceType.setType(t);
        resourceType.setLabel("Math Games");
        resourceType.setDescription("Math games that might help you");
        
        List<InmhItemData> items = new ArrayList<InmhItemData>();
        items.add(new InmhItemData(t,"/hotmath_help/games/ctf/ctf_hotmath.swf","Catch The Fly"));
        items.add(new InmhItemData(t,"/hotmath_help/games/numbercop/numbercop_hotmath.swf", "Number Cop"));
        items.add(new InmhItemData(t,"/hotmath_help/games/kp/kp_hotmath_sound.swf","Algebra vs. the Cockroaches"));
        items.add(new InmhItemData(t,"/hotmath_help/games/factortris/factortris_hotmath_sound.swf", "Factortris"));
        resourceType.setItems(items);
        add(resourceType);
        
        /** Add the Standard Flash Cards
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
        subMainEnglish.setChildren(createEnglishSubmenu(t,resourceType));
        
        SubMenuItem subMainSpanish = new SubMenuItem("Spanish");
        menu.add(subMainSpanish);
        subMainSpanish.setChildren(createSpanishSubmenu(t,resourceType));
        
        resourceType.setSubMenuItems(menu);
        add(resourceType);
    }	
    
	private List<SubMenuItem>  createEnglishSubmenu(String t,PrescriptionSessionDataResource resourceType) {
        
        List<SubMenuItem> subMenu = new ArrayList<SubMenuItem>();
        SubMenuItem si = new SubMenuItem("Fractions");
        
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_like.swf", "Adding Like Fractions"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_unlike.swf", "Adding Unlike Fractions"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_muti_frac.swf", "Multiplying Fractions"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_div_frac.swf", "Dividing Fractions"));        
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_improper.swf", "Converting Improper Fractions"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_mixed.swf", "Converting Mixed Numbers"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_reducingfractions.swf", "Reducing Fractions"));
        
        
        subMenu.add(si);
        
        
        si = new SubMenuItem("Decimals");
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_add_sub_deci.swf", "Adding Decimals"));
        subMenu.add(si);

        si = new SubMenuItem("Percents");
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_percents_level1.swf", "Percents: Level 1"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_percents_level2.swf", "Percents: Level 2"));
        subMenu.add(si);
        
        
        si = new SubMenuItem("Negatives");
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_add_sub_neg.swf", "Adding with Negatives"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_multi_div_neg.swf", "Multiplying with Negatives"));
        subMenu.add(si);
        
        si = new SubMenuItem("Order of Operations");
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level1.swf", "Order of Operations: Level 1"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level2.swf", "Order of Operations: Level 2"));
        subMenu.add(si);
        
        
        si = new SubMenuItem(null);
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_comparing_quantities.swf", "Comparing Quantities"));
        subMenu.add(si);
        
        si = new SubMenuItem("Multiplication");
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_multi.swf", "Multiplication Facts"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_dist_prop.swf", "The Distributive Property"));
        subMenu.add(si);
        
        si = new SubMenuItem(null);
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/prime_factorization.swf", "Prime Factorization"));
        subMenu.add(si);
        
        si = new SubMenuItem("Exponents");
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_exponents_level1.swf", "Exponents: Level 1"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_exponents_level2.swf", "Exponents: Level 2"));
        subMenu.add(si);
        
        si = new SubMenuItem("Word Problems");
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_wordproblems_level1.swf", "Word Problems: Level 1"));
        subMenu.add(si);
        
        return subMenu;
	}
    
	private List<SubMenuItem> createSpanishSubmenu(String t,PrescriptionSessionDataResource resourceType) {
        /** Add the Standard Spanish Flash Cards
         * 
         */
		List<SubMenuItem> subMenu = new ArrayList<SubMenuItem>();
		SubMenuItem si= new SubMenuItem("Fracciones");
        
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_reducingfractions.swf?lang=es", "Reduciendo Fracciones"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_like.swf?lang=es", "Sumando Fracciones Semejantes"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_unlike.swf?lang=es", "Sumando Fracciones no Semejantes"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_muti_frac.swf?lang=es", "Multiplicando Fracciones"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_improper.swf?lang=es", "Convirtiendo Fracciones Improprias"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_mixed.swf?lang=es", "Convirtiendo N&uacute;meros Mixtos"));
        subMenu.add(si);
        
        si = new SubMenuItem("Orden de las Operaciones");
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level1.swf?lang=es", "Orden de las Operaciones: Nivel 1"));
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level2.swf?lang=es", "Orden de las Operaciones: Nivel 2"));
        subMenu.add(si);

        si = new SubMenuItem(null);
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_comparing_quantities.swf?lang=es", "Comparando Cantidades"));
        subMenu.add(si);
        
        si = new SubMenuItem(null);
        si.getItemData().add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_multi.swf?lang=es", "Hechos de la Multiplicaci&oacute;n"));
        subMenu.add(si);
        
        return subMenu;
	}
}
