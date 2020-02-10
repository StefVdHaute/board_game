package mijnlieff.server.tasks;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import mijnlieff.server.Server;
import mijnlieff.spelers.Speler;
import mijnlieff.spelers.SpelersCompanion;

public class GetSpelersTask extends Task<ObservableList<Speler>> {
    private Server server;
    private SpelersCompanion companion;

    public GetSpelersTask(SpelersCompanion companion) {
        this.companion = companion;

        server = SpelersCompanion.getServer();
        server.stuurCommando("W");
    }

    @Override
    protected ObservableList<Speler> call() {
        ObservableList<Speler> spelers = FXCollections.observableArrayList();
        Speler speler;

        String naam = server.getAntwoord();

        while (! naam.equals("+")) {
            //if statement doet geen zak uit
            if(! naam.substring(2).equals(companion.getGebruikersnaam())){
                speler = new Speler(naam.substring(2));
                spelers.add(speler);
            }

            naam = server.getAntwoord();
        }

        return spelers;
    }

    @Override
    protected void succeeded() {
        companion.setSpelers(getValue());
    }

    @Override
    protected void failed() {

    }
}