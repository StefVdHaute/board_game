package mijnlieff.pionnen;

public class Puller extends Pion {
    @Override
    public boolean bedreig(Positie positie) {
        return pos.isPullerbedreiging(positie);
    }
}
