package by.pavka.march.military;

public class  UnseparableUnit extends Unit {
    @Override
    public boolean detach() {
        return false;
    }
}
