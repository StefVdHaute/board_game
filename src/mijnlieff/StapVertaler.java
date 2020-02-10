package mijnlieff;

import mijnlieff.server.Server;

public abstract class StapVertaler {
    //Houdt de server bij om stappen van de tegesntander op te vragen
    private static Server server;

    //Deze methode wordt opgeroepen in de start-methode om met de argumenten
    // een serverconnectie aan te gaan
    public static void setServer(Server server){
        StapVertaler.server = server;
    }

    public static Server getServer() {
        return server;
    }

    public abstract void vertaalAntwoord(String stap);
}
