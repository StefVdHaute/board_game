package mijnlieff.bord;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import mijnlieff.Listener;
import mijnlieff.StapVertaler;
import mijnlieff.houders.PionhouderModel;
import mijnlieff.pionnen.Pion;
import mijnlieff.pionnen.Pionnen;
import mijnlieff.pionnen.Positie;
import mijnlieff.server.tasks.GetZetTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Character.isDigit;

//TODO: ruim alerts en dialogs en whatever op
public class BordModel extends StapVertaler {
    //Een tekenToPion om de commando's van de server te vertalen om zo makkelijker
    // later aan de afbeeldingen te geraken
    private static final Map<Character, String> tekenToPion = new HashMap<>();
    private static final Map<String, String> pionToTeken = new HashMap<>();
    static {
        tekenToPion.put('@', "pusher");
        tekenToPion.put('o', "puller");
        tekenToPion.put('+', "toren");
        tekenToPion.put('X', "loper");

        pionToTeken.put("pusher", "@");
        pionToTeken.put("puller", "o");
        pionToTeken.put("toren", "+");
        pionToTeken.put("loper", "X");
    }

    private BordViewCompanion bordCompanion;

    private int eersteZet = 0;
    //Houdt bij welke pion waar staat
    private String[][] heeftPion;
    //Houdt de momentele bedreiging bij
    private boolean[][] bedreigd;
    //Houdt bij welke beurt het is
    private int beurt = 0;
    //Houdt de pionhoudermodellen bij zodat deze vanuit het bordmodel
    // kunnen aangepast worden
    private PionhouderModel[] pionhouders;
    //Geeft aan het bordmodel aan of de bedreiging moet weergegeven worden
    // (in het testgeval/viewermodus niet)
    private boolean bedreig = false;
    //Houdt de punten bij
    private int[] punten = {0, 0};

    //eerste slaat op de persoon die het bord samenstelt
    //eersteZet slaat op de persoon die de eerste zet doet
    public void setKleur(Boolean eerste){
        if (eerste){
            eersteZet = 1;
        }
    }

    //heeftPion en bedreigd worden geïnitialliseerd
    public void setBord(String[][] bordConfig) {
        bedreigd = new boolean[11][11];
        heeftPion = bordConfig;

        for (int i = 0; i < heeftPion.length; i++){
            for (int j = 0; j < bedreigd[i].length; j++){
                bedreigd[i][j] = heeftPion[i][j].equals("*");
            }
        }
    }

    //Stelt de pionhouders in zodat 0 wit is en 1 zwart
    public void setPionhouders(PionhouderModel wit, PionhouderModel zwart){
        pionhouders = new PionhouderModel[]{wit, zwart};
    }
    public void setBordCompanion(BordViewCompanion bordCompanion){
        this.bordCompanion = bordCompanion;
    }

    public void setBedreig(boolean bedreig) {
        this.bedreig = bedreig;
    }

    public boolean getBedreig() {
        return bedreig;
    }

    //Geeft de pion op een bepaalde positie terug
    public String getPion(int rij, int kolom){
        return heeftPion[rij][kolom];
    }

    //Geeft aan of een bepaalde positie bepaalde positie bedreigd is
    public boolean isBedreigd (int rij, int kolom) {
        return bedreigd[rij][kolom];
    }

