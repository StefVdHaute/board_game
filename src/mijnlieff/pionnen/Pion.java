package mijnlieff.pionnen;


public abstract class Pion {
    protected Positie pos;

    public abstract boolean bedreig(Positie pos);

    public Positie getPositie() {
        return pos;
    }

    public void setPositie(Positie pos) {
        this.pos = pos;
    }
}
