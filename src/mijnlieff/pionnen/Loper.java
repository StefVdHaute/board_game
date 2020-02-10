package mijnlieff.pionnen;

public class Loper extends Pion{
    @Override
    public boolean bedreig(Positie positie) {
        return pos.isLoperbedreiging(positie);
    }
}