    private void throwAlert(String msg){
        Alert dialog = new Alert(Alert.AlertType.WARNING);
        dialog.setTitle(null);
        dialog.setContentText(msg);

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));

        dialog.showAndWait();
    }

    public void plaatsPion(char pionSoort, int rij, int kolom) {
        String pion = tekenToPion.get(pionSoort);
        String kleur;

        //de kleur van de pion wordt bepaald door de beurt (even = wit, oneven = zwart)
        if(bedreig) {
            kleur = (eersteZet + 1) % 2 == 0 ? "wit" : "zwart";
            pionhouders[(eersteZet + 1) % 2].verwijderPion(pion);
        } else {
            kleur = beurt % 2 == 0 ? "wit" : "zwart";
            pionhouders[beurt % 2].verwijderPion(pion);
        }

        //kleur en pionsoort worden opgeslagen
        heeftPion[rij][kolom] = kleur + '-' + pion;

        if(bedreig) {
            bedreigBord(pion, rij, kolom);
            puntenBij(kleur, rij, kolom);//TODO: binnen (alleen bij bedreig) of buiten if (altijd)?
        }

        beurt ++;
        fireModelChanged();
    }

    public void plaatsPion(int rij, int kolom){
        if(eersteZet == beurt % 2) {
            PionhouderModel pionhouderModel = pionhouders[eersteZet];
            String pionSoort = pionhouderModel.getTeGebruiken();
            String zet = "X F " + rij + " " + kolom + " " + pionToTeken.get(pionSoort);

            if (pionSoort.equals("")) {
                throwAlert("Er is geen pion gekozen.");
            } else if (!pionhouderModel.pionnenOp()) {
                //voert uit als de positie een geldige positie is, anders wordt een waarschuwing gegeven
                if (heeftPion[rij][kolom].equals("") && !bedreigd[rij][kolom]) {
                    //de kleur van de pion wordt bepaald door de beurt (even = wit, oneven = zwart)
                    String kleur = eersteZet == 0 ? "wit" : "zwart";
                    //kleur en pionsoort worden opgeslagen
                    heeftPion[rij][kolom] = kleur + '-' + pionSoort;

                    pionhouderModel.verwijderPion(pionSoort);
                    pionhouderModel.setTeGebruiken("");

                    bedreigBord(pionSoort, rij, kolom);
                    puntenBij(kleur, rij, kolom);

                    beurt++;
                    fireModelChanged();

                    if(kanNietPlaatsen() && pionhouderModel.pionnenOp()){
                        gameOver();
                    } else {
                        Task task = new GetZetTask(this, zet);
                        new Thread(task).start();
                    }
                } else {
                    throwAlert("Hier kan geen pion staan\n" +
                            "Zet een pion op een wit of zwart vak zonder andere pionnen");
                }
            }else {
                gameOver();
            }
        }
    }

    //Verwijdert een pion op een bepaalde positie en zet ze terug in de juiste houder
    public void verwijderPion( int rij, int kolom){
        //beurt wordt direct afgetrokken zodat de kleur juist is zonder +1 of -1 te doen
        beurt--;
        pionhouders[beurt % 2].zetPionTerug(heeftPion[rij][kolom].split("-")[1]);
        heeftPion[rij][kolom] = "";
        fireModelChanged();
    }

    private void bedreigBord (String pionSoort, int rij, int kolom){
        //een pion wordt geïnitialiseerd om de bedreigde vakken te berekenen
        // door elk vak te overlopen met een geneste for loop
        Positie pionPos = new Positie(rij, kolom);
        Pion pion = Pionnen.create(pionSoort);
        pion.setPositie(pionPos);

        for (int i = 0; i < bedreigd.length; i++) {
            for (int j = 0; j < bedreigd.length; j++){
                if(heeftPion[i][j].equals("*")) {
                    bedreigd[i][j] = true;
                } else {
                    Positie positie = new Positie(i, j);
                    bedreigd[i][j] = pion.bedreig(positie);
                }
            }
        }
    }

    private void bedreigBord (){
        for (int i = 0; i < bedreigd.length; i++){
            for (int j = 0; j < bedreigd.length; j++){
                bedreigd[i][j] = heeftPion[i][j].equals("*");
            }
        }
    }

    private boolean kanNietPlaatsen(){
        boolean plaatsVrij = false;
        int i = 0, j = 0;

        while (i < heeftPion.length && !plaatsVrij) {
            while (j < heeftPion[i].length && !plaatsVrij) {
                plaatsVrij = heeftPion[i][j].equals("") && ! bedreigd[i][j];
                j++;
            }
            j = 0;
            i ++;
        }
        return ! plaatsVrij;
    }

    private void puntenBij(String kleur, int rij, int kolom) {
        int punten = 0, matches = 0;
        int startRij = rij, startKolom = kolom;

        //Berekening van de verticale punten
        for (String[] pionnen : heeftPion) {
            if (pionnen[kolom].split("-")[0].equals(kleur)) {
                matches++;
            }
        }

        if (matches - 2 > 0) {
            punten++;
        }
        matches = 0;

        //Berekening van de horizontale punten
        for (String pion: heeftPion[rij]) {
            if (pion.split("-")[0].equals(kleur)){
                matches++;
            }
        }

        if (matches - 2 > 0) {
            punten++;
        }
        matches = 0;
        //Berekening van de diagonale punten (dalend)
        while (startRij > 0 && startKolom > 0){
            startRij --;
            startKolom --;
        }

        while (startRij < heeftPion[rij].length && startKolom < heeftPion.length){
            if (heeftPion[startRij][startKolom].split("-")[0].equals(kleur)){
                matches++;
            }
            startRij ++;
            startKolom ++;
        }
        startRij = rij;
        startKolom = kolom;

        if (matches - 2 > 0) {
            punten++;
        }
        matches = 0;

        //Berekening van de diagonale punten (stijgend)
        while (startRij < heeftPion.length - 1 && startKolom > 0){
            startRij ++;
            startKolom --;
        }

        while (startRij >= 0 && startKolom < heeftPion[rij].length){
            if (heeftPion[startRij][startKolom].split("-")[0].equals(kleur)){
                matches++;
            }
            startRij --;
            startKolom ++;
        }

        if (matches - 2 > 0) {
            punten++;
        }

        this.punten[beurt % 2] += punten;

        bordCompanion.updatePunten(this.punten[0], this.punten[1]);
    }

    private boolean geldigCommando(String zet) {
        boolean geldig = false;
        String[] commando = zet.split(" ");

        if(commando.length == 1){
            geldig = commando[0].equals("X");
        } else if (commando.length == 5 && isDigit(commando[2].charAt(0)) && isDigit(commando[3].charAt(0))){
            int rij = Integer.parseInt(commando[2]);
            int kolom = Integer.parseInt(commando[3]);

            geldig = commando[0].equals("X") &&
                    commando[1].equals("F") &&
                    ! isBedreigd(rij, kolom) &&
                    getPion(rij, kolom).equals("");
        }

        return geldig;
    }

    private void gameOver() {
        String text;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initStyle(StageStyle.UTILITY);

        alert.setTitle(null);

        if (punten[0] > punten[1]) {
            text = "Wit is gewonnen!";
        } else if (punten[0] < punten[1]) {
            text = "Zwart is gewonnen!";
        } else {
            text = "Het is gelijk!?";
        }

        alert.setHeaderText(text);
        alert.setContentText("Wit heeft " + punten[0] + " punten\n" +
                "Zwart heeft " + punten[1] + " punten"
        );

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));
        alert.showAndWait();

        getServer().sluitServer();
    }

    @Override
    public void vertaalAntwoord(String stap) {
        String[] commando = stap.split(" ");

        if(geldigCommando(stap)) {
            if(stap.length() > 1) {
                plaatsPion(commando[4].charAt(0), Integer.parseInt(commando[2]), Integer.parseInt(commando[3]));

                if(pionhouders[eersteZet].pionnenOp()){
                    gameOver();
                } else if(kanNietPlaatsen()){
                    if(pionhouders[(eersteZet + 1)].pionnenOp()){
                        gameOver();
                    }else {
                        Task task = new GetZetTask(this, "X");
                        new Thread(task).start();
                    }
                }
            } else if(stap.equals("X")){
                bedreigBord();
                fireModelChanged();
            }
        } else {
            getServer().stuurCommando("Q");

            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("Mijnlieff");
            alert.setHeaderText("ERROR");
            alert.setContentText("Er is een ongeldige zet doorgestuurd");

            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));
            alert.showAndWait();

            System.exit(0);
        }
    }

    //Een lijst om alle velden bij te houden
    private List<Listener> listeners = new ArrayList<>(16);

    //Voegt nieuwe velden toe
    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    //Geeft aan dat het model verandert is en het bord dus hertekend moet worden
    private void fireModelChanged() {
        for (Listener listener : listeners) {
            listener.modelHasChanged();
        }
    }
}
