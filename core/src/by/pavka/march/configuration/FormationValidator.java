package by.pavka.march.configuration;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.PlayScreen;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;

public class FormationValidator {
    public static Force createGroup(Array<Force> forces, PlayScreen playScreen) {
        if (forces.size == 1) {
            return forces.get(0);
        }
        Formation formation = new Formation(playScreen.headForce, (playScreen.game).getTextureRegion("fr_cav"));
        for (Force f : forces) {
            formation.attach(f, false);
//            formation.viewForces.add(f);
        }
        formation.setName("Battle Group");
        formation.playScreen = forces.get(0).playScreen;
        formation.hex = forces.get(0).findHyperForce().hex;
        return formation;
    }

    public boolean canAttache(Force f) {
        return true;
    }
}
