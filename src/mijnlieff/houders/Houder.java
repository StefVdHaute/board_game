package mijnlieff.houders;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import mijnlieff.Listener;//todo fix deze import mss das lelijk

public class Houder extends StackPane implements EventHandler<MouseEvent>, Listener {
    private final Rectangle achtergrond;
    private final String kleur;
    private ImageView imageView;

    private PionhouderModel model;

    private int kolom;
    private int rij;
    private String pion;

    //De constructor zorgt voor de correcte grote van de velden en maakt een imageview aan
    public Houder(PionhouderModel model, String kleur, int kolom, int rij) {
        this.model = model;
        model.registerListener(this);
        this.kleur = kleur;
        this.kolom = kolom;
        this.rij = rij;

        //een vierkant doet dienst als achtergrond
        this.achtergrond = new Rectangle(100, 100);
        getChildren().add(achtergrond);
        achtergrond.setFill(Color.DARKGREY);
        achtergrond.setOnMouseClicked(this);

        //De imageview wordt aangemaakt, deze zal de pion weergeven
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(85);
        imageView.setMouseTransparent(true); // mouse events are handled by the rectangle

        getChildren().add(imageView);

        setAppearance();
    }

    //Dit wordt opgeroepen in de constructor en als het model verandert,
    // de pion wordt hiermee weggehaald
    private void setAppearance () {
        pion = model.getPion(rij,kolom);

        if (pion.equals("")) {
            imageView.setImage(null);
        } else {
            imageView.setImage(new Image("mijnlieff/pionnen/pionAfbeeldingen/" + kleur + "-" + pion + ".png"));
        }
    }

    //TODO: muisevent maken voor de juiste pion te vinden
    @Override
    public void handle(MouseEvent event) {
        if (! pion.equals("")) {
            model.setTeGebruiken(pion);
        }
    }

    @Override
    public void modelHasChanged() {
        setAppearance();
    }
}
