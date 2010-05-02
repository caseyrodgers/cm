package hotmath.gwt.cm.client.ui.context;


import hotmath.gwt.cm_rpc.client.rpc.InmhItemData;
import hotmath.gwt.cm_rpc.client.rpc.PrescriptionSessionDataResource;

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
        resourceType = new PrescriptionSessionDataResource();
        resourceType.setType(t);
        resourceType.setLabel("Flash Cards and Skill Builders");
        resourceType.setDescription("Flash Card and Skill Builder resources that might help you");
        
        items = new ArrayList<InmhItemData>();
        t = resourceType.getType();
        
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_vocab.swf", "Mathematical Vocabulary"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_like.swf", "Adding Like Fractions"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_unlike.swf", "Adding Unlike Fractions"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_multi.swf", "Multiplication Facts"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level1.swf", "Order of Operations: Level 1"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level2.swf", "Order of Operations: Level 2"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_reducingfractions.swf", "Reducing Fractions"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_comparing_quantities.swf", "Comparing Quantities"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_improper.swf", "Converting Improper Fractions"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_mixed.swf", "Converting Mixed Numbers"));
        // items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_mixed_improper.swf", "Mixed Numbers & Improper Fractions"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_muti_frac.swf", "Multiplying Fractions"));
        
        resourceType.setItems(items);        
        add(resourceType);
        
        /** Add the Standard Flash Cards
         * 
         */
        t = "flashcard_spanish";
        resourceType = new PrescriptionSessionDataResource();
        resourceType.setType(t);
        resourceType.setLabel("Tarjetas Flash en Espa&ntilde;ol");
        resourceType.setDescription("Spanish Flash Card and Skill Builder resources that might help you");
        
        items = new ArrayList<InmhItemData>();
        t = resourceType.getType();

        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_vocab.swf?lang=es", "Vocabulario Matem&aacute;tico"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_like.swf?lang=es", "Sumando Fracciones Semejantes"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_unlike.swf?lang=es", "Sumando Fracciones no Semejantes"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_multi.swf?lang=es", "Hechos de la Multiplicaci&oacute;n"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level1.swf?lang=es", "Orden de las Operaciones: Nivel 1"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level2.swf?lang=es", "Orden de las Operaciones: Nivel 2"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_reducingfractions.swf?lang=es", "Reduciendo Fracciones"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_comparing_quantities.swf?lang=es", "Comparando Cantidades"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_improper.swf?lang=es", "Convirtiendo Fracciones Improprias"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_mixed.swf?lang=es", "Convirtiendo N&uacute;meros Mixtos"));
        // items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_mixed_improper.swf?lang=es", "Nuacute;meros Mixtos y Fracciones Improprias"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_muti_frac.swf?lang=es", "Multiplicando Fracciones"));
        
        
        resourceType.setItems(items);        
        add(resourceType);
        
    }
}
