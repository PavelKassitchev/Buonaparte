package by.pavka.march.configuration;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.BuonaparteGame;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;

public class FormationValidator {
    public static Force createGroup(Array<Force> forces, BuonaparteGame game) {
        if (forces.size == 1) {
            return forces.get(0);
        }
        Formation formation = new Formation(game.getTextureRegion("fr_cav"));
        for (Force f : forces) {
            formation.attach(f, false);
//            formation.viewForces.add(f);
        }
        formation.setName("Battle Group");
        formation.playScreen = forces.get(0).playScreen;
        return formation;
    }

    public boolean canAttache(Force f) {
        return true;
    }
}
