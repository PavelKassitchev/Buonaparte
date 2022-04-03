package by.pavka.march.fight;

import by.pavka.march.military.Force;

public interface Fight {
    void affect(Force force, float delta);
    FightResult result();

    void restoreForce(Force force);

    void strike(Force force, float delta);

    void exclude(Force unit);

    void getPursued(Force force);

    void include(Force force);
}
