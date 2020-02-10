package mijnlieff.bord;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mijnlieff.Listener;

public class Veld extends StackPane implements EventHandler<MouseEvent>, Listener {
    private final Rectangle achtergrond;
    private ImageView imageView;

    private BordModel model;

    private int kolom;
    private int rij;

    //De constructor zorgt voor de correcte grote van de velden en maakt een imageview aan
    public Veld(BordModel model, int grootte, int rij, int kolom) {
        this.model = model;
        model.registerListener(this);
        this.rij = rij;
        this.kolom = kolom;

        //een vierkant doet dienst als achtergrond
        this.achtergrond = new Rectangle(grootte, grootte);
        getChildren().add(achtergrond);
        achtergrond.setOnMouseClicked(this);

        if ((rij + kolom) % 2 == 0) {
            achtergrond.setFill(Color.FLORALWHITE);
        } else {
            achtergrond.setFill(Color.DIMGREY);
        }


        //De imageview wordt aangemaakt, deze zal de pion weergeven
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(grootte * 0.8);
        imageView.setMouseTransparent(true); // mouse events are handled by the rectangle

        getChildren().add(imageView);

        setAppearance();
        setBedreiging();
    }

    //Dit wordt opgeroepen in de constructor en als het model verandert, de pion worden hiermee geplaatst
    private void setAppearance () {
        String pion = model.getPion(rij,kolom);

        //System.out.println(rij + ", " + kolom + ": " + model.getPion(rij,kolom));
        if (pion.equals("")) {
            imageView.setImage(null);
        } else {
            imageView.setImage(new Image("mijnlieff/pionnen/pionAfbeeldingen/" + pion + ".png"));
        }
    }

    private void setBedreiging () {
        if ((rij + kolom) % 2 == 0) {
            achtergrond.setFill(model.isBedreigd(rij, kolom) ? Color.PINK : Color.FLORALWHITE);
        } else {
            achtergrond.setFill(model.isBedreigd(rij, kolom) ? Color.PALEVIOLETRED : Color.DIMGREY);
        }
    }

    @Override
    public void handle(MouseEvent event) {
        if (model.getBedreig()) {
            model.plaatsPion(rij, kolom);
        }
    }

    @Override
    public void modelHasChanged() {
        setAppearance();
        setBedreiging();
    }
}
