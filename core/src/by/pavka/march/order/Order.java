package by.pavka.march.order;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.military.Force;

public interface Order {
    boolean execute(Force force);
    boolean execute(Array<Force> forces);
}
