package mijnlieff.spelers;

import javafx.beans.property.SimpleStringProperty;

public class Speler {
    public SimpleStringProperty naam = new SimpleStringProperty();

    public Speler(String gebruikersnaam){
        this.naam.set(gebruikersnaam);
    }

    public void setNaam(String naam) {
        this.naam.set(naam);
    }

    public String getNaam() {
        return naam.get();
    }

    public SimpleStringProperty naamProperty() {
        return naam;
    }
}
