package mijnlieff.pionnen;

public class Pusher extends Pion{
    @Override
    public boolean bedreig(Positie positie) {
        return pos.isPusherbedreiging(positie);
    }
}
