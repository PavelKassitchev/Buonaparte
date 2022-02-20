package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;

public abstract class CancellationOrder extends Order {

    @Override
    public void visualize(Force force) {

    }

    @Override
    public void set(Force force) {

    }

    @Override
    public boolean isExecutable(Force force) {
        return true;
    }

    @Override
    public boolean execute(Force force, float delta) {
        return false;
    }

    @Override
    public boolean execute(Array<Force> forces) {
        return false;
    }

    @Override
    public void cancel(Force f) {

    }
}
