package by.pavka.march.military;

import com.badlogic.gdx.utils.Array;

public class HQ extends Unit {
    Array<General> generals;
    HQType type;
    Formation formation;

    public HQ(Unitable type, Formation formation) {
        super(type);
        this.type = (HQType)type;
        this.formation = formation;
    }

    public HQ(Unitable type) {
        super(type);
//        this.type = (HQType)type;
    }

    @Override
    public String image() {
        return unitType.image();
    }

    @Override
    public boolean isDetachable() {
        return false;
    }
}
