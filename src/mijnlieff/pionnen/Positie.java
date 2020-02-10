package mijnlieff.pionnen;

public class Positie {
    private int rij;
    private int kol;

    public Positie(int rij, int kol) {
        this.rij = rij;
        this.kol = kol;
    }

    public int getRij() {
        return rij;

    }

    public int getKolom() {
        return kol;
    }

    public boolean isPusherbedreiging(Positie pos){
        int formule = (rij - pos.rij) * (rij - pos.rij) + (kol - pos.kol) * (kol - pos.kol);
        return formule == 0 || formule == 1 || formule == 2;
    }

    public boolean isPullerbedreiging(Positie pos){
        return ! isPusherbedreiging(pos) || (rij == pos.rij && kol == pos.kol);
    }

    public boolean isLoperbedreiging(Positie pos) {
        return rij + kol != pos.rij + pos.kol
                && rij - kol != pos.rij - pos.kol;
    }

    public boolean isTorenbedreiging(Positie pos) {
        return rij != pos.rij && kol != pos.kol;
    }
}
