package by.pavka.march.order;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.Array;

import by.pavka.march.map.Hex;
import by.pavka.march.map.Path;
import by.pavka.march.military.Force;

public class FollowOrder extends MoveOrder {

    public Force target;
    public Force thisForce;

    public FollowOrder(Force target) {
        this.target = target;
        destinations = new Array<>();
    }


    public FollowOrder() {
        destinations = new Array<>();
    }

    public void setTargetForce(Force target) {
        this.target = target;
    }


    public void setDestination() {
        destinations.clear();
//        System.out.println("Target = " + target);
        destinations.add(target.hex);

        GraphPath<Hex> hexPath;
        Array<Path> paths = new Array<>();
        Hex start = thisForce.hex;
        for (Hex h : destinations) {
            hexPath = thisForce.playScreen.getHexGraph().findPath(start, h);
            Hex st = null;
            Hex en;
            for (Hex hx : hexPath) {
                en = hx;
                if (st != null) {
                    paths.add(thisForce.playScreen.getHexGraph().getPath(st, en));
                }
                st = hx;
            }
            start = st;
        }
        thisForce.forcePath = paths;
        isSet = true;
    }

    @Override
    public void visualize(Force force) {

        if (force != null) {
            Order o = force.visualOrders.last();
            if (o instanceof MoveOrder) {
                if (additiveOrder) {
                    if (!o.irrevocable) {
                        force.visualOrders.removeOrder(o);
                        o.revoked = true;
                    }
                } else {
                    force.visualOrders.removeMoveOrders();
                }
            }
            super.visualize(force);
        }
    }

    @Override
    public void receive(Force force) {
        if (!revoked && !additiveOrder && force.actualOrders.first() instanceof MoveOrder) {
            force.actualOrders.removeMoveOrders();
        }
//        System.out.println(this + " Follow Order received with target " + target);
        super.receive(force);
    }


    @Override
    public void set(Force force) {
        if (force == null) {
            return;
        }
        thisForce = force;
        setDestination();
    }


    @Override
    public String toString() {
        String name = target == null? "" : target.getName();
        return super.toString() + " follow " + name;
    }


    @Override
    public boolean execute(Force force, float delta) {
        if (force == null) {
            return false;
        }
//TODO

        if (force.hex != target.hex && force.forcePath != null && !force.forcePath.isEmpty() || !force.tail.isEmpty()) {
            force.move(delta);
        }
        setDestination();
        return true;
    }


    @Override
    public void cancel(Force f) {
        Force.sendOrder(f, new RemoveDestinationsOrder(this), 50);
    }

    public SelectBox<? extends Force> createSelectBox(Force frc, Hex hex) {
        SelectBox<Force> selectForce = new SelectBox<>(frc.playScreen.game.getSkin());
        Array<Force> allies = frc.getAllies(hex);
        selectForce.setItems(allies);
        return selectForce;
    }
}
