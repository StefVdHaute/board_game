package mijnlieff.houders;

import mijnlieff.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PionhouderModel {
    //Een map om makkelijk aan de juiste pionnen te raken
    private static final Map<String, Integer> map = new HashMap<>();
    static {
        map.put("pusher", 0);
        map.put("puller", 1);
        map.put("toren", 2);
        map.put("loper", 3);
    }

    //Een dubbele array van strings die bijhoudt welke pionnen
    // er nog beschikbaar zijn
    private String[][] pionnen = new String[][]{
            {"pusher", "pusher"},
            {"puller", "puller"},
            {"toren", "toren"},
            {"loper", "loper"},
    };

    private String teGebruiken = "";

    public boolean pionnenOp(){
        boolean leeg = true;
        int i = 0, j = 0;

        while (i < pionnen.length && leeg) {
            String[] soort = pionnen[i];

            while (j < soort.length && leeg) {
                leeg = soort[j].equals("");
                j ++;
            }

            j = 0;
            i ++;
        }
        return leeg;
    }

    public void setTeGebruiken(String pion){
        teGebruiken = pion;
    }

    public String getTeGebruiken(){
        return teGebruiken;
    }

    //Haalt een pion weg uit pionnen
    public void verwijderPion(String pion){
        int positie = map.get(pion);

        if(pionnen[positie][1].equals(pion)){
            pionnen[positie][1] = "";
        } else if(pionnen[positie][0].equals(pion)){
            pionnen[positie][0] = "";
        }

        fireModelChanged();
    }

    //Zet een pion terug op de juiste plaats
    public void zetPionTerug(String pion){
        int positie = map.get(pion);

        if(pionnen[positie][0].equals("")){
            pionnen[positie][0] = pion;
        } else if(pionnen[positie][1].equals("")){
            pionnen[positie][1] = pion;
        }
        fireModelChanged();
    }

    public String getPion(int rij, int kolom){
        return pionnen[rij][kolom];
    }

    private List<Listener> listeners = new ArrayList<>(16);

    //Voegt nieuwe houders toe
    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    //Geeft aan dat het model verandert is en de pionhouder dus hertekend moet worden
    private void fireModelChanged() {
        for (Listener listener : listeners) {
            listener.modelHasChanged();
        }
    }
}
