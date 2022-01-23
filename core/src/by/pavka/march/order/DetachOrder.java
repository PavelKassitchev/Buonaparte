package by.pavka.march.order;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.BuonaparteGame;
import by.pavka.march.PlayScreen;
import by.pavka.march.configuration.Configurator;
import by.pavka.march.configuration.FormationValidator;
import by.pavka.march.configuration.Nation;
import by.pavka.march.map.Hex;
import by.pavka.march.military.Force;
import by.pavka.march.military.Formation;

public class DetachOrder extends Order {
    public Formation hyper;
    private DetachForceOrder detachForceOrder;

    public DetachOrder(Force force) {
        hyper = (Formation)force;
//        detachForceOrder = new DetachForceOrder(force, false);
        detachForceOrder = new DetachForceOrder(this);
        hyper.visualOrders.addFirstOrder(detachForceOrder);
    }

    @Override
    public void set(Force force) {
//        Force sub = null;
//        if (force.nation == null) {
//            Formation group = (Formation) force;
//            sub = group.subForces.get(0);
//        } else {
//            sub = force;
//        }
//        hyper = (Formation)sub.findHyperForce();
//        detachForceOrder = new DetachForceOrder(force, false);
//        hyper.visualOrders.addFirstOrder(detachForceOrder);
        execute(force, 0);
    }

    @Override
    public boolean execute(Force force, float delta) {
        if (force == null) {
            return false;
        }
        if (force.nation == null) {
            Formation group = (Formation) force;
            Force sub = group.subForces.get(0);
            group.nation = sub.nation;
            group.remoteHeadForce = sub.findHyperForce().remoteHeadForce == null?
                    (Formation)(sub.findHyperForce()) : sub.findHyperForce().remoteHeadForce;

            for (Force f : group.subForces) {
                f.detach(true, false);
                f.superForce = group;
                f.remoteHeadForce = null;
                group.shapeRenderer = f.shapeRenderer;
                f.shapeRenderer = null;
            }

            group.setRealHex(sub.hex);
            group.sendReport("");
            System.out.println("Group size is " + group.subForces.size);
        }
        else {
            force.detach(true, true);
        }
        force.actualOrders.fulfillOrder(this);
//        hyper.visualOrders.removeOrder(detachForceOrder);
        hyper.actualOrders.fulfillOrder(detachForceOrder);
        return true;
    }

    @Override
    public boolean execute(Array<Force> forces) {
        if (forces == null || forces.isEmpty()) {
            return false;
        }
        for (Force f : forces) {
            f.detach(true, true);
        }
        PlayScreen playScreen = forces.get(0).playScreen;
        BuonaparteGame game = playScreen.game;
        Nation nation = forces.get(0).nation;
        Hex hex = forces.get(0).hex;
        int col = hex.col;
        int row = hex.row;
        Formation hq = forces.get(0).remoteHeadForce;
        ShapeRenderer renderer = forces.get(0).shapeRenderer;
        Force group = FormationValidator.createGroup(forces, game);
        group.nation = nation;
        group.remoteHeadForce = hq;
        group.shapeRenderer = renderer;
        Configurator.addForce(group, col, row, playScreen);
        return true;
    }
}
