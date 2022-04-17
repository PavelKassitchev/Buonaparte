package by.pavka.march.fight.impl;

import static by.pavka.march.PlayScreen.HOURS_IN_SECOND;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.Nation;
import by.pavka.march.fight.Fight;
import by.pavka.march.map.Direction;
import by.pavka.march.map.Hex;
import by.pavka.march.military.Force;

public class BattleConfigurator {
    public static final int FIRE_1_HOUR_EQUIVALENT = 80;
    public static final int CHARGE_1_HOUR_EQUIVALENT = 220;
    public static final int PURSUIT_1_EQUIVALENT = 200;
    public float duration;
    public Direction default2RetreatDirection;

    Fight fight;
    double battlefieldWidth = 2000;
    Nation firstNation;
    Array<Force> first = new Array<>();
    Array<Force> second = new Array<>();
    int firstSoldiers;
    int secondSoldiers;
    final int initialFirst;
    final int initialSecond;
    double firstLength;
    double secondLength;
    double firstFire;
    double secondFire;
    double firstCharge;
    double secondCharge;
    double firstRecon;
    double secondRecon;
    double widthFactor;

    int firstDisordered;
    int secondDisordered;
    double firstDisorderedFire;
    double secondDisorderedFire;

    public BattleConfigurator(Fight fight, Hex hex, Nation nation, Direction direction) {
        this.fight = fight;
        firstNation = nation;
        default2RetreatDirection = direction;
        for (Force force : hex.getForces()) {
            force.prepareForFight();
            if (force.nation == firstNation) {
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
            force.fight = fight;
        }
        initialFirst = firstSoldiers;
        initialSecond = secondSoldiers;
        widthFactor = battlefieldWidth / (firstLength + secondLength);
        System.out.println("Initial first: " + first.get(0).detailedInfo() +
                " Initial second:" + second.get(0).detailedInfo());
    }

    public double getFireFactor(Force force, float delta) {
        if (force.nation == firstNation) {
//            System.out.println("Second Fire = " + secondFire + " First Soldiers " + firstSoldiers + " First Disordered " +
//                    firstDisordered + " Totally " + force.strength.soldiers() + " First fire " + firstFire + " Disordered " +
//                    firstDisorderedFire + " Total Fire " + force.strength.fire);
            return widthFactor * secondFire * HOURS_IN_SECOND * delta
                    * FIRE_1_HOUR_EQUIVALENT / firstSoldiers;
        } else {
//            System.out.println("First Fire = " + firstFire + " Second Soldiers " + secondSoldiers + " Second Disordered " +
//                    secondDisordered + " Totally " + force.strength.soldiers() + " Second fire " + secondFire + " Disordered "
//            + secondDisorderedFire + " Total Fire " + force.strength.fire);
            return widthFactor * firstFire * HOURS_IN_SECOND * delta
                    * FIRE_1_HOUR_EQUIVALENT / secondSoldiers;
        }
    }

    public void affect(Force force, Strength s, boolean onPursuit) {
        if (!onPursuit) {
            affect(force, s);
        }
    }

    public void affect(Force force, Strength s) {
        if (force.nation == firstNation) {
//            System.out.println("Affecting first while " + firstFire);
            firstSoldiers -= s.soldiers();
            firstLength -= s.length;
            firstFire -= s.fire;
//            System.out.println("First fire now is " + firstFire);
            if (Double.isNaN(firstFire) || firstFire < 0) {
                System.out.println("NAN or fFIRE " + firstFire + " " + force.getName() + " in force " +
                        force.strength.soldiers() + " " + s);
                System.exit(1);
            }
            firstCharge -= s.charge;
            firstRecon -= s.recon;
        } else {
//            System.out.println("Affecting second while " + secondFire);
            secondSoldiers -= s.soldiers();
            secondLength -= s.length;
            secondFire -= s.fire;
//            System.out.println("Second fire now is " + secondFire);
            if (Double.isNaN(secondFire) || secondFire < 0) {
                System.out.println("NAN or sFIRE " + secondFire + " " + force.getName() + " in force " +
                        force.strength.soldiers() + " "  + s);
                System.exit(1);
            }
            secondCharge -= s.charge;
            secondRecon -= s.recon;
        }
    }

    public double getChargeFactor(Force force, float delta) {
        if (force.nation == firstNation) {
            return widthFactor * secondCharge * HOURS_IN_SECOND * delta
                    * CHARGE_1_HOUR_EQUIVALENT / firstSoldiers * (0.5 + Math.random());
        } else {
            return widthFactor * firstCharge * HOURS_IN_SECOND * delta
                    * CHARGE_1_HOUR_EQUIVALENT / secondSoldiers * (0.5 + Math.random());
        }
    }

    public double getPostPursuitFactor(Force force) {
        double factor;
        if (force.nation == firstNation) {
            factor = (0.1 + secondCharge / initialFirst - force.strength.charge / force.strength.soldiers())
                    * FIRE_1_HOUR_EQUIVALENT / force.strength.soldiers();
//            return secondCharge
//                    * FIRE_1_HOUR_EQUIVALENT / initialFirst;
        } else {
            factor = (0.1 + firstCharge / initialSecond - force.strength.charge / force.strength.soldiers())
                    * FIRE_1_HOUR_EQUIVALENT / force.strength.soldiers();
//            return firstCharge
//                    * FIRE_1_HOUR_EQUIVALENT / initialSecond;
        }
        System.out.println("PURSUIT " + factor);
        return factor;
    }

    public void exclude(Force force) {
        if (force.nation == firstNation) {
            firstSoldiers -= force.strength.soldiers();
            firstDisordered += force.strength.soldiers();
            firstLength -= force.strength.length;
            firstFire -= force.strength.fire;
            firstDisorderedFire += force.strength.fire;
//            System.out.println("Excluding first " + firstFire);
            if (Double.isNaN(firstFire) || firstFire < 0) {
                System.out.println("NAN or FFIRE " + firstFire + " " + force.getName() + " in fire " + force.strength.fire +
                        " First Soldiers " + firstSoldiers + " First Disordered " + firstDisordered);
//                System.exit(1);
            }
            firstCharge -= force.strength.charge;
            firstRecon -= force.strength.recon;
        } else {
            secondSoldiers -= force.strength.soldiers();
            secondDisordered += force.strength.soldiers();
            secondLength -= force.strength.length;
            secondFire -= force.strength.fire;
            secondDisorderedFire += force.strength.fire;
//            System.out.println("Excluding second " + secondFire);
            if (Double.isNaN(secondFire) || secondFire < 0) {
                System.out.println("NAN or SFIRE " + secondFire + " " + force.getName() + " in fire " + force.strength.fire +
                        " Second Soldiers " + secondSoldiers + " Second Disordered " + secondDisordered);
//                System.exit(1);
            }
            secondCharge -= force.strength.charge;
            secondRecon -= force.strength.recon;
        }
    }

    public void pursue(Force force) {
        double pursuitFactor;
        if (force.nation == firstNation) {
            pursuitFactor = secondRecon / firstSoldiers * force.strength.soldiers() - force.strength.recon;
        } else {
            pursuitFactor = firstRecon / secondSoldiers * force.strength.soldiers() - force.strength.recon;
        }
        System.out.println(force.getName() + " under pursuit " + pursuitFactor + " current fire " + force.strength.fire);
        if (pursuitFactor > 0) {
            Strength s = force.getFired(pursuitFactor * PURSUIT_1_EQUIVALENT / force.strength.soldiers(), true);
            System.out.println(force.getName() + " Caught in Pursuit " + s.soldiers() + " left " +
                    force.strength.soldiers() + " left fire " + force.strength.fire);
            affect(force, s);
        }
    }

    public void include(Force force) {
        if (force.nation == firstNation) {
            firstSoldiers += force.strength.soldiers();
            firstLength += force.strength.length;
            firstFire += force.strength.fire;
            firstCharge += force.strength.charge;
            firstRecon += force.strength.recon;
        } else {
            secondSoldiers += force.strength.soldiers();
            secondLength += force.strength.length;
            secondFire += force.strength.fire;
            secondCharge += force.strength.charge;
            secondRecon += force.strength.recon;
        }
    }

}
