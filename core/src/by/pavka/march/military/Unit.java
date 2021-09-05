package by.pavka.march.military;

public class Unit extends Force implements Fighter {
    UnitType unitType;

    @Override
    public int getFightRank() {
        return unitType.fightRank();
    }
}
