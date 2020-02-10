package mijnlieff.bord;

import javafx.scene.layout.GridPane;
import mijnlieff.bord.bordConfiguratie.BordConfigCompanion;

public class Bord extends GridPane{
    //Een gridpane van Fields wordt gemaakt
    public Bord(BordModel model) {
        int grootte = BordConfigCompanion.getGrootte();

        this.setGridLinesVisible(true);

        for (int kolom=0; kolom < 11; kolom++) {
            for (int rij=0; rij < 11; rij++) {
                //kan ook "*" zijn in dat geval moet er op die plaats geen veld komen
                if(model.getPion(rij, kolom).equals("")) {
                    //kolom rij omwissellen van plaats bij het toevoegen (gridpane == [kolom][rij] ipv [rij][kolom]?)
                    add(new Veld(model, grootte, rij, kolom), kolom, rij);
                }
            }
        }
    }
}
