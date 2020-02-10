package mijnlieff.houders;

import javafx.scene.layout.GridPane;

public class Pionhouder extends GridPane {
    //Een gridpane van Houders wordt gemaakt
    public Pionhouder(int breedte, int lengte, String kleur, PionhouderModel model) {
        for (int i=0; i < breedte; i++) {
            for (int j=0; j < lengte; j++) {
                add(new Houder(model, kleur, i, j), i, j);
            }
        }
    }
}
