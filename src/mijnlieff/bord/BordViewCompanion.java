package mijnlieff.bord;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import mijnlieff.StapVertaler;
import mijnlieff.houders.Pionhouder;

public class BordViewCompanion {
    public Label naamWit;
    public AnchorPane pionnenWit;
    public Label puntenWit;

    public Label naamZwart;
    public AnchorPane pionnenZwart;
    public Label puntenZwart;

    public void setNamen(String wit, String zwart){
        naamWit.setText(wit);
        naamZwart.setText(zwart);
    }

    public void setPionhouders(Pionhouder wit, Pionhouder zwart){
        pionnenWit.getChildren().add(wit);
        pionnenZwart.getChildren().add(zwart);
    }

    public void cancel() {
        StapVertaler.getServer().stuurCommando("Q");
        System.exit(0);
    }

    public void updatePunten(int ptWit, int ptZwart) {
        puntenWit.setText(ptWit + "");
        puntenZwart.setText(ptZwart + "");
    }
}
