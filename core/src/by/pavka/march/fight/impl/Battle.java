package by.pavka.march.fight.impl;

import static by.pavka.march.PlayScreen.HOURS_IN_SECOND;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;
import by.pavka.march.fight.Fight;
import by.pavka.march.fight.FightResult;
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

    public Battle(Hex hex, Nation nation) {
        this.hex = hex;
        battleConfigurator = new BattleConfigurator(this, hex, nation);
        firstNation = nation;
        for (Force force : hex.getForces()) {
            if (force.nation == nation) {
                first.add(force);
                firstSoldiers += force.strength.soldiers();
                firstLength += force.strength.length;
                firstFire += force.strength.fire;
                firstCharge += force.strength.charge;
                firstRecon += force.strength.recon;
            } else {
                second.add(force);
                secondSoldiers += force.strength.soldiers();
                secondLength += force.strength.length;
                secondFire += force.strength.fire;
                secondCharge += force.strength.charge;
                secondRecon = force.strength.recon;
            }
            force.fight = this;
        }
        widthFactor = battlefieldWidth / (firstLength + secondLength);
        System.out.println("Initial first: " + first.get(0).strength.soldiers() +
                " Initial second:" + second.get(0).strength.soldiers());

    }

    @Override
    public void affect(Force force, float delta) {
        Strength s;
        if (force.nation == firstNation) {

            double fireFactor = widthFactor * secondFire * HOURS_IN_SECOND * delta
                    * FIRE_1_HOUR_EQUIVALENT / firstSoldiers;
            System.out.println(force.getName() + " delta " + delta + " underFire " + fireFactor);

            s = force.sufferLosses(fireFactor);
            firstSoldiers -= s.soldiers();
            firstLength -= s.length;
            firstFire -= s.fire;
            firstCharge -= s.charge;
            firstRecon -= s.recon;
            System.out.println(force.getName() + "killed " + s.soldiers() + " morale " + force.spirit.morale);

            double chargeFactor = widthFactor * secondCharge * HOURS_IN_SECOND * delta
                    * CHARGE_1_HOUR_EQUIVALENT / firstSoldiers * (0.5 + Math.random());
            System.out.println(force.getName() + " underCharge " + chargeFactor);
            force.changeSpirit(0, -chargeFactor * Math.pow((2 - force.spirit.morale), 2), 0);

            if (force.isDisordered) {
                double pursuitFactor = widthFactor * secondRecon * HOURS_IN_SECOND * delta
                        * PURSUIT_1_HOUR_EQUIVALENT / firstSoldiers * (0.5 + Math.random());
                double d = secondRecon / firstSoldiers * force.strength.soldiers() - force.strength.recon;
                if (d > 0) {
                    System.out.println("PursuitFactor = " + d * PURSUIT_1_EQUIVALENT / force.strength.soldiers());
//                    s = force.sufferLosses(d * pursuitFactor);
                    s = force.sufferLosses(d * PURSUIT_1_EQUIVALENT / force.strength.soldiers());
                    firstSoldiers -= s.soldiers();
                    firstLength -= s.length;
                    firstFire -= s.fire;
                    firstCharge -= s.charge;
                    firstRecon -= s.recon;
                }
                firstSoldiers -= force.strength.soldiers();
                firstLength -= force.strength.length;
                firstFire -= force.strength.fire;
                firstCharge -= force.strength.charge;
                firstRecon -= force.strength.recon;
            }
        } else {

            double fireFactor = widthFactor * firstFire * HOURS_IN_SECOND * delta * FIRE_1_HOUR_EQUIVALENT / secondSoldiers;
            System.out.println(force.getName() + " delta " + delta + " underFire " + fireFactor);
            s = force.sufferLosses(fireFactor);
            secondSoldiers -= s.soldiers();
            secondLength -= s.length;
            secondFire -= s.fire;
            secondCharge -= s.charge;
            secondRecon -= s.recon;
            System.out.println(force.getName() + "killed " + s.soldiers() + " morale " + force.spirit.morale);

            double chargeFactor = widthFactor * firstCharge * HOURS_IN_SECOND * delta
                    * CHARGE_1_HOUR_EQUIVALENT / secondSoldiers * (0.5 + Math.random());
            System.out.println(force.getName() + " underCharge " + chargeFactor);
            force.changeSpirit(0, -chargeFactor * Math.pow((2 - force.spirit.morale), 2), 0);

            if (force.isDisordered) {
                double pursuitFactor = widthFactor * firstRecon * HOURS_IN_SECOND * delta
                        * PURSUIT_1_HOUR_EQUIVALENT / secondSoldiers * (0.5 + Math.random());
                double d = firstRecon / secondSoldiers * force.strength.soldiers() - force.strength.recon;
                if (d > 0) {
//                    s = force.sufferLosses(d * pursuitFactor);
                    s = force.sufferLosses(d * PURSUIT_1_EQUIVALENT / force.strength.soldiers());
                    secondSoldiers -= s.soldiers();
                    secondLength -= s.length;
                    secondFire -= s.fire;
                    secondCharge -= s.charge;
                    secondRecon -= s.recon;
                }
                secondSoldiers -= force.strength.soldiers();
                secondLength -= force.strength.length;
                secondFire -= force.strength.fire;
                secondCharge -= force.strength.charge;
                secondRecon -= force.strength.recon;
            }
        }
        System.out.println(force.getName() + " soldiers " + force.strength.soldiers() +
                " morale " + force.spirit.morale);

        int c = checkOld();
        if (c != 0) {
            for (Force frc : first) {
                frc.fight = null;
            }
            for (Force frc : second) {
                frc.fight = null;
            }
            if (c == 1) {
                System.out.println("First is winner! Duration " + duration + " first " + first.get(0).strength.soldiers()
                        + " " + first.get(0).spirit.morale + " second " + second.get(0).strength.soldiers() + " " +
                        second.get(0).spirit.morale);
                return;
            } else {
                System.out.println("Second is winner! Duration " + duration + " first " + first.get(0).strength.soldiers()
                        + " " + first.get(0).spirit.morale + " second " + second.get(0).strength.soldiers() + " " +
                        second.get(0).spirit.morale);
                return;
            }
        }
    }


    @Override
    public FightResult result() {
        return null;
    }

    @Override
    public void restoreForce(Force force) {
//        if (force.nation == firstNation) {
//            firstSoldiers += force.strength.soldiers();
//            firstLength += force.strength.length;
//            firstFire += force.strength.fire;
//            firstCharge += force.strength.charge;
//            firstRecon += force.strength.recon;
//        } else {
//            secondSoldiers += force.strength.soldiers();
//            secondLength += force.strength.length;
//            secondFire += force.strength.fire;
//            secondCharge += force.strength.charge;
//            secondRecon += force.strength.recon;
//        }
    }

    @Override
    public void strike(Force force, float delta) {
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
//            for (Force frc : battleConfigurator.first) {
//                frc.fight = null;
//            }
//            for (Force frc : battleConfigurator.second) {
//                frc.fight = null;
//            }
            if (c == 1) {
                System.out.println("First is winner! Duration " + duration + " first " + battleConfigurator.first.get(0).strength.soldiers()
                        + " " + battleConfigurator.first.get(0).spirit.morale + " second " + battleConfigurator.second.get(0).strength.soldiers() + " " +
                        battleConfigurator.second.get(0).spirit.morale);
//                return;
            } else {
                System.out.println("Second is winner! Duration " + duration + " first " + battleConfigurator.first.get(0).strength.soldiers()
                        + " " + battleConfigurator.first.get(0).spirit.morale + " second " + battleConfigurator.second.get(0).strength.soldiers() + " " +
                        battleConfigurator.second.get(0).spirit.morale);
//                return;
            }
            for (Force frc : battleConfigurator.first) {
                frc.fight = null;
            }
            for (Force frc : battleConfigurator.second) {
                frc.fight = null;
            }
            return;
        }
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

    private double widthFactor() {
        return battlefieldWidth / (firstLength + secondLength);
    }

    public void addDuration(float delta) {
        duration += delta;
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

    public int checkOld() {
        for (Force f : first) {
            if (!f.isDisordered) {
                break;
            }
            return -1;
        }
        for (Force f : second) {
            if (!f.isDisordered) {
                break;
            }
            return 1;
        }
        return 0;
    }
}
