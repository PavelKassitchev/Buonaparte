package by.pavka.march.fight.impl;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;
import by.pavka.march.fight.Fight;
import by.pavka.march.fight.FightResult;
import by.pavka.march.map.Direction;
import by.pavka.march.map.Hex;
import by.pavka.march.military.Force;

public class Battle implements Fight {
    public static final int FIRE_1_HOUR_EQUIVALENT = 80;
    public static final int CHARGE_1_HOUR_EQUIVALENT = 220;
    public static final int PURSUIT_1_HOUR_EQUIVALENT = 100;
    public static final int PURSUIT_1_EQUIVALENT = 200;

    float duration;

    Hex hex;
    double battlefieldWidth = 2000;
    Nation firstNation;
    Array<Force> first = new Array<>();
    Array<Force> second = new Array<>();
    int firstSoldiers;
    int secondSoldiers;
    double firstLength;
    double secondLength;
    double firstFire;
    double secondFire;
    double firstCharge;
    double secondCharge;
    double firstRecon;
    double secondRecon;
    double widthFactor;

    BattleConfigurator battleConfigurator;

    public Battle(Hex hex, Nation nation, Direction direction) {
        this.hex = hex;
        battleConfigurator = new BattleConfigurator(this, hex, nation, direction);
    }


    @Override
    public FightResult result() {
        return null;
    }

    @Override
    public void strike(Force force, float delta) {
        addDuration(delta);
        double fireFactor = battleConfigurator.getFireFactor(force, delta);
        Strength s = force.getFired(fireFactor);
        battleConfigurator.affect(force, s);
        System.out.println(force.getName() + " killed " + s.soldiers() + " morale " + force.spirit.morale);

        double chargeFactor = battleConfigurator.getChargeFactor(force, delta);
        force.getCharged(-chargeFactor * Math.pow((2 - force.spirit.morale), 2));
        System.out.println(force.getName() + " charged by " + chargeFactor + " soldiers " + force.strength.soldiers() +
                " morale " + force.spirit.morale);

        int c = checkResult();
        if (c != 0) {
            if (c == 1) {
                if (battleConfigurator.firstCharge > battleConfigurator.secondCharge) {
//                    double pursuitFactor = battleConfigurator.getPostPursuitFactor(force);
                    for (Force f : battleConfigurator.second) {
                        double pursuitFactor = battleConfigurator.getPostPursuitFactor(f);

                        Strength str = f.getFired(pursuitFactor, true);
                        battleConfigurator.affect(f, str, f.isDisordered);
                        f.restoreMorale(delta);
                        f.retreat(get2RetreatDirection());
                    }
                }
                System.out.println("First is winner! Duration " + battleConfigurator.duration + " first "
                        + battleConfigurator.first.get(0).strength.soldiers()
                        + " " + battleConfigurator.first.get(0).spirit.morale + " " +
                        battleConfigurator.first.get(0).strength.charge + " second " +
                        battleConfigurator.second.get(0).strength.soldiers() + " " +
                        battleConfigurator.second.get(0).spirit.morale + " " +
                        battleConfigurator.second.get(0).strength.charge + " second disordered "
                        + battleConfigurator.secondDisordered);

            } else {
                if (battleConfigurator.secondCharge > battleConfigurator.firstCharge) {
//                    double pursuitFactor = battleConfigurator.getPostPursuitFactor(force);
                    for (Force f : battleConfigurator.first) {
                        double pursuitFactor = battleConfigurator.getPostPursuitFactor(f);

                        Strength str = f.getFired(pursuitFactor, true);
                        battleConfigurator.affect(f, str, f.isDisordered);
                        f.restoreMorale(delta);
                        f.retreat(get1RetreatDirection());
                    }
                }
                System.out.println("Second is winner! Duration " + battleConfigurator.duration + " first "
                        + battleConfigurator.first.get(0).strength.soldiers()
                        + " " + battleConfigurator.first.get(0).spirit.morale + " " +
                        battleConfigurator.first.get(0).strength.charge + " second " +
                        battleConfigurator.second.get(0).strength.soldiers() + " " +
                        battleConfigurator.second.get(0).spirit.morale + " " +
                        battleConfigurator.second.get(0).strength.charge + " first disordered "
                        + battleConfigurator.firstDisordered);

            }
            for (Force frc : battleConfigurator.first) {
                //TODO de-comment the line below
//                frc.sendReport("");
                frc.fight = null;
            }
            for (Force frc : battleConfigurator.second) {
                //TODO de-comment the line below
//                frc.sendReport("");
                frc.fight = null;
            }
//            System.exit(1);
        }
    }

    private Direction get2RetreatDirection() {
        return battleConfigurator.default2RetreatDirection;
    }

    private Direction get1RetreatDirection() {
        return battleConfigurator.default2RetreatDirection.opposite();
    }

    @Override
    public void exclude(Force force) {
        battleConfigurator.exclude(force);
    }

    @Override
    public void getPursued(Force force) {
        battleConfigurator.pursue(force);
    }

    @Override
    public void include(Force force) {
        battleConfigurator.include(force);
    }

//    private double widthFactor() {
//        return battlefieldWidth / (firstLength + secondLength);
//    }

    public void addDuration(float delta) {
        battleConfigurator.duration += delta;
    }

    public int checkResult() {
        for (Force f : battleConfigurator.first) {
            if (!f.isDisordered) {
                break;
            }
            return -1;
        }
        for (Force f : battleConfigurator.second) {
            if (!f.isDisordered) {
                break;
            }
            return 1;
        }
        return 0;
    }
}
