package hotmath.gwt.cm.client.ui.context;


import hotmath.gwt.cm_tools.client.data.InmhItemData;
import hotmath.gwt.cm_tools.client.data.PrescriptionSessionDataResource;

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
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_vocab.swf", "Math Vocabulary"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_like.swf", "Adding Like Fractions"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_unlike.swf", "Adding Unlike Fractions"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_multi.swf", "Multiplication Facts"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops.swf", "Order of Operations"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_reducingfractions.swf", "Reducing Fractions"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/inequality_one_variable.swf", "Inequalities on a Number Line"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/prime_factorization.swf", "Prime Factorization"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_comparing_quantities.swf", "Comparing Quantities"));
        
        resourceType.setItems(items);        
        add(resourceType);
        
        /** Add the Standard Flash Cards
         * 
         */
        t = "flashcard_spanish";
        resourceType = new PrescriptionSessionDataResource();
        resourceType.setType(t);
        resourceType.setLabel("Spanish Flash Cards  ");
        resourceType.setDescription("Spanish Flash Card and Skill Builder resources that might help you");
        
        
        /* 
         * Vocabulario Matemático (Math Vocabulary)
           Sumando Fracciones Semejantes (Adding Like Fractions)
           Sumando Fracciones no Semejantes (Adding Unlike Fractions)
           Hechos de la Multiplicación (Multiplication Facts)
           Orden de las Operaciones (Order of Operations)
           Reduciendo Fracciones (Reducing Fractions)
           Desigualdades en una Recta Numérica (Inequalities on a Number Line)
           Factorización Prima (Prima Factorizacion)
           Comparando Cantidades (Comparing Quantities)
           Multiplicando Fracciones (Multiplying Fractions)
       */
        
        
        items = new ArrayList<InmhItemData>();
        t = resourceType.getType();
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_vocab.swf?lang=es", "Vocabulario Matemático"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_like.swf?lang=es", "Sumando Fracciones Semejantes"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_addfrac_unlike.swf?lang=es", "Sumando Fracciones no Semejantes"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_multi.swf?lang=es", "Hechos de la Multiplicación"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_reducingfractions.swf?lang=es", "Reduciendo Fracciones"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_comparing_quantities.swf?lang=es", "La comparaci&oacute;n de las cantidades"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level1.swf?lang=es", "Orden de las Operaciones: Nivel 1"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_oops_level2.swf?lang=es", "Orden de las Operaciones: Nivel 2"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_improper.swf?lang=es", "Conversi&oacute;n de fracciones incorrecta"));
        items.add(new InmhItemData(t,"/learning_activities/interactivities/flashcard_convert_mixed.swf?lang=es", "Conversi&oacute;n de n&uacute; meros mixtos"));
        
        resourceType.setItems(items);        
        add(resourceType);
    }
}
