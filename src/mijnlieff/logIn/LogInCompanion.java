package mijnlieff.logIn;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import mijnlieff.server.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class LogInCompanion {
    public TextField gebruikersnaam;
    public Label naamLabel;
    public Label naamError;

    public TextField serverNaam;
    public Label serverNaamLabel;
    public Label serverNaamError;

    public TextField poort;
    public Label poortLabel;
    public Label poortError;

    private static String naam;
    private static Server server = null;

    public static String getGebruikersnaam() {
        return naam;
    }

    public static Server getServer() {
        return server;
    }

    //TODO: controle voor juiste poortnr -> foutief nr doet programma vastlopen

    public void logIn(){
        naam = checkNaam();
        InetAddress address = checkServerNaam();
        int poortNr = checkPoortNr();

        if(naam != null && address != null && poortNr >= 0) {
            server = new Server(address, poortNr);
            server.stuurCommando("I " + naam);

            if (server.getAntwoord().equals("-")) {
                Alert dialog = new Alert(Alert.AlertType.WARNING);
                dialog.setTitle("Mijnlieff");
                dialog.setHeaderText("Deze gebruikersnaam is al gekozen.");

                Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));

                dialog.showAndWait();
            } else {
                Stage stage = (Stage) gebruikersnaam.getScene().getWindow();
                stage.close();
            }
        }
    }

    public String checkNaam() {
        return checkLabel(gebruikersnaam, naamError, naamLabel);
    }

    public InetAddress checkServerNaam(){
        InetAddress address = null;

        try {
            address = InetAddress.getByName(checkLabel(serverNaam, serverNaamError, serverNaamLabel));
        } catch (UnknownHostException e) {
            serverNaamError.setVisible(true);
            serverNaamLabel.setTextFill(Color.RED);
        }
        return address;
    }

    public int checkPoortNr() {
        String nrString = checkLabel(poort, poortError, poortLabel);
        int nr = -1;

        if(! nrString.chars().allMatch(Character::isDigit)){
            poortError.setVisible(true);
            poortLabel.setTextFill(Color.RED);
        } else if (! poortError.isVisible()){
            nr = Integer.parseInt(nrString);
        }

        return nr;
    }

    private String checkLabel(TextField field, Label errorLabel, Label textLabel){
        String text = field.getText().trim();

        if(text.isEmpty()) {
            errorLabel.setVisible(true);
            textLabel.setTextFill(Color.RED);
        } else {
            errorLabel.setVisible(false);
            textLabel.setTextFill(Color.BLACK);
        }

        return text;
    }

    public void cancel() {
        System.exit(0);
    }
}
