package mijnlieff.pionnen;

public class Toren extends Pion{
    @Override
    public boolean bedreig(Positie positie) {
        return pos.isTorenbedreiging(positie);
    }
}
