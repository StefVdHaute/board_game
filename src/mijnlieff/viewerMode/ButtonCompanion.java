package mijnlieff.viewerMode;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;

import mijnlieff.StapVertaler;
import mijnlieff.bord.BordModel;
import mijnlieff.server.tasks.GetZetTask;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ButtonCompanion extends StapVertaler {
    //Sla viewerMode op als variabelen in companion
    public Button start;
    public Button vorige;
    public Button volgende;
    public Button laatste;

    private boolean ready = true;

    //De companion moet methoden van het model oproepen om het bord te veranderen
    private BordModel model;

    //Een lijst om alle commando's op te slaan + pointer
    private int positie = 0;
    private ArrayList<String> stappen = new ArrayList<>();

    //Een 1ste stap die samengaat met een leeg bord om terug te kunnen gaan
    public ButtonCompanion() {
        stappen.add("X F 0 0 null");
    }

    //Deze methode wordt opgeroepen in de start-methode om het model van het bord
    // te kunnen oproepen in de companion
    public void setModel(BordModel model){
        this.model = model;
    }

    //Brengt het bord terug in startpositie, wordt opgeroepen door start-button
    public void getStart(){
        while (positie > 0){
            getVorige();
        }
    }

    //Brengt het bord een stap terug, wordt opgeroepen door vorige-button
    public void getVorige(){
        String stap;
        int rij, kolom;

        stap = stappen.get(positie);

        rij = Integer.parseInt(stap.charAt(4) + "");
        kolom = Integer.parseInt(stap.charAt(6) + "");

        model.verwijderPion(rij, kolom);

        positie --;

        if (positie == 0) {
            start.setDisable(true);
            vorige.setDisable(true);
        }

        volgende.setDisable(false);
        laatste.setDisable(false);
    }

    //Brengt het bord een stap vooruit en vraagt een nieuwe stap op indien nodig,
    // wordt opgeroepen door volgende-button
    public void getVolgende(){
        String stap;

        if (positie + 1< stappen.size()){
            stap = stappen.get(positie + 1);
            vertaalAntwoord(stap);
        }else {
            getServer().stuurCommando("X");
            vertaalAntwoord(getServer().getAntwoord());
        }
    }

    //Voert alle stappen van de server uit, wordt opgeroepen door laatste-button
    public void getLaatste(){
        while (! laatste.isDisabled()){
            getVolgende();
        }
    }

    @Override
    public void vertaalAntwoord(String stap){
        char pion;
        int rij, kolom;

        if(! stappen.contains(stap)){
            stappen.add(stap);
        }

        pion = stap.charAt(8);
        rij = Integer.parseInt(stap.charAt(4) + "");
        kolom = Integer.parseInt(stap.charAt(6) + "");

        model.plaatsPion(pion, rij, kolom);

        if(stap.charAt(2) == 'T') {
            if(getServer() != null) {
                getServer().sluitServer();
                setServer(null);
            }

            volgende.setDisable(true);
            laatste.setDisable(true);
        }

        start.setDisable(false);
        vorige.setDisable(false);

        positie ++;
    }

    //Roept getLaatste op en neemt een screenshot die in png-formaat wordt opgeslagen
    // op de meegegeven locatie
    public void neemScreenshot(Scene scene, String location){
        getLaatste();

        WritableImage writableImage =
                new WritableImage((int)scene.getWidth(), (int)scene.getHeight());
        scene.snapshot(writableImage);

        File file = new File(location);
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
