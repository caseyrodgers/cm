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
 *  
 *  activity_standard, /learning_activities/interactivities/diamond.swf, Diamond Problem Flash Cards
 *  activity_standard, /learning_activities/interactivities/quadratics.swf, Quadratics in Factored Form
 *  activity_standard, /learning_activities/interactivities/flashcard_addfrac_like.swf, Fraction Flash Cards: Like Denominators
 *  activity_standard, /learning_activities/interactivities/flashcard_addfrac_unlike.swf, Fraction Flash Cards: Unike Denominators
 *  
 *  
 *  flashcard, /learning_activities/interactivities/flashcard_vocab.swf, Math Vocabulary
 *  flashcard, /learning_activities/interactivities/flashcard_addfrac_like.swf, Adding Like Fractions
 *  flashcard, /learning_activities/interactivities/flashcard_addfrac_unlike.swf, Adding Unlike Fractions
 *  flashcard, /learning_activities/interactivities/flashcard_multi.swf, Multiplication Facts
 *  
 * @author casey
 *
 */
public class CmInmhStandardResources extends ArrayList<PrescriptionSessionDataResource>{
    
    
    public CmInmhStandardResources() {
        
        /** Add the standard Activities
         * 
         */
        PrescriptionSessionDataResource resourceType = new PrescriptionSessionDataResource();
        resourceType.setType("activity_standard");
        resourceType.setLabel("Math Games");
        resourceType.setDescription("Math games that might help you");
        
        List<InmhItemData> items = new ArrayList<InmhItemData>();
        items.add(new InmhItemData(resourceType.getType(),"/hotmath_help/games/ctf/ctf_hotmath.swf","Catch The Fly"));
        items.add(new InmhItemData(resourceType.getType(),"/hotmath_help/games/numbercop/numbercop_hotmath.swf", "Number Cop"));
        items.add(new InmhItemData(resourceType.getType(),"/hotmath_help/games/kp/kp_hotmath_sound.swf","Algebra vs. the Cockroaches"));
        items.add(new InmhItemData(resourceType.getType(),"/hotmath_help/games/factortris/factortris_hotmath_sound.swf", "Factortris"));
        resourceType.setItems(items);
        add(resourceType);

        
        
        /** Add the Standard Flash Cards
         * 
         */
        resourceType = new PrescriptionSessionDataResource();
        resourceType.setType("flashcard");
        resourceType.setLabel("Flash Cards");
        resourceType.setDescription("Flash Card resources that might help you");
        
        items = new ArrayList<InmhItemData>();
        items.add(new InmhItemData(resourceType.getType(),"/learning_activities/interactivities/flashcard_vocab.swf", "Math Vocabulary"));
        items.add(new InmhItemData(resourceType.getType(),"/learning_activities/interactivities/flashcard_addfrac_like.swf", "Adding Like Fractions"));
        items.add(new InmhItemData(resourceType.getType(),"/learning_activities/interactivities/flashcard_addfrac_unlike.swf", "Adding Unlike Fractions"));
        items.add(new InmhItemData(resourceType.getType(),"/learning_activities/interactivities/flashcard_multi.swf", "Multiplication Facts"));
        resourceType.setItems(items);        
        add(resourceType);
    }
}
