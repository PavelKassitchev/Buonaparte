package by.pavka.march.military;

import static by.pavka.march.characteristic.Stock.AMMO_NORMAL;

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
        u1.strength.ammoConsumption = 0.5;
        u1.strength.foodConsumption = 1;
        u1.strength.capacity = 25;
        f.attach(u1);
        Unit u2 = new Unit();
        u2.name = "u2";
        u2.speed = 12;
        u2.spirit = new Spirit(0, 0, 0);
        u2.strength = new Strength();
        u2.strength.food = 30;
        u2.strength.ammo = 0;
        u2.strength.ammoConsumption = 0.7;
        u2.strength.foodConsumption = 2;
        u2.strength.capacity = 25;
        formation.attach(u2);
        formation.attach(f);
        WagonTrain wagon = new WagonTrain();
        wagon.name = "wagon";
        wagon.speed = 8;
        wagon.spirit = new Spirit(0, 0, 0);
        wagon.strength = new Strength();
        wagon.strength.food = 30;
        wagon.strength.ammo = 20;
        wagon.strength.ammoConsumption = 0.01;
        wagon.strength.foodConsumption = 0.1;
        wagon.strength.capacity = 60;
        WagonTrain wagon1 = new WagonTrain();
        wagon.name = "wagon1";
        wagon1.speed = 8;
        wagon1.spirit = new Spirit(0, 0, 0);
        wagon1.strength = new Strength();
        wagon1.strength.food = 5;
        wagon1.strength.ammo = 20;
        wagon1.strength.ammoConsumption = 0.01;
        wagon1.strength.foodConsumption = 0.1;
        wagon1.strength.capacity = 60;
        formation.attach(wagon1);
        f.attach(wagon);
        System.out.println("Wagon food need = " + wagon.findFoodNeed() + ", ammo need = " + wagon.findAmmoNeed());
        System.out.println("Total loaded: " + formation.strength.food + ", " + formation.strength.ammo);
        Stock stock = formation.emptyStock();
        System.out.println("Total unloaded: " + stock.food + " " + stock.ammo + ", " +
                "U2 stock: " + u2.strength.food + " " + u2.strength.ammo + ", U1 stock: " + u1.strength.food + " " + u1.strength.ammo
        + ", f stock: " + f.strength.food + " " + f.strength.ammo + ", wagon food " + wagon.strength.food + ", " + wagon1.strength.food);
        System.out.println("Ammo need: " + formation.findAmmoNeed() + " Wagon need = " + wagon.findAmmoNeed() + ", Wagon1 need = " + wagon1.findAmmoNeed());
        System.out.println("Speed: " + formation.speed);
        stock = formation.flattenEmptiedStock(stock, AMMO_NORMAL);
        System.out.println("FLATTED: " + formation.strength.food + ", " + formation.strength.ammo);
        System.out.println("FLATTED1: " + f.strength.food + ", " + f.strength.ammo);
        System.out.println("FLATTED U2: " + u2.strength.food + ", " + u2.strength.ammo);
        System.out.println("FLATTED WAGON: " + wagon.strength.food + ", " + wagon.strength.ammo);
        System.out.println("FLATTED U1: " + u1.strength.food + ", " + u1.strength.ammo);
        System.out.println("FLATTED WAGON1: " + wagon1.strength.food + ", " + wagon1.strength.ammo);
        System.out.println("F: foodConsumption " + f.strength.foodConsumption + ", ammoConsumption " + f.strength.ammoConsumption +
                ". Wagon food need = " + wagon.findFoodNeed() + ", ammo need = " + wagon.findAmmoNeed());

    }
}
