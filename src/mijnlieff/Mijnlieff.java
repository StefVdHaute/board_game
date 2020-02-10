package mijnlieff;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import mijnlieff.bord.Bord;
import mijnlieff.bord.bordConfiguratie.BordConfigCompanion;
import mijnlieff.bord.BordModel;
import mijnlieff.bord.BordViewCompanion;
import mijnlieff.viewerMode.ButtonCompanion;
import mijnlieff.houders.Pionhouder;
import mijnlieff.houders.PionhouderModel;
import mijnlieff.server.tasks.GetBordConfigTask;
import mijnlieff.server.tasks.GetZetTask;
import mijnlieff.logIn.LogInCompanion;
import mijnlieff.server.Server;
import mijnlieff.spelers.SpelersCompanion;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

public class Mijnlieff extends Application {
    private String bestandslocatie = "";

    private Server server;

    private BorderPane root;
    private BordModel model;
    private ButtonCompanion buttonCompanion;
    private BordViewCompanion bordCompanion;

    private FXMLLoader fxmlLoader;

    private static void error(String bericht) {
        System.err.println("*ERROR* -- " + bericht);
        Platform.exit();
    }

    public void setServer(Server server){
        //Een connectie met de server wordt aangegaan
        this.server = server;
        //Er wordt voor gezorgd dat de socket, input- en outputreader altijd worden afgesloten
        Runtime.getRuntime().addShutdownHook(new Thread(server::sluitServer, "Shutdown-thread"));
    }

    //TODO: gebruik voor alles hetzelfde venster ffs
    @Override
    public void init() throws IOException {
        //neemt argumenten van main (launch(args)) en zet ze in lijst
        List<String> argList = getParameters().getRaw();

        //speelbord wordt aangemaakt
        model = new BordModel();

        //witte pionhouder wordt aangemaakt
        PionhouderModel wit = new PionhouderModel();
        Pionhouder witHouder = new Pionhouder(2, 4, "wit", wit);
        witHouder.setPadding(new Insets(125, 0, 125, 20));
        witHouder.setAlignment(Pos.CENTER);

        //zwarte pionhouder wordt aangemaakt
        PionhouderModel zwart = new PionhouderModel();
        Pionhouder zwartHouder = new Pionhouder(2, 4, "zwart", zwart);
        zwartHouder.setPadding(new Insets(125, 20, 125, 0));
        zwartHouder.setAlignment(Pos.CENTER);

        //pionhouders worden toegekend aan het bordmodel
        model.setPionhouders(wit, zwart);

        if (argList.size() > 0) {
            String hostnaam = argList.get(0);

            try {
                int poort = Integer.parseInt(argList.get(1));
                InetAddress address = InetAddress.getByName(hostnaam);

                //Een connectie met de server wordt aangegaan
                setServer(new Server(address, poort));
            } catch (NumberFormatException ex) {
                error("De 2e parameter moet een getal zijn.");
            } catch (UnknownHostException ex) {
                error( "De opgegeven hostnaam werd niet gevonden");
            }

            if (argList.size() == 3) {
                bestandslocatie = argList.get(2);
            }

            //Benodigde fxml bestand voor viewermodus wordt geïnitialiseerd
            fxmlLoader = new FXMLLoader(getClass().getResource("viewerMode/viewerMode.fxml"));

            //root wordt aangemaakt op basis van fxml bestand
            // (dit gebeurt in de if statement omdat anders de buttoncompanion == null)
            root = fxmlLoader.load();

            root.setLeft(witHouder);
            root.setRight(zwartHouder);

            buttonCompanion = fxmlLoader.getController();

            ButtonCompanion.setServer(server);

            //model wordt verbonden met controller
            buttonCompanion.setModel(model);
        } else {
            fxmlLoader = new FXMLLoader(getClass().getResource("bord/BordView.fxml"));

            root = fxmlLoader.load();
            bordCompanion = fxmlLoader.getController();

            model.setBordCompanion(bordCompanion);
            model.setBedreig(true);

            //Het bord met pionhouders wordt in het borderpane geplaatst
            bordCompanion.setPionhouders(witHouder, zwartHouder);
        }

        //Een achtergrondkleur wordt ingesteld
        root.setStyle("-fx-background-color: #363636;");
    }

    //TODO: bord is te groot
    @Override
    public void start(Stage primaryStage) throws IOException {
        if(server == null) { //interactieve modus
            //login
            fxmlLoader = new FXMLLoader(getClass().getResource("logIn/loginScreen.fxml"));

            Stage stage = new Stage();

            stage.setScene(new Scene(fxmlLoader.load()));
            stage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));
            stage.setResizable(false);
            stage.setTitle("Mijnlieff");

            stage.showAndWait();

            setServer(LogInCompanion.getServer());
            BordModel.setServer(server);

            SpelersCompanion.setServer(server);

            String gebruikersNaam = LogInCompanion.getGebruikersnaam();
            //------------------------------------------------------------------------------------------

            //spelerselectie
            fxmlLoader = new FXMLLoader(getClass().getResource("spelers/spelers.fxml"));

            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(true);

            stage.showAndWait();

            //------------------------------------------------------------------------------------------

            //Spelverloop (wie begint wie maakt bord)
            model.setKleur(SpelersCompanion.isEerstAanbeurt());

            Task task;

            //TODO: fix namen (en geef ze minder ruimte, spel is te groot
            if (SpelersCompanion.isEerstAanbeurt()){//Speler maakt bord aan
                bordCompanion.setNamen(gebruikersNaam, SpelersCompanion.getTegenstander());

                fxmlLoader = new FXMLLoader(getClass().getResource("bord/bordConfiguratie/BordConfig.fxml"));
                task = new GetZetTask(model);
            } else {//Speler wacht tot bord is aangemaakt
                bordCompanion.setNamen(SpelersCompanion.getTegenstander(), gebruikersNaam);

                fxmlLoader = new FXMLLoader(getClass().getResource("loading/LoadingScreen.fxml"));
                BordConfigCompanion bordConfigCompanion = new BordConfigCompanion();

                task = new GetBordConfigTask(bordConfigCompanion, stage);
            }

            new Thread(task).start();

            stage.setScene(new Scene(fxmlLoader.load()));
            stage.setResizable(false);
            stage.showAndWait();
        }

        //Bord goedzetten
        model.setBord(BordConfigCompanion.getBordConfig());

        Bord bord = new Bord(model);
        bord.setPadding(new Insets(20, 20, 20, 20));
        bord.setAlignment(Pos.CENTER);

        root.setCenter(bord);

        //de scene wordt aangemaakt
        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));
        primaryStage.setTitle("Mijnlieff");

        //de scene wordt getoond
        primaryStage.show();

        //als een 3e argument wordt meegegeven wordt een screenshot gemaakt
        // en daarna het programma gesloten
        if (!bestandslocatie.equals("")) {
            buttonCompanion.neemScreenshot(scene, bestandslocatie);
            Platform.exit();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
