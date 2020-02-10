package mijnlieff.server;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Server{
    //Aanmaken van statische variabelen om elke methode in de klasse
    // toegang te geven tot de variabelen, de variabelen zijn statisch
    // omdat ze nooit gewijzigd worden
    private static Socket socket;
    private static BufferedReader in;
    private static PrintWriter out;

    //Een connectie met de server wordt aangemaakt, een buffered reader
    // en een buffered writer worden geïnitialiseerd
    public Server(InetAddress address, int poort) {
        //TODO: try with resources
        try{
            socket = new Socket(address, poort);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(), true);

        }catch (IOException exception) {
            exception.printStackTrace();
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Connectie met de server gefaald.");
            errorAlert.setContentText(
                    "Er is iets misgelopen bij het aanmaken\n" +
                    "van de verbinding met de server.\n" +
                    "Contoleer uw internetverbinding en druk op \"OK\" om opnieuw te proberen."
            );

            Stage stage = (Stage) errorAlert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));

            errorAlert.showAndWait();

            try{
                socket = new Socket(address, poort);

                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                out = new PrintWriter(socket.getOutputStream(), true);
            }catch (Exception e) {
                e.printStackTrace();
                Platform.exit();
            }
        }
    }

    //Sluit de connectie met de server
    public void sluitServer(){
        try {
            stuurCommando("Q");
            socket.close();
            in.close();
            out.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    //Vraagt een stap op van de server
    public String getAntwoord(){
        String stap = "";

        try {
            stap = in.readLine();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return stap;
    }

    //Verstuurt een commando naar de server
    public void stuurCommando(String bericht){
        try {
            out.println(bericht);
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
