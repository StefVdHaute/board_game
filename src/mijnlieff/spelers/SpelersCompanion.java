package mijnlieff.spelers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mijnlieff.StapVertaler;
import mijnlieff.server.tasks.GetSpelersTask;
import mijnlieff.server.tasks.GetZetTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SpelersCompanion extends StapVertaler {
    private static boolean eerstAanbeurt;

    public TableView<Speler> tableView;
    public TableColumn<Speler, String> gebruikersnaamColumn;
    public Button beschikbaar;
    public Button refresh;

    private String gebruikersnaam;
    private static String tegenstander;
    private boolean isBeschikbaar = false;

    public void initialize(){
        gebruikersnaamColumn.setCellValueFactory( new PropertyValueFactory<>("naam"));

        GetSpelersTask task = new GetSpelersTask(this);
        new Thread(task).start();
    }

    public void setSpelers(ObservableList<Speler> spelers){
        tableView.setItems(spelers);
    }

    public void setGebruikersnaam(String naam){
        this.gebruikersnaam = naam;
    }

    public String getGebruikersnaam(){
        return gebruikersnaam;
    }

    public void setBeschikbaar() {
        GetZetTask task = new GetZetTask(this, "P");
        new Thread(task).start();

        beschikbaar.setDisable(true);
        refresh.setDisable(true);
    }

    public void refresh(){
        GetSpelersTask task = new GetSpelersTask(this);
        new Thread(task).start();
    }

    public void kiesSpeler() {
        Speler speler = tableView.getSelectionModel().getSelectedItem();

        GetZetTask task = new GetZetTask(this, "C " + speler.getNaam());
        new Thread(task).start();
    }

    @Override
    public void vertaalAntwoord(String antwoord){
        ScheduledExecutorService executor =  Executors.newSingleThreadScheduledExecutor();
        Alert dialog = new Alert(Alert.AlertType.INFORMATION);
        dialog.setTitle("Mijnlieff");

        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("mijnlieff/pionnen/pionAfbeeldingen/zwart-pusher.png"));

        //zorgt ervoor dat de popup gesloten wordt na 3 seconden
        executor.submit(() -> Platform.runLater(stage::show));
        executor.schedule(
                () -> Platform.runLater(stage::close)
                , 3
                , TimeUnit.SECONDS);

        if (antwoord.contains("-")){
            dialog.setHeaderText("Deze speler is niet meer beschikbaar");
            dialog.showAndWait();
            refresh();
        } else{
            tegenstander = antwoord.substring(4);
            eerstAanbeurt = antwoord.charAt(2) == 'T';

            String text = "Het spel gaat starten\n" +
                    "Jij bent " + (eerstAanbeurt ? "zwart\n en jij" : "wit\nen " + tegenstander) +
                    " maakt het bord.";
            dialog.setHeaderText(text);

            dialog.showAndWait();

            Stage thisStage = (Stage) tableView.getScene().getWindow();
            thisStage.close();
        }
    }

    public static String getTegenstander() {
        return tegenstander;
    }

    public static boolean isEerstAanbeurt(){
        return eerstAanbeurt;
    }

    public void close() {
        StapVertaler.getServer().sluitServer();
        System.exit(0);
    }
}
