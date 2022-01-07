package by.pavka.march.view;

import static by.pavka.march.BuonaparteGame.HEX_SIZE_M;
import static by.pavka.march.BuonaparteGame.TILE_SIZE_PX;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;
import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;

public class ForceRep extends Image {
    Nation nation;
    final Force repForce;
    ForceRep superRep;
    Array<ForceRep> repSubForces;
    ObjectMap<Force, Hex> visualEnemies;
    ObjectSet<Hex> reconArea;
    Strength repStrength;
    Spirit repSpirit;
    float visualTime;
    Hex visualHex;
    Array<Path> visualPath;
    Array<Path> visualTail;
    ShapeRenderer shapeRenderer;
    boolean showPath;

    public static Object reconEnemy(Force force) {
        //TODO
        return new Object();
    }

    public ForceRep(Force force) {
        repForce = force;
//        nation = force.nation;
//        shapeRenderer = force.shapeRenderer;
        repSubForces = new Array<>();
        visualEnemies = new ObjectMap<>();
        reconArea = new ObjectSet<>();
        repStrength = new Strength(force.strength);
        repSpirit = new Spirit(force.spirit);
        visualHex = force.hex;
        visualPath = new Array<>();
        visualTail = new Array<>();
    }

    public void setVisualHex(Hex hex) {
        if (visualHex != null) {
            visualHex.removeActor(this);
        }
        visualHex = hex;
        visualHex.addActor(this);
        double len = repStrength.length;
        int iconSize;
        if (len < HEX_SIZE_M / 3) {
            iconSize = 1;
        } else if (len < HEX_SIZE_M) {
            iconSize = 2;
        } else if (len < HEX_SIZE_M * 2) {
            iconSize = 3;
        } else {
            iconSize = 4;
        }
        setBounds(0, 0, iconSize * TILE_SIZE_PX / 5.0f, iconSize * TILE_SIZE_PX / 5.0f);
        float offsetY = TILE_SIZE_PX * (1 - iconSize / 5.0f) / 2;
        float offsetX = TILE_SIZE_PX * (0.75f - iconSize / 5.0f) / 2;
        setPosition(offsetX, offsetY);

    }


    public void update(Array<ForceRep> subForces, ObjectMap<Force, Hex> enemies, ObjectSet<Hex> area, Strength strength,
                       Spirit spirit, Hex hex, Array<Path> path, Array<Path> tail, float time) {
        repSubForces = subForces;
        visualEnemies = enemies;
        reconArea = area;
        repStrength = strength;
        repSpirit = spirit;
        visualHex = hex;
        visualPath = path;
        visualTail = tail;
        visualTime = time;
    }

    public void setRenderer() {
        shapeRenderer = repForce.shapeRenderer;
        nation = repForce.nation;
    }

    public void detach() {
        if (superRep != null) {
            superRep.repSubForces.removeValue(this, true);
            superRep = null;
        }
    }

    public void attach(ForceRep forceRep) {
        repSubForces.add(forceRep);
        forceRep.superRep = this;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (nation == repForce.playScreen.game.nation || repForce.playScreen.enemies.containsKey(this.repForce)) {
            super.draw(batch, parentAlpha);
            batch.end();
            for (Path path : visualTail) {
                path.render(shapeRenderer, 0, 0, 1);
            }
            if (!visualPath.isEmpty()) {
                Path p = visualPath.first();
                p.render(shapeRenderer, 1, 0, 0);
                if (showPath) {
                    for (Path path : visualPath) {
                        path.render(shapeRenderer, 0.4f, 0.2f, 0.4f);
                    }
                }
            }
            batch.begin();
        }
    }
}
