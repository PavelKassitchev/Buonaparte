package by.pavka.march.military;

import com.badlogic.gdx.utils.Array;

import by.pavka.march.characteristic.Spirit;
import by.pavka.march.characteristic.Stock;
import by.pavka.march.characteristic.Strength;
import by.pavka.march.configuration.FormationValidator;

public class MainTest {
    public static void main(String[] args) {
        Formation formation = new Formation();
        FormationValidator validator = new FormationValidator() {

            @Override
            public boolean canAttch(Force force) {
                return true;
            }
        };
        formation.name = "formation";
        formation.validator = validator;
        formation.subForces = new Array<Force>();
        formation.strength = new Strength();
        formation.spirit = new Spirit(0, 0, 0);

        Formation f = new Formation();
        f.validator = new FormationValidator() {
            @Override
            public boolean canAttch(Force force) {
                return true;
            }
        };
        f.name = "f";
        f.subForces = new Array<Force>();
        f.strength = new Strength();
        f.spirit = new Spirit(0, 0, 0);
        Unit u1 = new Unit();
        u1.name = "u1";
        u1.speed = 10;
        u1.strength = new Strength();
        u1.strength.food = 20;
        u1.strength.ammo = 10;
        u1.spirit = new Spirit(0, 0, 0);
        f.attach(u1);
        Unit u2 = new Unit();
        u2.name = "u2";
        u2.speed = 12;
        u2.spirit = new Spirit(0, 0, 0);
        u2.strength = new Strength();
        u2.strength.food = 30;
        u2.strength.ammo = 0;
        formation.attach(u2);
        formation.attach(f);
        //Stock stock = formation.emptyStock();
        Stock stock = f.emptyStock();
        System.out.println("Total unloaded: " + stock.food + " " + stock.ammo + ", " +
                "U2 stock: " + u2.strength.food + " " + u2.strength.ammo + ", U1 stock: " + u1.strength.food + " " + u1.strength.ammo
        + ", f stock: " + f.strength.food + " " + f.strength.ammo);

    }
}
